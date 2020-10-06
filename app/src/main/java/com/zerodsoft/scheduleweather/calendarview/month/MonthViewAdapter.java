package com.zerodsoft.scheduleweather.calendarview.month;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarfragment.MonthFragment;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.utility.AppSettings;
import com.zerodsoft.scheduleweather.utility.Clock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

class MonthViewAdapter extends BaseAdapter
{
    private static final int TOTAL_DAY_COUNT = 42;
    public static final int FIRST_DAY = -1;
    public static final int LAST_DAY = -2;

    private List<ScheduleDTO> schedulesList = new ArrayList<>();
    private Calendar calendar;
    private LayoutInflater layoutInflater;
    private Context context;

    private Date[] previousMonthDays;
    private Date[] currentMonthDays;
    private Date[] nextMonthDays;
    private Date endDay;

    private GridView gridView;

    public MonthViewAdapter(Context context, Calendar calendar, GridView gridView)
    {
        this.context = context;
        this.calendar = calendar;
        this.gridView = gridView;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setDays();
    }

    public void setSchedulesList(List<ScheduleDTO> schedulesList)
    {
        this.schedulesList = schedulesList;
    }

    @Override
    public int getCount()
    {
        return TOTAL_DAY_COUNT;
    }

    @Override
    public Object getItem(int i)
    {
        return null;
    }

