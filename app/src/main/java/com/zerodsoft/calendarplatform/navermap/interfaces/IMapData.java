package com.zerodsoft.calendarplatform.navermap.interfaces;

import com.zerodsoft.calendarplatform.navermap.MarkerType;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.KakaoLocalDocument;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IMapData {
	void createMarkers(@NotNull List<? extends KakaoLocalDocument> kakaoLocalDocuments, @NotNull MarkerType markerType);

	void addExtraMarkers(@NotNull List<? extends KakaoLocalDocument> kakaoLocalDocuments, @NotNull MarkerType markerType);

	void removeMarkers(MarkerType... markerTypes);

	void removeMarker(MarkerType markerType, int index);

	void removeAllMarkers();

	void showMarkers(MarkerType... markerTypes);

	void showMarkers(MarkerType markerType, boolean isShow);

	void deselectMarker();

}
