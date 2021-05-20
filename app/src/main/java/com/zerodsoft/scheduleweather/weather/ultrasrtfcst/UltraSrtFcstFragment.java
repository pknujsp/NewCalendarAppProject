package com.zerodsoft.scheduleweather.weather.ultrasrtfcst;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.text.TextPaint;
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
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.WeatherItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtfcstresponse.UltraSrtFcstRoot;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.weather.common.ViewProgress;
import com.zerodsoft.scheduleweather.weather.common.WeatherDataCallback;
import com.zerodsoft.scheduleweather.weather.dataprocessing.UltraSrtFcstProcessing;
import com.zerodsoft.scheduleweather.weather.interfaces.OnDownloadedTimeListener;
import com.zerodsoft.scheduleweather.weather.repository.WeatherDataDownloader;
import com.zerodsoft.scheduleweather.weather.sunsetrise.SunSetRiseData;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.WeatherDataConverter;
import com.zerodsoft.scheduleweather.weather.viewmodel.WeatherDbViewModel;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class UltraSrtFcstFragment extends Fragment {
	private final OnDownloadedTimeListener onDownloadedTimeListener;

	private UltraSrtFcstFragmentBinding binding;
	private List<SunSetRiseData> sunSetRiseDataList;

	private WeatherAreaCodeDTO weatherAreaCode;
	private ViewProgress viewProgress;
	private UltraSrtFcstProcessing ultraSrtFcstProcessing;

	public UltraSrtFcstFragment(WeatherAreaCodeDTO weatherAreaCodeDTO, List<SunSetRiseData> sunSetRiseDataList, OnDownloadedTimeListener onDownloadedTimeListener) {
		this.weatherAreaCode = weatherAreaCodeDTO;
		this.sunSetRiseDataList = sunSetRiseDataList;
		this.onDownloadedTimeListener = onDownloadedTimeListener;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = UltraSrtFcstFragmentBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		clearViews();
		ultraSrtFcstProcessing = new UltraSrtFcstProcessing(getContext(), weatherAreaCode.getY(), weatherAreaCode.getX());
		viewProgress = new ViewProgress(binding.ultraSrtFcstLayout, binding.weatherProgressLayout.progressBar, binding.weatherProgressLayout.errorTextview);
		viewProgress.onStartedProcessingData();

		ultraSrtFcstProcessing.getWeatherData(new WeatherDataCallback<UltraSrtFcstResult>() {
			@Override
			public void isSuccessful(UltraSrtFcstResult e) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onDownloadedTimeListener.setDownloadedTime(e.getDownloadedDate(), WeatherDataDTO.ULTRA_SRT_FCST);
						viewProgress.onCompletedProcessingData(true);
						setTable(e);
					}
				});
			}

			@Override
			public void isFailure(Exception e) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						clearViews();
						onDownloadedTimeListener.setDownloadedTime(null, WeatherDataDTO.ULTRA_SRT_FCST);
						viewProgress.onCompletedProcessingData(false, getString(R.string.error));
					}
				});

			}
		});
	}


	public void refresh() {
		viewProgress.onStartedProcessingData();
		ultraSrtFcstProcessing.refresh(new WeatherDataCallback<UltraSrtFcstResult>() {
			@Override
			public void isSuccessful(UltraSrtFcstResult e) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onDownloadedTimeListener.setDownloadedTime(e.getDownloadedDate(), WeatherDataDTO.ULTRA_SRT_FCST);
						viewProgress.onCompletedProcessingData(true);
						setTable(e);
					}
				});
			}

			@Override
			public void isFailure(Exception e) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						clearViews();
						onDownloadedTimeListener.setDownloadedTime(null, WeatherDataDTO.ULTRA_SRT_FCST);
						viewProgress.onCompletedProcessingData(false, getString(R.string.error));
					}
				});

			}
		});
	}

	public void clearViews() {
		binding.ultraSrtFcstHeaderCol.removeAllViews();
		binding.ultraSrtFcstTable.removeAllViews();
	}

	private void setTable(UltraSrtFcstResult ultraSrtFcstResult) {
		final int ITEM_WIDTH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45f, getResources().getDisplayMetrics());
		final int MARGIN = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics());
		final int DP22 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22f, getResources().getDisplayMetrics());
		final int DP34 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 34f, getResources().getDisplayMetrics());
		final int TEMP_ROW_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55f, getResources().getDisplayMetrics());

		List<UltraSrtFcstFinalData> dataList = ultraSrtFcstResult.getUltraSrtFcstFinalDataList();

		final int DATA_SIZE = dataList.size();
		final int VIEW_WIDTH = DATA_SIZE * ITEM_WIDTH;

		clearViews();

		//시각, 하늘, 기온, 강수량, 강수확률, 바람, 습도 순으로 행 등록
		Context context = getContext();

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
		LinearLayout.LayoutParams tempLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, TEMP_ROW_HEIGHT);
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

		TableRow clockRow = new TableRow(context);
		TableRow skyRow = new TableRow(context);
		TempView tempRow = new TempView(context, VIEW_WIDTH, dataList);
		TableRow windRow = new TableRow(context);
		TableRow humidityRow = new TableRow(context);

		//시각 --------------------------------------------------------------------------
		for (int col = 0; col < DATA_SIZE; col++) {
			TextView textView = new TextView(context);
			setValueTextView(textView, ClockUtil.H.format(dataList.get(col).getDateTime()));

			TableRow.LayoutParams textParams = new TableRow.LayoutParams(ITEM_WIDTH, DP22);
			textParams.gravity = Gravity.CENTER;
			clockRow.addView(textView, textParams);
		}

		//하늘 ---------------------------------------------------------------------------
		for (int col = 0; col < DATA_SIZE; col++) {
			ImageView sky = new ImageView(context);
			sky.setScaleType(ImageView.ScaleType.FIT_CENTER);

			sky.setImageDrawable(getSkyImage(dataList.get(col)));

			TableRow.LayoutParams params = new TableRow.LayoutParams(ITEM_WIDTH, DP22);
			params.gravity = Gravity.CENTER;
			skyRow.addView(sky, params);
		}


		//바람 ------------------------------------------------------------------------------
		for (int col = 0; col < DATA_SIZE; col++) {
			TextView textView = new TextView(context);
			setValueTextView(textView, dataList.get(col).getWindSpeed() + "\n" + dataList.get(col).getWindDirection());

			TableRow.LayoutParams textParams = new TableRow.LayoutParams(ITEM_WIDTH, DP34);
			textParams.gravity = Gravity.CENTER;
			windRow.addView(textView, textParams);
		}

		//습도 ------------------------------------------------------------------------------
		for (int col = 0; col < DATA_SIZE; col++) {
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
		TableLayout.LayoutParams tempRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, TEMP_ROW_HEIGHT);
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

	private void setLabelTextView(TextView textView, String labelText) {
		textView.setTextColor(Color.GRAY);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
		textView.setGravity(Gravity.CENTER);
		textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		textView.setText(labelText);
	}

	private void setValueTextView(TextView textView, String value) {
		textView.setTextColor(Color.BLACK);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
		textView.setGravity(Gravity.CENTER);
		textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		textView.setText(value);
	}

	private Drawable getSkyImage(UltraSrtFcstFinalData data) {
		Calendar sunSetRiseCalendar = Calendar.getInstance();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data.getDateTime());

		Drawable drawable = null;

		for (SunSetRiseData sunSetRiseData : sunSetRiseDataList) {
			sunSetRiseCalendar.setTime(sunSetRiseData.getDate());
			if (sunSetRiseCalendar.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR) &&
					sunSetRiseCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
				Date calendarDate = calendar.getTime();
				boolean day = calendarDate.after(sunSetRiseData.getSunrise()) && calendarDate.before(sunSetRiseData.getSunset()) ? true : false;
				drawable = getContext().getDrawable(WeatherDataConverter.getSkyDrawableId(data.getSky(), data.getPrecipitationForm(), day));
			}
		}
		return drawable;
	}


	static class TempView extends View {
		private List<String> tempList;
		private final int MAX_TEMP;
		private final int MIN_TEMP;
		private final TextPaint TEMP_PAINT;
		private final Paint LINE_PAINT;
		private final Paint CIRCLE_PAINT;
		private final Paint MIN_MAX_TEMP_LINE_PAINT;
		private final int WIDTH;

		public TempView(Context context, int WIDTH, List<UltraSrtFcstFinalData> dataList) {
			super(context);
			this.WIDTH = WIDTH;

			TEMP_PAINT = new TextPaint();
			TEMP_PAINT.setTextAlign(Paint.Align.CENTER);
			TEMP_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, getResources().getDisplayMetrics()));
			TEMP_PAINT.setColor(Color.BLACK);

			LINE_PAINT = new Paint();
			LINE_PAINT.setAntiAlias(true);
			LINE_PAINT.setColor(Color.GRAY);
			LINE_PAINT.setStyle(Paint.Style.FILL);
			LINE_PAINT.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.3f, getResources().getDisplayMetrics()));

			MIN_MAX_TEMP_LINE_PAINT = new Paint();
			MIN_MAX_TEMP_LINE_PAINT.setAntiAlias(true);
			MIN_MAX_TEMP_LINE_PAINT.setColor(Color.LTGRAY);
			MIN_MAX_TEMP_LINE_PAINT.setStyle(Paint.Style.FILL);
			MIN_MAX_TEMP_LINE_PAINT.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, getResources().getDisplayMetrics()));

			CIRCLE_PAINT = new Paint();
			CIRCLE_PAINT.setAntiAlias(true);
			CIRCLE_PAINT.setColor(Color.GRAY);
			CIRCLE_PAINT.setStyle(Paint.Style.FILL);

			tempList = new LinkedList<>();

			int max = Integer.MIN_VALUE;
			int min = Integer.MAX_VALUE;
			int temp = 0;

			for (UltraSrtFcstFinalData data : dataList) {
				temp = Integer.parseInt(data.getTemperature());
				tempList.add(data.getTemperature());

				// 최대,최소 기온 구하기
				if (temp >= max) {
					max = temp;
				}

				if (temp <= min) {
					min = temp;
				}
			}
			MAX_TEMP = max;
			MIN_TEMP = min;

			setWillNotDraw(false);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			setMeasuredDimension(WIDTH, heightMeasureSpec);
		}

		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
			super.onLayout(changed, l, t, r, b);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			drawGraph(canvas);
		}

		private void drawGraph(Canvas canvas) {
			// 텍스트의 높이+원의 반지름 만큼 뷰의 상/하단에 여백을 설정한다.
			final float TEXT_HEIGHT = TEMP_PAINT.descent() - TEMP_PAINT.ascent();
			final float RADIUS = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics());

			final float VIEW_WIDTH = getWidth();
			final float VIEW_HEIGHT = getHeight() - ((TEXT_HEIGHT + RADIUS) * 2);
			final float COLUMN_WIDTH = VIEW_WIDTH / tempList.size();
			final float VERTICAL_SPACING = ((VIEW_HEIGHT) / (MAX_TEMP - MIN_TEMP)) / 10f;

			int temp = 0;
			float x = 0f;
			float y = 0f;

			PointF lastColumnPoint = new PointF();

			int index = 0;
			for (String value : tempList) {
				temp = Integer.parseInt(value);
				x = COLUMN_WIDTH / 2f + (COLUMN_WIDTH * index);
				y = (10f * (MAX_TEMP - temp)) * VERTICAL_SPACING + TEXT_HEIGHT + RADIUS;

				canvas.drawCircle(x, y, RADIUS, CIRCLE_PAINT);
				canvas.drawText(value, x, y + RADIUS + TEXT_HEIGHT, TEMP_PAINT);

				if (index != 0) {
					canvas.drawLine(lastColumnPoint.x, lastColumnPoint.y, x, y, LINE_PAINT);
				}

				lastColumnPoint.set(x, y);
				index++;
			}
		}
	}
}