package com.zerodsoft.scheduleweather.calendarview.assistantcalendar.monthlistassistant;

import android.content.ContentValues;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.assistantcalendar.monthassistant.MonthAssistantCalendarView;
import com.zerodsoft.scheduleweather.calendarview.callback.EventCallback;
import com.zerodsoft.scheduleweather.calendarview.interfaces.CalendarDateOnClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.month.MonthCalendarView;
import com.zerodsoft.scheduleweather.calendarview.month.MonthViewPagerAdapter;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MonthListAssistantCalendarAdapter extends RecyclerView.Adapter<MonthListAssistantCalendarAdapter.MonthListAssistantViewHolder>
{
    private static final Calendar CALENDAR = Calendar.getInstance(ClockUtil.TIME_ZONE);
    // 1910년 - 2100년
    public static final int TOTAL_COUNT = 191;
    public final int FIRST_POSITION;

    private final Date TODAY;
    private final IControlEvent iControlEvent;
    private final CalendarDateOnClickListener calendarDateOnClickListener;

    public MonthListAssistantCalendarAdapter(IControlEvent iControlEvent, CalendarDateOnClickListener calendarDateOnClickListener)
    {
        this.iControlEvent = iControlEvent;
        this.calendarDateOnClickListener = calendarDateOnClickListener;
        this.TODAY = CALENDAR.getTime();

        // 날짜를 이번 달 1일 0시 0분으로 설정
        CALENDAR.set(Calendar.YEAR, 1910);
        CALENDAR.set(Calendar.DAY_OF_MONTH, 1);
        CALENDAR.set(Calendar.HOUR_OF_DAY, 0);
        CALENDAR.set(Calendar.MINUTE, 0);
        CALENDAR.set(Calendar.SECOND, 0);

        FIRST_POSITION = CALENDAR.get(Calendar.YEAR) - 1910;
    }

    @NonNull
    @Override
    public MonthListAssistantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new MonthListAssistantViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.monthlist_assistant_recyclerview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MonthListAssistantViewHolder holder, int position)
    {
        holder.onBind();
    }

    @Override
    public int getItemCount()
    {
        return TOTAL_COUNT;
    }

    class MonthListAssistantViewHolder extends RecyclerView.ViewHolder
    {
        private final TableRow tableRow1;
        private final TableRow tableRow2;

        public MonthListAssistantViewHolder(@NonNull View itemView)
        {
            super(itemView);
            tableRow1 = (TableRow) itemView.findViewById(R.id.month_table_row1);
            tableRow2 = (TableRow) itemView.findViewById(R.id.month_table_row2);
        }

        public void onBind()
        {
            Calendar copiedCalendar = (Calendar) CALENDAR.clone();
            copiedCalendar.add(Calendar.YEAR, getAdapterPosition());

            int year = copiedCalendar.get(Calendar.YEAR);
            ((TextView) itemView.findViewById(R.id.year)).setText(String.valueOf(year));

            TableRow[] tableRows = {tableRow1, tableRow2};

            for (int row = 0; row <= 1; row++)
            {
                for (int column = 0; column < 6; column++)
                {
                    copiedCalendar.add(Calendar.MONTH, 1);
                    View child = tableRows[row].getChildAt(column);
                    ((TextView) child.findViewById(R.id.month_of_year)).setText(String.valueOf(copiedCalendar.get(Calendar.MONTH) + 1));
                    //  ((TextView) child.findViewById(R.id.instance_count_of_month)).setText(String.valueOf(month++));

                    child.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            calendarDateOnClickListener.onClickedMonth(copiedCalendar.getTime());
                        }
                    });
                }
            }
            copiedCalendar.set(Calendar.MONTH, 0);

            /*
            long begin = copiedCalendar.getTimeInMillis();
            copiedCalendar.add(Calendar.YEAR, 1);
            long end = copiedCalendar.getTimeInMillis();
            iControlEvent.getInstances(getAdapterPosition(), begin, end, new EventCallback<List<CalendarInstance>>()
            {
                @Override
                public void onResult(List<CalendarInstance> e)
                {
                    setResult(e);
                }
            });

             */
        }

        public void setResult(List<CalendarInstance> e)
        {
            List<ContentValues> instances = new ArrayList<>();
            // 인스턴스 목록 표시
            for (CalendarInstance calendarInstance : e)
            {
                instances.addAll(calendarInstance.getInstanceList());
            }

            final Date asOfDate = null;
            Map<Integer, Integer> countMap = new HashMap<>();

            // 인스턴스를 날짜 별로 분류(월별로 분류)
            for (ContentValues instance : instances)
            {
                Date beginDate = ClockUtil.instanceDateTimeToDate(instance.getAsLong(CalendarContract.Instances.BEGIN));
                Date endDate = ClockUtil.instanceDateTimeToDate(instance.getAsLong(CalendarContract.Instances.END), instance.getAsBoolean(CalendarContract.Instances.ALL_DAY));
                int beginIndex = ClockUtil.calcDayDifference(beginDate, asOfDate);
                int endIndex = ClockUtil.calcDayDifference(endDate, asOfDate);

                if (beginIndex < 0)
                {
                    beginIndex = 0;
                }
                if (endIndex > 11)
                {
                    endIndex = 11;
                }

                for (int index = beginIndex; index <= endIndex; index++)
                {
                    if (!countMap.containsKey(index))
                    {
                        countMap.put(index, 0);
                    }
                    countMap.put(index, countMap.get(index) + 1);
                }
            }


            TableRow[] tableRows = {tableRow1, tableRow2};
            for (int row = 0; row <= 1; row++)
            {
                for (int column = 0; column < 6; column++)
                {
                    //  ((TextView) tableRows[row].getChildAt(column).findViewById(R.id.instance_count_of_month)).setText(String.valueOf(month++));
                }
            }
        }
    }
}
