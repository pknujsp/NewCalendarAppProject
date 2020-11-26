package com.zerodsoft.scheduleweather.scheduleinfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.viewmodel.ScheduleData;
import com.zerodsoft.scheduleweather.viewmodel.ScheduleViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ScheduleInfoActivity extends AppCompatActivity
{
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private ScheduleViewModel viewModel;
    private BottomNavigationView bottomNavigationView;
    private FragmentContainerView fragmentContainerView;

    private ScheduleInfoFragment scheduleInfoFragment;
    private ScheduleWeatherFragment scheduleWeatherFragment;
    private InfoAroundLocationFragment infoAroundLocationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_info);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.schedule_bottom_nav);
        fragmentContainerView = (FragmentContainerView) findViewById(R.id.schedule_fragment_container);

        // List<Integer> navGraphIds = Arrays.asList(R.navigation.navigation_schedule_info, R.navigation.navigation_schedule_weather, R.navigation.navigation_schedule_location);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        int scheduleId = getIntent().getIntExtra("scheduleId", -1);

        viewModel = new ViewModelProvider(this).get(ScheduleViewModel.class).selectScheduleData(scheduleId);
        viewModel.getScheduleDataLiveData().observe(this, new Observer<ScheduleData>()
        {
            @Override
            public void onChanged(ScheduleData scheduleData)
            {
                scheduleInfoFragment = new ScheduleInfoFragment(scheduleData.getSchedule(), scheduleData.getAddresses(), scheduleData.getPlaces());
                scheduleWeatherFragment = new ScheduleWeatherFragment(scheduleData.getAddresses(), scheduleData.getPlaces());
                infoAroundLocationFragment = new InfoAroundLocationFragment(scheduleData.getAddresses(), scheduleData.getPlaces());
                getSupportFragmentManager().beginTransaction().add(R.id.schedule_fragment_container, scheduleInfoFragment).commitAllowingStateLoss();

            }
        });

        /*
        viewPager = (ViewPager2) findViewById(R.id.schedule_viewpager);
        tabLayout = (TabLayout) findViewById(R.id.schedule_tab);
        viewPager.setUserInputEnabled(false);

        int scheduleId = getIntent().getIntExtra("scheduleId", -1);

        viewModel = new ViewModelProvider(this).get(ScheduleViewModel.class).selectScheduleData(scheduleId);
        viewModel.getScheduleDataLiveData().observe(this, new Observer<ScheduleData>()
        {
            @Override
            public void onChanged(ScheduleData scheduleData)
            {
                ScheduleTabViewPager adapter = new ScheduleTabViewPager(ScheduleInfoActivity.this);
                viewPager.setAdapter(adapter.setFragments(scheduleData.getSchedule(), scheduleData.getAddresses(), scheduleData.getPlaces()));

                String[] tabs = {"일정", "날씨", "주변 정보"};

                new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy()
                {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position)
                    {
                        tab.setText(tabs[position]);
                    }
                }).attach();
            }
        });


         */
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            switch (item.getItemId())
            {
                case R.id.schedule_info:
                    fragmentTransaction.replace(R.id.schedule_fragment_container, scheduleInfoFragment);
                    break;
                case R.id.schedule_weather:
                    fragmentTransaction.replace(R.id.schedule_fragment_container, scheduleWeatherFragment);
                    break;
                case R.id.schedule_location:
                    fragmentTransaction.replace(R.id.schedule_fragment_container, infoAroundLocationFragment);
                    break;
            }
            fragmentTransaction.commitAllowingStateLoss();
            return true;
        }
    };
}