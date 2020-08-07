package com.zerodsoft.scheduleweather.CalendarView.Week;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.CalendarFragment.WeekFragment;
import com.zerodsoft.scheduleweather.CalendarView.HoursView;
import com.zerodsoft.scheduleweather.R;


public class WeekViewPagerAdapter extends RecyclerView.Adapter<WeekViewPagerAdapter.WeekViewPagerHolder> implements WeekView.OnRefreshChildViewListener
{
    public static final String TAG = "WEEKVIEWPAGER_ADAPTER";
    private Context context;
    private HoursView hoursView;
    private SparseArray<WeekView> weekViewSparseArray = new SparseArray<>();
    private SparseArray<WeekHeaderView> headerViewSparseArray = new SparseArray<>();

    public WeekViewPagerAdapter()
    {

    }

    public WeekViewPagerAdapter(Context context, HoursView hoursView)
    {
        this.context = context;
        this.hoursView = hoursView;
    }

    class WeekViewPagerHolder extends RecyclerView.ViewHolder
    {
        private View rootView;
        private WeekHeaderView headerView;
        private WeekView weekView;
        private int viewPosition;

        public WeekViewPagerHolder(View view)
        {
            super(view);
            this.rootView = view;
        }

        public void onBindView(WeekHeaderView headerView, WeekView weekView, int position)
        {
            this.headerView = headerView;
            this.weekView = weekView;
            this.viewPosition = position;
        }

        public View getRootView()
        {
            return rootView;
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
        return new WeekViewPagerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.weekview_viewpager_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WeekViewPagerHolder holder, int position)
    {
        View rootView = holder.getRootView();
        WeekHeaderView headerView = (WeekHeaderView) rootView.findViewById(R.id.week_header_view);
        WeekView weekView = (WeekView) rootView.findViewById(R.id.weekview);

        headerView.setPosition(position);
        weekView.setPosition(position).setCoordinateInfoInterface(headerView).setOnRefreshChildViewListener(this).setOnRefreshHoursViewListener(hoursView);

        holder.onBindView(headerView, weekView, position);

        weekViewSparseArray.put(position, weekView);
        headerViewSparseArray.put(position, headerView);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull WeekViewPagerHolder holder)
    {
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
        return WeekFragment.WEEK_TOTAL_COUNT;
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
