package com.zerodsoft.scheduleweather.calendar;

import java.io.Serializable;

public class UpdatedEventPrimaryValues implements Serializable {
	private Long originalEventId;
	private Long newEventId;
	private Long begin;
	private EventHelper.EventEditType eventEditType;

	public UpdatedEventPrimaryValues() {
	}

	public Long getOriginalEventId() {
		return originalEventId;
	}

	public void setEventEditType(EventHelper.EventEditType eventEditType) {
		this.eventEditType = eventEditType;
	}

	public EventHelper.EventEditType getEventEditType() {
		return eventEditType;
	}

	public void setOriginalEventId(Long originalEventId) {
		this.originalEventId = originalEventId;
	}

	public Long getNewEventId() {
		return newEventId;
	}

	public void setNewEventId(Long newEventId) {
		this.newEventId = newEventId;
	}

	public Long getBegin() {
		return begin;
	}

	public void setBegin(Long begin) {
		this.begin = begin;
	}
}