    @Override
    public long getItemId(int i)
    {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup)
    {
        view = layoutInflater.inflate(R.layout.month_event_cell, viewGroup, false);

        // 뷰의 높이를 화면의 1/6로 설정
        view.getLayoutParams().height = MonthViewPagerAdapter.CELL_HEIGHT;

        TextView day = (TextView) view.findViewById(R.id.day_of_month);
        LinearLayout eventsList = (LinearLayout) view.findViewById(R.id.month_event_list);
        Date currentDate = getDay(position);

        int dayTextColor = 0;
        if (currentDate.before(currentMonthDays[0]) || currentDate.after(currentMonthDays[currentMonthDays.length - 1]))
        {
            dayTextColor = Color.GRAY;
        } else
        {
            dayTextColor = Color.BLACK;
        }

        // 날짜 설정
        day.setText(Clock.DAY_OF_MONTH_FORMAT.format(currentDate));
        day.setTextColor(dayTextColor);

        if (!schedulesList.isEmpty())
        {
            // 이벤트가 있는 경우에 수행
            Date nextDate = getDay(position + 1);
            // 이벤트 리스트 설정
            List<ScheduleDTO> schedules = getSchedulesList(currentDate, nextDate);
            int count = 0;

            for (ScheduleDTO schedule : schedules)
            {
                TextView textView = new TextView(context);
                textView.setTextSize(12);
                textView.setMaxLines(1);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.topMargin = 2;

                if (count == 2)
                {
                    textView.setText("추가");
                    textView.setBackgroundColor(Color.GREEN);
                    textView.setTextColor(Color.BLACK);

                    layoutParams.leftMargin = 2;
                    layoutParams.rightMargin = 2;
                } else
                {
                    textView.setText(schedule.getSubject());
                    textView.setBackgroundColor(schedule.getCategory() == ScheduleDTO.LOCAL_CATEGORY ? AppSettings.getLocalEventBackgroundColor() : AppSettings.getGoogleEventBackgroundColor());
                    textView.setTextColor(schedule.getCategory() == ScheduleDTO.LOCAL_CATEGORY ? AppSettings.getLocalEventTextColor() : AppSettings.getGoogleEventTextColor());

                    // 시작/종료일이 date가 아니나, 일정에 포함되는 경우
                    if (schedule.getStartDate().before(currentDate) && schedule.getEndDate().after(nextDate))
                    {
                        layoutParams.leftMargin = 0;
                        layoutParams.rightMargin = 0;
                    }
                    // 시작일이 date인 경우
                    else if ((schedule.getEndDate().compareTo(currentDate) >= 0 && schedule.getEndDate().before(nextDate)) && schedule.getEndDate().after(nextDate))
                    {
                        layoutParams.leftMargin = 2;
                        layoutParams.rightMargin = 0;
                    }
                    // 종료일이 date인 경우
                    else if ((schedule.getEndDate().compareTo(currentDate) >= 0 && schedule.getEndDate().before(nextDate)) && schedule.getStartDate().before(currentDate))
                    {
                        layoutParams.leftMargin = 0;
                        layoutParams.rightMargin = 2;
                    }
                    // 시작/종료일이 date인 경우
                    else if ((schedule.getEndDate().compareTo(currentDate) >= 0 && schedule.getEndDate().before(nextDate)) && (schedule.getStartDate().compareTo(currentDate) >= 0 && schedule.getStartDate().before(nextDate)))
                    {
                        layoutParams.leftMargin = 2;
                        layoutParams.rightMargin = 2;
                    }
                }

                textView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Toast.makeText(context, textView.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                textView.setLayoutParams(layoutParams);
                eventsList.addView(textView);
                count++;

                if (count == 3)
                {
                    break;
                }
            }
        }
        return view;
    }

    private void setDays()
    {
        // 일요일 부터 토요일까지
        // 이번 달이 2020/10인 경우 1일이 목요일이므로, 그리드 뷰는 9/27 일요일 부터 시작하고
        // 10/31 토요일에 종료
        // SUNDAY : 1, SATURDAY : 7  (getFirstDayOfWeek)
        // 다음 달 일수 계산법 : 42 - 이번 달 - 이전 달

        int previousCount = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int currentCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int nextCount = TOTAL_DAY_COUNT - currentCount - previousCount;

        previousMonthDays = new Date[previousCount];
        currentMonthDays = new Date[currentCount];
        nextMonthDays = new Date[nextCount];

        // 이전 달 일수 만큼 이동 ex) 20201001에서 20200927로 이동
        calendar.add(Calendar.DATE, -previousCount);

        for (int i = 0; i < previousCount; i++)
        {
            previousMonthDays[i] = calendar.getTime();
            calendar.add(Calendar.DATE, 1);
        }

        for (int i = 0; i < currentCount; i++)
        {
            currentMonthDays[i] = calendar.getTime();
            calendar.add(Calendar.DATE, 1);
        }

        for (int i = 0; i < nextCount; i++)
        {
            nextMonthDays[i] = calendar.getTime();
            calendar.add(Calendar.DATE, 1);
        }

        endDay = calendar.getTime();
    }

    public Date getDay(int position)
    {
        if (position == FIRST_DAY)
        {
            if (previousMonthDays.length > 0)
            {
                return previousMonthDays[0];
            } else
            {
                return currentMonthDays[0];
            }
        } else if (position == LAST_DAY)
        {
            return endDay;
        } else if (position < previousMonthDays.length)
        {
            return previousMonthDays[position];
        } else if (position < currentMonthDays.length + previousMonthDays.length)
        {
            return currentMonthDays[position - previousMonthDays.length];
        } else if (position < TOTAL_DAY_COUNT)
        {
            return nextMonthDays[position - currentMonthDays.length - previousMonthDays.length];
        } else if (position == TOTAL_DAY_COUNT + 1)
        {
            return endDay;
        } else
        {
            return null;
        }
    }

    private List<ScheduleDTO> getSchedulesList(Date startDate, Date endDate)
    {
        /*
        (Datetime(start_date) >= Datetime(:startDate) AND Datetime(start_date) < Datetime(:endDate))
        OR
        (Datetime(end_date) >= Datetime(:startDate) AND Datetime(end_date) < Datetime(:endDate))
        OR
        (Datetime(start_date) < Datetime(:startDate) AND Datetime(end_date) > Datetime(:endDate))
         */
        List<ScheduleDTO> schedules = new ArrayList<>();

        for (ScheduleDTO schedule : schedulesList)
        {
            // 종료일이 date인 경우
            if (schedule.getEndDate().compareTo(startDate) >= 0 && schedule.getEndDate().before(endDate))
            {
                schedules.add(schedule);
            }
            // 시작일이 date인 경우
            if (schedule.getStartDate().compareTo(startDate) >= 0 && schedule.getStartDate().before(endDate))
            {
                schedules.add(schedule);
            }
            // 시작/종료일이 date가 아니나, 일정에 포함되는 경우
            if (schedule.getStartDate().before(startDate) && schedule.getEndDate().after(endDate))
            {
                schedules.add(schedule);
            }
        }

        return schedules;
    }

}
