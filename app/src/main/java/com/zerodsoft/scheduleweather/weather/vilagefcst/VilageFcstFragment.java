package com.zerodsoft.scheduleweather.weather.vilagefcst;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.VilageFcstFragmentBinding;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.weather.common.WeatherDataCallback;
import com.zerodsoft.scheduleweather.weather.dataprocessing.VilageFcstProcessing;
import com.zerodsoft.scheduleweather.weather.sunsetrise.SunSetRiseData;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.weather.dataprocessing.WeatherDataConverter;
import com.zerodsoft.scheduleweather.weather.sunsetrise.SunsetRise;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class VilageFcstFragment extends Fragment {
	private VilageFcstFragmentBinding binding;
	private WeatherAreaCodeDTO weatherAreaCode;
	private VilageFcstProcessing vilageFcstProcessing;

	public VilageFcstFragment(WeatherAreaCodeDTO weatherAreaCodeDTO) {
		this.weatherAreaCode = weatherAreaCodeDTO;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = VilageFcstFragmentBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		clearViews();
		vilageFcstProcessing = new VilageFcstProcessing(getContext(), weatherAreaCode.getY(), weatherAreaCode.getX());
		binding.customProgressView.setContentView(binding.vilageFcstLayout);
		binding.customProgressView.onStartedProcessingData();

		vilageFcstProcessing.getWeatherData(new WeatherDataCallback<VilageFcstResult>() {
			@Override
			public void isSuccessful(VilageFcstResult e) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						binding.customProgressView.onSuccessfulProcessingData();
						setTable(e);
					}
				});
			}

			@Override
			public void isFailure(Exception e) {
				clearViews();
				binding.customProgressView.onFailedProcessingData(getString(R.string.error));
			}
		});
	}

	public void refresh() {
		binding.customProgressView.onStartedProcessingData();

		vilageFcstProcessing.refresh(new WeatherDataCallback<VilageFcstResult>() {
			@Override
			public void isSuccessful(VilageFcstResult e) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						binding.customProgressView.onSuccessfulProcessingData();
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
						binding.customProgressView.onFailedProcessingData(getString(R.string.error));

					}
				});

			}
		});
	}

	public void clearViews() {
		binding.vilageFcstHeaderCol.removeAllViews();
		binding.vilageFcstView.removeAllViews();
	}

	private void setTable(VilageFcstResult vilageFcstResult) {
		final int COLUMN_WIDTH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 53f, getResources().getDisplayMetrics());
		final int TB_MARGIN = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics());

		final int CLOCK_ROW_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 34f,
				getResources().getDisplayMetrics());
		final int SKY_ROW_HEIGHT = COLUMN_WIDTH;
		final int TEMP_ROW_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60f, getResources().getDisplayMetrics());
		final int RAIN_ROW_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 34f, getResources().getDisplayMetrics());
		final int CHANCE_OF_SHOWER_ROW_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 34f,
				getResources().getDisplayMetrics());
		final int WIND_ROW_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 34f, getResources().getDisplayMetrics());
		final int HUMIDITY_ROW_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 34f,
				getResources().getDisplayMetrics());

		clearViews();
		List<VilageFcstFinalData> dataList = vilageFcstResult.getVilageFcstFinalDataList();

		final int COLUMN_SIZE = dataList.size();
		final int VIEW_WIDTH = COLUMN_SIZE * COLUMN_WIDTH;

		Context context = getContext();

		//label column 설정
		TextView clockLabel = new TextView(context);
		TextView skyLabel = new TextView(context);
		TextView tempLabel = new TextView(context);
		TextView rainfallLabel = new TextView(context);
		TextView chanceOfShowerLabel = new TextView(context);
		TextView windLabel = new TextView(context);
		TextView humidityLabel = new TextView(context);

		setLabelTextView(clockLabel, getString(R.string.clock));
		setLabelTextView(skyLabel, getString(R.string.sky));
		setLabelTextView(tempLabel, getString(R.string.temperature));
		setLabelTextView(rainfallLabel, getString(R.string.rainfall));
		setLabelTextView(chanceOfShowerLabel, getString(R.string.chance_of_shower));
		setLabelTextView(windLabel, getString(R.string.wind));
		setLabelTextView(humidityLabel, getString(R.string.humidity));

		LinearLayout.LayoutParams clockLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, CLOCK_ROW_HEIGHT);
		clockLabelParams.topMargin = TB_MARGIN;
		clockLabelParams.bottomMargin = TB_MARGIN;

		LinearLayout.LayoutParams skyLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, SKY_ROW_HEIGHT);
		LinearLayout.LayoutParams tempLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, TEMP_ROW_HEIGHT);
		tempLabelParams.topMargin = TB_MARGIN;
		tempLabelParams.bottomMargin = TB_MARGIN;

		LinearLayout.LayoutParams rainfallLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, RAIN_ROW_HEIGHT);
		rainfallLabelParams.topMargin = TB_MARGIN;
		rainfallLabelParams.bottomMargin = TB_MARGIN;

		LinearLayout.LayoutParams chanceOfShowerLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, CHANCE_OF_SHOWER_ROW_HEIGHT);
		chanceOfShowerLabelParams.topMargin = TB_MARGIN;
		chanceOfShowerLabelParams.bottomMargin = TB_MARGIN;

		LinearLayout.LayoutParams windLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, WIND_ROW_HEIGHT);
		windLabelParams.topMargin = TB_MARGIN;
		windLabelParams.bottomMargin = TB_MARGIN;

		LinearLayout.LayoutParams humidityLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, HUMIDITY_ROW_HEIGHT);
		humidityLabelParams.topMargin = TB_MARGIN;
		humidityLabelParams.bottomMargin = TB_MARGIN;

		clockLabelParams.gravity = Gravity.CENTER;
		skyLabelParams.gravity = Gravity.CENTER;
		tempLabelParams.gravity = Gravity.CENTER;
		rainfallLabelParams.gravity = Gravity.CENTER;
		chanceOfShowerLabelParams.gravity = Gravity.CENTER;
		windLabelParams.gravity = Gravity.CENTER;
		humidityLabelParams.gravity = Gravity.CENTER;

		binding.vilageFcstHeaderCol.addView(clockLabel, clockLabelParams);
		binding.vilageFcstHeaderCol.addView(skyLabel, skyLabelParams);
		binding.vilageFcstHeaderCol.addView(tempLabel, tempLabelParams);
		binding.vilageFcstHeaderCol.addView(rainfallLabel, rainfallLabelParams);
		binding.vilageFcstHeaderCol.addView(chanceOfShowerLabel, chanceOfShowerLabelParams);
		binding.vilageFcstHeaderCol.addView(windLabel, windLabelParams);
		binding.vilageFcstHeaderCol.addView(humidityLabel, humidityLabelParams);

		//시각, 하늘, 기온, 강수량, 강수확률, 바람, 습도 순으로 행 등록
		LinearLayout clockRow = new LinearLayout(context);
		SkyView skyRow = new SkyView(context, dataList);
		TempView tempRow = new TempView(context, dataList);
		RainfallView rainfallRow = new RainfallView(context, dataList);
		LinearLayout chanceOfShowerRow = new LinearLayout(context);
		LinearLayout windRow = new LinearLayout(context);
		LinearLayout humidityRow = new LinearLayout(context);

		clockRow.setOrientation(LinearLayout.HORIZONTAL);
		chanceOfShowerRow.setOrientation(LinearLayout.HORIZONTAL);
		windRow.setOrientation(LinearLayout.HORIZONTAL);
		humidityRow.setOrientation(LinearLayout.HORIZONTAL);

		//시각 --------------------------------------------------------------------------
		Calendar date = Calendar.getInstance();

		for (int col = 0; col < COLUMN_SIZE; col++) {
			TextView textView = new TextView(context);
			date.setTime(dataList.get(col).getFcstDateTime());

			if (date.get(Calendar.HOUR_OF_DAY) == 0 || col == 0) {
				setValueTextView(textView, ClockUtil.MdE_FORMAT.format(date.getTime()) + "\n" + Integer.toString(date.get(Calendar.HOUR_OF_DAY)));
			} else {
				setValueTextView(textView, Integer.toString(date.get(Calendar.HOUR_OF_DAY)));
			}

			LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(COLUMN_WIDTH, CLOCK_ROW_HEIGHT);
			textParams.gravity = Gravity.CENTER;
			clockRow.addView(textView, textParams);
		}

		//하늘 ---------------------------------------------------------------------------
		skyRow.measure(VIEW_WIDTH, SKY_ROW_HEIGHT);

		//기온 ------------------------------------------------------------------------------
		tempRow.measure(VIEW_WIDTH, TEMP_ROW_HEIGHT);

		//강수량 ------------------------------------------------------------------------------
		rainfallRow.measure(VIEW_WIDTH, RAIN_ROW_HEIGHT);

		//강수확률 ------------------------------------------------------------------------------
		for (int col = 0; col < COLUMN_SIZE; col++) {
			TextView textView = new TextView(context);
			setValueTextView(textView, dataList.get(col).getChanceOfShower());

			LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(COLUMN_WIDTH, CHANCE_OF_SHOWER_ROW_HEIGHT);
			textParams.gravity = Gravity.CENTER;
			chanceOfShowerRow.addView(textView, textParams);
		}

		//바람 ------------------------------------------------------------------------------
		for (int col = 0; col < COLUMN_SIZE; col++) {
			TextView textView = new TextView(context);
			setValueTextView(textView, dataList.get(col).getWindSpeed() + "\n" + dataList.get(col).getWindDirection());

			LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(COLUMN_WIDTH, WIND_ROW_HEIGHT);
			textParams.gravity = Gravity.CENTER;
			windRow.addView(textView, textParams);
		}

		//습도 ------------------------------------------------------------------------------
		for (int col = 0; col < COLUMN_SIZE; col++) {
			TextView textView = new TextView(context);
			setValueTextView(textView, dataList.get(col).getHumidity());

			LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(COLUMN_WIDTH, HUMIDITY_ROW_HEIGHT);
			textParams.gravity = Gravity.CENTER;
			humidityRow.addView(textView, textParams);
		}

		LinearLayout.LayoutParams clockRowParams = new LinearLayout.LayoutParams(VIEW_WIDTH, CLOCK_ROW_HEIGHT);
		clockRowParams.topMargin = TB_MARGIN;
		clockRowParams.bottomMargin = TB_MARGIN;

		LinearLayout.LayoutParams skyRowParams = new LinearLayout.LayoutParams(VIEW_WIDTH, SKY_ROW_HEIGHT);
		LinearLayout.LayoutParams tempRowParams = new LinearLayout.LayoutParams(VIEW_WIDTH, TEMP_ROW_HEIGHT);
		tempRowParams.topMargin = TB_MARGIN;
		tempRowParams.bottomMargin = TB_MARGIN;

		LinearLayout.LayoutParams rainfallRowParams = new LinearLayout.LayoutParams(VIEW_WIDTH, RAIN_ROW_HEIGHT);
		rainfallRowParams.topMargin = TB_MARGIN;
		rainfallRowParams.bottomMargin = TB_MARGIN;

		LinearLayout.LayoutParams chanceOfShowerRowParams = new LinearLayout.LayoutParams(VIEW_WIDTH, CHANCE_OF_SHOWER_ROW_HEIGHT);
		chanceOfShowerRowParams.topMargin = TB_MARGIN;
		chanceOfShowerRowParams.bottomMargin = TB_MARGIN;

		LinearLayout.LayoutParams windRowParams = new LinearLayout.LayoutParams(VIEW_WIDTH, WIND_ROW_HEIGHT);
		windRowParams.topMargin = TB_MARGIN;
		windRowParams.bottomMargin = TB_MARGIN;

		LinearLayout.LayoutParams humidityRowParams = new LinearLayout.LayoutParams(VIEW_WIDTH, HUMIDITY_ROW_HEIGHT);
		humidityRowParams.topMargin = TB_MARGIN;
		humidityRowParams.bottomMargin = TB_MARGIN;

		binding.vilageFcstView.addView(clockRow, clockRowParams);
		binding.vilageFcstView.addView(skyRow, skyRowParams);
		binding.vilageFcstView.addView(tempRow, tempRowParams);
		binding.vilageFcstView.addView(rainfallRow, rainfallRowParams);
		binding.vilageFcstView.addView(chanceOfShowerRow, chanceOfShowerRowParams);
		binding.vilageFcstView.addView(windRow, windRowParams);
		binding.vilageFcstView.addView(humidityRow, humidityRowParams);


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


	class SkyView extends View {
		private List<Drawable> skyImageList = new ArrayList<>();

		public SkyView(Context context, List<VilageFcstFinalData> dataList) {
			super(context);
			setSkyImgs(dataList);
			setWillNotDraw(false);
		}

		private void setSkyImgs(List<VilageFcstFinalData> dataList) {

			List<SunSetRiseData> setRiseDataList = SunsetRise.getSunsetRiseList(dataList.get(0).getFcstDateTime(),
					dataList.get(dataList.size() - 2).getFcstDateTime(), weatherAreaCode.getLatitudeSecondsDivide100()
					, weatherAreaCode.getLongitudeSecondsDivide100());
			for (int i = 0; i < dataList.size() - 1; i++) {

				for (SunSetRiseData sunSetRiseData : setRiseDataList) {
					if (ClockUtil.areSameDate(sunSetRiseData.getDate().getTime(), dataList.get(i).getFcstDateTime().getTime())) {
						boolean day = dataList.get(i).getFcstDateTime().after(sunSetRiseData.getSunrise()) && dataList.get(i).getFcstDateTime().before(sunSetRiseData.getSunset());
						skyImageList.add(ContextCompat.getDrawable(getContext(), WeatherDataConverter.getSkyDrawableId(dataList.get(i).getSky(),
								dataList.get(i).getPrecipitationForm(), day)));
						break;
					}
				}
			}
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		}

		@Override
		protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
			super.onLayout(changed, left, top, right, bottom);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			drawImages(canvas);
		}

		private void drawImages(Canvas canvas) {
			final int COLUMN_WIDTH = getWidth() / (skyImageList.size() + 1);
			final int RADIUS = getHeight() / 2;
			final int TOP = 0;
			final int BOTTOM = getHeight();
			final int LEFT = COLUMN_WIDTH - RADIUS;
			final int RIGHT = COLUMN_WIDTH + RADIUS;
			final Rect RECT = new Rect(LEFT, TOP, RIGHT, BOTTOM);

			for (Drawable image : skyImageList) {
				image.setBounds(RECT);
				image.draw(canvas);

				RECT.offset(COLUMN_WIDTH, 0);
			}
		}
	}

	class TempView extends View {
		private List<String> tempList;
		private final int MAX_TEMP;
		private final int MIN_TEMP;
		private final TextPaint TEMP_PAINT;
		private final Paint LINE_PAINT;
		private final Paint CIRCLE_PAINT;
		private final Paint MIN_MAX_TEMP_LINE_PAINT;

		public TempView(Context context, List<VilageFcstFinalData> dataList) {
			super(context);
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

			for (VilageFcstFinalData data : dataList) {
				temp = Integer.parseInt(data.getTemp3Hour());
				tempList.add(data.getTemp3Hour());

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
			setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
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
			final float SPACING = ((VIEW_HEIGHT) / (MAX_TEMP - MIN_TEMP)) / 10f;

			int temp = 0;
			float x = 0f;
			float y = 0f;

			PointF lastColumnPoint = new PointF();

			int index = 0;
			for (String value : tempList) {
				temp = Integer.parseInt(value);
				x = COLUMN_WIDTH / 2f + COLUMN_WIDTH * index;
				y = MIN_TEMP == MAX_TEMP ? getHeight() / 2f : (10f * (MAX_TEMP - temp)) * SPACING + TEXT_HEIGHT + RADIUS;
				canvas.drawCircle(x, y, RADIUS, CIRCLE_PAINT);
				canvas.drawText(value, x, y + RADIUS + TEXT_HEIGHT, TEMP_PAINT);

				if (index != 0) {
					canvas.drawLine(lastColumnPoint.x, lastColumnPoint.y, x, y, LINE_PAINT);
				}

				lastColumnPoint.set(x, y);
				index++;
			}

			//draw min max temp line
			//drawMinMaxTempLine(canvas, MIN_TEMP);
			//drawMinMaxTempLine(canvas, MAX_TEMP);
		}

		private void drawMinMaxTempLine(Canvas canvas, int temp) {
			final float TEXT_HEIGHT = TEMP_PAINT.descent() - TEMP_PAINT.ascent();
			final float RADIUS = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics());

			final float VIEW_WIDTH = getWidth();
			final float VIEW_HEIGHT = getHeight() - ((TEXT_HEIGHT + RADIUS) * 2);
			final float COLUMN_WIDTH = VIEW_WIDTH / tempList.size();
			final float SPACING = ((VIEW_HEIGHT) / (MAX_TEMP - MIN_TEMP)) / 10f;

			float startX = 0f;
			float stopX = 0f;
			float y = 0f;

			startX = COLUMN_WIDTH / 2f + COLUMN_WIDTH * 0;
			stopX = COLUMN_WIDTH / 2f + COLUMN_WIDTH * (tempList.size() - 1);
			y = (10f * (MAX_TEMP - temp)) * SPACING + TEXT_HEIGHT + RADIUS;

			canvas.drawLine(startX, y, stopX, y, MIN_MAX_TEMP_LINE_PAINT);
		}


	}

	class RainfallView extends View {
		List<String> rainfallList;
		final int COLUMN_SIZE;
		final TextPaint VALUE_PAINT;
		final Paint RECT_PAINT;
		final int TEXT_HEIGHT;

		public RainfallView(Context context, List<VilageFcstFinalData> dataList) {
			super(context);

			COLUMN_SIZE = dataList.size();
			rainfallList = new LinkedList<>();
			for (VilageFcstFinalData data : dataList) {
				if (data.getRainPrecipitation6Hour() != null) {
					rainfallList.add(data.getRainPrecipitation6Hour());
				}

			}

			/*
			발표시각 2, 5, 8, 11, 14, 17, 20, 23
			2 : 3
			 */


			VALUE_PAINT = new TextPaint();
			VALUE_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13f, getResources().getDisplayMetrics()));
			VALUE_PAINT.setColor(Color.WHITE);
			VALUE_PAINT.setTextAlign(Paint.Align.CENTER);

			Rect rect = new Rect();
			VALUE_PAINT.getTextBounds("0", 0, 1, rect);
			TEXT_HEIGHT = rect.height();

			RECT_PAINT = new Paint();
			RECT_PAINT.setStyle(Paint.Style.FILL);
			RECT_PAINT.setColor(Color.LTGRAY);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}

		@Override
		protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
			super.onLayout(changed, left, top, right, bottom);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			drawView(canvas);
		}

		private void drawView(Canvas canvas) {
			final float PADDING = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics());
			final float MARGIN_LR = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics());
			final int COLUMN_WIDTH = getWidth() / COLUMN_SIZE;
			final int COLUMN_WIDTH_HALF = COLUMN_WIDTH / 2;
			final float TOP = PADDING;
			final float BOTTOM = getHeight() - PADDING;
			final float Y = (BOTTOM - TOP) / 2f + TOP + (TEXT_HEIGHT / 2f);

			float left = 0f;
			float right = 0f;

			RectF rect = new RectF();
			rect.top = TOP;
			rect.bottom = BOTTOM;

			int index = 0;
			for (String value : rainfallList) {
				// 첫번째 자료는 발표시간+1시간 부터 발표시간+7시간 까지
				if (index == 0) {
					left = 0;
					right = COLUMN_WIDTH + COLUMN_WIDTH_HALF - MARGIN_LR;
				} else {
					left = COLUMN_WIDTH + COLUMN_WIDTH_HALF + ((COLUMN_WIDTH * 2) * (index - 1));
					right = left + (COLUMN_WIDTH * 2);
					left += MARGIN_LR;
					right -= MARGIN_LR;
				}
				rect.left = left;
				rect.right = right;

				canvas.drawRect(left, TOP, right, BOTTOM, RECT_PAINT);
				canvas.drawText(value, rect.centerX(), Y, VALUE_PAINT);

				index++;
			}
		}
	}
}
