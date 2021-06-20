package com.zerodsoft.scheduleweather.weather.aircondition.airconditionbar;

public class BarInitData {
	//색상, 기준, 상태명(보통,좋음,나쁨)
	private int color;
	private float referenceValueBegin;
	private String statusName;

	public BarInitData(int color, float referenceValueBegin, String statusName) {
		this.color = color;
		this.referenceValueBegin = referenceValueBegin;
		this.statusName = statusName;
	}

	public int getColor() {
		return color;
	}

	public float getReferenceValueBegin() {
		return referenceValueBegin;
	}

	public String getStatusName() {
		return statusName;
	}
}
