package com.zerodsoft.calendarplatform.room.dto;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "food_criteria_location_info_table")
public class FoodCriteriaLocationInfoDTO {
	@ColumnInfo(name = "id")
	@PrimaryKey(autoGenerate = true)
	private int id;

	@ColumnInfo(name = "event_id", defaultValue = "NULL")
	private Long eventId;

	@ColumnInfo(name = "instance_id", defaultValue = "NULL")
	private Long instanceId;

	@ColumnInfo(name = "using_type")
	private Integer usingType;

	@ColumnInfo(name = "history_location_id", defaultValue = "NULL")
	private Integer historyLocationId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}

	public Integer getUsingType() {
		return usingType;
	}

	public void setUsingType(Integer usingType) {
		this.usingType = usingType;
	}

	public Integer getHistoryLocationId() {
		return historyLocationId;
	}

	public void setHistoryLocationId(Integer historyLocationId) {
		this.historyLocationId = historyLocationId;
	}
}
