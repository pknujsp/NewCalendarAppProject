package com.zerodsoft.scheduleweather.calendarview;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.ColorStateList;
import android.provider.CalendarContract;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.zerodsoft.scheduleweather.calendarview.interfaces.ICalendarCheckBox;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.room.dto.SelectedCalendarDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SideBarCalendarListAdapter extends BaseExpandableListAdapter {
	private Context context;
	private ICalendarCheckBox iCalendarCheckBox;
	private LayoutInflater layoutInflater;

	private GroupViewHolder groupViewHolder;
	private ChildViewHolder childViewHolder;

	private String accountName;
	private String calendarDisplayName;
	private int calendarColor;
	private int calendarId;

	private Set<Integer> selectedCalendarIdSet = new HashSet<>();
	private Set<Integer> allCalendarIdSet = new HashSet<>();
	private List<ContentValues> calendarList = new ArrayList<>();
	private ArrayMap<String, List<ContentValues>> accountArrMap = new ArrayMap<>();

	public SideBarCalendarListAdapter(Activity context, ICalendarCheckBox iCalendarCheckBox) {
		this.context = context;
		this.iCalendarCheckBox = iCalendarCheckBox;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setCalendarList(List<ContentValues> calendarList) {
		this.calendarList.clear();
		this.accountArrMap.clear();
		this.allCalendarIdSet.clear();

		this.calendarList.addAll(calendarList);

		//account map 생성
		String accountName = null;
		for (ContentValues calendar : calendarList) {
			accountName = calendar.getAsString(CalendarContract.Calendars.ACCOUNT_NAME);

			if (!accountArrMap.containsKey(accountName)) {
				accountArrMap.put(accountName, new ArrayList<>());
			}
			accountArrMap.get(accountName).add(calendar);
			allCalendarIdSet.add(calendar.getAsInteger(CalendarContract.Calendars._ID));
		}
	}

	public void setSelectedCalendarList(List<SelectedCalendarDTO> selectedCalendarList) {
		selectedCalendarIdSet.clear();

		for (SelectedCalendarDTO selectedCalendar : selectedCalendarList) {
			selectedCalendarIdSet.add(selectedCalendar.getCalendarId());
		}
	}

	public ArrayMap<String, List<ContentValues>> getAccountArrMap() {
		return accountArrMap;
	}

	public List<ContentValues> getCalendarList() {
		return calendarList;
	}

	public Set<Integer> getSelectedCalendarIdSet() {
		return selectedCalendarIdSet;
	}

	public Set<Integer> getAllCalendarIdSet() {
		return allCalendarIdSet;
	}

	@Override
	public int getGroupCount() {
		return accountArrMap.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return accountArrMap.valueAt(groupPosition).size();
	}

	@Override
	public Object getGroup(int i) {
		return accountArrMap.keyAt(i);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return accountArrMap.valueAt(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int i) {
		return i;
	}

	@Override
	public long getChildId(int i, int i1) {
		return i1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {
		accountName = accountArrMap.keyAt(groupPosition);

		if (view == null) {
			view = layoutInflater.inflate(R.layout.side_nav_calendar_group_item, null);

			groupViewHolder = new GroupViewHolder();
			groupViewHolder.accountName = (TextView) view.findViewById(R.id.side_nav_account_name);

			view.setTag(groupViewHolder);
		} else {
			groupViewHolder = (GroupViewHolder) view.getTag();
		}

		groupViewHolder.accountName.setText(accountName);
		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {
		final int mGroupPosition = groupPosition;
		final int mChildPosition = childPosition;

		calendarDisplayName = ((ContentValues) getChild(mGroupPosition, mChildPosition)).getAsString(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME);
		calendarColor = EventUtil.getColor(((ContentValues) getChild(mGroupPosition, mChildPosition)).getAsInteger(CalendarContract.Calendars.CALENDAR_COLOR));
		calendarId = EventUtil.getColor(((ContentValues) getChild(mGroupPosition, mChildPosition)).getAsInteger(CalendarContract.Calendars._ID));

		if (view == null) {
			view = layoutInflater.inflate(R.layout.side_nav_calendar_child_item, null);

			MaterialCheckBox checkBox = (MaterialCheckBox) view.findViewById(R.id.side_nav_calendar_checkbox);
			childViewHolder = new ChildViewHolder();
			childViewHolder.checkBox = checkBox;

			view.setTag(R.layout.side_nav_calendar_child_item, childViewHolder);
		} else {
			childViewHolder = (ChildViewHolder) view.getTag(R.layout.side_nav_calendar_child_item);
		}

		childViewHolder.checkBox.setText(calendarDisplayName);
		childViewHolder.checkBox.setButtonTintList(ColorStateList.valueOf(EventUtil.getColor(calendarColor)));

		childViewHolder.checkBox.setOnCheckedChangeListener(null);
		childViewHolder.checkBox.setChecked(selectedCalendarIdSet.contains(calendarId) ? true : false);

		childViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
				if (isChecked) {
					selectedCalendarIdSet.remove(calendarId);
				} else {
					selectedCalendarIdSet.add(calendarId);
				}

				iCalendarCheckBox.onCheckedBox(accountArrMap.valueAt(mGroupPosition).get(mChildPosition), isChecked);
			}
		});

		return view;
	}

	@Override
	public boolean isChildSelectable(int i, int i1) {
		return true;
	}

	static final class GroupViewHolder {
		TextView accountName;
	}

	static final class ChildViewHolder {
		MaterialCheckBox checkBox;
	}
}
