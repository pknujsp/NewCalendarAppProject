package com.zerodsoft.scheduleweather.retrofit.queryresponse.commons;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Header implements Parcelable {
	@Expose
	@SerializedName("resultCode")
	private String resultCode;

	@Expose
	@SerializedName("resultMsg")
	private String resultMsg;

	protected Header(Parcel in) {
		resultCode = in.readString();
		resultMsg = in.readString();
	}

	public static final Creator<Header> CREATOR = new Creator<Header>() {
		@Override
		public Header createFromParcel(Parcel in) {
			return new Header(in);
		}

		@Override
		public Header[] newArray(int size) {
			return new Header[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(resultCode);
		dest.writeString(resultMsg);
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}
}
