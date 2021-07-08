package com.zerodsoft.scheduleweather.calendar;

import java.io.Serializable;

public class EditEventPrimaryValues implements Serializable {
	private Long begin;
	private EventHelper.EventEditType eventEditType;

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
}
