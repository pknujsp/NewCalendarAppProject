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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.ContentObserver;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.service.carrier.CarrierMessagingService;
import android.util.ArraySet;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.activity.editevent.activity.EditEventActivity;
import com.zerodsoft.scheduleweather.activity.editevent.value.EventDataController;
import com.zerodsoft.scheduleweather.activity.preferences.SettingsActivity;
import com.zerodsoft.scheduleweather.calendarview.CalendarsAdapter;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.assistantcalendar.assistantcalendar.MonthAssistantCalendarFragment;
import com.zerodsoft.scheduleweather.calendarview.day.DayFragment;
import com.zerodsoft.scheduleweather.calendarview.interfaces.CalendarDateOnClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.ICalendarCheckBox;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IstartActivity;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IToolbar;
import com.zerodsoft.scheduleweather.calendarview.month.MonthFragment;
import com.zerodsoft.scheduleweather.calendarview.week.WeekFragment;
import com.zerodsoft.scheduleweather.databinding.ActivityAppMainBinding;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendar.dto.AccountDto;
import com.zerodsoft.scheduleweather.etc.AppPermission;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.security.MessageDigest;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.SneakyThrows;

public class AppMainActivity extends AppCompatActivity implements ICalendarCheckBox, IToolbar, IConnectedCalendars, IstartActivity, CalendarDateOnClickListener
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

    public static final int TYPE_DAY = 10;
    public static final int TYPE_WEEK = 20;
    public static final int TYPE_MONTH = 30;
    public static final int DELETED_EVENT = 10000;
    public static final int EXCEPTED_INSTANCE = 10001;

    private MonthAssistantCalendarFragment monthAssistantCalendarFragment;
    private Date currentCalendarDate;

    private final View.OnClickListener currMonthOnClickListener = new View.OnClickListener()
    {
        /*
        캘린더의 타입에 따라 다른 정보를 보여준다.
         */
        @Override
        public void onClick(View view)
        {
            FragmentManager fragmentManager = calendarTransactionFragment.getParentFragmentManager();
            if (fragmentManager.findFragmentByTag(WeekFragment.TAG) != null ||
                    fragmentManager.findFragmentByTag(DayFragment.TAG) != null)
            {
                monthAssistantCalendarFragment.setCurrentMonth(currentCalendarDate);

                if (mainBinding.assistantCalendarContainer.getVisibility() != View.VISIBLE)
                {
                    mainBinding.assistantCalendarContainer.setVisibility(View.VISIBLE);
                } else
                {
                    mainBinding.assistantCalendarContainer.setVisibility(View.GONE);
                }
            }
        }
    };

    public static int getDisplayHeight()
    {
        return DISPLAY_HEIGHT;
    }

    public static int getDisplayWidth()
    {
        return DISPLAY_WIDTH;
    }

    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_app_main);

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);

        DISPLAY_WIDTH = point.x;
        DISPLAY_HEIGHT = point.y;

        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);

        if (AppPermission.grantedPermissions(getApplicationContext(), Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR))
        {
            //권한 확인
            List<ContentValues> calendars = calendarViewModel.getAllCalendars();
            initSideCalendars(calendars);
            init();
        } else
        {
            permissionsResultLauncher.launch(new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR});
        }
        //getAppKeyHash();
    }

    private void getAppKeyHash()
    {
        try
        {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures)
            {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("yyg", "key: " + something);
            }
        } catch (Exception e)
        {
        }
    }


    private void init()
    {
        App.setAppSettings(getApplicationContext());
        mainBinding.assistantCalendarContainer.setVisibility(View.GONE);
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
        currMonthTextView.setOnClickListener(currMonthOnClickListener);

        //보조 캘린더 프래그먼트 생성
        monthAssistantCalendarFragment = new MonthAssistantCalendarFragment(this);
        calendarTransactionFragment = new EventTransactionFragment(this, monthAssistantCalendarFragment);

        getSupportFragmentManager().beginTransaction().add(R.id.calendar_layout, calendarTransactionFragment, EventTransactionFragment.TAG)
                .add(R.id.assistant_calendar_container, monthAssistantCalendarFragment, MonthAssistantCalendarFragment.TAG).commit();

        SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_selected_calendar_type_key), Context.MODE_PRIVATE);
        int type = preferences.getInt(getString(R.string.preferences_selected_calendar_type_key), TYPE_MONTH);

        switch (type)
        {
            case TYPE_DAY:
                mainBinding.sideNavCalendarTypes.dayRadio.setVisibility(View.VISIBLE);
                changeCalendar(DayFragment.TAG);
                break;
            case TYPE_WEEK:
                mainBinding.sideNavCalendarTypes.weekRadio.setVisibility(View.VISIBLE);
                changeCalendar(WeekFragment.TAG);
                break;
            case TYPE_MONTH:
            case 0:
                mainBinding.sideNavCalendarTypes.monthRadio.setVisibility(View.VISIBLE);
                changeCalendar(MonthFragment.TAG);
                break;
        }

        mainBinding.sideNavMenu.favoriteLocation.setOnClickListener(sideNavOnClickListener);
        mainBinding.sideNavMenu.settings.setOnClickListener(sideNavOnClickListener);
        mainBinding.sideNavCalendarTypes.calendarTypeDay.setOnClickListener(calendarTypeOnClickListener);
        mainBinding.sideNavCalendarTypes.calendarTypeWeek.setOnClickListener(calendarTypeOnClickListener);
        mainBinding.sideNavCalendarTypes.calendarTypeMonth.setOnClickListener(calendarTypeOnClickListener);
    }

    private final ContentObserver contentObserver = new ContentObserver(new Handler())
    {
        @Override
        public boolean deliverSelfNotifications()
        {
            return true;
        }

        @SneakyThrows
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
                calendarTransactionFragment.refreshView();
            }
        }
    };

    private void initSideCalendars(List<ContentValues> calendarList)
    {
        // accountName을 기준으로 맵 구성
        Map<String, AccountDto> accountMap = new HashMap<>();

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

    private final View.OnClickListener calendarTypeOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.preferences_selected_calendar_type_key), MODE_PRIVATE).edit();

            switch (view.getId())
            {
                case R.id.calendar_type_day:
                    editor.putInt(getString(R.string.preferences_selected_calendar_type_key), TYPE_DAY).apply();
                    changeCalendar(DayFragment.TAG);
                    monthAssistantCalendarFragment.refresh();
                    break;
                case R.id.calendar_type_week:
                    editor.putInt(getString(R.string.preferences_selected_calendar_type_key), TYPE_WEEK).apply();
                    changeCalendar(WeekFragment.TAG);
                    monthAssistantCalendarFragment.refresh();
                    break;
                case R.id.calendar_type_month:
                    editor.putInt(getString(R.string.preferences_selected_calendar_type_key), TYPE_MONTH).apply();
                    changeCalendar(MonthFragment.TAG);
                    break;
            }
            mainBinding.drawerLayout.closeDrawer(mainBinding.sideNavigation);
        }
    };

    private void changeCalendar(String tag)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (tag)
        {
            case DayFragment.TAG:
                if (fragmentManager.findFragmentByTag(DayFragment.TAG) == null)
                {
                    mainBinding.sideNavCalendarTypes.dayRadio.setVisibility(View.VISIBLE);
                    mainBinding.sideNavCalendarTypes.weekRadio.setVisibility(View.GONE);
                    mainBinding.sideNavCalendarTypes.monthRadio.setVisibility(View.GONE);

                    calendarTransactionFragment.replaceFragment(DayFragment.TAG);
                }
                break;
            case WeekFragment.TAG:
                if (fragmentManager.findFragmentByTag(WeekFragment.TAG) == null)
                {
                    mainBinding.sideNavCalendarTypes.dayRadio.setVisibility(View.GONE);
                    mainBinding.sideNavCalendarTypes.weekRadio.setVisibility(View.VISIBLE);
                    mainBinding.sideNavCalendarTypes.monthRadio.setVisibility(View.GONE);

                    calendarTransactionFragment.replaceFragment(WeekFragment.TAG);
                }
                break;
            case MonthFragment.TAG:
                if (fragmentManager.findFragmentByTag(MonthFragment.TAG) == null)
                {
                    mainBinding.sideNavCalendarTypes.dayRadio.setVisibility(View.GONE);
                    mainBinding.sideNavCalendarTypes.weekRadio.setVisibility(View.GONE);
                    mainBinding.sideNavCalendarTypes.monthRadio.setVisibility(View.VISIBLE);

                    calendarTransactionFragment.replaceFragment(MonthFragment.TAG);
                    if (mainBinding.assistantCalendarContainer.getVisibility() == View.VISIBLE)
                    {
                        mainBinding.assistantCalendarContainer.setVisibility(View.GONE);
                    }
                }
                break;
        }
    }

    private final View.OnClickListener sideNavOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.favorite_location:
                    break;
                case R.id.settings:
                    Intent intent = new Intent(AppMainActivity.this, SettingsActivity.class);
                    activityResultLauncher.launch(intent);
                    break;
            }
            mainBinding.drawerLayout.closeDrawer(mainBinding.sideNavigation);
        }
    };

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
                Toast.makeText(AppMainActivity.this, "working", Toast.LENGTH_SHORT).show();
                // calendarViewModel.syncCalendars();
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
        /*
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

         */
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        /*
        getContentResolver().unregisterContentObserver(contentObserver);
        ContentResolver.removeStatusChangeListener(statusHandle);
         */
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @SneakyThrows
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
                        calendarTransactionFragment.refreshView();
                        break;
                }
            }
            break;
        }
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {

                }
            }
    );

    private final ActivityResultLauncher<String[]> permissionsResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>()
            {
                @SneakyThrows
                @Override
                public void onActivityResult(Map<String, Boolean> result)
                {
                    if (result.get(Manifest.permission.READ_CALENDAR))
                    {
                        // 권한 허용됨
                        init();
                        List<ContentValues> calendars = calendarViewModel.getAllCalendars();
                        initSideCalendars(calendars);
                    } else
                    {
                        // 권한 거부됨
                        Toast.makeText(getApplicationContext(), getString(R.string.message_needs_calendar_permission), Toast.LENGTH_SHORT).show();
                        moveTaskToBack(true); // 태스크를 백그라운드로 이동
                        finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
                        android.os.Process.killProcess(android.os.Process.myPid()); // 앱 프로세스 종료
                    }
                }
            });


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

                    calendarTransactionFragment.refreshView();
                }
                return;
            }
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        connectedCalendarMap.put(key, calendar);
        connectedCalendarList.add(calendar);
        editor.putString(key, key);
        editor.commit();

        calendarTransactionFragment.refreshView();
    }

    @Override
    public void setMonth(Date dateTime)
    {
        currentCalendarDate = (Date) dateTime.clone();
        currMonthTextView.setText(ClockUtil.YEAR_MONTH_FORMAT.format(dateTime));
    }

    @Override
    public List<ContentValues> getConnectedCalendars()
    {
        return connectedCalendarList;
    }


    @Override
    public void startActivityResult(Intent intent, int requestCode)
    {
        activityResultLauncher.launch(intent);
    }

    @Override
    public void onClickedDate(Date date)
    {
        Toast.makeText(this, ClockUtil.YYYY_M_D_E.format(date), Toast.LENGTH_SHORT).show();
        calendarTransactionFragment.changeDate(date);
    }

    @Override
    public void onClickedMonth(Date date)
    {

    }
}

