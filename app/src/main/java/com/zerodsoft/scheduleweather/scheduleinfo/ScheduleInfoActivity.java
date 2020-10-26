package com.zerodsoft.scheduleweather.scheduleinfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.viewmodel.ScheduleData;
import com.zerodsoft.scheduleweather.viewmodel.ScheduleViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScheduleInfoActivity extends AppCompatActivity
{
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private ScheduleViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_info);

        viewPager = (ViewPager2) findViewById(R.id.schedule_viewpager);
        tabLayout = (TabLayout) findViewById(R.id.schedule_tab);


        int scheduleId = getIntent().getIntExtra("scheduleId", -1);

        viewModel = new ViewModelProvider(this).get(ScheduleViewModel.class).selectScheduleData(scheduleId);
        viewModel.getScheduleDataLiveData().observe(this, new Observer<ScheduleData>()
        {
            @Override
            public void onChanged(ScheduleData scheduleData)
            {
                ScheduleTabViewPager adapter = new ScheduleTabViewPager(ScheduleInfoActivity.this);
                viewPager.setAdapter(adapter.setFragments(scheduleData.getSchedule(), scheduleData.getAddress(), scheduleData.getPlace()));

                String[] tabs = {"일정", "날씨", "지도"};

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

    }

}