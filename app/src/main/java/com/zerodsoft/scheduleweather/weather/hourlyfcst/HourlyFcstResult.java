package com.zerodsoft.scheduleweather.weather.hourlyfcst;

import android.util.ArrayMap;
import android.util.ArraySet;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.HourlyFcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtfcstresponse.UltraSrtFcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtfcstresponse.UltraSrtFcstItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.vilagefcstresponse.VilageFcstItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.vilagefcstresponse.VilageFcstItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class HourlyFcstResult {
	private Date downloadedDate;
	private List<HourlyFcstFinalData> hourlyFcstFinalDataList = new ArrayList<>();

	public void setHourlyFcstFinalDataList(VilageFcstItems vilageFcstItems, UltraSrtFcstItems ultraSrtFcstItems, Date downloadedDate) {
		this.downloadedDate = downloadedDate;

		ArrayMap<String, List<HourlyFcstItem>> hourlyFcstArrMap = new ArrayMap<>();
		List<UltraSrtFcstItem> ultraSrtFcstItemList = ultraSrtFcstItems.getItem();
		List<VilageFcstItem> vilageItemList = vilageFcstItems.getItem();

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

			if (dateTimeLong <= lastDateTimeLongOfUltraSrtFcst) {
				continue;
			}

			if (!hourlyFcstArrMap.containsKey(dateTime)) {
				hourlyFcstArrMap.put(dateTime, new ArrayList<>());
			}
			hourlyFcstArrMap.get(dateTime).add(item);
		}

		//카테고리와 값으로 되어있는 데이터를 날짜별로 조합하여 초단기예보 객체를 생성
		Set set = hourlyFcstArrMap.keySet();
		Iterator iterator = set.iterator();

		hourlyFcstFinalDataList.clear();

		while (iterator.hasNext()) {
			hourlyFcstFinalDataList.add(new HourlyFcstFinalData(hourlyFcstArrMap.get(iterator.next())));
		}

		//동네예보 데이터 리스트를 날짜 오름차순으로 정렬
		Collections.sort(hourlyFcstFinalDataList, (t1, t2) ->
		{
			if (t1.getFcstDateTime().after(t2.getFcstDateTime())) {
				return 1;
			} else {
				return -1;
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
