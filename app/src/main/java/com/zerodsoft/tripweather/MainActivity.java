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
import com.zerodsoft.tripweather.Room.DTO.Schedule;
import com.zerodsoft.tripweather.Room.DTO.Travel;
import com.zerodsoft.tripweather.Room.TravelScheduleThread;
import com.zerodsoft.tripweather.ScheduleList.TravelScheduleListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    Button btnCurrentWeather;
    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    private static final int NEW_TRAVEL_SCHEDULE = 20;

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

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_schedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        TravelScheduleThread travelScheduleThread = new TravelScheduleThread(MainActivity.this, 1);
        travelScheduleThread.start();
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
                startActivity(intent);
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

        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case NEW_TRAVEL_SCHEDULE:
                    ArrayList<Travel> travelList = (ArrayList<Travel>) data.getExtras().getSerializable("travelList");
                    TravelScheduleListAdapter adapter = new TravelScheduleListAdapter(MainActivity.this, travelList);
                    recyclerView.setAdapter(adapter);
                    // DB에 데이터 저장후 adapter갱신
            }
        }
    }
}
