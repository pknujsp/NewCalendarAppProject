package com.zerodsoft.scheduleweather.calendarview.instancedialog;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.CalendarContract;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.zerodsoft.scheduleweather.activity.main.AppMainActivity;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.adapter.EventsInfoRecyclerViewAdapter;
import com.zerodsoft.scheduleweather.calendarview.callback.EventCallback;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class InstanceListOnDayFragment extends DialogFragment
{
    public static final String TAG = "MonthEventsInfoFragment";

    private CalendarViewModel viewModel;

    private Long begin;
    private Long end;
    private final IConnectedCalendars iConnectedCalendars;
    private final OnEventItemClickListener onEventItemClickListener;
    private EventsInfoRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    public InstanceListOnDayFragment(IConnectedCalendars iConnectedCalendars, OnEventItemClickListener onEventItemClickListener)
    {
        this.onEventItemClickListener = onEventItemClickListener;
        this.iConnectedCalendars = iConnectedCalendars;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();

        begin = bundle.getLong("begin");
        end = bundle.getLong("end");
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
        adapter = new EventsInfoRecyclerViewAdapter(onEventItemClickListener, begin, end);
        recyclerView.setAdapter(adapter);

        ((TextView) view.findViewById(R.id.events_info_day)).setText(ClockUtil.YYYY_M_D_E.format(begin));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        viewModel.init(getContext());
        viewModel.getInstanceList(iConnectedCalendars.getConnectedCalendars(), begin, end, new EventCallback<List<CalendarInstance>>()
        {
            @Override
            public void onResult(List<CalendarInstance> e)
            {
                if (!e.isEmpty())
                {
                    /* 현재 날짜가 20201010이고, 20201009에 allday 인스턴스가 있는 경우에 이 인스턴스의 end값이 20201010 0시 0분
                    이라서 20201010의 인스턴스로 잡힌다.
                     */

                    List<ContentValues> instances = new ArrayList<>();
                    // 인스턴스 목록 표시
                    for (CalendarInstance calendarInstance : e)
                    {
                        instances.addAll(calendarInstance.getInstanceList());
                        // 데이터를 일정 길이의 내림차순으로 정렬
                    }

                    List<Integer> removeIndexList = new ArrayList<>();
                    for (int i = 0; i < instances.size(); i++)
                    {
                        if (instances.get(i).getAsBoolean(CalendarContract.Instances.ALL_DAY))
                        {
                            if (ClockUtil.areSameDate(instances.get(i).getAsLong(CalendarContract.Instances.END),
                                    begin) || ClockUtil.areSameDate(instances.get(i).getAsLong(CalendarContract.Instances.BEGIN), end))
                            {
                                removeIndexList.add(i);
                            }
                        } else
                        {
                            if (ClockUtil.areSameHourMinute(instances.get(i).getAsLong(CalendarContract.Instances.END), begin)
                                    || ClockUtil.areSameHourMinute(instances.get(i).getAsLong(CalendarContract.Instances.BEGIN), end))
                            {
                                removeIndexList.add(i);
                            }
                        }
                    }

                    for (int i = removeIndexList.size() - 1; i >= 0; i--)
                    {
                        instances.remove(removeIndexList.get(i).intValue());
                    }

                    Collections.sort(instances, EventUtil.INSTANCE_COMPARATOR);
                    adapter.setInstances(instances);
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }


    @Override
    public void onResume()
    {
        super.onResume();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = (int) (AppMainActivity.getDisplayWidth() * 0.9);
        layoutParams.height = (int) (AppMainActivity.getDisplayHeight() * 0.7);
        window.setAttributes(layoutParams);
    }

    @Override
    public void onStop()
    {
        super.onStop();
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

