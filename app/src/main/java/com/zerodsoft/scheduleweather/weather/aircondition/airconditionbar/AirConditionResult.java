package com.zerodsoft.scheduleweather.weather.aircondition.airconditionbar;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyBody;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtncstresponse.UltraSrtNcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtncstresponse.UltraSrtNcstItems;
import com.zerodsoft.scheduleweather.weather.ultrasrtncst.UltraSrtNcstFinalData;

import java.util.Date;
import java.util.List;

public class AirConditionResult {
	//대기상태 최종 데이터
	private AirConditionFinalData airConditionFinalData;
	//대기상태 응답 데이터
	private MsrstnAcctoRltmMesureDnstyItem msrstnAcctoRltmMesureDnstyItem;
	private Date downloadedDate;

	/*
	초단기실황 최종 데이터 생성
	 */
	public void setAirConditionFinalData(MsrstnAcctoRltmMesureDnstyItem msrstnAcctoRltmMesureDnstyItem, Date downloadedDate) {
		this.downloadedDate = downloadedDate;
		this.msrstnAcctoRltmMesureDnstyItem = msrstnAcctoRltmMesureDnstyItem;
		this.airConditionFinalData = (AirConditionFinalData) msrstnAcctoRltmMesureDnstyItem;
	}

	public AirConditionFinalData getAirConditionFinalData() {
		return airConditionFinalData;
	}

	public MsrstnAcctoRltmMesureDnstyItem getMsrstnAcctoRltmMesureDnstyItem() {
		return msrstnAcctoRltmMesureDnstyItem;
	}

	public Date getDownloadedDate() {
		return downloadedDate;
	}
}
