package com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.midlandfcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.commons.Header;

public class MidLandFcstResponse implements Parcelable {
	@Expose
	@SerializedName("header")
	private Header header;

	@Expose
	@SerializedName("body")
	private MidLandFcstBody body;


	protected MidLandFcstResponse(Parcel in) {
		header = in.readParcelable(Header.class.getClassLoader());
		body = in.readParcelable(MidLandFcstBody.class.getClassLoader());
	}

	public static final Creator<MidLandFcstResponse> CREATOR = new Creator<MidLandFcstResponse>() {
		@Override
		public MidLandFcstResponse createFromParcel(Parcel in) {
			return new MidLandFcstResponse(in);
		}

		@Override
		public MidLandFcstResponse[] newArray(int size) {
			return new MidLandFcstResponse[size];
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

	public MidLandFcstBody getBody() {
		return body;
	}

	public void setBody(MidLandFcstBody body) {
		this.body = body;
	}
}
