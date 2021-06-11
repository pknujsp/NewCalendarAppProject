package com.zerodsoft.scheduleweather.event.common;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.ActivityEmptyMapBinding;

public class SelectionDetailLocationActivity extends AppCompatActivity {
	protected ActivityEmptyMapBinding binding;
	protected SelectionDetailLocationFragment naverMapFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_empty_map);

		naverMapFragment = new SelectionDetailLocationFragment();
		getSupportFragmentManager().beginTransaction().add(binding.mapFragmentContainer.getId(),
				naverMapFragment, "").commit();
	}

}
