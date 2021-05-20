package com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.CtprvnRltmMesureDnsty;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.commons.Header;

public class CtprvnRltmMesureDnstyResponse implements Parcelable {
	@Expose
	@SerializedName("header")
	private Header header;

	@Expose
	@SerializedName("body")
	private CtprvnRltmMesureDnstyBody body;

	protected CtprvnRltmMesureDnstyResponse(Parcel in) {
		header = in.readParcelable(Header.class.getClassLoader());
		body = in.readParcelable(CtprvnRltmMesureDnstyBody.class.getClassLoader());
	}

	public static final Creator<CtprvnRltmMesureDnstyResponse> CREATOR = new Creator<CtprvnRltmMesureDnstyResponse>() {
		@Override
		public CtprvnRltmMesureDnstyResponse createFromParcel(Parcel in) {
			return new CtprvnRltmMesureDnstyResponse(in);
		}

		@Override
		public CtprvnRltmMesureDnstyResponse[] newArray(int size) {
			return new CtprvnRltmMesureDnstyResponse[size];
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

	public CtprvnRltmMesureDnstyBody getBody() {
		return body;
	}

	public void setBody(CtprvnRltmMesureDnstyBody body) {
		this.body = body;
	}
}
