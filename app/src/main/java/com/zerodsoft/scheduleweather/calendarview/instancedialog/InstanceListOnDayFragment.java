package com.zerodsoft.scheduleweather.calendarview.instancedialog;

import android.annotation.SuppressLint;
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
import java.util.List;

import lombok.SneakyThrows;

public class InstanceListOnDayFragment extends DialogFragment implements EventsInfoRecyclerViewAdapter.InstanceOnLongClickedListener
{
    public static final String TAG = "MonthEventsInfoFragment";

    private CalendarViewModel viewModel;
    private FragmentMonthEventsInfoBinding binding;

    private Long begin;
    private Long end;
    private ContentValues instance;
    private final IConnectedCalendars iConnectedCalendars;
    private final OnEventItemClickListener onEventItemClickListener;
    private final IRefreshView iRefreshView;
    private EventsInfoRecyclerViewAdapter adapter;

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
        binding.eventsInfoEventsList.addItemDecoration(new RecyclerViewItemDecoration(getContext()));
        binding.eventsInfoEventsList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        adapter = new EventsInfoRecyclerViewAdapter(this, onEventItemClickListener, begin, end);
        binding.eventsInfoEventsList.setAdapter(adapter);

        binding.eventsInfoDay.setText(ClockUtil.YYYY_M_D_E.format(begin));

        viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        // setData(viewModel.getInstances(begin, end));
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

        PopupMenu popupMenu = new PopupMenu(getContext(), view, Gravity.BOTTOM);
        popupMenu.getMenuInflater().inflate(R.menu.edit_instance_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.edit_instance:
                    {
                        //인스턴스 수정 액티비티 실행
                        /*
                        Intent intent = new Intent(EventActivity.this, EditEventActivity.class);
                        intent.putExtra("requestCode", EventDataController.MODIFY_EVENT);
                        intent.putExtra("calendarId", calendarId.intValue());
                        intent.putExtra("eventId", eventId.longValue());
                        startActivity(intent);
                         */
                        Toast.makeText(getActivity(), "작성 중", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.delete_instance:
                    {
                        //인스턴스 수정 다이얼로그 표시
                        //이번 일정만 삭제, 향후 모든 일정 삭제, 모든 일정 삭제
                        /*
                        반복없는 이벤트 인 경우 : 일정 삭제
                       반복있는 이벤트 인 경우 : 이번 일정만 삭제, 향후 모든 일정 삭제, 모든 일정 삭제
                          */

                        String[] items = null;

                        if (instance.getAsString(CalendarContract.Instances.RRULE) != null)
                        {
                            items = new String[]{getString(R.string.remove_this_instance), getString(R.string.remove_all_future_instance_including_current_instance)
                                    , getString(R.string.remove_event)};
                        } else
                        {
                            items = new String[]{getString(R.string.remove_event)};
                        }
                        new MaterialAlertDialogBuilder(getActivity()).setTitle(getString(R.string.remove_event))
                                .setItems(items, new DialogInterface.OnClickListener()
                                {
                                    @SneakyThrows
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int index)
                                    {
                                        if (instance.getAsString(CalendarContract.Instances.RRULE) != null)
                                        {
                                            switch (index)
                                            {
                                                case 0:
                                                    // 이번 일정만 삭제
                                                    // 완성
                                                    exceptThisInstance(instance);
                                                    break;
                                                case 1:
                                                    // 향후 모든 일정만 삭제
                                                    // deleteSubsequentIncludingThis();
                                                    Toast.makeText(getActivity(), "작성 중", Toast.LENGTH_SHORT).show();
                                                    break;
                                                case 2:
                                                    // 모든 일정 삭제
                                                    deleteEvent(instance);
                                                    break;
                                            }
                                        } else
                                        {
                                            switch (index)
                                            {
                                                case 0:
                                                    // 모든 일정 삭제
                                                    deleteEvent(instance);
                                                    break;
                                            }
                                        }
                                    }
                                }).create().show();
                        break;
                    }
                }
                return true;
            }
        });

        popupMenu.show();
    }

    private void exceptThisInstance(ContentValues instance)
    {
        viewModel.deleteInstance(instance.getAsLong(CalendarContract.Instances.BEGIN),
                instance.getAsLong(CalendarContract.Instances.EVENT_ID));
        //setData(viewModel.getInstances(begin, end));
        iRefreshView.refreshView();
    }

    private void deleteEvent(ContentValues instance)
    {
        // 참석자 - 알림 - 이벤트 순으로 삭제 (외래키 때문)
        // db column error
        viewModel.deleteEvent(instance.getAsInteger(CalendarContract.Instances.CALENDAR_ID), instance.getAsLong(CalendarContract.Instances.EVENT_ID));
        // 삭제 완료 후 캘린더 화면으로 나가고, 새로고침한다.
        //  setData(viewModel.getInstances(begin, end));
        iRefreshView.refreshView();
    }

    private void setData(List<CalendarInstance> calendarInstances)
    {
         /* 현재 날짜가 20201010이고, 20201009에 allday 인스턴스가 있는 경우에 이 인스턴스의 end값이 20201010 0시 0분
              이라서 20201010의 인스턴스로 잡힌다.
             */

        List<ContentValues> instances = new ArrayList<>();
        // 인스턴스 목록 표시
        for (CalendarInstance calendarInstance : calendarInstances)
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

    private final ActivityResultLauncher<String> deletePermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>()
            {
                @Override
                public void onActivityResult(Boolean result)
                {
                    deleteEvent(instance);
                }
            });

    private final ActivityResultLauncher<String> exceptPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>()
            {
                @Override
                public void onActivityResult(Boolean result)
                {
                    exceptThisInstance(instance);
                }
            });

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

