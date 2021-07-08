package com.zerodsoft.scheduleweather.calendar;

import com.zerodsoft.scheduleweather.common.enums.LocationIntentCode;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import java.io.Serializable;

public class EditEventPrimaryValues implements Serializable {
	private Long begin;
	private Long newEventId;
	private Long originalEventId;
	private EventHelper.EventEditType eventEditType;
	private LocationIntentCode locationIntentCode;
	private LocationDTO newLocationDto;

	public EditEventPrimaryValues() {
	}


	public Long getBegin() {
		return begin;
	}

	public void setBegin(Long begin) {
		this.begin = begin;
	}

	public void setEventEditType(EventHelper.EventEditType eventEditType) {
		this.eventEditType = eventEditType;
	}

	public EventHelper.EventEditType getEventEditType() {
		return eventEditType;
	}

	public void setNewEventId(Long newEventId) {
		this.newEventId = newEventId;
	}

	public Long getNewEventId() {
		return newEventId;
	}

	public void setNewLocationDto(LocationIntentCode locationIntentCode, LocationDTO newLocationDto) {
		this.locationIntentCode = locationIntentCode;
		this.newLocationDto = newLocationDto;
	}

	public LocationDTO getNewLocationDto() {
		return newLocationDto;
	}


	public void setOriginalEventId(Long originalEventId) {
		this.originalEventId = originalEventId;
	}

	public Long getOriginalEventId() {
		return originalEventId;
	}

	public LocationIntentCode getLocationIntentCode() {
		return locationIntentCode;
	}
}
