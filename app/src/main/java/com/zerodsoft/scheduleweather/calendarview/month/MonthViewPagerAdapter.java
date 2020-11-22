package com.zerodsoft.scheduleweather.calendarview.month;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarfragment.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarfragment.MonthFragment;
import com.zerodsoft.scheduleweather.calendarfragment.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.DateGetter;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MonthViewPagerAdapter extends RecyclerView.Adapter<MonthViewPagerAdapter.MonthViewHolder> implements DateGetter
{
    public static final int TOTAL_DAY_COUNT = 42;
    public static final int FIRST_DAY = -1;
    public static final int LAST_DAY = -2;

    private final SparseArray<MonthViewHolder> holderSparseArray = new SparseArray<>();
    private final Calendar calendar;
    private MonthFragment monthFragment;
    private Context context;

    private OnEventItemClickListener onEventItemClickListener;

    public MonthViewPagerAdapter(MonthFragment monthFragment)
    {
        this.monthFragment = monthFragment;
        this.onEventItemClickListener = (OnEventItemClickListener) monthFragment;
        context = monthFragment.getContext();
        calendar = Calendar.getInstance(ClockUtil.TIME_ZONE);

        // 날짜를 이번 달 1일 0시 0분으로 설정
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        monthFragment.setMonth(calendar.getTime());
    }

    public Calendar getCalendar()
    {
        return (Calendar) calendar.clone();
    }

    public void setData(int position, List<ScheduleDTO> schedules)
    {
        holderSparseArray.get(position).setData(schedules);
    }

    @Override
    public Date getDate(int position, int dateType)
    {
        return holderSparseArray.get(position).getDay(dateType).getTime();
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
        holder.onBind();
        holderSparseArray.put(holder.getAdapterPosition(), holder);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MonthViewHolder holder)
    {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MonthViewHolder holder)
    {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(@NonNull MonthViewHolder holder)
    {
        holderSparseArray.remove(holder.getOldPosition());
        holder.clearHolder();
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount()
    {
        return Integer.MAX_VALUE;
    }

    public Date getMonth(int position)
    {
        return holderSparseArray.get(position).currentMonthDays[0].getTime();
    }

    class MonthViewHolder extends RecyclerView.ViewHolder
    {
        private MonthCalendarView monthCalendarView;

        private Calendar[] previousMonthDays;
        private Calendar[] currentMonthDays;
        private Calendar[] nextMonthDays;
        private Calendar endDay;

        public MonthViewHolder(View view)
        {
            super(view);
            monthCalendarView = (MonthCalendarView) view.findViewById(R.id.month_calendar_view);
        }

        public void setData(List<ScheduleDTO> schedulesList)
        {
            // 데이터를 일정 길이의 내림차순으로 정렬
            Collections.sort(schedulesList, comparator);

            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            List<EventData> list = new ArrayList<>();
            // 데이터를 일자별로 분류
            for (ScheduleDTO schedule : schedulesList)
            {
                startDate.setTime(schedule.getStartDate());
                endDate.setTime(schedule.getEndDate());
                list.add(new EventData(schedule, getDateToIndex(startDate), getDateToIndex(endDate)));
            }
            monthCalendarView.setSchedules(list);
        }

        public void clearHolder()
        {
            monthCalendarView.removeAllViews();
            monthCalendarView.clear();

            previousMonthDays = null;
            currentMonthDays = null;
            nextMonthDays = null;
            endDay = null;
        }

        public void onBind()
        {
            Calendar copiedCalendar = (Calendar) calendar.clone();
            copiedCalendar.add(Calendar.MONTH, getAdapterPosition() - EventTransactionFragment.FIRST_VIEW_POSITION);
            monthCalendarView.setCalendar(copiedCalendar);
            setDays(copiedCalendar);

            for (int i = 0; i < TOTAL_DAY_COUNT; i++)
            {
                Calendar currentDate = getDay(i);

                int dayTextColor = 0;
                if (currentDate.before(currentMonthDays[0]) || currentDate.after(currentMonthDays[currentMonthDays.length - 1]))
                {
                    dayTextColor = Color.GRAY;
                } else
                {
                    dayTextColor = Color.BLACK;
                }

                // 날짜 설정
                MonthCalendarItemView itemView = new MonthCalendarItemView(context, dayTextColor);
                itemView.setDate(currentDate.getTime(), getDay(i + 1).getTime());
                itemView.setClickable(true);
                itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        onEventItemClickListener.onClicked(itemView.getStartDate(), itemView.getEndDate());
                    }
                });
                monthCalendarView.addView(itemView);
            }
            monthCalendarView.invalidate();
            monthFragment.requestSchedules(getAdapterPosition(), getDay(FIRST_DAY).getTime(), getDay(LAST_DAY).getTime());
        }

        private void setDays(Calendar calendar)
        {
            // 일요일 부터 토요일까지
            // 이번 달이 2020/10인 경우 1일이 목요일이므로, 그리드 뷰는 9/27 일요일 부터 시작하고
            // 10/31 토요일에 종료
            // SUNDAY : 1, SATURDAY : 7  (getFirstDayOfWeek)
            // 다음 달 일수 계산법 : 42 - 이번 달 - 이전 달

            int previousCount = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            int currentCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            int nextCount = TOTAL_DAY_COUNT - currentCount - previousCount;

            previousMonthDays = new Calendar[previousCount];
            currentMonthDays = new Calendar[currentCount];
            nextMonthDays = new Calendar[nextCount];

            // 이전 달 일수 만큼 이동 ex) 20201001에서 20200927로 이동
            calendar.add(Calendar.DATE, -previousCount);

            for (int i = 0; i < previousCount; i++)
            {
                previousMonthDays[i] = (Calendar) calendar.clone();
                calendar.add(Calendar.DATE, 1);
            }

            for (int i = 0; i < currentCount; i++)
            {
                currentMonthDays[i] = (Calendar) calendar.clone();
                calendar.add(Calendar.DATE, 1);
            }

            for (int i = 0; i < nextCount; i++)
            {
                nextMonthDays[i] = (Calendar) calendar.clone();
                calendar.add(Calendar.DATE, 1);
            }

            endDay = (Calendar) calendar.clone();
        }

        public int getDateToIndex(Calendar date)
        {
            int index = 0;

            for (int i = 0; i < previousMonthDays.length; i++)
            {
                if (previousMonthDays[i].get(Calendar.YEAR) == date.get(Calendar.YEAR) && previousMonthDays[i].get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR))
                {
                    index = i;
                    return index;
                }
            }
            for (int i = 0; i < currentMonthDays.length; i++)
            {
                if (currentMonthDays[i].get(Calendar.YEAR) == date.get(Calendar.YEAR) && currentMonthDays[i].get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR))
                {
                    index = i + previousMonthDays.length;
                    return index;
                }
            }
            for (int i = 0; i < nextMonthDays.length; i++)
            {
                if (nextMonthDays[i].get(Calendar.YEAR) == date.get(Calendar.YEAR) && nextMonthDays[i].get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR))
                {
                    index = i + previousMonthDays.length + currentMonthDays.length;
                    return index;
                }
            }

            // 달력에 표시된 첫 날짜 이전 인 경우
            if (previousMonthDays.length > 0)
            {
                if (date.before(previousMonthDays[0]))
                {
                    return Integer.MIN_VALUE;
                }
            } else if (date.before(currentMonthDays[0]))
            {
                // 이전 달 날짜가 들어가지 않을 때
                return Integer.MIN_VALUE;
            }

            // 달력에 표시된 마지막 날짜 이후 인 경우
            if (date.compareTo(endDay) >= 0)
            {
                return Integer.MAX_VALUE;
            }
            return -1;
        }

        public Calendar getDay(int position)
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
            } else if (position == TOTAL_DAY_COUNT)
            {
                return endDay;
            } else
            {
                return null;
            }
        }


        /*
        ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                // CELL_HEIGHT = gridView.getHeight() / 6;
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
         */
    }


    private final Comparator<ScheduleDTO> comparator = new Comparator<ScheduleDTO>()
    {
        @Override
        public int compare(ScheduleDTO t1, ScheduleDTO t2)
        {
            /*
            음수 또는 0이면 객체의 자리가 그대로 유지되며, 양수인 경우에는 두 객체의 자리가 변경된다.
             */
            if ((t1.getEndDate().getTime() - t1.getStartDate().getTime()) < (t2.getEndDate().getTime() - t2.getStartDate().getTime()))
            {
                return 1;
            } else
            {
                return 0;
            }
        }
    };
}
