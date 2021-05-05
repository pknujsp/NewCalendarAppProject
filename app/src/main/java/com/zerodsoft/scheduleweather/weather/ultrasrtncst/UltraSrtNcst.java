package com.zerodsoft.scheduleweather.weather.ultrasrtncst;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtncstresponse.UltraSrtNcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtncstresponse.UltraSrtNcstItems;

import java.util.Date;
import java.util.List;

public class UltraSrtNcst
{
    //초단기실황 최종 데이터
    private UltraSrtNcstFinalData ultraSrtNcstFinalData;
    //초단기실황 응답 데이터
    private UltraSrtNcstItems ultraSrtNcstItems;
    private Date downloadedDate;

    /*
     초단기실황 최종 데이터 생성
      */
    public void setUltraSrtNcstFinalData(UltraSrtNcstItems ultraSrtNcstItems, Date downloadedDate)
    {
        this.downloadedDate = downloadedDate;
        this.ultraSrtNcstItems = ultraSrtNcstItems;
        List<UltraSrtNcstItem> items = ultraSrtNcstItems.getItem();
        this.ultraSrtNcstFinalData = new UltraSrtNcstFinalData(items);
    }

    public UltraSrtNcstFinalData getUltraSrtNcstFinalData()
    {
        return ultraSrtNcstFinalData;
    }

    public UltraSrtNcstItems getUltraSrtNcstItems()
    {
        return ultraSrtNcstItems;
    }

    public Date getDownloadedDate()
    {
        return downloadedDate;
    }
}
