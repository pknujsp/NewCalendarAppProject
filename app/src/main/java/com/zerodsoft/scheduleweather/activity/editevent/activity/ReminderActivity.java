package com.zerodsoft.scheduleweather.activity.editevent.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.ActivityReminderBinding;
import com.zerodsoft.scheduleweather.utility.CalendarEventUtil;
import com.zerodsoft.scheduleweather.utility.model.ReminderDto;

public class ReminderActivity extends AppCompatActivity
{
    private ActivityReminderBinding binding;
    private boolean hasAlarm = false;
    private int givedMinutes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reminder);
        init();
    }

    private void init()
    {
        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

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

        binding.reminderWeekValue.addTextChangedListener(textWatcher);
        binding.reminderDayValue.addTextChangedListener(textWatcher);
        binding.reminderHourValue.addTextChangedListener(textWatcher);
        binding.reminderMinuteValue.addTextChangedListener(textWatcher);

        binding.upWeek.setOnClickListener(onUpClickListener);
        binding.upDay.setOnClickListener(onUpClickListener);
        binding.upHour.setOnClickListener(onUpClickListener);
        binding.upMinute.setOnClickListener(onUpClickListener);

        binding.upWeek.setOnLongClickListener(onUpLongClickListener);
        binding.upDay.setOnLongClickListener(onUpLongClickListener);
        binding.upHour.setOnLongClickListener(onUpLongClickListener);
        binding.upMinute.setOnLongClickListener(onUpLongClickListener);

        binding.downWeek.setOnClickListener(onDownClickListener);
        binding.downDay.setOnClickListener(onDownClickListener);
        binding.downHour.setOnClickListener(onDownClickListener);
        binding.downMinute.setOnClickListener(onDownClickListener);

        binding.downWeek.setOnLongClickListener(onDownLongClickListener);
        binding.downDay.setOnLongClickListener(onDownLongClickListener);
        binding.downHour.setOnLongClickListener(onDownLongClickListener);
        binding.downMinute.setOnLongClickListener(onDownLongClickListener);

        if (getIntent().getIntExtra("hasAlarm", 0) == 1)
        {
            hasAlarm = true;
            givedMinutes = getIntent().getIntExtra("minutes", 0);

            ReminderDto reminderDto = CalendarEventUtil.convertAlarmMinutes(givedMinutes);
            binding.reminderWeekValue.setText(String.valueOf(reminderDto.getWeek()));
            binding.reminderDayValue.setText(String.valueOf(reminderDto.getDay()));
            binding.reminderHourValue.setText(String.valueOf(reminderDto.getHour()));
            binding.reminderMinuteValue.setText(String.valueOf(reminderDto.getMinute()));

            binding.reminderSelector.setVisibility(View.VISIBLE);
            binding.eventReminderRadioGroup.check(R.id.ok_remind_radio);
        } else
        {
            binding.eventReminderRadioGroup.check(R.id.not_remind_radio);
        }
    }

    @Override
    public void onBackPressed()
    {
        if (binding.okRemindRadio.isChecked())
        {
            final int newMinutes = CalendarEventUtil.convertReminderValues(
                    new ReminderDto(Integer.parseInt(binding.reminderWeekValue.getText().toString())
                            , Integer.parseInt(binding.reminderDayValue.getText().toString())
                            , Integer.parseInt(binding.reminderHourValue.getText().toString())
                            , Integer.parseInt(binding.reminderMinuteValue.getText().toString())));

            // 정시에 알림도 있음
            if (hasAlarm)
            {
                if (givedMinutes == newMinutes)
                {
                    setResult(RESULT_CANCELED);
                } else
                {
                    getIntent().putExtra("hasAlarm", true);
                    getIntent().putExtra("newMinutes", newMinutes);
                    setResult(RESULT_OK, getIntent());
                }
            } else
            {
                getIntent().putExtra("hasAlarm", true);
                getIntent().putExtra("newMinutes", newMinutes);
                setResult(RESULT_OK, getIntent());
            }
        } else
        {
            if (hasAlarm)
            {
                getIntent().putExtra("hasAlarm", false);
                setResult(RESULT_OK, getIntent());
            } else
            {
                setResult(RESULT_CANCELED);
            }
        }

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final TextWatcher textWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {
            if (charSequence.length() == 0)
            {
                if (binding.reminderMinuteValue.isFocused())
                {
                    binding.reminderMinuteValue.setText("0");
                } else if (binding.reminderHourValue.isFocused())
                {
                    binding.reminderHourValue.setText("0");
                } else if (binding.reminderDayValue.isFocused())
                {
                    binding.reminderDayValue.setText("0");
                } else if (binding.reminderWeekValue.isFocused())
                {
                    binding.reminderWeekValue.setText("0");
                }
            } else
            {
                boolean invalidValue = false;
                String value = null;

                if (charSequence.toString().startsWith("0") && charSequence.length() >= 2)
                {
                    invalidValue = true;
                    value = Integer.toString(Integer.parseInt(charSequence.toString()));
                }

                if (binding.reminderMinuteValue.isFocused())
                {
                    if (Integer.parseInt(charSequence.toString()) > 59)
                    {
                        binding.reminderMinuteValue.setText("59");
                    } else if (invalidValue)
                    {
                        binding.reminderMinuteValue.setText(value);
                    }

                } else if (binding.reminderHourValue.isFocused())
                {
                    if (Integer.parseInt(charSequence.toString()) < 1)
                    {
                        binding.reminderHourValue.setText("0");
                    } else if (invalidValue)
                    {
                        binding.reminderHourValue.setText(value);
                    }

                } else if (binding.reminderDayValue.isFocused())
                {
                    if (Integer.parseInt(charSequence.toString()) < 1)
                    {
                        binding.reminderDayValue.setText("0");
                    } else if (invalidValue)
                    {
                        binding.reminderDayValue.setText(value);
                    }

                } else if (binding.reminderWeekValue.isFocused())
                {
                    if (Integer.parseInt(charSequence.toString()) < 1)
                    {
                        binding.reminderWeekValue.setText("0");
                    } else if (invalidValue)
                    {
                        binding.reminderWeekValue.setText(value);
                    }

                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable)
        {

        }
    };

    private final View.OnClickListener onUpClickListener = new View.OnClickListener()
    {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view)
        {
            int value = 0;
            switch (view.getId())
            {
                case R.id.up_week:
                    value = Integer.parseInt(binding.reminderWeekValue.getText().toString());
                    binding.reminderWeekValue.setText(String.valueOf(++value));
                    break;
                case R.id.up_day:
                case R.id.up_hour:
                case R.id.up_minute:
            }
        }
    };

    private final View.OnLongClickListener onUpLongClickListener = new View.OnLongClickListener()
    {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onLongClick(View view)
        {
            switch (view.getId())
            {
                case R.id.up_week:
                case R.id.up_day:
                case R.id.up_hour:
                case R.id.up_minute:
            }
            return true;
        }
    };

    private final View.OnClickListener onDownClickListener = new View.OnClickListener()
    {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view)
        {
            int value = 0;
            switch (view.getId())
            {
                case R.id.down_week:
                    value = Integer.parseInt(binding.reminderWeekValue.getText().toString());
                    binding.reminderWeekValue.setText(String.valueOf(--value));
                    break;
                case R.id.down_day:
                case R.id.down_hour:
                case R.id.down_minute:
            }
        }
    };

    private final View.OnLongClickListener onDownLongClickListener = new View.OnLongClickListener()
    {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onLongClick(View view)
        {
            switch (view.getId())
            {
                case R.id.down_week:
                case R.id.down_day:
                case R.id.down_hour:
                case R.id.down_minute:
            }
            return true;
        }
    };
}