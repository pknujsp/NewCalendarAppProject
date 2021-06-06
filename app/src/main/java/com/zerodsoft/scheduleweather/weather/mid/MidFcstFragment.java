package com.zerodsoft.scheduleweather.weather.mid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.MidFcstFragmentBinding;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.weather.common.ViewProgress;
import com.zerodsoft.scheduleweather.weather.common.WeatherDataCallback;
import com.zerodsoft.scheduleweather.weather.dataprocessing.MidFcstProcessing;
import com.zerodsoft.scheduleweather.weather.interfaces.OnDownloadedTimeListener;
import com.zerodsoft.scheduleweather.weather.dataprocessing.WeatherDataConverter;

import java.util.LinkedList;
import java.util.List;

public class MidFcstFragment extends Fragment {
	private MidFcstFragmentBinding binding;
	private WeatherAreaCodeDTO weatherAreaCode;
	private final OnDownloadedTimeListener onDownloadedTimeListener;
	private ViewProgress viewProgress;
	private MidFcstProcessing midFcstProcessing;

	public MidFcstFragment(WeatherAreaCodeDTO weatherAreaCodeDTO, OnDownloadedTimeListener onDownloadedTimeListener) {
		this.weatherAreaCode = weatherAreaCodeDTO;
		this.onDownloadedTimeListener = onDownloadedTimeListener;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = MidFcstFragmentBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		clearViews();

		midFcstProcessing = new MidFcstProcessing(getContext(), weatherAreaCode.getY(), weatherAreaCode.getX(), weatherAreaCode);
		viewProgress = new ViewProgress(binding.midFcstLayout, binding.weatherProgressLayout.progressBar,
				binding.weatherProgressLayout.progressStatusTextview, binding.weatherProgressLayout.getRoot());
		viewProgress.onStartedProcessingData();

		midFcstProcessing.getWeatherData(new WeatherDataCallback<MidFcstResult>() {
			@Override
			public void isSuccessful(MidFcstResult e) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onDownloadedTimeListener.setDownloadedTime(e.getDownloadedDate(), WeatherDataDTO.MID_LAND_FCST);
						onDownloadedTimeListener.setDownloadedTime(e.getDownloadedDate(), WeatherDataDTO.MID_TA);
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
						onDownloadedTimeListener.setDownloadedTime(null, WeatherDataDTO.MID_LAND_FCST);
						onDownloadedTimeListener.setDownloadedTime(null, WeatherDataDTO.MID_TA);
						viewProgress.onCompletedProcessingData(false, e.getMessage());

					}
				});
			}
		});
	}

	public void refresh() {
		viewProgress.onStartedProcessingData();
		midFcstProcessing.refresh(new WeatherDataCallback<MidFcstResult>() {
			@Override
			public void isSuccessful(MidFcstResult e) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onDownloadedTimeListener.setDownloadedTime(e.getDownloadedDate(), WeatherDataDTO.MID_LAND_FCST);
						onDownloadedTimeListener.setDownloadedTime(e.getDownloadedDate(), WeatherDataDTO.MID_TA);
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
						onDownloadedTimeListener.setDownloadedTime(null, WeatherDataDTO.MID_LAND_FCST);
						onDownloadedTimeListener.setDownloadedTime(null, WeatherDataDTO.MID_TA);
						viewProgress.onCompletedProcessingData(false, e.getMessage());

					}
				});
			}
		});
	}


	public void clearViews() {
		binding.midFcstHeaderCol.removeAllViews();
		binding.midFcstTable.removeAllViews();
	}

	private void setTable(MidFcstResult midFcstResult) {
		final int COLUMN_WIDTH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80f, getResources().getDisplayMetrics());
		final int MARGIN_TB = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics());
		final int DIVISION_LINE_WIDTH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, getResources().getDisplayMetrics());
		final int DP4 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics());

		final int DATE_ROW_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 34f, getResources().getDisplayMetrics());
		final int SMALL_IMG_SIZE = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 26f, getResources().getDisplayMetrics());
		final int BIG_SKY_IMG_SIZE = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, getResources().getDisplayMetrics());
		final int CHANCE_OF_SHOWER_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, getResources().getDisplayMetrics());
		final int TEMP_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90f, getResources().getDisplayMetrics());

		List<MidFcstData> dataList = midFcstResult.getMidFcstFinalDataList();

		final int DATA_SIZE = dataList.size();
		final int VIEW_WIDTH = DATA_SIZE * COLUMN_WIDTH;

		clearViews();

		Context context = getContext();

		TableRow dateRow = new TableRow(context);
		TableRow skyRow = new TableRow(context);
		TableRow chanceOfShowerRow = new TableRow(context);
		TempView tempRow = new TempView(context, dataList);

		TextView dateLabel = new TextView(context);
		TextView skyLabel = new TextView(context);
		TextView tempLabel = new TextView(context);
		TextView chanceOfShowerLabel = new TextView(context);

		dateLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f);
		skyLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f);
		tempLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f);
		chanceOfShowerLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f);

		dateLabel.setGravity(Gravity.CENTER);
		dateLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		skyLabel.setGravity(Gravity.CENTER);
		skyLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		tempLabel.setGravity(Gravity.CENTER);
		tempLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		chanceOfShowerLabel.setGravity(Gravity.CENTER);
		chanceOfShowerLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

		dateLabel.setTextColor(Color.GRAY);
		skyLabel.setTextColor(Color.GRAY);
		tempLabel.setTextColor(Color.GRAY);
		chanceOfShowerLabel.setTextColor(Color.GRAY);

		dateLabel.setText(getString(R.string.date));
		skyLabel.setText(getString(R.string.sky) + "\n" + "(" + getString(R.string.am) + "/" + getString(R.string.pm) + ")");
		tempLabel.setText(getString(R.string.temperature));
		chanceOfShowerLabel.setText(getString(R.string.chance_of_shower));

		LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DATE_ROW_HEIGHT);
		dateParams.topMargin = MARGIN_TB;
		dateParams.bottomMargin = MARGIN_TB;
		LinearLayout.LayoutParams skyParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, BIG_SKY_IMG_SIZE);
		skyParams.topMargin = MARGIN_TB;
		skyParams.bottomMargin = MARGIN_TB;
		LinearLayout.LayoutParams chanceOfShowerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, CHANCE_OF_SHOWER_HEIGHT);
		chanceOfShowerParams.topMargin = MARGIN_TB;
		chanceOfShowerParams.bottomMargin = MARGIN_TB;
		LinearLayout.LayoutParams tempParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, TEMP_HEIGHT);
		tempParams.topMargin = MARGIN_TB;
		tempParams.bottomMargin = MARGIN_TB;

		dateParams.gravity = Gravity.CENTER;
		skyParams.gravity = Gravity.CENTER;
		tempParams.gravity = Gravity.CENTER;
		chanceOfShowerParams.gravity = Gravity.CENTER;

		binding.midFcstHeaderCol.addView(dateLabel, dateParams);
		binding.midFcstHeaderCol.addView(skyLabel, skyParams);
		binding.midFcstHeaderCol.addView(chanceOfShowerLabel, chanceOfShowerParams);
		binding.midFcstHeaderCol.addView(tempLabel, tempParams);

		//시각 --------------------------------------------------------------------------
		for (int col = 0; col < DATA_SIZE; col++) {
			TextView textView = new TextView(context);
			setValueTextView(textView);
			textView.setText(dataList.get(col).getDate());

			TableRow.LayoutParams textParams = new TableRow.LayoutParams(COLUMN_WIDTH, DATE_ROW_HEIGHT);
			textParams.gravity = Gravity.CENTER;
			dateRow.addView(textView, textParams);
		}

		//하늘 ---------------------------------------------------------------------------
		//3~7일
		for (int col = 0; col <= 4; col++) {
			ImageView skyAm = new ImageView(context);
			ImageView skyPm = new ImageView(context);

			skyAm.setScaleType(ImageView.ScaleType.FIT_CENTER);
			skyPm.setScaleType(ImageView.ScaleType.FIT_CENTER);

			//오전,오후 하늘
			Drawable[] drawables = getSkyImage(dataList.get(col));
			skyAm.setImageDrawable(drawables[0]);
			skyPm.setImageDrawable(drawables[1]);

			LinearLayout linearLayout = new LinearLayout(context);
			linearLayout.setOrientation(LinearLayout.HORIZONTAL);

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, SMALL_IMG_SIZE);
			layoutParams.gravity = Gravity.CENTER;
			layoutParams.weight = 1;

			View divisionLine = new View(context);
			LinearLayout.LayoutParams divisionLineParams = new LinearLayout.LayoutParams(DIVISION_LINE_WIDTH, SMALL_IMG_SIZE);
			divisionLineParams.leftMargin = DP4;
			divisionLineParams.rightMargin = DP4;
			divisionLineParams.gravity = Gravity.CENTER;

			divisionLine.setLayoutParams(divisionLineParams);
			divisionLine.setBackgroundColor(Color.GRAY);

			linearLayout.addView(skyAm, layoutParams);
			linearLayout.addView(divisionLine);
			linearLayout.addView(skyPm, layoutParams);

			TableRow.LayoutParams params = new TableRow.LayoutParams(COLUMN_WIDTH, BIG_SKY_IMG_SIZE);
			params.gravity = Gravity.CENTER;
			skyRow.addView(linearLayout, params);
		}

		//8~10일
		for (int col = 5; col <= 7; col++) {
			ImageView sky = new ImageView(context);
			sky.setScaleType(ImageView.ScaleType.FIT_CENTER);

			//하늘
			Drawable[] drawables = getSkyImage(dataList.get(col));
			sky.setImageDrawable(drawables[0]);

			LinearLayout linearLayout = new LinearLayout(context);
			linearLayout.setOrientation(LinearLayout.HORIZONTAL);

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(COLUMN_WIDTH, BIG_SKY_IMG_SIZE);
			layoutParams.gravity = Gravity.CENTER;
			linearLayout.addView(sky, layoutParams);

			TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams(COLUMN_WIDTH, BIG_SKY_IMG_SIZE);
			tableRowParams.gravity = Gravity.CENTER;
			skyRow.addView(linearLayout, tableRowParams);
		}

		//강수확률 ---------------------------------------------------------------------------------
		//3~7일
		for (int col = 0; col <= 4; col++) {
			TextView textView = new TextView(context);
			setValueTextView(textView);

			textView.setText(dataList.get(col).getAmShowerOfChance() + " / " + dataList.get(col).getPmShowerOfChance());

			TableRow.LayoutParams textParams = new TableRow.LayoutParams(COLUMN_WIDTH, CHANCE_OF_SHOWER_HEIGHT);
			textParams.gravity = Gravity.CENTER;
			chanceOfShowerRow.addView(textView, textParams);
		}

		// 8~10일
		for (int col = 5; col <= 7; col++) {
			TextView textView = new TextView(context);
			setValueTextView(textView);

			textView.setText(dataList.get(col).getShowerOfChance());

			TableRow.LayoutParams textParams = new TableRow.LayoutParams(COLUMN_WIDTH, CHANCE_OF_SHOWER_HEIGHT);
			textParams.gravity = Gravity.CENTER;
			chanceOfShowerRow.addView(textView, textParams);
		}

		//기온 ------------------------------------------------------------------------------
		tempRow.measure(VIEW_WIDTH, TEMP_HEIGHT);

		TableLayout.LayoutParams dateRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
		dateRowParams.topMargin = MARGIN_TB;
		dateRowParams.bottomMargin = MARGIN_TB;
		TableLayout.LayoutParams skyRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
		skyRowParams.topMargin = MARGIN_TB;
		skyRowParams.bottomMargin = MARGIN_TB;
		TableLayout.LayoutParams chanceOfShowerRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
		chanceOfShowerRowParams.topMargin = MARGIN_TB;
		chanceOfShowerRowParams.bottomMargin = MARGIN_TB;
		TableLayout.LayoutParams tempRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, TEMP_HEIGHT);
		tempRowParams.topMargin = MARGIN_TB;
		tempRowParams.bottomMargin = MARGIN_TB;

		binding.midFcstTable.addView(dateRow, dateRowParams);
		binding.midFcstTable.addView(skyRow, skyRowParams);
		binding.midFcstTable.addView(chanceOfShowerRow, chanceOfShowerRowParams);
		binding.midFcstTable.addView(tempRow, tempRowParams);
	}

	private void setValueTextView(TextView textView) {
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f);
		textView.setGravity(Gravity.CENTER);
		textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		textView.setTextColor(Color.BLACK);
	}

	private Drawable[] getSkyImage(MidFcstData data) {
		Drawable[] drawables;
		if (data.getAmSky() != null) {
			drawables = new Drawable[2];
			drawables[0] = getContext().getDrawable(WeatherDataConverter.getSkyDrawableId(data.getAmSky()));
			drawables[1] = getContext().getDrawable(WeatherDataConverter.getSkyDrawableId(data.getPmSky()));
		} else {
			drawables = new Drawable[1];
			drawables[0] = getContext().getDrawable(WeatherDataConverter.getSkyDrawableId(data.getSky()));
		}
		return drawables;
	}

	class TempView extends View {
		private List<String> maxTempList;
		private List<String> minTempList;
		private final int MAX_TEMP;
		private final int MIN_TEMP;
		private final TextPaint TEMP_PAINT;
		private final Paint LINE_PAINT;
		private final Paint MIN_MAX_TEMP_LINE_PAINT;
		private final Paint CIRCLE_PAINT;

		public TempView(Context context, List<MidFcstData> dataList) {
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

			maxTempList = new LinkedList<>();
			minTempList = new LinkedList<>();

			int max = Integer.MIN_VALUE;
			int min = Integer.MAX_VALUE;
			int maxTemp = 0;
			int minTemp = 0;

			for (MidFcstData data : dataList) {
				maxTemp = Integer.parseInt(data.getTempMax());
				minTemp = Integer.parseInt(data.getTempMin());
				maxTempList.add(data.getTempMax());
				minTempList.add(data.getTempMin());

				// 최대,최소 기온 구하기
				if (maxTemp >= max) {
					max = maxTemp;
				}
				if (minTemp <= min) {
					min = minTemp;
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
			final float TEXT_ASCENT = -TEMP_PAINT.ascent();
			final float TEXT_DESCENT = TEMP_PAINT.descent();
			final float RADIUS = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics());

			final float VIEW_WIDTH = getWidth();
			final float VIEW_HEIGHT = getHeight() - ((TEXT_HEIGHT + RADIUS) * 2);
			final float COLUMN_WIDTH = VIEW_WIDTH / maxTempList.size();
			final float SPACING = ((VIEW_HEIGHT) / (MAX_TEMP - MIN_TEMP)) / 10f;

			int min = 0;
			int max = 0;
			float x = 0f;
			float minY = 0f;
			float maxY = 0f;

			PointF lastMinColumnPoint = new PointF();
			PointF lastMaxColumnPoint = new PointF();

			for (int index = 0; index < maxTempList.size(); index++) {
				min = Integer.parseInt(minTempList.get(index));
				max = Integer.parseInt(maxTempList.get(index));

				x = COLUMN_WIDTH / 2f + COLUMN_WIDTH * index;
				minY = (10f * (MAX_TEMP - min)) * SPACING + TEXT_HEIGHT + RADIUS;
				maxY = (10f * (MAX_TEMP - max)) * SPACING + TEXT_HEIGHT + RADIUS;

				canvas.drawCircle(x, minY, RADIUS, CIRCLE_PAINT);
				canvas.drawText(minTempList.get(index), x, minY + RADIUS + TEXT_HEIGHT, TEMP_PAINT);

				canvas.drawCircle(x, maxY, RADIUS, CIRCLE_PAINT);
				canvas.drawText(maxTempList.get(index), x, maxY - RADIUS - TEXT_HEIGHT + TEXT_ASCENT, TEMP_PAINT);

				if (index != 0) {
					canvas.drawLine(lastMinColumnPoint.x, lastMinColumnPoint.y, x, minY, LINE_PAINT);
					canvas.drawLine(lastMaxColumnPoint.x, lastMaxColumnPoint.y, x, maxY, LINE_PAINT);
				}

				lastMinColumnPoint.set(x, minY);
				lastMaxColumnPoint.set(x, maxY);
			}

			drawMinMaxTempLine(canvas, MIN_TEMP);
			drawMinMaxTempLine(canvas, MAX_TEMP);
		}

		private void drawMinMaxTempLine(Canvas canvas, int temp) {
			final float TEXT_HEIGHT = TEMP_PAINT.descent() - TEMP_PAINT.ascent();
			final float RADIUS = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics());

			final float VIEW_WIDTH = getWidth();
			final float VIEW_HEIGHT = getHeight() - ((TEXT_HEIGHT + RADIUS) * 2);
			final float COLUMN_WIDTH = VIEW_WIDTH / maxTempList.size();
			final float SPACING = ((VIEW_HEIGHT) / (MAX_TEMP - MIN_TEMP)) / 10f;

			float startX = COLUMN_WIDTH / 2f + COLUMN_WIDTH * 0;
			float stopX = COLUMN_WIDTH / 2f + COLUMN_WIDTH * (maxTempList.size() - 1);
			float y = (10f * (MAX_TEMP - temp)) * SPACING + TEXT_HEIGHT + RADIUS;

			canvas.drawLine(startX, y, stopX, y, MIN_MAX_TEMP_LINE_PAINT);
		}

	}
}
