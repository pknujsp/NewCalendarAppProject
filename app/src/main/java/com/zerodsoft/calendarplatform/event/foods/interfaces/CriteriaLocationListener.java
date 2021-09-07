package com.zerodsoft.calendarplatform.event.foods.interfaces;

import com.zerodsoft.calendarplatform.room.dto.LocationDTO;

public interface CriteriaLocationListener {
	void onStartedGettingCriteriaLocation();

	void onFinishedGettingCriteriaLocation(LocationDTO criteriaLocation);
}
