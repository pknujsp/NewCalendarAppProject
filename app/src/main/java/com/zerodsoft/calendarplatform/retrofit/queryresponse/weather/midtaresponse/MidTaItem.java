package com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.midtaresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MidTaItem implements Parcelable, Cloneable {
	@Expose
	@SerializedName("regId")
	private String regId;


	@Expose
	@SerializedName("taMax3")
	private String taMax3;


	@Expose
	@SerializedName("taMax4")
	private String taMax4;


	@Expose
	@SerializedName("taMax5")
	private String taMax5;


	@Expose
	@SerializedName("taMax6")
	private String taMax6;


	@Expose
	@SerializedName("taMax7")
	private String taMax7;


	@Expose
	@SerializedName("taMax8")
	private String taMax8;


	@Expose
	@SerializedName("taMax9")
	private String taMax9;

	@Expose
	@SerializedName("taMax10")
	private String taMax10;


	@Expose
	@SerializedName("taMin3")
	private String taMin3;


	@Expose
	@SerializedName("taMin4")
	private String taMin4;


	@Expose
	@SerializedName("taMin5")
	private String taMin5;


	@Expose
	@SerializedName("taMin6")
	private String taMin6;


	@Expose
	@SerializedName("taMin7")
	private String taMin7;


	@Expose
	@SerializedName("taMin8")
	private String taMin8;


	@Expose
	@SerializedName("taMin9")
	private String taMin9;

	@Expose
	@SerializedName("taMin10")
	private String taMin10;


	protected MidTaItem(Parcel in) {
		regId = in.readString();
		taMax3 = in.readString();
		taMax4 = in.readString();
		taMax5 = in.readString();
		taMax6 = in.readString();
		taMax7 = in.readString();
		taMax8 = in.readString();
		taMax9 = in.readString();
		taMax10 = in.readString();
		taMin3 = in.readString();
		taMin4 = in.readString();
		taMin5 = in.readString();
		taMin6 = in.readString();
		taMin7 = in.readString();
		taMin8 = in.readString();
		taMin9 = in.readString();
		taMin10 = in.readString();
	}

	public static final Creator<MidTaItem> CREATOR = new Creator<MidTaItem>() {
		@Override
		public MidTaItem createFromParcel(Parcel in) {
			return new MidTaItem(in);
		}

		@Override
		public MidTaItem[] newArray(int size) {
			return new MidTaItem[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(regId);
		parcel.writeString(taMax3);
		parcel.writeString(taMax4);
		parcel.writeString(taMax5);
		parcel.writeString(taMax6);
		parcel.writeString(taMax7);
		parcel.writeString(taMax8);
		parcel.writeString(taMax9);
		parcel.writeString(taMax10);
		parcel.writeString(taMin3);
		parcel.writeString(taMin4);
		parcel.writeString(taMin5);
		parcel.writeString(taMin6);
		parcel.writeString(taMin7);
		parcel.writeString(taMin8);
		parcel.writeString(taMin9);
		parcel.writeString(taMin10);
	}

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public String getTaMax3() {
		return taMax3;
	}

	public void setTaMax3(String taMax3) {
		this.taMax3 = taMax3;
	}

	public String getTaMax4() {
		return taMax4;
	}

	public void setTaMax4(String taMax4) {
		this.taMax4 = taMax4;
	}

	public String getTaMax5() {
		return taMax5;
	}

	public void setTaMax5(String taMax5) {
		this.taMax5 = taMax5;
	}

	public String getTaMax6() {
		return taMax6;
	}

	public void setTaMax6(String taMax6) {
		this.taMax6 = taMax6;
	}

	public String getTaMax7() {
		return taMax7;
	}

	public void setTaMax7(String taMax7) {
		this.taMax7 = taMax7;
	}

	public String getTaMax8() {
		return taMax8;
	}

	public void setTaMax8(String taMax8) {
		this.taMax8 = taMax8;
	}

	public String getTaMax9() {
		return taMax9;
	}

	public void setTaMax9(String taMax9) {
		this.taMax9 = taMax9;
	}

	public String getTaMax10() {
		return taMax10;
	}

	public void setTaMax10(String taMax10) {
		this.taMax10 = taMax10;
	}

	public String getTaMin3() {
		return taMin3;
	}

	public void setTaMin3(String taMin3) {
		this.taMin3 = taMin3;
	}

	public String getTaMin4() {
		return taMin4;
	}

	public void setTaMin4(String taMin4) {
		this.taMin4 = taMin4;
	}

	public String getTaMin5() {
		return taMin5;
	}

	public void setTaMin5(String taMin5) {
		this.taMin5 = taMin5;
	}

	public String getTaMin6() {
		return taMin6;
	}

	public void setTaMin6(String taMin6) {
		this.taMin6 = taMin6;
	}

	public String getTaMin7() {
		return taMin7;
	}

	public void setTaMin7(String taMin7) {
		this.taMin7 = taMin7;
	}

	public String getTaMin8() {
		return taMin8;
	}

	public void setTaMin8(String taMin8) {
		this.taMin8 = taMin8;
	}

	public String getTaMin9() {
		return taMin9;
	}

	public void setTaMin9(String taMin9) {
		this.taMin9 = taMin9;
	}

	public String getTaMin10() {
		return taMin10;
	}

	public void setTaMin10(String taMin10) {
		this.taMin10 = taMin10;
	}

	public String[] getMinArr() {
		return new String[]{taMin3, taMin4, taMin5, taMin6, taMin7, taMin8, taMin9, taMin10};
	}

	public String[] getMaxArr() {
		return new String[]{taMax3, taMax4, taMax5, taMax6, taMax7, taMax8, taMax9, taMax10};
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
