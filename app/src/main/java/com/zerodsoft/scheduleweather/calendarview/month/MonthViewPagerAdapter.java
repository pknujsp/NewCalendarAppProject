package com.zerodsoft.scheduleweather.calendarview.month;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.AppMainActivity;
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
    public static int CELL_HEIGHT;

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
        Log.e(getClass().getSimpleName(), "onBindViewHolder : " + position);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MonthViewHolder holder)
    {
        holder.onBind(holder.getAdapterPosition());
        holderSparseArray.put(holder.getAdapterPosition(), holder);
        Log.e(getClass().getSimpleName(), "onViewAttachedToWindow : " + holder.getAdapterPosition());
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MonthViewHolder holder)
    {
        super.onViewDetachedFromWindow(holder);
        Log.e(getClass().getSimpleName(), "onViewDetachedFromWindow : " + holder.getAdapterPosition());
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
        private LinearLayout header;

        public MonthViewHolder(View view)
        {
            super(view);
            gridView = (GridView) view.findViewById(R.id.monthview);
            header = (LinearLayout) view.findViewById(R.id.month_header_days);
            header.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
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

            monthFragment.setMonth(copiedCalendar.getTime());
            adapter = new MonthViewAdapter(context, copiedCalendar, gridView);
            gridView.setAdapter(adapter);
            monthFragment.requestSchedules(position, adapter.getDay(MonthViewAdapter.FIRST_DAY), adapter.getDay(MonthViewAdapter.LAST_DAY));
        }

        ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                CELL_HEIGHT = gridView.getHeight() / 6;
                //리스너 제거 (해당 뷰의 상태가 변할때 마다 호출되므로)
                removeOnGlobalLayoutListener(header.getViewTreeObserver(), mGlobalLayoutListener);
            }
        };


        private void removeOnGlobalLayoutListener(ViewTreeObserver observer, ViewTreeObserver.OnGlobalLayoutListener listener)
        {
            if (observer == null)
            {
                return;
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            {
                observer.removeGlobalOnLayoutListener(listener);
            } else
            {
                observer.removeOnGlobalLayoutListener(listener);
            }
        }
    }
}
