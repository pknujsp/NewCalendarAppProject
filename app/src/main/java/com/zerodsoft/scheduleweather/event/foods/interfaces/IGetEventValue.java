package com.zerodsoft.scheduleweather.event.foods.interfaces;

import java.io.Serializable;

public interface IGetEventValue extends Serializable {
	long getEventId();

	int getCalendarId();
}
