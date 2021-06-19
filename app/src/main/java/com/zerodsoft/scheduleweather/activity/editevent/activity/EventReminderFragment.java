package com.zerodsoft.scheduleweather.activity.editevent.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.enums.EventIntentCode;
import com.zerodsoft.scheduleweather.databinding.FragmentEventReminderBinding;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.utility.model.ReminderDto;

import org.jetbrains.annotations.NotNull;

public class EventReminderFragment extends Fragment {
	private FragmentEventReminderBinding binding;
	private OnEventReminderResultListener onEventReminderResultListener;

	private Handler repeatUpdateHandler = new Handler();
	private boolean cIncrement = false;
	private boolean cDecrement = false;

	private final int WEEK = 0;
	private final int DAY = 1;
	private final int HOUR = 2;
	private final int MINUTE = 3;

	private EventIntentCode requestCode;
	private Integer previousMinutes;
	private Integer previousMethod;

	public EventReminderFragment(OnEventReminderResultListener onEventReminderResultListener) {
		this.onEventReminderResultListener = onEventReminderResultListener;
	}

	@Override
	public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		binding = FragmentEventReminderBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Bundle arguments = getArguments();

		binding.eventReminderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@SuppressLint("NonConstantResourceId")
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int id) {
				switch (id) {
					case R.id.not_remind_radio:
						binding.reminderMethodChipGroup.setVisibility(View.GONE);
						binding.reminderSelector.setVisibility(View.GONE);
						break;
					case R.id.ok_remind_radio:
						binding.reminderMethodChipGroup.setVisibility(View.VISIBLE);
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

		binding.upWeek.setOnTouchListener(onTouchListener);
		binding.upDay.setOnTouchListener(onTouchListener);
		binding.upHour.setOnTouchListener(onTouchListener);
		binding.upMinute.setOnTouchListener(onTouchListener);

		binding.downWeek.setOnClickListener(onDownClickListener);
		binding.downDay.setOnClickListener(onDownClickListener);
		binding.downHour.setOnClickListener(onDownClickListener);
		binding.downMinute.setOnClickListener(onDownClickListener);

		binding.downWeek.setOnLongClickListener(onDownLongClickListener);
		binding.downDay.setOnLongClickListener(onDownLongClickListener);
		binding.downHour.setOnLongClickListener(onDownLongClickListener);
		binding.downMinute.setOnLongClickListener(onDownLongClickListener);

		binding.downWeek.setOnTouchListener(onTouchListener);
		binding.downDay.setOnTouchListener(onTouchListener);
		binding.downHour.setOnTouchListener(onTouchListener);
		binding.downMinute.setOnTouchListener(onTouchListener);

		// 알람 추가/수정 여부 확인
		requestCode = EventIntentCode.enumOf(arguments.getInt("requestCode", 0));

		switch (requestCode) {
			case REQUEST_ADD_REMINDER: {
				// 추가할 경우에는 인수를 받지 않음
				binding.eventReminderRadioGroup.check(R.id.not_remind_radio);
			}
			break;
			case REQUEST_MODIFY_REMINDER: {
				// 수정할 경우에는 분, 메소드 인수를 받음
				previousMinutes = arguments.getInt("previousMinutes", 0);
				previousMethod = arguments.getInt("previousMethod", 0);

				switch (previousMethod) {
					case CalendarContract.Reminders.METHOD_ALERT:
						binding.reminderMethodChipGroup.check(R.id.method_alert);
						break;
					case CalendarContract.Reminders.METHOD_EMAIL:
						binding.reminderMethodChipGroup.check(R.id.method_email);
						break;
				}

				ReminderDto reminderDto = EventUtil.convertAlarmMinutes(previousMinutes);

				binding.reminderWeekValue.setText(String.valueOf(reminderDto.getWeek()));
				binding.reminderDayValue.setText(String.valueOf(reminderDto.getDay()));
				binding.reminderHourValue.setText(String.valueOf(reminderDto.getHour()));
				binding.reminderMinuteValue.setText(String.valueOf(reminderDto.getMinute()));

				binding.eventReminderRadioGroup.check(R.id.ok_remind_radio);
			}
			break;
		}
	}

	@Override
	public void onDestroy() {
		//정시에 알림도 있음
		final int newMinutes = EventUtil.convertReminderValues(
				new ReminderDto(Integer.parseInt(binding.reminderWeekValue.getText().toString())
						, Integer.parseInt(binding.reminderDayValue.getText().toString())
						, Integer.parseInt(binding.reminderHourValue.getText().toString())
						, Integer.parseInt(binding.reminderMinuteValue.getText().toString())));

		switch (requestCode) {
			case REQUEST_ADD_REMINDER: {
				if (binding.okRemindRadio.isChecked()) {
					//알람을 추가
					ContentValues reminder = new ContentValues();
					reminder.put(CalendarContract.Reminders.MINUTES, newMinutes);
					reminder.put(CalendarContract.Reminders.METHOD, getMethod());

					onEventReminderResultListener.onResultAddedReminder(reminder);
				} else {
					//알람을 추가하지 않음
				}
				break;
			}

			case REQUEST_MODIFY_REMINDER: {
				if (binding.okRemindRadio.isChecked()) {
					if (previousMinutes == newMinutes && previousMethod == getMethod()) {
						//기존 알람 값과 동일하여 취소로 판단
					} else {
						//알람 수정
						ContentValues reminder = new ContentValues();
						reminder.put(CalendarContract.Reminders.MINUTES, newMinutes);
						reminder.put(CalendarContract.Reminders.METHOD, getMethod());

						onEventReminderResultListener.onResultModifiedReminder(reminder, previousMinutes);
					}
				} else {
					//알람 삭제
					onEventReminderResultListener.onResultRemovedReminder(previousMinutes);
				}
				break;
			}
		}
		super.onDestroy();
	}

	private int getMethod() {
		int method = 0;

		switch (binding.reminderMethodChipGroup.getCheckedChipId()) {
			case R.id.method_alert:
				method = CalendarContract.Reminders.METHOD_ALERT;
				break;
			case R.id.method_email:
				method = CalendarContract.Reminders.METHOD_EMAIL;
				break;
		}

		return method;
	}


	private final TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			if (charSequence.length() == 0) {
				if (binding.reminderMinuteValue.isFocused()) {
					binding.reminderMinuteValue.setText("0");
				} else if (binding.reminderHourValue.isFocused()) {
					binding.reminderHourValue.setText("0");
				} else if (binding.reminderDayValue.isFocused()) {
					binding.reminderDayValue.setText("0");
				} else if (binding.reminderWeekValue.isFocused()) {
					binding.reminderWeekValue.setText("0");
				}
			} else {
				boolean invalidValue = false;
				String value = null;

				if (charSequence.toString().startsWith("0") && charSequence.length() >= 2) {
					invalidValue = true;
					value = Integer.toString(Integer.parseInt(charSequence.toString()));
				}

				if (binding.reminderMinuteValue.isFocused()) {
					if (Integer.parseInt(charSequence.toString()) > 59) {
						binding.reminderMinuteValue.setText("59");
					} else if (invalidValue) {
						binding.reminderMinuteValue.setText(value);
					}

				} else if (binding.reminderHourValue.isFocused()) {
					if (Integer.parseInt(charSequence.toString()) < 0) {
						binding.reminderHourValue.setText("0");
					} else if (invalidValue) {
						binding.reminderHourValue.setText(value);
					}

				} else if (binding.reminderDayValue.isFocused()) {
					if (Integer.parseInt(charSequence.toString()) < 0) {
						binding.reminderDayValue.setText("0");
					} else if (invalidValue) {
						binding.reminderDayValue.setText(value);
					}

				} else if (binding.reminderWeekValue.isFocused()) {
					if (Integer.parseInt(charSequence.toString()) < 0) {
						binding.reminderWeekValue.setText("0");
					} else if (invalidValue) {
						binding.reminderWeekValue.setText(value);
					}

				}
			}
		}

		@Override
		public void afterTextChanged(Editable editable) {

		}
	};

	private void increaseValue(int type) {
		int value = 0;
		switch (type) {
			case WEEK:
				value = Integer.parseInt(binding.reminderWeekValue.getText().toString());
				binding.reminderWeekValue.setText(String.valueOf(++value));
				break;
			case DAY:
				value = Integer.parseInt(binding.reminderDayValue.getText().toString());
				binding.reminderDayValue.setText(String.valueOf(++value));
				break;
			case HOUR:
				value = Integer.parseInt(binding.reminderHourValue.getText().toString());
				binding.reminderHourValue.setText(String.valueOf(++value));
				break;
			case MINUTE:
				value = Integer.parseInt(binding.reminderMinuteValue.getText().toString());
				binding.reminderMinuteValue.setText(String.valueOf(value >= 59 ? 59 : ++value));
				break;
		}
	}


	private void decreaseValue(int type) {
		int value = 0;
		switch (type) {
			case WEEK:
				value = Integer.parseInt(binding.reminderWeekValue.getText().toString());
				binding.reminderWeekValue.setText(String.valueOf(value <= 0 ? 0 : --value));
				break;
			case DAY:
				value = Integer.parseInt(binding.reminderDayValue.getText().toString());
				binding.reminderDayValue.setText(String.valueOf(value <= 0 ? 0 : --value));
				break;
			case HOUR:
				value = Integer.parseInt(binding.reminderHourValue.getText().toString());
				binding.reminderHourValue.setText(String.valueOf(value <= 0 ? 0 : --value));
				break;
			case MINUTE:
				value = Integer.parseInt(binding.reminderMinuteValue.getText().toString());
				binding.reminderMinuteValue.setText(String.valueOf(value <= 0 ? 0 : --value));
				break;
		}
	}

	private final View.OnClickListener onUpClickListener = new View.OnClickListener() {
		@SuppressLint("NonConstantResourceId")
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
				case R.id.up_week:
					increaseValue(WEEK);
					break;
				case R.id.up_day:
					increaseValue(DAY);
					break;
				case R.id.up_hour:
					increaseValue(HOUR);
					break;
				case R.id.up_minute:
					increaseValue(MINUTE);
					break;
			}
		}
	};

	private final View.OnLongClickListener onUpLongClickListener = new View.OnLongClickListener() {
		@SuppressLint("NonConstantResourceId")
		@Override
		public boolean onLongClick(View view) {
			cIncrement = true;

			switch (view.getId()) {
				case R.id.up_week:
					repeatUpdateHandler.post(new RptUpdater(WEEK));
					return false;
				case R.id.up_day:
					repeatUpdateHandler.post(new RptUpdater(DAY));
					return false;
				case R.id.up_hour:
					repeatUpdateHandler.post(new RptUpdater(HOUR));
					return false;
				case R.id.up_minute:
					repeatUpdateHandler.post(new RptUpdater(MINUTE));
					return false;
			}
			return true;
		}
	};

	private final View.OnClickListener onDownClickListener = new View.OnClickListener() {
		@SuppressLint("NonConstantResourceId")
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
				case R.id.down_week:
					decreaseValue(WEEK);
					break;
				case R.id.down_day:
					decreaseValue(DAY);
					break;
				case R.id.down_hour:
					decreaseValue(HOUR);
					break;
				case R.id.down_minute:
					decreaseValue(MINUTE);
					break;
			}
		}
	};

	private final View.OnLongClickListener onDownLongClickListener = new View.OnLongClickListener() {
		@SuppressLint("NonConstantResourceId")
		@Override
		public boolean onLongClick(View view) {
			cDecrement = true;

			switch (view.getId()) {
				case R.id.down_week:
					repeatUpdateHandler.post(new RptUpdater(WEEK));
					return false;
				case R.id.down_day:
					repeatUpdateHandler.post(new RptUpdater(DAY));
					return false;
				case R.id.down_hour:
					repeatUpdateHandler.post(new RptUpdater(HOUR));
					return false;
				case R.id.down_minute:
					repeatUpdateHandler.post(new RptUpdater(MINUTE));
					return false;
			}
			return true;
		}
	};


	private final View.OnTouchListener onTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
				if (cIncrement) {
					cIncrement = false;
				} else {
					cDecrement = false;
				}
			}
			return false;
		}
	};

	class RptUpdater implements Runnable {
		int type;

		public RptUpdater(int type) {
			this.type = type;
		}

		public void run() {
			if (cIncrement) {
				increaseValue(type);
				repeatUpdateHandler.postDelayed(new RptUpdater(type), 40);
			} else if (cDecrement) {
				decreaseValue(type);
				repeatUpdateHandler.postDelayed(new RptUpdater(type), 40);
			}
		}
	}

	public interface OnEventReminderResultListener {
		void onResultModifiedReminder(ContentValues reminder, int previousMinutes);

		void onResultAddedReminder(ContentValues reminder);

		void onResultRemovedReminder(int previousMinutes);
	}
}