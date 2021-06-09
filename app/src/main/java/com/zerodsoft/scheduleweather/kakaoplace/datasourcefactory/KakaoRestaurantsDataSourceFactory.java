package com.zerodsoft.scheduleweather.kakaoplace.datasourcefactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.zerodsoft.scheduleweather.common.interfaces.OnProgressBarListener;
import com.zerodsoft.scheduleweather.common.interfaces.OnProgressViewListener;
import com.zerodsoft.scheduleweather.kakaoplace.datasource.KakaoRestaurantsDataSource;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

public class KakaoRestaurantsDataSourceFactory extends DataSource.Factory<Integer, PlaceDocuments> {
	private KakaoRestaurantsDataSource dataSource;
	private MutableLiveData<KakaoRestaurantsDataSource> liveData;
	private LocalApiPlaceParameter placeParameter;
	private OnProgressViewListener onProgressViewListener;

	public KakaoRestaurantsDataSourceFactory(LocalApiPlaceParameter placeParameter, OnProgressViewListener onProgressViewListener) {
		liveData = new MutableLiveData<>();
		this.placeParameter = placeParameter;
		this.onProgressViewListener = onProgressViewListener;
	}

	@NonNull
	@Override
	public DataSource<Integer, PlaceDocuments> create() {
		dataSource = new KakaoRestaurantsDataSource(placeParameter, onProgressViewListener);
		liveData.postValue(dataSource);
		return dataSource;
	}


	public MutableLiveData<KakaoRestaurantsDataSource> getLiveData() {
		return liveData;
	}
}
