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

    private final int index;

    public WeatherData(int index, String areaName, String nx, String ny, String midLandFcstRegId, String midTaFcstRegId, Calendar downloadedDate)
    {
        this.index = index;
        this.areaName = areaName;
        this.nx = nx;
        this.ny = ny;
        this.midLandFcstRegId = midLandFcstRegId;
        this.midTaFcstRegId = midTaFcstRegId;
        this.downloadedDate = (Calendar) downloadedDate.clone();
    }

    public Calendar getDownloadedDate()
    {
        return (Calendar) downloadedDate.clone();
    }

    public int getIndex()
    {
        return index;
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
        String dateTime = null;
        Map<String, List<UltraSrtFcstItem>> map = new HashMap<>();

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
        return this;
    }

    public WeatherData setVilageFcstDataList(List<VilageFcstItem> items)
    {
        String dateTime = null;
        Map<String, List<VilageFcstItem>> map = new HashMap<>();

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
        return this;
    }
}
