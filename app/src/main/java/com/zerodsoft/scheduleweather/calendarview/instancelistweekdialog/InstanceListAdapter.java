package com.zerodsoft.scheduleweather.calendarview.instancelistweekdialog;

import android.content.ContentValues;
import android.content.Context;
import android.provider.CalendarContract;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.scheduleweather.common.view.CustomProgressView;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class InstanceListAdapter extends RecyclerView.Adapter<InstanceListAdapter.ViewHolder> {
	private final OnEventItemClickListener onEventItemClickListener;
	private final OnEventItemLongClickListener onEventItemLongClickListener;
	private final long viewBegin;
	private final long viewEnd;
	private List<List<ContentValues>> instancesList = new ArrayList<>();
	private List<Date> dateList = new LinkedList<>();

	public InstanceListAdapter(OnEventItemClickListener onEventItemClickListener, OnEventItemLongClickListener onEventItemLongClickListener, long viewBegin, long viewEnd) {
		this.onEventItemClickListener = onEventItemClickListener;
		this.onEventItemLongClickListener = onEventItemLongClickListener;
		this.viewBegin = viewBegin;
		this.viewEnd = viewEnd;
	}

	public void setInstancesList(List<List<ContentValues>> instancesList) {
		this.instancesList = instancesList;
	}

	public void setDateList(List<Date> dateList) {
		this.dateList = dateList;
	}

	public List<List<ContentValues>> getInstancesList() {
		return instancesList;
	}

	@NonNull
	@NotNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.instance_list_week_list_item_view, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
		holder.onBind();
	}

	@Override
	public int getItemCount() {
		return instancesList.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		TextView dateTextView;
		LinearLayout listLayout;
		CustomProgressView customProgressView;

		public ViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			dateTextView = itemView.findViewById(R.id.date_textview);
			listLayout = itemView.findViewById(R.id.instance_list);
			customProgressView = itemView.findViewById(R.id.custom_progress_view);
			customProgressView.setContentView(listLayout);
		}

		public void onBind() {
			if (listLayout.getChildCount() > 0) {
				listLayout.removeAllViews();
			}

			dateTextView.setText(ClockUtil.YYYY_M_D_E.format(dateList.get(getBindingAdapterPosition())));

			final int bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f,
					itemView.getContext().getResources().getDisplayMetrics());
			final int instanceViewHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42f,
					itemView.getContext().getResources().getDisplayMetrics());

			List<ContentValues> instanceList = instancesList.get(getBindingAdapterPosition());
			final int totalCount = instanceList.size();
			if (totalCount == 0) {
				customProgressView.onFailedProcessingData(itemView.getContext().getString(R.string.not_data));
			} else {
				customProgressView.onSuccessfulProcessingData();
			}

			int index = 0;

			LayoutInflater layoutInflater = (LayoutInflater) itemView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			for (ContentValues instance : instanceList) {
				LinearLayout instanceView = (LinearLayout) layoutInflater.inflate(R.layout.events_info_list_item, null);
				//remove checkbox
				instanceView.removeView(instanceView.findViewById(R.id.checkbox));

				TextView instanceTextView = (TextView) instanceView.findViewById(R.id.instance_item);
				instanceTextView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onEventItemClickListener.onClicked(instance.getAsInteger(CalendarContract.Instances.CALENDAR_ID),
								instance.getAsLong(CalendarContract.Instances._ID), instance.getAsLong(CalendarContract.Instances.EVENT_ID)
								, instance.getAsLong(CalendarContract.Instances.BEGIN), instance.getAsLong(CalendarContract.Instances.END));
					}
				});
				instanceTextView.setClickable(true);

				if (instance.getAsInteger(CalendarContract.Instances.CALENDAR_ACCESS_LEVEL) == CalendarContract.Instances.CAL_ACCESS_READ) {
					instanceTextView.setLongClickable(false);
				} else {
					instanceTextView.setLongClickable(true);
					instanceTextView.setOnLongClickListener(new View.OnLongClickListener() {
						@Override
						public boolean onLongClick(View v) {
							onEventItemLongClickListener.createInstancePopupMenu(instance, v, Gravity.CENTER);
							return true;
						}
					});
				}

				instanceTextView.setBackgroundColor(EventUtil.getColor(instance.getAsInteger(CalendarContract.Instances.EVENT_COLOR)));
				if (instance.getAsString(CalendarContract.Instances.TITLE) != null) {
					if (instance.getAsString(CalendarContract.Instances.TITLE).isEmpty()) {
						instanceTextView.setText(itemView.getContext().getString(R.string.empty_title));
					} else {
						instanceTextView.setText(instance.getAsString(CalendarContract.Instances.TITLE));
					}
				} else {
					instanceTextView.setText(itemView.getContext().getString(R.string.empty_title));
				}

				/*
				int[] margin = EventUtil.getViewSideMargin(instance.getAsLong(CalendarContract.Instances.BEGIN)
						, instance.getAsLong(CalendarContract.Instances.END)
						, viewBegin, viewEnd, 16, instance.getAsInteger(CalendarContract.Instances.ALL_DAY) == 1);

				 */
				LinearLayout.LayoutParams instanceViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						instanceViewHeight);
				//	instanceViewLayoutParams.leftMargin = margin[0];
				//	instanceViewLayoutParams.rightMargin = margin[1];

				if (index++ < totalCount - 1) {
					instanceViewLayoutParams.bottomMargin = bottomMargin;
				}
				listLayout.addView(instanceView, instanceViewLayoutParams);
			}
		}
	}
}
