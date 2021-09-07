package com.zerodsoft.calendarplatform.notification.receiver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.ContentValues;
import android.os.Bundle;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.databinding.ActivityAlarmBinding;

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