package com.zerodsoft.scheduleweather.activity.editevent.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.adapter.AttendeeListAdapter;
import com.zerodsoft.scheduleweather.databinding.FragmentAttendeesBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AttendeesFragment extends Fragment {
	private final String EMAIL_REGRESSION = "^[a-zA-Z0-9.+-/*]+@[a-zA-Z0-9/*-+]+\\.[a-zA-Z]{1,6}$";

	private FragmentAttendeesBinding binding;
	private AttendeeListAdapter adapter;
	private ContentValues organizer;
	private OnAttendeesResultListener onAttendeesResultListener;


	public AttendeesFragment(OnAttendeesResultListener onAttendeesResultListener) {
		this.onAttendeesResultListener = onAttendeesResultListener;
	}

	@Override
	public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		binding = FragmentAttendeesBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.customProgressView.setContentView(binding.attendeeList);

		binding.authorityModifyEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
				if (isChecked) {
					binding.authorityAttendeeInvites.setChecked(true);
					binding.authorityAttendeeShowAttendees.setChecked(true);

					binding.authorityAttendeeInvites.setEnabled(false);
					binding.authorityAttendeeShowAttendees.setEnabled(false);
				} else {
					binding.authorityAttendeeInvites.setEnabled(true);
					binding.authorityAttendeeShowAttendees.setEnabled(true);
				}
			}
		});
		Bundle arguments = getArguments();

		List<ContentValues> attendeeList = arguments.getParcelableArrayList("attendeeList");
		organizer = arguments.getParcelable("organizer");

		adapter = new AttendeeListAdapter(organizer);
		adapter.setAttendeeList(attendeeList);
		adapter.registerAdapterDataObserver(adapterDataObserver);

		binding.attendeeList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
		binding.attendeeList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		binding.attendeeList.setAdapter(adapter);

		if (attendeeList.isEmpty()) {
			binding.customProgressView.onFailedProcessingData(getString(R.string.not_attendee));
		} else {
			binding.customProgressView.onSuccessfulProcessingData();
		}

		boolean guestsModify = arguments.getBoolean(CalendarContract.Events.GUESTS_CAN_MODIFY, false);
		boolean guestsCanInviteOthers = arguments.getBoolean(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, false);
		boolean guestsCanSeeGuests = arguments.getBoolean(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, false);

		binding.authorityAttendeeInvites.setChecked(guestsCanInviteOthers);
		binding.authorityAttendeeShowAttendees.setChecked(guestsCanSeeGuests);
		binding.authorityModifyEvent.setChecked(guestsModify);

		binding.customSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				if (!query.isEmpty()) {
                    /*
                    이메일 주소인지 파악한다.
                    이메일이 아니면 검색 완료클릭시 이메일 주소가 아닙니다라는 내용의 메시지를 표시
                    */
					if (query.matches(EMAIL_REGRESSION)) {
						final String selectedCalendarOwnerAccount = organizer.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL);
						final String selectedCalendarCalendarName = organizer.getAsString(CalendarContract.Attendees.ATTENDEE_NAME);
						// 중복 여부 확인
						// 리스트내에 이미 존재하는지 확인
						List<ContentValues> attendeeList = adapter.getAttendeeList();

						for (ContentValues value : attendeeList) {
							if (value.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL).equals(query)) {
								Toast.makeText(getContext(), R.string.duplicate_attendee, Toast.LENGTH_SHORT).show();
								return false;
							}
						}

						// 이벤트의 캘린더와 중복되는지 확인
						if (query.equals(selectedCalendarOwnerAccount) ||
								query.equals(selectedCalendarCalendarName)) {
							Toast.makeText(getContext(), R.string.duplicate_attendee, Toast.LENGTH_SHORT).show();
							return false;
						}

						if (attendeeList.isEmpty()) {
							// 리스트가 비어있는 경우에는 이벤트에서 선택된 캘린더를 리스트의 맨 앞에 위치시킨다.
							attendeeList.add(organizer);
						}
						ContentValues attendee = new ContentValues();
						attendee.put(CalendarContract.Attendees.ATTENDEE_EMAIL, query);
						attendee.put(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP, CalendarContract.Attendees.RELATIONSHIP_ATTENDEE);
						attendee.put(CalendarContract.Attendees.ATTENDEE_STATUS, CalendarContract.Attendees.ATTENDEE_STATUS_INVITED);
						attendeeList.add(attendee);

						adapter.notifyDataSetChanged();
						return true;
					} else {
						Toast.makeText(getContext(), R.string.not_matches_with_email, Toast.LENGTH_SHORT).show();
						return false;
					}
				} else {
					Toast.makeText(getContext(), R.string.not_matches_with_email, Toast.LENGTH_SHORT).show();
					return false;
				}
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
	}

	@Override
	public void onDestroy() {
		if (adapter.getAttendeeList().isEmpty()) {
			binding.authorityModifyEvent.setChecked(false);
			binding.authorityAttendeeInvites.setChecked(false);
			binding.authorityAttendeeShowAttendees.setChecked(false);
		}

		onAttendeesResultListener.onResult(adapter.getAttendeeList(), binding.authorityModifyEvent.isChecked()
				, binding.authorityAttendeeInvites.isChecked(), binding.authorityAttendeeShowAttendees.isChecked());

		super.onDestroy();
	}

	private final RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver() {
		@Override
		public void onChanged() {
			super.onChanged();

			if (adapter.getItemCount() == 0) {
				binding.customProgressView.onFailedProcessingData(getString(R.string.not_attendee));
				if (binding.authorityChipGroup.getVisibility() == View.VISIBLE) {
					binding.authorityChipGroup.setVisibility(View.GONE);
				}
			} else if (binding.authorityChipGroup.getVisibility() == View.GONE) {
				binding.authorityChipGroup.setVisibility(View.VISIBLE);
			} else {
				binding.customProgressView.onSuccessfulProcessingData();
			}
		}

	};

	public interface OnAttendeesResultListener {
		void onResult(List<ContentValues> newAttendeeList, boolean guestsCanModify, boolean guestsCanInviteOthers, boolean guestsCanSeeGuests);
	}
}