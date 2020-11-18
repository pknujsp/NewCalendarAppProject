package com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.SunSetRiseData;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.WeatherData;
import com.zerodsoft.scheduleweather.scheduleinfo.weatherfragments.resultdata.responseresult.VilageFcstData;
import com.zerodsoft.scheduleweather.utility.Clock;
import com.zerodsoft.scheduleweather.utility.WeatherDataConverter;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class VilageFcstFragment extends Fragment
{
    private WeatherData weatherData;
    private List<SunSetRiseData> sunSetRiseDataList;
    private TableLayout table;
    private LinearLayout headerCol;

    public VilageFcstFragment()
    {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.vilage_fcst_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        table = (TableLayout) view.findViewById(R.id.vilage_fcst_table);
        headerCol = (LinearLayout) view.findViewById(R.id.vilage_fcst_header_col);
    }

    public void setWeatherData(WeatherData weatherData, List<SunSetRiseData> sunSetRiseDataList)
    {
        this.weatherData = weatherData;
        this.sunSetRiseDataList = sunSetRiseDataList;
        setTable();
    }

    private void setTable()
    {
        Context context = getContext();
        final int ITEM_WIDTH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, getResources().getDisplayMetrics());
        final int MARGIN = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics());
        final int DP22 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22f, getResources().getDisplayMetrics());
        final int DP34 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 34f, getResources().getDisplayMetrics());
        final int DP80 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80f, getResources().getDisplayMetrics());

        List<VilageFcstData> dataList = weatherData.getVilageFcstFinalData().getData();
        final int DATA_SIZE = dataList.size();
        final int VIEW_WIDTH = DATA_SIZE * ITEM_WIDTH;

        //시각, 하늘, 기온, 강수량, 강수확률, 바람, 습도 순으로 행 등록
        TableRow clockRow = new TableRow(context);
        SkyView skyRow = new SkyView(context, dataList);
        TempView tempRow = new TempView(context, dataList);
        RainfallView rainfallRow = new RainfallView(context, dataList);
        TableRow chanceOfShowerRow = new TableRow(context);
        TableRow windRow = new TableRow(context);
        TableRow humidityRow = new TableRow(context);

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

        LinearLayout.LayoutParams clockLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP34);
        clockLabelParams.topMargin = MARGIN;
        clockLabelParams.bottomMargin = MARGIN;
        LinearLayout.LayoutParams skyLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP22);
        skyLabelParams.topMargin = MARGIN;
        skyLabelParams.bottomMargin = MARGIN;
        LinearLayout.LayoutParams tempLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP80);
        tempLabelParams.topMargin = MARGIN;
        tempLabelParams.bottomMargin = MARGIN;
        LinearLayout.LayoutParams rainfallLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP34);
        rainfallLabelParams.topMargin = MARGIN;
        rainfallLabelParams.bottomMargin = MARGIN;
        LinearLayout.LayoutParams chanceOfShowerLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP34);
        chanceOfShowerLabelParams.topMargin = MARGIN;
        chanceOfShowerLabelParams.bottomMargin = MARGIN;
        LinearLayout.LayoutParams windLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP34);
        windLabelParams.topMargin = MARGIN;
        windLabelParams.bottomMargin = MARGIN;
        LinearLayout.LayoutParams humidityLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP34);
        humidityLabelParams.topMargin = MARGIN;
        humidityLabelParams.bottomMargin = MARGIN;

        clockLabelParams.gravity = Gravity.CENTER;
        skyLabelParams.gravity = Gravity.CENTER;
        tempLabelParams.gravity = Gravity.CENTER;
        rainfallLabelParams.gravity = Gravity.CENTER;
        chanceOfShowerLabelParams.gravity = Gravity.CENTER;
        windLabelParams.gravity = Gravity.CENTER;
        humidityLabelParams.gravity = Gravity.CENTER;

        headerCol.addView(clockLabel, clockLabelParams);
        headerCol.addView(skyLabel, skyLabelParams);
        headerCol.addView(tempLabel, tempLabelParams);
        headerCol.addView(rainfallLabel, rainfallLabelParams);
        headerCol.addView(chanceOfShowerLabel, chanceOfShowerLabelParams);
        headerCol.addView(windLabel, windLabelParams);
        headerCol.addView(humidityLabel, humidityLabelParams);

        //시각 --------------------------------------------------------------------------
        Calendar date = Calendar.getInstance();

        for (int col = 0; col < DATA_SIZE; col++)
        {
            TextView textView = new TextView(context);
            date.setTime(dataList.get(col).getDateTime());

            if (date.get(Calendar.HOUR_OF_DAY) == 0 || col == 0)
            {
                setValueTextView(textView, Clock.MdE_FORMAT.format(date.getTime()) + "\n" + Integer.toString(date.get(Calendar.HOUR_OF_DAY)));
            } else
            {
                setValueTextView(textView, Integer.toString(date.get(Calendar.HOUR_OF_DAY)));
            }

            TableRow.LayoutParams textParams = new TableRow.LayoutParams(ITEM_WIDTH, DP34);
            textParams.gravity = Gravity.CENTER;
            clockRow.addView(textView, textParams);
        }

        //하늘 ---------------------------------------------------------------------------
        skyRow.measure(VIEW_WIDTH, DP22);

        //기온 ------------------------------------------------------------------------------
        tempRow.measure(VIEW_WIDTH, DP80);

        //강수량 ------------------------------------------------------------------------------
        rainfallRow.measure(VIEW_WIDTH, DP34);

        //강수확률 ------------------------------------------------------------------------------
        for (int col = 0; col < DATA_SIZE; col++)
        {
            TextView textView = new TextView(context);
            setValueTextView(textView, dataList.get(col).getChanceOfShower());

            TableRow.LayoutParams textParams = new TableRow.LayoutParams(ITEM_WIDTH, DP34);
            textParams.gravity = Gravity.CENTER;
            chanceOfShowerRow.addView(textView, textParams);
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
        TableLayout.LayoutParams skyRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, DP22);
        skyRowParams.topMargin = MARGIN;
        skyRowParams.bottomMargin = MARGIN;
        TableLayout.LayoutParams tempRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, DP80);
        tempRowParams.topMargin = MARGIN;
        tempRowParams.bottomMargin = MARGIN;
        TableLayout.LayoutParams rainfallRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, DP34);
        rainfallRowParams.topMargin = MARGIN;
        rainfallRowParams.bottomMargin = MARGIN;
        TableLayout.LayoutParams chanceOfShowerRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
        chanceOfShowerRowParams.topMargin = MARGIN;
        chanceOfShowerRowParams.bottomMargin = MARGIN;
        TableLayout.LayoutParams windRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
        windRowParams.topMargin = MARGIN;
        windRowParams.bottomMargin = MARGIN;
        TableLayout.LayoutParams humidityRowParams = new TableLayout.LayoutParams(VIEW_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
        humidityRowParams.topMargin = MARGIN;
        humidityRowParams.bottomMargin = MARGIN;

        table.addView(clockRow, clockRowParams);
        table.addView(skyRow, skyRowParams);
        table.addView(tempRow, tempRowParams);
        table.addView(rainfallRow, rainfallRowParams);
        table.addView(chanceOfShowerRow, chanceOfShowerRowParams);
        table.addView(windRow, windRowParams);
        table.addView(humidityRow, humidityRowParams);
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

    private Drawable getSkyImage(VilageFcstData data)
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

    class SkyView extends View
    {
        private List<Drawable> skyImageList;

        public SkyView(Context context, List<VilageFcstData> dataList)
        {
            super(context);

            skyImageList = new LinkedList<>();
            for (int i = 0; i < dataList.size() - 1; i++)
            {
                skyImageList.add(getSkyImage(dataList.get(i)));
            }

            setWillNotDraw(false);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
        {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom)
        {
            super.onLayout(changed, left, top, right, bottom);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);
            drawImages(canvas);
        }

        private void drawImages(Canvas canvas)
        {
            final int COLUMN_WIDTH = getWidth() / (skyImageList.size() + 1);
            final int RADIUS = getHeight() / 2;
            final int TOP = 0;
            final int BOTTOM = getHeight();
            final int LEFT = COLUMN_WIDTH - RADIUS;
            final int RIGHT = COLUMN_WIDTH + RADIUS;
            final Rect RECT = new Rect(LEFT, TOP, RIGHT, BOTTOM);

            int index = 0;
            for (Drawable image : skyImageList)
            {
                image.setBounds(RECT);
                image.draw(canvas);

                RECT.offset(COLUMN_WIDTH, 0);
                index++;
            }
        }
    }

    class TempView extends View
    {
        private List<String> tempList;
        private final float MAX_TEMP;
        private final float MIN_TEMP;
        private final TextPaint TEMP_PAINT;
        private final Paint LINE_PAINT;
        private final Paint CIRCLE_PAINT;

        public TempView(Context context, List<VilageFcstData> dataList)
        {
            super(context);
            TEMP_PAINT = new TextPaint();
            TEMP_PAINT.setTextAlign(Paint.Align.CENTER);
            TEMP_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, getResources().getDisplayMetrics()));
            TEMP_PAINT.setColor(Color.BLACK);

            LINE_PAINT = new Paint();
            LINE_PAINT.setAntiAlias(true);
            LINE_PAINT.setColor(Color.GRAY);
            LINE_PAINT.setStyle(Paint.Style.FILL);
            LINE_PAINT.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, getResources().getDisplayMetrics()));

            CIRCLE_PAINT = new Paint();
            CIRCLE_PAINT.setAntiAlias(true);
            CIRCLE_PAINT.setColor(Color.GRAY);
            CIRCLE_PAINT.setStyle(Paint.Style.FILL);

            tempList = new LinkedList<>();

            float max = Float.MIN_VALUE;
            float min = Float.MAX_VALUE;
            float temp = 0f;

            for (VilageFcstData data : dataList)
            {
                temp = Float.parseFloat(data.getTemp3Hour());
                tempList.add(data.getTemp3Hour());

                // 최대,최소 기온 구하기
                if (temp > max)
                {
                    max = temp;
                } else if (temp < min)
                {
                    min = temp;
                }
            }
            MAX_TEMP = max;
            MIN_TEMP = min;

            setWillNotDraw(false);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
        {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b)
        {
            super.onLayout(changed, l, t, r, b);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);
            drawGraph(canvas);
        }

        private void drawGraph(Canvas canvas)
        {
            // 텍스트의 높이+원의 반지름 만큼 뷰의 상/하단에 여백을 설정한다.
            final float TEXT_HEIGHT = TEMP_PAINT.descent() - TEMP_PAINT.ascent();
            final float RADIUS = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics());

            final float VIEW_WIDTH = getWidth();
            final float VIEW_HEIGHT = getHeight() - ((TEXT_HEIGHT + RADIUS) * 2);
            final float COLUMN_WIDTH = VIEW_WIDTH / tempList.size();
            final float SPACING = ((VIEW_HEIGHT) / (MAX_TEMP - MIN_TEMP)) / 10f;

            float temp = 0f;
            float x = 0f;
            float y = 0f;

            PointF lastColumnPoint = new PointF();

            int index = 0;
            for (String value : tempList)
            {
                temp = Float.parseFloat(value);
                x = COLUMN_WIDTH / 2f + COLUMN_WIDTH * index;
                y = (10f * (MAX_TEMP - temp)) * SPACING + TEXT_HEIGHT + RADIUS;
                canvas.drawCircle(x, y, RADIUS, CIRCLE_PAINT);
                canvas.drawText(value, x, y + RADIUS + TEXT_HEIGHT, TEMP_PAINT);

                if (index != 0)
                {
                    canvas.drawLine(lastColumnPoint.x, lastColumnPoint.y, x, y, LINE_PAINT);
                }

                lastColumnPoint.set(x, y);
                index++;
            }
        }


    }

    class RainfallView extends View
    {
        List<String> rainfallList;
        final int COLUMN_SIZE;
        final TextPaint VALUE_PAINT;
        final Paint RECT_PAINT;

        public RainfallView(Context context, List<VilageFcstData> dataList)
        {
            super(context);

            COLUMN_SIZE = dataList.size();
            rainfallList = new LinkedList<>();
            for (VilageFcstData data : dataList)
            {
                if (data.getRainPrecipitation6Hour() != null)
                {
                    rainfallList.add(data.getRainPrecipitation6Hour());
                }
            }

            VALUE_PAINT = new TextPaint();
            VALUE_PAINT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13f, getResources().getDisplayMetrics()));
            VALUE_PAINT.setColor(Color.BLACK);
            VALUE_PAINT.setTextAlign(Paint.Align.CENTER);

            RECT_PAINT = new Paint();
            RECT_PAINT.setStyle(Paint.Style.FILL);
            RECT_PAINT.setColor(Color.GRAY);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
        {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom)
        {
            super.onLayout(changed, left, top, right, bottom);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);
            drawView(canvas);
        }

        private void drawView(Canvas canvas)
        {
            final float PADDING = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics());
            final float MARGIN_LR = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics());
            final int COLUMN_WIDTH = getWidth() / COLUMN_SIZE;
            final int COLUMN_WIDTH_HALF = COLUMN_WIDTH / 2;
            final float TOP = PADDING;
            final float BOTTOM = getHeight() - PADDING;
            float left = 0f;
            float right = 0f;
            float x = 0f;
            final float Y = (BOTTOM - TOP) / 2f + VALUE_PAINT.descent() + TOP;

            RectF rect = new RectF();
            rect.top = TOP;
            rect.bottom = BOTTOM;

            int index = 0;
            for (String value : rainfallList)
            {
                // 첫번째 자료는 발표시간+1시간 부터 발표시간+7시간 까지
                if (index == 0)
                {
                    left = 0;
                    right = COLUMN_WIDTH + COLUMN_WIDTH_HALF - MARGIN_LR;
                } else
                {
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
