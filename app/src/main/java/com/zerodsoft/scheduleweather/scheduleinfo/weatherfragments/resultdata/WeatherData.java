package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata;

import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midlandfcstresponse.MidLandFcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midlandfcstresponse.MidLandFcstItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midtaresponse.MidTaItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midtaresponse.MidTaItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtfcstresponse.UltraSrtFcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtfcstresponse.UltraSrtFcstItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtncstresponse.UltraSrtNcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtncstresponse.UltraSrtNcstItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.vilagefcstresponse.VilageFcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.vilagefcstresponse.VilageFcstItems;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.responseresult.MidFcstData;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.responseresult.UltraSrtFcstData;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.responseresult.UltraSrtNcstData;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.responseresult.VilageFcstData;
import com.zerodsoft.scheduleweather.utility.Clock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WeatherData
{
    //지역 정보
    private final String areaName;
    //데이터 처음 불러올때의 시각
    private final Calendar downloadedDate;

    private final WeatherAreaCodeDTO weatherAreaCode;
    private final int index;

    //초단기실황 최종 데이터
    private DataWrapper<UltraSrtNcstData> ultraSrtNcstFinalData;
    //초단기예보 최종 데이터
    private DataWrapper<List<UltraSrtFcstData>> ultraSrtFcstFinalData;
    //동네예보 최종 데이터
    private DataWrapper<List<VilageFcstData>> vilageFcstFinalData;
    //중기예보(중기육상예보+중기기온예보) 최종 데이터
    private DataWrapper<List<MidFcstData>> midFcstFinalData;

    //초단기실황 응답 데이터
    private DataWrapper<UltraSrtNcstItems> ultraSrtNcstItemsDataWrapper;
    //초단기예보 응답 데이터
    private DataWrapper<UltraSrtFcstItems> ultraSrtFcstItemsDataWrapper;
    //동네예보 응답 데이터
    private DataWrapper<VilageFcstItems> vilageFcstItemsDataWrapper;
    //중기육상예보 응답 데이터
    private DataWrapper<MidLandFcstItems> midLandFcstItemsDataWrapper;
    //중기기온예보 응답 데이터
    private DataWrapper<MidTaItems> midTaItemsDataWrapper;


    public WeatherData(int index, Calendar downloadedDate, WeatherAreaCodeDTO weatherAreaCode)
    {
        this.index = index;
        this.areaName = weatherAreaCode.getPhase1() + " " + weatherAreaCode.getPhase2() + " " + weatherAreaCode.getPhase3();
        this.downloadedDate = (Calendar) downloadedDate.clone();
        this.weatherAreaCode = weatherAreaCode;
    }

    public WeatherAreaCodeDTO getWeatherAreaCode()
    {
        return weatherAreaCode;
    }

    public Calendar getDownloadedDate()
    {
        return (Calendar) downloadedDate.clone();
    }

    public int getIndex()
    {
        return index;
    }


    public String getAreaName()
    {
        return areaName;
    }


    public WeatherData setUltraSrtNcstItemsDataWrapper(DataWrapper<UltraSrtNcstItems> ultraSrtNcstItemsDataWrapper)
    {
        this.ultraSrtNcstItemsDataWrapper = ultraSrtNcstItemsDataWrapper;
        return this;
    }

    public WeatherData setUltraSrtFcstItemsDataWrapper(DataWrapper<UltraSrtFcstItems> ultraSrtFcstItemsDataWrapper)
    {
        this.ultraSrtFcstItemsDataWrapper = ultraSrtFcstItemsDataWrapper;
        return this;
    }

    public WeatherData setVilageFcstItemsDataWrapper(DataWrapper<VilageFcstItems> vilageFcstItemsDataWrapper)
    {
        this.vilageFcstItemsDataWrapper = vilageFcstItemsDataWrapper;
        return this;
    }

    public WeatherData setMidLandFcstItemsDataWrapper(DataWrapper<MidLandFcstItems> midLandFcstItemsDataWrapper)
    {
        this.midLandFcstItemsDataWrapper = midLandFcstItemsDataWrapper;
        return this;
    }

    public WeatherData setMidTaItemsDataWrapper(DataWrapper<MidTaItems> midTaItemsDataWrapper)
    {
        this.midTaItemsDataWrapper = midTaItemsDataWrapper;
        return this;
    }

    public DataWrapper<UltraSrtNcstData> getUltraSrtNcstFinalData()
    {
        return ultraSrtNcstFinalData;
    }

    public DataWrapper<List<UltraSrtFcstData>> getUltraSrtFcstFinalData()
    {
        return ultraSrtFcstFinalData;
    }

    public DataWrapper<List<VilageFcstData>> getVilageFcstFinalData()
    {
        return vilageFcstFinalData;
    }

    public DataWrapper<List<MidFcstData>> getMidFcstFinalData()
    {
        return midFcstFinalData;
    }

    public void setFinalData()
    {
        setUltraSrtNcstData();
        setUltraSrtFcstDataList();
        setVilageFcstDataList();
        setMidFcstDataList();
    }

    /*
    초단기실황 최종 데이터 생성
     */
    public WeatherData setUltraSrtNcstData()
    {
        if (ultraSrtNcstItemsDataWrapper.getException() == null)
        {
            List<UltraSrtNcstItem> items = ultraSrtNcstItemsDataWrapper.getData().getItem();
            UltraSrtNcstData ultraSrtNcstData = new UltraSrtNcstData(items);

            ultraSrtNcstFinalData = new DataWrapper<UltraSrtNcstData>(ultraSrtNcstData);
        } else
        {
            ultraSrtNcstFinalData = new DataWrapper<UltraSrtNcstData>(ultraSrtNcstItemsDataWrapper.getException());
        }
        return this;
    }

    /*
   중기예보 최종 데이터 생성
    */
    public WeatherData setMidFcstDataList()
    {
        if (midLandFcstItemsDataWrapper.getException() == null && midTaItemsDataWrapper.getException() == null)
        {
            //중기예보 데이터 생성 3~10일후
            Calendar copiedCalendar = (Calendar) downloadedDate.clone();

            MidLandFcstItem midLandFcstData = midLandFcstItemsDataWrapper.getData().getItem().get(0);
            MidTaItem midTaFcstData = midTaItemsDataWrapper.getData().getItem().get(0);
            List<MidFcstData> midFcstDataList = new ArrayList<>();

            //3일 후로 이동
            copiedCalendar.add(Calendar.DAY_OF_YEAR, 3);

            midFcstDataList.add(new MidFcstData(Clock.MdE_FORMAT.format(copiedCalendar.getTime()), midLandFcstData.getWf3Am(), midLandFcstData.getWf3Pm()
                    , midLandFcstData.getRnSt3Am(), midLandFcstData.getRnSt3Pm(), midTaFcstData.getTaMin3(), midTaFcstData.getTaMax3()));

            copiedCalendar.add(Calendar.DAY_OF_YEAR, 1);

            midFcstDataList.add(new MidFcstData(Clock.MdE_FORMAT.format(copiedCalendar.getTime()), midLandFcstData.getWf4Am(), midLandFcstData.getWf4Pm()
                    , midLandFcstData.getRnSt4Am(), midLandFcstData.getRnSt4Pm(), midTaFcstData.getTaMin4(), midTaFcstData.getTaMax4()));

            copiedCalendar.add(Calendar.DAY_OF_YEAR, 1);

            midFcstDataList.add(new MidFcstData(Clock.MdE_FORMAT.format(copiedCalendar.getTime()), midLandFcstData.getWf5Am(), midLandFcstData.getWf5Pm()
                    , midLandFcstData.getRnSt5Am(), midLandFcstData.getRnSt5Pm(), midTaFcstData.getTaMin5(), midTaFcstData.getTaMax5()));

            copiedCalendar.add(Calendar.DAY_OF_YEAR, 1);

            midFcstDataList.add(new MidFcstData(Clock.MdE_FORMAT.format(copiedCalendar.getTime()), midLandFcstData.getWf6Am(), midLandFcstData.getWf6Pm()
                    , midLandFcstData.getRnSt6Am(), midLandFcstData.getRnSt6Pm(), midTaFcstData.getTaMin6(), midTaFcstData.getTaMax6()));

            copiedCalendar.add(Calendar.DAY_OF_YEAR, 1);

            midFcstDataList.add(new MidFcstData(Clock.MdE_FORMAT.format(copiedCalendar.getTime()), midLandFcstData.getWf7Am(), midLandFcstData.getWf7Pm()
                    , midLandFcstData.getRnSt7Am(), midLandFcstData.getRnSt7Pm(), midTaFcstData.getTaMin7(), midTaFcstData.getTaMax7()));

            copiedCalendar.add(Calendar.DAY_OF_YEAR, 1);

            midFcstDataList.add(new MidFcstData(Clock.MdE_FORMAT.format(copiedCalendar.getTime()), midLandFcstData.getWf8(), midLandFcstData.getRnSt8(), midTaFcstData.getTaMin8(), midTaFcstData.getTaMax8()));

            copiedCalendar.add(Calendar.DAY_OF_YEAR, 1);

            midFcstDataList.add(new MidFcstData(Clock.MdE_FORMAT.format(copiedCalendar.getTime()), midLandFcstData.getWf9(), midLandFcstData.getRnSt9(), midTaFcstData.getTaMin9(), midTaFcstData.getTaMax9()));

            copiedCalendar.add(Calendar.DAY_OF_YEAR, 1);

            midFcstDataList.add(new MidFcstData(Clock.MdE_FORMAT.format(copiedCalendar.getTime()), midLandFcstData.getWf10(), midLandFcstData.getRnSt10(), midTaFcstData.getTaMin10(), midTaFcstData.getTaMax10()));

            midFcstFinalData = new DataWrapper<>(midFcstDataList);
        } else
        {
            midFcstFinalData = new DataWrapper<>(midLandFcstItemsDataWrapper.getException() != null ? midLandFcstItemsDataWrapper.getException() :
                    midTaItemsDataWrapper.getException());
        }
        return this;
    }

    /*
   초단기예보 최종 데이터 생성
    */
    public WeatherData setUltraSrtFcstDataList()
    {
        if (ultraSrtFcstItemsDataWrapper.getException() == null)
        {
            String dateTime = null;
            Map<String, List<UltraSrtFcstItem>> map = new HashMap<>();

            List<UltraSrtFcstItem> items = ultraSrtFcstItemsDataWrapper.getData().getItem();

            //데이터를 날짜별로 분류해서 map에 저장
            for (UltraSrtFcstItem item : items)
            {
                dateTime = item.getFcstDate() + item.getFcstTime();
                if (map.get(dateTime) == null)
                {
                    map.put(dateTime, new ArrayList<>());
                }
                map.get(dateTime).add(item);
            }

            //카테고리와 값으로 되어있는 데이터를 날짜별로 조합하여 초단기예보 객체를 생성
            Set set = map.keySet();
            Iterator iterator = set.iterator();

            List<UltraSrtFcstData> ultraShortFcstDataList = new ArrayList<>();

            while (iterator.hasNext())
            {
                ultraShortFcstDataList.add(new UltraSrtFcstData(map.get(iterator.next())));
            }

            //초단기예보 데이터 리스트를 날짜 오름차순으로 정렬
            Collections.sort(ultraShortFcstDataList, new Comparator<UltraSrtFcstData>()
            {
                @Override
                public int compare(UltraSrtFcstData t1, UltraSrtFcstData t2)
                {
                    if ((t1.getDateTime()).compareTo(t2.getDateTime()) > 0)
                    {
                        return 1;
                    } else
                    {
                        return 0;
                    }
                }
            });

            ultraSrtFcstFinalData = new DataWrapper<>(ultraShortFcstDataList);
        } else
        {
            ultraSrtFcstFinalData = new DataWrapper<>(ultraSrtFcstItemsDataWrapper.getException());
        }
        return this;
    }

    /*
    동네예보 최종 데이터 생성
     */
    public WeatherData setVilageFcstDataList()
    {
        if (vilageFcstItemsDataWrapper.getException() == null)
        {
            String dateTime = null;
            Map<String, List<VilageFcstItem>> map = new HashMap<>();

            List<VilageFcstItem> items = vilageFcstItemsDataWrapper.getData().getItem();

            //데이터를 날짜별로 분류해서 map에 저장
            for (VilageFcstItem item : items)
            {
                dateTime = item.getFcstDate() + item.getFcstTime();
                if (map.get(dateTime) == null)
                {
                    map.put(dateTime, new ArrayList<>());
                }
                map.get(dateTime).add(item);
            }

            //카테고리와 값으로 되어있는 데이터를 날짜별로 조합하여 초단기예보 객체를 생성
            Set set = map.keySet();
            Iterator iterator = set.iterator();

            List<VilageFcstData> vilageFcstDataList = new ArrayList<>();

            while (iterator.hasNext())
            {
                vilageFcstDataList.add(new VilageFcstData(map.get(iterator.next())));
            }

            //초단기예보 데이터 리스트를 날짜 오름차순으로 정렬
            Collections.sort(vilageFcstDataList, new Comparator<VilageFcstData>()
            {
                @Override
                public int compare(VilageFcstData t1, VilageFcstData t2)
                {
                    if ((t1.getDateTime()).compareTo(t2.getDateTime()) > 0)
                    {
                        return 1;
                    } else
                    {
                        return 0;
                    }
                }
            });

            vilageFcstFinalData = new DataWrapper<>(vilageFcstDataList);
        } else
        {
            vilageFcstFinalData = new DataWrapper<>(vilageFcstItemsDataWrapper.getException());
        }
        return this;
    }


}
