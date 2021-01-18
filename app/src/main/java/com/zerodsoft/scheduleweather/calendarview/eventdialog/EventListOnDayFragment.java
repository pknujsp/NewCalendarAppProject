package com.zerodsoft.scheduleweather.calendarview.eventdialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.zerodsoft.scheduleweather.activity.main.AppMainActivity;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.adapter.EventsInfoRecyclerViewAdapter;
import com.zerodsoft.scheduleweather.calendarview.viewmodel.EventsInfoViewModel;
import com.zerodsoft.scheduleweather.event.EventActivity;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.Date;
import java.util.List;

public class EventListOnDayFragment extends DialogFragment
{
    public static final String TAG = "MonthEventsInfoFragment";

    private EventsInfoViewModel viewModel;

    private Date startDate;
    private Date endDate;
    private EventsInfoRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    public EventListOnDayFragment(Date startDate, Date endDate)
    {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public EventListOnDayFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EventsInfoViewModel.class);
        viewModel.selectSchedules(startDate, endDate);
        viewModel.getSchedulesMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<ScheduleDTO>>()
        {
            @Override
            public void onChanged(List<ScheduleDTO> schedules)
            {
                adapter.setSchedulesList(schedules);
                adapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_month_events_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.events_info_events_list);
        recyclerView.addItemDecoration(new RecyclerViewItemDecoration(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        adapter = new EventsInfoRecyclerViewAdapter(EventListOnDayFragment.this, startDate, endDate);
        recyclerView.setAdapter(adapter);
        ((TextView) view.findViewById(R.id.events_info_day)).setText(ClockUtil.YYYY_년_M_월_D_일_E.format(startDate));
    }


    @Override
    public void onResume()
    {
        super.onResume();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = (int) (AppMainActivity.getDisplayWidth() * 0.8);
        layoutParams.height = (int) (AppMainActivity.getDisplayHeight() * 0.7);
        window.setAttributes(layoutParams);
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    public void showSchedule(int scheduleId)
    {
        Intent intent = new Intent(getActivity(), EventActivity.class);
        intent.putExtra("scheduleId", scheduleId);
        startActivity(intent);
    }

    class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration
    {


        private final int decorationHeight;
        private Context context;

        public RecyclerViewItemDecoration(Context context)
        {
            this.context = context;
            decorationHeight = context.getResources().getDimensionPixelSize(R.dimen.event_info_listview_spacing);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
        {
            super.getItemOffsets(outRect, view, parent, state);

            if (parent != null && view != null)
            {
                int itemPosition = parent.getChildAdapterPosition(view);
                int totalCount = parent.getAdapter().getItemCount();

                if (itemPosition >= 0 && itemPosition < totalCount - 1)
                {
                    outRect.bottom = decorationHeight;
                }

            }

        }

    }
}

