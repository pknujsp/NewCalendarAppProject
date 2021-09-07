package com.zerodsoft.calendarplatform.calendarview.interfaces;

import java.util.Date;

public interface OnDateTimeChangedListener {
	void receivedTimeTick(Date date);

	void receivedDateChanged(Date date);
}
