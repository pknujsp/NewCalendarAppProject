package com.zerodsoft.calendarplatform.notification.receiver;

import android.content.ContentValues;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.CalendarAlerts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.calendarplatform.calendar.CalendarProvider;
import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.databinding.FragmentAlarmBinding;
import com.zerodsoft.calendarplatform.etc.LocationType;
import com.zerodsoft.calendarplatform.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.calendarplatform.event.util.EventUtil;
import com.zerodsoft.calendarplatform.room.dto.LocationDTO;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class AlarmFragment extends Fragment {
	private FragmentAlarmBinding binding;
	private List<ContentValues> alertInstanceList;
	private LocationViewModel locationViewModel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();
		alertInstanceList = bundle.getParcelableArrayList("instanceList");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentAlarmBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

		ContentValues instance = alertInstanceList.get(0);
		binding.eventTitle.setText(EventUtil.convertTitle(getContext(), instance.getAsString(Events.TITLE)));

		binding.eventDatetime.setText(EventUtil.getSimpleDateTime(getContext(), instance));

		if (instance.containsKey(Events.DESCRIPTION)) {
			binding.eventDescription.setText(instance.getAsString(Events.DESCRIPTION));
		} else {
			binding.eventDescription.setVisibility(View.GONE);
		}

		if (instance.containsKey(Events.EVENT_LOCATION)) {
			if (instance.getAsString(Events.EVENT_LOCATION) != null) {
				locationViewModel.getLocation(instance.getAsLong(CalendarAlerts.EVENT_ID), new DbQueryCallback<LocationDTO>() {
					@Override
					public void onResultSuccessful(LocationDTO result) {
						StringBuilder detailLocationStringBuilder = new StringBuilder();

						if (result.getLocationType() == LocationType.PLACE) {
							detailLocationStringBuilder.append(result.getPlaceName()).append("\n")
									.append(result.getAddressName());
						} else {
							detailLocationStringBuilder.append(result.getAddressName());
						}

						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								binding.eventLocation.setText(detailLocationStringBuilder.toString());
							}
						});
					}

					@Override
					public void onResultNoData() {
						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								binding.locationLayout.setVisibility(View.GONE);
							}
						});
					}
				});
			} else {
				binding.locationLayout.setVisibility(View.GONE);
			}
		} else {
			binding.locationLayout.setVisibility(View.GONE);
		}

		binding.checkEventFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CalendarProvider calendarProvider = new CalendarProvider(getContext());
				calendarProvider.updateEventStatus(instance.getAsLong(CalendarAlerts.EVENT_ID), Events.STATUS_CONFIRMED);

				getActivity().finish();
			}
		});
	}
}