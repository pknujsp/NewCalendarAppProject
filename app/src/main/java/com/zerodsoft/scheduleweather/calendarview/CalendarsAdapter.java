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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarsAdapter extends BaseExpandableListAdapter
{
    private Context context;
    private ICalendarCheckBox iCalendarCheckBox;
    private List<AccountDto> accountList;
    private LayoutInflater layoutInflater;

    private GroupViewHolder groupViewHolder;
    private ChildViewHolder childViewHolder;

    private String accountName;
    private String calendarDisplayName;
    private int calendarColor;

    private Map<Integer, boolean[]> mChildCheckStates;

    public CalendarsAdapter(Activity activity, List<AccountDto> accountList, boolean[][] states)
    {
        this.context = activity;
        this.iCalendarCheckBox = (ICalendarCheckBox) activity;
        this.accountList = accountList;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mChildCheckStates = new HashMap<>();
        for (int group = 0; group < states.length; group++)
        {
            mChildCheckStates.put(group, states[group]);
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
        accountName = accountList.get(i).getAccountName();

        if (view == null)
        {
            view = layoutInflater.inflate(R.layout.side_nav_calendar_group_item, null);

            groupViewHolder = new GroupViewHolder();
            groupViewHolder.accountName = (TextView) view.findViewById(R.id.side_nav_account_name);

            view.setTag(groupViewHolder);
        } else
        {
            groupViewHolder = (GroupViewHolder) view.getTag();
        }

        groupViewHolder.accountName.setText(accountName);
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup viewGroup)
    {
        final int mGroupPosition = groupPosition;
        final int mChildPosition = childPosition;

        calendarDisplayName = ((ContentValues) getChild(mGroupPosition, mChildPosition)).getAsString(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME);
        calendarColor = CalendarUtil.getColor(((ContentValues) getChild(mGroupPosition, mChildPosition)).getAsInteger(CalendarContract.Calendars.CALENDAR_COLOR));

        if (view == null)
        {
            view = layoutInflater.inflate(R.layout.side_nav_calendar_child_item, null);

            MaterialCheckBox checkBox = (MaterialCheckBox) view.findViewById(R.id.side_nav_calendar_checkbox);
            childViewHolder = new ChildViewHolder();
            childViewHolder.checkBox = checkBox;

            view.setTag(R.layout.side_nav_calendar_child_item, childViewHolder);
        } else
        {
            childViewHolder = (ChildViewHolder) view.getTag(R.layout.side_nav_calendar_child_item);
        }

        childViewHolder.checkBox.setText(calendarDisplayName);

        childViewHolder.checkBox.setOnCheckedChangeListener(null);
        if (mChildCheckStates.containsKey(mGroupPosition))
        {
            boolean[] getChecked = mChildCheckStates.get(mGroupPosition);
            childViewHolder.checkBox.setChecked(getChecked[mChildPosition]);
        } else
        {
            boolean[] getChecked = new boolean[getChildrenCount(mGroupPosition)];
            mChildCheckStates.put(mGroupPosition, getChecked);
            childViewHolder.checkBox.setChecked(false);
        }

        childViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
                getChecked[mChildPosition] = isChecked;
                mChildCheckStates.put(mGroupPosition, getChecked);

                String key = accountList.get(groupPosition).getCalendars().get(childPosition).getAsString(CalendarContract.Calendars.ACCOUNT_NAME)
                        + "&" + accountList.get(groupPosition).getCalendars().get(childPosition).getAsString(CalendarContract.Calendars._ID);
                iCalendarCheckBox.onCheckedBox(key, accountList.get(groupPosition).getCalendars().get(childPosition), isChecked);
            }
        });

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


    public final class GroupViewHolder
    {
        TextView accountName;
    }

    public final class ChildViewHolder
    {
        View calendarColor;
        MaterialCheckBox checkBox;
    }
}
