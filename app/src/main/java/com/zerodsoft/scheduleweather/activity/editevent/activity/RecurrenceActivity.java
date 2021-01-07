package com.zerodsoft.scheduleweather.activity.editevent.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.ActivityRecurrenceBinding;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.RecurrenceRule;

import java.util.Calendar;

public class RecurrenceActivity extends AppCompatActivity
{
    private ActivityRecurrenceBinding binding;
    private ArrayAdapter<CharSequence> spinnerAdapter;

    private Calendar eventStartDateTime;
    private final RecurrenceRule NEW_RULE = new RecurrenceRule();
    private final RecurrenceRule GIVED_RULE = new RecurrenceRule();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recurrence);

        String givedRecurrenceRuleStr = getIntent().getStringExtra("recurrenceRule");
        eventStartDateTime = (Calendar) getIntent().getSerializableExtra("eventStartDateTime");
        GIVED_RULE.separateValues(givedRecurrenceRuleStr);
        init();
    }

    private void init()
    {
        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        spinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.recurrence_date_types, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.recurrenceCustomRule.dateTypeSpinner.setAdapter(spinnerAdapter);

        binding.notRecurrenceRadio.setOnCheckedChangeListener(recurrenceCheckedListener);
        binding.recurrenceDailyRadio.setOnCheckedChangeListener(recurrenceCheckedListener);
        binding.recurrenceWeeklyRadio.setOnCheckedChangeListener(recurrenceCheckedListener);
        binding.recurrenceMonthlyRadio.setOnCheckedChangeListener(recurrenceCheckedListener);
        binding.recurrenceYearlyRadio.setOnCheckedChangeListener(recurrenceCheckedListener);
        binding.recurrenceCustomRadio.setOnCheckedChangeListener(recurrenceCheckedListener);

        binding.recurrenceCustomRule.firstDayRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id)
            {
                switch (id)
                {
                    case R.id.same_date_radio:

                    case R.id.same_week_radio:
                }
            }
        });

        binding.recurrenceCustomRule.dateTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l)
            {
                boolean showFirstDayRadio = false;
                boolean showDaysChips = false;

                switch (index)
                {
                    case 0:
                        NEW_RULE.putValue(RecurrenceRule.FREQ, RecurrenceRule.FREQ_DAILY);
                        break;
                    case 1:
                        NEW_RULE.putValue(RecurrenceRule.FREQ, RecurrenceRule.FREQ_WEEKLY);
                        break;
                    case 2:
                        NEW_RULE.putValue(RecurrenceRule.FREQ, RecurrenceRule.FREQ_MONTHLY);
                        showFirstDayRadio = true;
                        showDaysChips = true;
                        break;
                    case 3:
                        NEW_RULE.putValue(RecurrenceRule.FREQ, RecurrenceRule.FREQ_YEARLY);
                        showFirstDayRadio = true;
                        break;
                }

                if (showFirstDayRadio)
                {
                    if (binding.recurrenceCustomRule.firstDayRadioGroup.getVisibility() == View.GONE)
                    {
                        binding.recurrenceCustomRule.firstDayRadioGroup.setVisibility(View.VISIBLE);
                    }
                } else
                {
                    if (binding.recurrenceCustomRule.firstDayRadioGroup.getVisibility() == View.VISIBLE)
                    {
                        binding.recurrenceCustomRule.firstDayRadioGroup.setVisibility(View.GONE);
                    }
                }

                if (showDaysChips)
                {
                    if (binding.recurrenceCustomRule.recurrenceDayView.getRoot().getVisibility() == View.GONE)
                    {
                        binding.recurrenceCustomRule.recurrenceDayView.getRoot().setVisibility(View.VISIBLE);
                    }
                } else
                {
                    if (binding.recurrenceCustomRule.recurrenceDayView.getRoot().getVisibility() == View.VISIBLE)
                    {
                        binding.recurrenceCustomRule.recurrenceDayView.getRoot().setVisibility(View.GONE);
                    }
                }
                setFirstDayRadioGroupText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });

        binding.recurrenceCustomRule.recurrenceInterval.addTextChangedListener(textWatcher);
        binding.recurrenceDetailRule.certainRecurrenceCount.addTextChangedListener(textWatcher);

        if (!GIVED_RULE.isEmpty())
        {
            if (GIVED_RULE.containsKey(RecurrenceRule.FREQ))
            {
                String freq = GIVED_RULE.getValue(RecurrenceRule.FREQ);

                switch (freq)
                {
                    case RecurrenceRule.FREQ_DAILY:
                        binding.recurrenceDailyRadio.setChecked(true);
                        break;
                    case RecurrenceRule.FREQ_WEEKLY:
                        binding.recurrenceWeeklyRadio.setChecked(true);
                        break;
                    case RecurrenceRule.FREQ_MONTHLY:
                        binding.recurrenceMonthlyRadio.setChecked(true);
                        break;
                    case RecurrenceRule.FREQ_YEARLY:
                        binding.recurrenceYearlyRadio.setChecked(true);
                        break;
                }
                binding.recurrenceCustomRule.dateTypeSpinner.setSelection(0);
            } else
            {
                // custom인 경우
                binding.recurrenceCustomRadio.setChecked(true);
            }
        } else
        {
            binding.notRecurrenceRadio.performClick();
            binding.notRecurrenceRadio.setChecked(true);
            binding.recurrenceCustomRule.dateTypeSpinner.setSelection(0);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }


    private void setFirstDayRadioGroupText()
    {
        if (NEW_RULE.getValue(RecurrenceRule.FREQ).equals(RecurrenceRule.FREQ_MONTHLY)
                || NEW_RULE.getValue(RecurrenceRule.FREQ).equals(RecurrenceRule.FREQ_YEARLY))
        {
            String sameDateText = null;
            String sameWeekText = null;
            final String dayOfWeek = ClockUtil.DAY_OF_WEEK_FORMAT.format(eventStartDateTime.getTime());

            if (NEW_RULE.getValue(RecurrenceRule.FREQ).equals(RecurrenceRule.FREQ_MONTHLY))
            {
                // 매월 5일 마다, 매월 첫째주 월요일 마다
                sameDateText = "매월 " + eventStartDateTime.get(Calendar.DAY_OF_MONTH) + "일 마다";
                sameWeekText = "매월 " + eventStartDateTime.get(Calendar.WEEK_OF_MONTH) + "번째 주 " + dayOfWeek + "요일 마다";
            } else
            {
                // 매년 1월 5일 마다, 매년 첫째주 월요일 마다
                sameDateText = "매년 " + ClockUtil.Md_FORMAT.format(eventStartDateTime.getTime()) + " 마다";
                sameWeekText = "매년 " + eventStartDateTime.get(Calendar.WEEK_OF_YEAR) + "번째 주 " + dayOfWeek + "요일 마다";
            }

            NEW_RULE.putValue(RecurrenceRule.);

            binding.recurrenceCustomRule.sameDateRadio.setText(sameDateText);
            binding.recurrenceCustomRule.sameDateRadio.setText(sameWeekText);
        }
    }

    private void onCompletedSelection()
    {
        getIntent().putExtra("recurrenceRule", NEW_RULE.getRule());
        setResult(RESULT_OK, getIntent());
        finish();
    }

    @Override
    public void onBackPressed()
    {
        onCompletedSelection();
    }

    private final CompoundButton.OnCheckedChangeListener recurrenceCheckedListener = new CompoundButton.OnCheckedChangeListener()
    {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
        {
            if (isChecked)
            {
                boolean isCustom = false;
                NEW_RULE.clear();

                switch (compoundButton.getId())
                {
                    case R.id.not_recurrence_radio:
                        break;
                    case R.id.recurrence_daily_radio:
                        NEW_RULE.putValue(RecurrenceRule.FREQ, RecurrenceRule.FREQ_DAILY);
                        NEW_RULE.putValue(RecurrenceRule.INTERVAL, 1);
                        break;
                    case R.id.recurrence_weekly_radio:
                        NEW_RULE.putValue(RecurrenceRule.FREQ, RecurrenceRule.FREQ_WEEKLY);
                        NEW_RULE.putValue(RecurrenceRule.INTERVAL, 1);
                        break;
                    case R.id.recurrence_monthly_radio:
                        NEW_RULE.putValue(RecurrenceRule.FREQ, RecurrenceRule.FREQ_MONTHLY);
                        NEW_RULE.putValue(RecurrenceRule.INTERVAL, 1);
                        break;
                    case R.id.recurrence_yearly_radio:
                        NEW_RULE.putValue(RecurrenceRule.FREQ, RecurrenceRule.FREQ_YEARLY);
                        NEW_RULE.putValue(RecurrenceRule.INTERVAL, 1);
                        break;
                    case R.id.recurrence_custom_radio:
                        NEW_RULE.putValue(RecurrenceRule.INTERVAL, 1);
                        isCustom = true;
                        break;
                }

                if (isCustom)
                {
                    if (binding.recurrenceCustomRule.getRoot().getVisibility() == View.GONE)
                    {
                        binding.recurrenceCustomRule.getRoot().setVisibility(View.VISIBLE);
                    }
                } else
                {
                    if (binding.recurrenceCustomRule.getRoot().getVisibility() == View.VISIBLE)
                    {
                        binding.recurrenceCustomRule.getRoot().setVisibility(View.GONE);
                    }
                }
            }
        }
    };

    private final TextWatcher textWatcher = new TextWatcher()
    {
        String previousValue;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {
            previousValue = charSequence.toString();
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {
            if (charSequence.toString().isEmpty())
            {
                if (binding.recurrenceCustomRule.recurrenceInterval.isFocused())
                {
                    binding.recurrenceCustomRule.recurrenceInterval.setText("1");
                } else if (binding.recurrenceDetailRule.certainRecurrenceCount.isFocused())
                {
                    binding.recurrenceDetailRule.certainRecurrenceCount.setText("1");
                }
            }

            if (!previousValue.equals(charSequence.toString()))
            {
                if (Integer.parseInt(charSequence.toString()) <= 1)
                {
                    if (binding.recurrenceCustomRule.recurrenceInterval.isFocused())
                    {
                        binding.recurrenceCustomRule.recurrenceInterval.setText("1");
                    } else if (binding.recurrenceDetailRule.certainRecurrenceCount.isFocused())
                    {
                        binding.recurrenceDetailRule.certainRecurrenceCount.setText("1");
                    }
                }
            }

        }

        @Override
        public void afterTextChanged(Editable editable)
        {
            if (binding.recurrenceCustomRule.recurrenceInterval.isFocused())
            {
                NEW_RULE.putValue(RecurrenceRule.INTERVAL, editable.toString());
            } else if (binding.recurrenceDetailRule.certainRecurrenceCount.isFocused())
            {
                NEW_RULE.putValue(RecurrenceRule.COUNT, editable.toString());
            }
        }
    };
}