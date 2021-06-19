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
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.adapter.TimeZoneRecyclerViewAdapter;
import com.zerodsoft.scheduleweather.activity.editevent.interfaces.ITimeZone;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.databinding.TimezoneFragmentBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TimeZoneFragment extends Fragment implements OnClickedListItem<TimeZone> {
	private OnTimeZoneResultListener onTimeZoneResultListener;
	private TimezoneFragmentBinding binding;
	private TimeZoneRecyclerViewAdapter adapter;

	public TimeZoneFragment(OnTimeZoneResultListener onTimeZoneResultListener) {
		this.onTimeZoneResultListener = onTimeZoneResultListener;
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
		binding.customProgressView.setContentView(binding.timezoneList);
		binding.customProgressView.onSuccessfulProcessingData();

		binding.timezoneList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		binding.timezoneList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

		final String[] timeZones = TimeZone.getAvailableIDs();
		final List<TimeZone> timeZoneList = new ArrayList<>();

		for (String v : timeZones) {
			timeZoneList.add(TimeZone.getTimeZone(v));
		}

		adapter = new TimeZoneRecyclerViewAdapter(this, timeZoneList, new Date(System.currentTimeMillis()));
		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				if (adapter.getItemCount() == 0) {
					binding.customProgressView.onFailedProcessingData(getString(R.string.not_founded_search_result));
				} else {
					binding.customProgressView.onSuccessfulProcessingData();
				}
			}
		});
		binding.timezoneList.setAdapter(adapter);

		binding.customSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				adapter.getFilter().filter(newText);
				return false;
			}
		});

	}

	@Override
	public void onClickedListItem(TimeZone e, int position) {

	}

	@Override
	public void deleteListItem(TimeZone e, int position) {

	}

	public interface OnTimeZoneResultListener {
		void onResult(TimeZone timeZone);
	}
}
