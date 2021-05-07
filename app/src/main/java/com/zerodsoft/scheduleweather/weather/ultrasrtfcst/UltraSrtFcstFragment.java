package com.zerodsoft.scheduleweather.weather.ultrasrtfcst;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.databinding.UltraSrtFcstFragmentBinding;
import com.zerodsoft.scheduleweather.retrofit.paremeters.UltraSrtFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.UltraSrtNcstParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.WeatherItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.midlandfcstresponse.MidLandFcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.midtaresponse.MidTaRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtfcstresponse.UltraSrtFcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtncstresponse.UltraSrtNcstRoot;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.weather.common.ViewProgress;
import com.zerodsoft.scheduleweather.weather.interfaces.OnDownloadedTimeListener;
import com.zerodsoft.scheduleweather.weather.repository.WeatherDataDownloader;
import com.zerodsoft.scheduleweather.weather.sunsetrise.SunSetRiseData;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.WeatherDataConverter;
import com.zerodsoft.scheduleweather.weather.viewmodel.WeatherDbViewModel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UltraSrtFcstFragment extends Fragment
{
    private final OnDownloadedTimeListener onDownloadedTimeListener;

    private UltraSrtFcstFragmentBinding binding;
    private List<SunSetRiseData> sunSetRiseDataList;

    private UltraSrtFcst ultraSrtFcst = new UltraSrtFcst();
    private WeatherAreaCodeDTO weatherAreaCode;
    private WeatherDbViewModel weatherDbViewModel;
    private ViewProgress viewProgress;

    private final WeatherDataDownloader weatherDataDownloader = new WeatherDataDownloader()
    {
        @Override
        public void onResponseSuccessful(WeatherItems result)
        {

        }

        @Override
        public void onResponseFailed(Exception e)
        {

        }
    };

    public UltraSrtFcstFragment(WeatherAreaCodeDTO weatherAreaCodeDTO, List<SunSetRiseData> sunSetRiseDataList, OnDownloadedTimeListener onDownloadedTimeListener)
    {
        this.weatherAreaCode = weatherAreaCodeDTO;
        this.sunSetRiseDataList = sunSetRiseDataList;
        this.onDownloadedTimeListener = onDownloadedTimeListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = UltraSrtFcstFragmentBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        clearViews();
        viewProgress = new ViewProgress(binding.ultraSrtFcstLayout, binding.weatherProgressLayout.progressBar, binding.weatherProgressLayout.errorTextview);
        viewProgress.onStartedProcessingData();

        weatherDbViewModel = new ViewModelProvider(this).get(WeatherDbViewModel.class);
        weatherDbViewModel.getWeatherData(weatherAreaCode.getY(), weatherAreaCode.getX(), WeatherDataDTO.ULTRA_SRT_FCST, new CarrierMessagingService.ResultCallback<WeatherDataDTO>()
        {
            @Override
            public void onReceiveResult(@NonNull WeatherDataDTO ultraSrtFcstWeatherDataDTO) throws RemoteException
            {
                if (ultraSrtFcstWeatherDataDTO == null)
                {
                    getWeatherData();
                } else
                {
                    Gson gson = new Gson();
                    UltraSrtFcstRoot ultraSrtFcstRoot = gson.fromJson(ultraSrtFcstWeatherDataDTO.getJson(), UltraSrtFcstRoot.class);
                    Date downloadedDate = new Date(Long.parseLong(ultraSrtFcstWeatherDataDTO.getDownloadedDate()));

                    ultraSrtFcst.setUltraSrtFcstFinalDataList(ultraSrtFcstRoot.getResponse().getBody().getItems(), downloadedDate);
                    requireActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            onDownloadedTimeListener.setDownloadedTime(downloadedDate, WeatherDataDTO.ULTRA_SRT_FCST);
                            viewProgress.onCompletedProcessingData(true);
                            setTable();
                        }
                    });
                }
            }
        });
    }


    public void getWeatherData()
    {
        requireActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                viewProgress.onStartedProcessingData();
            }
        });
        UltraSrtFcstParameter ultraSrtFcstParameter = new UltraSrtFcstParameter();
        ultraSrtFcstParameter.setNx(weatherAreaCode.getX()).setNy(weatherAreaCode.getY()).setNumOfRows("250").setPageNo("1");

        Calendar calendar = Calendar.getInstance();
        weatherDataDownloader.getUltraSrtFcstData(ultraSrtFcstParameter, calendar, new JsonDownloader<JsonObject>()
        {
            @Override
            public void onResponseSuccessful(JsonObject result)
            {
                setWeatherData(result);
            }

            @Override
            public void onResponseFailed(Exception e)
            {
                requireActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        clearViews();
                        viewProgress.onCompletedProcessingData(false);
                        Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void setWeatherData(JsonObject result)
    {
        Gson gson = new Gson();
        UltraSrtFcstRoot ultraSrtFcstRoot = gson.fromJson(result.toString(), UltraSrtFcstRoot.class);

        Date downloadedDate = new Date(System.currentTimeMillis());

        WeatherDataDTO ultraSrtFcstWeatherDataDTO = new WeatherDataDTO();
        ultraSrtFcstWeatherDataDTO.setLatitude(weatherAreaCode.getY());
        ultraSrtFcstWeatherDataDTO.setLongitude(weatherAreaCode.getX());
        ultraSrtFcstWeatherDataDTO.setDataType(WeatherDataDTO.ULTRA_SRT_FCST);
        ultraSrtFcstWeatherDataDTO.setJson(result.toString());
        ultraSrtFcstWeatherDataDTO.setDownloadedDate(String.valueOf(downloadedDate.getTime()));

        weatherDbViewModel.contains(weatherAreaCode.getY(), weatherAreaCode.getX(), WeatherDataDTO.ULTRA_SRT_FCST, new CarrierMessagingService.ResultCallback<Boolean>()
        {
            @Override
            public void onReceiveResult(@NonNull Boolean isContains) throws RemoteException
            {
                if (isContains)
                {
                    weatherDbViewModel.update(weatherAreaCode.getY(), weatherAreaCode.getX(), WeatherDataDTO.ULTRA_SRT_FCST, result.toString(), new CarrierMessagingService.ResultCallback<Boolean>()
                    {
                        @Override
                        public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException
                        {

                        }
                    });
                } else
                {
                    weatherDbViewModel.insert(ultraSrtFcstWeatherDataDTO, new CarrierMessagingService.ResultCallback<WeatherDataDTO>()
                    {
                        @Override
                        public void onReceiveResult(@NonNull WeatherDataDTO weatherDataDTO) throws RemoteException
                        {

                        }
                    });
                }
            }
        });

        ultraSrtFcst.setUltraSrtFcstFinalDataList(ultraSrtFcstRoot.getResponse().getBody().getItems(), downloadedDate);
        requireActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                onDownloadedTimeListener.setDownloadedTime(downloadedDate, WeatherDataDTO.ULTRA_SRT_FCST);
                viewProgress.onCompletedProcessingData(true);
                setTable();
            }
        });
    }


    public void clearViews()
    {
        binding.ultraSrtFcstHeaderCol.removeAllViews();
        binding.ultraSrtFcstTable.removeAllViews();
    }

    private void setTable()
    {
        final int ITEM_WIDTH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45f, getResources().getDisplayMetrics());
        final int MARGIN = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics());
        final int DP22 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22f, getResources().getDisplayMetrics());
        final int DP34 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 34f, getResources().getDisplayMetrics());

        List<UltraSrtFcstFinalData> dataList = ultraSrtFcst.getUltraSrtFcstFinalDataList();

        final int DATA_SIZE = dataList.size();
        final int VIEW_WIDTH = DATA_SIZE * ITEM_WIDTH;

        clearViews();

        //시각, 하늘, 기온, 강수량, 강수확률, 바람, 습도 순으로 행 등록
        Context context = getContext();

        TableRow clockRow = new TableRow(context);
        TableRow skyRow = new TableRow(context);
        TableRow tempRow = new TableRow(context);
        TableRow windRow = new TableRow(context);
        TableRow humidityRow = new TableRow(context);

        //label column 설정
        TextView clockLabel = new TextView(context);
        TextView skyLabel = new TextView(context);
        TextView tempLabel = new TextView(context);
        TextView windLabel = new TextView(context);
        TextView humidityLabel = new TextView(context);

        setLabelTextView(clockLabel, getString(R.string.clock));
        setLabelTextView(skyLabel, getString(R.string.sky));
        setLabelTextView(tempLabel, getString(R.string.temperature));
        setLabelTextView(windLabel, getString(R.string.wind));
        setLabelTextView(humidityLabel, getString(R.string.humidity));

        LinearLayout.LayoutParams clockLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP22);
        clockLabelParams.topMargin = MARGIN;
        clockLabelParams.bottomMargin = MARGIN;
        LinearLayout.LayoutParams skyLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP22);
        skyLabelParams.topMargin = MARGIN;
        skyLabelParams.bottomMargin = MARGIN;
        LinearLayout.LayoutParams tempLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP34);
        tempLabelParams.topMargin = MARGIN;
        tempLabelParams.bottomMargin = MARGIN;
        LinearLayout.LayoutParams windLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP34);
        windLabelParams.topMargin = MARGIN;
        windLabelParams.bottomMargin = MARGIN;
        LinearLayout.LayoutParams humidityLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP34);
        humidityLabelParams.topMargin = MARGIN;
        humidityLabelParams.bottomMargin = MARGIN;

        clockLabelParams.gravity = Gravity.CENTER;
        skyLabelParams.gravity = Gravity.CENTER;
        tempLabelParams.gravity = Gravity.CENTER;
        windLabelParams.gravity = Gravity.CENTER;
        humidityLabelParams.gravity = Gravity.CENTER;

        binding.ultraSrtFcstHeaderCol.addView(clockLabel, clockLabelParams);
        binding.ultraSrtFcstHeaderCol.addView(skyLabel, skyLabelParams);
        binding.ultraSrtFcstHeaderCol.addView(tempLabel, tempLabelParams);
        binding.ultraSrtFcstHeaderCol.addView(windLabel, windLabelParams);
        binding.ultraSrtFcstHeaderCol.addView(humidityLabel, humidityLabelParams);

        //시각 --------------------------------------------------------------------------
        for (int col = 0; col < DATA_SIZE; col++)
        {
            TextView textView = new TextView(context);
            setValueTextView(textView, ClockUtil.H.format(dataList.get(col).getDateTime()));

            TableRow.LayoutParams textParams = new TableRow.LayoutParams(ITEM_WIDTH, DP22);
            textParams.gravity = Gravity.CENTER;
            clockRow.addView(textView, textParams);
        }

        //하늘 ---------------------------------------------------------------------------
        for (int col = 0; col < DATA_SIZE; col++)
        {
            ImageView sky = new ImageView(context);
            sky.setScaleType(ImageView.ScaleType.FIT_CENTER);

            sky.setImageDrawable(getSkyImage(dataList.get(col)));

            TableRow.LayoutParams params = new TableRow.LayoutParams(ITEM_WIDTH, DP22);
            params.gravity = Gravity.CENTER;
            skyRow.addView(sky, params);
        }

        //기온 ------------------------------------------------------------------------------
        for (int col = 0; col < DATA_SIZE; col++)
        {
            TextView textView = new TextView(context);
            setValueTextView(textView, dataList.get(col).getTemperature());

            TableRow.LayoutParams textParams = new TableRow.LayoutParams(ITEM_WIDTH, DP34);
            textParams.gravity = Gravity.CENTER;
            tempRow.addView(textView, textParams);
        }


        //바람 ------------------------------------------------------------------------------
        for (int col = 0; col < DATA_SIZE; col++)
        {
            TextView textView = new TextView(context);
            setValueTextView(textView, dataList.get(col).getWindSpeed() + "\n" + dataList.get(col).getWindDirection());

            TableRow.LayoutParams textParams = new TableRow.LayoutParams(ITEM_WIDTH, DP34);
            textParams.gravity = Gravity.CENTER;
            windRow.addView(textView, textParams);
        }

        //습도 ------------------------------------------------------------------------------
        for (int col = 0; col < DATA_SIZE; col++)
        {
            TextView textView = new TextView(context);
            setValueTextView(textView, dataList.get(col).getHumidity());

            TableRow.LayoutParams textParams = new TableRow.LayoutParams(ITEM_WIDTH, DP34);
            textParams.gravity = Gravity.CENTER;
            humidityRow.addView(textView, textParams);
        }

        TableLayout.LayoutParams clockRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
        clockRowParams.topMargin = MARGIN;
        clockRowParams.bottomMargin = MARGIN;
        TableLayout.LayoutParams skyRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
        skyRowParams.topMargin = MARGIN;
        skyRowParams.bottomMargin = MARGIN;
        TableLayout.LayoutParams tempRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
        tempRowParams.topMargin = MARGIN;
        tempRowParams.bottomMargin = MARGIN;
        TableLayout.LayoutParams windRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
        windRowParams.topMargin = MARGIN;
        windRowParams.bottomMargin = MARGIN;
        TableLayout.LayoutParams humidityRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
        humidityRowParams.topMargin = MARGIN;
        humidityRowParams.bottomMargin = MARGIN;

        binding.ultraSrtFcstTable.addView(clockRow, clockRowParams);
        binding.ultraSrtFcstTable.addView(skyRow, skyRowParams);
        binding.ultraSrtFcstTable.addView(tempRow, tempRowParams);
        binding.ultraSrtFcstTable.addView(windRow, windRowParams);
        binding.ultraSrtFcstTable.addView(humidityRow, humidityRowParams);
    }

    private void setLabelTextView(TextView textView, String labelText)
    {
        textView.setTextColor(Color.GRAY);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        textView.setGravity(Gravity.CENTER);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setText(labelText);
    }

    private void setValueTextView(TextView textView, String value)
    {
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        textView.setGravity(Gravity.CENTER);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setText(value);
    }

    private Drawable getSkyImage(UltraSrtFcstFinalData data)
    {
        Calendar sunSetRiseCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(data.getDateTime());

        Drawable drawable = null;

        for (SunSetRiseData sunSetRiseData : sunSetRiseDataList)
        {
            sunSetRiseCalendar.setTime(sunSetRiseData.getDate());
            if (sunSetRiseCalendar.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR) &&
                    sunSetRiseCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))
            {
                Date calendarDate = calendar.getTime();
                boolean day = calendarDate.after(sunSetRiseData.getSunrise()) && calendarDate.before(sunSetRiseData.getSunset()) ? true : false;
                drawable = getContext().getDrawable(WeatherDataConverter.getSkyDrawableId(data.getSky(), data.getPrecipitationForm(), day));
            }
        }
        return drawable;
    }
}