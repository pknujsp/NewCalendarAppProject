package com.zerodsoft.scheduleweather.notification.receiver;

import android.content.ContentValues;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.provider.CalendarContract.CalendarAlerts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.calendar.CalendarProvider;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.databinding.FragmentAlarmBinding;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


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

		StringBuilder dateTimeStringBuilder = new StringBuilder();
		boolean isAllDay = instance.getAsInteger(Events.ALL_DAY) == 1;
		if (isAllDay) {
			int startDay = instance.getAsInteger(Instances.START_DAY);
			int endDay = instance.getAsInteger(Instances.END_DAY);
			int dayDifference = endDay - startDay;

			if (startDay == endDay) {
				dateTimeStringBuilder.append(EventUtil.convertDate(instance.getAsLong(Instances.BEGIN)));
			} else {
				Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
				calendar.setTimeInMillis(instance.getAsLong(Instances.BEGIN));
				dateTimeStringBuilder.append(EventUtil.convertDate(calendar.getTime().getTime())).append("\n").append(" -> ");

				calendar.add(Calendar.DAY_OF_YEAR, dayDifference);
				dateTimeStringBuilder.append(EventUtil.convertDate(calendar.getTime().getTime()));
			}
		} else {
			dateTimeStringBuilder.append(EventUtil.convertDateTime(instance.getAsLong(Instances.BEGIN), false,
					App.isPreference_key_using_24_hour_system()))
					.append("\n")
					.append(" -> ")
					.append(EventUtil.convertDateTime(instance.getAsLong(Instances.END), false,
							App.isPreference_key_using_24_hour_system()));
		}

		binding.eventDatetime.setText(dateTimeStringBuilder.toString());

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