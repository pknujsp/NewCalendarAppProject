package com.zerodsoft.scheduleweather.activity.editevent.fragments;

import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.adapter.TimeZoneRecyclerViewAdapter;
import com.zerodsoft.scheduleweather.activity.editevent.interfaces.ITimeZone;
import com.zerodsoft.scheduleweather.databinding.TimezoneFragmentBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TimeZoneFragment extends Fragment {
	private TimezoneFragmentBinding binding;
	private TimeZoneRecyclerViewAdapter adapter;
	private ITimeZone iTimeZone;
	private final long startTime = System.currentTimeMillis();

	public TimeZoneFragment() {

	}

	public void setiTimeZone(ITimeZone iTimeZone) {
		this.iTimeZone = iTimeZone;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = TimezoneFragmentBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		binding.timezoneList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		binding.timezoneList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

		final String[] timeZones = TimeZone.getAvailableIDs();
		final List<TimeZone> timeZoneList = new ArrayList<>();

		for (String v : timeZones) {
			timeZoneList.add(TimeZone.getTimeZone(v));
		}

		Date startDate = new Date(startTime);

		adapter = new TimeZoneRecyclerViewAdapter(iTimeZone, timeZoneList, startDate);
		binding.timezoneList.setAdapter(adapter);

		binding.searchTimezone.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				// 실시간 검색
				adapter.getFilter().filter(charSequence);
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});
	}
}
