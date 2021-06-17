package com.zerodsoft.scheduleweather.kakaoplace.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.zerodsoft.scheduleweather.kakaoplace.datasource.KakaoRestaurantsDataSource;
import com.zerodsoft.scheduleweather.kakaoplace.datasourcefactory.KakaoRestaurantsDataSourceFactory;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class KakaoRestaurantsViewModel extends ViewModel {
	private LiveData<PagedList<PlaceDocuments>> pagedListLiveData;
	private KakaoRestaurantsDataSourceFactory dataSourceFactory;
	private MutableLiveData<KakaoRestaurantsDataSource> dataSourceMutableLiveData;
	private Executor executor;
	private PagedList.Config config;

	public KakaoRestaurantsViewModel() {
		executor = Executors.newSingleThreadExecutor();
		pagedListLiveData = new MutableLiveData<>();
	}

	public void init(LocalApiPlaceParameter placeParameter, PagedList.BoundaryCallback<PlaceDocuments> boundaryCallback) {
		dataSourceFactory = new KakaoRestaurantsDataSourceFactory(placeParameter);
		dataSourceMutableLiveData = dataSourceFactory.getLiveData();

		config = (new PagedList.Config.Builder())
				.setEnablePlaceholders(false)
				.setInitialLoadSizeHint(Integer.parseInt(LocalApiPlaceParameter.DEFAULT_SIZE) * 2)
				.setPageSize(15)
				.setPrefetchDistance(4)
				.build();

		pagedListLiveData = new LivePagedListBuilder<Integer, PlaceDocuments>(dataSourceFactory, config)
				.setBoundaryCallback(boundaryCallback)
				.setFetchExecutor(executor)
				.build();

	}

	public LiveData<PagedList<PlaceDocuments>> getPagedListMutableLiveData() {
		return pagedListLiveData;
	}

}
