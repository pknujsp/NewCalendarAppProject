package com.zerodsoft.scheduleweather.calendarview.assistantcalendar.assistantcalendar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.interfaces.CalendarDateOnClickListener;
import com.zerodsoft.scheduleweather.databinding.FragmentAssistantForMonthBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

public class AssistantForMonthFragment extends Fragment {
	private FragmentAssistantForMonthBinding binding;
	private CalendarDateOnClickListener calendarDateOnClickListener;

	public AssistantForMonthFragment(CalendarDateOnClickListener calendarDateOnClickListener) {
		this.calendarDateOnClickListener = calendarDateOnClickListener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentAssistantForMonthBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		binding.yearPicker.setMinValue(1920);
		binding.yearPicker.setMaxValue(2090);

		binding.monthPicker.setMinValue(1);
		binding.monthPicker.setMaxValue(12);


		binding.moveToDateBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, binding.yearPicker.getValue());
				calendar.set(Calendar.MONTH, binding.monthPicker.getValue() - 1);
				calendarDateOnClickListener.onClickedDate(calendar.getTime());
			}
		});
	}

	public void setCurrentDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		final int year = calendar.get(Calendar.YEAR);
		final int month = calendar.get(Calendar.MONTH) + 1;

		binding.yearPicker.setValue(year);
		binding.monthPicker.setValue(month);
	}
}