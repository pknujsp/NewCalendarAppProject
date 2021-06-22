package com.zerodsoft.scheduleweather.calendarview;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.ColorStateList;
import android.provider.CalendarContract;
import android.util.ArrayMap;
import android.util.Log;
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

	private Set<Integer> selectedCalendarIdSet = new HashSet<>();
	private Set<Integer> allCalendarIdSet = new HashSet<>();
	private List<ContentValues> calendarList = new ArrayList<>();
	private ArrayMap<String, List<ContentValues>> accountArrMap = new ArrayMap<>();

	public SideBarCalendarListAdapter(Activity activity, ICalendarCheckBox iCalendarCheckBox) {
		this.context = activity;
		this.iCalendarCheckBox = iCalendarCheckBox;
		this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
	public String getGroup(int i) {
		return accountArrMap.keyAt(i);
	}

	@Override
	public ContentValues getChild(int groupPosition, int childPosition) {
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
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {
		String accountName = accountArrMap.keyAt(groupPosition);
		GroupViewHolder groupViewHolder = null;

		if (view == null) {
			view = layoutInflater.inflate(R.layout.side_nav_calendar_group_item, null);

			groupViewHolder = new GroupViewHolder((TextView) view.findViewById(R.id.side_nav_account_name));
			view.setTag(groupViewHolder);
		} else {
			groupViewHolder = (GroupViewHolder) view.getTag();
		}

		groupViewHolder.accountName.setText(accountName);
		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {
		String calendarDisplayName =
				accountArrMap.valueAt(groupPosition).get(childPosition).getAsString(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME);
		int calendarColor =
				EventUtil.getColor(accountArrMap.valueAt(groupPosition).get(childPosition).getAsInteger(CalendarContract.Calendars.CALENDAR_COLOR));
		int calendarId = accountArrMap.valueAt(groupPosition).get(childPosition).getAsInteger(CalendarContract.Calendars._ID);

		ChildViewHolder childViewHolder = null;

		if (view == null) {
			view = layoutInflater.inflate(R.layout.side_nav_calendar_child_item, null);

			childViewHolder = new ChildViewHolder((MaterialCheckBox) view.findViewById(R.id.side_nav_calendar_checkbox));
			view.setTag(childViewHolder);
		} else {
			childViewHolder = (ChildViewHolder) view.getTag();
		}

		CheckBoxViewHolder checkBoxViewHolder = new CheckBoxViewHolder(groupPosition, childPosition);
		childViewHolder.checkBox.setTag(null);
		childViewHolder.checkBox.setTag(checkBoxViewHolder);

		childViewHolder.checkBox.setText(calendarDisplayName);
		childViewHolder.checkBox.setButtonTintList(ColorStateList.valueOf(EventUtil.getColor(calendarColor)));

		childViewHolder.checkBox.setOnCheckedChangeListener(null);
		childViewHolder.checkBox.setChecked(selectedCalendarIdSet.contains(calendarId));
		childViewHolder.checkBox.setOnCheckedChangeListener(onCheckedChangeListener);

		return view;
	}

	private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			CheckBoxViewHolder checkBoxViewHolder = (CheckBoxViewHolder) buttonView.getTag();

			iCalendarCheckBox.onCheckedBox(accountArrMap.valueAt(checkBoxViewHolder.groupPosition).get(checkBoxViewHolder.childPosition),
					isChecked);

			int calendarId =
					accountArrMap.valueAt(checkBoxViewHolder.groupPosition).get(checkBoxViewHolder.childPosition).getAsInteger(CalendarContract.Calendars._ID);

			if (isChecked) {
				selectedCalendarIdSet.add(accountArrMap.valueAt(checkBoxViewHolder.groupPosition).get(checkBoxViewHolder.childPosition).getAsInteger(CalendarContract.Calendars._ID));
			} else {
				selectedCalendarIdSet.remove(accountArrMap.valueAt(checkBoxViewHolder.groupPosition).get(checkBoxViewHolder.childPosition).getAsInteger(CalendarContract.Calendars._ID));
			}

			Log.e("체크박스 클릭됨",
					"calendarId : " + calendarId + " calendarDisplayName : " +
							accountArrMap.valueAt(checkBoxViewHolder.groupPosition).get(checkBoxViewHolder.childPosition).getAsString(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)
							+ " group : " + checkBoxViewHolder.groupPosition + " child : " + checkBoxViewHolder.childPosition);
		}
	};

	@Override
	public boolean isChildSelectable(int i, int i1) {
		return false;
	}

	static final class GroupViewHolder {
		final TextView accountName;

		public GroupViewHolder(TextView accountName) {
			this.accountName = accountName;
		}
	}

	static final class ChildViewHolder {
		final MaterialCheckBox checkBox;

		public ChildViewHolder(MaterialCheckBox checkBox) {
			this.checkBox = checkBox;
		}
	}

	static final class CheckBoxViewHolder {
		final int groupPosition;
		final int childPosition;

		public CheckBoxViewHolder(int groupPosition, int childPosition) {
			this.groupPosition = groupPosition;
			this.childPosition = childPosition;
		}
	}
}
