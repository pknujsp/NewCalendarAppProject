package com.zerodsoft.scheduleweather.Utility;

import android.content.Context;

import com.zerodsoft.scheduleweather.RequestResponse.WeatherResponseItem;
import com.zerodsoft.scheduleweather.WeatherData.BaseData;
import com.zerodsoft.scheduleweather.WeatherData.WeatherData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class ResponseDataClassifier
{
    public static final int CURRENT_WEATHER = 0;
    public static final int N_FORECAST = 1;

    public static ArrayList<WeatherData> classifyWeatherResponseItem(List<WeatherResponseItem> items, Context context)
    {

        ArrayList<WeatherData> dataList = new ArrayList<>();
        Set<BaseData> baseSet = new CustomedLinkedHashSet<>();
        ArrayList<Integer> arrIndexes = new ArrayList<>();

        if (items.get(0).getFcstDate() != null)
        {
            // 동네예보 인 경우 - items의 각 날짜별 index값을 arrIndexes에 저장함
            for (int i = 0; i < items.size(); ++i)
            {
                if (baseSet.add(new BaseData().setFcstDate(items.get(i).getFcstDate()).setFcstTime(items.get(i).getFcstTime())))
                {
                    arrIndexes.add(i);
                }
            }
        }

        if (arrIndexes.size() == 0)
        {
            // 초단기 실황
            WeatherData weatherData = new WeatherData(context);

            for (int j = 0; j < items.size(); j++)
            {
                setValue(items.get(j), weatherData);
            }

            dataList.add(weatherData);
        } else
        {
            // 동네예보
            for (int i = 0; i < arrIndexes.size() - 1; i++)
            {
                WeatherData weatherData = new WeatherData(context);

                int idx = arrIndexes.get(i).intValue();
                weatherData.setAreaX(items.get(idx).getNx());
                weatherData.setAreaY(items.get(idx).getNy());
                weatherData.setFcstDate(items.get(idx).getFcstDate());
                weatherData.setFcstTime(items.get(idx).getFcstTime());

                for (int j = arrIndexes.get(i).intValue(); j < arrIndexes.get(i + 1).intValue(); j++)
                {
                    // 각 시간별 데이터를 저장
                    // 0600 - 기온, 하늘상태 등
                    setValue(items.get(j), weatherData);
                }
                // 시간 오름차순으로 저장됨
                dataList.add(weatherData);
            }
        }
        return dataList;
    }


    private static void setValue(WeatherResponseItem item, WeatherData weatherData)
    {
        if (item.getCategory().equals("T1H"))
        {
            //기온
            weatherData.setTemperature(item.getObsrValue());
        } else if (item.getCategory().equals("RN1"))
        {
            // 1시간 강수량
            weatherData.setRainfall(item.getObsrValue());
        } else if (item.getCategory().equals("UUU"))
        {
            // 동서바람성분
            if (item.getObsrValue() != null)
            {
                weatherData.setEastWestWind(item.getObsrValue());
            } else
            {
                weatherData.setEastWestWind(item.getFcstValue());
            }
        } else if (item.getCategory().equals("VVV"))
        {
            //남북바람성분
            if (item.getObsrValue() != null)
            {
                weatherData.setSouthNorthWind(item.getObsrValue());
            } else
            {
                weatherData.setSouthNorthWind(item.getFcstValue());
            }
        } else if (item.getCategory().equals("REH"))
        {
            //습도
            if (item.getObsrValue() != null)
            {
                weatherData.setHumidity(item.getObsrValue());
            } else
            {
                weatherData.setHumidity(item.getFcstValue());
            }
        } else if (item.getCategory().equals("PTY"))
        {
            //강수형태
            if (item.getObsrValue() != null)
            {
                weatherData.setPrecipitationForm(item.getObsrValue());
            } else
            {
                weatherData.setPrecipitationForm(item.getFcstValue());
            }
        } else if (item.getCategory().equals("VEC"))
        {
            //풍향
            if (item.getObsrValue() != null)
            {
                weatherData.setWindDirection(item.getObsrValue());
            } else
            {
                weatherData.setWindDirection(item.getFcstValue());
            }
        } else if (item.getCategory().equals("WSD"))
        {
            //풍속
            if (item.getObsrValue() != null)
            {
                weatherData.setWindSpeed(item.getObsrValue());
            } else
            {
                weatherData.setWindSpeed(item.getFcstValue());
            }
        } else if (item.getCategory().equals("POP"))
        {
            //강수확률
            weatherData.setChanceOfShower(item.getFcstValue());
        } else if (item.getCategory().equals("R06"))
        {
            //6시간 강수량
            weatherData.setPrecipitationRain(item.getFcstValue());
        } else if (item.getCategory().equals("S06"))
        {
            //6시간 신적설
            weatherData.setPrecipitationSnow(item.getFcstValue());
        } else if (item.getCategory().equals("SKY"))
        {
            //하늘상태
            weatherData.setSky(item.getFcstValue());
        } else if (item.getCategory().equals("T3H"))
        {
            //3시간 기온
            weatherData.setThreeHourTemp(item.getFcstValue());
        } else if (item.getCategory().equals("TMN"))
        {
            //아침 최저기온
            weatherData.setMorningMinTemp(item.getFcstValue());
        } else if (item.getCategory().equals("TMX"))
        {
            //낮 최고기온
            weatherData.setDayMaxTemp(item.getFcstValue());
        } else if (item.getCategory().equals("WAV"))
        {
            //파고
            weatherData.setWaveHeight(item.getFcstValue());
        }
    }
}


class CustomedLinkedHashSet<E> extends java.util.LinkedHashSet
{
    @Override
    public boolean add(Object o)
    {
        final String baseDate = ((BaseData) o).getFcstDate();
        final String baseTime = ((BaseData) o).getFcstTime();

        Iterator<BaseData> iterator = super.iterator();

        while (iterator.hasNext())
        {
            BaseData baseData = iterator.next();

            if (baseData.getFcstDate().equals(baseDate) && baseData.getFcstTime().equals(baseTime))
            {
                return false;
            }
        }
        super.add(o);
        return true;
    }
}