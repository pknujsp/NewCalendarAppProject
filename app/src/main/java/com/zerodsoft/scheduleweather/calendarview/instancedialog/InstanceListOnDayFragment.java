package com.zerodsoft.scheduleweather.calendarview.instancedialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.CalendarContract;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.activity.main.AppMainActivity;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendar.CommonPopupMenu;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.instancedialog.adapter.EventsInfoRecyclerViewAdapter;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IRefreshView;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.databinding.FragmentMonthEventsInfoBinding;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.SneakyThrows;

public class InstanceListOnDayFragment extends DialogFragment implements EventsInfoRecyclerViewAdapter.InstanceOnLongClickedListener, IRefreshView
{
    public static final String TAG = "MonthEventsInfoFragment";

    private final IConnectedCalendars iConnectedCalendars;
    private final OnEventItemClickListener onEventItemClickListener;
    private final IRefreshView iRefreshView;

    private CalendarViewModel viewModel;
    private FragmentMonthEventsInfoBinding binding;

    private Long begin;
    private Long end;
    private ContentValues instance;

    private EventsInfoRecyclerViewAdapter adapter;

    private final CommonPopupMenu commonPopupMenu = new CommonPopupMenu()
    {
        @Override
        public void onExceptedInstance(boolean isSuccessful)
        {
            if (isSuccessful)
            {
                iRefreshView.refreshView();
                refreshView();
            }
        }

        @Override
        public void onDeletedInstance(boolean isSuccessful)
        {
            if (isSuccessful)
            {
                iRefreshView.refreshView();
                refreshView();
            }
        }
    };

    public InstanceListOnDayFragment(IConnectedCalendars iConnectedCalendars, Fragment fragment)
    {
        this.onEventItemClickListener = (OnEventItemClickListener) fragment;
        this.iRefreshView = (IRefreshView) fragment;
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
        binding = FragmentMonthEventsInfoBinding.inflate(inflater);
        return binding.getRoot();
    }

    @SneakyThrows
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        binding.eventsInfoDay.setText(ClockUtil.YYYY_M_D_E.format(begin));

        binding.eventsInfoEventsList.addItemDecoration(new RecyclerViewItemDecoration(getContext()));
        binding.eventsInfoEventsList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        adapter = new EventsInfoRecyclerViewAdapter(this, onEventItemClickListener, begin, end);
        binding.eventsInfoEventsList.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        setData(viewModel.getInstances(begin, end));
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

    @Override
    public void showPopup(View view)
    {
        EventsInfoRecyclerViewAdapter.InstanceTagHolder holder = (EventsInfoRecyclerViewAdapter.InstanceTagHolder) view.getTag();
        instance = holder.instance;
        commonPopupMenu.createInstancePopupMenu(instance, getActivity(), view, Gravity.CENTER);
    }

    private void setData(Map<Integer, CalendarInstance> resultMap)
    {
         /* 현재 날짜가 20201010이고, 20201009에 allday 인스턴스가 있는 경우에 이 인스턴스의 end값이 20201010 0시 0분
              이라서 20201010의 인스턴스로 잡힌다.
             */

        //선택되지 않은 캘린더는 제외
        List<ContentValues> connectedCalendars = iConnectedCalendars.getConnectedCalendars();
        Set<Integer> connectedCalendarIdSet = new HashSet<>();

        for (ContentValues calendar : connectedCalendars)
        {
            connectedCalendarIdSet.add(calendar.getAsInteger(CalendarContract.Calendars._ID));
        }

        List<ContentValues> instances = new ArrayList<>();

        for (Integer calendarIdKey : connectedCalendarIdSet)
        {
            if (resultMap.containsKey(calendarIdKey))
            {
                instances.addAll(resultMap.get(calendarIdKey).getInstanceList());
            }
        }

        // 데이터를 일정 길이의 내림차순으로 정렬
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


    @Override
    public void refreshView()
    {
        setData(viewModel.getInstances(begin, end));
    }

    static class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration
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

