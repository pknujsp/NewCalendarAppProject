package com.zerodsoft.scheduleweather.event.weather.resultdata;

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
import com.zerodsoft.scheduleweather.event.weather.resultdata.responseresult.MidFcstData;
import com.zerodsoft.scheduleweather.event.weather.resultdata.responseresult.UltraSrtFcstData;
import com.zerodsoft.scheduleweather.event.weather.resultdata.responseresult.UltraSrtNcstData;
import com.zerodsoft.scheduleweather.event.weather.resultdata.responseresult.VilageFcstData;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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

    //초단기실황 최종 데이터
    private UltraSrtNcstData ultraSrtNcstFinalData;
    //초단기예보 최종 데이터
    private List<UltraSrtFcstData> ultraSrtFcstFinalData;
    //동네예보 최종 데이터
    private List<VilageFcstData> vilageFcstFinalData;
    //중기예보(중기육상예보+중기기온예보) 최종 데이터
    private List<MidFcstData> midFcstFinalData;

    //초단기실황 응답 데이터
    private UltraSrtNcstItems ultraSrtNcstItems;
    //초단기예보 응답 데이터
    private UltraSrtFcstItems ultraSrtFcstItems;
    //동네예보 응답 데이터
    private VilageFcstItems vilageFcstItems;
    //중기육상예보 응답 데이터
    private MidLandFcstItems midLandFcstItems;
    //중기기온예보 응답 데이터
    private MidTaItems midTaItems;


    public WeatherData(Calendar downloadedDate, WeatherAreaCodeDTO weatherAreaCode)
    {
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

    public String getAreaName()
    {
        return areaName;
    }


    public WeatherData setUltraSrtNcstItems(UltraSrtNcstItems ultraSrtNcstItems)
    {
        this.ultraSrtNcstItems = ultraSrtNcstItems;
        return this;
    }

    public WeatherData setUltraSrtFcstItems(UltraSrtFcstItems ultraSrtFcstItems)
    {
        this.ultraSrtFcstItems = ultraSrtFcstItems;
        return this;
    }

    public WeatherData setVilageFcstItems(VilageFcstItems vilageFcstItems)
    {
        this.vilageFcstItems = vilageFcstItems;
        return this;
    }

    public WeatherData setMidLandFcstItems(MidLandFcstItems midLandFcstItems)
    {
        this.midLandFcstItems = midLandFcstItems;
        return this;
    }

    public WeatherData setMidTaItems(MidTaItems midTaItems)
    {
        this.midTaItems = midTaItems;
        return this;
    }

    public UltraSrtNcstData getUltraSrtNcstFinalData()
    {
        return ultraSrtNcstFinalData;
    }

    public List<UltraSrtFcstData> getUltraSrtFcstFinalData()
    {
        return ultraSrtFcstFinalData;
    }

    public List<VilageFcstData> getVilageFcstFinalData()
    {
        return vilageFcstFinalData;
    }

    public List<MidFcstData> getMidFcstFinalData()
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

        List<UltraSrtNcstItem> items = ultraSrtNcstItems.getItem();
        UltraSrtNcstData ultraSrtNcstData = new UltraSrtNcstData(items);

        ultraSrtNcstFinalData = ultraSrtNcstData;

        return this;
    }

    /*
   중기예보 최종 데이터 생성
    */
    public WeatherData setMidFcstDataList()
    {

        //중기예보 데이터 생성 3~10일후
        Calendar copiedCalendar = (Calendar) downloadedDate.clone();

        MidLandFcstItem midLandFcstData = midLandFcstItems.getItem().get(0);
        MidTaItem midTaFcstData = midTaItems.getItem().get(0);
        List<MidFcstData> midFcstDataList = new ArrayList<>();

        //3일 후로 이동
        copiedCalendar.add(Calendar.DAY_OF_YEAR, 3);

        midFcstDataList.add(new MidFcstData(ClockUtil.MdE_FORMAT.format(copiedCalendar.getTime()), midLandFcstData.getWf3Am(), midLandFcstData.getWf3Pm()
                , midLandFcstData.getRnSt3Am(), midLandFcstData.getRnSt3Pm(), midTaFcstData.getTaMin3(), midTaFcstData.getTaMax3()));

        copiedCalendar.add(Calendar.DAY_OF_YEAR, 1);

        midFcstDataList.add(new MidFcstData(ClockUtil.MdE_FORMAT.format(copiedCalendar.getTime()), midLandFcstData.getWf4Am(), midLandFcstData.getWf4Pm()
                , midLandFcstData.getRnSt4Am(), midLandFcstData.getRnSt4Pm(), midTaFcstData.getTaMin4(), midTaFcstData.getTaMax4()));

        copiedCalendar.add(Calendar.DAY_OF_YEAR, 1);

        midFcstDataList.add(new MidFcstData(ClockUtil.MdE_FORMAT.format(copiedCalendar.getTime()), midLandFcstData.getWf5Am(), midLandFcstData.getWf5Pm()
                , midLandFcstData.getRnSt5Am(), midLandFcstData.getRnSt5Pm(), midTaFcstData.getTaMin5(), midTaFcstData.getTaMax5()));

        copiedCalendar.add(Calendar.DAY_OF_YEAR, 1);

        midFcstDataList.add(new MidFcstData(ClockUtil.MdE_FORMAT.format(copiedCalendar.getTime()), midLandFcstData.getWf6Am(), midLandFcstData.getWf6Pm()
                , midLandFcstData.getRnSt6Am(), midLandFcstData.getRnSt6Pm(), midTaFcstData.getTaMin6(), midTaFcstData.getTaMax6()));

        copiedCalendar.add(Calendar.DAY_OF_YEAR, 1);

        midFcstDataList.add(new MidFcstData(ClockUtil.MdE_FORMAT.format(copiedCalendar.getTime()), midLandFcstData.getWf7Am(), midLandFcstData.getWf7Pm()
                , midLandFcstData.getRnSt7Am(), midLandFcstData.getRnSt7Pm(), midTaFcstData.getTaMin7(), midTaFcstData.getTaMax7()));

        copiedCalendar.add(Calendar.DAY_OF_YEAR, 1);

        midFcstDataList.add(new MidFcstData(ClockUtil.MdE_FORMAT.format(copiedCalendar.getTime()), midLandFcstData.getWf8(), midLandFcstData.getRnSt8(), midTaFcstData.getTaMin8(), midTaFcstData.getTaMax8()));

        copiedCalendar.add(Calendar.DAY_OF_YEAR, 1);

        midFcstDataList.add(new MidFcstData(ClockUtil.MdE_FORMAT.format(copiedCalendar.getTime()), midLandFcstData.getWf9(), midLandFcstData.getRnSt9(), midTaFcstData.getTaMin9(), midTaFcstData.getTaMax9()));

        copiedCalendar.add(Calendar.DAY_OF_YEAR, 1);

        midFcstDataList.add(new MidFcstData(ClockUtil.MdE_FORMAT.format(copiedCalendar.getTime()), midLandFcstData.getWf10(), midLandFcstData.getRnSt10(), midTaFcstData.getTaMin10(), midTaFcstData.getTaMax10()));

        midFcstFinalData = midFcstDataList;
        return this;
    }

    /*
   초단기예보 최종 데이터 생성
    */
    public WeatherData setUltraSrtFcstDataList()
    {
        String dateTime = null;
        Map<String, List<UltraSrtFcstItem>> map = new HashMap<>();

        List<UltraSrtFcstItem> items = ultraSrtFcstItems.getItem();

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
        Collections.sort(ultraShortFcstDataList, (t1, t2) ->
        {
            if (t1.getDateTime().after(t2.getDateTime()))
            {
                return 1;
            } else
            {
                return -1;
            }
        });

        ultraSrtFcstFinalData = ultraShortFcstDataList;
        return this;
    }

    /*
    동네예보 최종 데이터 생성
     */
    public WeatherData setVilageFcstDataList()
    {

        String dateTime = null;
        Map<String, List<VilageFcstItem>> map = new HashMap<>();

        List<VilageFcstItem> items = vilageFcstItems.getItem();

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

        //동네예보 데이터 리스트를 날짜 오름차순으로 정렬
        Collections.sort(vilageFcstDataList, (t1, t2) ->
        {
            if (t1.getDateTime().after(t2.getDateTime()))
            {
                return 1;
            } else
            {
                return -1;
            }
        });

        vilageFcstFinalData = vilageFcstDataList;

        return this;
    }


}
