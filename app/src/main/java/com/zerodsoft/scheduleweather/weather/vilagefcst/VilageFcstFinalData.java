package com.zerodsoft.scheduleweather.weather.vilagefcst;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.vilagefcstresponse.VilageFcstItem;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.weather.dataprocessing.WeatherDataConverter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VilageFcstFinalData {
	//nx
	private String nx;
	//ny
	private String ny;

	private Date fcstDateTime;

	//강수확률 POP
	private String chanceOfShower;
	//강수형태 PTY
	private String precipitationForm;
	//1시간 강수량 PCP
	private String rainPrecipitation1Hour;
	//습도 REH
	private String humidity;
	//1시간 신적설 SNO
	private String snowPrecipitation1Hour;
	//구름상태 SKY
	private String sky;
	//1시간 기온 TMP
	private String temp1Hour;
	//최저기온 TMN
	private String tempMin;
	//최고기온 TMX
	private String tempMax;
	//풍향 VEC
	private String windDirection;
	//풍속 WSD
	private String windSpeed;

	public VilageFcstFinalData(List<VilageFcstItem> items) {
		nx = items.get(0).getNx();
		ny = items.get(0).getNy();
		String date = items.get(0).getFcstDate();
		String time = items.get(0).getFcstTime().substring(0, 2);

		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(4, 6));
		int day = Integer.parseInt(date.substring(6, 8));
		int hour = Integer.parseInt(time.substring(0, 2));

		Calendar calendar = Calendar.getInstance(ClockUtil.TIME_ZONE);
		calendar.set(year, month - 1, day, hour, 0, 0);

		fcstDateTime = calendar.getTime();

		for (VilageFcstItem item : items) {
			if (item.getCategory().equals("POP")) {
				chanceOfShower = item.getFcstValue();
			} else if (item.getCategory().equals("PTY")) {
				precipitationForm = item.getFcstValue();
			} else if (item.getCategory().equals("PCP")) {
				rainPrecipitation1Hour = item.getFcstValue();
			} else if (item.getCategory().equals("REH")) {
				humidity = item.getFcstValue();
			} else if (item.getCategory().equals("SNO")) {
				snowPrecipitation1Hour = item.getFcstValue();
			} else if (item.getCategory().equals("SKY")) {
				sky = item.getFcstValue();
			} else if (item.getCategory().equals("TMP")) {
				temp1Hour = item.getFcstValue();
			} else if (item.getCategory().equals("TMN")) {
				tempMin = item.getFcstValue();
			} else if (item.getCategory().equals("TMX")) {
				tempMax = item.getFcstValue();
			} else if (item.getCategory().equals("VEC")) {
				windDirection = WeatherDataConverter.convertWindDirection(item.getFcstValue());
			} else if (item.getCategory().equals("WSD")) {
				windSpeed = item.getFcstValue();
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
