package com.zerodsoft.scheduleweather.weather.dataprocessing;

import android.content.Context;

import androidx.annotation.Nullable;

import com.zerodsoft.scheduleweather.R;

public class WeatherDataConverter {
	public static Context context;
	/*
- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
- 강수형태(PTY) 코드 : (초단기) 없음(0), 비(1), 비/눈(2), 눈(3), 빗방울(5), 빗방울눈날림(6), 눈날림(7)
                      (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
SKY와 PTY는 별개의 데이터
	 */
	public static final String SUNNY_FOR_MID = "맑음";
	public static final String CLOUDY_FOR_MID = "구름많음";
	public static final String CLOUDY_AND_RAIN_FOR_MID = "구름많고 비";
	public static final String CLOUDY_AND_SNOW_FOR_MID = "구름많고 눈";
	public static final String CLOUDY_AND_RAIN_AND_SNOW_FOR_MID = "구름많고 비/눈";
	public static final String CLOUDY_AND_SNOW_AND_RAIN_FOR_MID = "구름많고 눈/비";
	public static final String CLOUDY_AND_SHOWER_FOR_MID = "구름많고 소나기";
	public static final String OVERCAST_FOR_MID = "흐림";
	public static final String OVERCAST_AND_RAIN_FOR_MID = "흐리고 비";
	public static final String OVERCAST_AND_SNOW_FOR_MID = "흐리고 눈";
	public static final String OVERCAST_AND_RAIN_AND_SNOW_FOR_MID = "흐리고 비/눈";
	public static final String OVERCAST_AND_SNOW_AND_RAIN_FOR_MID = "흐리고 눈/비";
	public static final String OVERCAST_AND_SHOWER_FOR_MID = "흐리고 소나기";

	public static final String RAINDROP = "빗방울";

	public static final String PTY_NOT = "0";
	public static final String PTY_RAIN = "1";
	public static final String PTY_RAIN_AND_SNOW = "2";
	public static final String PTY_SNOW = "3";
	public static final String PTY_SHOWER = "4";
	public static final String PTY_RAINDROP = "5";
	public static final String PTY_RAINDROP_AND_SNOWDRIFTING = "6";
	public static final String PTY_SNOWDRIFTING = "7";

	public static final String SKY_SUNNY = "1";
	public static final String SKY_CLOUDY = "3";
	public static final String SKY_OVERCAST = "4";


	private WeatherDataConverter() {
	}

	public static int getSkyDrawableId(@Nullable String sky, @Nullable String pty, boolean isDay) {
		int id = 0;

		if (sky != null) {
			if (sky.equals(SKY_SUNNY)) {
				if (isDay) {
					id = R.drawable.sunny_day_up;
				} else {
					id = R.drawable.sunny_night_up;
				}
			} else if (sky.equals(SKY_CLOUDY)) {
				if (isDay) {
					id = R.drawable.cloudy_day_up;
				} else {
					id = R.drawable.cloudy_night_up;
				}
			} else if (sky.equals(SKY_OVERCAST)) {
				id = R.drawable.overcast_up;
			}
		}

		if (pty != null) {
			if (pty.equals(PTY_NOT)) {
				return id;
			} else if (pty.equals(PTY_RAIN)) {
				id = R.drawable.rain_up;
			} else if (pty.equals(PTY_RAIN_AND_SNOW)) {
				id = R.drawable.rain_or_snow_up;
			} else if (pty.equals(PTY_SNOW)) {
				id = R.drawable.snow_up;
			} else if (pty.equals(PTY_SHOWER)) {
				id = R.drawable.shower_up;
			} else if (pty.equals(PTY_RAINDROP)) {
				id = R.drawable.raindrop_up;
			} else if (pty.equals(PTY_RAINDROP_AND_SNOWDRIFTING)) {
				id = R.drawable.sometimes_snow_or_rain_onetime_snow_or_rain_up;
			} else if (pty.equals(PTY_SNOWDRIFTING)) {
				id = R.drawable.snow_drifting_up;
			}
		}

		return id;
	}

	public static int getSkyDrawableIdForMid(String sky) {
		int id = 0;

		if (sky.equals(SUNNY_FOR_MID)) {
			id = R.drawable.sunny_day_up;
		} else if (sky.equals(CLOUDY_FOR_MID)) {
			id = R.drawable.cloudy_day_up;
		} else if (sky.equals(CLOUDY_AND_RAIN_FOR_MID)) {
			id = R.drawable.sometimes_rain_onetime_rain_up;
		} else if (sky.equals(CLOUDY_AND_SNOW_FOR_MID)) {
			id = R.drawable.sometimes_snow_onetime_snow_up;
		} else if (sky.equals(CLOUDY_AND_RAIN_AND_SNOW_FOR_MID)) {
			id = R.drawable.sometimes_rain_or_snow_onetime_rain_or_snow_up;
		} else if (sky.equals(CLOUDY_AND_SNOW_AND_RAIN_FOR_MID)) {
			id = R.drawable.sometimes_snow_or_rain_onetime_snow_or_rain_up;
		} else if (sky.equals(OVERCAST_FOR_MID)) {
			id = R.drawable.overcast_up;
		} else if (sky.equals(OVERCAST_AND_RAIN_FOR_MID)) {
			id = R.drawable.rain_up;
		} else if (sky.equals(OVERCAST_AND_SNOW_FOR_MID)) {
			id = R.drawable.snow_up;
		} else if (sky.equals(OVERCAST_AND_RAIN_AND_SNOW_FOR_MID)) {
			id = R.drawable.rain_or_snow_up;
		} else if (sky.equals(OVERCAST_AND_SNOW_AND_RAIN_FOR_MID)) {
			id = R.drawable.snow_or_rain_up;
		} else if (sky.equals(CLOUDY_AND_SHOWER_FOR_MID)) {
			id = R.drawable.shower_up;
		} else if (sky.equals(OVERCAST_AND_SHOWER_FOR_MID)) {
			id = R.drawable.shower_up;
		}

		return id;
	}

	public static String convertPrecipitationForm(String pty) {
		String convertedValue = null;

		switch (pty) {
			case PTY_NOT:
				convertedValue = "";
				break;
			case PTY_RAIN:
				convertedValue = context.getString(R.string.rain);
				break;
			case PTY_RAIN_AND_SNOW:
				convertedValue = context.getString(R.string.sleet);
				break;
			case PTY_SNOW:
				convertedValue = context.getString(R.string.snow);
				break;
			case PTY_SHOWER:
				convertedValue = context.getString(R.string.shower);
				break;
			case PTY_RAINDROP:
				convertedValue = context.getString(R.string.raindrop);
				break;
			case PTY_RAINDROP_AND_SNOWDRIFTING:
				convertedValue = context.getString(R.string.raindrop_snowdrifting);
				break;
			case PTY_SNOWDRIFTING:
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

	public static String getWindSpeedDescription(String windSpeed) {
		double speed = Double.parseDouble(windSpeed);
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
		double speed = Double.parseDouble(windSpeed);
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