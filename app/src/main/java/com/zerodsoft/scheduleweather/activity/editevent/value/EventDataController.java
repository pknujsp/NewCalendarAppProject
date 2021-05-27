package com.zerodsoft.scheduleweather.activity.editevent.value;

import android.content.ContentValues;
import android.content.Context;
import android.provider.CalendarContract;

import com.zerodsoft.scheduleweather.common.enums.EventIntentCode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventDataController {
	public final EventIntentCode REQUEST_CODE;

	private EventData newEventData;
	private EventData modifiedEventData;
	private EventData savedEventData;
	private EventDefaultValue eventDefaultValue;
	private ContentValues selectedCalendar = new ContentValues();

	public EventDataController(Context context, EventIntentCode requestCode) {
		this.REQUEST_CODE = requestCode;

		if (requestCode == EventIntentCode.REQUEST_NEW_EVENT) {
			this.newEventData = new EventData();
		} else if (requestCode == EventIntentCode.REQUEST_MODIFY_EVENT) {
			this.modifiedEventData = new EventData();
			this.savedEventData = new EventData();
		}
		this.eventDefaultValue = new EventDefaultValue(context);
	}

	public EventDefaultValue getEventDefaultValue() {
		return eventDefaultValue;
	}

	public EventData getNewEventData() {
		return newEventData;
	}

	public EventData getSavedEventData() {
		return savedEventData;
	}

	public EventData getModifiedEventData() {
		return modifiedEventData;
	}

	public ContentValues getSelectedCalendar() {
		return selectedCalendar;
	}


	public Integer getEventValueAsInt(String key) {
		if (REQUEST_CODE == EventIntentCode.REQUEST_NEW_EVENT) {
			return newEventData.getEVENT().getAsInteger(key);
		} else {
			return modifiedEventData.getEVENT().getAsInteger(key);
		}
	}

	public Long getEventValueAsLong(String key) {
		if (REQUEST_CODE == EventIntentCode.REQUEST_NEW_EVENT) {
			return newEventData.getEVENT().getAsLong(key);
		} else {
			return modifiedEventData.getEVENT().getAsLong(key);
		}
	}

	public String getEventValueAsString(String key) {
		if (REQUEST_CODE == EventIntentCode.REQUEST_NEW_EVENT) {
			return newEventData.getEVENT().getAsString(key);
		} else {
			return modifiedEventData.getEVENT().getAsString(key);
		}
	}

	public Boolean getEventValueAsBoolean(String key) {
		if (REQUEST_CODE == EventIntentCode.REQUEST_NEW_EVENT) {
			return newEventData.getEVENT().getAsBoolean(key);
		} else {
			return modifiedEventData.getEVENT().getAsBoolean(key);
		}
	}

	public List<ContentValues> getReminders() {
		if (REQUEST_CODE == EventIntentCode.REQUEST_NEW_EVENT) {
			return newEventData.getREMINDERS();
		} else {
			return modifiedEventData.getREMINDERS();
		}
	}

	public List<ContentValues> getAttendees() {
		if (REQUEST_CODE == EventIntentCode.REQUEST_NEW_EVENT) {
			return newEventData.getATTENDEES();
		} else {
			return modifiedEventData.getATTENDEES();
		}
	}

	public void putEventValue(String key, String value) {
		if (REQUEST_CODE == EventIntentCode.REQUEST_NEW_EVENT) {
			newEventData.getEVENT().put(key, value);
		} else if (REQUEST_CODE == EventIntentCode.REQUEST_MODIFY_EVENT) {
			modifiedEventData.getEVENT().put(key, value);

		}
	}

	public void putEventValue(String key, long value) {
		if (REQUEST_CODE == EventIntentCode.REQUEST_NEW_EVENT) {
			newEventData.getEVENT().put(key, value);
		} else if (REQUEST_CODE == EventIntentCode.REQUEST_MODIFY_EVENT) {
			modifiedEventData.getEVENT().put(key, value);
		}
	}

	public void putEventValue(String key, boolean value) {
		if (REQUEST_CODE == EventIntentCode.REQUEST_NEW_EVENT) {
			newEventData.getEVENT().put(key, value ? 1 : 0);
		} else if (REQUEST_CODE == EventIntentCode.REQUEST_MODIFY_EVENT) {
			modifiedEventData.getEVENT().put(key, value ? 1 : 0);
		}
	}

	public void putEventValue(String key, int value) {
		if (REQUEST_CODE == EventIntentCode.REQUEST_NEW_EVENT) {
			newEventData.getEVENT().put(key, value);
		} else if (REQUEST_CODE == EventIntentCode.REQUEST_MODIFY_EVENT) {
			modifiedEventData.getEVENT().put(key, value);
		}
	}

	public void putAttendees(List<ContentValues> attendees, boolean guestsCanModify, boolean guestsCanInviteOthers,
	                         boolean guestsCanSeeGuests) {
		//eventId는 이벤트 수정/등록 후 받은 id를 가지고 지정
		//calendarId는 수정/등록 시에 지정

		if (REQUEST_CODE == EventIntentCode.REQUEST_NEW_EVENT) {
			newEventData.getATTENDEES().clear();
			newEventData.getATTENDEES().addAll(attendees);
		} else if (REQUEST_CODE == EventIntentCode.REQUEST_MODIFY_EVENT) {
			modifiedEventData.getATTENDEES().clear();
			modifiedEventData.getATTENDEES().addAll(attendees);
		}

		putEventValue(CalendarContract.Events.GUESTS_CAN_MODIFY, guestsCanModify ? 1 : 0);
		putEventValue(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, guestsCanInviteOthers ? 1 : 0);
		putEventValue(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, guestsCanSeeGuests ? 1 : 0);

	}

	public void putReminders(List<ContentValues> reminders) {
		//eventId는 이벤트 수정/등록 후 받은 id를 가지고 지정
		//calendarId는 수정/등록 시에 지정

		if (REQUEST_CODE == EventIntentCode.REQUEST_NEW_EVENT) {
			newEventData.getREMINDERS().clear();
			newEventData.getREMINDERS().addAll(reminders);
		} else if (REQUEST_CODE == EventIntentCode.REQUEST_MODIFY_EVENT) {
			modifiedEventData.getREMINDERS().clear();
			modifiedEventData.getREMINDERS().addAll(reminders);
		}

		if (getEventValueAsInt(CalendarContract.Events.HAS_ALARM) == 0) {
			putEventValue(CalendarContract.Events.HAS_ALARM, 1);
		}
	}

	public void putReminder(ContentValues reminder) {
		if (REQUEST_CODE == EventIntentCode.REQUEST_NEW_EVENT) {
			newEventData.getREMINDERS().add(reminder);
		} else if (REQUEST_CODE == EventIntentCode.REQUEST_MODIFY_EVENT) {
			modifiedEventData.getREMINDERS().add(reminder);
		}

		putEventValue(CalendarContract.Events.HAS_ALARM, 1);
	}

	public void removeEventValue(String key) {
		if (REQUEST_CODE == EventIntentCode.REQUEST_NEW_EVENT) {
			if (newEventData.getEVENT().containsKey(key)) {
				newEventData.getEVENT().remove(key);
			}
		} else if (REQUEST_CODE == EventIntentCode.REQUEST_MODIFY_EVENT) {
			if (modifiedEventData.getEVENT().containsKey(key)) {
				modifiedEventData.getEVENT().remove(key);
			}
		}
	}

	public void removeReminder(int minutes) {
		List<ContentValues> reminders = new ArrayList<>();

		if (REQUEST_CODE == EventIntentCode.REQUEST_NEW_EVENT) {
			reminders = newEventData.getREMINDERS();
		} else if (REQUEST_CODE == EventIntentCode.REQUEST_MODIFY_EVENT) {
			reminders = modifiedEventData.getREMINDERS();
		}

		if (!reminders.isEmpty()) {
			for (int i = 0; i < reminders.size(); i++) {
				if (reminders.get(i).getAsInteger(CalendarContract.Reminders.MINUTES) == minutes) {
					reminders.remove(i);

					if (reminders.isEmpty()) {
						putEventValue(CalendarContract.Events.HAS_ALARM, 0);
					}
					break;
				}
			}

		}

	}

	public void modifyReminder(ContentValues reminder, int previousMinutes) {
		List<ContentValues> reminders = new ArrayList<>();

		if (REQUEST_CODE == EventIntentCode.REQUEST_NEW_EVENT) {
			reminders = newEventData.getREMINDERS();
		} else if (REQUEST_CODE == EventIntentCode.REQUEST_MODIFY_EVENT) {
			reminders = modifiedEventData.getREMINDERS();
		}

		for (int i = 0; i < reminders.size(); i++) {
			if (reminders.get(i).getAsInteger(CalendarContract.Reminders.MINUTES) == previousMinutes) {
				reminders.get(i).clear();
				reminders.get(i).putAll(reminder);
				break;
			}
		}

	}

	public void removeReminders() {
		List<ContentValues> reminders = new ArrayList<>();

		if (REQUEST_CODE == EventIntentCode.REQUEST_NEW_EVENT) {
			reminders = newEventData.getREMINDERS();
		} else if (REQUEST_CODE == EventIntentCode.REQUEST_MODIFY_EVENT) {
			reminders = modifiedEventData.getREMINDERS();
		}

		reminders.clear();
		putEventValue(CalendarContract.Events.HAS_ALARM, 0);
	}

	public void removeAttendee(String email) {
		List<ContentValues> attendees = new ArrayList<>();

		if (REQUEST_CODE == EventIntentCode.REQUEST_NEW_EVENT) {
			attendees = newEventData.getATTENDEES();
		} else if (REQUEST_CODE == EventIntentCode.REQUEST_MODIFY_EVENT) {
			attendees = modifiedEventData.getATTENDEES();
		}

		// 참석자수가 2명이면 모두 삭제(조직자, 참석자로 구성된 상태에서 참석자를 제거하기 때문)
		if (attendees.size() == 2) {
			attendees.clear();
			removeEventValue(CalendarContract.Events.GUESTS_CAN_MODIFY);
			removeEventValue(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS);
			removeEventValue(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS);
		} else if (attendees.size() >= 3) {
			int index = 0;
			for (ContentValues attendee : attendees) {
				if (attendee.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL).equals(email)) {
					attendees.remove(index);
					break;
				}
				index++;
			}
		}
	}

	public void removeAttendees() {
		List<ContentValues> attendees = new ArrayList<>();

		if (REQUEST_CODE == EventIntentCode.REQUEST_NEW_EVENT) {
			attendees = newEventData.getATTENDEES();
		} else if (REQUEST_CODE == EventIntentCode.REQUEST_MODIFY_EVENT) {
			attendees = modifiedEventData.getATTENDEES();
		}

		attendees.clear();
		removeEventValue(CalendarContract.Events.GUESTS_CAN_MODIFY);
		removeEventValue(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS);
		removeEventValue(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS);
	}

	public void setCalendarValue(ContentValues calendar) {
		putEventValue(CalendarContract.Events.CALENDAR_ID, calendar.getAsInteger(CalendarContract.Calendars._ID));

		selectedCalendar.clear();
		selectedCalendar.putAll(calendar);
	}
}
