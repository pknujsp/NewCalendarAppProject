package com.zerodsoft.calendarplatform.navermap.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.zerodsoft.calendarplatform.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.calendarplatform.navermap.model.datasource.PlaceItemDataSource;
import com.zerodsoft.calendarplatform.navermap.model.datasourcefactory.PlaceItemDataSourceFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PlacesViewModel extends ViewModel {
	private LiveData<PagedList<PlaceDocuments>> pagedListLiveData;
	private PlaceItemDataSourceFactory dataSourceFactory;
	private MutableLiveData<PlaceItemDataSource> dataSourceMutableLiveData;
	private Executor executor;
	private PagedList.Config config;

	public PlacesViewModel() {
		executor = Executors.newSingleThreadExecutor();
		pagedListLiveData = new MutableLiveData<>();
	}

	public void init(LocalApiPlaceParameter placeParameter, PagedList.BoundaryCallback<PlaceDocuments> boundaryCallback) {
		dataSourceFactory = new PlaceItemDataSourceFactory(placeParameter);
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
