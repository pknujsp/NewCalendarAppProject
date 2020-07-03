package com.zerodsoft.scheduleweather;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.zerodsoft.scheduleweather.CalendarFragment.WeekFragment;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AppMainActivity extends AppCompatActivity
{
    private WeekFragment weekFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

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

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        weekFragment = new WeekFragment();
        fragmentTransaction.replace(R.id.nav_host_fragment, weekFragment).commitAllowingStateLoss();

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
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
                /*
                Intent intent = new Intent(getApplicationContext(), AddScheduleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("action", Actions.START_ADD_SCHEDULE_ACTIVITY);
                intent.putExtras(bundle);
                startActivity(intent);

                 */
                weekFragment.goToToday();

                return true;
            case R.id.menu_item_refresh:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}