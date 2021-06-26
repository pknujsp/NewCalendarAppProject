package com.zerodsoft.scheduleweather.calendarview.instancelistdaydialog.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.provider.CalendarContract;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.scheduleweather.event.util.EventUtil;

import java.util.List;

public class EventsInfoRecyclerViewAdapter extends RecyclerView.Adapter<EventsInfoRecyclerViewAdapter.EventsInfoViewHolder> {
	private List<ContentValues> instances;
	private final OnEventItemClickListener onEventItemClickListener;
	private final OnEventItemLongClickListener onEventItemLongClickListener;
	private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
	private final long BEGIN;
	private final long END;

	private int checkBoxVisibility = View.GONE;
	private Context context;
	private Float viewMargin;

	public EventsInfoRecyclerViewAdapter(OnEventItemLongClickListener onEventItemLongClickListener,
	                                     OnEventItemClickListener onEventItemClickListener, CompoundButton.OnCheckedChangeListener onCheckedChangeListener,
	                                     long BEGIN, long END) {
		this.onEventItemLongClickListener = onEventItemLongClickListener;
		this.onEventItemClickListener = onEventItemClickListener;
		this.onCheckedChangeListener = onCheckedChangeListener;
		this.BEGIN = BEGIN;
		this.END = END;
	}

	@NonNull
	@Override
	public EventsInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		this.context = parent.getContext();
		this.viewMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13f, context.getResources().getDisplayMetrics());
		return new EventsInfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.events_info_list_item, parent, false));
	}

	public List<ContentValues> getInstances() {
		return instances;
	}

	@Override
	public void onBindViewHolder(@NonNull EventsInfoViewHolder holder, int position) {
		holder.onBind(position);
	}

	@Override
	public int getItemCount() {
		return instances == null ? 0 : instances.size();
	}

	public void setInstances(List<ContentValues> instances) {
		this.instances = instances;
	}

	public void setCheckBoxVisibility(int checkBoxVisibility) {
		this.checkBoxVisibility = checkBoxVisibility;
	}


	class EventsInfoViewHolder extends RecyclerView.ViewHolder {
		private long instanceId;
		private int calendarId;
		private long begin;
		private long end;
		private long eventId;
		private LinearLayout rootLayout;
		private TextView instanceTitleTextView;
		private CheckBox checkBox;

		public EventsInfoViewHolder(View view) {
			super(view);
			rootLayout = (LinearLayout) view;
			instanceTitleTextView = (TextView) view.findViewById(R.id.instance_item);
			checkBox = (CheckBox) view.findViewById(R.id.checkbox);

			instanceTitleTextView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					onEventItemClickListener.onClickedOnDialog(calendarId, instanceId, eventId, begin, end);
				}
			});

			instanceTitleTextView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					//popupMenu
					ContentValues instance = ((InstanceTagHolder) view.getTag()).instance;
					onEventItemLongClickListener.createInstancePopupMenu(instance, view, Gravity.CENTER);
					return true;
				}
			});
		}

		public void onBind(int position) {
			this.instanceId = instances.get(position).getAsLong(CalendarContract.Instances._ID);
			this.calendarId = instances.get(position).getAsInteger(CalendarContract.Instances.CALENDAR_ID);
			this.begin = instances.get(position).getAsLong(CalendarContract.Instances.BEGIN);
			this.end = instances.get(position).getAsLong(CalendarContract.Instances.END);
			this.eventId = instances.get(position).getAsLong(CalendarContract.Instances.EVENT_ID);

			final InstanceTagHolder holder = new InstanceTagHolder(instances.get(position));
			instanceTitleTextView.setTag(holder);

			checkBox.setChecked(false);
			if (instances.get(position).getAsInteger(CalendarContract.Instances.CALENDAR_ACCESS_LEVEL) == CalendarContract.Instances.CAL_ACCESS_READ) {
				checkBox.setVisibility(View.GONE);
			} else {
				checkBox.setVisibility(checkBoxVisibility);
			}

			instanceTitleTextView.setClickable(checkBoxVisibility == View.VISIBLE ? false : true);
			instanceTitleTextView.setLongClickable(checkBoxVisibility == View.VISIBLE ? false : true);

			checkBox.setTag(holder);
			checkBox.setOnCheckedChangeListener(onCheckedChangeListener);

			RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) rootLayout.getLayoutParams();

			int[] margin = EventUtil.getViewSideMargin(instances.get(position).getAsLong(CalendarContract.Instances.BEGIN)
					, instances.get(position).getAsLong(CalendarContract.Instances.END)
					, BEGIN, END, viewMargin.intValue(), instances.get(position).getAsBoolean(CalendarContract.Instances.ALL_DAY));

			layoutParams.leftMargin = margin[0];
			layoutParams.rightMargin = margin[1];

			rootLayout.setLayoutParams(layoutParams);
			rootLayout.setBackgroundColor(EventUtil.getColor(instances.get(position).getAsInteger(CalendarContract.Instances.EVENT_COLOR)));
			if (instances.get(position).getAsString(CalendarContract.Instances.TITLE) != null) {
				if (instances.get(position).getAsString(CalendarContract.Instances.TITLE).isEmpty()) {
					instanceTitleTextView.setText(context.getString(R.string.empty_title));
				} else {
					instanceTitleTextView.setText(instances.get(position).getAsString(CalendarContract.Instances.TITLE));
				}
			} else {
				instanceTitleTextView.setText(context.getString(R.string.empty_title));
			}
		}
	}

	public static class InstanceTagHolder {
		public ContentValues instance;

		public InstanceTagHolder(ContentValues instance) {
			this.instance = instance;
		}
	}

	public interface InstanceOnLongClickedListener {
		void showPopup(View view);
	}

}
