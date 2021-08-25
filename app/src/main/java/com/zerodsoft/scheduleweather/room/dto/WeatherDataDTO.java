package com.zerodsoft.scheduleweather.room.dto;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "weather_data_table")
public class WeatherDataDTO {
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	private Integer id;

	@ColumnInfo(name = "latitude")
	private String latitude;

	@ColumnInfo(name = "longitude")
	private String longitude;

	@ColumnInfo(name = "data_type")
	private Integer dataType;

	@ColumnInfo(name = "json")
	private String json;

	@ColumnInfo(name = "downloaded_date")
	private String downloadedDate;

	@ColumnInfo(name = "base_date_time")
	private String baseDateTime;

	@Ignore
	public static final int ULTRA_SRT_NCST = 0;
	@Ignore
	public static final int ULTRA_SRT_FCST = 1;
	@Ignore
	public static final int VILAGE_FCST = 2;
	@Ignore
	public static final int MID_LAND_FCST = 3;
	@Ignore
	public static final int MID_TA = 4;
	@Ignore
	public static final int AIR_CONDITION = 5;
	@Ignore
	public static final int NEAR_BY_MSRSTN_LIST = 6;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}

	public Integer getDataType() {
		return dataType;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public void setDownloadedDate(String downloadedDate) {
		this.downloadedDate = downloadedDate;
	}

	public String getDownloadedDate() {
		return downloadedDate;
	}

	public void setBaseDateTime(String baseDateTime) {
		this.baseDateTime = baseDateTime;
	}

	public String getBaseDateTime() {
		return baseDateTime;
	}
}
