package com.zerodsoft.scheduleweather.CalendarView.Week;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.zerodsoft.scheduleweather.CalendarView.AccountType;
import com.zerodsoft.scheduleweather.CalendarView.EventDrawingInfo;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Room.DTO.ScheduleDTO;
import com.zerodsoft.scheduleweather.Utility.AppSettings;

public class WeekHeaderEventRow extends TableRow
{
    private TextView[] cols;
    private View rootView;
    private int height;

    public WeekHeaderEventRow(Context context)
    {
        super(context);
    }

    public WeekHeaderEventRow(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public View getRootView()
    {
        return super.getRootView();
    }

    public void initCols()
    {
        cols = new TextView[7];
        rootView = getRootView();

        cols[0] = rootView.findViewById(R.id.week_header_event_0);
        cols[1] = rootView.findViewById(R.id.week_header_event_1);
        cols[2] = rootView.findViewById(R.id.week_header_event_2);
        cols[3] = rootView.findViewById(R.id.week_header_event_3);
        cols[4] = rootView.findViewById(R.id.week_header_event_4);
        cols[5] = rootView.findViewById(R.id.week_header_event_5);
        cols[6] = rootView.findViewById(R.id.week_header_event_6);
    }

    public void setCol(int startCol, int endCol, EventDrawingInfo eventDrawingInfo)
    {
        for (int i = startCol; i <= endCol; i++)
        {
            cols[i].setText(eventDrawingInfo.getSchedule().getSubject());
            if (eventDrawingInfo.getAccountType() == AccountType.GOOGLE)
            {
                cols[i].setBackgroundColor(AppSettings.getGoogleEventBackgroundColor());
                cols[i].setTextColor(AppSettings.getGoogleEventTextColor());
            } else
            {
                cols[i].setBackgroundColor(AppSettings.getLocalEventBackgroundColor());
                cols[i].setTextColor(AppSettings.getLocalEventTextColor());
            }
        }
    }
}
