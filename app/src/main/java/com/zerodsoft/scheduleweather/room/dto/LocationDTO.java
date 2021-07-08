package com.zerodsoft.scheduleweather.room.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.zerodsoft.scheduleweather.etc.LocationType;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "location_table")
public class LocationDTO implements Parcelable, Cloneable, Serializable {

	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	private int id;

	@ColumnInfo(name = "event_id")
	private long eventId;

	@ColumnInfo(name = "latitude")
	private String latitude;

	@ColumnInfo(name = "longitude")
	private String longitude;

	@ColumnInfo(name = "address_name")
	private String addressName;

	@ColumnInfo(name = "road_address_name")
	private String roadAddressName;

	@ColumnInfo(name = "place_id")
	private String placeId;

	@ColumnInfo(name = "place_name")
	private String placeName;

	@ColumnInfo(name = "location_type")
	private Integer locationType;

	@Ignore
	public LocationDTO() {
	}

	public LocationDTO(int id, long eventId, String latitude, String longitude, String addressName, String roadAddressName, String placeId, String placeName, int locationType) {
		this.id = id;
		this.eventId = eventId;
		this.latitude = latitude;
		this.longitude = longitude;
		this.addressName = addressName;
		this.roadAddressName = roadAddressName;
		this.placeId = placeId;
		this.placeName = placeName;
		this.locationType = locationType;
	}

	protected LocationDTO(Parcel in) {
		id = in.readInt();
		eventId = in.readLong();
		latitude = in.readString();
		longitude = in.readString();
		addressName = in.readString();
		roadAddressName = in.readString();
		placeId = in.readString();
		placeName = in.readString();
		locationType = in.readInt();
	}

	public static final Creator<LocationDTO> CREATOR = new Creator<LocationDTO>() {
		@Override
		public LocationDTO createFromParcel(Parcel in) {
			return new LocationDTO(in);
		}

		@Override
		public LocationDTO[] newArray(int size) {
			return new LocationDTO[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(id);
		parcel.writeLong(eventId);
		parcel.writeString(latitude);
		parcel.writeString(longitude);
		parcel.writeString(addressName);
		parcel.writeString(roadAddressName);
		parcel.writeString(placeId);
		parcel.writeString(placeName);
		parcel.writeInt(locationType);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		LocationDTO that = (LocationDTO) o;
		return id == that.id &&
				eventId == that.eventId &&
				Objects.equals(latitude, that.latitude) &&
				Objects.equals(longitude, that.longitude) &&
				Objects.equals(addressName, that.addressName) &&
				Objects.equals(roadAddressName, that.roadAddressName) &&
				Objects.equals(placeId, that.placeId) &&
				Objects.equals(placeName, that.placeName) &&
				Objects.equals(locationType, that.locationType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, eventId, latitude, longitude, addressName, roadAddressName, placeId, placeName, locationType);
	}

	@NonNull
	@Override
	public LocationDTO clone() throws CloneNotSupportedException {
		return (LocationDTO) super.clone();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
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

	public String getAddressName() {
		return addressName;
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	public String getRoadAddressName() {
		return roadAddressName;
	}

	public void setRoadAddressName(String roadAddressName) {
		this.roadAddressName = roadAddressName;
	}

	public String getPlaceId() {
		return placeId;
	}

	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public void setLocationType(Integer locationType) {
		this.locationType = locationType;
	}

	public Integer getLocationType() {
		return locationType;
	}

	public String getLocTitleName() {
		return locationType == LocationType.PLACE ?
				placeName : addressName;
	}

	public boolean isEmpty() {
		return latitude == null;
	}

	public void setPlace(String placeId, String placeName, String addressName, String roadAddressName, String latitude, String longitude) {
		this.placeId = placeId;
		this.placeName = placeName;
		this.addressName = addressName;
		this.roadAddressName = roadAddressName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.locationType = LocationType.PLACE;
	}

	public void setAddress(String addressName, String roadAddressName, String latitude, String longitude) {
		this.addressName = addressName;
		this.roadAddressName = roadAddressName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.locationType = LocationType.ADDRESS;
	}
}
