package com.zerodsoft.scheduleweather.CalendarView.Week;

import android.app.Activity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.CalendarFragment.WeekFragment;
import com.zerodsoft.scheduleweather.CalendarView.HoursView;
import com.zerodsoft.scheduleweather.R;

import java.util.Calendar;


public class WeekViewPagerAdapter extends RecyclerView.Adapter<WeekViewPagerAdapter.WeekViewPagerHolder> implements WeekView.OnRefreshChildViewListener
{
    public static final String TAG = "WEEKVIEWPAGER_ADAPTER";
    public static final int WEEK_TOTAL_COUNT = 521;
    public static final int FIRST_VIEW_NUMBER = 261;

    private Activity activity;
    private SparseArray<WeekView> weekViewSparseArray = new SparseArray<>();
    private SparseArray<WeekHeaderView> headerViewSparseArray = new SparseArray<>();

    private int lastPosition = FIRST_VIEW_NUMBER;
    private Calendar today = Calendar.getInstance();

    public WeekViewPagerAdapter(Activity activity)
    {
        this.activity = activity;
    }

    class WeekViewPagerHolder extends RecyclerView.ViewHolder
    {
        private WeekView weekView;
        private RelativeLayout weekDatesLayout;
        private TextView weekDatesTextView;
        private ImageButton weekDatesButton;
        private HoursView hoursView;
        private WeekHeaderView weekHeaderView;

        private LinearLayout weekLayout;
        private LinearLayout headerLayout;
        private LinearLayout contentLayout;

        private int viewPosition;
        private Calendar calendar;

        public WeekViewPagerHolder(View view)
        {
            super(view);
            this.weekLayout = (LinearLayout) view.findViewById(R.id.week_layout);
            this.headerLayout = (LinearLayout) view.findViewById(R.id.week_header_layout);
            this.contentLayout = (LinearLayout) view.findViewById(R.id.week_content_layout);
            this.weekDatesLayout = (RelativeLayout) view.findViewById(R.id.week_dates_layout);
            this.hoursView = (HoursView) view.findViewById(R.id.week_hours_view);
            this.weekHeaderView = (WeekHeaderView) view.findViewById(R.id.week_header_view);
            this.weekDatesTextView = (TextView) view.findViewById(R.id.week_dates_textview);
            this.weekDatesButton = (ImageButton) view.findViewById(R.id.week_dates_button);
            this.weekView = (WeekView) view.findViewById(R.id.week_view);

            weekDatesLayout.setLayoutParams(new LinearLayout.LayoutParams(WeekFragment.SPACING_BETWEEN_DAY, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        public void onBindView(int position)
        {
            this.viewPosition = position;

            setWeekDates();
            weekHeaderView.setPosition(viewPosition);
            weekView.setPosition(viewPosition).setCoordinateInfoInterface(weekHeaderView).setOnRefreshHoursViewListener(hoursView);
        }

        private void setWeekDates()
        {
            calendar = (Calendar) today.clone();
            calendar.add(Calendar.WEEK_OF_YEAR, viewPosition - FIRST_VIEW_NUMBER);
            weekDatesTextView.setText(Integer.toString(calendar.get(Calendar.WEEK_OF_YEAR)) + "주");
        }

        public int getViewPosition()
        {
            return viewPosition;
        }
    }

    @NonNull
    @Override
    public WeekViewPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new WeekViewPagerHolder(LayoutInflater.from(activity).inflate(R.layout.weekview_viewpager_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WeekViewPagerHolder holder, int position)
    {
        //Log.e(TAG, "onBindViewHolder : " + Integer.toString(position));
        holder.onBindView(position);
        //  weekViewSparseArray.put(position, weekView);
        //  headerViewSparseArray.put(position, weekHeaderView);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull WeekViewPagerHolder holder)
    {
        Log.e(TAG, "onViewAttachedToWindow : " + holder.getViewPosition());
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull WeekViewPagerHolder holder)
    {
        // weekViewSparseArray.remove(holder.getViewPosition());
        // headerViewSparseArray.remove(holder.getViewPosition());
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount()
    {
        return WeekViewPagerAdapter.WEEK_TOTAL_COUNT;
    }


    @Override
    public void refreshChildView(int position)
    {
        try
        {
            weekViewSparseArray.get(position - 1).invalidate();
            weekViewSparseArray.get(position + 1).invalidate();
        } catch (NullPointerException e)
        {

        }
    }


    public int getEventRowNum(int position)
    {
        return headerViewSparseArray.get(position).getEventRowNum();
    }
}
