package com.zerodsoft.tripweather;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

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
import com.zerodsoft.tripweather.Utility.Actions;

import java.security.MessageDigest;
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
    private CloseActivity closeActivity;
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Bundle bundle = msg.getData();

            switch (msg.what)
            {
                case Actions.FINISHED_DELETE_TRAVEL:
                    Toast.makeText(getApplicationContext(), "삭제 완료", Toast.LENGTH_SHORT).show();
                    TravelScheduleThread travelScheduleThread = new TravelScheduleThread(MainActivity.this);
                    travelScheduleThread.setAction(Actions.SET_MAINACTIVITY_VIEW);
                    travelScheduleThread.setMainActivityHandler(handler);
                    travelScheduleThread.start();
                    break;

                case Actions.SET_TRAVEL_RECYCLERVIEW_ADAPTER:
                    ArrayList<Travel> travelList = (ArrayList<Travel>) bundle.getSerializable("travelList");
                    TravelScheduleListAdapter adapter = new TravelScheduleListAdapter(MainActivity.this, travelList);
                    adapter.setMainActivityHandler(handler);
                    recyclerView.setAdapter(adapter);
                    break;

                case Actions.START_SCHEDULE_ACTIVITY:
                    Intent scheduleActivityIntent = new Intent(getApplicationContext(), TravelScheduleActivity.class);
                    bundle.putBoolean("isNewTravel", false);
                    scheduleActivityIntent.putExtras(bundle);
                    startActivity(scheduleActivityIntent);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        btnCurrentWeather = (Button) findViewById(R.id.btn_current_weather);
        closeActivity = new CloseActivity(MainActivity.this);

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        navigationView.setNavigationItemSelectedListener(this);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.toolbar_menu_icon);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_schedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

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
                Log.e("Hash key", something);
            }
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
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
            closeActivity.closeActivity();
        }
    }

    @Override
    protected void onResume()
    {
        TravelScheduleThread travelScheduleThread = new TravelScheduleThread(MainActivity.this);
        travelScheduleThread.setAction(Actions.SET_MAINACTIVITY_VIEW);
        travelScheduleThread.setMainActivityHandler(handler);
        travelScheduleThread.start();
        super.onResume();
    }
}
