package com.zerodsoft.scheduleweather.calendarview.instancedialog.adapter;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.instancedialog.InstancesOfDayView;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IRefreshView;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemLongClickListener;

import java.util.Calendar;

public class InstancesOfDayAdapter extends RecyclerView.Adapter<InstancesOfDayAdapter.InstancesViewHolder> {
	private final SparseArray<InstancesViewHolder> viewHolderSparseArray = new SparseArray<>();
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
		viewHolderSparseArray.put(position, holder);
	}

	@Override
	public void onViewRecycled(@NonNull InstancesViewHolder holder) {
		viewHolderSparseArray.remove(holder.getOldPosition());
		super.onViewRecycled(holder);
	}

	public boolean containsPosition(int position) {
		return viewHolderSparseArray.get(position) != null;
	}

	@Override
	public int getItemCount() {
		return Integer.MAX_VALUE;
	}

	public void refresh(int position) {
		viewHolderSparseArray.get(position).instancesOfDayView.refresh();
	}

	class InstancesViewHolder extends RecyclerView.ViewHolder {
		private InstancesOfDayView instancesOfDayView;

		public InstancesViewHolder(@NonNull View itemView) {
			super(itemView);
			instancesOfDayView = new InstancesOfDayView(itemView);
		}

		public void onBind() {
			Calendar copiedCalendar = (Calendar) beginCalendar.clone();
			copiedCalendar.add(Calendar.DATE, getBindingAdapterPosition() - EventTransactionFragment.FIRST_VIEW_POSITION);
			instancesOfDayView.init(copiedCalendar, onEventItemLongClickListener, onEventItemClickListener, iControlEvent, iConnectedCalendars,
					instanceDialogMenuListener, iRefreshView, deleteEventsListener);
		}
	}
}
