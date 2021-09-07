package com.zerodsoft.calendarplatform.retrofit.queryresponse.map.coordtoregioncoderesponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.KakaoLocalResponse;

import java.util.List;

public class CoordToRegionCode extends KakaoLocalResponse {
	@SerializedName("meta")
	@Expose
	private CoordToRegionCodeMeta coordToRegionCodeMeta;

	@SerializedName("documents")
	@Expose
	List<CoordToRegionCodeDocument> coordToRegionCodeDocuments;

	public CoordToRegionCodeMeta getCoordToRegionCodeMeta() {
		return coordToRegionCodeMeta;
	}

	public void setCoordToRegionCodeMeta(CoordToRegionCodeMeta coordToRegionCodeMeta) {
		this.coordToRegionCodeMeta = coordToRegionCodeMeta;
	}

	public List<CoordToRegionCodeDocument> getCoordToRegionCodeDocuments() {
		return coordToRegionCodeDocuments;
	}

	public void setCoordToRegionCodeDocuments(List<CoordToRegionCodeDocument> coordToRegionCodeDocuments) {
		this.coordToRegionCodeDocuments = coordToRegionCodeDocuments;
	}
}
