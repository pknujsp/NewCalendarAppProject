package com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoregioncoderesponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;

public class CoordToRegionCodeDocument extends KakaoLocalDocument {

	@Expose
	@SerializedName("region_type")
	private String regionType;

	@Expose
	@SerializedName("address_name")
	private String addressName;

	@Expose
	@SerializedName("region_1depth_name")
	private String region1DepthName;


	@Expose
	@SerializedName("region_2depth_name")
	private String region2DepthName;

	@Expose
	@SerializedName("region_3depth_name")
	private String region3DepthName;

	@Expose
	@SerializedName("region_4depth_name")
	private String region4DepthName;

	@Expose
	@SerializedName("code")
	private String regionCode;

	@Expose
	@SerializedName("x")
	private String longitude;

	@Expose
	@SerializedName("y")
	private String latitude;

	public String getRegionType() {
		return regionType;
	}

	public void setRegionType(String regionType) {
		this.regionType = regionType;
	}

	public String getAddressName() {
		return addressName;
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	public String getRegion1DepthName() {
		return region1DepthName;
	}

	public void setRegion1DepthName(String region1DepthName) {
		this.region1DepthName = region1DepthName;
	}

	public String getRegion2DepthName() {
		return region2DepthName;
	}

	public void setRegion2DepthName(String region2DepthName) {
		this.region2DepthName = region2DepthName;
	}

	public String getRegion3DepthName() {
		return region3DepthName;
	}

	public void setRegion3DepthName(String region3DepthName) {
		this.region3DepthName = region3DepthName;
	}

	public String getRegion4DepthName() {
		return region4DepthName;
	}

	public void setRegion4DepthName(String region4DepthName) {
		this.region4DepthName = region4DepthName;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
}
