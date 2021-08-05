package com.zerodsoft.scheduleweather.event.event.fragments;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.activity.editevent.activity.ModifyInstanceFragment;
import com.zerodsoft.scheduleweather.activity.editevent.interfaces.OnEditEventResultListener;
import com.zerodsoft.scheduleweather.calendar.AsyncQueryService;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendar.EventHelper;
import com.zerodsoft.scheduleweather.calendar.calendarcommon2.EventRecurrence;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IRefreshView;
import com.zerodsoft.scheduleweather.common.enums.LocationIntentCode;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.databinding.EventFragmentBinding;
import com.zerodsoft.scheduleweather.event.common.DetailLocationSelectorKey;
import com.zerodsoft.scheduleweather.event.common.SelectionDetailLocationFragment;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationHistoryViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.utility.model.ReminderDto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class EventFragment extends BottomSheetDialogFragment {
	// 참석자가 있는 경우 참석 여부 표시
	// 알림 값을 클릭하면 알림표시를 하는 시각을 보여준다
    /*
    공휴일인 경우 : 제목, 날짜, 이벤트 색상, 캘린더 정보만 출력
     */
	private final int VIEW_HEIGHT;
	private final IRefreshView iRefreshView;
	private OnEventFragmentDismissListener onEventFragmentDismissListener;

	private long eventId;
	private long instanceId;
	private long originalBegin;
	private long originalEnd;

	private Dialog dialog;
	private boolean isHidden = false;
	private Boolean isOrganizer = null;
	private Long attendeeIdForThisAccount = null;

	private EventFragmentBinding binding;
	private ContentValues instanceValues;
	private List<ContentValues> attendeeList;
	private CalendarViewModel calendarViewModel;
	private LocationViewModel locationViewModel;
	private ArrayAdapter<CharSequence> answerForInviteSpinnerAdapter;

	private AlertDialog attendeeDialog;
	private BottomSheetBehavior<FrameLayout> bottomSheetBehavior;

	private FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
		@Override
		public void onFragmentAttached(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f, @NonNull @NotNull Context context) {
			super.onFragmentAttached(fm, f, context);
			if (dialog != null) {
				if (f instanceof SelectionDetailLocationFragment) {
					if (fm.findFragmentByTag(getString(R.string.tag_modify_instance_fragment)) == null) {
						isHidden = true;
						dialog.hide();
					}
				} else if (f instanceof ModifyInstanceFragment) {
					isHidden = true;
					dialog.hide();
				}
			}
		}

		@Override
		public void onFragmentDestroyed(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f) {
			super.onFragmentDestroyed(fm, f);
			if (dialog != null) {
				if (f instanceof SelectionDetailLocationFragment) {
					if (fm.findFragmentByTag(getString(R.string.tag_modify_instance_fragment)) == null) {
						isHidden = false;
						dialog.show();
					}
				} else if (f instanceof ModifyInstanceFragment) {
					isHidden = false;
					dialog.show();
				}
			}
		}

	};

	public void showSetLocationDialog() {
		MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity())
				.setTitle(getString(R.string.request_select_location_title))
				.setMessage(getString(R.string.request_select_location_description))
				.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.cancel();
					}
				})
				.setPositiveButton(getString(R.string.check), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						Bundle bundle = new Bundle();
						bundle.putString(DetailLocationSelectorKey.LOCATION_NAME_IN_EVENT.value(),
								instanceValues.getAsString(CalendarContract.Instances.EVENT_LOCATION));
						bundle.putInt("requestCode", LocationIntentCode.REQUEST_CODE_SELECT_LOCATION_BY_QUERY.value());

						SelectionDetailLocationFragment selectionDetailLocationFragment = new SelectionDetailLocationFragment(new SelectionDetailLocationFragment.OnDetailLocationSelectionResultListener() {
							@Override
							public void onResultChangedLocation(LocationDTO newLocation) {

							}

							@Override
							public void onResultSelectedLocation(LocationDTO newLocation) {
								saveDetailLocation(newLocation);
							}

							@Override
							public void onResultUnselectedLocation() {

							}
						});

						selectionDetailLocationFragment.setArguments(bundle);
						getParentFragmentManager().beginTransaction().add(R.id.fragment_container, selectionDetailLocationFragment,
								getString(R.string.tag_detail_location_selection_fragment)).addToBackStack(getString(R.string.tag_detail_location_selection_fragment)).commit();
						dialogInterface.dismiss();
						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								getDialog().hide();
							}
						});
					}
				});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public EventFragment(OnEventFragmentDismissListener onEventFragmentDismissListener, IRefreshView iRefreshView, int VIEW_HEIGHT) {
		this.onEventFragmentDismissListener = onEventFragmentDismissListener;
		this.iRefreshView = iRefreshView;
		this.VIEW_HEIGHT = VIEW_HEIGHT;
	}


	@Override
	public void onDismiss(@NonNull @NotNull DialogInterface dialog) {
		super.onDismiss(dialog);
		if (onEventFragmentDismissListener != null) {
			onEventFragmentDismissListener.onResult(eventId);
		}
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
	}

	@Override
	public void onViewStateRestored(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();

		if (isHidden) {
			dialog.hide();
		} else {
			dialog.show();
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getParentFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, false);
		calendarViewModel = new ViewModelProvider(requireActivity()).get(CalendarViewModel.class);

		Bundle arguments = getArguments();
		eventId = arguments.getLong(CalendarContract.Instances.EVENT_ID);
		instanceId = arguments.getLong(CalendarContract.Instances._ID);
		originalBegin = arguments.getLong(CalendarContract.Instances.BEGIN);
		originalEnd = arguments.getLong(CalendarContract.Instances.END);
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		bottomSheetBehavior = ((BottomSheetDialog) dialog).getBehavior();
		bottomSheetBehavior.setDraggable(false);
		bottomSheetBehavior.setHideable(false);
		bottomSheetBehavior.setPeekHeight(0);
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

		return dialog;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = EventFragmentBinding.inflate(inflater, container, false);
		return binding.getRoot();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		dialog = getDialog();
		View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
		bottomSheet.getLayoutParams().height = VIEW_HEIGHT;

		locationViewModel = new ViewModelProvider(getParentFragment()).get(LocationViewModel.class);

		binding.eventRemindersView.addReminderButton.setVisibility(View.GONE);
		binding.eventAttendeesView.showAttendeesDetail.setVisibility(View.GONE);
		binding.eventDatetimeView.allDaySwitchLayout.setVisibility(View.GONE);

		binding.eventFab.setOnClickListener(new View.OnClickListener() {
			boolean isExpanded = true;

			@Override
			public void onClick(View view) {
				if (isExpanded) {
					binding.eventFab.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.more_icon));
					binding.fabsContainer.setVisibility(View.GONE);
				} else {
					binding.eventFab.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.close_icon));
					binding.fabsContainer.setVisibility(View.VISIBLE);
				}
				isExpanded = !isExpanded;
			}
		});

		binding.selectDetailLocationFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				locationViewModel.hasDetailLocation(eventId, new DbQueryCallback<Boolean>() {
					@Override
					public void onResultSuccessful(Boolean hasDetailLocation) {
						requireActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (hasDetailLocation) {
									locationViewModel.getLocation(eventId, new DbQueryCallback<LocationDTO>() {
										@Override
										public void onResultSuccessful(LocationDTO locationResultDto) {
											if (!locationResultDto.isEmpty()) {
												SelectionDetailLocationFragment selectionDetailLocationFragment = new SelectionDetailLocationFragment(new SelectionDetailLocationFragment.OnDetailLocationSelectionResultListener() {
													@Override
													public void onResultChangedLocation(LocationDTO newLocation) {
														changedDetailLocation(newLocation);
													}

													@Override
													public void onResultSelectedLocation(LocationDTO newLocation) {

													}

													@Override
													public void onResultUnselectedLocation() {
														deletedDetailLocation();
													}
												});

												Bundle bundle = new Bundle();
												bundle.putParcelable(DetailLocationSelectorKey.SELECTED_LOCATION_DTO_IN_EVENT.value(), locationResultDto);
												bundle.putInt("requestCode", LocationIntentCode.REQUEST_CODE_CHANGE_LOCATION.value());

												selectionDetailLocationFragment.setArguments(bundle);
												getParentFragmentManager().beginTransaction().add(R.id.fragment_container, selectionDetailLocationFragment,
														getString(R.string.tag_detail_location_selection_fragment)).addToBackStack(getString(R.string.tag_detail_location_selection_fragment)).commit();
											}
										}

										@Override
										public void onResultNoData() {

										}
									});
								} else {
									showSetLocationDialog();
								}
							}
						});
					}

					@Override
					public void onResultNoData() {

					}
				});

			}
		});

		binding.modifyEventFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ModifyInstanceFragment modifyInstanceFragment = new ModifyInstanceFragment(new OnEditEventResultListener() {
					@Override
					public void onSavedNewEvent(long dtStart) {
						onEventFragmentDismissListener = null;
						getParentFragment().getParentFragmentManager().popBackStackImmediate();
					}

					@Override
					public void onUpdatedOnlyThisEvent(long dtStart) {
						onEventFragmentDismissListener = null;
						getParentFragment().getParentFragmentManager().popBackStackImmediate();
					}

					@Override
					public void onUpdatedFollowingEvents(long dtStart) {
						onEventFragmentDismissListener = null;
						getParentFragment().getParentFragmentManager().popBackStackImmediate();
					}

					@Override
					public void onUpdatedAllEvents(long dtStart) {
						onEventFragmentDismissListener = null;
						getParentFragment().getParentFragmentManager().popBackStackImmediate();
					}

					@Override
					public void onRemovedAllEvents() {

					}

					@Override
					public void onRemovedFollowingEvents() {

					}

					@Override
					public void onRemovedOnlyThisEvents() {

					}

					@Override
					public int describeContents() {
						return 0;
					}

					@Override
					public void writeToParcel(Parcel dest, int flags) {

					}
				});
				Bundle bundle = new Bundle();

				bundle.putLong(CalendarContract.Instances.EVENT_ID, eventId);
				bundle.putLong(CalendarContract.Instances._ID, instanceId);
				bundle.putLong(CalendarContract.Instances.BEGIN, originalBegin);
				bundle.putLong(CalendarContract.Instances.END, originalEnd);

				modifyInstanceFragment.setArguments(bundle);
				getParentFragmentManager().beginTransaction().add(R.id.fragment_container, modifyInstanceFragment,
						getString(R.string.tag_modify_instance_fragment)).addToBackStack(getString(R.string.tag_modify_instance_fragment)).commit();
			}
		});

		binding.removeEventFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String[] items = null;
				//이번 일정만 삭제, 향후 모든 일정 삭제, 모든 일정 삭제
                /*
                반복없는 이벤트 인 경우 : 일정 삭제
                반복있는 이벤트 인 경우 : 이번 일정만 삭제, 향후 모든 일정 삭제, 모든 일정 삭제
                 */
				if (instanceValues.getAsString(CalendarContract.Instances.RRULE) != null) {
					items = new String[]{getString(R.string.remove_this_instance), getString(R.string.remove_all_future_instance_including_current_instance)
							, getString(R.string.remove_event)};
				} else {
					items = new String[]{getString(R.string.remove_event)};
				}
				new MaterialAlertDialogBuilder(getActivity()).setTitle(getString(R.string.remove_event))
						.setItems(items, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int index) {
								if (instanceValues.getAsString(CalendarContract.Instances.RRULE) != null) {
									switch (index) {
										case 0:
											// 이번 일정만 삭제
											// 완성
											showExceptThisInstanceDialog();
											break;
										case 1:
											// 향후 모든 일정만 삭제
											deleteSubsequentIncludingThis();
											break;
										case 2:
											// 모든 일정 삭제
											showDeleteEventDialog();
											break;
									}
								} else {
									switch (index) {
										case 0:
											// 모든 일정 삭제
											showDeleteEventDialog();
											break;
									}
								}
							}
						}).create().show();
			}
		});

		setInstanceData();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getParentFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
	}

	private void showDeleteEventDialog() {
		new MaterialAlertDialogBuilder(requireActivity())
				.setTitle(R.string.remove_event)
				.setPositiveButton(R.string.check, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EventHelper eventHelper = new EventHelper(new AsyncQueryService(getContext(), calendarViewModel));
						eventHelper.removeEvent(EventHelper.EventEditType.REMOVE_ALL_EVENTS, instanceValues);
						dialog.dismiss();
						dismiss();
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
	}


	private void deleteSubsequentIncludingThis() {
		EventHelper eventHelper = new EventHelper(new AsyncQueryService(getContext(), calendarViewModel));
		eventHelper.removeEvent(EventHelper.EventEditType.REMOVE_FOLLOWING_EVENTS, instanceValues);
	}

	private void showExceptThisInstanceDialog() {
		new MaterialAlertDialogBuilder(requireActivity())
				.setTitle(R.string.remove_this_instance)
				.setPositiveButton(R.string.check, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EventHelper eventHelper = new EventHelper(new AsyncQueryService(getContext(), calendarViewModel));
						eventHelper.removeEvent(EventHelper.EventEditType.REMOVE_ONLY_THIS_EVENT, instanceValues);
						dialog.dismiss();
						dismiss();
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
	}

	private void addAttendeeRow(String attendeeEmail, String attendeeRelationshipStr, String attendeeStatusStr) {
		TableRow tableRow = new TableRow(getContext());
		View row = getLayoutInflater().inflate(R.layout.event_attendee_item, null);
		LinearLayout attendeeInfoLayout = (LinearLayout) row.findViewById(R.id.attendee_info_layout);

		TextView attendeeEmailView = (TextView) row.findViewById(R.id.attendee_name);
		TextView attendeeRelationshipView = (TextView) row.findViewById(R.id.attendee_relationship);
		TextView attendeeStatusView = (TextView) row.findViewById(R.id.attendee_status);
		// 삭제버튼 숨기기
		row.findViewById(R.id.remove_attendee_button).setVisibility(View.GONE);

		attendeeInfoLayout.setClickable(true);
		attendeeInfoLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// logics for communications with attendee
				if (attendeeDialog == null) {
					final String[] itemList = {"기능 구성중"};
					MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext())
							.setTitle(attendeeEmail + "(" + attendeeRelationshipStr + ", " + attendeeStatusStr + ")")
							.setItems(itemList, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {

								}
							});
					attendeeDialog = builder.create();
				}
				attendeeDialog.setTitle(attendeeEmail + "(" + attendeeRelationshipStr + ", " + attendeeStatusStr + ")");
				attendeeDialog.show();
				// 기능목록 파악중
			}
		});

		attendeeEmailView.setText(attendeeEmail);
		attendeeRelationshipView.setText(attendeeRelationshipStr);
		attendeeStatusView.setText(attendeeStatusStr);

		tableRow.addView(row);
		binding.eventAttendeesView.eventAttendeesTable.addView(tableRow,
				new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
	}

	private void setAnswerForInviteSpinner() {
		if (attendeeList.isEmpty()) {
			return;
		}

		//이벤트 캘린더 계정의 응답값 확인
		int attendeeStatus = -1;
		int selectionIndex = -1;
		final String accountOfEvent = instanceValues.getAsString(Events.ACCOUNT_NAME);

		if (isOrganizer) {
			attendeeStatus = instanceValues.getAsInteger(Events.SELF_ATTENDEE_STATUS);
		} else {
			for (ContentValues attendee : attendeeList) {
				if (attendee.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL).equals(accountOfEvent)) {
					attendeeIdForThisAccount = attendee.getAsLong(CalendarContract.Attendees._ID);
					attendeeStatus = attendee.getAsInteger(CalendarContract.Attendees.ATTENDEE_STATUS);
					break;
				}
			}
		}

		switch (attendeeStatus) {
			case CalendarContract.Attendees.ATTENDEE_STATUS_ACCEPTED:
				selectionIndex = 0;
				break;
			case CalendarContract.Attendees.ATTENDEE_STATUS_DECLINED:
				selectionIndex = 1;
				break;
			case CalendarContract.Attendees.ATTENDEE_STATUS_INVITED:
				selectionIndex = 2;
				break;
			case CalendarContract.Attendees.ATTENDEE_STATUS_NONE:
				selectionIndex = 3;
				break;
		}

		answerForInviteSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
				isOrganizer ? R.array.answer_for_invite_organizer_spinner :
						R.array.answer_for_invite_attendee_spinner, android.R.layout.simple_spinner_item);
		answerForInviteSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		binding.eventAttendeesView.answerSpinner.setAdapter(answerForInviteSpinnerAdapter);
		binding.eventAttendeesView.answerSpinner.setSelection(selectionIndex);
		binding.eventAttendeesView.answerSpinner.setOnItemSelectedListener(answerSpinnerOnItemSelectedListener);
	}

	private final AdapterView.OnItemSelectedListener answerSpinnerOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
		boolean initializing = true;

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (initializing) {
				initializing = false;
				return;
			}
			ContentValues updateContentValues = new ContentValues();
			Uri uri = null;
			String where = CalendarContract.Attendees._ID + "=?";
			String[] selectionArgs = {attendeeIdForThisAccount.toString()};

			if (isOrganizer) {
				uri = ContentUris.withAppendedId(Events.CONTENT_URI, instanceValues.getAsLong(CalendarContract.Instances.EVENT_ID));
			} else {
				uri = CalendarContract.Attendees.CONTENT_URI;
			}

			switch (position) {
				case 0:
					updateContentValues.put(CalendarContract.Attendees.ATTENDEE_STATUS, CalendarContract.Attendees.ATTENDEE_STATUS_ACCEPTED);
					break;
				case 1:
					updateContentValues.put(CalendarContract.Attendees.ATTENDEE_STATUS, CalendarContract.Attendees.ATTENDEE_STATUS_DECLINED);
					break;
				case 2:
					updateContentValues.put(CalendarContract.Attendees.ATTENDEE_STATUS, CalendarContract.Attendees.ATTENDEE_STATUS_INVITED);
					break;
				case 3:
					updateContentValues.put(CalendarContract.Attendees.ATTENDEE_STATUS, CalendarContract.Attendees.ATTENDEE_STATUS_NONE);
					break;
			}

			getContext().getContentResolver().update(uri, updateContentValues, where, selectionArgs);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};


	private void setInstanceData() {
		// 제목, 캘린더, 시간, 시간대, 반복, 알림, 설명, 위치, 공개범위, 유효성, 참석자
		// 캘린더, 시간대, 참석자 정보는 따로 불러온다.
		// 제목
		instanceValues = calendarViewModel.getInstance(instanceId, originalBegin, originalEnd);

		Date begin = new Date(instanceValues.getAsLong(CalendarContract.Instances.BEGIN));
		Date end = new Date(instanceValues.getAsLong(CalendarContract.Instances.END));

		if (instanceValues.getAsInteger(CalendarContract.Instances.CALENDAR_ACCESS_LEVEL) == CalendarContract.Instances.CAL_ACCESS_READ) {
			binding.fabsLayout.setVisibility(View.GONE);
		}

		if (instanceValues.getAsString(CalendarContract.Instances.TITLE) != null) {
			if (!instanceValues.getAsString(CalendarContract.Instances.TITLE).isEmpty()) {
				binding.eventTitle.setText(instanceValues.getAsString(CalendarContract.Instances.TITLE));
			} else {
				binding.eventTitle.setText(getString(R.string.empty_title));
			}
		} else {
			binding.eventTitle.setText(getString(R.string.empty_title));
		}
		//캘린더
		setCalendarText();

		// 시간대
		boolean isAllDay = instanceValues.getAsInteger(CalendarContract.Instances.ALL_DAY) == 1;
		if (isAllDay) {
			// allday이면 시간대 뷰를 숨긴다
			binding.eventDatetimeView.eventTimezoneLayout.setVisibility(View.GONE);
			binding.eventDatetimeView.startTime.setVisibility(View.GONE);
			binding.eventDatetimeView.endTime.setVisibility(View.GONE);

			binding.eventDatetimeView.eventTimezoneLayout.setVisibility(View.GONE);
			int startDay = instanceValues.getAsInteger(CalendarContract.Instances.START_DAY);
			int endDay = instanceValues.getAsInteger(CalendarContract.Instances.END_DAY);
			int dayDifference = endDay - startDay;

			if (startDay == endDay) {
				binding.eventDatetimeView.eventStartdatetimeLabel.setText(R.string.date);
				binding.eventDatetimeView.enddatetimeLayout.setVisibility(View.GONE);

				binding.eventDatetimeView.startDate.setText(EventUtil.convertDate(instanceValues.getAsLong(CalendarContract.Instances.BEGIN)));
			} else {
				Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
				calendar.setTimeInMillis(instanceValues.getAsLong(CalendarContract.Instances.BEGIN));
				binding.eventDatetimeView.startDate.setText(EventUtil.convertDate(calendar.getTime().getTime()));
				calendar.add(Calendar.DAY_OF_YEAR, dayDifference);
				binding.eventDatetimeView.endDate.setText(EventUtil.convertDate(calendar.getTime().getTime()));
			}
		} else {
			String timeZoneStr = instanceValues.getAsString(CalendarContract.Instances.EVENT_TIMEZONE);
			TimeZone timeZone = TimeZone.getTimeZone(timeZoneStr);
			setTimeZoneText(timeZone);

			Date beginDate = new Date(instanceValues.getAsLong(CalendarContract.Instances.BEGIN));
			Date endDate = new Date(instanceValues.getAsLong(CalendarContract.Instances.END));

			binding.eventDatetimeView.startDate.setText(EventUtil.convertDate(beginDate.getTime()));
			binding.eventDatetimeView.startTime.setText(EventUtil.convertTime(beginDate.getTime(), App.isPreference_key_using_24_hour_system()));
			binding.eventDatetimeView.endDate.setText(EventUtil.convertDate(endDate.getTime()));
			binding.eventDatetimeView.endTime.setText(EventUtil.convertTime(endDate.getTime(), App.isPreference_key_using_24_hour_system()));
		}

		// 반복
		if (instanceValues.getAsString(CalendarContract.Instances.RRULE) != null) {
			setRecurrenceText(instanceValues.getAsString(CalendarContract.Instances.RRULE));
			binding.eventRecurrenceView.getRoot().setVisibility(View.VISIBLE);
		} else {
			binding.eventRecurrenceView.getRoot().setVisibility(View.GONE);
		}

		// 알람
		if (instanceValues.getAsBoolean(CalendarContract.Instances.HAS_ALARM)) {
			List<ContentValues> reminderList = calendarViewModel.getReminders(eventId);
			setReminderText(reminderList);
			binding.eventRemindersView.notReminder.setVisibility(View.GONE);
			binding.eventRemindersView.remindersTable.setVisibility(View.VISIBLE);
			binding.eventRemindersView.getRoot().setVisibility(View.VISIBLE);
		} else {
			// 알람이 없으면 알람 테이블을 숨기고, 알람 없음 텍스트를 표시한다.
			binding.eventRemindersView.notReminder.setVisibility(View.VISIBLE);
			binding.eventRemindersView.remindersTable.setVisibility(View.GONE);
			binding.eventRemindersView.getRoot().setVisibility(View.GONE);
		}

		// 설명
		binding.eventDescriptionView.descriptionEdittext.setVisibility(View.GONE);
		if (instanceValues.getAsString(CalendarContract.Instances.DESCRIPTION) != null) {
			if (!instanceValues.getAsString(CalendarContract.Instances.DESCRIPTION).isEmpty()) {
				binding.eventDescriptionView.notDescription.setText(instanceValues.getAsString(CalendarContract.Instances.DESCRIPTION));
				binding.eventDescriptionView.getRoot().setVisibility(View.VISIBLE);
			} else {
				binding.eventDescriptionView.getRoot().setVisibility(View.GONE);
			}
		} else {
			binding.eventDescriptionView.getRoot().setVisibility(View.GONE);
		}

		// 위치
		if (instanceValues.getAsString(CalendarContract.Instances.EVENT_LOCATION) != null) {
			if (!instanceValues.getAsString(CalendarContract.Instances.EVENT_LOCATION).isEmpty()) {
				binding.eventLocationView.eventLocation.setText(instanceValues.getAsString(CalendarContract.Instances.EVENT_LOCATION));
				binding.eventLocationView.getRoot().setVisibility(View.VISIBLE);
				binding.selectDetailLocationFab.setVisibility(View.VISIBLE);
			} else {
				binding.eventLocationView.getRoot().setVisibility(View.GONE);
				binding.selectDetailLocationFab.setVisibility(View.GONE);
			}
		} else {
			binding.eventLocationView.getRoot().setVisibility(View.GONE);
			binding.selectDetailLocationFab.setVisibility(View.GONE);
		}

		// 참석자
		attendeeList = calendarViewModel.getAttendees(eventId);

		isOrganizer = instanceValues.containsKey(Events.IS_ORGANIZER) ?
				(instanceValues.getAsString(Events.IS_ORGANIZER).equals("1") ? true : false) : false;

		// 참석자가 없는 경우 - 테이블 숨김, 참석자 없음 텍스트 표시
		if (attendeeList.isEmpty()) {
			binding.modifyEventFab.setVisibility(View.VISIBLE);
			binding.eventAttendeesView.notAttendees.setVisibility(View.VISIBLE);
			binding.eventAttendeesView.eventAttendeesTable.setVisibility(View.GONE);
			binding.eventAttendeesView.answerLayout.setVisibility(View.GONE);
			binding.eventAttendeesView.getRoot().setVisibility(View.GONE);
		} else {
			final boolean guestsCanModify = instanceValues.getAsInteger(Events.GUESTS_CAN_MODIFY) == 1;
			final boolean guestsCanSeeGuests = instanceValues.getAsInteger(Events.GUESTS_CAN_SEE_GUESTS) == 1;
			final boolean guestsCanInviteOthers = instanceValues.getAsInteger(Events.GUESTS_CAN_INVITE_OTHERS) == 1;

			if (guestsCanInviteOthers) {

			} else {

			}

			binding.eventAttendeesView.notAttendees.setVisibility(View.GONE);
			binding.eventAttendeesView.eventAttendeesTable.setVisibility(View.VISIBLE);
			binding.eventAttendeesView.getRoot().setVisibility(View.VISIBLE);

			if (isOrganizer) {
				binding.eventAttendeesView.answerLayout.setVisibility(View.GONE);
			} else {
				binding.eventAttendeesView.answerLayout.setVisibility(View.VISIBLE);
				setAnswerForInviteSpinner();
			}

			if (binding.eventAttendeesView.eventAttendeesTable.getChildCount() > 0) {
				binding.eventAttendeesView.eventAttendeesTable.removeAllViews();
			}

			final String accountNameOfEvent = instanceValues.getAsString(Events.ACCOUNT_NAME);
			final String organizerEmail = attendeeList.get(0).getAsString(CalendarContract.Attendees.ORGANIZER);
			List<ContentValues> finalAttendeeList = new ArrayList<>();

			addAttendeeRow(organizerEmail, EventUtil.convertAttendeeRelationship(CalendarContract.Attendees.RELATIONSHIP_ORGANIZER, getContext()),
					"");

			for (ContentValues attendee : attendeeList) {
				if (attendee.getAsInteger(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP) ==
						CalendarContract.Attendees.RELATIONSHIP_ORGANIZER) {
					continue;
				}
				finalAttendeeList.add(attendee);
			}

			for (ContentValues attendee : finalAttendeeList) {
				// 이름, 메일 주소, 상태
				final String attendeeEmail = attendee.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL);
				final int attendeeStatus = attendee.getAsInteger(CalendarContract.Attendees.ATTENDEE_STATUS);
				final int attendeeRelationship = attendee.getAsInteger(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP);

				final String attendeeStatusStr = EventUtil.convertAttendeeStatus(attendeeStatus, getContext());
				final String attendeeRelationshipStr = EventUtil.convertAttendeeRelationship(attendeeRelationship, getContext());

				addAttendeeRow(attendeeEmail, attendeeRelationshipStr, attendeeStatusStr);
			}
		}

		// 공개 범위 표시
		if (instanceValues.getAsInteger(CalendarContract.Instances.ACCESS_LEVEL) != null) {
			setAccessLevelText();
			binding.eventAccessLevelView.getRoot().setVisibility(View.VISIBLE);
		} else {
			binding.eventAccessLevelView.getRoot().setVisibility(View.GONE);
		}

		// 유효성 표시
		if (instanceValues.getAsInteger(CalendarContract.Instances.AVAILABILITY) != null) {
			setAvailabilityText();
			binding.eventAvailabilityView.getRoot().setVisibility(View.VISIBLE);
		} else {
			binding.eventAvailabilityView.getRoot().setVisibility(View.GONE);
		}
	}

	private void setAvailabilityText() {
		binding.eventAvailabilityView.eventAvailability.setText(EventUtil.convertAvailability(instanceValues.getAsInteger(CalendarContract.Instances.AVAILABILITY), getContext()));
	}

	private void setAccessLevelText() {
		binding.eventAccessLevelView.eventAccessLevel.setText(EventUtil.convertAccessLevel(instanceValues.getAsInteger(CalendarContract.Instances.ACCESS_LEVEL), getContext()));
	}

	private void setTimeZoneText(TimeZone timeZone) {
		binding.eventDatetimeView.eventTimezone.setText(timeZone.getDisplayName(Locale.KOREAN));
	}

	private void setReminderText(List<ContentValues> reminders) {
		LayoutInflater layoutInflater = getLayoutInflater();
		if (binding.eventRemindersView.remindersTable.getChildCount() > 0) {
			binding.eventRemindersView.remindersTable.removeAllViews();
		}

		for (ContentValues reminder : reminders) {
			ReminderDto reminderDto = EventUtil.convertAlarmMinutes(reminder.getAsInteger(CalendarContract.Reminders.MINUTES));
			String alarmValueText = EventUtil.makeAlarmText(reminderDto, getContext());

			TableRow tableRow = new TableRow(getContext());
			View row = layoutInflater.inflate(R.layout.event_reminder_item, null);

			// 삭제 버튼 숨기기
			row.findViewById(R.id.remove_reminder_button).setVisibility(View.GONE);
			((TextView) row.findViewById(R.id.reminder_value)).setText(alarmValueText);
			row.findViewById(R.id.reminder_value).setClickable(false);

			tableRow.addView(row);
			binding.eventRemindersView.remindersTable.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
		}
	}

	private void setRecurrenceText(String rRule) {
		EventRecurrence eventRecurrence = new EventRecurrence();
		eventRecurrence.parse(rRule);
		binding.eventRecurrenceView.eventRecurrence.setText(eventRecurrence.toSimple());
	}

	private void setCalendarText() {
		binding.eventCalendarView.calendarColor.setBackgroundColor(EventUtil.getColor(instanceValues.getAsInteger(CalendarContract.Instances.CALENDAR_COLOR)));
		binding.eventCalendarView.calendarDisplayName.setText(instanceValues.getAsString(CalendarContract.Instances.CALENDAR_DISPLAY_NAME));
		binding.eventCalendarView.calendarAccountName.setText(instanceValues.getAsString(CalendarContract.Instances.OWNER_ACCOUNT));
	}

	public ContentValues getInstanceValues() {
		return instanceValues;
	}

	public List<ContentValues> getAttendeeList() {
		return attendeeList;
	}


	private void saveDetailLocation(LocationDTO locationDTO) {
		// 지정이 완료된 경우 - DB에 등록하고 이벤트 액티비티로 넘어가서 날씨 또는 주변 정보 프래그먼트를 실행한다.
		locationDTO.setEventId(eventId);

		//선택된 위치를 DB에 등록
		locationViewModel.addLocation(locationDTO, new DbQueryCallback<LocationDTO>() {
			@Override
			public void onResultSuccessful(LocationDTO result) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						iRefreshView.refreshView();
					}
				});

			}

			@Override
			public void onResultNoData() {

			}
		});
	}

	private void changedDetailLocation(LocationDTO locationDTO) {
		// 지정이 완료된 경우 - DB에 등록하고 이벤트 액티비티로 넘어가서 날씨 또는 주변 정보 프래그먼트를 실행한다.
		// 선택된 위치를 DB에 등록
		locationDTO.setEventId(eventId);
		locationViewModel.addLocation(locationDTO, new DbQueryCallback<LocationDTO>() {
			@Override
			public void onResultSuccessful(LocationDTO result) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						iRefreshView.refreshView();
					}
				});
			}

			@Override
			public void onResultNoData() {

			}
		});
	}

	private void deletedDetailLocation() {
		locationViewModel.removeLocation(eventId, new DbQueryCallback<Boolean>() {
			@Override
			public void onResultSuccessful(Boolean result) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						iRefreshView.refreshView();
					}
				});

			}

			@Override
			public void onResultNoData() {

			}
		});
	}

	public interface OnEventFragmentDismissListener {
		void onResult(long newEventId);
	}
}
