package com.zerodsoft.scheduleweather.event.util;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.text.TextPaint;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.model.ReminderDto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class EventUtil {
	private EventUtil() {
	}

	public static int getColor(Integer color) {
		if (color == null) {
			return Color.LTGRAY;
		} else {
			int finalColor = color;

			int alpha = Color.alpha(finalColor);
			int red = Color.red(finalColor);
			int green = Color.green(finalColor);
			int blue = Color.blue(finalColor);

			return Color.argb(alpha, red, green, blue);
		}
	}

	public static Paint getEventColorPaint(ContentValues event) {
		Paint eventColorPaint = new Paint();
		eventColorPaint.setColor(event.size() > 0 ? getColor(event.getAsInteger(Events.EVENT_COLOR)) : Color.RED);
		return eventColorPaint;
	}

	public static TextPaint getEventTextPaint(float textSize) {
		TextPaint eventTextPaint = new TextPaint();
		eventTextPaint.setTextAlign(Paint.Align.LEFT);
		eventTextPaint.setTextSize(textSize);
		eventTextPaint.setAntiAlias(true);
		eventTextPaint.setColor(Color.WHITE);

		return eventTextPaint;
	}

	public static int[] getViewSideMargin(long instanceBegin, long instanceEnd, long viewBegin, long viewEnd, int margin, boolean allDay) {
		GregorianCalendar instanceBeginCalendar = new GregorianCalendar();
		GregorianCalendar instanceEndCalendar = new GregorianCalendar();
		GregorianCalendar viewBeginCalendar = new GregorianCalendar();
		GregorianCalendar viewEndCalendar = new GregorianCalendar();

		instanceBeginCalendar.setTimeInMillis(instanceBegin);
		instanceEndCalendar.setTimeInMillis(instanceEnd);
		viewBeginCalendar.setTimeInMillis(viewBegin);
		viewEndCalendar.setTimeInMillis(viewEnd);

		if (allDay) {
			instanceEndCalendar.add(Calendar.HOUR_OF_DAY, -9);
		}

		final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy/M/d H:m");

		final String instanceBeginStr = instanceBeginCalendar.get(Calendar.YEAR)
				+ "/" + (instanceBeginCalendar.get(Calendar.MONTH) + 1) + "/" +
				instanceBeginCalendar.get(Calendar.DAY_OF_MONTH) + " " +
				instanceBeginCalendar.get(Calendar.HOUR_OF_DAY) + ":" +
				instanceBeginCalendar.get(Calendar.MINUTE);

		final String instanceEndStr = instanceEndCalendar.get(Calendar.YEAR)
				+ "/" + (instanceEndCalendar.get(Calendar.MONTH) + 1) + "/" +
				instanceEndCalendar.get(Calendar.DAY_OF_MONTH) + " " +
				instanceEndCalendar.get(Calendar.HOUR_OF_DAY) + ":" +
				instanceEndCalendar.get(Calendar.MINUTE);

		final String viewBeginStr = viewBeginCalendar.get(Calendar.YEAR)
				+ "/" + (viewBeginCalendar.get(Calendar.MONTH) + 1) + "/" +
				viewBeginCalendar.get(Calendar.DAY_OF_MONTH) + " " +
				viewBeginCalendar.get(Calendar.HOUR_OF_DAY) + ":" +
				viewBeginCalendar.get(Calendar.MINUTE);

		final String viewEndStr = viewEndCalendar.get(Calendar.YEAR)
				+ "/" + (viewEndCalendar.get(Calendar.MONTH) + 1) + "/" +
				viewEndCalendar.get(Calendar.DAY_OF_MONTH) + " " +
				viewEndCalendar.get(Calendar.HOUR_OF_DAY) + ":" +
				viewEndCalendar.get(Calendar.MINUTE);

		Date iBeginDate = null;
		Date iEndDate = null;
		Date vBeginDate = null;
		Date vEndDate = null;

		try {
			iBeginDate = dateTimeFormat.parse(instanceBeginStr);
			iEndDate = dateTimeFormat.parse(instanceEndStr);
			vBeginDate = dateTimeFormat.parse(viewBeginStr);
			vEndDate = dateTimeFormat.parse(viewEndStr);
		} catch (Exception e) {

		}
		int[] margins = new int[2];

		int compare1 = iEndDate.compareTo(vEndDate);

		// 시작/종료일이 date가 아니나, 일정에 포함되는 경우
		if (iBeginDate.before(vBeginDate) && iEndDate.after(vEndDate)) {
			margins[0] = 0;
			margins[1] = 0;
		}
		// 시작일이 date인 경우, 종료일은 endDate 이후
		else if (iBeginDate.compareTo(vBeginDate) >= 0 &&
				iBeginDate.before(vEndDate) &&
				iEndDate.after(vEndDate)) {
			margins[0] = margin;
			margins[1] = 0;
		}
		// 종료일이 date인 경우, 시작일은 startDate이전
		else if (iEndDate.compareTo(vBeginDate) >= 0 &&
				iEndDate.compareTo(vEndDate) <= 0 &&
				iBeginDate.before(vBeginDate)) {
			margins[0] = 0;
			margins[1] = margin;
		}
		// 시작/종료일이 date인 경우
		else if (iBeginDate.compareTo(vBeginDate) >= 0 &&
				iBeginDate.before(vEndDate) &&
				iEndDate.compareTo(vBeginDate) >= 0 &&
				iEndDate.compareTo(vEndDate) <= 0) {
			margins[0] = margin;
			margins[1] = margin;
		}
		return margins;
	}

	public static String convertAttendeeStatus(int status, Context context) {
		String attendeeStatusStr = null;

		switch (status) {
			case CalendarContract.Attendees.ATTENDEE_STATUS_NONE:
				attendeeStatusStr = context.getString(R.string.ATTENDEE_STATUS_NONE);
				break;
			case CalendarContract.Attendees.ATTENDEE_STATUS_ACCEPTED:
				attendeeStatusStr = context.getString(R.string.ATTENDEE_STATUS_ACCEPTED);
				break;
			case CalendarContract.Attendees.ATTENDEE_STATUS_DECLINED:
				attendeeStatusStr = context.getString(R.string.ATTENDEE_STATUS_DECLINED);
				break;
			case CalendarContract.Attendees.ATTENDEE_STATUS_INVITED:
				attendeeStatusStr = context.getString(R.string.ATTENDEE_STATUS_INVITED);
				break;
			case CalendarContract.Attendees.ATTENDEE_STATUS_TENTATIVE:
				attendeeStatusStr = context.getString(R.string.ATTENDEE_STATUS_TENTATIVE);
				break;
		}

		return attendeeStatusStr;
	}

	public static String convertAttendeeRelationship(int relationship, Context context) {
		String attendeeRelationshipStr = null;

		switch (relationship) {
			case CalendarContract.Attendees.RELATIONSHIP_NONE:
				attendeeRelationshipStr = context.getString(R.string.RELATIONSHIP_NONE);
				break;
			case CalendarContract.Attendees.RELATIONSHIP_ATTENDEE:
				attendeeRelationshipStr = context.getString(R.string.RELATIONSHIP_ATTENDEE);
				break;
			case CalendarContract.Attendees.RELATIONSHIP_ORGANIZER:
				attendeeRelationshipStr = context.getString(R.string.RELATIONSHIP_ORGANIZER);
				break;
			case CalendarContract.Attendees.RELATIONSHIP_PERFORMER:
				attendeeRelationshipStr = context.getString(R.string.RELATIONSHIP_PERFORMER);
				break;
			case CalendarContract.Attendees.RELATIONSHIP_SPEAKER:
				attendeeRelationshipStr = context.getString(R.string.RELATIONSHIP_SPEAKER);
				break;
		}

		return attendeeRelationshipStr;
	}

	public static final Comparator<ContentValues> INSTANCE_COMPARATOR = new Comparator<ContentValues>() {
		@Override
		public int compare(ContentValues t1, ContentValues t2) {
			// 양수이면 변경된다
			long t1Begin = t1.getAsLong(CalendarContract.Instances.BEGIN);
			long t1End = t1.getAsLong(CalendarContract.Instances.END);
			long t2Begin = t2.getAsLong(CalendarContract.Instances.BEGIN);
			long t2End = t2.getAsLong(CalendarContract.Instances.END);

			if ((t1End - t1Begin) < (t2End - t2Begin)) {
				return 1;
			} else {
				return 0;
			}
		}
	};

	public static ReminderDto convertAlarmMinutes(int minutes) {
		final int WEEK_1 = 10080;
		final int DAY_1 = 1440;
		final int HOUR_1 = 60;

		// 10일 - 14400, 4주 - 40320, (1주 - 10080, 1일 - 1440, 1시간 - 60)
		final int week = minutes / WEEK_1;
		int remainder = minutes - (WEEK_1 * week);

		final int day = remainder / DAY_1;
		remainder = remainder - (DAY_1 * day);

		final int hour = remainder / HOUR_1;
		remainder = remainder - (HOUR_1 * hour);

		final int minute = remainder;

		return new ReminderDto(week, day, hour, minute);
	}

	public static String makeAlarmText(ReminderDto reminderDto, Context context) {
		StringBuilder stringBuilder = new StringBuilder();

		if (reminderDto.getWeek() > 0) {
			stringBuilder.append(reminderDto.getWeek()).append(context.getString(R.string.week)).append(" ");
		}
		if (reminderDto.getDay() > 0) {
			stringBuilder.append(reminderDto.getDay()).append(context.getString(R.string.day)).append(" ");
		}
		if (reminderDto.getHour() > 0) {
			stringBuilder.append(reminderDto.getHour()).append(context.getString(R.string.hour)).append(" ");
		}
		if (reminderDto.getMinute() > 0) {
			stringBuilder.append(reminderDto.getMinute()).append(context.getString(R.string.minute)).append(" ");
		}

		if (stringBuilder.length() == 0) {
			stringBuilder.append(context.getString(R.string.notification_on_time));
		} else {
			stringBuilder.append(context.getString(R.string.remind_before));
		}

		return stringBuilder.toString();
	}

	public static int convertReminderValues(ReminderDto reminderDto) {
		final int WEEK_1 = 10080;
		final int DAY_1 = 1440;
		final int HOUR_1 = 60;

		final int minutes = reminderDto.getWeek() * WEEK_1 + reminderDto.getDay() * DAY_1 +
				reminderDto.getHour() * HOUR_1 + reminderDto.getMinute();
		return minutes;
	}

	public static String convertDateTime(long dateTime, boolean allDay, boolean is24HourSystem) {
		if (allDay) {
			return ClockUtil.YYYY_M_D_E.format(new Date(dateTime));
		} else {
			return ClockUtil.YYYY_M_D_E.format(new Date(dateTime)) + " " +
					(is24HourSystem ? ClockUtil.HOURS_24.format(new Date(dateTime))
							: ClockUtil.HOURS_12.format(new Date(dateTime)));
		}
	}

	public static String convertDate(long date) {
		return ClockUtil.YYYY_M_D_E.format(new Date(date));
	}

	public static String convertTime(long time, boolean is24HourSystem) {
		return is24HourSystem ? ClockUtil.HOURS_24.format(new Date(time))
				: ClockUtil.HOURS_12.format(new Date(time));
	}

	public static String convertTitle(Context context, String title) {
		if (title != null) {
			if (title.isEmpty()) {
				return context.getString(R.string.empty_title);
			} else {
				return title;
			}
		} else {
			return context.getString(R.string.empty_title);
		}
	}

	public static String convertAvailability(int availability, Context context) {
		String result = null;

		switch (availability) {
			case CalendarContract.Events.AVAILABILITY_BUSY:
				result = context.getString(R.string.busy);
				break;
			case CalendarContract.Events.AVAILABILITY_FREE:
				result = context.getString(R.string.free);
				break;
			case CalendarContract.Events.AVAILABILITY_TENTATIVE:
				break;
		}

		return result;
	}

	public static String convertAccessLevel(int accessLevel, Context context) {
		String result = null;

		switch (accessLevel) {
			case CalendarContract.Events.ACCESS_DEFAULT:
				result = context.getString(R.string.access_default);
				break;
			case CalendarContract.Events.ACCESS_CONFIDENTIAL:
				break;
			case CalendarContract.Events.ACCESS_PRIVATE:
				result = context.getString(R.string.access_private);
				break;
			case CalendarContract.Events.ACCESS_PUBLIC:
				result = context.getString(R.string.access_public);
				break;
		}
		return result;
	}

	public static String getReminderMethod(Context context, int method) {
		String methodStr = null;

		switch (method) {
			case CalendarContract.Reminders.METHOD_DEFAULT:
				methodStr = context.getString(R.string.reminder_method_default);
				break;
			case CalendarContract.Reminders.METHOD_ALERT:
				methodStr = context.getString(R.string.reminder_method_alert);
				break;
			case CalendarContract.Reminders.METHOD_EMAIL:
				methodStr = context.getString(R.string.reminder_method_email);
				break;
			case CalendarContract.Reminders.METHOD_SMS:
				methodStr = context.getString(R.string.reminder_method_sms);
				break;
			case CalendarContract.Reminders.METHOD_ALARM:
				methodStr = context.getString(R.string.reminder_method_alarm);
				break;
		}
		return methodStr;
	}

	public static String[] getAccessLevelItems(Context context) {
		return new String[]{context.getString(R.string.access_default), context.getString(R.string.access_public), context.getString(R.string.access_private)};
	}

	public static String[] getAvailabilityItems(Context context) {
		return new String[]{context.getString(R.string.busy), context.getString(R.string.free)};
	}
}
