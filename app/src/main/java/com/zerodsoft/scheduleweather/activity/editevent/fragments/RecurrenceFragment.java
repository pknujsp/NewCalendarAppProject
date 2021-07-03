package com.zerodsoft.scheduleweather.activity.editevent.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.calendarcommon2.EventRecurrence;
import com.zerodsoft.scheduleweather.databinding.FragmentRecurrenceBinding;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;


public class RecurrenceFragment extends Fragment {
	private FragmentRecurrenceBinding binding;
	private OnResultRecurrence onResultRecurrence;

	private long startDate;
	private EventRecurrence givedEventRecurrence = new EventRecurrence();

	final Set<MaterialRadioButton> mainRadioButtonSet = new HashSet<>();
	final Set<MaterialRadioButton> countRadioButtonSet = new HashSet<>();

	enum RepeatRadioType {
		NOT_REPEAT,
		REPEAT_DAILY,
		REPEAT_WEEKLY,
		REPEAT_MONTHLY,
		REPEAT_YEARLY
	}

	enum RepeatCountRadioType {
		ENDLESS_COUNT,
		LIMIT_COUNT,
		UNTIL
	}


	public RecurrenceFragment(OnResultRecurrence onResultRecurrence) {
		this.onResultRecurrence = onResultRecurrence;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();
		String rrule = bundle.getString(CalendarContract.Events.RRULE);
		startDate = bundle.getLong(CalendarContract.Events.DTSTART);

		if (!rrule.isEmpty()) {
			givedEventRecurrence.parse(rrule);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentRecurrenceBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		binding.repeatWeekLayout.setVisibility(View.GONE);
		binding.repeatMonthLayout.setVisibility(View.GONE);

		binding.radioNotRepeat.setTag(RepeatRadioType.NOT_REPEAT);
		binding.radioRepeatDaily.getRadioButton().setTag(RepeatRadioType.REPEAT_DAILY);
		binding.radioRepeatWeekly.getRadioButton().setTag(RepeatRadioType.REPEAT_WEEKLY);
		binding.radioRepeatMonthly.getRadioButton().setTag(RepeatRadioType.REPEAT_MONTHLY);
		binding.radioRepeatYearly.getRadioButton().setTag(RepeatRadioType.REPEAT_YEARLY);

		binding.repeatEndless.setTag(RepeatCountRadioType.ENDLESS_COUNT);
		binding.repeatCount.getRadioButton().setTag(RepeatCountRadioType.LIMIT_COUNT);
		binding.repeatUntil.getRadioButton().setTag(RepeatCountRadioType.UNTIL);

		CompoundButton.OnCheckedChangeListener radioButtonOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				RepeatRadioType repeatRadioType = (RepeatRadioType) buttonView.getTag();

				if (repeatRadioType == RepeatRadioType.REPEAT_WEEKLY) {
					binding.repeatWeekLayout.setVisibility(View.VISIBLE);
					binding.repeatMonthLayout.setVisibility(View.GONE);
				} else if (repeatRadioType == RepeatRadioType.REPEAT_MONTHLY) {
					binding.repeatWeekLayout.setVisibility(View.GONE);
					binding.repeatMonthLayout.setVisibility(View.VISIBLE);
				} else {
					binding.repeatWeekLayout.setVisibility(View.GONE);
					binding.repeatMonthLayout.setVisibility(View.GONE);
				}

				for (MaterialRadioButton anotherRadio : mainRadioButtonSet) {
					if (!anotherRadio.equals((MaterialRadioButton) buttonView)) {
						if (anotherRadio.isChecked()) {
							anotherRadio.setChecked(false);
						}
					}
				}
			}
		};

		binding.radioNotRepeat.setOnCheckedChangeListener(radioButtonOnCheckedChangeListener);
		binding.radioRepeatDaily.getRadioButton().setOnCheckedChangeListener(radioButtonOnCheckedChangeListener);
		binding.radioRepeatWeekly.getRadioButton().setOnCheckedChangeListener(radioButtonOnCheckedChangeListener);
		binding.radioRepeatMonthly.getRadioButton().setOnCheckedChangeListener(radioButtonOnCheckedChangeListener);
		binding.radioRepeatYearly.getRadioButton().setOnCheckedChangeListener(radioButtonOnCheckedChangeListener);

		mainRadioButtonSet.add(binding.radioNotRepeat);
		mainRadioButtonSet.add(binding.radioRepeatDaily.getRadioButton());
		mainRadioButtonSet.add(binding.radioRepeatWeekly.getRadioButton());
		mainRadioButtonSet.add(binding.radioRepeatMonthly.getRadioButton());
		mainRadioButtonSet.add(binding.radioRepeatYearly.getRadioButton());

		CompoundButton.OnCheckedChangeListener countRadioButtonOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				for (MaterialRadioButton anotherRadio : countRadioButtonSet) {
					if (!anotherRadio.equals((MaterialRadioButton) buttonView)) {
						if (anotherRadio.isChecked()) {
							anotherRadio.setChecked(false);
						}
					}
				}
			}
		};
		binding.repeatEndless.setOnCheckedChangeListener(countRadioButtonOnCheckedChangeListener);
		binding.repeatCount.getRadioButton().setOnCheckedChangeListener(countRadioButtonOnCheckedChangeListener);
		binding.repeatUntil.getRadioButton().setOnCheckedChangeListener(countRadioButtonOnCheckedChangeListener);

		countRadioButtonSet.add(binding.repeatEndless);
		countRadioButtonSet.add(binding.repeatCount.getRadioButton());
		countRadioButtonSet.add(binding.repeatUntil.getRadioButton());

		binding.radioRepeatDaily.setMainText(getString(R.string.repeat_days));
		binding.radioRepeatWeekly.setMainText(getString(R.string.repeat_weeks));
		binding.radioRepeatMonthly.setMainText(getString(R.string.repeat_months));
		binding.radioRepeatYearly.setMainText(getString(R.string.repeat_years));

		binding.repeatCount.setMainText(getString(R.string.repeat_specific_count));
		binding.repeatUntil.setMainText(getString(R.string.repeat_until));

		binding.radioRepeatDaily.applyMainText();
		binding.radioRepeatWeekly.applyMainText();
		binding.radioRepeatMonthly.applyMainText();
		binding.radioRepeatYearly.applyMainText();

		binding.repeatCount.applyMainText();
		binding.repeatUntil.applyMainText();
	}

	public interface OnResultRecurrence {
		void onResult(String rrule);
	}
}