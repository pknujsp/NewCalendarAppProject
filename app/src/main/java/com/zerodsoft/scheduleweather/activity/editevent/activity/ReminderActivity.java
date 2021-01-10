package com.zerodsoft.scheduleweather.activity.editevent.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.ActivityReminderBinding;

public class ReminderActivity extends AppCompatActivity
{
    private ActivityReminderBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reminder);
        init();
    }

    private void init()
    {
        binding.eventReminderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id)
            {
                switch (id)
                {
                    case R.id.not_remind_radio:
                        binding.reminderSelector.setVisibility(View.GONE);
                        break;
                    case R.id.ok_remind_radio:
                        binding.reminderSelector.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}