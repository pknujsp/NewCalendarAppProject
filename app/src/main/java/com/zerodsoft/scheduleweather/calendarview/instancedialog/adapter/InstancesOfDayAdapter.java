package com.zerodsoft.scheduleweather.calendarview.instancedialog.adapter;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.instancedialog.InstancesOfDayView;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemLongClickListener;

import java.util.Calendar;

public class InstancesOfDayAdapter extends RecyclerView.Adapter<InstancesOfDayAdapter.InstancesViewHolder>
{
    private final SparseArray<InstancesViewHolder> viewHolderSparseArray = new SparseArray<>();
    private final OnEventItemClickListener onEventItemClickListener;
    private final OnEventItemLongClickListener onEventItemLongClickListener;
    private final IConnectedCalendars iConnectedCalendars;
    private final IControlEvent iControlEvent;
    private final Calendar beginCalendar;
    private final Calendar endCalendar;

    public InstancesOfDayAdapter(long begin, long end, OnEventItemClickListener onEventItemClickListener, OnEventItemLongClickListener onEventItemLongClickListener
            , IConnectedCalendars iConnectedCalendars, IControlEvent iControlEvent)
    {
        this.beginCalendar = Calendar.getInstance();
        this.endCalendar = Calendar.getInstance();
        this.beginCalendar.setTimeInMillis(begin);
        this.endCalendar.setTimeInMillis(end);

        this.onEventItemClickListener = onEventItemClickListener;
        this.onEventItemLongClickListener = onEventItemLongClickListener;
        this.iConnectedCalendars = iConnectedCalendars;
        this.iControlEvent = iControlEvent;
    }

    @NonNull
    @Override
    public InstancesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new InstancesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.instances_dialog_viewpager_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InstancesViewHolder holder, int position)
    {
        holder.onBind();
        viewHolderSparseArray.put(position, holder);
    }

    @Override
    public void onViewRecycled(@NonNull InstancesViewHolder holder)
    {
        viewHolderSparseArray.remove(holder.getOldPosition());
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount()
    {
        return Integer.MAX_VALUE;
    }

    public void refresh(int position)
    {
        viewHolderSparseArray.get(position).instancesOfDayView.refresh();
    }

    class InstancesViewHolder extends RecyclerView.ViewHolder
    {
        private InstancesOfDayView instancesOfDayView;

        public InstancesViewHolder(@NonNull View itemView)
        {
            super(itemView);
            instancesOfDayView = new InstancesOfDayView(itemView);
        }

        public void onBind()
        {
            Calendar copiedCalendar = (Calendar) beginCalendar.clone();
            copiedCalendar.add(Calendar.DATE, getAdapterPosition() - EventTransactionFragment.FIRST_VIEW_POSITION);
            instancesOfDayView.init(copiedCalendar, onEventItemLongClickListener, onEventItemClickListener, iControlEvent, iConnectedCalendars);
        }
    }
}
