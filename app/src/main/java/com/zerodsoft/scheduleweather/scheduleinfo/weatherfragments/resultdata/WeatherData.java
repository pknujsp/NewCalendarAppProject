package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.midlandfcstresponse.MidLandFcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midtaresponse.MidTaItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtfcstresponse.UltraSrtFcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtncstresponse.UltraSrtNcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.vilagefcstresponse.VilageFcstItem;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.responseresult.MidFcstData;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.responseresult.UltraSrtFcstData;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.responseresult.UltraSrtNcstData;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.responseresult.VilageFcstData;
import com.zerodsoft.scheduleweather.utility.Clock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class WeatherData
{
    //지역 정보
    private final String areaName;
    //x좌표 값
    private final String nx;
    //y좌표 값
    private final String ny;
    //중기육상예보 regId값
    private final String midLandFcstRegId;
    //중기기온예보 regId값
    private final String midTaFcstRegId;
    //데이터 처음 불러올때의 시각
    private final Calendar downloadedDate;

    //초단기예보 데이터
    private List<UltraSrtFcstData> ultraShortFcstDataList = new ArrayList<>();
    //동네예보 데이터
    private List<VilageFcstData> vilageFcstDataList = new ArrayList<>();
    //중기육상예보+중기기온예보 데이터
    private List<MidFcstData> midFcstDataList = new ArrayList<>();

    //초단기실황 데이터
    private UltraSrtNcstData ultraSrtNcstData;
    //중기육상예보 데이터
    private MidLandFcstItem midLandFcstData;
    //중기기온예보 데이터
    private MidTaItem midTaFcstData;

    public WeatherData(String areaName, String nx, String ny, String midLandFcstRegId, String midTaFcstRegId, Calendar downloadedDate)
    {
        this.areaName = areaName;
        this.nx = nx;
        this.ny = ny;
        this.midLandFcstRegId = midLandFcstRegId;
        this.midTaFcstRegId = midTaFcstRegId;
        this.downloadedDate = (Calendar) downloadedDate.clone();
    }


    public WeatherData setUltraSrtNcstData(List<UltraSrtNcstItem> ultraSrtNcstItems)
    {
        ultraSrtNcstData = new UltraSrtNcstData(ultraSrtNcstItems);
        return this;
    }

    public String getAreaName()
    {
        return areaName;
    }

    public String getNx()
    {
        return nx;
    }

    public String getNy()
    {
        return ny;
    }

    public String getMidLandFcstRegId()
    {
        return midLandFcstRegId;
    }

    public String getMidTaFcstRegId()
    {
        return midTaFcstRegId;
    }


    public WeatherData setMidLandFcstData(MidLandFcstItem midLandFcstData)
    {
        this.midLandFcstData = midLandFcstData;
        return this;
    }

    public WeatherData setMidTaFcstData(MidTaItem midTaFcstData)
    {
        this.midTaFcstData = midTaFcstData;
        return this;
    }

    public List<MidFcstData> getMidFcstDataList()
    {
        return midFcstDataList;
    }

    public List<UltraSrtFcstData> getUltraShortFcstDataList()
    {
        return ultraShortFcstDataList;
    }

    public List<VilageFcstData> getVilageFcstDataList()
    {
        return vilageFcstDataList;
    }

    public UltraSrtNcstData getUltraSrtNcstData()
    {
        return ultraSrtNcstData;
    }


    public WeatherData setMidFcstDataList()
    {
        //중기예보 데이터 생성 3~10일후
        Calendar copiedCalendar = (Calendar) downloadedDate.clone();

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

        return this;
    }

    public WeatherData setUltraShortFcstDataList(List<UltraSrtFcstItem> items)
    {
        String lastDate = null;
        String lastTime = null;
        List<UltraSrtFcstItem> sameDateTimeItems = new ArrayList<>();

        for (int i = 0; i < items.size(); )
        {
            lastDate = items.get(i).getFcstDate();
            lastTime = items.get(i).getFcstTime();

            sameDateTimeItems.add(items.get(i));

            for (int j = i; j < items.size(); j++)
            {
                if (lastDate.equals(items.get(j).getFcstDate()) && lastTime.equals(items.get(j).getFcstTime()))
                {
                    sameDateTimeItems.add(items.get(j));

                    if (j == items.size())
                    {
                        List<UltraSrtFcstItem> list = new ArrayList<>(sameDateTimeItems.size());
                        Collections.copy(list, sameDateTimeItems);
                        ultraShortFcstDataList.add(new UltraSrtFcstData(list));
                        sameDateTimeItems.clear();

                        i = j;
                    }
                } else
                {
                    List<UltraSrtFcstItem> list = new ArrayList<>(sameDateTimeItems.size());
                    Collections.copy(list, sameDateTimeItems);
                    ultraShortFcstDataList.add(new UltraSrtFcstData(list));
                    sameDateTimeItems.clear();

                    i = j;
                }
            }
        }
        return this;
    }

    public WeatherData setVilageFcstDataList(List<VilageFcstItem> items)
    {
        String lastDate = null;
        String lastTime = null;
        List<VilageFcstItem> sameDateTimeItems = new ArrayList<>();

        for (int i = 0; i < items.size(); )
        {
            lastDate = items.get(i).getFcstDate();
            lastTime = items.get(i).getFcstTime();

            sameDateTimeItems.add(items.get(i));

            for (int j = i; j < items.size(); j++)
            {
                if (lastDate.equals(items.get(j).getFcstDate()) && lastTime.equals(items.get(j).getFcstTime()))
                {
                    sameDateTimeItems.add(items.get(j));

                    if (j == items.size())
                    {
                        List<VilageFcstItem> list = new ArrayList<>(sameDateTimeItems.size());
                        Collections.copy(list, sameDateTimeItems);
                        vilageFcstDataList.add(new VilageFcstData(list));
                        sameDateTimeItems.clear();

                        i = j;
                    }
                } else
                {
                    List<VilageFcstItem> list = new ArrayList<>(sameDateTimeItems.size());
                    Collections.copy(list, sameDateTimeItems);
                    vilageFcstDataList.add(new VilageFcstData(list));
                    sameDateTimeItems.clear();

                    i = j;
                }
            }
        }
        return this;
    }
}
