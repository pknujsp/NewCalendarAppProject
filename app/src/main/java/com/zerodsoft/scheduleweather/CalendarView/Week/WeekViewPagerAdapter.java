package com.zerodsoft.scheduleweather.CalendarView.Week;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.CalendarFragment.WeekFragment;
import com.zerodsoft.scheduleweather.CalendarView.AccountType;
import com.zerodsoft.scheduleweather.CalendarView.Dto.CoordinateInfo;
import com.zerodsoft.scheduleweather.CalendarView.EventDrawingInfo;
import com.zerodsoft.scheduleweather.CalendarView.HoursView;
import com.zerodsoft.scheduleweather.CalendarView.ViewModel.WeekViewModel;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Room.AppDb;
import com.zerodsoft.scheduleweather.Room.DTO.ScheduleDTO;
import com.zerodsoft.scheduleweather.Room.DTO.TypeConverter;
import com.zerodsoft.scheduleweather.Utility.AppSettings;

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

    class WeekViewPagerHolder extends RecyclerView.ViewHolder
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
        public GridLayout eventGrid;

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
            this.eventGrid = (GridLayout) view.findViewById(R.id.week_header_event_layout);

            //  this.weekHeaderView.setViewHeightChangeListener(WeekViewPagerHolder.this);
            datesLayout.setLayoutParams(new LinearLayout.LayoutParams(WeekFragment.SPACING_BETWEEN_DAY, ViewGroup.LayoutParams.WRAP_CONTENT));
            eventGrid.setVisibility(View.GONE);
        }

        public void onBindView(int position)
        {
            this.viewPosition = position;

            setWeekDates();
            weekHeaderView.setPosition(viewPosition);
            weekView.setPosition(viewPosition).setCoordinateInfoInterface(weekHeaderView).setOnRefreshHoursViewListener(hoursView);
            clearEvents();
        }

        private void clearEvents()
        {
            schedules = null;
            eventMatrix = null;
            rowCount = 0;
            eventDrawingInfoList = null;
            coordinateInfos = null;
            weekFirstDate = null;
            weekLastDate = null;

            if (eventGrid != null)
            {
                eventGrid.removeAllViews();
                eventGrid.setVisibility(View.GONE);
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

                    schedules = AppDb.getInstance(activity).scheduleDAO().selectSchedules(AccountType.LOCAL.ordinal(), weekFirstDate, weekLastDate);

                    coordinateInfos = weekHeaderView.getArray();
                    eventMatrix = new boolean[EVENT_ROW_MAX][7];
                    eventDrawingInfoList = new ArrayList<>();

                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (!schedules.isEmpty())
                            {
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

            if (row + 1 > rowCount)
            {
                rowCount = row + 1;
            }

            return map;
        }

        private void drawEvents()
        {
            eventGrid.setVisibility(View.VISIBLE);
            eventGrid.setRowCount(rowCount);

            for (EventDrawingInfo eventDrawingInfo : eventDrawingInfoList)
            {
                int row = eventDrawingInfo.getRow();
                int startCol = eventDrawingInfo.getStartCol();
                int endCol = eventDrawingInfo.getEndCol();
                int size = endCol - startCol + 1;

                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.width = weekHeaderView.WEEK_HEADER_WIDTH_PER_DAY * size - 4;
                param.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                param.columnSpec = GridLayout.spec(startCol, size);
                param.rowSpec = GridLayout.spec(row);
                param.bottomMargin = 2;
                param.topMargin = 2;
                param.leftMargin = 2;
                param.rightMargin = 2;

                TextView textView = new TextView(activity);
                textView.setLayoutParams(param);
                textView.setBackgroundColor(Color.WHITE);
                textView.setTextSize(12);
                textView.setText(eventDrawingInfo.getSchedule().getSubject());
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                if (eventDrawingInfo.getAccountType() == AccountType.GOOGLE)
                {
                    textView.setBackgroundColor(AppSettings.getGoogleEventBackgroundColor());
                    textView.setTextColor(AppSettings.getGoogleEventTextColor());
                } else
                {
                    textView.setBackgroundColor(AppSettings.getLocalEventBackgroundColor());
                    textView.setTextColor(AppSettings.getLocalEventTextColor());
                }
                eventGrid.addView(textView);
            }

            eventGrid.requestLayout();
            eventLayout.requestLayout();
            headerLayout.requestLayout();

            eventGrid.invalidate();
            eventLayout.invalidate();
            headerLayout.invalidate();
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
        pagerHolderSparseArr.put(position, holder);
        Log.e(TAG, "onBindViewHolder : " + position);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull WeekViewPagerHolder holder)
    {
        super.onViewAttachedToWindow(holder);
        holder.setEvents();
        Log.e(TAG, "-------------------------------------------------");
        Log.e(TAG, "onViewAttachedToWindow : " + holder.viewPosition);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull WeekViewPagerHolder holder)
    {
        super.onViewDetachedFromWindow(holder);
        holder.eventGrid.removeAllViews();
        holder.eventGrid.setVisibility(View.GONE);
        Log.e(TAG, "onViewDetachedFromWindow : " + holder.viewPosition);
    }

    @Override
    public void onViewRecycled(@NonNull WeekViewPagerHolder holder)
    {
        super.onViewRecycled(holder);
        Log.e(TAG, "onViewRecycled : " + holder.viewPosition);
    }

    @Override
    public int getItemCount()
    {
        return WeekViewPagerAdapter.WEEK_TOTAL_COUNT;
    }


    public void selectEvents(int position)
    {
        //  pagerHolderSparseArr.get(position).setEvents();
    }

}