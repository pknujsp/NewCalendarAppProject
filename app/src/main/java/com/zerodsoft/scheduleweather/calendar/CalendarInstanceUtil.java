package com.zerodsoft.scheduleweather.calendar;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;

import androidx.annotation.NonNull;

import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationHistoryViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;

public class CalendarInstanceUtil {
	private CalendarInstanceUtil() {
	}


	public static boolean deleteEvent(CalendarViewModel calendarViewModel, LocationViewModel locationViewModel, FoodCriteriaLocationInfoViewModel foodCriteriaLocationInfoViewModel,
	                                  FoodCriteriaLocationHistoryViewModel foodCriteriaLocationHistoryViewModel, final int CALENDAR_ID, final long EVENT_ID) {
		// 참석자 - 알림 - 이벤트 순으로 삭제 (외래키 때문)
		// db column error
		calendarViewModel.deleteEvent(EVENT_ID);
		locationViewModel.removeLocation(EVENT_ID, new DbQueryCallback<Boolean>() {
			@Override
			public void onResultSuccessful(Boolean result) {

			}

			@Override
			public void onResultNoData() {

			}
		});
		foodCriteriaLocationInfoViewModel.deleteByEventId(CALENDAR_ID, EVENT_ID, new CarrierMessagingService.ResultCallback<Boolean>() {
			@Override
			public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException {

			}
		});
		foodCriteriaLocationHistoryViewModel.deleteByEventId(CALENDAR_ID, EVENT_ID, new CarrierMessagingService.ResultCallback<Boolean>() {
			@Override
			public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException {

			}
		});
		// 삭제 완료 후 캘린더 화면으로 나가고, 새로고침한다.
		return true;
	}

	private void deleteSubsequentIncludingThis() {
        /*
        // 이벤트의 반복 UNTIL을 현재 인스턴스의 시작날짜로 수정
        ContentValues recurrenceData = viewModel.getRecurrence(calendarId, eventId);
        RecurrenceRule recurrenceRule = new RecurrenceRule();
        recurrenceRule.separateValues(recurrenceData.getAsString(CalendarContract.Events.RRULE));

        GregorianCalendar calendar = new GregorianCalendar();
        final long thisInstanceBegin = instance.getAsLong(CalendarContract.Instances.BEGIN);
        calendar.setTimeInMillis(thisInstanceBegin);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        recurrenceRule.putValue(RecurrenceRule.UNTIL, ClockUtil.yyyyMMdd.format(calendar.getTime()));
        recurrenceRule.removeValue(RecurrenceRule.INTERVAL);

        recurrenceData.put(CalendarContract.Events.RRULE, recurrenceRule.getRule());
        viewModel.updateEvent(recurrenceData);

         */
		// Toast.makeText(getActivity(), "작성 중", Toast.LENGTH_SHORT).show();
	}


	public static boolean exceptThisInstance(CalendarViewModel calendarViewModel, final long BEGIN, final long EVENT_ID) {
		calendarViewModel.deleteInstance(BEGIN, EVENT_ID);
		return true;
	}
}
