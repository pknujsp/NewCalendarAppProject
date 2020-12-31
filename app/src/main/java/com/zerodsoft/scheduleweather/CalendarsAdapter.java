package com.zerodsoft.scheduleweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.zerodsoft.scheduleweather.googlecalendar.dto.AccountDto;
import com.zerodsoft.scheduleweather.googlecalendar.dto.CalendarDto;

import java.util.List;

public class CalendarsAdapter extends BaseExpandableListAdapter
{
    private Context context;
    private List<AccountDto> accountList;

    public CalendarsAdapter(Context context, List<AccountDto> accountList)
    {
        this.context = context;
        this.accountList = accountList;
    }

    @Override
    public int getGroupCount()
    {
        return 0;
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
            view = LayoutInflater.from(context).inflate(R.layout.side_nav_calendar_header, viewGroup);
        }

        TextView accountName = (TextView) view.findViewById(R.id.side_nav_account_name);
        accountName.setText(accountList.get(i).getCalendars().get(0).getACCOUNT_NAME());
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup viewGroup)
    {
        if (view == null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.side_nav_calendar_list_item, viewGroup);
        }

        MaterialCheckBox checkBox = (MaterialCheckBox) view.findViewById(R.id.side_nav_calendar_checkbox);
        checkBox.setText(((CalendarDto) getChild(groupPosition, childPosition)).getCALENDAR_DISPLAY_NAME());
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1)
    {
        return true;
    }
}
