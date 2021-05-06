package com.zerodsoft.scheduleweather.weather.interfaces;

import java.util.Date;

public interface OnDownloadedTimeListener
{
    void setDownloadedTime(Date downloadedTime, int dataType);
}
