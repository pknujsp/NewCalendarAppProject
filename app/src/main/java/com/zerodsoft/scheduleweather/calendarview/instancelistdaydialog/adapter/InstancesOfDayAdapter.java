package com.zerodsoft.scheduleweather.calendarview.instancelistdaydialog.adapter;

import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.instancelistdaydialog.InstancesOfDayView;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IRefreshView;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.scheduleweather.common.interfaces.OnViewPagerPageListener;

import java.util.Calendar;
import java.util.Set;

public class InstancesOfDayAdapter extends RecyclerView.Adapter<InstancesOfDayAdapter.InstancesViewHolder> implements OnViewPagerPageListener {
	private final OnEventItemClickListener onEventItemClickListener;
	private final OnEventItemLongClickListener onEventItemLongClickListener;
	private final IConnectedCalendars iConnectedCalendars;
	private final IControlEvent iControlEvent;
	private final IRefreshView iRefreshView;
	private final InstancesOfDayView.DeleteEventsListener deleteEventsListener;
	private final InstancesOfDayView.InstanceDialogMenuListener instanceDialogMenuListener;
	private final Calendar beginCalendar;
	private final Calendar endCalendar;

	public InstancesOfDayAdapter(long begin, long end, OnEventItemClickListener onEventItemClickListener
			, IConnectedCalendars iConnectedCalendars, Fragment fragment) {
		this.beginCalendar = Calendar.getInstance();
		this.endCalendar = Calendar.getInstance();
		this.beginCalendar.setTimeInMillis(begin);
		this.endCalendar.setTimeInMillis(end);

		this.onEventItemClickListener = onEventItemClickListener;
		this.iConnectedCalendars = iConnectedCalendars;
		this.onEventItemLongClickListener = (OnEventItemLongClickListener) fragment;
		this.iControlEvent = (IControlEvent) fragment;
		this.instanceDialogMenuListener = (InstancesOfDayView.InstanceDialogMenuListener) fragment;
		this.deleteEventsListener = (InstancesOfDayView.DeleteEventsListener) fragment;
		this.iRefreshView = (IRefreshView) fragment;
	}

	@NonNull
	@Override
	public InstancesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new InstancesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.instances_dialog_viewpager_item, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull InstancesViewHolder holder, int position) {
		holder.onBind();
	}

	@Override
	public void onViewRecycled(@NonNull InstancesViewHolder holder) {
		super.onViewRecycled(holder);
	}


	@Override
	public int getItemCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public void onPageChanged() {

	}

	@Override
	public void onPageChanged(int position) {

	}

	class InstancesViewHolder extends RecyclerView.ViewHolder {
		InstancesOfDayView instancesOfDayView;

		public InstancesViewHolder(@NonNull View itemView) {
			super(itemView);
			instancesOfDayView = new InstancesOfDayView(itemView);
			instancesOfDayView.init(onEventItemLongClickListener, onEventItemClickListener, iControlEvent, iConnectedCalendars,
					instanceDialogMenuListener, iRefreshView, deleteEventsListener);
		}

		public void onBind() {
			Calendar copiedCalendar = (Calendar) beginCalendar.clone();
			copiedCalendar.add(Calendar.DATE, getBindingAdapterPosition() - EventTransactionFragment.FIRST_VIEW_POSITION);
			instancesOfDayView.init(copiedCalendar);
		}


	}
}
