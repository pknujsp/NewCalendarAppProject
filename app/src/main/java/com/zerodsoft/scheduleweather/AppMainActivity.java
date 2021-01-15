package com.zerodsoft.scheduleweather;

import android.Manifest;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.zerodsoft.scheduleweather.activity.editevent.activity.EventActivity;
import com.zerodsoft.scheduleweather.calendarfragment.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarfragment.DayFragment;
import com.zerodsoft.scheduleweather.calendarfragment.MonthFragment;
import com.zerodsoft.scheduleweather.calendarfragment.WeekFragment;
import com.zerodsoft.scheduleweather.databinding.ActivityAppMainBinding;
import com.zerodsoft.scheduleweather.calendar.GoogleCalendarApi;
import com.zerodsoft.scheduleweather.calendar.CalendarProvider;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendar.dto.AccountDto;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarDto;
import com.zerodsoft.scheduleweather.calendar.dto.EventDto;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppMainActivity extends AppCompatActivity implements ICalendarCheckBox
{
    private EventTransactionFragment calendarTransactionFragment;

    private static int DISPLAY_WIDTH = 0;
    private static int DISPLAY_HEIGHT = 0;

    private ActivityAppMainBinding mainBinding;
    private CalendarViewModel calendarViewModel;
    private CalendarsAdapter calendarsAdapter;

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

        calendarTransactionFragment = new EventTransactionFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.calendar_layout, calendarTransactionFragment, EventTransactionFragment.TAG).commit();
    }

    private void init()
    {
        KakaoLocalApiCategoryUtil.loadCategories(getApplicationContext());
    }

    private void initCalendarViewModel()
    {
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        calendarViewModel.init(this);

        //권한 확인
        int permissionState = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CALENDAR);
        if (permissionState == PackageManager.PERMISSION_GRANTED)
        {
            calendarViewModel.getCalendarList();
        } else
        {
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, CalendarProvider.REQUEST_READ_CALENDAR);
        }

        calendarViewModel.getCalendarListLiveData().observe(this, new Observer<DataWrapper<List<CalendarDto>>>()
        {
            @Override
            public void onChanged(DataWrapper<List<CalendarDto>> listDataWrapper)
            {
                if (listDataWrapper.getData() != null)
                {
                    List<AccountDto> accountList = new ArrayList<>();
                    List<CalendarDto> calendarList = listDataWrapper.getData();

                    for (CalendarDto calendar : calendarList)
                    {
                        String accountName = calendar.getACCOUNT_NAME();
                        String accountType = calendar.getACCOUNT_TYPE();
                        boolean isExistingAccount = false;

                        for (AccountDto account : accountList)
                        {
                            if (account.getAccountName().equals(accountName) &&
                                    account.getAccountType().equals(accountType))
                            {
                                isExistingAccount = true;
                                account.addCalendar(calendar);
                                break;
                            }
                        }

                        if (!isExistingAccount)
                        {
                            AccountDto accountDto = new AccountDto();
                            accountList.add(accountDto);

                            accountDto.setAccountName(accountName).setAccountType(accountType);
                            accountDto.addCalendar(calendar);
                        }
                    }
                    calendarsAdapter = new CalendarsAdapter(AppMainActivity.this, accountList);


                    SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferences_selected_caledars_key), Context.MODE_PRIVATE);
                    Set<String> selectedCalendarSet = sharedPreferences.getStringSet(getString(R.string.preferences_selected_caledars_key), new HashSet<>());
                    // 선택된 캘린더가 이미 있는지 확인

                    if (selectedCalendarSet.isEmpty())
                    {
                        for (AccountDto accountDto : accountList)
                        {
                            List<CalendarDto> calendarDtoList = accountDto.getCalendars();
                            for (CalendarDto calendarDto : calendarDtoList)
                            {
                                selectedCalendarSet.add(calendarDto.getACCOUNT_NAME()
                                        + calendarDto.get_ID());
                            }
                        }
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putStringSet(getString(R.string.preferences_selected_caledars_key), selectedCalendarSet);
                        editor.commit();
                    }

                    boolean[][] checkBoxStates = new boolean[accountList.size()][];

                    for (int i = 0; i < accountList.size(); i++)
                    {
                        checkBoxStates[i] = new boolean[accountList.get(i).getCalendars().size()];
                    }

                    for (String value : selectedCalendarSet)
                    {
                        int group = 0;

                        for (AccountDto accountDto : accountList)
                        {
                            List<CalendarDto> calendarDtoList = accountDto.getCalendars();
                            int child = 0;

                            for (CalendarDto calendarDto : calendarDtoList)
                            {
                                if (value.equals(calendarDto.getACCOUNT_NAME() + calendarDto.get_ID()))
                                {
                                    checkBoxStates[group][child] = true;
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
                    // calendarViewModel.getEvents();
                }
            }
        });

        calendarViewModel.getEventsLiveData().observe(this, new Observer<DataWrapper<List<EventDto>>>()
        {
            @Override
            public void onChanged(DataWrapper<List<EventDto>> listDataWrapper)
            {
                if (listDataWrapper.getData() != null)
                {

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
        mainBinding.sideNavCalendarList.setOnChildClickListener(new ExpandableListView.OnChildClickListener()
        {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l)
            {
                return false;
            }
        });

        mainBinding.sideNavCalendarList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
        {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l)
            {
                return false;
            }
        });

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
                        CalendarProvider provider = CalendarProvider.newInstance(getApplicationContext());
                        List<CalendarDto> calendarsList = provider.getAllCalendars();

                        String accountName = calendarsList.get(0).getACCOUNT_NAME();
                        String accountType = calendarsList.get(0).getACCOUNT_TYPE();
                        int calendarId = (int) calendarsList.get(0).get_ID();
                        String ownerAccount = calendarsList.get(0).getACCOUNT_NAME();
                        provider.getEvents(accountName, accountType, calendarId, ownerAccount);
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

        switch (requestCode)
        {
            case GoogleCalendarApi.REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null)
                {
                    String keyAccountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (keyAccountName != null)
                    {
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(GoogleCalendarApi.GOOGLE_ACCOUNT_NAME, keyAccountName);
                        editor.apply();
                        try
                        {
                            calendarViewModel.connect(keyAccountName);
                        } catch (IOException | GeneralSecurityException e)
                        {
                            e.printStackTrace();
                        }
                    }
                } else if (resultCode == RESULT_CANCELED)
                {

                }
                break;
            case GoogleCalendarApi.REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK)
                {
                    calendarViewModel.getCalendarList();
                } else
                {

                }
                break;

        }

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
            case GoogleCalendarApi.REQUEST_PERMISSION_GET_ACCOUNTS_AUTO:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // 권한 허용됨
                    String accountName = getPreferences(Context.MODE_PRIVATE).getString(GoogleCalendarApi.GOOGLE_ACCOUNT_NAME, "");

                    try
                    {
                        calendarViewModel.connect(accountName);
                    } catch (IOException | GeneralSecurityException e)
                    {
                        e.printStackTrace();
                    }
                } else
                {
                    // 권한 거부됨
                }
                break;

            case GoogleCalendarApi.REQUEST_PERMISSION_GET_ACCOUNTS_SELF:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // 권한 허용됨
                    calendarViewModel.requestAccountPicker();
                } else
                {
                    // 권한 거부됨
                }
                break;

            case CalendarProvider.REQUEST_READ_CALENDAR:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // 권한 허용됨
                    calendarViewModel.getCalendarList();
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
    public void onCheckedBox(String value, boolean state)
    {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferences_selected_caledars_key), Context.MODE_PRIVATE);
        Set<String> selectedCalendarSet = sharedPreferences.getStringSet(getString(R.string.preferences_selected_caledars_key), new HashSet<>());

        //set가 비워져있지는 않는지 검사
        if (!selectedCalendarSet.isEmpty())
        {
            for (String v : selectedCalendarSet)
            {
                if (v.equals(value))
                {
                    if (!state)
                    {
                        // 같은 값을 가진 것이 이미 추가되어있는 경우 선택해제 하는 것이므로 삭제한다.
                        selectedCalendarSet.remove(value);
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
        selectedCalendarSet.add(value);
        editor.remove(getString(R.string.preferences_selected_caledars_key));
        editor.commit();
        SharedPreferences.Editor editor2 = sharedPreferences.edit();
        editor2.putStringSet(getString(R.string.preferences_selected_caledars_key), selectedCalendarSet);
        editor2.commit();
    }

}

