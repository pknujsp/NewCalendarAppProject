package com.zerodsoft.calendarplatform.retrofit.queryresponse.aircondition.MsrstnList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MsrstnListItem {
	@Expose
	@SerializedName("stationName")
	private String stationName;

	@Expose
	@SerializedName("addr")
	private String addr;

	@Expose
	@SerializedName("year")
	private String year;

	@Expose
	@SerializedName("mangName")
	private String mangName;

	@Expose
	@SerializedName("item")
	private String item;

	@Expose
	@SerializedName("dmX")
	private String dmX;

	@Expose
	@SerializedName("dmY")
	private String dmY;

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMangName() {
		return mangName;
	}

	public void setMangName(String mangName) {
		this.mangName = mangName;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getDmX() {
		return dmX;
	}

	public void setDmX(String dmX) {
		this.dmX = dmX;
	}

	public String getDmY() {
		return dmY;
	}

	public void setDmY(String dmY) {
		this.dmY = dmY;
	}
}
