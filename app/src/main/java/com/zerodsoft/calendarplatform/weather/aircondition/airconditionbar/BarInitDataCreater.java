package com.zerodsoft.calendarplatform.weather.aircondition.airconditionbar;

import android.content.Context;

import com.zerodsoft.calendarplatform.R;

import java.util.ArrayList;
import java.util.List;

public class BarInitDataCreater {
	public static final int FINEDUST = 0;
	public static final int ULTRA_FINEDUST = 1;
	public static final int SO2 = 2;
	public static final int CO = 3;
	public static final int O3 = 4;
	public static final int NO2 = 5;

	private BarInitDataCreater() {
	}

	public static List<BarInitData> getBarInitData(Context context, int type) {
		String[] references = null;

		switch (type) {
			case FINEDUST:
				references = context.getResources().getStringArray(R.array.finddust_reference);
				break;
			case ULTRA_FINEDUST:
				references = context.getResources().getStringArray(R.array.ultrafinddust_reference);
				break;
			case SO2:
				references = context.getResources().getStringArray(R.array.so2_reference);
				break;
			case CO:
				references = context.getResources().getStringArray(R.array.co_reference);
				break;
			case O3:
				references = context.getResources().getStringArray(R.array.o3_reference);
				break;
			case NO2:
				references = context.getResources().getStringArray(R.array.no2_reference);
				break;
		}

		BarInitData good = new BarInitData(context.getColor(R.color.air_condition_good), Float.parseFloat(references[0]),
				context.getString(R.string.good));
		BarInitData normal = new BarInitData(context.getColor(R.color.air_condition_normal), Float.parseFloat(references[1]), context.getString(R.string.normal));
		BarInitData bad = new BarInitData(context.getColor(R.color.air_condition_bad_begin), Float.parseFloat(references[2]), context.getString(R.string.bad));
		BarInitData veryBad = new BarInitData(context.getColor(R.color.air_condition_very_bad_end), Float.parseFloat(references[3]), context.getString(R.string.very_bad));

		List<BarInitData> barInitDataList = new ArrayList<>();

		barInitDataList.add(good);
		barInitDataList.add(normal);
		barInitDataList.add(bad);
		barInitDataList.add(veryBad);

		return barInitDataList;
	}


	public static String getGrade(String grade, Context context) {
		if (grade.equals("1")) {
			return context.getString(R.string.good);
		} else if (grade.equals("2")) {
			return context.getString(R.string.normal);
		} else if (grade.equals("3")) {
			return context.getString(R.string.bad);
		} else {
			return context.getString(R.string.very_bad);
		}
	}

	public static int getGradeColor(String grade, Context context) {
		if (grade.equals("1")) {
			return context.getColor(R.color.air_condition_good);
		} else if (grade.equals("2")) {
			return context.getColor(R.color.air_condition_normal);
		} else if (grade.equals("3")) {
			return context.getColor(R.color.air_condition_bad_begin);
		} else {
			return context.getColor(R.color.air_condition_very_bad_end);
		}
	}

}
