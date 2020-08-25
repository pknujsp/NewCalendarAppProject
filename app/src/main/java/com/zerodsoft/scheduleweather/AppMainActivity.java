package com.zerodsoft.scheduleweather;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zerodsoft.scheduleweather.Activity.ScheduleInfoActivity;
import com.zerodsoft.scheduleweather.CalendarFragment.DayFragment;
import com.zerodsoft.scheduleweather.CalendarFragment.MonthFragment;
import com.zerodsoft.scheduleweather.CalendarFragment.WeekFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Date;

public class AppMainActivity extends AppCompatActivity
{
    private MonthFragment monthFragment;
    private WeekFragment weekFragment;
    private DayFragment dayFragment;


    public static final int WEEK_FRAGMENT = 0;
    public static final int DAY_FRAGMENT = 1;
    public static final int MONTH_FRAGMENT = 2;

    private static int calendarFragmentType = WEEK_FRAGMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_main);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.toolbar_menu_icon);


        onCalendarFragmentChanged(calendarFragmentType);

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AppMainActivity.this, ScheduleInfoActivity.class);
                intent.putExtra("requestCode", ScheduleInfoActivity.ADD_SCHEDULE_REQUEST);
                startActivityForResult(intent, ScheduleInfoActivity.ADD_SCHEDULE_REQUEST);
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
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
                    case ScheduleInfoActivity.ADD_LOCATION_ACTIVITY:
                    case ScheduleInfoActivity.SHOW_SCHEDULE_REQUEST:
                }
                break;
            case RESULT_CANCELED:
                switch (requestCode)
                {
                    case ScheduleInfoActivity.ADD_LOCATION_ACTIVITY:
                    case ScheduleInfoActivity.SHOW_SCHEDULE_REQUEST:
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.app_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                // drawerLayout.openDrawer(navigationView);
                return true;
            case R.id.menu_item_today:
                weekFragment.goToToday();
                return true;
            case R.id.menu_item_refresh:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onCalendarFragmentChanged(int type)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        switch (type)
        {
            case MONTH_FRAGMENT:
                if (monthFragment == null)
                {
                    monthFragment = new MonthFragment();
                }
                fragmentTransaction.replace(R.id.nav_host_fragment, monthFragment).commit();
                break;
            case WEEK_FRAGMENT:
                if (weekFragment == null)
                {
                    weekFragment = new WeekFragment();
                }
                fragmentTransaction.replace(R.id.nav_host_fragment, weekFragment).commit();
                break;
            case DAY_FRAGMENT:
                if (dayFragment == null)
                {
                    dayFragment = new DayFragment();
                }
                fragmentTransaction.replace(R.id.nav_host_fragment, dayFragment).commit();
                break;
        }
    }

    public void goToScheduleInfoAcitivity(int scheduleId)
    {
        Intent intent = new Intent(AppMainActivity.this, ScheduleInfoActivity.class);
        intent.putExtra("scheduleId", scheduleId);
        intent.putExtra("requestCode", ScheduleInfoActivity.SHOW_SCHEDULE_REQUEST);
        startActivityForResult(intent, ScheduleInfoActivity.SHOW_SCHEDULE_REQUEST);
    }
}