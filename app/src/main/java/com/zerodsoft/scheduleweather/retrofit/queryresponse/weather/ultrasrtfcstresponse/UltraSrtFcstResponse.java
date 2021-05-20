package com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtfcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.commons.Header;

public class UltraSrtFcstResponse implements Parcelable {
	@Expose
	@SerializedName("header")
	private Header header;

	@Expose
	@SerializedName("body")
	private UltraSrtFcstBody body;


	protected UltraSrtFcstResponse(Parcel in) {
		header = in.readParcelable(Header.class.getClassLoader());
		body = in.readParcelable(UltraSrtFcstBody.class.getClassLoader());
	}

	public static final Creator<UltraSrtFcstResponse> CREATOR = new Creator<UltraSrtFcstResponse>() {
		@Override
		public UltraSrtFcstResponse createFromParcel(Parcel in) {
			return new UltraSrtFcstResponse(in);
		}

		@Override
		public UltraSrtFcstResponse[] newArray(int size) {
			return new UltraSrtFcstResponse[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(header, flags);
		dest.writeParcelable(body, flags);
	}

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public UltraSrtFcstBody getBody() {
		return body;
	}

	public void setBody(UltraSrtFcstBody body) {
		this.body = body;
	}
}
