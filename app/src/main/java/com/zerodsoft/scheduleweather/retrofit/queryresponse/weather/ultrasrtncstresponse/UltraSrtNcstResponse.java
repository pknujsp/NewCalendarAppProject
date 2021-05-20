package com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtncstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.commons.Header;

public class UltraSrtNcstResponse implements Parcelable {
	@Expose
	@SerializedName("header")
	private Header header;

	@Expose
	@SerializedName("body")
	private UltraSrtNcstBody body;


	protected UltraSrtNcstResponse(Parcel in) {
		header = in.readParcelable(Header.class.getClassLoader());
		body = in.readParcelable(UltraSrtNcstBody.class.getClassLoader());
	}

	public static final Creator<UltraSrtNcstResponse> CREATOR = new Creator<UltraSrtNcstResponse>() {
		@Override
		public UltraSrtNcstResponse createFromParcel(Parcel in) {
			return new UltraSrtNcstResponse(in);
		}

		@Override
		public UltraSrtNcstResponse[] newArray(int size) {
			return new UltraSrtNcstResponse[size];
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

	public UltraSrtNcstBody getBody() {
		return body;
	}

	public void setBody(UltraSrtNcstBody body) {
		this.body = body;
	}
}
