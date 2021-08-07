package com.zerodsoft.scheduleweather.activity.editevent.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.text.util.Rfc822Token;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.calendarcommon2.EventRecurrence;
import com.zerodsoft.scheduleweather.databinding.FragmentRecurrenceBinding;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class RecurrenceFragment extends Fragment {
	private FragmentRecurrenceBinding binding;
	private OnResultRecurrence onResultRecurrence;

	private long startDate;

	private Date untilDate;
	private EventRecurrence givedEventRecurrence;

	private Map<RepeatRadioType, ViewGroup> primaryLayoutMap = new HashMap<>();
	private Map<RepeatCountRadioType, ViewGroup> countLayoutMap = new HashMap<>();

	private RepeatRadioType lastRepeatRadioType = RepeatRadioType.NOT_REPEAT;

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

		if (rrule != null) {
			givedEventRecurrence = new EventRecurrence();
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

		binding.repeatDailyLayout.setVisibility(View.GONE);
		binding.repeatWeeklyLayout.setVisibility(View.GONE);
		binding.repeatMonthLayout.setVisibility(View.GONE);
		binding.repeatYearlyLayout.setVisibility(View.GONE);
		binding.repeatCountLayout.setVisibility(View.GONE);
		binding.repeatUntilLayout.setVisibility(View.GONE);

		primaryLayoutMap.put(RepeatRadioType.REPEAT_DAILY, binding.repeatDailyLayout);
		primaryLayoutMap.put(RepeatRadioType.REPEAT_WEEKLY, binding.repeatWeeklyLayout);
		primaryLayoutMap.put(RepeatRadioType.REPEAT_MONTHLY, binding.repeatMonthLayout);
		primaryLayoutMap.put(RepeatRadioType.REPEAT_YEARLY, binding.repeatYearlyLayout);

		countLayoutMap.put(RepeatCountRadioType.LIMIT_COUNT, binding.repeatCountLayout);
		countLayoutMap.put(RepeatCountRadioType.UNTIL, binding.repeatUntilLayout);

		binding.radioNotRepeat.setTag(RepeatRadioType.NOT_REPEAT);
		binding.radioRepeatDaily.setTag(RepeatRadioType.REPEAT_DAILY);
		binding.radioRepeatWeekly.setTag(RepeatRadioType.REPEAT_WEEKLY);
		binding.radioRepeatMonthly.setTag(RepeatRadioType.REPEAT_MONTHLY);
		binding.radioRepeatYearly.setTag(RepeatRadioType.REPEAT_YEARLY);

		binding.repeatEndless.setTag(RepeatCountRadioType.ENDLESS_COUNT);
		binding.repeatCount.setTag(RepeatCountRadioType.LIMIT_COUNT);
		binding.repeatUntil.setTag(RepeatCountRadioType.UNTIL);

		binding.repeatDailyIntervalEdittext.addTextChangedListener(new CustomTextWatcher(binding.repeatDailyIntervalEdittext));
		binding.repeatWeeklyIntervalEdittext.addTextChangedListener(new CustomTextWatcher(binding.repeatWeeklyIntervalEdittext));
		binding.repeatMonthlyIntervalEdittext.addTextChangedListener(new CustomTextWatcher(binding.repeatMonthlyIntervalEdittext));
		binding.repeatYearlyIntervalEdittext.addTextChangedListener(new CustomTextWatcher(binding.repeatYearlyIntervalEdittext));
		binding.repeatCountEdittext.addTextChangedListener(new CustomTextWatcher(binding.repeatCountEdittext));

		binding.repeatPrimaryRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RepeatRadioType clickedRepeatRadioType = (RepeatRadioType) group.findViewById(checkedId).getTag();
				setPrimaryLayoutState(clickedRepeatRadioType);
			}
		});

		binding.repeatMonthDateRadiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

			}
		});

		binding.repeatCountRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == binding.repeatCount.getId()) {
					binding.repeatCountLayout.setVisibility(View.VISIBLE);
					binding.repeatUntilLayout.setVisibility(View.GONE);
				} else if (checkedId == binding.repeatUntil.getId()) {
					binding.repeatCountLayout.setVisibility(View.GONE);
					binding.repeatUntilLayout.setVisibility(View.VISIBLE);
				} else {
					binding.repeatCountLayout.setVisibility(View.GONE);
					binding.repeatUntilLayout.setVisibility(View.GONE);
				}
			}
		});

		binding.repeatUntilTextview.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TimeZone timeZone = TimeZone.getDefault();
				final int offset = timeZone.getOffset(untilDate.getTime());

				long convertedToUtcDate = untilDate.getTime() + offset;

				MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
				builder.setTitleText(R.string.datepicker)
						.setSelection(convertedToUtcDate);
				MaterialDatePicker<Long> picker = builder.build();
				picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
					@Override
					public void onPositiveButtonClick(Long selection) {
						untilDate.setTime(selection);
						binding.repeatUntilTextview.setText(ClockUtil.YYYY_M_D_E.format(untilDate));
					}
				});
				picker.show(getChildFragmentManager(), picker.toString());
			}
		});

		untilDate = new Date(startDate);
		binding.repeatUntilTextview.setText(ClockUtil.YYYY_M_D_E.format(untilDate));

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(untilDate);

		SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("매월 d일", Locale.KOREAN);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("매월 W번째 주 E요일", Locale.KOREAN);

		binding.radioRepeatMonthlySameDayOfMonth.setText(simpleDateFormat1.format(untilDate));
		binding.radioRepeatMonthlySameWeekDay.setText(simpleDateFormat2.format(untilDate));

		final int dayOfWeek = EventRecurrence.calendarDay2Day(calendar.get(Calendar.DAY_OF_WEEK));
		if (dayOfWeek == EventRecurrence.SU) {
			binding.sundayChip.setChecked(true);
		} else if (dayOfWeek == EventRecurrence.MO) {
			binding.mondayChip.setChecked(true);
		} else if (dayOfWeek == EventRecurrence.TU) {
			binding.tuesdayChip.setChecked(true);
		} else if (dayOfWeek == EventRecurrence.WE) {
			binding.wednesdayChip.setChecked(true);
		} else if (dayOfWeek == EventRecurrence.TH) {
			binding.thursdayChip.setChecked(true);
		} else if (dayOfWeek == EventRecurrence.FR) {
			binding.fridayChip.setChecked(true);
		} else if (dayOfWeek == EventRecurrence.SA) {
			binding.saturdayChip.setChecked(true);
		}

		if (givedEventRecurrence != null) {
			switch (givedEventRecurrence.freq) {
				case EventRecurrence.DAILY:
					binding.radioRepeatDaily.setChecked(true);
					binding.repeatDailyIntervalEdittext.setText(String.valueOf(givedEventRecurrence.interval));
					break;
				case EventRecurrence.WEEKLY:
					binding.radioRepeatWeekly.setChecked(true);
					binding.repeatWeeklyIntervalEdittext.setText(String.valueOf(givedEventRecurrence.interval));
					final int byDayCount = givedEventRecurrence.bydayCount;

					if (byDayCount > 0) {
						final int[] byDayArr = givedEventRecurrence.byday;

						for (int byDay : byDayArr) {
							if (byDay == EventRecurrence.SU) {
								binding.sundayChip.setChecked(true);
							} else if (byDay == EventRecurrence.MO) {
								binding.mondayChip.setChecked(true);
							} else if (byDay == EventRecurrence.TU) {
								binding.tuesdayChip.setChecked(true);
							} else if (byDay == EventRecurrence.WE) {
								binding.wednesdayChip.setChecked(true);
							} else if (byDay == EventRecurrence.TH) {
								binding.thursdayChip.setChecked(true);
							} else if (byDay == EventRecurrence.FR) {
								binding.fridayChip.setChecked(true);
							} else if (byDay == EventRecurrence.SA) {
								binding.saturdayChip.setChecked(true);
							}
						}
					}
					break;
				case EventRecurrence.MONTHLY:
					binding.radioRepeatMonthly.setChecked(true);
					binding.repeatMonthlyIntervalEdittext.setText(String.valueOf(givedEventRecurrence.interval));

					if (givedEventRecurrence.bymonthdayCount > 0) {
						binding.radioRepeatMonthlySameDayOfMonth.setChecked(true);
					} else {
						binding.radioRepeatMonthlySameWeekDay.setChecked(true);
					}
					break;
				case EventRecurrence.YEARLY:
					binding.radioRepeatYearly.setChecked(true);
					binding.repeatYearlyIntervalEdittext.setText(String.valueOf(givedEventRecurrence.interval));
					break;
			}

			if (givedEventRecurrence.count > 0) {
				binding.repeatCount.setChecked(true);
				binding.repeatCountEdittext.setText(String.valueOf(givedEventRecurrence.count));
			} else if (givedEventRecurrence.until != null) {
				binding.repeatUntil.setChecked(true);
				Time until = new Time();
				until.parse(givedEventRecurrence.until);
				untilDate = new Date(until.toMillis(true));
				binding.repeatUntilTextview.setText(ClockUtil.YYYY_M_D_E.format(untilDate));
			} else {
				binding.repeatEndless.setChecked(true);
			}
		} else {
			binding.radioNotRepeat.setChecked(true);
			binding.repeatEndless.setChecked(true);
		}
	}

	@Override
	public void onDestroy() {
		if (binding.radioNotRepeat.isChecked()) {
			onResultRecurrence.onResult("");
		} else {
			EventRecurrence newEventRecurrence = new EventRecurrence();

			if (binding.radioRepeatDaily.isChecked()) {
				newEventRecurrence.freq = EventRecurrence.DAILY;
				newEventRecurrence.interval = Integer.parseInt(binding.repeatDailyIntervalEdittext.getText().toString());
			} else if (binding.radioRepeatWeekly.isChecked()) {
				newEventRecurrence.freq = EventRecurrence.WEEKLY;
				newEventRecurrence.interval = Integer.parseInt(binding.repeatWeeklyIntervalEdittext.getText().toString());

				final List<Integer> checkedChipIdList = binding.recurrenceDayChipgroup.getCheckedChipIds();
				final int byDayCount = checkedChipIdList.size();
				final int[] byDay = new int[byDayCount];
				final int[] byDayNum = new int[byDayCount];

				int index = 0;
				for (Integer checkedChipId : checkedChipIdList) {
					int dayOfWeek = Integer.parseInt(String.valueOf(binding.recurrenceDayChipgroup.findViewById(checkedChipId).getTag()));
					byDay[index] = EventRecurrence.timeDay2Day(dayOfWeek);
					index++;
				}

				newEventRecurrence.bydayCount = byDayCount;
				newEventRecurrence.byday = byDay;
				newEventRecurrence.bydayNum = byDayNum;
			} else if (binding.radioRepeatMonthly.isChecked()) {
				newEventRecurrence.freq = EventRecurrence.MONTHLY;
				newEventRecurrence.interval = Integer.parseInt(binding.repeatMonthlyIntervalEdittext.getText().toString());
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(startDate);

				if (binding.radioRepeatMonthlySameDayOfMonth.isChecked()) {
					int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

					newEventRecurrence.bymonthday = new int[]{dayOfMonth};
					newEventRecurrence.bymonthdayCount = 1;
				} else {
					int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
					int dayOfWeek = EventRecurrence.calendarDay2Day(calendar.get(Calendar.DAY_OF_WEEK));

					newEventRecurrence.byday = new int[]{dayOfWeek};
					newEventRecurrence.bydayNum = new int[]{weekOfMonth};
					newEventRecurrence.bydayCount = 1;
					// byday는 요일
					// bydaynum은 weekofmonth 값
					// byday의 문자열 변환 값이 4SU 이면 byday는 EventRecurrence.SU값 하나를 가지고
					// bydaynum은 4값 하나를 가짐
				}

			} else if (binding.radioRepeatYearly.isChecked()) {
				newEventRecurrence.freq = EventRecurrence.YEARLY;
				newEventRecurrence.interval = Integer.parseInt(binding.repeatYearlyIntervalEdittext.getText().toString());
			}

			//count값 지정
			if (binding.repeatEndless.isChecked()) {

			} else if (binding.repeatCount.isChecked()) {
				newEventRecurrence.count = Integer.parseInt(binding.repeatCountEdittext.getText().toString());
			} else if (binding.repeatUntil.isChecked()) {
				Time until = new Time();
				until.set(untilDate.getTime());
				until.hour = 0;
				until.minute = 0;
				until.second = 0;
				newEventRecurrence.until = until.format2445();
			}

			//interval이 1이면 매일/주/월/년 반복이므로 interval삭제
			if (newEventRecurrence.interval == 1) {
				newEventRecurrence.interval = 0;
			}

			newEventRecurrence.wkst = EventRecurrence.SU;
			onResultRecurrence.onResult(newEventRecurrence.toString());
		}
		super.onDestroy();
	}

	private void setPrimaryLayoutState(RepeatRadioType clickedRepeatRadioType) {
		if (lastRepeatRadioType != clickedRepeatRadioType) {
			if (lastRepeatRadioType != RepeatRadioType.NOT_REPEAT) {
				primaryLayoutMap.get(lastRepeatRadioType).setVisibility(View.GONE);
			}

			if (clickedRepeatRadioType != RepeatRadioType.NOT_REPEAT) {
				primaryLayoutMap.get(clickedRepeatRadioType).setVisibility(View.VISIBLE);
			}
		}

		if (clickedRepeatRadioType == RepeatRadioType.NOT_REPEAT) {
			if (binding.repeatCountRadioGroup.getVisibility() != View.GONE) {
				binding.repeatCountRadioGroup.setVisibility(View.GONE);
			}
		} else {
			if (binding.repeatCountRadioGroup.getVisibility() != View.VISIBLE) {
				binding.repeatCountRadioGroup.setVisibility(View.VISIBLE);
			}
		}
		lastRepeatRadioType = clickedRepeatRadioType;
	}


	public void processOnTextChanged(EditText editText, CharSequence s, int start, int before, int count) {
		if (s.length() > 0) {
			if (s.charAt(0) == '0') {
				String removedVal = s.toString().substring(1);
				editText.setText(removedVal);
			}
		}
	}


	public void processAfterTextChanged(Editable s) {
		if (s.length() < 1) {
			s.append("1");
		} else if (Integer.parseInt(s.toString()) < 1) {
			s.clear();
			s.append("1");
		}
	}

	public interface OnResultRecurrence {
		void onResult(String rrule);
	}

	class CustomTextWatcher implements TextWatcher {
		EditText editText;

		public CustomTextWatcher(EditText editText) {
			this.editText = editText;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			processOnTextChanged(editText, s, start, before, count);
		}

		@Override
		public void afterTextChanged(Editable s) {
			processAfterTextChanged(s);
		}
	}
}