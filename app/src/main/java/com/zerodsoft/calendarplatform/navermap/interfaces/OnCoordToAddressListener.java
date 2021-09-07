package com.zerodsoft.calendarplatform.navermap.interfaces;

import com.zerodsoft.calendarplatform.common.classes.JsonDownloader;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;

public interface OnCoordToAddressListener
{
    void coordToAddress(String latitude, String longitude, JsonDownloader<CoordToAddressDocuments> jsonDownloader);
}
