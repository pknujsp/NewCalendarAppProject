package com.zerodsoft.scheduleweather;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zerodsoft.scheduleweather.activity.ScheduleInfoActivity;
import com.zerodsoft.scheduleweather.calendarfragment.CalendarTransactionFragment;
import com.zerodsoft.scheduleweather.calendarfragment.DayFragment;
import com.zerodsoft.scheduleweather.calendarfragment.MonthFragment;
import com.zerodsoft.scheduleweather.calendarfragment.WeekFragment;
import com.zerodsoft.scheduleweather.databinding.ActivityAppMainBinding;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.time.Month;

public class AppMainActivity extends AppCompatActivity
{
    private ActivityAppMainBinding binding;
    private CalendarTransactionFragment calendarTransactionFragment;

    private static int DISPLAY_WIDTH = 0;
    private static int DISPLAY_HEIGHT = 0;
    private static int CALENDAR_VIEW_HEIGHT = 0;

    public static final int WEEK_FRAGMENT = 0;
    public static final int DAY_FRAGMENT = 1;
    public static final int MONTH_FRAGMENT = 2;

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
        binding = ActivityAppMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);

        DISPLAY_WIDTH = point.x;
        DISPLAY_HEIGHT = point.y;

        Toolbar toolbar = binding.mainToolbar;
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.toolbar_menu_icon);

        calendarTransactionFragment = new CalendarTransactionFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.calendar_layout, calendarTransactionFragment, CalendarTransactionFragment.TAG).commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_schedule_button);

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AppMainActivity.this, ScheduleInfoActivity.class);
                intent.putExtra("requestCode", ScheduleInfoActivity.REQUEST_NEW_SCHEDULE);
                startActivityForResult(intent, ScheduleInfoActivity.REQUEST_NEW_SCHEDULE);
            }
        });
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
                    case ScheduleInfoActivity.ADD_LOCATION:
                    case ScheduleInfoActivity.REQUEST_SHOW_SCHEDULE:
                }
                break;
            case RESULT_CANCELED:
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
        // toolbar menu
        switch (item.getItemId())
        {
            case android.R.id.home:
                break;
            case R.id.menu_item_today:
                // 오늘 날짜로 이동
                break;
            case R.id.menu_item_refresh:
                // 달력 갱신
                break;
            case R.id.monthFragment:
                calendarTransactionFragment.replaceFragment(MonthFragment.TAG);
                break;
            case R.id.weekFragment:
                calendarTransactionFragment.replaceFragment(WeekFragment.TAG);
                break;
            case R.id.dayFragment:
                calendarTransactionFragment.replaceFragment(DayFragment.TAG);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static int getCalendarViewHeight()
    {
        return CALENDAR_VIEW_HEIGHT;
    }

}