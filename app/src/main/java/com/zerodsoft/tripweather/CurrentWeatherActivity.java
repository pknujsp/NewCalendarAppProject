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
import com.zerodsoft.tripweather.RequestResponse.CurrentWeatherResponse;
import com.zerodsoft.tripweather.Utility.Clock;
import com.zerodsoft.tripweather.RequestResponse.CurrentWeatherResponseItem;
import com.zerodsoft.tripweather.Utility.GpsLocation;
import com.zerodsoft.tripweather.Utility.LonLat;
import com.zerodsoft.tripweather.Utility.LonLatConverter;
import com.zerodsoft.tripweather.Utility.ResponseDataClassifier;
import com.zerodsoft.tripweather.WeatherData.CurrentWeather;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrentWeatherActivity extends AppCompatActivity {
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
    protected void onCreate(Bundle savedInstanceState) {
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


        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataDownloadService = DataCommunicationClient.getApiService();
                Map<String, String> currentDateTime = Clock.getCurrentDateTime();
                Map<String, String> queryMap = new HashMap<>();
                GpsLocation gpsLocation = new GpsLocation(CurrentWeatherActivity.this);
                Location location = gpsLocation.getCurrentLocation();
                LonLat lonLat = new LonLatConverter().convertLonLat(location.getLongitude(), location.getLatitude());

                Toast.makeText(CurrentWeatherActivity.this, gpsLocation.getCurrentAddress(location.getLatitude(), location.getLongitude()), Toast.LENGTH_SHORT).show();

                Clock.convertBaseDateTime(currentDateTime);

                queryMap.put("serviceKey", serviceKey);
                queryMap.put("numOfRows", numOfRows);
                queryMap.put("dataType", dataType);
                queryMap.put("pageNo", pageNo);
                queryMap.put("base_date", currentDateTime.get("currentDate"));
                queryMap.put("base_time", currentDateTime.get("currentTime"));
                queryMap.put("nx", String.valueOf(lonLat.getX()));
                queryMap.put("ny", String.valueOf(lonLat.getY()));

                Call<CurrentWeatherResponse> call = dataDownloadService.downloadCurrentWeatherData(queryMap);
                call.enqueue(new Callback<CurrentWeatherResponse>() {
                    @Override
                    public void onResponse
                            (Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                        List<CurrentWeatherResponseItem> currentWeatherResponseItems = response.body().getCurrentWeatherResponseResponse()
                                .getCurrentWeatherResponseBody().getCurrentWeatherResponseItems().getCurrentWeatherResponseItemList();

                        CurrentWeather currentWeather = ResponseDataClassifier.classifyCurrentWeatherResponseItem(currentWeatherResponseItems, getApplicationContext());

                        temperature.setText(currentWeather.getTemperature());
                        rainfall.setText(currentWeather.getRainfall());
                        eastWest.setText(currentWeather.getEastWestWind());
                        southNorth.setText(currentWeather.getSouthNorthWind());
                        humidity.setText(currentWeather.getHumidity());
                        precipitationForm.setText(currentWeather.getPrecipitationForm());
                        windDirection.setText(currentWeather.getWindDirection());
                        windSpeed.setText(currentWeather.getWindSpeed());

                        Toast.makeText(getApplicationContext(), "finished", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getCause().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


}