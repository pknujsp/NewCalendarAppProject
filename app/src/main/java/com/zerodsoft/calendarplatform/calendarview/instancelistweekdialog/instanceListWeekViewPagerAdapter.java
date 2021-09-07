package com.zerodsoft.calendarplatform.calendarview.instancelistweekdialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.calendarview.EventTransactionFragment;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.calendarplatform.calendarview.interfaces.IControlEvent;
import com.zerodsoft.calendarplatform.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.calendarplatform.calendarview.interfaces.OnEventItemLongClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class instanceListWeekViewPagerAdapter extends RecyclerView.Adapter<instanceListWeekViewPagerAdapter.InstanceListWeekViewHolder> {
	private final OnEventItemClickListener onEventItemClickListener;
	private final OnEventItemLongClickListener onEventItemLongClickListener;
	private final IConnectedCalendars iConnectedCalendars;
	private final IControlEvent iControlEvent;
	private final Calendar beginCalendar;

	public instanceListWeekViewPagerAdapter(long begin, OnEventItemClickListener onEventItemClickListener
			, IConnectedCalendars iConnectedCalendars, Fragment fragment) {
		this.beginCalendar = Calendar.getInstance();
		this.beginCalendar.setTimeInMillis(begin);

		// 날짜를 일요일 0시 0분으로 설정
		int amount = -(beginCalendar.get(Calendar.DAY_OF_WEEK) - 1);

		beginCalendar.add(Calendar.DAY_OF_YEAR, amount);
		beginCalendar.set(Calendar.HOUR_OF_DAY, 0);
		beginCalendar.set(Calendar.MINUTE, 0);
		beginCalendar.set(Calendar.SECOND, 0);
		beginCalendar.set(Calendar.MILLISECOND, 0);

		this.onEventItemClickListener = onEventItemClickListener;
		this.iConnectedCalendars = iConnectedCalendars;
		this.onEventItemLongClickListener = (OnEventItemLongClickListener) fragment;
		this.iControlEvent = (IControlEvent) fragment;
	}

	@NonNull
	@NotNull
	@Override
	public InstanceListWeekViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		return new InstanceListWeekViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.instance_list_week_item_view,
				parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull InstanceListWeekViewHolder holder, int position) {
		holder.onBind();
	}

	@Override
	public void onViewRecycled(@NonNull @NotNull InstanceListWeekViewHolder holder) {
		holder.onRecycled();
		super.onViewRecycled(holder);
	}

	@Override
	public int getItemCount() {
		return Integer.MAX_VALUE;
	}

	class InstanceListWeekViewHolder extends RecyclerView.ViewHolder {
		private InstanceListWeekView instanceListWeekView;

		public InstanceListWeekViewHolder(@NonNull View itemView) {
			super(itemView);
			instanceListWeekView = new InstanceListWeekView(itemView.getContext(), onEventItemLongClickListener, onEventItemClickListener,
					iControlEvent,
					iConnectedCalendars);
			((FrameLayout) itemView.findViewById(R.id.root_layout)).addView(instanceListWeekView);
		}

		public void onBind() {
			Calendar copiedCalendar = (Calendar) beginCalendar.clone();
			copiedCalendar.add(Calendar.WEEK_OF_YEAR, getBindingAdapterPosition() - EventTransactionFragment.FIRST_VIEW_POSITION);
			instanceListWeekView.init(copiedCalendar);
		}

		public void onRecycled() {
			instanceListWeekView.clear();
		}
	}
}
