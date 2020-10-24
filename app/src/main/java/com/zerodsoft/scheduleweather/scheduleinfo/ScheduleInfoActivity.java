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
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
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

        viewModel = new ViewModelProvider(this).get(ScheduleViewModel.class).selectSchedule(scheduleId);
        viewModel.getSchedule().observe(this, new Observer<ScheduleDTO>()
        {
            @Override
            public void onChanged(ScheduleDTO scheduleDTO)
            {
                ScheduleTabViewPager adapter = new ScheduleTabViewPager(ScheduleInfoActivity.this);
                viewPager.setAdapter(adapter.setFragments(scheduleDTO, viewModel.getAddress().getValue(), viewModel.getPlace().getValue()));

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