package com.zerodsoft.calendarplatform.weather.hourlyfcst;

import android.util.ArrayMap;
import android.util.SparseLongArray;

import com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.HourlyFcstItem;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.ultrasrtfcstresponse.UltraSrtFcstItem;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.ultrasrtfcstresponse.UltraSrtFcstItems;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.vilagefcstresponse.VilageFcstItem;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.vilagefcstresponse.VilageFcstItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HourlyFcstResult {
	private Date downloadedDate;
	private List<HourlyFcstFinalData> hourlyFcstFinalDataList = new ArrayList<>();

	public void setHourlyFcstFinalDataList(VilageFcstItems vilageFcstItems, UltraSrtFcstItems ultraSrtFcstItems, Date downloadedDate) {
		this.downloadedDate = downloadedDate;

		List<UltraSrtFcstItem> ultraSrtFcstItemList = ultraSrtFcstItems.getItem();
		List<VilageFcstItem> vilageItemList = vilageFcstItems.getItem();
		Map<Long, List<HourlyFcstItem>> hourDataListMap = new HashMap();

		long newDateTime = 0L;
		long lastDateTime = 0L;

		//데이터를 날짜별로 분류해서 map에 저장
		for (UltraSrtFcstItem item : ultraSrtFcstItemList) {
			newDateTime = Long.parseLong(new String(item.getFcstDate() + item.getFcstTime()));
			if (newDateTime > lastDateTime) {
				hourDataListMap.put(newDateTime, new ArrayList<>());
				lastDateTime = newDateTime;
			}
			hourDataListMap.get(newDateTime).add(item);
		}

		final long lastDateTimeLongOfUltraSrtFcst = lastDateTime;

		for (VilageFcstItem item : vilageItemList) {
			newDateTime = Long.parseLong(new String(item.getFcstDate() + item.getFcstTime()));

			if (newDateTime > lastDateTimeLongOfUltraSrtFcst) {
				if (newDateTime > lastDateTime) {
					hourDataListMap.put(newDateTime, new ArrayList<>());
					lastDateTime = newDateTime;
				}
				hourDataListMap.get(newDateTime).add(item);
			}
		}

		hourlyFcstFinalDataList.clear();

		for (Map.Entry<Long, List<HourlyFcstItem>> entry :
				hourDataListMap.entrySet()) {
			HourlyFcstFinalData hourlyFcstFinalData = new HourlyFcstFinalData();
			hourlyFcstFinalData.init(entry.getValue());
			hourlyFcstFinalDataList.add(hourlyFcstFinalData);
		}

		Collections.sort(hourlyFcstFinalDataList, new Comparator<HourlyFcstFinalData>() {
			@Override
			public int compare(HourlyFcstFinalData t1, HourlyFcstFinalData t2) {
				return t1.getFcstDateTime().compareTo(t2.getFcstDateTime());
			}
		});
	}

	public Date getDownloadedDate() {
		return downloadedDate;
	}

	public List<HourlyFcstFinalData> getHourlyFcstFinalDataList() {
		return hourlyFcstFinalDataList;
	}
}
