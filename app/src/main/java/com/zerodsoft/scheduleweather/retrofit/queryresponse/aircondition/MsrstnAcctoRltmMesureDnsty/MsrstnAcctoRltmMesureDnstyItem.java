package com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MsrstnAcctoRltmMesureDnstyItem implements Parcelable, Serializable {
	@Expose
	@SerializedName("dataTime")
	private String dataTime;

	@Expose
	@SerializedName("mangName")
	private String mangName;


	@Expose
	@SerializedName("so2Value")
	private String so2Value;


	@Expose
	@SerializedName("coValue")
	private String coValue;


	@Expose
	@SerializedName("o3Value")
	private String o3Value;


	@Expose
	@SerializedName("no2Value")
	private String no2Value;


	@Expose
	@SerializedName("pm10Value")
	private String pm10Value;


	@Expose
	@SerializedName("pm10Value24")
	private String pm10Value24;


	@Expose
	@SerializedName("pm25Value")
	private String pm25Value;


	@Expose
	@SerializedName("pm25Value24")
	private String pm25Value24;


	@Expose
	@SerializedName("khaiValue")
	private String khaiValue;


	@Expose
	@SerializedName("khaiGrade")
	private String khaiGrade;


	@Expose
	@SerializedName("so2Grade")
	private String so2Grade;


	@Expose
	@SerializedName("coGrade")
	private String coGrade;


	@Expose
	@SerializedName("o3Grade")
	private String o3Grade;


	@Expose
	@SerializedName("no2Grade")
	private String no2Grade;


	@Expose
	@SerializedName("pm10Grade")
	private String pm10Grade;


	@Expose
	@SerializedName("pm25Grade")
	private String pm25Grade;


	@Expose
	@SerializedName("pm10Grade1h")
	private String pm10Grade1h;


	@Expose
	@SerializedName("pm25Grade1h")
	private String pm25Grade1h;


	@Expose
	@SerializedName("so2Flag")
	private String so2Flag;


	@Expose
	@SerializedName("coFlag")
	private String coFlag;


	@Expose
	@SerializedName("o3Flag")
	private String o3Flag;


	@Expose
	@SerializedName("no2Flag")
	private String no2Flag;


	@Expose
	@SerializedName("pm10Flag")
	private String pm10Flag;


	@Expose
	@SerializedName("pm25Flag")
	private String pm25Flag;


	protected MsrstnAcctoRltmMesureDnstyItem(Parcel in) {
		dataTime = in.readString();
		mangName = in.readString();
		so2Value = in.readString();
		coValue = in.readString();
		o3Value = in.readString();
		no2Value = in.readString();
		pm10Value = in.readString();
		pm10Value24 = in.readString();
		pm25Value = in.readString();
		pm25Value24 = in.readString();
		khaiValue = in.readString();
		khaiGrade = in.readString();
		so2Grade = in.readString();
		coGrade = in.readString();
		o3Grade = in.readString();
		no2Grade = in.readString();
		pm10Grade = in.readString();
		pm25Grade = in.readString();
		pm10Grade1h = in.readString();
		pm25Grade1h = in.readString();
		so2Flag = in.readString();
		coFlag = in.readString();
		o3Flag = in.readString();
		no2Flag = in.readString();
		pm10Flag = in.readString();
		pm25Flag = in.readString();
	}

	public static final Creator<MsrstnAcctoRltmMesureDnstyItem> CREATOR = new Creator<MsrstnAcctoRltmMesureDnstyItem>() {
		@Override
		public MsrstnAcctoRltmMesureDnstyItem createFromParcel(Parcel in) {
			return new MsrstnAcctoRltmMesureDnstyItem(in);
		}

		@Override
		public MsrstnAcctoRltmMesureDnstyItem[] newArray(int size) {
			return new MsrstnAcctoRltmMesureDnstyItem[size];
		}
	};

	public MsrstnAcctoRltmMesureDnstyItem() {

	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(dataTime);
		parcel.writeString(mangName);
		parcel.writeString(so2Value);
		parcel.writeString(coValue);
		parcel.writeString(o3Value);
		parcel.writeString(no2Value);
		parcel.writeString(pm10Value);
		parcel.writeString(pm10Value24);
		parcel.writeString(pm25Value);
		parcel.writeString(pm25Value24);
		parcel.writeString(khaiValue);
		parcel.writeString(khaiGrade);
		parcel.writeString(so2Grade);
		parcel.writeString(coGrade);
		parcel.writeString(o3Grade);
		parcel.writeString(no2Grade);
		parcel.writeString(pm10Grade);
		parcel.writeString(pm25Grade);
		parcel.writeString(pm10Grade1h);
		parcel.writeString(pm25Grade1h);
		parcel.writeString(so2Flag);
		parcel.writeString(coFlag);
		parcel.writeString(o3Flag);
		parcel.writeString(no2Flag);
		parcel.writeString(pm10Flag);
		parcel.writeString(pm25Flag);
	}

	public String getDataTime() {
		return dataTime;
	}

	public void setDataTime(String dataTime) {
		this.dataTime = dataTime;
	}

	public String getMangName() {
		return mangName;
	}

	public void setMangName(String mangName) {
		this.mangName = mangName;
	}

	public String getSo2Value() {
		return so2Value;
	}

	public void setSo2Value(String so2Value) {
		this.so2Value = so2Value;
	}

	public String getCoValue() {
		return coValue;
	}

	public void setCoValue(String coValue) {
		this.coValue = coValue;
	}

	public String getO3Value() {
		return o3Value;
	}

	public void setO3Value(String o3Value) {
		this.o3Value = o3Value;
	}

	public String getNo2Value() {
		return no2Value;
	}

	public void setNo2Value(String no2Value) {
		this.no2Value = no2Value;
	}

	public String getPm10Value() {
		return pm10Value;
	}

	public void setPm10Value(String pm10Value) {
		this.pm10Value = pm10Value;
	}

	public String getPm10Value24() {
		return pm10Value24;
	}

	public void setPm10Value24(String pm10Value24) {
		this.pm10Value24 = pm10Value24;
	}

	public String getPm25Value() {
		return pm25Value;
	}

	public void setPm25Value(String pm25Value) {
		this.pm25Value = pm25Value;
	}

	public String getPm25Value24() {
		return pm25Value24;
	}

	public void setPm25Value24(String pm25Value24) {
		this.pm25Value24 = pm25Value24;
	}

	public String getKhaiValue() {
		return khaiValue;
	}

	public void setKhaiValue(String khaiValue) {
		this.khaiValue = khaiValue;
	}

	public String getKhaiGrade() {
		return khaiGrade;
	}

	public void setKhaiGrade(String khaiGrade) {
		this.khaiGrade = khaiGrade;
	}

	public String getSo2Grade() {
		return so2Grade;
	}

	public void setSo2Grade(String so2Grade) {
		this.so2Grade = so2Grade;
	}

	public String getCoGrade() {
		return coGrade;
	}

	public void setCoGrade(String coGrade) {
		this.coGrade = coGrade;
	}

	public String getO3Grade() {
		return o3Grade;
	}

	public void setO3Grade(String o3Grade) {
		this.o3Grade = o3Grade;
	}

	public String getNo2Grade() {
		return no2Grade;
	}

	public void setNo2Grade(String no2Grade) {
		this.no2Grade = no2Grade;
	}

	public String getPm10Grade() {
		return pm10Grade;
	}

	public void setPm10Grade(String pm10Grade) {
		this.pm10Grade = pm10Grade;
	}

	public String getPm25Grade() {
		return pm25Grade;
	}

	public void setPm25Grade(String pm25Grade) {
		this.pm25Grade = pm25Grade;
	}

	public String getPm10Grade1h() {
		return pm10Grade1h;
	}

	public void setPm10Grade1h(String pm10Grade1h) {
		this.pm10Grade1h = pm10Grade1h;
	}

	public String getPm25Grade1h() {
		return pm25Grade1h;
	}

	public void setPm25Grade1h(String pm25Grade1h) {
		this.pm25Grade1h = pm25Grade1h;
	}

	public String getSo2Flag() {
		return so2Flag;
	}

	public void setSo2Flag(String so2Flag) {
		this.so2Flag = so2Flag;
	}

	public String getCoFlag() {
		return coFlag;
	}

	public void setCoFlag(String coFlag) {
		this.coFlag = coFlag;
	}

	public String getO3Flag() {
		return o3Flag;
	}

	public void setO3Flag(String o3Flag) {
		this.o3Flag = o3Flag;
	}

	public String getNo2Flag() {
		return no2Flag;
	}

	public void setNo2Flag(String no2Flag) {
		this.no2Flag = no2Flag;
	}

	public String getPm10Flag() {
		return pm10Flag;
	}

	public void setPm10Flag(String pm10Flag) {
		this.pm10Flag = pm10Flag;
	}

	public String getPm25Flag() {
		return pm25Flag;
	}

	public void setPm25Flag(String pm25Flag) {
		this.pm25Flag = pm25Flag;
	}
}
