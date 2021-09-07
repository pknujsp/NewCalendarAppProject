package com.zerodsoft.calendarplatform.weather.hourlyfcst;

import com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.HourlyFcstItem;
import com.zerodsoft.calendarplatform.utility.ClockUtil;
import com.zerodsoft.calendarplatform.weather.dataprocessing.WeatherDataConverter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HourlyFcstFinalData {
	private static final String POP = "POP";
	private static final String PTY = "PTY";
	private static final String PCP = "PCP";
	private static final String REH = "REH";
	private static final String SNO = "SNO";
	private static final String SKY = "SKY";
	private static final String TMP = "TMP";
	private static final String TMN = "TMN";
	private static final String TMX = "TMX";
	private static final String UUU = "UUU";
	private static final String VVV = "VVV";
	private static final String WAV = "WAV";
	private static final String VEC = "VEC";
	private static final String WSD = "WSD";
	private static final String T1H = "T1H";
	private static final String RN1 = "RN1";
	private static final String LGT = "LGT";

	//nx
	private String nx;
	//ny
	private String ny;

	private Date fcstDateTime;

	//강수확률 POP
	private String chanceOfShower;
	//강수형태 PTY
	private String precipitationForm;
	//1시간 강수량 PCP, RN1
	private String rainPrecipitation1Hour;
	//습도 REH
	private String humidity;
	//1시간 신적설 SNO
	private String snowPrecipitation1Hour;
	//구름상태 SKY
	private String sky;
	//1시간 기온 TMP, T1H
	private String temp1Hour;
	//최저기온 TMN
	private String tempMin;
	//최고기온 TMX
	private String tempMax;
	//풍향 VEC
	private String windDirection;
	//풍속 WSD
	private String windSpeed;
	//낙뢰 LGT
	private String lightning;

	private static final String notRain = "강수없음";
	private static final String rainLessThan1MMStr = "1mm 미만";
	private static final String rainLessThan1MM = "~1";
	private static final String zero = "0";
	private static final String mm = "mm";

	public HourlyFcstFinalData(List<HourlyFcstItem> hourlyFcstItems) {
		nx = hourlyFcstItems.get(0).getNx();
		ny = hourlyFcstItems.get(0).getNy();
		String date = hourlyFcstItems.get(0).getFcstDate();
		String time = hourlyFcstItems.get(0).getFcstTime().substring(0, 2);

		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(4, 6));
		int day = Integer.parseInt(date.substring(6, 8));
		int hour = Integer.parseInt(time.substring(0, 2));

		Calendar calendar = Calendar.getInstance(ClockUtil.TIME_ZONE);
		calendar.set(year, month - 1, day, hour, 0, 0);

		fcstDateTime = calendar.getTime();

		for (HourlyFcstItem item : hourlyFcstItems) {
			if (item.getCategory().equals(POP)) {
				chanceOfShower = item.getFcstValue();
			} else if (item.getCategory().equals(PTY)) {
				precipitationForm = item.getFcstValue();
			} else if (item.getCategory().equals(PCP)) {
				rainPrecipitation1Hour = item.getFcstValue();
			} else if (item.getCategory().equals(REH)) {
				humidity = item.getFcstValue();
			} else if (item.getCategory().equals(SNO)) {
				snowPrecipitation1Hour = item.getFcstValue();
			} else if (item.getCategory().equals(SKY)) {
				sky = item.getFcstValue();
			} else if (item.getCategory().equals(TMP)) {
				temp1Hour = item.getFcstValue();
			} else if (item.getCategory().equals(TMN)) {
				tempMin = item.getFcstValue();
			} else if (item.getCategory().equals(TMX)) {
				tempMax = item.getFcstValue();
			} else if (item.getCategory().equals(VEC)) {
				windDirection = WeatherDataConverter.convertWindDirection(item.getFcstValue());
			} else if (item.getCategory().equals(WSD)) {
				windSpeed = item.getFcstValue();
			} else if (item.getCategory().equals(T1H)) {
				temp1Hour = item.getFcstValue();
			} else if (item.getCategory().equals(RN1)) {
				rainPrecipitation1Hour = item.getFcstValue();
			} else if (item.getCategory().equals(LGT)) {
				lightning = item.getFcstValue();
			}
		}

		if (chanceOfShower == null) {
			chanceOfShower = "-";
		} else if (chanceOfShower.equals(zero)) {
			chanceOfShower = "-";
		}

		if (precipitationForm.equals(WeatherDataConverter.PTY_RAINDROP)) {
			rainPrecipitation1Hour = WeatherDataConverter.RAINDROP;
		} else if (precipitationForm.equals(WeatherDataConverter.PTY_NOT)) {
			if (rainPrecipitation1Hour.equals(rainLessThan1MMStr) || rainPrecipitation1Hour.equals(notRain)) {
				rainPrecipitation1Hour = "-";
			}
		} else {
			if (rainPrecipitation1Hour.equals(rainLessThan1MMStr)) {
				rainPrecipitation1Hour = rainLessThan1MM;
			} else if (rainPrecipitation1Hour.equals(notRain)) {
				rainPrecipitation1Hour = "-";
			} else if (rainPrecipitation1Hour.contains(mm)) {
				rainPrecipitation1Hour = rainPrecipitation1Hour.substring(0, rainPrecipitation1Hour.length() - 2);
			}
		}
	}

	public String getNx() {
		return nx;
	}

	public String getNy() {
		return ny;
	}

	public Date getFcstDateTime() {
		return fcstDateTime;
	}

	public String getChanceOfShower() {
		return chanceOfShower;
	}

	public String getPrecipitationForm() {
		return precipitationForm;
	}

	public String getRainPrecipitation1Hour() {
		return rainPrecipitation1Hour;
	}

	public String getHumidity() {
		return humidity;
	}

	public String getSnowPrecipitation1Hour() {
		return snowPrecipitation1Hour;
	}

	public String getSky() {
		return sky;
	}

	public String getTemp1Hour() {
		return temp1Hour;
	}

	public String getTempMin() {
		return tempMin;
	}

	public String getTempMax() {
		return tempMax;
	}

	public String getWindDirection() {
		return windDirection;
	}

	public String getWindSpeed() {
		return windSpeed;
	}
}
