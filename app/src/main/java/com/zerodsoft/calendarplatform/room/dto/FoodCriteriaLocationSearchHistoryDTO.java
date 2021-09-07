package com.zerodsoft.calendarplatform.room.dto;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.zerodsoft.calendarplatform.etc.LocationType;

@Entity(tableName = "food_criteria_location_search_history_table")
public class FoodCriteriaLocationSearchHistoryDTO {
	@ColumnInfo(name = "id")
	@PrimaryKey(autoGenerate = true)
	private int id;

	@ColumnInfo(name = "event_id")
	private Long eventId;

	@ColumnInfo(name = "instance_id")
	private Long instanceId;

	@ColumnInfo(name = "place_name")
	private String placeName;

	@ColumnInfo(name = "address_name")
	private String addressName;

	@ColumnInfo(name = "latitude")
	private String latitude;

	@ColumnInfo(name = "longitude")
	private String longitude;

	@ColumnInfo(name = "location_type")
	private Integer locationType;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public String getAddressName() {
		return addressName;
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName;
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

	public void setLocationType(Integer locationType) {
		this.locationType = locationType;
	}

	public Integer getLocationType() {
		return locationType;
	}

	public void setAddress(String addressName, String latitude, String longitude) {
		this.addressName = addressName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.locationType = LocationType.ADDRESS;
	}

	public void setPlace(String placeName, String addressName, String latitude, String longitude) {
		this.addressName = addressName;
		this.placeName = placeName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.locationType = LocationType.PLACE;
	}
}
