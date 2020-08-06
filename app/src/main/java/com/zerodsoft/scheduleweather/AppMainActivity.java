package com.zerodsoft.scheduleweather;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.zerodsoft.scheduleweather.Activity.AddScheduleActivity;
import com.zerodsoft.scheduleweather.CalendarFragment.WeekFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AppMainActivity extends AppCompatActivity
{
    private WeekFragment weekFragment;

    private static final int ADD_SCHEDULE_REQUEST = 0;
    public static final int WEEK_FRAGMENT = 1;
    public static final int DAY_FRAGMENT = 2;
    public static final int MONTH_FRAGMENT = 3;

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

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AppMainActivity.this, AddScheduleActivity.class);
                startActivityForResult(intent, ADD_SCHEDULE_REQUEST);
            }
        });
    }

    @Override
    protected void onStart()
    {
        onCalendarFragmentChanged(calendarFragmentType);
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_SCHEDULE_REQUEST)
        {
            if (requestCode == RESULT_OK)
            {
                Bundle bundle = data.getExtras();

                long startDate = bundle.getLong("startDate");
                int scheduleId = bundle.getInt("scheduleId");

                Toast.makeText(AppMainActivity.this, Long.toString(startDate) + ", " + Integer.toString(scheduleId), Toast.LENGTH_SHORT).show();
            }
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
                break;
            case WEEK_FRAGMENT:
                if (weekFragment == null)
                {
                    weekFragment = new WeekFragment();
                }
                fragmentTransaction.replace(R.id.nav_host_fragment, weekFragment).commit();
                break;
            case DAY_FRAGMENT:
                break;
        }
    }
}