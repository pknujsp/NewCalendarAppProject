package com.zerodsoft.calendarplatform.weather.aircondition.airconditionbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.zerodsoft.calendarplatform.R;

import java.util.List;

public class AirConditionBar extends View {
	private final List<BarInitData> barInitDataList;
	private final int STATUS_COUNT = 4;
	private final float BAR_LR_PADDING;
	private final int DATA_TYPE;
	private final TextPaint REFERENCE_VALUE_TEXTPAINT;
	private final TextPaint REFERENCE_STATUS_TEXTPAINT;
	private final Paint BAR_PAINT = new Paint();

	private final Paint VALUE_CIRCLE_PAINT;
	private final int VALUE_CIRCLE_COLOR;

	private final int REFERENCE_VALUE_TEXT_HEIGHT;
	private final int REFERENCE_STATUS_TEXT_HEIGHT;
	private final int BAR_HEIGHT;
	private final int BAR_TOP_MARGIN;
	private final int BAR_BOTTOM_MARGIN;
	private final int BAR_SPACING;
	private final int VIEW_HEIGHT;
	private final float VALUE_CIRCLE_SIZE;

	private Rect barRect = new Rect();

	private Double dataValue;

	public AirConditionBar(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AirConditionBar, 0, 0);
		try {
			VALUE_CIRCLE_COLOR = a.getInteger(R.styleable.AirConditionBar_value_circle_color, 0);
			VALUE_CIRCLE_SIZE = a.getDimension(R.styleable.AirConditionBar_value_circle_size, 0f);
			BAR_HEIGHT = (int) a.getDimension(R.styleable.AirConditionBar_bar_height, 0f);
			DATA_TYPE = a.getInteger(R.styleable.AirConditionBar_data_type, 0);
			barInitDataList = BarInitDataCreater.getBarInitData(context, DATA_TYPE);
		} finally {
			a.recycle();
		}

