package com.zerodsoft.scheduleweather.weather.dataprocessing;

import android.content.Context;

import com.zerodsoft.scheduleweather.R;

public class WeatherDataConverter {
	public static Context context;
	/*
- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
- 강수형태(PTY) 코드 : 없음(0), 비(1), 비/눈/진눈깨비(2), 눈(3), 소나기(4), 빗방울(5), 빗방울/눈날림(6), 눈날림(7)
SKY와 PTY는 별개의 데이터
	 */
	private static final String SUNNY = "맑음";
	private static final String CLOUD = "구름많음";
	private static final String CLOUD_RAIN = "구름많고 비";
	private static final String CLOUD_SNOW = "구름많고 눈";
	private static final String CLOUD_RAIN_SNOW = "구름많고 비/눈";
	private static final String CLOUD_SNOW_RAIN = "구름많고 눈/비";
	private static final String CLOUD_SHOWER = "구름많고 소나기";
	private static final String CLOUDY = "흐림";
	private static final String CLOUDY_RAIN = "흐리고 비";
	private static final String CLOUDY_SNOW = "흐리고 눈";
	private static final String CLOUDY_RAIN_SNOW = "흐리고 비/눈";
	private static final String CLOUDY_SNOW_RAIN = "흐리고 눈/비";
	private static final String CLOUDY_SHOWER = "흐리고 소나기";

	private static final String RAIN = "비";
	private static final String SLEET = "진눈깨비";
	private static final String SHOWER = "소나기";
	private static final String SNOW = "눈";

	private WeatherDataConverter() {
	}

	public static int getSkyDrawableId(String sky, String precipitationForm, boolean day) {
		int id = 0;

		if (sky.equals(SUNNY)) {
			if (day) {
				id = R.drawable.sunny_day_svg;
			} else {
				id = R.drawable.sunny_night_svg;
			}
		} else if (sky.equals(CLOUD)) {
			if (day) {
				id = R.drawable.cloud_day_svg;
			} else {
				id = R.drawable.cloud_night_svg;
			}
		} else if (sky.equals(CLOUDY)) {
			id = R.drawable.cloudy_svg;
		}

		if (precipitationForm.equals(RAIN)) {
			id = R.drawable.rain_svg;

		} else if (precipitationForm.equals(SLEET)) {
			id = R.drawable.sleet_svg;

		} else if (precipitationForm.equals(SNOW)) {
			id = R.drawable.snow_svg;

		} else if (precipitationForm.equals(SHOWER)) {
			id = R.drawable.shower_svg;
		}

		return id;
	}

	public static int getSkyDrawableId(String sky) {
		int id = 0;

		if (sky.equals(SUNNY)) {
			id = R.drawable.sunny_day_svg;
		} else if (sky.equals(CLOUD)) {
			id = R.drawable.cloud_day_svg;
		} else if (sky.equals(CLOUD_RAIN)) {
			id = R.drawable.rain_svg;
		} else if (sky.equals(CLOUD_SNOW)) {
			id = R.drawable.snow_svg;
		} else if (sky.equals(CLOUD_RAIN_SNOW)) {
			id = R.drawable.rain_svg;
		} else if (sky.equals(CLOUD_SNOW_RAIN)) {
			id = R.drawable.snow_svg;
		} else if (sky.equals(CLOUDY)) {
			id = R.drawable.cloudy_svg;
		} else if (sky.equals(CLOUDY_RAIN)) {
			id = R.drawable.rain_svg;
		} else if (sky.equals(CLOUDY_SNOW)) {
			id = R.drawable.snow_svg;
		} else if (sky.equals(CLOUDY_RAIN_SNOW)) {
			id = R.drawable.rain_svg;
		} else if (sky.equals(CLOUDY_SNOW_RAIN)) {
			id = R.drawable.snow_svg;
		} else if (sky.equals(CLOUD_SHOWER)) {
			id = R.drawable.shower_svg;
		} else if (sky.equals(CLOUDY_SHOWER)) {
			id = R.drawable.shower_svg;
		}

		return id;
	}

	public static String convertPrecipitationForm(String value) {
		String convertedValue = null;

		switch (value) {
			case "0":
				convertedValue = "";
				break;
			case "1":
				convertedValue = context.getString(R.string.rain);
				break;
			case "2":
				convertedValue = context.getString(R.string.sleet);
				break;
			case "3":
				convertedValue = context.getString(R.string.snow);
				break;
			case "4":
				convertedValue = context.getString(R.string.shower);
				break;
			case "5":
				convertedValue = context.getString(R.string.raindrop);
				break;
			case "6":
				convertedValue = context.getString(R.string.raindrop_snowdrifting);
				break;
			case "7":
				convertedValue = context.getString(R.string.snowdrifting);
				break;
		}
		return convertedValue;
	}

	public static String convertWindDirection(String value) {
		final int windDirectionValue = (int) ((Integer.valueOf(value).intValue() + 22.5 * 0.5) / 22.5);

		String convertedValue = null;

		switch (windDirectionValue) {
			case 0:
				convertedValue = context.getString(R.string.n);
				break;
			case 1:
				convertedValue = context.getString(R.string.nne);
				break;
			case 2:
				convertedValue = context.getString(R.string.ne);
				break;
			case 3:
				convertedValue = context.getString(R.string.ene);
				break;
			case 4:
				convertedValue = context.getString(R.string.e);
				break;
			case 5:
				convertedValue = context.getString(R.string.ese);
				break;
			case 6:
				convertedValue = context.getString(R.string.se);
				break;
			case 7:
				convertedValue = context.getString(R.string.sse);
				break;
			case 8:
				convertedValue = context.getString(R.string.s);
				break;
			case 9:
				convertedValue = context.getString(R.string.ssw);
				break;
			case 10:
				convertedValue = context.getString(R.string.sw);
				break;
			case 11:
				convertedValue = context.getString(R.string.wsw);
				break;
			case 12:
				convertedValue = context.getString(R.string.w);
				break;
			case 13:
				convertedValue = context.getString(R.string.wnw);
				break;
			case 14:
				convertedValue = context.getString(R.string.nw);
				break;
			case 15:
				convertedValue = context.getString(R.string.nnw);
				break;
			case 16:
				convertedValue = context.getString(R.string.n);
				break;
		}

		return convertedValue;
	}

	public static String convertSky(String value) {
		String convertedValue = null;

		switch (value) {
			case "1":
				convertedValue = SUNNY;
				break;
			case "3":
				convertedValue = CLOUD;
				break;
			case "4":
				convertedValue = CLOUDY;
				break;
		}
		return convertedValue;
	}

	public static String getWindSpeedDescription(String windSpeed) {
		double speed = Double.valueOf(windSpeed);
		if (speed >= 14) {
			return "매우 강한 바람";
		} else if (speed >= 9) {
			return "강한 바람";
		} else if (speed >= 4) {
			return "약간 강한 바람";
		} else {
			return "약한 바람";
		}
	}

	public static String getSimpleWindSpeedDescription(String windSpeed) {
		double speed = Double.valueOf(windSpeed);
		if (speed >= 14) {
			return "매우 강";
		} else if (speed >= 9) {
			return "강";
		} else if (speed >= 4) {
			return "약간 강";
		} else {
			return "약";
		}
	}
}