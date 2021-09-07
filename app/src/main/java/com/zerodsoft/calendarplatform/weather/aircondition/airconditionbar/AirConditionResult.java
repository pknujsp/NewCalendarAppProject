package com.zerodsoft.calendarplatform.weather.aircondition.airconditionbar;

import com.zerodsoft.calendarplatform.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyBody;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyItem;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.aircondition.NearbyMsrstnList.NearbyMsrstnListRoot;

import java.util.Date;

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
	public void setAirConditionFinalData(MsrstnAcctoRltmMesureDnstyBody msrstnAcctoRltmMesureDnstyBody, NearbyMsrstnListRoot nearbyMsrstnListRoot,
	                                     Date downloadedDate) {
		this.msrstnAcctoRltmMesureDnstyItem = msrstnAcctoRltmMesureDnstyBody.getItem().isEmpty() ? null :
				msrstnAcctoRltmMesureDnstyBody.getItem().get(0);
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
