package com.zerodsoft.calendarplatform.retrofit.queryresponse.map.addressresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.KakaoLocalDocument;

public class AddressResponseDocuments extends KakaoLocalDocument implements Parcelable {
	@SerializedName("address_name")
	@Expose
	private String addressName;

	@SerializedName("address_type")
	@Expose
	private String addressType;

	@SerializedName("x")
	@Expose
	private String x;

	@SerializedName("y")
	@Expose
	private String y;

	@SerializedName("address")
	@Expose
	private AddressResponseAddress addressResponseAddress;

	@SerializedName("road_address")
	@Expose
	private AddressResponseRoadAddress addressResponseRoadAddress;

	public static final String REGION = "REGION";
	public static final String ROAD = "ROAD";
	public static final String REGION_ADDR = "REGION_ADDR";
	public static final String ROAD_ADDR = "ROAD_ADDR";

	public AddressResponseDocuments() {
	}

	protected AddressResponseDocuments(Parcel in) {
		addressName = in.readString();
		addressType = in.readString();
		x = in.readString();
		y = in.readString();
		addressResponseAddress = in.readParcelable(AddressResponseAddress.class.getClassLoader());
		addressResponseRoadAddress = in.readParcelable(AddressResponseRoadAddress.class.getClassLoader());
	}

	public static final Creator<AddressResponseDocuments> CREATOR = new Creator<AddressResponseDocuments>() {
		@Override
		public AddressResponseDocuments createFromParcel(Parcel in) {
			return new AddressResponseDocuments(in);
		}

		@Override
		public AddressResponseDocuments[] newArray(int size) {
			return new AddressResponseDocuments[size];
		}
	};

	public String getAddressName() {
		return addressName;
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public AddressResponseAddress getAddressResponseAddress() {
		return addressResponseAddress;
	}

	public void setAddressResponseAddress(AddressResponseAddress addressResponseAddress) {
		this.addressResponseAddress = addressResponseAddress;
	}

	public AddressResponseRoadAddress getAddressResponseRoadAddress() {
		return addressResponseRoadAddress;
	}

	public void setAddressResponseRoadAddress(AddressResponseRoadAddress addressResponseRoadAddress) {
		this.addressResponseRoadAddress = addressResponseRoadAddress;
	}

	public String getAddressTypeStr() {
		if (addressType.equals(ROAD) || addressType.equals(ROAD_ADDR)) {
			return "도로명";
		} else {
			return "지번";
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(this.addressName);
		parcel.writeString(this.addressType);
		parcel.writeString(this.x);
		parcel.writeString(this.y);
		parcel.writeParcelable(this.addressResponseAddress, i);
		parcel.writeParcelable(this.addressResponseRoadAddress, i);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
