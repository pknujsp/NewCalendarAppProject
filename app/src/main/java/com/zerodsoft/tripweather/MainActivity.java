package com.zerodsoft.tripweather;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.zerodsoft.tripweather.ScheduleData.TravelSchedule;
import com.zerodsoft.tripweather.ScheduleList.ScheduleListAdapter;
import com.zerodsoft.tripweather.ScheduleList.ViewItemDecoration;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    Button btnCurrentWeather;
    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    public static final int NEW_TRAVEL_SCHEDULE = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        btnCurrentWeather = (Button) findViewById(R.id.btn_current_weather);

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        navigationView.setNavigationItemSelectedListener(this);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.toolbar_menu_icon);

        ArrayList<TravelSchedule> travelSchedules = new ArrayList<>();
        ArrayList<String> destinations = new ArrayList<>();

        destinations.add("서울");
        destinations.add("진해");
        destinations.add("울진");

        for (int index = 0; index < 10; ++index)
        {
            travelSchedules.add(new TravelSchedule().setTravelName(index + 1 + "번째 여행")
                    .setTravelStartDate("2020/04/01").setTravelEndDate("2020/04/12")
                    .setTravelDestinations(destinations));
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_schedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ScheduleListAdapter adapter = new ScheduleListAdapter(travelSchedules);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new ViewItemDecoration(16));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                drawerLayout.openDrawer(navigationView);
                return true;
            case R.id.menu_item_add:
                Intent intent = new Intent(getApplicationContext(), AddScheduleActivity.class);
                startActivityForResult(intent, NEW_TRAVEL_SCHEDULE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {

            case R.id.btn_current_weather:
                Intent intent = new Intent(getApplicationContext(), CurrentWeatherActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
