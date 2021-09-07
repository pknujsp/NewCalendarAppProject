package com.zerodsoft.calendarplatform.weather.hourlyfcst;

import android.util.ArrayMap;

import com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.HourlyFcstItem;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.ultrasrtfcstresponse.UltraSrtFcstItem;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.ultrasrtfcstresponse.UltraSrtFcstItems;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.vilagefcstresponse.VilageFcstItem;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.vilagefcstresponse.VilageFcstItems;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class HourlyFcstResult {
	private Date downloadedDate;
	private List<HourlyFcstFinalData> hourlyFcstFinalDataList = new ArrayList<>();

	public void setHourlyFcstFinalDataList(VilageFcstItems vilageFcstItems, UltraSrtFcstItems ultraSrtFcstItems, Date downloadedDate) {
		this.downloadedDate = downloadedDate;

		List<UltraSrtFcstItem> ultraSrtFcstItemList = ultraSrtFcstItems.getItem();
		List<VilageFcstItem> vilageItemList = vilageFcstItems.getItem();

		ArrayMap<String, List<HourlyFcstItem>> hourlyFcstArrMap = new ArrayMap<>();
		String dateTime = null;
		//초단기예보 마지막 데이터의 날짜 set

		//데이터를 날짜별로 분류해서 map에 저장
		for (UltraSrtFcstItem item : ultraSrtFcstItemList) {
			dateTime = item.getFcstDate() + item.getFcstTime();
			if (!hourlyFcstArrMap.containsKey(dateTime)) {
				hourlyFcstArrMap.put(dateTime, new ArrayList<>());
			}
			hourlyFcstArrMap.get(dateTime).add(item);
		}

		final long lastDateTimeLongOfUltraSrtFcst = Long.parseLong(dateTime);
		long dateTimeLong = 0L;

		for (VilageFcstItem item : vilageItemList) {
			dateTime = item.getFcstDate() + item.getFcstTime();
			dateTimeLong = Long.parseLong(dateTime);

			if (dateTimeLong > lastDateTimeLongOfUltraSrtFcst) {

				if (!hourlyFcstArrMap.containsKey(dateTime)) {
					hourlyFcstArrMap.put(dateTime, new ArrayList<>());
				}
				hourlyFcstArrMap.get(dateTime).add(item);
			}
		}

		Set set = hourlyFcstArrMap.keySet();
		Iterator iterator = set.iterator();

		hourlyFcstFinalDataList.clear();

		while (iterator.hasNext()) {
			hourlyFcstFinalDataList.add(new HourlyFcstFinalData(hourlyFcstArrMap.get(iterator.next())));
		}

	}

	public Date getDownloadedDate() {
		return downloadedDate;
	}

	public List<HourlyFcstFinalData> getHourlyFcstFinalDataList() {
		return hourlyFcstFinalDataList;
	}
}
