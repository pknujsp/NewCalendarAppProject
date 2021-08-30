package com.zerodsoft.scheduleweather.calendar;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;

import androidx.core.content.ContextCompat;

import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendar.interfaces.ICalendarProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CalendarProvider implements ICalendarProvider {
	private Context context;
	public static final int REQUEST_READ_CALENDAR = 200;
	public static final int REQUEST_WRITE_CALENDAR = 300;

	private final String[] EVENTS_PROJECTION =
			{
					CalendarContract.Events.TITLE,
					CalendarContract.Events.EVENT_COLOR_KEY,
					CalendarContract.Events.ACCOUNT_NAME,
					CalendarContract.Events.CALENDAR_ID,
					CalendarContract.Events.ORGANIZER,
					CalendarContract.Events.EVENT_END_TIMEZONE,
					CalendarContract.Events.EVENT_TIMEZONE,
					CalendarContract.Events.ACCOUNT_TYPE,
					CalendarContract.Events.DTSTART,
					CalendarContract.Events.DTEND,
					CalendarContract.Events.RRULE,
					CalendarContract.Events.RDATE,
					CalendarContract.Events.EXRULE,
					CalendarContract.Events.EXDATE,
					CalendarContract.Events.EVENT_LOCATION,
					CalendarContract.Events.AVAILABILITY,
					CalendarContract.Events.ACCESS_LEVEL,
					CalendarContract.Events.HAS_ATTENDEE_DATA
			};

	private final String[] CALENDAR_PROJECTION = {
			CalendarContract.Calendars._ID,
			CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
			CalendarContract.Calendars.ACCOUNT_NAME,
			CalendarContract.Calendars.OWNER_ACCOUNT,
			CalendarContract.Calendars.CALENDAR_COLOR,
			CalendarContract.Calendars.ACCOUNT_TYPE
	};

	private final String EVENTS_QUERY;
	private final String EVENT_QUERY;
	private final String CALENDARS_QUERY;

	public CalendarProvider(Context context) {
		this.context = context;
		StringBuilder stringBuilder = new StringBuilder();

		CALENDARS_QUERY = stringBuilder.append(CalendarContract.Calendars._ID).append("=?").toString();

		stringBuilder.delete(0, stringBuilder.length());

		EVENTS_QUERY = stringBuilder.append(CalendarContract.Events.CALENDAR_ID).append("=?").toString();

		stringBuilder.delete(0, stringBuilder.length());

		EVENT_QUERY = stringBuilder.append(CalendarContract.Events._ID).append("=?").toString();
	}

	@Override
	public void updateEventStatus(Long eventId, Integer newStatus) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
			ContentValues contentValues = new ContentValues();
			contentValues.put(CalendarContract.Events.STATUS, newStatus);

			context.getContentResolver().update(uri, contentValues, null, null);
		}
	}

	// account
	@Override
	public List<ContentValues> getGoogleAccounts() {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			final String[] PROJECTION = {
					CalendarContract.Calendars.ACCOUNT_NAME, CalendarContract.Calendars.OWNER_ACCOUNT, CalendarContract.Calendars.ACCOUNT_TYPE,
					CalendarContract.Calendars.IS_PRIMARY};
			ContentResolver contentResolver = context.getContentResolver();

			Cursor cursor = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, PROJECTION, null, null, null);

			final String GOOGLE_SECONDARY_CALENDAR = "@group.calendar.google.com";
			List<ContentValues> accountList = new ArrayList<>();
			Set<String> ownerAccountSet = new HashSet<>();

			if (cursor != null) {
				while (cursor.moveToNext()) {
					if (cursor.getInt(3) == 1) {
						// another || google primary calendar
						if (!ownerAccountSet.contains(cursor.getString(1))) {
							ownerAccountSet.add(cursor.getString(1));
							ContentValues accountValues = new ContentValues();

							accountValues.put(CalendarContract.Calendars.ACCOUNT_NAME, cursor.getString(0));
							accountValues.put(CalendarContract.Calendars.OWNER_ACCOUNT, cursor.getString(1));
							accountValues.put(CalendarContract.Calendars.ACCOUNT_TYPE, cursor.getString(2));

							accountList.add(accountValues);
						}
					} else if (cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.OWNER_ACCOUNT)).contains(GOOGLE_SECONDARY_CALENDAR)) {
						if (!ownerAccountSet.contains(cursor.getString(1))) {
							ownerAccountSet.add(cursor.getString(1));
							ContentValues accountValues = new ContentValues();

							accountValues.put(CalendarContract.Calendars.ACCOUNT_NAME, cursor.getString(0));
							accountValues.put(CalendarContract.Calendars.OWNER_ACCOUNT, cursor.getString(1));
							accountValues.put(CalendarContract.Calendars.ACCOUNT_TYPE, cursor.getString(2));

							accountList.add(accountValues);
						}
					}
				}
				cursor.close();
			}
			return accountList;
		} else {
			return new ArrayList<>();
		}
	}


	// event - crud

	/**
	 * 하나의 이벤트에 대한 구체적인 정보를 가져온다.
	 *
	 * @return
	 */
	@Override
	public ContentValues getEvent(Long eventId) {
		// 화면에 이벤트 정보를 표시하기 위해 기본적인 데이터만 가져온다.
		// 요청 매개변수 : ID, 캘린더 ID, 오너 계정, 조직자
		// 표시할 데이터 : 제목, 일정 기간, 반복, 위치, 알림, 설명, 소유 계정, 참석자, 바쁨/한가함, 공개 범위 참석 여부 확인 창, 색상
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			String[] selectionArgs = {eventId.toString()};

			Cursor cursor = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, null, EVENT_QUERY, selectionArgs, null);
			ContentValues event = new ContentValues();

			if (cursor != null) {
				while (cursor.moveToNext()) {
					String[] keys = cursor.getColumnNames();
					for (String key : keys) {
						event.put(key, cursor.getString(cursor.getColumnIndex(key)));
					}
				}
				cursor.close();
			}
			return event;
		} else {
			return new ContentValues();
		}
	}

	/**
	 * 특정 캘린더의 모든 이벤트를 가져온다.
	 *
	 * @param calendarId
	 * @return
	 */
	@Override
	public List<ContentValues> getEvents(Integer calendarId) {
		// 필요한 데이터 : 제목, 색상, 오너 관련, 일정 길이, 반복 관련
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			String[] selectionArgs = {calendarId.toString()};

			ContentResolver contentResolver = context.getContentResolver();
			Cursor cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI, EVENTS_PROJECTION, EVENTS_QUERY, selectionArgs, null);
			List<ContentValues> eventList = new ArrayList<>();

			if (cursor != null) {
				while (cursor.moveToNext()) {
					ContentValues event = new ContentValues();
					eventList.add(event);

					event.put(CalendarContract.Events.TITLE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE)));
					event.put(CalendarContract.Events.CALENDAR_ID, cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.CALENDAR_ID)));
					event.put(CalendarContract.Events._ID, cursor.getLong(cursor.getColumnIndex(CalendarContract.Events._ID)));
					event.put(CalendarContract.Events.DTSTART, cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTSTART)));
					event.put(CalendarContract.Events.DTEND, cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTEND)));
					event.put(CalendarContract.Events.ALL_DAY, cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.ALL_DAY)));
					event.put(CalendarContract.Events.EVENT_TIMEZONE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_TIMEZONE)));
					event.put(CalendarContract.Events.EVENT_END_TIMEZONE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_END_TIMEZONE)));
					event.put(CalendarContract.Events.CALENDAR_TIME_ZONE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.CALENDAR_TIME_ZONE)));
					event.put(CalendarContract.Events.RDATE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.RDATE)));
					event.put(CalendarContract.Events.RRULE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.RRULE)));
					event.put(CalendarContract.Events.EXDATE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EXDATE)));
					event.put(CalendarContract.Events.EXRULE, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EXRULE)));
					event.put(CalendarContract.Events.HAS_ATTENDEE_DATA, cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.HAS_ATTENDEE_DATA)));
					event.put(CalendarContract.Events.EVENT_LOCATION, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION)));
					event.put(CalendarContract.Events.DESCRIPTION, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION)));
					event.put(CalendarContract.Events.ACCESS_LEVEL, cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.ACCESS_LEVEL)));
					event.put(CalendarContract.Events.AVAILABILITY, cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.AVAILABILITY)));
					event.put(CalendarContract.Events.HAS_ALARM, cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.HAS_ALARM)));
					event.put(CalendarContract.Events.ACCOUNT_NAME, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.ACCOUNT_NAME)));
					event.put(CalendarContract.Events.CALENDAR_COLOR, cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.CALENDAR_COLOR)));
					event.put(CalendarContract.Events.CALENDAR_DISPLAY_NAME, cursor.getString(cursor.getColumnIndex(CalendarContract.Events.CALENDAR_DISPLAY_NAME)));
					event.put(CalendarContract.Events.EVENT_COLOR, cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.EVENT_COLOR)));
				}
				cursor.close();
			}
			return eventList;
		} else {
			return new ArrayList<>();
		}

	}

	/**
	 * 이벤트를 추가한다.
	 *
	 * @return
	 */
	@Override
	public long addEvent(ContentValues event) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			Uri uri = context.getContentResolver().insert(CalendarContract.Events.CONTENT_URI, event);
			return Long.parseLong(uri.getLastPathSegment());
		} else {
			return -1L;
		}
	}

	/**
	 * 이벤트를 제거한다.
	 *
	 * @return
	 */
	@Override
	public int deleteEvent(Long eventId) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
			return context.getContentResolver().delete(uri, null, null);
		} else {
			return -1;
		}
	}

	/**
	 * 이벤트들을 삭제한다.
	 *
	 * @return
	 */
	@Override
	public int deleteEvents(Long[] eventIds) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			ContentResolver contentResolver = context.getContentResolver();
			int deletedRows = 0;

			for (Long eventId : eventIds) {
				Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
				deletedRows += contentResolver.delete(uri, null, null);
			}
			return deletedRows;
		} else {
			return -1;
		}
	}

	/**
	 * 이벤트를 갱신한다.
	 *
	 * @return
	 */
	@Override
	public int updateEvent(ContentValues event) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.getAsLong(CalendarContract.Events._ID));
			return context.getContentResolver().update(uri, event, null, null);
		} else {
			return -1;
		}
	}

	// calendar - select

	/**
	 * 기기내의 모든 캘린더를 가져온다.
	 *
	 * @return
	 */
	@Override
	public List<ContentValues> getAllCalendars() {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			ContentResolver contentResolver = context.getContentResolver();
			Cursor cursor = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, CALENDAR_PROJECTION, null, null, null);
			List<ContentValues> calendarList = new ArrayList<>();
        /*
        필요한 데이터 : 달력 색상, 달력 이름, 소유자 계정, 계정 이름, 계정 타입, ID
         */
			if (cursor != null) {
				while (cursor.moveToNext()) {
					ContentValues calendar = new ContentValues();
					calendarList.add(calendar);

					calendar.put(CalendarContract.Calendars._ID, cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID)));
					calendar.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)));
					calendar.put(CalendarContract.Calendars.ACCOUNT_NAME, cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME)));
					calendar.put(CalendarContract.Calendars.OWNER_ACCOUNT, cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.OWNER_ACCOUNT)));
					calendar.put(CalendarContract.Calendars.CALENDAR_COLOR, cursor.getInt(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_COLOR)));
					calendar.put(CalendarContract.Calendars.ACCOUNT_TYPE, cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_TYPE)));
				}
				cursor.close();
			}
			return calendarList;
		} else {
			return new ArrayList<>();
		}
	}

	/**
	 * 공휴일, 생일 등을 제외한 주요 캘린더 모두를 가져온다.
	 *
	 * @return
	 */
	@Override
	public List<ContentValues> getCalendars() {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			Cursor cursor = context.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, null, null, null, null);

			final String GOOGLE_SECONDARY_CALENDAR = "@group.calendar.google.com";
			List<ContentValues> calendarList = new ArrayList<>();

			if (cursor != null) {
				while (cursor.moveToNext()) {
					ContentValues calendar = new ContentValues();
					String[] keys = cursor.getColumnNames();

					for (String key : keys) {
						if (!cursor.isNull(cursor.getColumnIndex(key))) {
							calendar.put(key, cursor.getString(cursor.getColumnIndex(key)));
						}
					}
					if (calendar.getAsInteger(CalendarContract.Calendars.IS_PRIMARY) == 1) {
						// another || google primary calendar
						calendarList.add(calendar);
					} else if (calendar.getAsString(CalendarContract.Calendars.OWNER_ACCOUNT)
							.contains(GOOGLE_SECONDARY_CALENDAR)) {
						calendarList.add(calendar);
						break;
					}
				}
				cursor.close();
			}
			return calendarList;
		} else {
			return new ArrayList<>();
		}
	}

	/**
	 * 하나의 캘린더에 대한 정보를 가져온다.
	 *
	 * @param calendarId
	 * @return
	 */
	@Override
	public ContentValues getCalendar(Integer calendarId) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			ContentResolver contentResolver = context.getContentResolver();
			String[] selectionArgs = {calendarId.toString()};
			Cursor cursor = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, null, CALENDARS_QUERY, selectionArgs, null);

			ContentValues calendar = null;
			if (cursor != null) {
				while (cursor.moveToNext()) {
					calendar = new ContentValues();

					calendar.put(CalendarContract.Calendars._ID, cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID)));
					calendar.put(CalendarContract.Calendars.NAME, cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.NAME)));
					calendar.put(CalendarContract.Calendars.ACCOUNT_NAME, cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME)));
					calendar.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
							cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)));
					calendar.put(CalendarContract.Calendars.OWNER_ACCOUNT, cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.OWNER_ACCOUNT)));
					calendar.put(CalendarContract.Calendars.CALENDAR_COLOR, cursor.getInt(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_COLOR)));
				}
				cursor.close();
			}
			return calendar;
		} else {
			return new ContentValues();
		}
	}

	// reminder - crud

	/**
	 * 하나의 이벤트에 대한 알림 모두를 가져온다.
	 *
	 * @return
	 */
	@Override
	public List<ContentValues> getReminders(Long eventId) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			ContentResolver contentResolver = context.getContentResolver();
			Cursor cursor = CalendarContract.Reminders.query(contentResolver, eventId, null);
			List<ContentValues> reminders = new ArrayList<>();

			if (cursor != null) {
				while (cursor.moveToNext()) {
					ContentValues reminder = new ContentValues();
					reminders.add(reminder);

					reminder.put(CalendarContract.Reminders.EVENT_ID, cursor.getLong(cursor.getColumnIndex(CalendarContract.Reminders.EVENT_ID)));
					reminder.put(CalendarContract.Reminders.METHOD, cursor.getInt(cursor.getColumnIndex(CalendarContract.Reminders.METHOD)));
					reminder.put(CalendarContract.Reminders.MINUTES, cursor.getInt(cursor.getColumnIndex(CalendarContract.Reminders.MINUTES)));
				}
				cursor.close();
			}
			return reminders;
		} else {
			return new ArrayList<>();
		}
	}

	/**
	 * 알림값을 갱신한다.
	 *
	 * @return
	 */
	@Override
	public int updateReminder(ContentValues reminder) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			final String where = CalendarContract.Reminders._ID + "=? AND " +
					CalendarContract.Reminders.EVENT_ID + "=? AND " +
					CalendarContract.Reminders.CALENDAR_ID + "=?";

			String[] selectionArgs = {
					reminder.getAsLong(CalendarContract.Reminders._ID).toString()
					, reminder.getAsLong(CalendarContract.Reminders.EVENT_ID).toString()
					, reminder.getAsInteger(CalendarContract.Reminders.CALENDAR_ID).toString()};

			ContentResolver contentResolver = context.getContentResolver();
			int updatedRows = 0;
			updatedRows += contentResolver.update(CalendarContract.Reminders.CONTENT_URI, reminder, where, selectionArgs);

			return updatedRows;
		} else {
			return -1;
		}
	}

	/**
	 * 알림을 삭제한다
	 */
	@Override
	public int deleteReminders(Long eventId, Long[] reminderIds) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			String where = CalendarContract.Reminders.EVENT_ID + "=? AND "
					+ CalendarContract.Reminders._ID + "=?";
			String[] selectionArgs = new String[2];
			selectionArgs[0] = eventId.toString();

			ContentResolver contentResolver = context.getContentResolver();
			int deletedRows = 0;

			for (Long reminderId : reminderIds) {
				selectionArgs[1] = String.valueOf(reminderId);
				deletedRows += contentResolver.delete(CalendarContract.Reminders.CONTENT_URI, where, selectionArgs);
			}
			return deletedRows;
		} else {
			return -1;
		}
	}

	@Override
	public int deleteAllReminders(Long eventId) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			String where = CalendarContract.Reminders.EVENT_ID + "=?";
			String[] selectionArgs = {eventId.toString()};
			return context.getContentResolver().delete(CalendarContract.Reminders.CONTENT_URI, where, selectionArgs);
		} else {
			return -1;
		}
	}

	/**
	 * 알림을 추가한다.
	 *
	 * @return
	 */
	@Override
	public int addReminders(List<ContentValues> reminders) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			ContentResolver contentResolver = context.getContentResolver();
			int addedRows = 0;

			for (ContentValues reminder : reminders) {
				contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminder);
				addedRows++;
			}
			return addedRows;
		} else {
			return -1;
		}
	}

	// instance - select

	/**
	 * 각각의 캘린더들 내에 특정한 기간 사이에 있는 인스턴스들을 가져온다.
	 *
	 * @param begin
	 * @param end
	 * @return
	 */
	@Override
	public Map<Integer, CalendarInstance> getInstances(Long begin, Long end) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			Cursor cursor = CalendarContract.Instances.query(context.getContentResolver(), null, begin, end);
			Map<Integer, CalendarInstance> calendarInstanceMap = new HashMap<>();
			int calendarId = 0;

			while (cursor.moveToNext()) {
				ContentValues instance = new ContentValues();
				String[] keys = cursor.getColumnNames();

				for (String key : keys) {
					if (!cursor.isNull(cursor.getColumnIndex(key))) {
						instance.put(key, cursor.getString(cursor.getColumnIndex(key)));
					}
				}

				if (instance.getAsLong(CalendarContract.Instances.BEGIN).equals(end)) {
					continue;
				}

				calendarId = cursor.getInt(cursor.getColumnIndex(CalendarContract.Instances.CALENDAR_ID));
				if (!calendarInstanceMap.containsKey(calendarId)) {
					calendarInstanceMap.put(calendarId, new CalendarInstance(new ArrayList<>(), calendarId));
				}
				calendarInstanceMap.get(calendarId).getInstanceList().add(instance);
			}
			cursor.close();
			return calendarInstanceMap;
		} else {
			return new HashMap<>();
		}
	}

	@Override
	public ContentValues getInstance(Long instanceId, Long begin, Long end) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			final String selection = "Instances._id = ?";
			final String[] selectionArgs = {instanceId.toString()};

			Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
			ContentUris.appendId(builder, begin);
			ContentUris.appendId(builder, end);

			Cursor cursor = context.getContentResolver().query(builder.build(), null, selection, selectionArgs, null);
			ContentValues instance = new ContentValues();

			while (cursor.moveToNext()) {
				String[] keys = cursor.getColumnNames();
				for (String key : keys) {
					if (!cursor.isNull(cursor.getColumnIndex(key))) {
						instance.put(key, cursor.getString(cursor.getColumnIndex(key)));
					}
				}

			}
			cursor.close();
			return instance;
		} else {
			return new ContentValues();
		}
	}


	/**
	 * 이번 일정을 포함한 이후의 모든 일정을 변경
	 * 기존 이벤트 반복 규칙에 UNTIL 추가
	 *
	 * @return
	 */
	@Override
	public long updateAllFutureInstances(ContentValues modifiedInstance, ContentValues previousInstance) {
		return 0;
	}

	@Override
	public int updateOneInstance(ContentValues modifiedInstance, ContentValues previousInstance) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			return 1;
		} else {
			return -1;
		}
	}

	/**
	 * Find the instance you want to delete. (using Instances.query())
	 * Create the exception URI with the event ID appended.
	 * Create ContentValues. Put your instance's BEGIN value as ...Events.ORIGINAL_INSTANCE_TIME. Put STATUS_CANCELED as ...Events.STATUS
	 * Now only insert(yourURI, yourValues) and that's it!
	 *
	 * @param begin
	 * @param eventId
	 * @return
	 */
	@Override
	public int deleteInstance(Long begin, Long eventId) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			ContentValues exceptionEvent = new ContentValues();
			exceptionEvent.put(CalendarContract.Events.ORIGINAL_INSTANCE_TIME, begin);
			exceptionEvent.put(CalendarContract.Events.STATUS, CalendarContract.Events.STATUS_CANCELED);

			Uri exceptionUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_EXCEPTION_URI, eventId);
			ContentResolver contentResolver = context.getContentResolver();
			Uri result = contentResolver.insert(exceptionUri, exceptionEvent);

			return Integer.parseInt(result.getLastPathSegment());
		} else {
			return -1;
		}
	}

	// attendee - crud

	/**
	 * 참석자들을 추가한다.
	 *
	 * @return
	 */
	@Override
	public int addAttendees(List<ContentValues> attendeeList) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			ContentResolver contentResolver = context.getContentResolver();
			int addedRows = 0;
			for (ContentValues attendee : attendeeList) {
				contentResolver.insert(CalendarContract.Attendees.CONTENT_URI, attendee);
				addedRows++;
			}
			return addedRows;
		} else {
			return -1;
		}
	}

	/**
	 * 하나의 이벤트에 대한 참석자 정보들을 가져온다.
	 *
	 * @return
	 */
	@Override
	public List<ContentValues> getAttendees(Long eventId) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			ContentResolver contentResolver = context.getContentResolver();

			List<ContentValues> attendees = new ArrayList<>();
			Cursor cursor = CalendarContract.Attendees.query(contentResolver, eventId, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					ContentValues attendee = new ContentValues();
					attendees.add(attendee);

					attendee.put(CalendarContract.Attendees._ID, cursor.getLong(cursor.getColumnIndex(CalendarContract.Attendees._ID)));
					attendee.put(CalendarContract.Attendees.EVENT_ID, cursor.getLong(cursor.getColumnIndex(CalendarContract.Attendees.EVENT_ID)));
					attendee.put(CalendarContract.Attendees.CALENDAR_ID, cursor.getInt(cursor.getColumnIndex(CalendarContract.Attendees.CALENDAR_ID)));
					attendee.put(CalendarContract.Attendees.ATTENDEE_EMAIL, cursor.getString(cursor.getColumnIndex(CalendarContract.Attendees.ATTENDEE_EMAIL)));
					attendee.put(CalendarContract.Attendees.ATTENDEE_ID_NAMESPACE, cursor.getString(cursor.getColumnIndex(CalendarContract.Attendees.ATTENDEE_ID_NAMESPACE)));
					attendee.put(CalendarContract.Attendees.ATTENDEE_IDENTITY, cursor.getString(cursor.getColumnIndex(CalendarContract.Attendees.ATTENDEE_IDENTITY)));
					attendee.put(CalendarContract.Attendees.ATTENDEE_NAME, cursor.getString(cursor.getColumnIndex(CalendarContract.Attendees.ATTENDEE_NAME)));
					attendee.put(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP, cursor.getInt(cursor.getColumnIndex(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP)));
					attendee.put(CalendarContract.Attendees.ATTENDEE_STATUS, cursor.getInt(cursor.getColumnIndex(CalendarContract.Attendees.ATTENDEE_STATUS)));
					attendee.put(CalendarContract.Attendees.ATTENDEE_TYPE, cursor.getInt(cursor.getColumnIndex(CalendarContract.Attendees.ATTENDEE_TYPE)));
					attendee.put(CalendarContract.Attendees.IS_ORGANIZER, cursor.getString(cursor.getColumnIndex(CalendarContract.Attendees.IS_ORGANIZER)));
					attendee.put(CalendarContract.Attendees.ORGANIZER, cursor.getString(cursor.getColumnIndex(CalendarContract.Attendees.ORGANIZER)));
				}
				cursor.close();
			}
			return attendees;
		} else {
			return new ArrayList<>();
		}
	}

	@Override
	public List<ContentValues> getAttendeeListForEdit(Long eventId) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			ContentResolver contentResolver = context.getContentResolver();

			List<ContentValues> attendees = new ArrayList<>();
			Cursor cursor = CalendarContract.Attendees.query(contentResolver, eventId, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					ContentValues attendee = new ContentValues();
					attendees.add(attendee);

					attendee.put(CalendarContract.Attendees.ATTENDEE_NAME, cursor.getString(cursor.getColumnIndex(CalendarContract.Attendees.ATTENDEE_NAME)));
					attendee.put(CalendarContract.Attendees.EVENT_ID, cursor.getLong(cursor.getColumnIndex(CalendarContract.Attendees.EVENT_ID)));
					attendee.put(CalendarContract.Attendees.ATTENDEE_EMAIL, cursor.getString(cursor.getColumnIndex(CalendarContract.Attendees.ATTENDEE_EMAIL)));
					attendee.put(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP, cursor.getInt(cursor.getColumnIndex(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP)));
					attendee.put(CalendarContract.Attendees.ATTENDEE_STATUS, cursor.getInt(cursor.getColumnIndex(CalendarContract.Attendees.ATTENDEE_STATUS)));
					attendee.put(CalendarContract.Attendees.ATTENDEE_TYPE, cursor.getInt(cursor.getColumnIndex(CalendarContract.Attendees.ATTENDEE_TYPE)));
				}
				cursor.close();
			}
			return attendees;
		} else {
			return new ArrayList<>();
		}
	}

	/**
	 * 참석자 정보를 갱신한다.
	 *
	 * @return
	 */
	@Override
	public int updateAttendees(List<ContentValues> attendeeList) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			ContentResolver contentResolver = context.getContentResolver();
			int updatedRows = 0;
			final int eventId = attendeeList.get(0).getAsInteger(CalendarContract.Attendees.EVENT_ID);
			final int calendarId = attendeeList.get(0).getAsInteger(CalendarContract.Attendees.CALENDAR_ID);

			final String where = CalendarContract.Attendees._ID + "=? AND " +
					CalendarContract.Attendees.EVENT_ID + "=? AND " +
					CalendarContract.Attendees.CALENDAR_ID + "=?";

			String[] selectionArgs = new String[3];
			selectionArgs[1] = String.valueOf(eventId);
			selectionArgs[2] = String.valueOf(calendarId);

			for (ContentValues attendee : attendeeList) {
				selectionArgs[0] = attendee.getAsLong(CalendarContract.Attendees._ID).toString();
				updatedRows += contentResolver.update(CalendarContract.Attendees.CONTENT_URI, attendee, where, selectionArgs);
			}
			return updatedRows;
		} else {
			return -1;
		}
	}

	/**
	 * 참석자 정보를 모두 삭제한다.
	 *
	 * @return
	 */
	@Override
	public int deleteAllAttendees(Long eventId) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			ContentResolver contentResolver = context.getContentResolver();
			String where = CalendarContract.Attendees.EVENT_ID + "=?";
			String[] selectionArgs = {eventId.toString()};

			return contentResolver.delete(CalendarContract.Attendees.CONTENT_URI, where, selectionArgs);
		} else {
			return -1;
		}
	}

	/**
	 * 특정 참석자 정보를 삭제한다.
	 *
	 * @return
	 */
	@Override
	public int deleteAttendees(Long eventId, Long[] attendeeIds) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			final String where =
					CalendarContract.Attendees.EVENT_ID + "=? AND " + "Attendees._id=?";

			String[] selectionArgs = new String[2];
			selectionArgs[0] = eventId.toString();

			ContentResolver contentResolver = context.getContentResolver();
			int updatedRows = 0;

			for (long attendeeId : attendeeIds) {
				selectionArgs[1] = String.valueOf(attendeeId);
				updatedRows += contentResolver.delete(CalendarContract.Attendees.CONTENT_URI, where, selectionArgs);
			}
			return updatedRows;
		} else {
			return -1;
		}
	}

	public void syncCalendars() {
		AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
		final Account[] accounts = accountManager.getAccountsByType("com.google");

		for (Account account : accounts) {
			Bundle extras = new Bundle();
			extras.putBoolean(
					ContentResolver.SYNC_EXTRAS_MANUAL, true);
			extras.putBoolean(
					ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
			ContentResolver.requestSync(account, CalendarContract.AUTHORITY, extras);
		}
	}

	@Override
	public ContentValues getRecurrence(Long eventId) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			ContentResolver contentResolver = context.getContentResolver();

			final String[] projection = {CalendarContract.Events._ID, CalendarContract.Events.CALENDAR_ID,
					CalendarContract.Events.RRULE, CalendarContract.Events.RDATE, CalendarContract.Events.EXDATE,
					CalendarContract.Events.EXRULE};
			final String[] selectionArgs = {eventId.toString()};

			Cursor cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI, projection, EVENT_QUERY, selectionArgs, null);
			ContentValues result = new ContentValues();

			while (cursor.moveToNext()) {
				result.put(CalendarContract.Events._ID, cursor.getLong(0));
				result.put(CalendarContract.Events.CALENDAR_ID, cursor.getInt(1));
				result.put(CalendarContract.Events.RRULE, cursor.getString(2));
				result.put(CalendarContract.Events.RDATE, cursor.getString(3));
				result.put(CalendarContract.Events.EXDATE, cursor.getString(4));
				result.put(CalendarContract.Events.EXRULE, cursor.getString(5));
			}
			cursor.close();
			return result;
		} else {
			return new ContentValues();
		}
	}

	@Override
	public int getCalendarColor(String accountName, String accountType) {
		return -1;
	}

	@Override
	public List<ContentValues> getCalendarColors(String accountName, String accountType) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			String[] projection = {CalendarContract.Colors.COLOR, CalendarContract.Colors.COLOR_KEY};
			String selection = CalendarContract.Colors.ACCOUNT_NAME + "=? AND " + CalendarContract.Colors.ACCOUNT_TYPE + "=? AND "
					+ CalendarContract.Colors.COLOR_TYPE + "=?";
			String[] selectionArgs = {accountName, accountType, String.valueOf(CalendarContract.Colors.TYPE_CALENDAR)};

			Cursor cursor = context.getContentResolver().query(CalendarContract.Colors.CONTENT_URI, projection, selection, selectionArgs, null);
			List<ContentValues> colors = new ArrayList<>();

			while (cursor.moveToNext()) {
				ContentValues color = new ContentValues();
				color.put(CalendarContract.Colors.COLOR, cursor.getInt(0));
				color.put(CalendarContract.Colors.COLOR_KEY, cursor.getString(1));

				colors.add(color);
			}
			cursor.close();
			return colors;
		} else {
			return new ArrayList<>();
		}
	}

	@Override
	public List<ContentValues> getEventColors(String accountName) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			String[] projection = {CalendarContract.Colors.COLOR, CalendarContract.Colors.COLOR_KEY};
			String selection = CalendarContract.Colors.ACCOUNT_NAME + "=? AND "
					+ CalendarContract.Colors.COLOR_TYPE + "=?";
			String[] selectionArgs = {accountName, String.valueOf(CalendarContract.Colors.TYPE_EVENT)};
			Cursor cursor = context.getContentResolver().query(CalendarContract.Colors.CONTENT_URI, projection, selection, selectionArgs, null);
			List<ContentValues> colors = new ArrayList<>();

			while (cursor.moveToNext()) {
				ContentValues color = new ContentValues();
				color.put(CalendarContract.Colors.COLOR, cursor.getInt(0));
				color.put(CalendarContract.Colors.COLOR_KEY, cursor.getString(1));

				colors.add(color);
			}
			cursor.close();
			return colors;
		} else {
			return new ArrayList<>();
		}
	}

	@Override
	public int updateCalendarColor(Integer calendarId, Integer color, String colorKey) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			ContentValues calendar = new ContentValues();
			calendar.put(CalendarContract.Calendars.CALENDAR_COLOR, color);
			calendar.put(CalendarContract.Calendars.CALENDAR_COLOR_KEY, colorKey);

			String where = CalendarContract.Calendars._ID + "=?";
			String[] selectionArgs = {calendarId.toString()};
			return context.getContentResolver().update(CalendarContract.Calendars.CONTENT_URI, calendar, where, selectionArgs);
		} else {
			return -1;
		}
	}

	@Override
	public ContentValues getCalendarColor(Integer calendarId) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			String[] projection = {CalendarContract.Calendars.CALENDAR_COLOR, CalendarContract.Calendars.CALENDAR_COLOR_KEY};
			String selection = CalendarContract.Calendars._ID + "=?";
			String[] selectionArgs = {calendarId.toString()};

			Cursor cursor = context.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, projection, selection, selectionArgs, null);
			ContentValues color = new ContentValues();

			while (cursor.moveToNext()) {
				color.put(CalendarContract.Calendars.CALENDAR_COLOR, cursor.getInt(0));
				color.put(CalendarContract.Calendars.CALENDAR_COLOR_KEY, cursor.getString(1));
			}
			cursor.close();
			return color;
		} else {
			return new ContentValues();
		}
	}

	@Override
	public ContentValues getValuesOfEvent(Long eventId, String... keys) {
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			String selection = CalendarContract.Events._ID + " = ?";
			String[] selectionArgs = {eventId.toString()};
			Cursor cursor = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, keys, selection, selectionArgs, null);
			ContentValues contentValues = new ContentValues();

			while (cursor.moveToNext()) {
				int index = 0;
				for (String key : keys) {
					contentValues.put(key, cursor.getString(index++));
				}
			}

			cursor.close();
			return contentValues;
		} else {
			return new ContentValues();
		}
	}
}
