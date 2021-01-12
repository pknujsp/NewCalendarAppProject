package com.zerodsoft.scheduleweather.scheduleinfo;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.viewmodel.ScheduleData;
import com.zerodsoft.scheduleweather.viewmodel.ScheduleViewModel;

public class ScheduleInfoActivity extends AppCompatActivity
{
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private ScheduleViewModel viewModel;
    private BottomNavigationView bottomNavigationView;
    private FragmentContainerView fragmentContainerView;

    private ScheduleWeatherFragment scheduleWeatherFragment;
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

        int scheduleId = getIntent().getIntExtra("scheduleId", -1);

        viewModel = new ViewModelProvider(this).get(ScheduleViewModel.class).selectScheduleData(scheduleId);
        viewModel.getScheduleDataLiveData().observe(this, new Observer<ScheduleData>()
        {
            @Override
            public void onChanged(ScheduleData scheduleData)
            {
     /*
                scheduleWeatherFragment = new ScheduleWeatherFragment(scheduleData.getAddresses(), scheduleData.getPlaces());
                placesAroundLocationFragment = new PlacesAroundLocationFragment(scheduleData.getAddresses(), scheduleData.getPlaces());
                fragmentManager.beginTransaction().add(R.id.schedule_fragment_container, scheduleInfoFragment, TAG_INFO).hide(scheduleInfoFragment)
                        .add(R.id.schedule_fragment_container, scheduleWeatherFragment, TAG_WEATHER).hide(placesAroundLocationFragment)
                        .add(R.id.schedule_fragment_container, placesAroundLocationFragment, TAG_LOCATION).hide(scheduleWeatherFragment)

                        .commit();


      */
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
                    newFragment = scheduleInfoFragment;
                    break;
                case R.id.schedule_weather:
                    newFragment = scheduleWeatherFragment;
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