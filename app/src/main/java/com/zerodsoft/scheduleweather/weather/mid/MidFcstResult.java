package com.zerodsoft.scheduleweather.weather.mid;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.midlandfcstresponse.MidLandFcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.midlandfcstresponse.MidLandFcstItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.midtaresponse.MidTaItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.midtaresponse.MidTaItems;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MidFcstResult {
	//중기예보(중기육상예보+중기기온예보) 최종 데이터
	private List<MidFcstData> midFcstFinalDataList;
	//중기육상예보 응답 데이터
	private MidLandFcstItems midLandFcstItems;
	//중기기온예보 응답 데이터
	private MidTaItems midTaItems;
	private Date downloadedDate;
	
	/*
	   중기예보 최종 데이터 생성
	*/
	public void setMidFcstDataList(MidLandFcstItems midLandFcstItems, MidTaItems midTaItems, Date downloadedDate) {
		this.midLandFcstItems = midLandFcstItems;
		this.midTaItems = midTaItems;
		this.downloadedDate = downloadedDate;
		
		//중기예보 데이터 생성 3~10일후
		Calendar copiedCalendar = Calendar.getInstance();
		copiedCalendar.setTime(downloadedDate);
		
		MidLandFcstItem midLandFcstData = midLandFcstItems.getItem().get(0);
		MidTaItem midTaFcstData = midTaItems.getItem().get(0);
		List<MidFcstData> midFcstDataList = new ArrayList<>();
		
		//3일 후로 이동
		copiedCalendar.add(Calendar.DAY_OF_YEAR, 3);
		
		midFcstDataList.add(new MidFcstData(ClockUtil.MdE_FORMAT.format(copiedCalendar.getTime()), midLandFcstData.getWf3Am(),
				midLandFcstData.getWf3Pm(), midLandFcstData.getRnSt3Am(), midLandFcstData.getRnSt3Pm(), midTaFcstData.getTaMin3(),
				midTaFcstData.getTaMax3()));
		
		copiedCalendar.add(Calendar.DAY_OF_YEAR, 1);
		
		midFcstDataList.add(new MidFcstData(ClockUtil.MdE_FORMAT.format(copiedCalendar.getTime()), midLandFcstData.getWf4Am(),
				midLandFcstData.getWf4Pm(), midLandFcstData.getRnSt4Am(), midLandFcstData.getRnSt4Pm(), midTaFcstData.getTaMin4(),
				midTaFcstData.getTaMax4()));
		
		copiedCalendar.add(Calendar.DAY_OF_YEAR, 1);
		
		midFcstDataList.add(new MidFcstData(ClockUtil.MdE_FORMAT.format(copiedCalendar.getTime()), midLandFcstData.getWf5Am(),
				midLandFcstData.getWf5Pm(), midLandFcstData.getRnSt5Am(), midLandFcstData.getRnSt5Pm(), midTaFcstData.getTaMin5(),
				midTaFcstData.getTaMax5()));
		
		copiedCalendar.add(Calendar.DAY_OF_YEAR, 1);
		
		midFcstDataList.add(new MidFcstData(ClockUtil.MdE_FORMAT.format(copiedCalendar.getTime()), midLandFcstData.getWf6Am(),
				midLandFcstData.getWf6Pm(), midLandFcstData.getRnSt6Am(), midLandFcstData.getRnSt6Pm(), midTaFcstData.getTaMin6(),
				midTaFcstData.getTaMax6()));
		
		copiedCalendar.add(Calendar.DAY_OF_YEAR, 1);
		
		midFcstDataList.add(new MidFcstData(ClockUtil.MdE_FORMAT.format(copiedCalendar.getTime()), midLandFcstData.getWf7Am(),
				midLandFcstData.getWf7Pm(), midLandFcstData.getRnSt7Am(), midLandFcstData.getRnSt7Pm(), midTaFcstData.getTaMin7(),
				midTaFcstData.getTaMax7()));
		
		copiedCalendar.add(Calendar.DAY_OF_YEAR, 1);
		
		midFcstDataList.add(
				new MidFcstData(ClockUtil.MdE_FORMAT.format(copiedCalendar.getTime()), midLandFcstData.getWf8(), midLandFcstData.getRnSt8(),
						midTaFcstData.getTaMin8(), midTaFcstData.getTaMax8()));
		
		copiedCalendar.add(Calendar.DAY_OF_YEAR, 1);
		
		midFcstDataList.add(
				new MidFcstData(ClockUtil.MdE_FORMAT.format(copiedCalendar.getTime()), midLandFcstData.getWf9(), midLandFcstData.getRnSt9(),
						midTaFcstData.getTaMin9(), midTaFcstData.getTaMax9()));
		
		copiedCalendar.add(Calendar.DAY_OF_YEAR, 1);
		
		midFcstDataList.add(new MidFcstData(ClockUtil.MdE_FORMAT.format(copiedCalendar.getTime()), midLandFcstData.getWf10(),
				midLandFcstData.getRnSt10(), midTaFcstData.getTaMin10(), midTaFcstData.getTaMax10()));
		
		midFcstFinalDataList = midFcstDataList;
	}
	
	public Date getDownloadedDate() {
		return downloadedDate;
	}
	
	public List<MidFcstData> getMidFcstFinalDataList() {
		return midFcstFinalDataList;
	}
	
	public MidLandFcstItems getMidLandFcstItems() {
		return midLandFcstItems;
	}
	
	public MidTaItems getMidTaItems() {
		return midTaItems;
	}
}
