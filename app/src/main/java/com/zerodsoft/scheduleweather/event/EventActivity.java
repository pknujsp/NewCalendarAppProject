package com.zerodsoft.scheduleweather.event;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.event.event.EventFragment;
import com.zerodsoft.scheduleweather.event.location.PlacesAroundLocationFragment;
import com.zerodsoft.scheduleweather.event.weather.WeatherFragment;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;

public class EventActivity extends AppCompatActivity
{
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private CalendarViewModel viewModel;
    private BottomNavigationView bottomNavigationView;
    private FragmentContainerView fragmentContainerView;

    private EventFragment eventFragment;
    private WeatherFragment weatherFragment;
    private PlacesAroundLocationFragment placesAroundLocationFragment;

    private OnBackPressedCallback onBackPressedCallback;

    private FragmentManager fragmentManager;

    private static final String TAG_INFO = "info";
    private static final String TAG_WEATHER = "weather";
    private static final String TAG_LOCATION = "location";
    private Fragment currentFragment = null;

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        onBackPressedCallback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_info);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.schedule_bottom_nav);
        fragmentContainerView = (FragmentContainerView) findViewById(R.id.schedule_fragment_container);

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        fragmentManager = getSupportFragmentManager();

        int eventId = getIntent().getIntExtra("eventId", 0);
        int calendarId = getIntent().getIntExtra("calendarId", 0);
        String accountName = getIntent().getStringExtra("accountName");

        viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        viewModel.getEvent(calendarId, eventId, accountName);
        viewModel.getEventLiveData().observe(this, new Observer<DataWrapper<ContentValues>>()
        {
            @Override
            public void onChanged(DataWrapper<ContentValues> contentValuesDataWrapper)
            {
                if (contentValuesDataWrapper.getData() != null)
                {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("event", contentValuesDataWrapper.getData());

                    eventFragment = new EventFragment();
                    weatherFragment = new WeatherFragment();
                    placesAroundLocationFragment = new PlacesAroundLocationFragment(new AddressDTO());

                    eventFragment.setArguments(bundle);
                    weatherFragment.setArguments(bundle);
                    placesAroundLocationFragment.setArguments(bundle);

                    fragmentManager.beginTransaction().add(R.id.schedule_fragment_container, eventFragment, TAG_INFO).hide(eventFragment)
                            .add(R.id.schedule_fragment_container, weatherFragment, TAG_WEATHER).hide(placesAroundLocationFragment)
                            .add(R.id.schedule_fragment_container, placesAroundLocationFragment, TAG_LOCATION).hide(weatherFragment)
                            .commit();
                }
            }
        });


    }

    private final BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            Fragment newFragment = null;

            switch (item.getItemId())
            {
                case R.id.schedule_info:
                    newFragment = eventFragment;
                    break;
                case R.id.schedule_weather:
                    newFragment = weatherFragment;
                    break;
                case R.id.schedule_location:
                    newFragment = placesAroundLocationFragment;
                    break;
            }

            //현재 표시된 프래그먼트와 변경할 프래그먼트가 같은 경우 변경하지 않음
            if (currentFragment != newFragment)
            {
                fragmentManager.beginTransaction().hide(currentFragment).show(newFragment).commit();
                currentFragment = newFragment;
            }
            return true;
        }
    };

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        onBackPressedCallback.remove();
    }

}