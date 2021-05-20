package com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.commons.Header;

public class MsrstnAcctoRltmMesureDnstyResponse implements Parcelable {
	@Expose
	@SerializedName("header")
	private Header header;

	@Expose
	@SerializedName("body")
	private MsrstnAcctoRltmMesureDnstyBody body;

	protected MsrstnAcctoRltmMesureDnstyResponse(Parcel in) {
		header = in.readParcelable(Header.class.getClassLoader());
		body = in.readParcelable(MsrstnAcctoRltmMesureDnstyBody.class.getClassLoader());
	}

	public static final Creator<MsrstnAcctoRltmMesureDnstyResponse> CREATOR = new Creator<MsrstnAcctoRltmMesureDnstyResponse>() {
		@Override
		public MsrstnAcctoRltmMesureDnstyResponse createFromParcel(Parcel in) {
			return new MsrstnAcctoRltmMesureDnstyResponse(in);
		}

		@Override
		public MsrstnAcctoRltmMesureDnstyResponse[] newArray(int size) {
			return new MsrstnAcctoRltmMesureDnstyResponse[size];
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

	public MsrstnAcctoRltmMesureDnstyBody getBody() {
		return body;
	}

	public void setBody(MsrstnAcctoRltmMesureDnstyBody body) {
		this.body = body;
	}
}
