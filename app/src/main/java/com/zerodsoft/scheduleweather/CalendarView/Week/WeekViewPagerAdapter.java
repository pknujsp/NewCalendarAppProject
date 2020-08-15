package com.zerodsoft.scheduleweather.CalendarView.Week;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.CalendarFragment.WeekFragment;
import com.zerodsoft.scheduleweather.CalendarView.AccountType;
import com.zerodsoft.scheduleweather.CalendarView.Dto.CoordinateInfo;
import com.zerodsoft.scheduleweather.CalendarView.EventDrawingInfo;
import com.zerodsoft.scheduleweather.CalendarView.HoursView;
import com.zerodsoft.scheduleweather.CalendarView.ViewModel.WeekViewModel;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Room.DTO.ScheduleDTO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WeekViewPagerAdapter extends RecyclerView.Adapter<WeekViewPagerAdapter.WeekViewPagerHolder> implements WeekView.OnRefreshChildViewListener
{
    public static final String TAG = "WEEKVIEWPAGER_ADAPTER";
    public static final int WEEK_TOTAL_COUNT = 521;
    public static final int FIRST_VIEW_NUMBER = 261;

    private Activity activity;
    private WeekFragment weekFragment;
    private SparseArray<WeekViewPagerHolder> pagerHolderSparseArr = new SparseArray<>();

    private Calendar today = Calendar.getInstance();

    private WeekViewModel weekViewModel;

    @Override
    public void refreshChildView(int position)
    {

    }

    class WeekViewPagerHolder extends RecyclerView.ViewHolder implements WeekHeaderView.ViewHeightChangeListener
    {
        public WeekView weekView;
        public TextView weekDatesTextView;
        public ImageButton weekDatesButton;
        public HoursView hoursView;
        public WeekHeaderView weekHeaderView;

        public LinearLayout weekLayout;
        public LinearLayout headerLayout;
        public LinearLayout datesLayout;
        public LinearLayout eventLayout;
        public LinearLayout contentLayout;
        public LinearLayout tableLayout;

        public int viewPosition;
        public Calendar calendar;

        public List<ScheduleDTO> schedules;

        public boolean[][] eventMatrix;
        public int rowNum;
        public List<EventDrawingInfo> eventDrawingInfoList;
        public CoordinateInfo[] coordinateInfos;

        public static final int EVENT_ROW_MAX = 20;

        public Date weekFirstDate;
        public Date weekLastDate;

        public WeekViewPagerHolder(View view)
        {
            super(view);
            this.weekLayout = (LinearLayout) view.findViewById(R.id.week_layout);
            this.headerLayout = (LinearLayout) view.findViewById(R.id.week_header_layout);
            this.contentLayout = (LinearLayout) view.findViewById(R.id.week_content_layout);
            this.datesLayout = (LinearLayout) view.findViewById(R.id.week_dates_layout);
            this.eventLayout = (LinearLayout) view.findViewById(R.id.week_event_layout);
            this.hoursView = (HoursView) view.findViewById(R.id.week_hours_view);
            this.weekHeaderView = (WeekHeaderView) view.findViewById(R.id.week_header_view);
            this.weekDatesTextView = (TextView) view.findViewById(R.id.week_dates_textview);
            this.weekDatesButton = (ImageButton) view.findViewById(R.id.week_dates_button);
            this.weekView = (WeekView) view.findViewById(R.id.week_view);
            this.tableLayout = (TableLayout) view.findViewById(R.id.week_header_event_table);

            this.weekHeaderView.setViewHeightChangeListener(WeekViewPagerHolder.this);
            datesLayout.setLayoutParams(new LinearLayout.LayoutParams(WeekFragment.SPACING_BETWEEN_DAY, ViewGroup.LayoutParams.WRAP_CONTENT));
            tableLayout.setVisibility(View.GONE);
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

        public void setEvents()
        {
            weekFirstDate = weekHeaderView.getWeekFirstDate();
            weekLastDate = weekHeaderView.getWeekLastDate();

            schedules = weekViewModel.selectSchedules(AccountType.LOCAL, weekFirstDate, weekLastDate).getValue();

            coordinateInfos = weekHeaderView.getArray();
            eventMatrix = new boolean[EVENT_ROW_MAX][7];
            eventDrawingInfoList = new ArrayList<>();

            if (schedules == null)
            {
                return;
            } else
            {
                setEventDrawingInfo();
                drawEvents();
            }
                        /*
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    weekFirstDayMillis = weekHeaderView.getWeekFirstDayMillis();
                    weekLastDayMillis = weekHeaderView.getWeekLastDayMillis();

                    schedules = weekViewModel.selectSchedules(AccountType.LOCAL, weekFirstDayMillis, weekLastDayMillis).getValue();

                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            coordinateInfos = weekHeaderView.getArray();
                            eventMatrix = new boolean[EVENT_ROW_MAX][7];
                            eventDrawingInfoList = new ArrayList<>();

                            if (schedules == null)
                            {
                                return;
                            } else
                            {
                                setEventDrawingInfo();
                                drawEvents();
                            }
                        }
                    });
                }
            }).start();                         */
        }

        public int getViewPosition()
        {
            return viewPosition;
        }

        @Override
        public void onHeightChanged(int height)
        {
            headerLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
            datesLayout.setLayoutParams(new LinearLayout.LayoutParams(WeekFragment.SPACING_BETWEEN_DAY, height));
        }


        private void setEventDrawingInfo()
        {
            for (ScheduleDTO schedule : schedules)
            {
                Map<String, Integer> map = calcEventPosition(schedule);
                if (map != null)
                {
                    if (schedule.getCategory() == ScheduleDTO.GOOGLE_CATEGORY)
                    {
                        eventDrawingInfoList.add(new EventDrawingInfo(map.get("startCol"), map.get("endCol"), map.get("row"), schedule, AccountType.GOOGLE));
                    } else
                    {
                        eventDrawingInfoList.add(new EventDrawingInfo(map.get("startCol"), map.get("endCol"), map.get("row"), schedule, AccountType.LOCAL));
                    }
                }
            }
        }

        private Map<String, Integer> calcEventPosition(ScheduleDTO schedule)
        {
            Date startDate = schedule.getStartDate();
            Date endDate = schedule.getEndDate();
            int startCol = 0;
            int endCol = 0;
            int row = 0;


            if (endDate.before(weekFirstDate) || startDate.after(weekLastDate))
            {
                return null;
            } else if (startDate.compareTo(weekFirstDate) >= 0 && endDate.compareTo(weekLastDate) <= 0)
            {
                // 이번주 내에 시작/종료
                for (int i = 6; i >= 0; --i)
                {
                    if (startDate.compareTo(coordinateInfos[i].getDate().getTime()) >= 0)
                    {
                        startCol = i;
                        break;
                    }
                }

                for (int i = 6; i >= 0; --i)
                {
                    if (endDate.compareTo(coordinateInfos[i].getDate().getTime()) >= 0)
                    {
                        endCol = i;
                        break;
                    }
                }

                RowLoop:
                for (; row < EVENT_ROW_MAX; row++)
                {
                    for (int col = startCol; col <= endCol; col++)
                    {
                        if (eventMatrix[row][col])
                        {
                            // false이면 추가
                            break;
                        } else if (col == endCol)
                        {
                            if (!eventMatrix[row][col])
                            {
                                break RowLoop;
                            }
                        }
                    }
                }

                for (int col = startCol; col <= endCol; col++)
                {
                    eventMatrix[row][col] = true;
                    // eventMatrix의 해당 부분이 false일 경우(등록된 데이터가 없음)에 추가가능
                }
            } else if (startDate.before(weekFirstDate) && endDate.compareTo(weekLastDate) <= 0)
            {
                // 이전 주 부터 시작되어 이번 주 중에 종료
                for (int i = 6; i >= 0; --i)
                {
                    if (endDate.compareTo(coordinateInfos[i].getDate().getTime()) >= 0)
                    {
                        endCol = i;
                        break;
                    }
                }

                RowLoop:
                for (; row < EVENT_ROW_MAX; row++)
                {
                    for (int col = 0; col <= endCol; col++)
                    {
                        if (eventMatrix[row][col])
                        {
                            break;
                        } else if (col == endCol)
                        {
                            if (!eventMatrix[row][col])
                            {
                                break RowLoop;
                            }
                        }
                    }
                }

                for (int col = 0; col <= endCol; col++)
                {
                    eventMatrix[row][col] = true;
                    // eventMatrix의 해당 부분이 false일 경우 draw
                }
            } else if (startDate.before(weekFirstDate) && endDate.after(weekLastDate))
            {
                // 이전 주 부터 시작되어 이번 주 이후에 종료
                RowLoop:
                for (; row < EVENT_ROW_MAX; row++)
                {
                    for (int col = 0; col <= 6; col++)
                    {
                        if (eventMatrix[row][col])
                        {
                            break;
                        } else if (col == 6)
                        {
                            if (!eventMatrix[row][6])
                            {
                                break RowLoop;
                            }
                        }
                    }
                }

                for (int col = 0; col <= 6; col++)
                {
                    eventMatrix[row][col] = true;
                    // eventMatrix의 해당 부분이 false일 경우 draw
                }
            } else if (startDate.compareTo(weekFirstDate) >= 0 && endDate.after(weekLastDate))
            {
                // 이번 주 부터 시작되어 이번 주 이후에 종료

                for (int i = 6; i >= 0; --i)
                {
                    if (startDate.compareTo(coordinateInfos[i].getDate().getTime()) >= 0)
                    {
                        startCol = i;
                        break;
                    }
                }

                RowLoop:
                for (; row < EVENT_ROW_MAX; row++)
                {
                    for (int col = startCol; col <= 6; col++)
                    {
                        if (eventMatrix[row][col])
                        {
                            break;
                        } else if (col == 6)
                        {
                            if (!eventMatrix[row][6])
                            {
                                break RowLoop;
                            }
                        }
                    }
                }

                for (int col = startCol; col <= 6; col++)
                {
                    eventMatrix[row][col] = true;
                    // eventMatrix의 해당 부분이 false일 경우 draw
                }
            }
            Map<String, Integer> map = new HashMap<>();
            map.put("startCol", startCol);
            map.put("endCol", endCol);
            map.put("row", row);

            if (row + 1 > rowNum)
            {
                rowNum = row + 1;
            }

            return map;
        }

        private void drawEvents()
        {
            WeekHeaderEventRow[] tableRows = new WeekHeaderEventRow[rowNum + 1];
            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            for (int i = 0; i <= rowNum; ++i)
            {
                tableRows[i] = (WeekHeaderEventRow) layoutInflater.inflate(R.layout.week_header_event_row, null, false);
                tableRows[i].initCols();
                tableLayout.addView(tableRows[i], i);
            }

            for (EventDrawingInfo eventDrawingInfo : eventDrawingInfoList)
            {
                tableRows[eventDrawingInfo.getRow()].setCol(eventDrawingInfo.getStartCol(), eventDrawingInfo.getEndCol(), eventDrawingInfo);
                tableRows[eventDrawingInfo.getRow()].invalidate();
            }
            tableLayout.invalidate();
            eventLayout.invalidate();
            headerLayout.invalidate();
        }

        public void setHeaderLayoutHeight()
        {
            Log.e(TAG, "table:" + tableLayout.getHeight());
            Log.e(TAG, "header:" + headerLayout.getHeight());
            onHeightChanged(headerLayout.getHeight() + tableLayout.getHeight());
        }
    }

    public WeekViewPagerAdapter(Activity activity, WeekFragment fragment)
    {
        this.activity = activity;
        this.weekFragment = fragment;

        weekViewModel = new ViewModelProvider(weekFragment).get(WeekViewModel.class);
        weekViewModel.getSchedules().observe(weekFragment.getViewLifecycleOwner(), scheduleDTOS ->
        {
            Log.e(TAG, "observe");
        });
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
        holder.onBindView(position);
        pagerHolderSparseArr.put(position, holder);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull WeekViewPagerHolder holder)
    {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull WeekViewPagerHolder holder)
    {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount()
    {
        return WeekViewPagerAdapter.WEEK_TOTAL_COUNT;
    }


    public void selectEvents(int position)
    {
        pagerHolderSparseArr.get(position).setEvents();
    }
}