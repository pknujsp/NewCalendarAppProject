package com.zerodsoft.scheduleweather.activity.editevent.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.ActivityRecurrenceBinding;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.RecurrenceRule;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.PatternSyntaxException;

public class RecurrenceActivity extends AppCompatActivity
{
    private ActivityRecurrenceBinding binding;
    private ArrayAdapter<CharSequence> spinnerAdapter;

    private Calendar eventStartDateTime;
    private Calendar untilDateTime;
    private final RecurrenceRule GIVED_RULE = new RecurrenceRule();
    private final RecurrenceRule NEW_RULE = new RecurrenceRule();

    private final int[] dayViewIds = {R.id.recurrence_sunday, R.id.recurrence_monday, R.id.recurrence_tuesday
            , R.id.recurrence_wednesday, R.id.recurrence_thursday, R.id.recurrence_friday, R.id.recurrence_saturday};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recurrence);

        String givedRecurrenceRuleStr = getIntent().getStringExtra(CalendarContract.Events.RRULE);
        eventStartDateTime = Calendar.getInstance();
        eventStartDateTime.setTimeInMillis(getIntent().getLongExtra(CalendarContract.Events.DTSTART,0L));

        untilDateTime = (Calendar) eventStartDateTime.clone();
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

        binding.recurrenceCustomRule.recurrenceDayView.getRoot().check(dayViewIds[eventStartDateTime.get(Calendar.DAY_OF_WEEK) - 1]);
        binding.recurrenceDetailRule.recurrenceUntil.setText(ClockUtil.YYYY_년_M_월_D_일_E.format(eventStartDateTime.getTime()));
        binding.recurrenceDetailRule.notEndRadio.setChecked(true);

        binding.notRecurrenceRadio.setOnCheckedChangeListener(recurrenceCheckedListener);
        binding.recurrenceDailyRadio.setOnCheckedChangeListener(recurrenceCheckedListener);
        binding.recurrenceWeeklyRadio.setOnCheckedChangeListener(recurrenceCheckedListener);
        binding.recurrenceMonthlyRadio.setOnCheckedChangeListener(recurrenceCheckedListener);
        binding.recurrenceYearlyRadio.setOnCheckedChangeListener(recurrenceCheckedListener);
        binding.recurrenceCustomRadio.setOnCheckedChangeListener(recurrenceCheckedListener);

        binding.recurrenceDetailRule.notEndRadio.setOnCheckedChangeListener(detailRadioCheckedListener);
        binding.recurrenceDetailRule.recurrenceUntilRadio.setOnCheckedChangeListener(detailRadioCheckedListener);
        binding.recurrenceDetailRule.recurrenceCountRadio.setOnCheckedChangeListener(detailRadioCheckedListener);

        binding.recurrenceCustomRule.dateTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l)
            {
                boolean showFirstDayRadio = false;
                boolean showDaysChips = false;

                switch (index)
                {
                    case 1:
                        showDaysChips = true;
                        break;
                    case 2:
                        showFirstDayRadio = true;
                        setFirstDayRadioGroupText();
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });

        binding.recurrenceDetailRule.recurrenceUntil.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final long dateMillisec = untilDateTime.getTimeInMillis();

                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText(R.string.datepicker)
                        .setSelection(dateMillisec);
                MaterialDatePicker<Long> picker = builder.build();
                picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>()
                {
                    @Override
                    public void onPositiveButtonClick(Long selection)
                    {
                        untilDateTime.setTimeInMillis(selection);
                        binding.recurrenceDetailRule.recurrenceUntil.setText(ClockUtil.YYYY_년_M_월_D_일_E.format(untilDateTime.getTime()));
                    }
                });
                picker.show(getSupportFragmentManager(), picker.toString());
            }
        });

        binding.recurrenceCustomRule.recurrenceInterval.addTextChangedListener(textWatcher);
        binding.recurrenceDetailRule.recurrenceCount.addTextChangedListener(textWatcher);

        // 뷰에 값을 설정
        if (!GIVED_RULE.isEmpty())
        {
            // interval 값에 따라 커스텀인지 아닌지 여부가 결정된다.
            // freq, interval, firstday(월 만 해당), until, byday, count
            binding.recurrenceCustomRadio.setChecked(true);

            boolean showFirstDayRadio = false;
            boolean showDaysChips = false;

            switch (GIVED_RULE.getValue(RecurrenceRule.FREQ))
            {
                case RecurrenceRule.FREQ_DAILY:
                    binding.recurrenceCustomRule.dateTypeSpinner.setSelection(0, false);
                    break;
                case RecurrenceRule.FREQ_WEEKLY:
                    binding.recurrenceCustomRule.dateTypeSpinner.setSelection(1, false);
                    showDaysChips = true;
                    break;
                case RecurrenceRule.FREQ_MONTHLY:
                    binding.recurrenceCustomRule.dateTypeSpinner.setSelection(2, false);
                    showFirstDayRadio = true;
                    break;
                case RecurrenceRule.FREQ_YEARLY:
                    binding.recurrenceCustomRule.dateTypeSpinner.setSelection(3, false);
                    break;
            }

            if (!showDaysChips)
            {
                binding.recurrenceCustomRule.recurrenceDayView.getRoot().setVisibility(View.GONE);
            }
            if (!showFirstDayRadio)
            {
                binding.recurrenceCustomRule.firstDayRadioGroup.setVisibility(View.GONE);
            }

            if (GIVED_RULE.containsKey(RecurrenceRule.INTERVAL))
            {
                binding.recurrenceCustomRule.recurrenceInterval.setText(GIVED_RULE.getValue(RecurrenceRule.INTERVAL));
            }

            if (GIVED_RULE.containsKey(RecurrenceRule.UNTIL))
            {
                binding.recurrenceDetailRule.recurrenceUntilRadio.setChecked(true);
                Calendar calendar = convertDate(GIVED_RULE.getValue(RecurrenceRule.UNTIL));
                binding.recurrenceDetailRule.recurrenceUntil.setText(ClockUtil.YYYY_년_M_월_D_일_E.format(calendar.getTime()));
            }

            if (GIVED_RULE.containsKey(RecurrenceRule.BYDAY))
            {
                final int[] days = getSelectedDays(GIVED_RULE.getValue(RecurrenceRule.BYDAY));
                if (GIVED_RULE.getValue(RecurrenceRule.FREQ).equals(RecurrenceRule.FREQ_WEEKLY))
                {
                    for (int v : days)
                    {
                        if (binding.recurrenceCustomRule.recurrenceDayView.getRoot().getCheckedChipId() != dayViewIds[v])
                        {
                            binding.recurrenceCustomRule.recurrenceDayView.getRoot().check(dayViewIds[v]);
                        }
                    }
                } else if (GIVED_RULE.getValue(RecurrenceRule.FREQ).equals(RecurrenceRule.FREQ_MONTHLY))
                {
                    binding.recurrenceCustomRule.firstDayRadioGroup.check(R.id.same_week_radio);
                }
            }

            if (GIVED_RULE.containsKey(RecurrenceRule.COUNT))
            {
                binding.recurrenceDetailRule.recurrenceCount.setText(GIVED_RULE.getValue(RecurrenceRule.COUNT));
                binding.recurrenceDetailRule.recurrenceCountRadio.setChecked(true);
            } else if (!GIVED_RULE.containsKey(RecurrenceRule.COUNT) && !GIVED_RULE.containsKey(RecurrenceRule.UNTIL))
            {
                binding.recurrenceDetailRule.notEndRadio.setChecked(true);
            }

            // 화면 간소화
            if (binding.recurrenceCustomRule.recurrenceInterval.getText().toString().equals("1"))
            {
                switch (binding.recurrenceCustomRule.dateTypeSpinner.getSelectedItemPosition())
                {
                    case 0:
                        binding.recurrenceDailyRadio.setChecked(true);
                        break;
                    case 1:
                        if (GIVED_RULE.containsKey(RecurrenceRule.BYDAY))
                        {
                            if (GIVED_RULE.getValue(RecurrenceRule.BYDAY).equals(RecurrenceRule.getDayOfWeek(eventStartDateTime.get(Calendar.DAY_OF_WEEK) - 1)))
                            {
                                binding.recurrenceWeeklyRadio.setChecked(true);
                            }
                        }
                        break;
                    case 2:
                        if (GIVED_RULE.containsKey(RecurrenceRule.BYDAY))
                        {
                            binding.recurrenceMonthlyRadio.setChecked(true);
                        }
                        break;
                    case 3:
                        binding.recurrenceYearlyRadio.setChecked(true);
                        break;
                }
            }
        } else
        {
            binding.recurrenceCustomRule.dateTypeSpinner.setSelection(0, false);
            binding.notRecurrenceRadio.setChecked(true);
        }

    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    private void setFirstDayRadioGroupText()
    {
        final String dayOfWeek = ClockUtil.E.format(eventStartDateTime.getTime());

        // 매월 5일 마다, 매월 첫째주 월요일 마다
        StringBuilder sameDate = new StringBuilder();
        StringBuilder sameWeek = new StringBuilder();

        sameDate.append(getString(R.string.recurrence_monthly)).append(" ").append(eventStartDateTime.get(Calendar.DAY_OF_MONTH))
                .append(getString(R.string.N_days_of_each_month)).append(" ").append(getString(R.string.recurrence));

        sameWeek.append(getString(R.string.recurrence_monthly)).append(" ").append(eventStartDateTime.get(Calendar.WEEK_OF_MONTH))
                .append(getString(R.string.N_weeks_of_each_month)).append(" ").append(dayOfWeek).append(getString(R.string.N_day))
                .append(" ").append(getString(R.string.recurrence));

        binding.recurrenceCustomRule.sameDateRadio.setText(sameDate.toString());
        binding.recurrenceCustomRule.sameWeekRadio.setText(sameWeek.toString());
    }

    @Override
    public void onBackPressed()
    {
        onCompletedSelection();
    }

    @SuppressLint("NonConstantResourceId")
    private void onCompletedSelection()
    {
        switch (binding.recurrenceRadioGroup.getCheckedRadioButtonId())
        {
            case R.id.not_recurrence_radio:
                onCompletedNotRecurrence();
                break;
            case R.id.recurrence_daily_radio:
                onCompletedRecurrenceDaily();
                break;
            case R.id.recurrence_weekly_radio:
                onCompletedRecurrenceWeekly();
                break;
            case R.id.recurrence_monthly_radio:
                onCompletedRecurrenceMonthly();
                break;
            case R.id.recurrence_yearly_radio:
                onCompletedRecurrenceYearly();
                break;
            case R.id.recurrence_custom_radio:
                onCompletedRecurrenceCustom();
                break;
        }
        onCompletedRecurrenceDetail();
        getIntent().putExtra(CalendarContract.Events.RRULE, NEW_RULE.getRule());
        setResult(RESULT_OK, getIntent());
        finish();
    }

    private void onCompletedNotRecurrence()
    {
        NEW_RULE.clear();
    }

    private void onCompletedRecurrenceDaily()
    {
        NEW_RULE.putValue(RecurrenceRule.FREQ, RecurrenceRule.FREQ_DAILY);
    }

    private void onCompletedRecurrenceWeekly()
    {
        NEW_RULE.putValue(RecurrenceRule.FREQ, RecurrenceRule.FREQ_WEEKLY);
        NEW_RULE.putValue(RecurrenceRule.BYDAY, RecurrenceRule.getDayOfWeek(eventStartDateTime.get(Calendar.DAY_OF_WEEK) - 1));
    }

    private void onCompletedRecurrenceMonthly()
    {
        NEW_RULE.putValue(RecurrenceRule.FREQ, RecurrenceRule.FREQ_MONTHLY);
        int weekOfMonth = eventStartDateTime.get(Calendar.WEEK_OF_MONTH);
        String day = RecurrenceRule.getDayOfWeek(eventStartDateTime.get(Calendar.DAY_OF_WEEK) - 1);
        NEW_RULE.putValue(RecurrenceRule.BYDAY, weekOfMonth + day);
    }

    private void onCompletedRecurrenceYearly()
    {
        NEW_RULE.putValue(RecurrenceRule.FREQ, RecurrenceRule.FREQ_YEARLY);
    }

    private void onCompletedRecurrenceCustom()
    {
        switch (binding.recurrenceCustomRule.dateTypeSpinner.getSelectedItemPosition())
        {
            case 0:
                NEW_RULE.putValue(RecurrenceRule.FREQ, RecurrenceRule.FREQ_DAILY);
                break;
            case 1:
                NEW_RULE.putValue(RecurrenceRule.FREQ, RecurrenceRule.FREQ_WEEKLY);
                // 반복 요일 지정
                List<Integer> checkedChipIds = binding.recurrenceCustomRule.recurrenceDayView.getRoot().getCheckedChipIds();
                String[] days = new String[checkedChipIds.size()];
                for (int i = 0; i < checkedChipIds.size(); i++)
                {
                    for (int j = 0; j < dayViewIds.length; j++)
                    {
                        if (checkedChipIds.get(i) == dayViewIds[j])
                        {
                            days[i] = RecurrenceRule.getDayOfWeek(j);
                            break;
                        }
                    }
                }
                NEW_RULE.putValue(RecurrenceRule.BYDAY, days);
                break;
            case 2:
                NEW_RULE.putValue(RecurrenceRule.FREQ, RecurrenceRule.FREQ_MONTHLY);
                // 매월 N일과, 매월 M번째주 N요일 중 하나 지정
                if (binding.recurrenceCustomRule.sameWeekRadio.isChecked())
                {
                    int weekOfMonth = eventStartDateTime.get(Calendar.WEEK_OF_MONTH);
                    String day = RecurrenceRule.getDayOfWeek(eventStartDateTime.get(Calendar.DAY_OF_WEEK) - 1);
                    NEW_RULE.putValue(RecurrenceRule.BYDAY, weekOfMonth + day);
                }
                break;
            case 3:
                NEW_RULE.putValue(RecurrenceRule.FREQ, RecurrenceRule.FREQ_YEARLY);
                break;
        }

        NEW_RULE.putValue(RecurrenceRule.INTERVAL, binding.recurrenceCustomRule.recurrenceInterval.getText().toString());
    }

    private void onCompletedRecurrenceDetail()
    {
        if (binding.recurrenceDetailRule.recurrenceUntilRadio.isChecked())
        {
            NEW_RULE.putValue(RecurrenceRule.UNTIL, ClockUtil.yyyyMMdd.format(untilDateTime.getTime()));
        } else if (binding.recurrenceDetailRule.recurrenceCountRadio.isChecked())
        {
            NEW_RULE.putValue(RecurrenceRule.COUNT, binding.recurrenceDetailRule.recurrenceCount.getText().toString());
        }
    }


    private Calendar convertDate(String dateStr)
    {
        String year = dateStr.substring(0, 4);
        String month = dateStr.substring(4, 6);
        String date = dateStr.substring(6, 8);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(date), 0, 0, 0);

        return calendar;
    }

    private int[] getSelectedDays(String value)
    {
        try
        {
            String[] separatedDays = value.split(",");
            int[] days = new int[separatedDays.length];

            for (int i = 0; i < days.length; i++)
            {
                days[i] = RecurrenceRule.getDayOfWeek(separatedDays[i]);
            }
            return days;
        } catch (PatternSyntaxException e)
        {
            return new int[]{RecurrenceRule.getDayOfWeek(value)};
        }
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
                boolean notRecurrence = false;

                switch (compoundButton.getId())
                {
                    case R.id.not_recurrence_radio:
                        notRecurrence = true;
                        break;
                    case R.id.recurrence_custom_radio:
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

                if (notRecurrence)
                {
                    if (binding.recurrenceDetailRule.getRoot().getVisibility() == View.VISIBLE)
                    {
                        binding.recurrenceDetailRule.getRoot().setVisibility(View.GONE);
                    }
                } else
                {
                    if (binding.recurrenceDetailRule.getRoot().getVisibility() == View.GONE)
                    {
                        binding.recurrenceDetailRule.getRoot().setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    };

    private final CompoundButton.OnCheckedChangeListener detailRadioCheckedListener = new CompoundButton.OnCheckedChangeListener()
    {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
        {
            if (isChecked)
            {
                switch (compoundButton.getId())
                {
                    case R.id.not_end_radio:
                        binding.recurrenceDetailRule.recurrenceUntilRadio.setChecked(false);
                        binding.recurrenceDetailRule.recurrenceCountRadio.setChecked(false);
                        break;
                    case R.id.recurrence_until_radio:
                        binding.recurrenceDetailRule.notEndRadio.setChecked(false);
                        binding.recurrenceDetailRule.recurrenceCountRadio.setChecked(false);
                        break;
                    case R.id.recurrence_count_radio:
                        binding.recurrenceDetailRule.notEndRadio.setChecked(false);
                        binding.recurrenceDetailRule.recurrenceUntilRadio.setChecked(false);
                        break;
                }
            }
        }
    };


    private final TextWatcher textWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {
            if (charSequence.toString().isEmpty())
            {
                if (binding.recurrenceCustomRule.recurrenceInterval.isFocused())
                {
                    binding.recurrenceCustomRule.recurrenceInterval.setText("1");
                } else if (binding.recurrenceDetailRule.recurrenceCount.isFocused())
                {
                    binding.recurrenceDetailRule.recurrenceCount.setText("1");
                }
            }
            if (charSequence.toString().startsWith("0"))
            {
                String value = Integer.toString(Integer.parseInt(charSequence.toString()));
                if (binding.recurrenceCustomRule.recurrenceInterval.isFocused())
                {
                    binding.recurrenceCustomRule.recurrenceInterval.setText(value);
                } else if (binding.recurrenceDetailRule.recurrenceCount.isFocused())
                {
                    binding.recurrenceDetailRule.recurrenceCount.setText(value);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable)
        {

        }
    };

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
}