package com.zerodsoft.scheduleweather.navermap.interfaces;

import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;

public interface OnCoordToAddressListener
{
    void coordToAddress(String latitude, String longitude, JsonDownloader<CoordToAddressDocuments> jsonDownloader);
}
