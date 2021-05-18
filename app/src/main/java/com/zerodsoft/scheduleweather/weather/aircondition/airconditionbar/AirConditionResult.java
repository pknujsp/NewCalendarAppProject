package com.zerodsoft.scheduleweather.weather.aircondition.airconditionbar;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyBody;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.NearbyMsrstnList.NearbyMsrstnListRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtncstresponse.UltraSrtNcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtncstresponse.UltraSrtNcstItems;
import com.zerodsoft.scheduleweather.weather.ultrasrtncst.UltraSrtNcstFinalData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AirConditionResult {
	//대기상태 최종 데이터
	private MsrstnAcctoRltmMesureDnstyItem airConditionFinalData;
	//측정소 데이터
	private NearbyMsrstnListRoot nearbyMsrstnListRoot;
	//대기상태 응답 데이터
	private MsrstnAcctoRltmMesureDnstyItem msrstnAcctoRltmMesureDnstyItem;
	private Date downloadedDate;

	/*
	초단기실황 최종 데이터 생성
	 */
	public void setAirConditionFinalData(MsrstnAcctoRltmMesureDnstyItem msrstnAcctoRltmMesureDnstyItem, NearbyMsrstnListRoot nearbyMsrstnListRoot,
	                                     Date downloadedDate) {
		this.msrstnAcctoRltmMesureDnstyItem = msrstnAcctoRltmMesureDnstyItem;
		this.airConditionFinalData = msrstnAcctoRltmMesureDnstyItem;
		this.nearbyMsrstnListRoot = nearbyMsrstnListRoot;
		this.downloadedDate = downloadedDate;

	}

	public MsrstnAcctoRltmMesureDnstyItem getAirConditionFinalData() {
		return airConditionFinalData;
	}

	public MsrstnAcctoRltmMesureDnstyItem getMsrstnAcctoRltmMesureDnstyItem() {
		return msrstnAcctoRltmMesureDnstyItem;
	}

	public Date getDownloadedDate() {
		return downloadedDate;
	}

	public NearbyMsrstnListRoot getNearbyMsrstnListRoot() {
		return nearbyMsrstnListRoot;
	}
}
