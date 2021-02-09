package com.zerodsoft.scheduleweather.activity.editevent.value;

import android.content.ContentValues;
import android.content.Context;
import android.provider.CalendarContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventDataController
{
    private EventData newEventData;
    private EventData modifiedEventData;
    private EventData savedEventData;
    private EventDefaultValue eventDefaultValue;

    private ContentValues selectedCalendar = new ContentValues();

    public static final int NEW_EVENT = 0;
    public static final int MODIFY_EVENT = 1;

    public static final int ATTENDEE = 10;
    public static final int EVENT = 20;
    public static final int REMINDER = 30;

    public static final int NEW = 100;
    public static final int SAVED = 110;
    public static final int MODIFY = 120;

    public final int REQUEST_CODE;

    private boolean modifiedStart = false;
    private boolean modifiedEnd = false;

    public EventDataController(Context context, int requestCode)
    {
        this.REQUEST_CODE = requestCode;

        if (requestCode == NEW_EVENT)
        {
            this.newEventData = new EventData();
        } else if (requestCode == MODIFY_EVENT)
        {
            this.modifiedEventData = new EventData();
            this.savedEventData = new EventData();
        }
        this.eventDefaultValue = new EventDefaultValue(context);
    }

    public EventDefaultValue getEventDefaultValue()
    {
        return eventDefaultValue;
    }

    public EventData getNewEventData()
    {
        return newEventData;
    }

    public EventData getSavedEventData()
    {
        return savedEventData;
    }

    public EventData getModifiedEventData()
    {
        return modifiedEventData;
    }

    public ContentValues getSelectedCalendar()
    {
        return selectedCalendar;
    }

    /**
     * 가져올 데이터를 NEW/MODIFY/SAVED중 어디서 가져올 것인지 파악
     *
     * @param key
     * @return
     */
    public int getDataStorageType(String key)
    {
        if (REQUEST_CODE == NEW_EVENT)
        {
            return NEW;
        } else
        {
            if (modifiedEventData.getEVENT().containsKey(key))
            {
                return MODIFY;
            } else
            {
                return SAVED;
            }
        }
    }

    /**
     * 저장할 데이터가 NEW/MODIFY중 어디인지 파악
     *
     * @return
     */
    public int getDataStorageType()
    {
        if (REQUEST_CODE == NEW_EVENT)
        {
            return NEW;
        } else
        {
            return MODIFY;
        }
    }

    public Calendar getStart()
    {
        if (REQUEST_CODE == NEW_EVENT)
        {
            return newEventData.getStart();
        } else
        {
            if (modifiedStart)
            {
                return modifiedEventData.getStart();
            } else
            {
                return savedEventData.getStart();
            }
        }
    }

    public Calendar getEnd()
    {
        if (REQUEST_CODE == NEW_EVENT)
        {
            return newEventData.getEnd();
        } else
        {
            if (modifiedEnd)
            {
                return modifiedEventData.getEnd();
            } else
            {
                return savedEventData.getEnd();
            }
        }
    }

    public void setStart(long time)
    {
        if (REQUEST_CODE == NEW_EVENT)
        {
            newEventData.setStart(time);
        } else
        {
            modifiedEventData.setStart(time);
            modifiedStart = true;
        }
    }

    public void setEnd(long time)
    {
        if (REQUEST_CODE == NEW_EVENT)
        {
            newEventData.setEnd(time);
        } else
        {
            modifiedEventData.setEnd(time);
            modifiedEnd = true;
        }
    }

    public Integer getEventValueAsInt(String key)
    {
        int responseDataType = getDataStorageType(key);

        if (responseDataType == NEW)
        {
            return newEventData.getEVENT().getAsInteger(key);
        } else if (responseDataType == MODIFY)
        {
            return modifiedEventData.getEVENT().getAsInteger(key);
        } else
        {
            return savedEventData.getEVENT().getAsInteger(key);
        }
    }

    public Long getEventValueAsLong(String key)
    {
        int responseDataType = getDataStorageType(key);

        if (responseDataType == NEW)
        {
            return newEventData.getEVENT().getAsLong(key);
        } else if (responseDataType == MODIFY)
        {
            return modifiedEventData.getEVENT().getAsLong(key);
        } else
        {
            return savedEventData.getEVENT().getAsLong(key);
        }
    }

    public String getEventValueAsString(String key)
    {
        int responseDataType = getDataStorageType(key);

        if (responseDataType == NEW)
        {
            return newEventData.getEVENT().getAsString(key);
        } else if (responseDataType == MODIFY)
        {
            return modifiedEventData.getEVENT().getAsString(key);
        } else
        {
            return savedEventData.getEVENT().getAsString(key);
        }
    }

    public Boolean getEventValueAsBoolean(String key)
    {
        int responseDataType = getDataStorageType(key);

        if (responseDataType == NEW)
        {
            return newEventData.getEVENT().getAsBoolean(key);
        } else if (responseDataType == MODIFY)
        {
            return modifiedEventData.getEVENT().getAsBoolean(key);
        } else
        {
            return savedEventData.getEVENT().getAsBoolean(key);
        }
    }

    public List<ContentValues> getReminders()
    {
        int responseDataType = 0;

        if (REQUEST_CODE == NEW_EVENT)
        {
            responseDataType = NEW;
        } else if (REQUEST_CODE == MODIFY_EVENT)
        {
            if (modifiedEventData.getREMINDERS().isEmpty())
            {
                responseDataType = SAVED;
            } else
            {
                responseDataType = MODIFY;
            }
        }

        if (responseDataType == NEW)
        {
            return newEventData.getREMINDERS();
        } else if (responseDataType == MODIFY)
        {
            return modifiedEventData.getREMINDERS();
        } else
        {
            return savedEventData.getREMINDERS();
        }
    }

    public List<ContentValues> getAttendees()
    {
        int responseDataType = 0;

        if (REQUEST_CODE == NEW_EVENT)
        {
            responseDataType = NEW;
        } else if (REQUEST_CODE == MODIFY_EVENT)
        {
            if (modifiedEventData.getATTENDEES().isEmpty())
            {
                responseDataType = SAVED;
            } else
            {
                responseDataType = MODIFY;
            }
        }

        if (responseDataType == NEW)
        {
            return newEventData.getATTENDEES();
        } else if (responseDataType == MODIFY)
        {
            return modifiedEventData.getATTENDEES();
        } else
        {
            return savedEventData.getATTENDEES();
        }
    }

    public void putEventValue(String key, String value)
    {
        int putDataType = getDataStorageType();

        if (putDataType == NEW)
        {
            newEventData.getEVENT().put(key, value);
        } else if (putDataType == MODIFY)
        {
            modifiedEventData.getEVENT().put(key, value);
        }
    }

    public void putEventValue(String key, long value)
    {
        int putDataType = getDataStorageType();

        if (putDataType == NEW)
        {
            newEventData.getEVENT().put(key, value);
        } else if (putDataType == MODIFY)
        {
            modifiedEventData.getEVENT().put(key, value);
        }
    }

    public void putEventValue(String key, boolean value)
    {
        int putDataType = getDataStorageType();

        if (putDataType == NEW)
        {
            newEventData.getEVENT().put(key, value ? 1 : 0);
        } else if (putDataType == MODIFY)
        {
            modifiedEventData.getEVENT().put(key, value ? 1 : 0);
        }
    }

    public void putEventValue(String key, int value)
    {
        int putDataType = getDataStorageType();

        if (putDataType == NEW)
        {
            newEventData.getEVENT().put(key, value);
        } else if (putDataType == MODIFY)
        {
            modifiedEventData.getEVENT().put(key, value);
        }
    }

    public void putAttendees(List<ContentValues> attendees, boolean guestsCanModify, boolean guestsCanInviteOthers,
                             boolean guestsCanSeeGuests)
    {
        int dataStorageType = getDataStorageType();

        //eventId는 이벤트 수정/등록 후 받은 id를 가지고 지정
        //calendarId는 수정/등록 시에 지정

        if (dataStorageType == NEW)
        {
            newEventData.getATTENDEES().clear();
            newEventData.getATTENDEES().addAll(attendees);
        } else if (dataStorageType == MODIFY)
        {
            modifiedEventData.getATTENDEES().clear();
            modifiedEventData.getATTENDEES().addAll(attendees);
        }

        putEventValue(CalendarContract.Events.GUESTS_CAN_MODIFY, guestsCanModify ? 1 : 0);
        putEventValue(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, guestsCanInviteOthers ? 1 : 0);
        putEventValue(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, guestsCanSeeGuests ? 1 : 0);

    }

    public void putReminders(List<ContentValues> reminders)
    {
        //eventId는 이벤트 수정/등록 후 받은 id를 가지고 지정
        //calendarId는 수정/등록 시에 지정
        int dataStorageType = getDataStorageType();

        if (dataStorageType == NEW)
        {
            newEventData.getREMINDERS().clear();
            newEventData.getREMINDERS().addAll(reminders);
        } else if (dataStorageType == MODIFY)
        {
            modifiedEventData.getREMINDERS().clear();
            modifiedEventData.getREMINDERS().addAll(reminders);
        }

        if (getEventValueAsInt(CalendarContract.Events.HAS_ALARM) == 0)
        {
            putEventValue(CalendarContract.Events.HAS_ALARM, 1);
        }
    }

    public void putReminder(ContentValues reminder)
    {
        int dataStorageType = getDataStorageType();

        if (dataStorageType == NEW)
        {
            newEventData.getREMINDERS().add(reminder);
        } else if (dataStorageType == MODIFY)
        {
            modifiedEventData.getREMINDERS().add(reminder);
        }

        if (getEventValueAsInt(CalendarContract.Events.HAS_ALARM) == 0)
        {
            putEventValue(CalendarContract.Events.HAS_ALARM, 1);
        }
    }

    public void removeEventValue(String key)
    {
        int dataStorageType = getDataStorageType();

        if (dataStorageType == NEW)
        {
            if (newEventData.getEVENT().containsKey(key))
            {
                newEventData.getEVENT().remove(key);
            }
        } else if (dataStorageType == MODIFY)
        {
            if (modifiedEventData.getEVENT().containsKey(key))
            {
                modifiedEventData.getEVENT().remove(key);
            }
        }
    }

    public void removeReminder(int minutes)
    {
        int dataStorageType = getDataStorageType();
        List<ContentValues> reminders = new ArrayList<>();

        if (dataStorageType == NEW)
        {
            reminders = newEventData.getREMINDERS();
        } else if (dataStorageType == MODIFY)
        {
            reminders = modifiedEventData.getREMINDERS();
        }

        if (!reminders.isEmpty())
        {
            for (int i = 0; i < reminders.size(); i++)
            {
                if (reminders.get(i).getAsInteger(CalendarContract.Reminders.MINUTES) == minutes)
                {
                    reminders.remove(i);

                    if (reminders.isEmpty())
                    {
                        putEventValue(CalendarContract.Events.HAS_ALARM, 0);
                    }
                    break;
                }
            }

        }

    }

    public void modifyReminder(ContentValues reminder, int previousMinutes)
    {
        int dataStorageType = getDataStorageType();
        List<ContentValues> reminders = new ArrayList<>();

        if (dataStorageType == NEW)
        {
            reminders = newEventData.getREMINDERS();
        } else if (dataStorageType == MODIFY)
        {
            reminders = modifiedEventData.getREMINDERS();
        }

        for (int i = 0; i < reminders.size(); i++)
        {
            if (reminders.get(i).getAsInteger(CalendarContract.Reminders.MINUTES) == previousMinutes)
            {
                reminders.get(i).clear();
                reminders.get(i).putAll(reminder);
                break;
            }
        }

    }

    public void removeReminders()
    {
        int dataStorageType = getDataStorageType();
        List<ContentValues> reminders = new ArrayList<>();

        if (dataStorageType == NEW)
        {
            reminders = newEventData.getREMINDERS();
        } else if (dataStorageType == MODIFY)
        {
            reminders = modifiedEventData.getREMINDERS();
        }

        reminders.clear();
        putEventValue(CalendarContract.Events.HAS_ALARM, 0);
    }

    public void removeAttendee(String email)
    {
        int responseDataType = getDataStorageType();
        List<ContentValues> attendees = new ArrayList<>();

        if (responseDataType == NEW)
        {
            attendees = newEventData.getATTENDEES();
        } else if (responseDataType == MODIFY)
        {
            attendees = modifiedEventData.getATTENDEES();
        }

        // 참석자수가 2명이면 모두 삭제(조직자, 참석자로 구성된 상태에서 참석자를 제거하기 때문)
        if (attendees.size() == 2)
        {
            attendees.clear();
            removeEventValue(CalendarContract.Events.GUESTS_CAN_MODIFY);
            removeEventValue(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS);
            removeEventValue(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS);
        } else if (attendees.size() >= 3)
        {
            int index = 0;
            for (ContentValues attendee : attendees)
            {
                if (attendee.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL).equals(email))
                {
                    attendees.remove(index);
                    break;
                }
                index++;
            }
        }
    }

    public void removeAttendees()
    {
        int responseDataType = getDataStorageType();
        List<ContentValues> attendees = new ArrayList<>();

        if (responseDataType == NEW)
        {
            attendees = newEventData.getATTENDEES();
        } else if (responseDataType == MODIFY)
        {
            attendees = modifiedEventData.getATTENDEES();
        }

        attendees.clear();
        removeEventValue(CalendarContract.Events.GUESTS_CAN_MODIFY);
        removeEventValue(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS);
        removeEventValue(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS);
    }

    public void setCalendarValue(ContentValues calendar)
    {
        putEventValue(CalendarContract.Events.CALENDAR_ID, calendar.getAsInteger(CalendarContract.Calendars._ID));

        selectedCalendar.clear();
        selectedCalendar.putAll(calendar);
    }
}
