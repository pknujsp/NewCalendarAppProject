package com.zerodsoft.calendarplatform.kakaoplace.datasourcefactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.zerodsoft.calendarplatform.kakaoplace.datasource.KakaoRestaurantsDataSource;
import com.zerodsoft.calendarplatform.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

public class KakaoRestaurantsDataSourceFactory extends DataSource.Factory<Integer, PlaceDocuments> {
	private KakaoRestaurantsDataSource dataSource;
	private MutableLiveData<KakaoRestaurantsDataSource> liveData;
	private LocalApiPlaceParameter placeParameter;

	public KakaoRestaurantsDataSourceFactory(LocalApiPlaceParameter placeParameter ) {
		liveData = new MutableLiveData<>();
		this.placeParameter = placeParameter;
	}

	@NonNull
	@Override
	public DataSource<Integer, PlaceDocuments> create() {
		dataSource = new KakaoRestaurantsDataSource(placeParameter);
		liveData.postValue(dataSource);
		return dataSource;
	}


	public MutableLiveData<KakaoRestaurantsDataSource> getLiveData() {
		return liveData;
	}
}
