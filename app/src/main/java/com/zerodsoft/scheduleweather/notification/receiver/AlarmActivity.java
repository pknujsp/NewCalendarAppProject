package com.zerodsoft.scheduleweather.notification.receiver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.ContentValues;
import android.os.Bundle;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.ActivityAlarmBinding;

import java.util.List;

public class AlarmActivity extends AppCompatActivity {
	private ActivityAlarmBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_alarm);

		Bundle bundle = getIntent().getExtras();
		List<ContentValues> alertInstanceList = bundle.getParcelableArrayList("instanceList");

		AlarmFragment alarmFragment = new AlarmFragment();
		alarmFragment.setArguments(bundle);

		getSupportFragmentManager().beginTransaction().add(binding.fragmentContainer.getId(),
				alarmFragment, alarmFragment.getTag()).commitNow();
	}
}