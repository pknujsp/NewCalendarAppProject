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
import android.widget.RadioGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.ActivityRecurrenceBinding;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.Calendar;

import biweekly.property.RecurrenceProperty;
import biweekly.property.RecurrenceRule;
import biweekly.util.Frequency;
import biweekly.util.Recurrence;

public class RecurrenceActivity extends AppCompatActivity
{
    private ActivityRecurrenceBinding binding;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private String recurrenceRule;
    private String finalRecurrenceRule;

    private static final int NOT_RECURRENCE = 0;
    private static final int FREQ_DAILY = 1;
    private static final int FREQ_WEEKLY = 2;
    private static final int FREQ_MONTHLY = 3;
    private static final int FREQ_YEARLY = 4;

    private int recurrenceDateType = 0;
    private Calendar eventStartDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recurrence);
        recurrenceRule = getIntent().getStringExtra("recurrenceRule");
        eventStartDateTime = (Calendar) getIntent().getSerializableExtra("eventStartDateTime");
        init();
    }

    private void init()
    {
        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        spinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.recurrence_date_types, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.dateTypeSpinner.setAdapter(spinnerAdapter);
        binding.dateTypeSpinner.setSelection(0, false);

        binding.recurrenceRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id)
            {
                boolean isCustom = false;

                switch (id)
                {
                    case R.id.not_recurrence_radio:
                        setDefaultRecurrenceRule(NOT_RECURRENCE);
                        break;
                    case R.id.recurrence_daily_radio:
                        setDefaultRecurrenceRule(FREQ_DAILY);
                        break;
                    case R.id.recurrence_weekly_radio:
                        setDefaultRecurrenceRule(FREQ_WEEKLY);
                        break;
                    case R.id.recurrence_monthly_radio:
                        setDefaultRecurrenceRule(FREQ_MONTHLY);
                        break;
                    case R.id.recurrence_yearly_radio:
                        setDefaultRecurrenceRule(FREQ_YEARLY);
                        break;
                    case R.id.recurrence_custom_radio:
                        binding.recurrenceCustomViewContainer.setVisibility(View.VISIBLE);
                        isCustom = true;
                        break;
                }

                if (!isCustom)
                {
                    if (binding.recurrenceCustomViewContainer.getVisibility() == View.VISIBLE)
                    {
                        binding.recurrenceCustomViewContainer.setVisibility(View.GONE);
                    }
                }
            }
        });

        binding.firstDayRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
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

        binding.dateTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l)
            {
                switch (index)
                {
                    case 0:
                        recurrenceDateType = FREQ_DAILY;
                        binding.firstDayRadioGroup.setVisibility(View.GONE);
                        break;
                    case 1:
                        recurrenceDateType = FREQ_WEEKLY;
                        binding.firstDayRadioGroup.setVisibility(View.GONE);
                        break;
                    case 2:
                        recurrenceDateType = FREQ_MONTHLY;
                        binding.firstDayRadioGroup.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        recurrenceDateType = FREQ_YEARLY;
                        binding.firstDayRadioGroup.setVisibility(View.VISIBLE);
                        break;
                }
                setFirstDayRadioGroupText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });

        binding.recurrenceCycle.addTextChangedListener(textWatcher);
        binding.certainRecurrenceNumber.addTextChangedListener(textWatcher);

        switch (recurrenceRule)
        {
            case "":
                binding.recurrenceRadioGroup.check(R.id.not_recurrence_radio);
                binding.dateTypeSpinner.setSelection(0);
                break;
            case "FREQ=DAILY":
                binding.recurrenceRadioGroup.check(R.id.recurrence_daily_radio);
                binding.dateTypeSpinner.setSelection(0);
                break;
            case "FREQ=WEEKLY":
                binding.recurrenceRadioGroup.check(R.id.recurrence_weekly_radio);
                binding.dateTypeSpinner.setSelection(0);
                break;
            case "FREQ=MONTHLY":
                binding.recurrenceRadioGroup.check(R.id.recurrence_monthly_radio);
                binding.dateTypeSpinner.setSelection(0);
                break;
            case "FREQ=YEARLY":
                binding.recurrenceRadioGroup.check(R.id.recurrence_yearly_radio);
                binding.dateTypeSpinner.setSelection(0);
                break;
            default:
                // custom인 경우
                break;
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    private void setDefaultRecurrenceRule(int frequency)
    {
        switch (frequency)
        {
            case NOT_RECURRENCE:
                recurrenceRule = "";
                break;
            case FREQ_DAILY:
                recurrenceRule = "FREQ=DAILY";
                break;
            case FREQ_WEEKLY:
                recurrenceRule = "FREQ=WEEKLY";
                break;
            case FREQ_MONTHLY:
                recurrenceRule = "FREQ=MONTHLY";
                break;
            case FREQ_YEARLY:
                recurrenceRule = "FREQ=YEARLY";
                break;
        }
    }


    private void setFirstDayRadioGroupText()
    {
        if (recurrenceDateType == FREQ_MONTHLY || recurrenceDateType == FREQ_YEARLY)
        {
            String sameDateText = null;
            String sameWeekText = null;
            final String dayOfWeek = ClockUtil.DAY_OF_WEEK_FORMAT.format(eventStartDateTime.getTime());

            if (recurrenceDateType == FREQ_MONTHLY)
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

            binding.sameDateRadio.setText(sameDateText);
            binding.sameWeekRadio.setText(sameWeekText);
        }
    }

    private void setRecurrenceRule()
    {
     
    }

    private void onCompletedSelection()
    {
        if (recurrenceRule.equals(finalRecurrenceRule))
        {
            setResult(RESULT_CANCELED);
        } else
        {
            getIntent().putExtra("recurrenceRule", finalRecurrenceRule);
            setResult(RESULT_OK, getIntent());
        }

        finish();
    }

    @Override
    public void onBackPressed()
    {
        onCompletedSelection();
    }

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
            if (!previousValue.equals(charSequence.toString()))
            {
                int value = Integer.parseInt(charSequence.toString());

                if (value <= 0)
                {
                    if (binding.recurrenceCycle.isFocused())
                    {
                        binding.recurrenceCycle.setText("0");
                    } else if (binding.certainRecurrenceNumber.isFocused())
                    {
                        binding.certainRecurrenceNumber.setText("0");
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable)
        {
        }
    };
}