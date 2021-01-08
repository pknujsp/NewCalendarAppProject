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
    private final RecurrenceRule GIVED_RULE = new RecurrenceRule();
    private final RecurrenceRule NEW_RULE = new RecurrenceRule();

    private final int[] dayViewIds = {R.id.recurrence_sunday, R.id.recurrence_monday, R.id.recurrence_tuesday
            , R.id.recurrence_wednesday, R.id.recurrence_thursday, R.id.recurrence_friday, R.id.recurrence_saturday};

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
                        if (RecurrenceRule.FREQ_MONTHLY.equals(NEW_RULE.getValue(RecurrenceRule.FREQ)))
                        {
                            NEW_RULE.removeValue(RecurrenceRule.BYDAY);
                        }
                        break;
                    case R.id.same_week_radio:
                        if (RecurrenceRule.FREQ_MONTHLY.equals(NEW_RULE.getValue(RecurrenceRule.FREQ)))
                        {
                            int weekOfMonth = eventStartDateTime.get(Calendar.WEEK_OF_MONTH);
                            String day = NEW_RULE.getDayOfWeek(eventStartDateTime.get(Calendar.DAY_OF_WEEK) - 1);
                            NEW_RULE.putValue(RecurrenceRule.BYDAY, weekOfMonth + day);
                        }
                        break;
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
                        showDaysChips = true;
                        break;
                    case 2:
                        NEW_RULE.putValue(RecurrenceRule.FREQ, RecurrenceRule.FREQ_MONTHLY);
                        showFirstDayRadio = true;
                        break;
                    case 3:
                        NEW_RULE.putValue(RecurrenceRule.FREQ, RecurrenceRule.FREQ_YEARLY);
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

        binding.recurrenceDetailRule.keepingRecurrenceRadio.setOnCheckedChangeListener(detailRadioCheckedListener);
        binding.recurrenceDetailRule.recurrenceEndDateRadio.setOnCheckedChangeListener(detailRadioCheckedListener);
        binding.recurrenceDetailRule.certainRecurrenceCountRadio.setOnCheckedChangeListener(detailRadioCheckedListener);

        binding.recurrenceDetailRule.recurrenceEndDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                long dateMillisec = 0L;

                if (NEW_RULE.containsKey(RecurrenceRule.UNTIL))
                {
                    Calendar calendar = convertDate(NEW_RULE.getValue(RecurrenceRule.UNTIL));
                    dateMillisec = calendar.getTimeInMillis();
                } else
                {
                    dateMillisec = eventStartDateTime.getTimeInMillis();
                }

                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText(R.string.datepicker)
                        .setSelection(dateMillisec);
                MaterialDatePicker<Long> picker = builder.build();
                picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>()
                {
                    @Override
                    public void onPositiveButtonClick(Long selection)
                    {
                        String selectedDate = ClockUtil.yyyyMMdd.format(new Date(selection));
                        NEW_RULE.removeValue(RecurrenceRule.UNTIL);
                        NEW_RULE.removeValue(RecurrenceRule.COUNT);
                        NEW_RULE.putValue(RecurrenceRule.UNTIL, selectedDate);

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(selection);
                        binding.recurrenceDetailRule.recurrenceEndDate.setText(ClockUtil.YYYY_년_M_월_D_일_E.format(calendar.getTime()));
                    }
                });
                picker.show(getSupportFragmentManager(), picker.toString());
            }
        });

        binding.recurrenceCustomRule.recurrenceInterval.addTextChangedListener(textWatcher);
        binding.recurrenceDetailRule.certainRecurrenceCount.addTextChangedListener(textWatcher);

        if (!GIVED_RULE.isEmpty())
        {
            // interval 값에 따라 커스텀인지 아닌지 여부가 결정된다.
            // freq, interval, firstday(월 만 해당), until, byday, count
            binding.recurrenceCustomRadio.setChecked(true);

            int dateTypeSpinnerIndex = 0;

            switch (GIVED_RULE.getValue(RecurrenceRule.FREQ))
            {
                case RecurrenceRule.FREQ_DAILY:
                    dateTypeSpinnerIndex = 0;
                    break;
                case RecurrenceRule.FREQ_WEEKLY:
                    dateTypeSpinnerIndex = 1;
                    break;
                case RecurrenceRule.FREQ_MONTHLY:
                    dateTypeSpinnerIndex = 2;
                    break;
                case RecurrenceRule.FREQ_YEARLY:
                    dateTypeSpinnerIndex = 3;
                    break;
            }
            NEW_RULE.clear();
            NEW_RULE.separateValues(GIVED_RULE.getRule());

            binding.recurrenceCustomRule.dateTypeSpinner.setSelection(dateTypeSpinnerIndex);

            if (GIVED_RULE.containsKey(RecurrenceRule.INTERVAL))
            {
                binding.recurrenceCustomRule.recurrenceInterval.setText(GIVED_RULE.getValue(RecurrenceRule.INTERVAL));
            }

            if (GIVED_RULE.containsKey(RecurrenceRule.UNTIL))
            {
                binding.recurrenceDetailRule.recurrenceEndDateRadio.setChecked(true);
                Calendar calendar = convertDate(GIVED_RULE.getValue(RecurrenceRule.UNTIL));
                binding.recurrenceDetailRule.recurrenceEndDate.setText(ClockUtil.YYYY_년_M_월_D_일_E.format(calendar.getTime()));
            } else
            {
                binding.recurrenceDetailRule.recurrenceEndDate.setText(ClockUtil.YYYY_년_M_월_D_일_E.format(eventStartDateTime.getTime()));
            }

            binding.recurrenceCustomRule.recurrenceDayView.getRoot().check(dayViewIds[eventStartDateTime.get(Calendar.DAY_OF_WEEK) - 1]);

            if (GIVED_RULE.containsKey(RecurrenceRule.BYDAY))
            {
                final int[] days = getSelectedDays(GIVED_RULE.getValue(RecurrenceRule.BYDAY));
                if (GIVED_RULE.getValue(RecurrenceRule.FREQ).equals(RecurrenceRule.FREQ_WEEKLY))
                {
                    for (int v : days)
                    {
                        binding.recurrenceCustomRule.recurrenceDayView.getRoot().check(dayViewIds[v]);
                    }
                } else if (GIVED_RULE.getValue(RecurrenceRule.FREQ).equals(RecurrenceRule.FREQ_MONTHLY))
                {
                    binding.recurrenceCustomRule.firstDayRadioGroup.check(R.id.same_week_radio);
                }
            }

            if (GIVED_RULE.containsKey(RecurrenceRule.COUNT))
            {
                binding.recurrenceDetailRule.certainRecurrenceCount.setText(GIVED_RULE.getValue(RecurrenceRule.COUNT));
                binding.recurrenceDetailRule.certainRecurrenceCountRadio.setChecked(true);
            }
            if (!GIVED_RULE.containsKey(RecurrenceRule.COUNT) && !GIVED_RULE.containsKey(RecurrenceRule.UNTIL))
            {
                binding.recurrenceDetailRule.keepingRecurrenceRadio.setChecked(true);
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
            binding.recurrenceCustomRule.recurrenceDayView.getRoot().check(dayViewIds[eventStartDateTime.get(Calendar.DAY_OF_WEEK) - 1]);
            binding.recurrenceDetailRule.recurrenceEndDate.setText(ClockUtil.YYYY_년_M_월_D_일_E.format(eventStartDateTime.getTime()));
            binding.recurrenceDetailRule.keepingRecurrenceRadio.setChecked(true);
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
        if (NEW_RULE.getValue(RecurrenceRule.FREQ).equals(RecurrenceRule.FREQ_MONTHLY)
                || NEW_RULE.getValue(RecurrenceRule.FREQ).equals(RecurrenceRule.FREQ_YEARLY))
        {
            String sameDateText = null;
            String sameWeekText = null;
            final String dayOfWeek = ClockUtil.E.format(eventStartDateTime.getTime());

            if (NEW_RULE.getValue(RecurrenceRule.FREQ).equals(RecurrenceRule.FREQ_MONTHLY))
            {
                // 매월 5일 마다, 매월 첫째주 월요일 마다
                sameDateText = "매월 " + eventStartDateTime.get(Calendar.DAY_OF_MONTH) + "일 마다";
                sameWeekText = "매월 " + eventStartDateTime.get(Calendar.WEEK_OF_MONTH) + "번째 주 " + dayOfWeek + "요일 마다";
            } else
            {
                // 매년 1월 5일 마다, 매년 첫째주 월요일 마다
                sameDateText = "매년 " + ClockUtil.M_월_D_일.format(eventStartDateTime.getTime()) + " 마다";
                sameWeekText = "매년 " + eventStartDateTime.get(Calendar.WEEK_OF_YEAR) + "번째 주 " + dayOfWeek + "요일 마다";
            }
            binding.recurrenceCustomRule.sameDateRadio.setText(sameDateText);
            binding.recurrenceCustomRule.sameWeekRadio.setText(sameWeekText);
        }
    }

    private void onCompletedSelection()
    {
        if (!binding.notRecurrenceRadio.isChecked())
        {
            if (binding.recurrenceCustomRadio.isChecked())
            {
                NEW_RULE.putValue(RecurrenceRule.INTERVAL, binding.recurrenceCustomRule.recurrenceInterval.getText().toString());

                if (NEW_RULE.getValue(RecurrenceRule.FREQ).equals(RecurrenceRule.FREQ_WEEKLY))
                {
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
                }
            }
            if (binding.recurrenceDetailRule.certainRecurrenceCountRadio.isChecked())
            {
                NEW_RULE.putValue(RecurrenceRule.COUNT, binding.recurrenceDetailRule.certainRecurrenceCount.getText().toString());
            }
        }
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
                boolean notRecurrence = false;
                NEW_RULE.removeValue(RecurrenceRule.FREQ, RecurrenceRule.BYDAY, RecurrenceRule.INTERVAL);

                switch (compoundButton.getId())
                {
                    case R.id.not_recurrence_radio:
                        notRecurrence = true;
                        break;
                    case R.id.recurrence_daily_radio:
                        NEW_RULE.putValue(RecurrenceRule.FREQ, RecurrenceRule.FREQ_DAILY);
                        break;
                    case R.id.recurrence_weekly_radio:
                        NEW_RULE.putValue(RecurrenceRule.FREQ, RecurrenceRule.FREQ_WEEKLY);
                        String day = RecurrenceRule.getDayOfWeek(eventStartDateTime.get(Calendar.DAY_OF_WEEK) - 1);
                        NEW_RULE.putValue(RecurrenceRule.BYDAY, day);
                        break;
                    case R.id.recurrence_monthly_radio:
                        NEW_RULE.putValue(RecurrenceRule.FREQ, RecurrenceRule.FREQ_MONTHLY);
                        int weekOfMonth = eventStartDateTime.get(Calendar.WEEK_OF_MONTH);
                        String day2 = RecurrenceRule.getDayOfWeek(eventStartDateTime.get(Calendar.DAY_OF_WEEK) - 1);
                        NEW_RULE.putValue(RecurrenceRule.BYDAY, weekOfMonth + day2);
                        break;
                    case R.id.recurrence_yearly_radio:
                        NEW_RULE.putValue(RecurrenceRule.FREQ, RecurrenceRule.FREQ_YEARLY);
                        break;
                    case R.id.recurrence_custom_radio:
                        isCustom = true;
                        binding.recurrenceCustomRule.dateTypeSpinner.setSelection(0);
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
                    case R.id.keeping_recurrence_radio:
                        NEW_RULE.removeValue(RecurrenceRule.UNTIL);
                        NEW_RULE.removeValue(RecurrenceRule.COUNT);
                        binding.recurrenceDetailRule.recurrenceEndDateRadio.setChecked(false);
                        binding.recurrenceDetailRule.certainRecurrenceCountRadio.setChecked(false);
                        break;
                    case R.id.recurrence_end_date_radio:
                        // 날짜 선택 다이얼로그 표시
                        binding.recurrenceDetailRule.keepingRecurrenceRadio.setChecked(false);
                        binding.recurrenceDetailRule.certainRecurrenceCountRadio.setChecked(false);

                        long dateMillisec;

                        if (NEW_RULE.containsKey(RecurrenceRule.UNTIL))
                        {
                            Calendar calendar = convertDate(NEW_RULE.getValue(RecurrenceRule.UNTIL));
                            dateMillisec = calendar.getTimeInMillis();
                        } else
                        {
                            dateMillisec = eventStartDateTime.getTimeInMillis();
                        }

                        String selectedDate = ClockUtil.yyyyMMdd.format(new Date(dateMillisec));

                        NEW_RULE.removeValue(RecurrenceRule.COUNT);
                        NEW_RULE.putValue(RecurrenceRule.UNTIL, selectedDate);
                        break;
                    case R.id.certain_recurrence_count_radio:
                        binding.recurrenceDetailRule.keepingRecurrenceRadio.setChecked(false);
                        binding.recurrenceDetailRule.recurrenceEndDateRadio.setChecked(false);

                        NEW_RULE.removeValue(RecurrenceRule.UNTIL);
                        NEW_RULE.putValue(RecurrenceRule.COUNT, binding.recurrenceDetailRule.certainRecurrenceCount.toString());
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
                } else if (binding.recurrenceDetailRule.certainRecurrenceCount.isFocused())
                {
                    binding.recurrenceDetailRule.certainRecurrenceCount.setText("1");
                }
            }
            if (charSequence.toString().startsWith("0"))
            {
                String value = Integer.toString(Integer.parseInt(charSequence.toString()));
                if (binding.recurrenceCustomRule.recurrenceInterval.isFocused())
                {
                    binding.recurrenceCustomRule.recurrenceInterval.setText(value);
                } else if (binding.recurrenceDetailRule.certainRecurrenceCount.isFocused())
                {
                    binding.recurrenceDetailRule.certainRecurrenceCount.setText(value);
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