package com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.midtaresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.commons.Header;

public class MidTaResponse implements Parcelable {
	@Expose
	@SerializedName("header")
	private Header header;

	@Expose
	@SerializedName("body")
	private MidTaBody body;


	protected MidTaResponse(Parcel in) {
		header = in.readParcelable(Header.class.getClassLoader());
		body = in.readParcelable(MidTaBody.class.getClassLoader());
	}

	public static final Creator<MidTaResponse> CREATOR = new Creator<MidTaResponse>() {
		@Override
		public MidTaResponse createFromParcel(Parcel in) {
			return new MidTaResponse(in);
		}

		@Override
		public MidTaResponse[] newArray(int size) {
			return new MidTaResponse[size];
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

	public MidTaBody getBody() {
		return body;
	}

	public void setBody(MidTaBody body) {
		this.body = body;
	}
}
