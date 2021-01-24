package com.zerodsoft.scheduleweather.activity.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.android.material.internal.NavigationSubMenu;
import com.google.android.material.navigation.NavigationView;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.activity.EventActivity;
import com.zerodsoft.scheduleweather.calendarview.CalendarsAdapter;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.day.DayFragment;
import com.zerodsoft.scheduleweather.calendarview.interfaces.ICalendarCheckBox;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.SubMenuBuilder;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppMainActivity extends AppCompatActivity implements ICalendarCheckBox, IToolbar, IConnectedCalendars
{
    private EventTransactionFragment calendarTransactionFragment;

    private static int DISPLAY_WIDTH = 0;
    private static int DISPLAY_HEIGHT = 0;
    private static Map<String, ContentValues> connectedCalendarMap = new HashMap<>();
    private static List<ContentValues> connectedCalendarList = new ArrayList<>();

    private ActivityAppMainBinding mainBinding;
    private CalendarViewModel calendarViewModel;
    private CalendarsAdapter calendarsAdapter;
    private TextView currMonthTextView;

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

        init();
        setNavigationView();
        initCalendarViewModel();

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);

        DISPLAY_WIDTH = point.x;
        DISPLAY_HEIGHT = point.y;

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

    private void init()
    {
        KakaoLocalApiCategoryUtil.loadCategories(getApplicationContext());
    }

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
                    Map<String, AccountDto> accountMap = new HashMap<>();
                    List<ContentValues> calendarList = listDataWrapper.getData();

                    for (ContentValues calendar : calendarList)
                    {
                        String accountName = calendar.getAsString(CalendarContract.Calendars.ACCOUNT_NAME);
                        String ownerAccount = calendar.getAsString(CalendarContract.Calendars.OWNER_ACCOUNT);
                        String accountType = calendar.getAsString(CalendarContract.Calendars.ACCOUNT_TYPE);

                        if (accountMap.containsKey(ownerAccount))
                        {
                            accountMap.get(ownerAccount).addCalendar(calendar);
                        } else
                        {
                            AccountDto accountDto = new AccountDto();

                            accountDto.setAccountName(accountName).setAccountType(accountType)
                                    .setOwnerAccount(ownerAccount).addCalendar(calendar);
                            accountMap.put(ownerAccount, accountDto);
                        }

                    }
                    List<AccountDto> accountList = new ArrayList<>(accountMap.values());

                    // 네비게이션 내 캘린더 리스트 구성

                    calendarsAdapter = new CalendarsAdapter(AppMainActivity.this, accountList);

                    SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferences_selected_caledars_key), Context.MODE_PRIVATE);
                    Set<String> selectedCalendarSet = sharedPreferences.getStringSet(getString(R.string.preferences_selected_caledars_key), new HashSet<>());
                    // 선택된 캘린더가 이미 있는지 확인

                    if (selectedCalendarSet.isEmpty())
                    {
                        String key = null;

                        for (AccountDto accountDto : accountList)
                        {
                            List<ContentValues> newCalendarList = accountDto.getCalendars();

                            for (ContentValues calendar : newCalendarList)
                            {
                                key = calendar.getAsString(CalendarContract.Calendars.OWNER_ACCOUNT) + "&"
                                        + calendar.getAsString(CalendarContract.Calendars._ID);

                                selectedCalendarSet.add(key);
                                connectedCalendarMap.put(key, calendar);
                            }
                        }
                        connectedCalendarList.addAll(connectedCalendarMap.values());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putStringSet(getString(R.string.preferences_selected_caledars_key), selectedCalendarSet);
                        editor.commit();
                    }

                    boolean[][] checkBoxStates = new boolean[accountList.size()][];

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
                                if (key.equals(calendar.getAsString(CalendarContract.Calendars.OWNER_ACCOUNT) + "&"
                                        + calendar.getAsString(CalendarContract.Calendars._ID)))
                                {
                                    checkBoxStates[group][child] = true;
                                    connectedCalendarList.add(calendar);
                                    break;
                                }
                                child++;
                            }
                            group++;
                        }
                    }

                    calendarsAdapter.setCheckBoxStates(checkBoxStates);
                    mainBinding.sideNavCalendarList.setAdapter(calendarsAdapter);
                    expandAllGroup();
                }
            }
        });

        calendarViewModel.getCalendarLiveData().observe(this, new Observer<DataWrapper<ContentValues>>()
        {
            @Override
            public void onChanged(DataWrapper<ContentValues> contentValuesDataWrapper)
            {
                if (contentValuesDataWrapper.getData() != null)
                {
                    ContentValues calendar = contentValuesDataWrapper.getData();
                    connectedCalendarMap.put(calendar.getAsString(CalendarContract.Calendars.OWNER_ACCOUNT) + "&"
                            + calendar.getAsString(CalendarContract.Calendars._ID), calendar);
                    connectedCalendarList.add(calendar);
                }
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
        mainBinding.sideNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.month_type:
                        calendarTransactionFragment.replaceFragment(MonthFragment.TAG);
                        break;
                    case R.id.week_type:
                        calendarTransactionFragment.replaceFragment(WeekFragment.TAG);
                        break;
                    case R.id.day_type:
                        calendarTransactionFragment.replaceFragment(DayFragment.TAG);
                        break;
                    case R.id.favorite:
                        break;
                    case R.id.app_setting:

                        break;
                }
                mainBinding.drawerLayout.closeDrawer(mainBinding.sideNavigation);
                return true;
            }
        });


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
                Intent intent = new Intent(AppMainActivity.this, EventActivity.class);
                intent.putExtra("requestCode", EventActivity.NEW_EVENT);
                startActivityForResult(intent, EventActivity.NEW_EVENT);
                break;
            case R.id.go_to_today:
                calendarTransactionFragment.goToToday();
                break;
            case R.id.refresh_calendar:
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode)
        {
            case RESULT_OK:
                switch (requestCode)
                {
                    case EventActivity.NEW_EVENT:
                        //새로운 일정이 추가됨 -> 달력 이벤트 갱신
                        calendarTransactionFragment.refreshCalendar((Date) data.getSerializableExtra("startDate"));
                        break;
                }
                break;
            case RESULT_CANCELED:
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
    public void onCheckedBox(String key, long calendarId, boolean state)
    {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferences_selected_caledars_key), Context.MODE_PRIVATE);
        Set<String> selectedCalendarSet = sharedPreferences.getStringSet(getString(R.string.preferences_selected_caledars_key), new HashSet<>());

        //set가 비워져있지는 않는지 검사
        if (!selectedCalendarSet.isEmpty())
        {
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
                        selectedCalendarSet.remove(key);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(getString(R.string.preferences_selected_caledars_key));
                        editor.commit();

                        SharedPreferences.Editor editor2 = sharedPreferences.edit();
                        editor2.putStringSet(getString(R.string.preferences_selected_caledars_key), selectedCalendarSet);
                        editor2.commit();
                    }
                    return;
                }
            }
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        selectedCalendarSet.add(key);
        calendarViewModel.getCalendar((int) calendarId);

        editor.remove(getString(R.string.preferences_selected_caledars_key));
        editor.commit();

        SharedPreferences.Editor editor2 = sharedPreferences.edit();
        editor2.putStringSet(getString(R.string.preferences_selected_caledars_key), selectedCalendarSet);
        editor2.commit();
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
}

