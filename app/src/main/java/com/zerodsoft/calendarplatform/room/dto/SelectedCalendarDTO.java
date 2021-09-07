package com.zerodsoft.calendarplatform.room.dto;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "selected_calendars_table")
public class SelectedCalendarDTO {

	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	private Integer id;

	@ColumnInfo(name = "owner_account")
	private String ownerAccount;

	@ColumnInfo(name = "account_name")
	private String accountName;

	@ColumnInfo(name = "calendar_id")
	private Integer calendarId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOwnerAccount() {
		return ownerAccount;
	}

	public void setOwnerAccount(String ownerAccount) {
		this.ownerAccount = ownerAccount;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public Integer getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(Integer calendarId) {
		this.calendarId = calendarId;
	}
}
