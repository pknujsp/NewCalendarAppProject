package com.zerodsoft.scheduleweather.calendarview.week;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.AppMainActivity;
import com.zerodsoft.scheduleweather.calendarfragment.WeekFragment;
import com.zerodsoft.scheduleweather.calendarview.AccountType;
import com.zerodsoft.scheduleweather.calendarview.dto.CoordinateInfo;
import com.zerodsoft.scheduleweather.calendarview.EventDrawingInfo;
import com.zerodsoft.scheduleweather.calendarview.HoursView;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

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
    private Calendar today = Calendar.getInstance();

    @Override
    public void refreshChildView(int position)
    {
    }

    class WeekViewPagerHolder extends RecyclerView.ViewHolder
    {
        public WeekView weekView;
        public TextView weekDatesTextView;
        public HoursView hoursView;
        public WeekHeaderView weekHeaderView;

        public LinearLayout weekLayout;
        public LinearLayout headerLayout;
        public LinearLayout tableLayout;
        public LinearLayout headerCalendarLayout;
        public RelativeLayout eventListLayout;

        public int viewPosition;
        public Calendar calendar;

        public List<ScheduleDTO> schedules;

        public boolean[][] eventMatrix;
        public int rowCount;
        public List<EventDrawingInfo> eventDrawingInfoList;
        public CoordinateInfo[] coordinateInfos;

        public static final int EVENT_ROW_MAX = 20;

        public Date weekFirstDate;
        public Date weekLastDate;

        private List<EventGridInfo> eventGridInfos;


        public WeekViewPagerHolder(View view)
        {
            super(view);
            this.weekLayout = (LinearLayout) view.findViewById(R.id.week_layout);
            this.headerLayout = (LinearLayout) view.findViewById(R.id.week_header_layout);
            this.tableLayout = (LinearLayout) view.findViewById(R.id.week_table_layout);
            this.headerCalendarLayout = (LinearLayout) view.findViewById(R.id.week_header_calendar_layout);
            this.hoursView = (HoursView) view.findViewById(R.id.week_hours_view);
            this.weekHeaderView = (WeekHeaderView) view.findViewById(R.id.week_header_view);
            this.weekDatesTextView = (TextView) view.findViewById(R.id.week_dates_textview);
            this.weekView = (WeekView) view.findViewById(R.id.week_view);
            this.eventListLayout = (RelativeLayout) view.findViewById(R.id.week_header_event_list_layout);

            weekDatesTextView.setLayoutParams(new LinearLayout.LayoutParams(WeekFragment.getSpacingBetweenDay(), ViewGroup.LayoutParams.WRAP_CONTENT));
            hoursView.setLayoutParams(new LinearLayout.LayoutParams(WeekFragment.getSpacingBetweenDay(), ViewGroup.LayoutParams.WRAP_CONTENT));
            weekView.setLayoutParams(new LinearLayout.LayoutParams(AppMainActivity.getDisplayWidth() - WeekFragment.getSpacingBetweenDay(), ViewGroup.LayoutParams.WRAP_CONTENT));
            headerCalendarLayout.setLayoutParams(new LinearLayout.LayoutParams(AppMainActivity.getDisplayWidth() - WeekFragment.getSpacingBetweenDay(), ViewGroup.LayoutParams.WRAP_CONTENT));

            eventListLayout.setVisibility(View.GONE);

            eventListLayout.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent)
                {
                    float x = motionEvent.getX();
                    float y = motionEvent.getY();

                    for (EventGridInfo eventGridInfo : eventGridInfos)
                    {
                        if (x >= eventGridInfo.getLeft() && x <= eventGridInfo.getRight() && y >= eventGridInfo.getTop() && y <= eventGridInfo.getBottom())
                        {
                            // 오늘 날짜로 이동
                            //   ((AppMainActivity) activity).goToScheduleInfoAcitivity(eventGridInfo.getScheduleDTO().getId());
                            break;
                        }
                    }

                    return false;
                }
            });
        }

        public void setEventList(int position, List<ScheduleDTO> schedules)
        {
            // holderSparseArray.get(position).setData(schedules);
        }

        public void onBindView(int position)
        {
            this.viewPosition = position;

            setWeekDates();
            weekHeaderView.setPosition(viewPosition);
            weekView.setPosition(viewPosition);
            weekView.setCoordinateInfoInterface(weekHeaderView).setOnRefreshHoursViewListener(hoursView);
            clearEvents();
        }

        private void clearEvents()
        {
            if (eventListLayout != null)
            {
                if (eventListLayout.getChildCount() > 0)
                {
                    eventListLayout.removeAllViews();
                }
                eventListLayout.setVisibility(View.GONE);
            }
        }

        private void setWeekDates()
        {
            calendar = (Calendar) today.clone();
            calendar.add(Calendar.WEEK_OF_YEAR, viewPosition - FIRST_VIEW_NUMBER);
            weekDatesTextView.setText(Integer.toString(calendar.get(Calendar.WEEK_OF_YEAR)) + "주");
        }

        public void setEvents()
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    weekFirstDate = weekHeaderView.getWeekFirstDate();
                    weekLastDate = weekHeaderView.getWeekLastDate();

                    schedules = (List<ScheduleDTO>) AppDb.getInstance(activity).scheduleDAO().selectSchedules(AccountType.LOCAL.ordinal(), weekFirstDate, weekLastDate);

                    coordinateInfos = weekHeaderView.getArray();
                    eventMatrix = new boolean[EVENT_ROW_MAX][7];
                    eventDrawingInfoList = new ArrayList<>();

                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (schedules != null)
                            {
                                eventGridInfos = new ArrayList<>();
                                setEventDrawingInfo();
                                drawEvents();
                            }
                        }
                    });
                }
            }).start();
        }


        private void setEventDrawingInfo()
        {
            for (ScheduleDTO schedule : schedules)
            {
                Map<String, Integer> map = calcEventPosition(schedule);
                if (schedule.getCategory() == ScheduleDTO.GOOGLE_CATEGORY)
                {
                    eventDrawingInfoList.add(new EventDrawingInfo(map.get("startCol"), map.get("endCol"), map.get("row"), schedule, AccountType.GOOGLE));
                } else
                {
                    eventDrawingInfoList.add(new EventDrawingInfo(map.get("startCol"), map.get("endCol"), map.get("row"), schedule, AccountType.LOCAL));
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

            if (startDate.compareTo(weekFirstDate) >= 0 && endDate.compareTo(weekLastDate) <= 0)
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

            if (row + 1 > rowCount)
            {
                rowCount = row + 1;
            }
            return map;
        }

        private void drawEvents()
        {
            Rect textRect = new Rect();
            Paint textPaint = new Paint();
            textPaint.setTextSize(activity.getResources().getDimension(R.dimen.week_header_view_day_event_text_size));
            textPaint.getTextBounds("12", 0, 1, textRect);

            int layoutHeight = (textRect.height() + 16) * rowCount;

            eventListLayout.setVisibility(View.VISIBLE);
            eventListLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, layoutHeight));
            eventListLayout.requestLayout();

            for (EventDrawingInfo eventDrawingInfo : eventDrawingInfoList)
            {
                HeaderEventView headerEventView = new HeaderEventView(activity, eventDrawingInfo, AppMainActivity.getDisplayWidth() - WeekFragment.getSpacingBetweenDay(), layoutHeight);
                eventGridInfos.add(new EventGridInfo(headerEventView.getViewRect(), eventDrawingInfo.getSchedule()));
                eventListLayout.addView(headerEventView);
            }
            eventListLayout.invalidate();
        }
    }

    public WeekViewPagerAdapter(Activity activity, WeekFragment fragment)
    {
        this.activity = activity;
        this.weekFragment = fragment;
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
        Log.e(TAG, "onBindViewHolder : " + position);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull WeekViewPagerHolder holder)
    {
        super.onViewAttachedToWindow(holder);
        holder.clearEvents();
        holder.setEvents();
        Log.e(TAG, "-------------------------------------------------");
        Log.e(TAG, "onViewAttachedToWindow : " + holder.viewPosition);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull WeekViewPagerHolder holder)
    {
        super.onViewDetachedFromWindow(holder);
        holder.clearEvents();
        Log.e(TAG, "onViewDetachedFromWindow : " + holder.viewPosition);
    }

    @Override
    public void onViewRecycled(@NonNull WeekViewPagerHolder holder)
    {
        super.onViewRecycled(holder);
        holder.clearEvents();
        Log.e(TAG, "onViewRecycled : " + holder.viewPosition);
    }

    @Override
    public int getItemCount()
    {
        return WeekViewPagerAdapter.WEEK_TOTAL_COUNT;
    }

    private void setGrid()
    {

    }

    class EventGridInfo
    {
        private int left;
        private int right;
        private int top;
        private int bottom;
        private ScheduleDTO scheduleDTO;

        public EventGridInfo(Rect rect, ScheduleDTO scheduleDTO)
        {
            left = rect.left;
            right = rect.right;
            top = rect.top;
            bottom = rect.bottom;
            this.scheduleDTO = scheduleDTO;
        }

        public ScheduleDTO getScheduleDTO()
        {
            return scheduleDTO;
        }

        public int getBottom()
        {
            return bottom;
        }

        public int getLeft()
        {
            return left;
        }

        public int getRight()
        {
            return right;
        }

        public int getTop()
        {
            return top;
        }
    }

}