package com.zerodsoft.calendarplatform.weather.vilagefcst;

import com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.vilagefcstresponse.VilageFcstItem;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.vilagefcstresponse.VilageFcstItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VilageFcstResult {
	//동네예보 최종 데이터
	private List<VilageFcstFinalData> vilageFcstFinalDataList;
	//동네예보 응답 데이터
	private VilageFcstItems vilageFcstItems;
	private Date downloadedDate;

	/*
   동네예보 최종 데이터 생성
	*/
	public void setVilageFcstDataList(VilageFcstItems vilageFcstItems, Date downloadedDate) {
		this.vilageFcstItems = vilageFcstItems;
		this.downloadedDate = downloadedDate;

		String dateTime = null;
		Map<String, List<VilageFcstItem>> map = new HashMap<>();

		List<VilageFcstItem> items = vilageFcstItems.getItem();

		//데이터를 날짜별로 분류해서 map에 저장
		for (VilageFcstItem item : items) {
			dateTime = item.getFcstDate() + item.getFcstTime();
			if (map.get(dateTime) == null) {
				map.put(dateTime, new ArrayList<>());
			}
			map.get(dateTime).add(item);
		}

		//카테고리와 값으로 되어있는 데이터를 날짜별로 조합하여 초단기예보 객체를 생성
		Set set = map.keySet();
		Iterator iterator = set.iterator();

		List<VilageFcstFinalData> vilageFcstFinalDataList = new ArrayList<>();

		while (iterator.hasNext()) {
			vilageFcstFinalDataList.add(new VilageFcstFinalData(map.get(iterator.next())));
		}

		//동네예보 데이터 리스트를 날짜 오름차순으로 정렬
		Collections.sort(vilageFcstFinalDataList, (t1, t2) ->
		{
			if (t1.getFcstDateTime().after(t2.getFcstDateTime())) {
				return 1;
			} else {
				return -1;
			}
		});

		this.vilageFcstFinalDataList = vilageFcstFinalDataList;
	}


	public Date getDownloadedDate() {
		return downloadedDate;
	}

	public List<VilageFcstFinalData> getVilageFcstFinalDataList() {
		return vilageFcstFinalDataList;
	}

	public VilageFcstItems getVilageFcstItems() {
		return vilageFcstItems;
	}
}
