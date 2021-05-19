package com.zerodsoft.scheduleweather.event.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.ActivityNewInstanceMainBinding;

public class NewInstanceMainActivity extends AppCompatActivity {
	private ActivityNewInstanceMainBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_new_instance_main);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		int calendarId = 0;
		long instanceId = 0L;
		long eventId = 0L;
		long begin = 0L;
		long end = 0L;

		if (bundle == null) {
			calendarId = intent.getIntExtra(CalendarContract.Instances.CALENDAR_ID, calendarId);
			instanceId = intent.getLongExtra(CalendarContract.Instances._ID, instanceId);
			eventId = intent.getLongExtra(CalendarContract.Instances.EVENT_ID, eventId);
			begin = intent.getLongExtra(CalendarContract.Instances.BEGIN, begin);
			end = intent.getLongExtra(CalendarContract.Instances.END, end);
		} else {
			calendarId = bundle.getInt(CalendarContract.Instances.CALENDAR_ID);
			instanceId = bundle.getLong(CalendarContract.Instances._ID);
			eventId = bundle.getLong(CalendarContract.Instances.EVENT_ID);
			begin = bundle.getLong(CalendarContract.Instances.BEGIN);
			end = bundle.getLong(CalendarContract.Instances.END);
		}
		NewInstanceMainFragment newInstanceMainFragment = new NewInstanceMainFragment(calendarId, eventId, instanceId, begin, end);
		newInstanceMainFragment.setPlaceBottomSheetSelectBtnVisibility(View.GONE);
		newInstanceMainFragment.setPlaceBottomSheetUnSelectBtnVisibility(View.GONE);
		getSupportFragmentManager().beginTransaction().add(binding.fragmentContainer.getId(), newInstanceMainFragment, NewInstanceMainFragment.TAG).commitNow();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}