package com.zerodsoft.scheduleweather.calendarview;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.zerodsoft.scheduleweather.calendarview.interfaces.ICalendarCheckBox;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.dto.AccountDto;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarDto;

import java.util.List;

public class CalendarsAdapter extends BaseExpandableListAdapter
{
    private Context context;
    private ICalendarCheckBox iCalendarCheckBox;
    private List<AccountDto> accountList;
    private boolean[][] checkBoxStates;
    private MaterialCheckBox[][] checkBoxes;
    private LayoutInflater layoutInflater;

    public CalendarsAdapter(Activity activity, List<AccountDto> accountList)
    {
        this.context = activity;
        this.iCalendarCheckBox = (ICalendarCheckBox) activity;
        this.accountList = accountList;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.checkBoxStates = new boolean[accountList.size()][];
        this.checkBoxes = new MaterialCheckBox[accountList.size()][];

        for (int i = 0; i < accountList.size(); i++)
        {
            checkBoxStates[i] = new boolean[accountList.get(i).getCalendars().size()];
            checkBoxes[i] = new MaterialCheckBox[accountList.get(i).getCalendars().size()];
        }
    }

    @Override
    public int getGroupCount()
    {
        return accountList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return accountList.get(groupPosition).getCalendars().size();
    }

    @Override
    public Object getGroup(int i)
    {
        return accountList.get(i);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return accountList.get(groupPosition).getCalendars().get(childPosition);
    }

    @Override
    public long getGroupId(int i)
    {
        return i;
    }

    @Override
    public long getChildId(int i, int i1)
    {
        return i1;
    }

    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup)
    {
        if (view == null)
        {
            view = layoutInflater.inflate(R.layout.side_nav_calendar_group_item, viewGroup, false);
        }

        TextView accountName = (TextView) view.findViewById(R.id.side_nav_account_name);
        accountName.setText(accountList.get(i).getAccountName());
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup viewGroup)
    {
        if (view == null)
        {
            view = layoutInflater.inflate(R.layout.side_nav_calendar_child_item, viewGroup, false);
        }

        MaterialCheckBox checkBox = (MaterialCheckBox) view.findViewById(R.id.side_nav_calendar_checkbox);
        checkBox.setText(((ContentValues) getChild(groupPosition, childPosition)).getAsString(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if (isChecked)
                {
                    checkBoxStates[groupPosition][childPosition] = true;
                } else
                {
                    checkBoxStates[groupPosition][childPosition] = false;
                }
                String key = accountList.get(groupPosition).getCalendars().get(childPosition).getAsString(CalendarContract.Calendars.OWNER_ACCOUNT)
                        + "&" + accountList.get(groupPosition).getCalendars().get(childPosition).getAsString(CalendarContract.Calendars._ID);
                iCalendarCheckBox.onCheckedBox(key, accountList.get(groupPosition).getCalendars().get(childPosition).getAsLong(CalendarContract.Calendars._ID), isChecked);
            }
        });
        checkBoxes[groupPosition][childPosition] = checkBox;
        if (checkBoxStates[groupPosition][childPosition])
        {
            checkBox.setChecked(true);
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1)
    {
        return true;
    }

    public AccountDto getAccount(int groupPosition)
    {
        return accountList.get(groupPosition);
    }

    public ContentValues getCalendar(int groupPosition, int childPosition)
    {
        return accountList.get(groupPosition).getCalendars().get(childPosition);
    }

    public void setCheckBoxStates(boolean[][] states)
    {
        checkBoxStates = states;
    }
}
