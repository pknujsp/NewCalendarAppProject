package com.zerodsoft.calendarplatform.activity.editevent.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.activity.editevent.adapter.AttendeeListAdapter;
import com.zerodsoft.calendarplatform.databinding.FragmentAttendeesBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AttendeesFragment extends Fragment {
	private final String EMAIL_REGRESSION = "^[a-zA-Z0-9.+-/*]+@[a-zA-Z0-9/*-+]+\\.[a-zA-Z]{1,6}$";

	private FragmentAttendeesBinding binding;
	private AttendeeListAdapter adapter;
	private ContentValues selectedAccount;
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

		binding.guestsCanModify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
				if (isChecked) {
					binding.guestsCanInviteOthers.setChecked(true);
					binding.guestsCanSeeGuests.setChecked(true);
					binding.guestsCanInviteOthers.setEnabled(false);
					binding.guestsCanSeeGuests.setEnabled(false);
				} else {
					binding.guestsCanInviteOthers.setEnabled(true);
					binding.guestsCanSeeGuests.setEnabled(true);
				}
			}
		});

		Bundle arguments = getArguments();
		List<ContentValues> attendeeList = arguments.getParcelableArrayList("attendeeList");
		selectedAccount = arguments.getParcelable("selectedAccount");

		adapter = new AttendeeListAdapter(selectedAccount);

		binding.attendeeList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
		binding.attendeeList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		binding.attendeeList.setAdapter(adapter);

		adapter.registerAdapterDataObserver(adapterDataObserver);

		List<ContentValues> finalAttendeeList = new ArrayList<>();
		for (ContentValues attendee : attendeeList) {
			if (attendee.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL).equals(selectedAccount.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL))) {
			} else {
				finalAttendeeList.add(attendee);
			}
		}
		adapter.setAttendeeList(finalAttendeeList);
		adapter.notifyDataSetChanged();

		if (attendeeList.isEmpty()) {
			binding.customProgressView.onFailedProcessingData(getString(R.string.not_attendee));
		} else {
			binding.customProgressView.onSuccessfulProcessingData();
		}

		final boolean guestsCanModify = arguments.getBoolean(CalendarContract.Events.GUESTS_CAN_MODIFY, false);
		final boolean guestsCanInviteOthers = arguments.getBoolean(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, false);
		final boolean guestsCanSeeGuests = arguments.getBoolean(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, false);

		binding.guestsCanInviteOthers.setChecked(guestsCanInviteOthers);
		binding.guestsCanSeeGuests.setChecked(guestsCanSeeGuests);
		binding.guestsCanModify.setChecked(guestsCanModify);

		binding.customSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				if (!query.isEmpty()) {
                    /*
                    이메일 주소인지 파악한다.
                    이메일이 아니면 검색 완료클릭시 이메일 주소가 아닙니다라는 내용의 메시지를 표시
                    */
					if (query.matches(EMAIL_REGRESSION)) {
						final String selectedCalendarOwnerAccount = selectedAccount.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL);
						final String selectedCalendarCalendarName = selectedAccount.getAsString(CalendarContract.Attendees.ATTENDEE_NAME);
						// 중복 여부 확인
						// 리스트내에 이미 존재하는지 확인
						List<ContentValues> attendeeList = adapter.getAttendeeList();

						for (ContentValues value : attendeeList) {
							if (value.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL).equals(query)) {
								Toast.makeText(getContext(), R.string.duplicate_attendee, Toast.LENGTH_SHORT).show();
								return false;
							}
						}

						// organizer와 중복되는지 확인
						if (query.equals(selectedCalendarOwnerAccount) ||
								query.equals(selectedCalendarCalendarName)) {
							Toast.makeText(getContext(), R.string.not_available_add_organizer_as_attendee, Toast.LENGTH_SHORT).show();
							return false;
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
			binding.guestsCanModify.setChecked(false);
			binding.guestsCanInviteOthers.setChecked(false);
			binding.guestsCanSeeGuests.setChecked(false);
		}

		onAttendeesResultListener.onResult(adapter.getAttendeeList(), binding.guestsCanModify.isChecked()
				, binding.guestsCanInviteOthers.isChecked(), binding.guestsCanSeeGuests.isChecked());

		super.onDestroy();
	}

	private final RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver() {
		@Override
		public void onChanged() {
			super.onChanged();

			if (adapter.getItemCount() == 0) {
				binding.customProgressView.onFailedProcessingData(getString(R.string.not_attendee));
				binding.authorityChipGroup.setVisibility(View.GONE);
			} else {
				binding.authorityChipGroup.setVisibility(View.VISIBLE);
				binding.customProgressView.onSuccessfulProcessingData();
			}
		}

	};

	public interface OnAttendeesResultListener {
		void onResult(List<ContentValues> newAttendeeList, boolean guestsCanModify, boolean guestsCanInviteOthers, boolean guestsCanSeeGuests);
	}
}