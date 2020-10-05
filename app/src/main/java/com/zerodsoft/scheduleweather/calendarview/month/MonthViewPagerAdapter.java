package com.zerodsoft.scheduleweather.calendarview.month;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarfragment.CalendarTransactionFragment;
import com.zerodsoft.scheduleweather.calendarfragment.MonthFragment;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MonthViewPagerAdapter extends RecyclerView.Adapter<MonthViewPagerAdapter.MonthViewHolder>
{
    private SparseArray<MonthViewHolder> holderSparseArray = new SparseArray<>();
    private Calendar calendar;
    private MonthFragment monthFragment;
    private Context context;
    private int cellHeight;

    public MonthViewPagerAdapter(MonthFragment monthFragment)
    {
        this.monthFragment = monthFragment;
        context = monthFragment.getContext();
        calendar = Calendar.getInstance();

        // 날짜를 이번 달 1일 0시 0분으로 설정
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
    }

    public void setData(int position, List<ScheduleDTO> schedules)
    {
        holderSparseArray.get(position).setData(schedules);
    }

    @NonNull
    @Override
    public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new MonthViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.monthview_viewpager_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MonthViewHolder holder, int position)
    {
        holder.onBind(position);
        holderSparseArray.put(position, holder);
    }

    @Override
    public int getItemCount()
    {
        return Integer.MAX_VALUE;
    }

    class MonthViewHolder extends RecyclerView.ViewHolder
    {
        private int position;
        private MonthViewAdapter adapter;
        private GridView gridView;

        public MonthViewHolder(View view)
        {
            super(view);
            gridView = (GridView) view.findViewById(R.id.monthview);
        }

        public void setData(List<ScheduleDTO> schedulesList)
        {
            adapter.setSchedulesList(schedulesList);
            adapter.notifyDataSetChanged();
        }

        public void onBind(int position)
        {
            this.position = position;

            Calendar copiedCalendar = (Calendar) calendar.clone();
            copiedCalendar.add(Calendar.MONTH, position - CalendarTransactionFragment.FIRST_VIEW_POSITION);

            adapter = new MonthViewAdapter(context, copiedCalendar, cellHeight, gridView);
            gridView.setAdapter(adapter);
            monthFragment.requestSchedules(position, adapter.getDay(MonthViewAdapter.FIRST_DAY), adapter.getDay(MonthViewAdapter.LAST_DAY));
        }
    }
}
