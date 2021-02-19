package com.zerodsoft.scheduleweather.activity.main;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncStatusObserver;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.util.ArraySet;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.activity.EditEventActivity;
import com.zerodsoft.scheduleweather.activity.editevent.value.EventDataController;
import com.zerodsoft.scheduleweather.calendarview.CalendarsAdapter;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.day.DayFragment;
import com.zerodsoft.scheduleweather.calendarview.interfaces.ICalendarCheckBox;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IstartActivity;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IToolbar;
import com.zerodsoft.scheduleweather.calendarview.month.MonthFragment;
import com.zerodsoft.scheduleweather.calendarview.week.WeekFragment;
import com.zerodsoft.scheduleweather.databinding.ActivityAppMainBinding;
import com.zerodsoft.scheduleweather.calendar.CalendarProvider;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendar.dto.AccountDto;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppMainActivity extends AppCompatActivity implements ICalendarCheckBox, IToolbar, IConnectedCalendars, IstartActivity
{
    private EventTransactionFragment calendarTransactionFragment;
    private static int DISPLAY_WIDTH = 0;
    private static int DISPLAY_HEIGHT = 0;
    private static Map<String, ContentValues> connectedCalendarMap = new HashMap<>();
    private static List<ContentValues> connectedCalendarList = new ArrayList<>();
    private List<AccountDto> accountList;

    private ActivityAppMainBinding mainBinding;
    private CalendarViewModel calendarViewModel;
    private CalendarsAdapter calendarsAdapter;
    private TextView currMonthTextView;
    private Object statusHandle;

    public static final int TYPE_DAY = 10;
    public static final int TYPE_WEEK = 20;
    public static final int TYPE_MONTH = 30;
    public static final int DELETED_EVENT = 10000;
    public static final int EXCEPTED_INSTANCE = 10001;

    public static int getDisplayHeight()
    {
        return DISPLAY_HEIGHT;
    }

    public static int getDisplayWidth()
    {
        return DISPLAY_WIDTH;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_app_main);

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);

        DISPLAY_WIDTH = point.x;
        DISPLAY_HEIGHT = point.y;

        init();
        initCalendarViewModel();
        setNavigationView();
    }

    private void init()
    {
        KakaoLocalApiCategoryUtil.loadCategories(getApplicationContext());

        setSupportActionBar(mainBinding.mainToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        View customToolbar = getLayoutInflater().inflate(R.layout.app_main_toolbar, null);
        actionBar.setCustomView(customToolbar);
        currMonthTextView = (TextView) customToolbar.findViewById(R.id.calendar_month);

        calendarTransactionFragment = new EventTransactionFragment(this);
        getSupportFragmentManager().beginTransaction().add(R.id.calendar_layout, calendarTransactionFragment, EventTransactionFragment.TAG).commit();
    }

    private final ContentObserver contentObserver = new ContentObserver(new Handler())
    {
        @Override
        public boolean deliverSelfNotifications()
        {
            return true;
        }

        @Override
        public void onChange(boolean selfChange)
        {
            AccountManager accountManager = AccountManager.get(getApplicationContext());
            Account[] accounts = accountManager.getAccountsByType("com.google");

            boolean check = false;
            for (Account account : accounts)
            {
                if (ContentResolver.isSyncActive(account, CalendarContract.AUTHORITY))
                {
                    check = true;
                } else
                {
                    break;
                }
            }

            if (check)
            {
                Toast.makeText(AppMainActivity.this, "SYNCED", Toast.LENGTH_SHORT).show();
                calendarTransactionFragment.refreshCalendar();
            }
        }
    };


    private void initCalendarViewModel()
    {
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        calendarViewModel.init(getApplicationContext());

        //권한 확인
        int permissionState = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CALENDAR);
        if (permissionState == PackageManager.PERMISSION_GRANTED)
        {
            calendarViewModel.getAllCalendars();
        } else
        {
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, CalendarProvider.REQUEST_READ_CALENDAR);
        }

        calendarViewModel.getAllCalendarListLiveData().observe(this, new Observer<DataWrapper<List<ContentValues>>>()
        {
            @Override
            public void onChanged(DataWrapper<List<ContentValues>> listDataWrapper)
            {
                if (listDataWrapper.getData() != null)
                {
                    // accountName을 기준으로 맵 구성
                    Map<String, AccountDto> accountMap = new HashMap<>();
                    List<ContentValues> calendarList = listDataWrapper.getData();

                    for (ContentValues calendar : calendarList)
                    {
                        String accountName = calendar.getAsString(CalendarContract.Calendars.ACCOUNT_NAME);
                        String ownerAccount = calendar.getAsString(CalendarContract.Calendars.OWNER_ACCOUNT);
                        String accountType = calendar.getAsString(CalendarContract.Calendars.ACCOUNT_TYPE);

                        if (accountMap.containsKey(accountName))
                        {
                            accountMap.get(accountName).addCalendar(calendar);
                        } else
                        {
                            AccountDto accountDto = new AccountDto();

                            accountDto.setAccountName(accountName).setAccountType(accountType)
                                    .setOwnerAccount(ownerAccount).addCalendar(calendar);
                            accountMap.put(accountName, accountDto);
                        }
                    }
                    accountList = new ArrayList<>(accountMap.values());


                    SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferences_selected_calendars_key), Context.MODE_PRIVATE);
                    Set<String> selectedCalendarSet = new ArraySet<>();
                    Map<String, String> selectedCalendarsMap = (Map<String, String>) sharedPreferences.getAll();

                    selectedCalendarSet.addAll(selectedCalendarsMap.values());

                    final boolean[][] checkBoxStates = new boolean[accountList.size()][];

                    for (int i = 0; i < accountList.size(); i++)
                    {
                        checkBoxStates[i] = new boolean[accountList.get(i).getCalendars().size()];
                    }

                    for (String key : selectedCalendarSet)
                    {
                        int group = 0;

                        for (AccountDto accountDto : accountList)
                        {
                            List<ContentValues> selectedCalendarList = accountDto.getCalendars();
                            int child = 0;

                            for (ContentValues calendar : selectedCalendarList)
                            {
                                if (key.equals(calendar.getAsString(CalendarContract.Calendars.ACCOUNT_NAME) + "&"
                                        + calendar.getAsString(CalendarContract.Calendars._ID)))
                                {
                                    checkBoxStates[group][child] = true;
                                    connectedCalendarMap.put(key, calendar);
                                    connectedCalendarList.add(calendar);
                                    break;
                                }
                                child++;
                            }
                            group++;
                        }
                    }

                    // 네비게이션 내 캘린더 리스트 구성
                    calendarsAdapter = new CalendarsAdapter(AppMainActivity.this, accountList, checkBoxStates);
                    mainBinding.sideNavCalendarList.setAdapter(calendarsAdapter);
                    mainBinding.sideNavCalendarList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
                    {
                        @Override
                        public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l)
                        {
                            return false;
                        }
                    });
                }
            }
        });

        calendarViewModel.getCalendarLiveData().observe(this, new Observer<DataWrapper<ContentValues>>()
        {
            @Override
            public void onChanged(DataWrapper<ContentValues> contentValuesDataWrapper)
            {

            }
        });

        /*
        calendarViewModel.getCalendarListLiveData().observe(this, new Observer<DataWrapper<List<CalendarListEntry>>>()
        {
            @Override
            public void onChanged(DataWrapper<List<CalendarListEntry>> listDataWrapper)
            {
                if (listDataWrapper.getData() != null)
                {
                    List<CalendarListEntry> calendarList = listDataWrapper.getData();
                    calendarViewModel.getEvents(null);

                    final int DP_32 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, getResources().getDisplayMetrics());
                    sideNavHeaderBinding.googleCalendarList.removeAllViews();

                    for (CalendarListEntry calendarListEntry : calendarList)
                    {
                        MaterialCheckBox calendarCheckBox = new MaterialCheckBox(AppMainActivity.this);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP_32);
                        calendarCheckBox.setLayoutParams(layoutParams);
                        calendarCheckBox.setText(calendarListEntry.getSummary());
                        sideNavHeaderBinding.googleCalendarList.addView(calendarCheckBox);
                    }
                } else if (listDataWrapper.getException() != null)
                {
                    Exception exception = listDataWrapper.getException();

                    if (exception instanceof UserRecoverableAuthIOException)
                    {
                        startActivityForResult(((UserRecoverableAuthIOException) listDataWrapper.getException()).getIntent(), GoogleCalendarApi.REQUEST_AUTHORIZATION);
                    }
                }
            }
        });


         */
    }

    /*
    private void checkSavedGoogleAccount()
    {
        String accountName = getPreferences(Context.MODE_PRIVATE).getString(GoogleCalendarApi.GOOGLE_ACCOUNT_NAME, "");
        if (!accountName.isEmpty())
        {
            // 저장된 계정이 있는 경우
            int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
            if (permission == PackageManager.PERMISSION_GRANTED)
            {
                try
                {
                    calendarViewModel.connect(accountName);
                } catch (IOException | GeneralSecurityException e)
                {
                    e.printStackTrace();
                }
            } else if (permission == PackageManager.PERMISSION_DENIED)
            {
                // 권한 요청 화면 표시
                requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS}, GoogleCalendarApi.REQUEST_PERMISSION_GET_ACCOUNTS_AUTO);
            }
        } else
        {

        }
    }
     */

    private void setNavigationView()
    {
        mainBinding.sideNavMenu.favoriteLocation.setOnClickListener(sideNavOnClickListener);
        mainBinding.sideNavMenu.settings.setOnClickListener(sideNavOnClickListener);
        mainBinding.sideNavCalendarTypes.calendarTypeToggleGroup.addOnButtonCheckedListener(calendarTypeButtonCheckedListener);

        SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_selected_calendar_type_key), Context.MODE_PRIVATE);
        int type = preferences.getInt(getString(R.string.preferences_selected_calendar_type_key), 0);

        switch (type)
        {
            case TYPE_DAY:
                mainBinding.sideNavCalendarTypes.calendarTypeToggleGroup.check(R.id.calendar_type_day);
                break;
            case TYPE_WEEK:
                mainBinding.sideNavCalendarTypes.calendarTypeToggleGroup.check(R.id.calendar_type_week);
                break;
            case TYPE_MONTH:
            case 0:
                mainBinding.sideNavCalendarTypes.calendarTypeToggleGroup.check(R.id.calendar_type_month);
                break;
        }

        /*
        sideNavHeaderBinding.connectGoogle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int permission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.GET_ACCOUNTS);
                if (permission == PackageManager.PERMISSION_GRANTED)
                {
                    calendarViewModel.requestAccountPicker();
                } else if (permission == PackageManager.PERMISSION_DENIED)
                {
                    // 권한 요청 화면 표시
                    requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS}, GoogleCalendarApi.REQUEST_PERMISSION_GET_ACCOUNTS_SELF);
                }
            }
        });
        sideNavHeaderBinding.disconnectGoogle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(GoogleCalendarApi.GOOGLE_ACCOUNT_NAME, "");
                editor.apply();

                calendarViewModel.disconnect();
                calendarViewModel = null;
                initCalendarViewModel();
                Toast.makeText(AppMainActivity.this, getString(R.string.disconnected_google_calendar), Toast.LENGTH_SHORT).show();
            }
        });

         */
    }

    /*
    private void setExpandableListViewHeight()
    {
        ExpandableListView listView = mainBinding.sideNavCalendarList;
        ExpandableListAdapter listAdapter = mainBinding.sideNavCalendarList.getExpandableListAdapter();
        if (listAdapter == null)
        {
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int group = 0; group < listAdapter.getGroupCount(); group++)
        {
            listAdapter.getGr
            for (int child = 0; child < listAdapter.getChildrenCount(group); child++)
            {
                View childView = listAdapter.getC
                childView.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += childView.getMeasuredHeight();
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

     */

    private final MaterialButtonToggleGroup.OnButtonCheckedListener calendarTypeButtonCheckedListener
            = new MaterialButtonToggleGroup.OnButtonCheckedListener()
    {
        @Override
        public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked)
        {
            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.preferences_selected_calendar_type_key), MODE_PRIVATE).edit();

            switch (checkedId)
            {
                case R.id.calendar_type_day:
                    editor.putInt(getString(R.string.preferences_selected_calendar_type_key), TYPE_DAY);
                    calendarTransactionFragment.replaceFragment(DayFragment.TAG);
                    break;
                case R.id.calendar_type_week:
                    editor.putInt(getString(R.string.preferences_selected_calendar_type_key), TYPE_WEEK);
                    calendarTransactionFragment.replaceFragment(WeekFragment.TAG);
                    break;
                case R.id.calendar_type_month:
                    editor.putInt(getString(R.string.preferences_selected_calendar_type_key), TYPE_MONTH);
                    calendarTransactionFragment.replaceFragment(MonthFragment.TAG);
                    break;
            }
            editor.apply();
            mainBinding.drawerLayout.closeDrawer(mainBinding.sideNavigation);
        }
    };

    private final View.OnClickListener sideNavOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.favorite:
                    break;
                case R.id.app_setting:
                    break;
            }
            mainBinding.drawerLayout.closeDrawer(mainBinding.sideNavigation);
        }
    };

    private void expandAllGroup()
    {
        int groupSize = calendarsAdapter.getGroupCount();
        for (int i = 0; i < groupSize; i++)
        {
            mainBinding.sideNavCalendarList.expandGroup(i);
        }
    }

    private void collapseAllGroup()
    {
        int groupSize = calendarsAdapter.getGroupCount();
        for (int i = 0; i < groupSize; i++)
        {
            mainBinding.sideNavCalendarList.collapseGroup(i);
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void onClickToolbar(View view)
    {
        switch (view.getId())
        {
            case R.id.open_navigation_drawer:
                mainBinding.drawerLayout.openDrawer(mainBinding.sideNavigation);
                break;
            case R.id.calendar_month:
                break;
            case R.id.add_schedule:
                Intent intent = new Intent(AppMainActivity.this, EditEventActivity.class);
                intent.putExtra("requestCode", EventDataController.NEW_EVENT);
                startActivityForResult(intent, EventDataController.NEW_EVENT);
                break;
            case R.id.go_to_today:
                calendarTransactionFragment.goToToday();
                break;
            case R.id.refresh_calendar:
                calendarViewModel.syncCalendars();
                break;
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        getContentResolver().registerContentObserver(CalendarContract.Events.CONTENT_URI, true, contentObserver);
        statusHandle = ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE
                , new SyncStatusObserver()
                {
                    @Override
                    public void onStatusChanged(int mask)
                    {
                        if (mask == ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE)
                        {

                        }
                    }
                });
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        getContentResolver().unregisterContentObserver(contentObserver);
        ContentResolver.removeStatusChangeListener(statusHandle);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case EventDataController.NEW_EVENT:
            {
                switch (resultCode)
                {
                    case RESULT_OK:
                        //새로운 일정이 추가됨 -> 달력 이벤트 갱신
                        calendarTransactionFragment.refreshCalendar();
                        break;
                }
            }
            break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case CalendarProvider.REQUEST_READ_CALENDAR:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // 권한 허용됨
                    calendarViewModel.getAllCalendars();
                } else
                {
                    // 권한 거부됨
                }
                break;

            case CalendarProvider.REQUEST_WRITE_CALENDAR:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // 권한 허용됨
                } else
                {
                    // 권한 거부됨
                }
                break;
        }
    }


    @Override
    public void onCheckedBox(String key, ContentValues calendar, boolean state)
    {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferences_selected_calendars_key), Context.MODE_PRIVATE);
        Set<String> selectedCalendarSet = new ArraySet<>();
        Map<String, String> selectedCalendarsMap = (Map<String, String>) sharedPreferences.getAll();

        selectedCalendarSet.addAll(selectedCalendarsMap.values());

        for (String v : selectedCalendarSet)
        {
            if (v.equals(key))
            {
                if (!state)
                {
                    // 같은 값을 가진 것이 이미 추가되어있는 경우 선택해제 하는 것이므로 삭제한다.
                    connectedCalendarMap.remove(key);

                    connectedCalendarList.clear();
                    connectedCalendarList.addAll(connectedCalendarMap.values());

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(key);
                    editor.commit();

                    calendarTransactionFragment.refreshCalendar();
                }
                return;
            }
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        connectedCalendarMap.put(key, calendar);
        connectedCalendarList.add(calendar);
        editor.putString(key, key);
        editor.commit();
        
        calendarTransactionFragment.refreshCalendar();
    }

    @Override
    public void setMonth(Date dateTime)
    {
        currMonthTextView.setText(ClockUtil.YEAR_MONTH_FORMAT.format(dateTime));
    }

    @Override
    public List<ContentValues> getConnectedCalendars()
    {
        return connectedCalendarList;
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    final int resultCode = result.getResultCode();

                    if (resultCode == DELETED_EVENT || resultCode == EXCEPTED_INSTANCE)
                    {
                        calendarTransactionFragment.refreshCalendar();
                    }
                }
            }
    );

    @Override
    public void startActivityResult(Intent intent, int requestCode)
    {
        activityResultLauncher.launch(intent);
    }
}

