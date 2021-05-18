package com.zerodsoft.scheduleweather.weather.ultrasrtfcst;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtfcstresponse.UltraSrtFcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtfcstresponse.UltraSrtFcstItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UltraSrtFcstResult
{
    //초단기예보 최종 데이터
    private List<UltraSrtFcstFinalData> ultraSrtFcstFinalDataList;
    //초단기예보 응답 데이터
    private UltraSrtFcstItems ultraSrtFcstItems;
    private Date downloadedDate;

    /*
  초단기예보 최종 데이터 생성
   */
    public void setUltraSrtFcstFinalDataList(UltraSrtFcstItems ultraSrtFcstItems, Date downloadedDate)
    {
        this.downloadedDate = downloadedDate;
        this.ultraSrtFcstItems = ultraSrtFcstItems;

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

        List<UltraSrtFcstFinalData> ultraShortFcstDataList = new ArrayList<>();

        while (iterator.hasNext())
        {
            ultraShortFcstDataList.add(new UltraSrtFcstFinalData(map.get(iterator.next())));
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

        this.ultraSrtFcstFinalDataList = ultraShortFcstDataList;
    }

    public Date getDownloadedDate()
    {
        return downloadedDate;
    }

    public List<UltraSrtFcstFinalData> getUltraSrtFcstFinalDataList()
    {
        return ultraSrtFcstFinalDataList;
    }

    public UltraSrtFcstItems getUltraSrtFcstItems()
    {
        return ultraSrtFcstItems;
    }
}