		REFERENCE_VALUE_TEXTPAINT = new TextPaint();
		REFERENCE_VALUE_TEXTPAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10f, getResources().getDisplayMetrics()));
		REFERENCE_VALUE_TEXTPAINT.setColor(Color.GRAY);
		REFERENCE_VALUE_TEXTPAINT.setTextAlign(Paint.Align.LEFT);

		Rect rect = new Rect();
		REFERENCE_VALUE_TEXTPAINT.getTextBounds("0", 0, 1, rect);
		REFERENCE_VALUE_TEXT_HEIGHT = rect.height();

		REFERENCE_STATUS_TEXTPAINT = new TextPaint();
		REFERENCE_STATUS_TEXTPAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 11f,
				getResources().getDisplayMetrics()));
		REFERENCE_STATUS_TEXTPAINT.setColor(Color.GRAY);
		REFERENCE_STATUS_TEXTPAINT.setTextAlign(Paint.Align.CENTER);

		String sampleText = "매우 나쁨";
		REFERENCE_STATUS_TEXTPAINT.getTextBounds(sampleText, 0, sampleText.length(), rect);
		REFERENCE_STATUS_TEXT_HEIGHT = rect.height();

		BAR_LR_PADDING = rect.width() / 2 + 5;

		VALUE_CIRCLE_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
		VALUE_CIRCLE_PAINT.setColor(VALUE_CIRCLE_COLOR);

		BAR_TOP_MARGIN = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics());
		BAR_BOTTOM_MARGIN = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics());
		BAR_SPACING = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics());

		VIEW_HEIGHT = REFERENCE_STATUS_TEXT_HEIGHT + BAR_TOP_MARGIN + BAR_HEIGHT + BAR_BOTTOM_MARGIN + REFERENCE_VALUE_TEXT_HEIGHT;
	}

	public void setDataValue(double dataValue) {
		this.dataValue = dataValue;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(widthMeasureSpec, VIEW_HEIGHT);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		final int viewWidth = getWidth();
		final int barWidth = (int) ((viewWidth - (BAR_LR_PADDING * 2) - (BAR_SPACING * 2)) / 3);
		REFERENCE_VALUE_TEXTPAINT.setTextAlign(Paint.Align.LEFT);

		barRect.set(0, REFERENCE_STATUS_TEXT_HEIGHT + BAR_TOP_MARGIN, 0, REFERENCE_STATUS_TEXT_HEIGHT + BAR_TOP_MARGIN + BAR_HEIGHT);

		ArrayMap<Float, ValueCoordinates> valueXCoordinateArrMap = null;

		if (dataValue != null) {
			valueXCoordinateArrMap = new ArrayMap<>();
		}

		for (int statusIndex = 0; statusIndex < STATUS_COUNT; statusIndex++) {
			final float referenceValue = barInitDataList.get(statusIndex).getReferenceValueBegin();

			if (statusIndex != 3) {
				//좋음, 보통, 나쁨
				barRect.left = (int) ((barWidth + BAR_SPACING) * statusIndex + BAR_LR_PADDING);
				barRect.right = barRect.left + barWidth;

				//바 그리기
				if (statusIndex == 2) {
					Paint gradientPaint = new Paint();
					gradientPaint.setShader(new LinearGradient(barRect.left, 0, barRect.right, 0,
							new int[]{barInitDataList.get(2).getColor(), barInitDataList.get(3).getColor()}, null,
							Shader.TileMode.CLAMP));
					canvas.drawRect(barRect, gradientPaint);
				} else {
					BAR_PAINT.setColor(barInitDataList.get(statusIndex).getColor());
					canvas.drawRect(barRect, BAR_PAINT);
				}

				//기준값 표시
				canvas.drawText(String.valueOf(referenceValue), barRect.left,
						barRect.bottom + BAR_BOTTOM_MARGIN + REFERENCE_VALUE_TEXT_HEIGHT, REFERENCE_VALUE_TEXTPAINT);
				//상태 표시
				canvas.drawText(barInitDataList.get(statusIndex).getStatusName(), barRect.centerX(), REFERENCE_STATUS_TEXT_HEIGHT, REFERENCE_STATUS_TEXTPAINT);

				if (dataValue != null) {
					if (statusIndex == 2) {
						int widthPerValue =
								(int) (barWidth / (barInitDataList.get(statusIndex + 1).getReferenceValueBegin() - barInitDataList.get(statusIndex).getReferenceValueBegin()));
						valueXCoordinateArrMap.put(referenceValue, new ValueCoordinates(barRect.left, barRect.right - widthPerValue));
					} else {
						valueXCoordinateArrMap.put(referenceValue, new ValueCoordinates(barRect.left, barRect.right));
					}
				}
			} else {
				//매우 나쁨
				//기준값 표시
				REFERENCE_VALUE_TEXTPAINT.setTextAlign(Paint.Align.CENTER);
				canvas.drawText(String.valueOf(referenceValue), barRect.right,
						barRect.bottom + BAR_BOTTOM_MARGIN + REFERENCE_VALUE_TEXT_HEIGHT, REFERENCE_VALUE_TEXTPAINT);
				//상태 표시
				canvas.drawText(barInitDataList.get(statusIndex).getStatusName(), barRect.right, REFERENCE_STATUS_TEXT_HEIGHT,
						REFERENCE_STATUS_TEXTPAINT);

				if (dataValue != null) {
					valueXCoordinateArrMap.put(referenceValue, new ValueCoordinates(barRect.right, barRect.right));
				}
			}
		}

		if (dataValue != null) {
			//데이터 값 표시
			int beginX = 0;
			int endX = 0;
			float beginValue = 0f;
			float endValue = 0f;
			Float cx = null;

			// 0, 31, 81, 151
			// 10, 20, 30, 40
			for (int index = 1; index < valueXCoordinateArrMap.size(); index++) {
				if (dataValue <= valueXCoordinateArrMap.keyAt(index)) {
					beginValue = valueXCoordinateArrMap.keyAt(index - 1);
					endValue = valueXCoordinateArrMap.keyAt(index);
					beginX = valueXCoordinateArrMap.valueAt(index - 1).beginX;
					endX = valueXCoordinateArrMap.valueAt(index - 1).endX;

					cx = (float) (((endX - beginX) / (endValue - beginValue)) * (dataValue - beginValue) + beginX);
					break;
				}
			}

			if (cx == null) {
				cx = (float) valueXCoordinateArrMap.valueAt(3).beginX;
			}
			canvas.drawCircle(cx, barRect.centerY(), VALUE_CIRCLE_SIZE, VALUE_CIRCLE_PAINT);
		}
	}

	static class ValueCoordinates {
		final int beginX;
		final int endX;

		public ValueCoordinates(int beginX, int endX) {
			this.beginX = beginX;
			this.endX = endX;
		}
	}
}
