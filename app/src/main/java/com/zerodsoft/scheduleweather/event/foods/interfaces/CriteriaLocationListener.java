package com.zerodsoft.scheduleweather.event.foods.interfaces;

import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public interface CriteriaLocationListener {
	void onStartedGettingCriteriaLocation();

	void onFinishedGettingCriteriaLocation(LocationDTO criteriaLocation);
}
