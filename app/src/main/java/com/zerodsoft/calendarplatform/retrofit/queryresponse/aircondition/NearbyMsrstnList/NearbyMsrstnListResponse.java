package com.zerodsoft.calendarplatform.retrofit.queryresponse.aircondition.NearbyMsrstnList;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.commons.Header;

public class NearbyMsrstnListResponse implements Parcelable {
	@Expose
	@SerializedName("header")
	private Header header;

	@Expose
	@SerializedName("body")
	private NearbyMsrstnListBody body;


	protected NearbyMsrstnListResponse(Parcel in) {
		header = in.readParcelable(Header.class.getClassLoader());
		body = in.readParcelable(NearbyMsrstnListBody.class.getClassLoader());
	}

	public static final Creator<NearbyMsrstnListResponse> CREATOR = new Creator<NearbyMsrstnListResponse>() {
		@Override
		public NearbyMsrstnListResponse createFromParcel(Parcel in) {
			return new NearbyMsrstnListResponse(in);
		}

		@Override
		public NearbyMsrstnListResponse[] newArray(int size) {
			return new NearbyMsrstnListResponse[size];
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

	public NearbyMsrstnListBody getBody() {
		return body;
	}

	public void setBody(NearbyMsrstnListBody body) {
		this.body = body;
	}
}
