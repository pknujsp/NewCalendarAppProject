package com.zerodsoft.tripweather;


import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zerodsoft.tripweather.DataCommunication.DataCommunicationClient;
import com.zerodsoft.tripweather.DataCommunication.DataDownloadService;
import com.zerodsoft.tripweather.RequestResponse.WeatherResponse;
import com.zerodsoft.tripweather.Utility.Clock;
import com.zerodsoft.tripweather.RequestResponse.WeatherResponseItem;
import com.zerodsoft.tripweather.Utility.GpsLocation;
import com.zerodsoft.tripweather.Utility.LonLat;
import com.zerodsoft.tripweather.Utility.LonLatConverter;
import com.zerodsoft.tripweather.Utility.ResponseDataClassifier;
import com.zerodsoft.tripweather.WeatherData.WeatherData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrentWeatherActivity extends AppCompatActivity
{
    private DataDownloadService dataDownloadService = null;
    private final String serviceKey = "T2nJm9zlOA0Z7Dut%2BThT6Jp0Itn0zZw80AUP3uMdOWlZJR1gVPkx9p1t8etuSW1kWsSNrGGHKdxbwr1IUlt%2Baw%3D%3D";
    private final String numOfRows = "10";
    private final String dataType = "JSON";
    private final String pageNo = "1";
    private final String nx = "94";
    private final String ny = "77";

    private TextView areaName, temperature, rainfall, eastWest, southNorth, humidity, precipitationForm, windDirection, windSpeed;
    private Button downloadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_weather);

        downloadBtn = (Button) findViewById(R.id.getWeatherBtn);
        areaName = (TextView) findViewById(R.id.area);
        temperature = (TextView) findViewById(R.id.temperature);
        rainfall = (TextView) findViewById(R.id.rainfall);
        eastWest = (TextView) findViewById(R.id.eastWestWind);
        southNorth = (TextView) findViewById(R.id.southNorthWind);
        humidity = (TextView) findViewById(R.id.humidity);
        precipitationForm = (TextView) findViewById(R.id.precipitationForm);
        windDirection = (TextView) findViewById(R.id.windDirection);
        windSpeed = (TextView) findViewById(R.id.windSpeed);


        downloadBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dataDownloadService = DataCommunicationClient.getApiService();
                Map<String, Object> currentDate = Clock.getCurrentDateTime();
                Map<String, String> queryMap = new HashMap<>();
                GpsLocation gpsLocation = new GpsLocation(CurrentWeatherActivity.this);
                Location location = gpsLocation.getCurrentLocation();
                LonLat lonLat = new LonLatConverter().convertLonLat(location.getLongitude(), location.getLatitude());

                Toast.makeText(CurrentWeatherActivity.this, gpsLocation.getCurrentAddress(location.getLatitude(), location.getLongitude()), Toast.LENGTH_SHORT).show();

                Clock.convertBaseDateTime(currentDate, Clock.CURRENT_WEATHER);

                queryMap.put("serviceKey", serviceKey);
                queryMap.put("numOfRows", numOfRows);
                queryMap.put("dataType", dataType);
                queryMap.put("pageNo", pageNo);
                queryMap.put("base_date", (String) currentDate.get("baseDate"));
                queryMap.put("base_time", (String) currentDate.get("baseTime"));
                queryMap.put("nx", String.valueOf(lonLat.getX()));
                queryMap.put("ny", String.valueOf(lonLat.getY()));

                Call<WeatherResponse> call = dataDownloadService.downloadCurrentWeatherData(queryMap);
                call.enqueue(new Callback<WeatherResponse>()
                {
                    @Override
                    public void onResponse
                            (Call<WeatherResponse> call, Response<WeatherResponse> response)
                    {
                        List<WeatherResponseItem> weatherResponseItems = response.body().getWeatherResponseResponse()
                                .getWeatherResponseBody().getWeatherResponseItems().getWeatherResponseItemList();

                        ArrayList<WeatherData> weatherData = ResponseDataClassifier.classifyWeatherResponseItem(weatherResponseItems, getApplicationContext());

                        temperature.setText(weatherData.get(0).getTemperature());
                        rainfall.setText(weatherData.get(0).getRainfall());
                        eastWest.setText(weatherData.get(0).getEastWestWind());
                        southNorth.setText(weatherData.get(0).getSouthNorthWind());
                        humidity.setText(weatherData.get(0).getHumidity());
                        precipitationForm.setText(weatherData.get(0).getPrecipitationForm());
                        windDirection.setText(weatherData.get(0).getWindDirection());
                        windSpeed.setText(weatherData.get(0).getWindSpeed());

                        Toast.makeText(getApplicationContext(), "finished", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<WeatherResponse> call, Throwable t)
                    {
                        Toast.makeText(getApplicationContext(), t.getCause().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


}