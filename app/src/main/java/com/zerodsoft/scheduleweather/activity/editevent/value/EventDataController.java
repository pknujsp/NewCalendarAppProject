package com.zerodsoft.scheduleweather.activity.editevent.value;

import android.content.ContentValues;
import android.content.Context;
import android.provider.CalendarContract;

import java.util.Calendar;
import java.util.List;

public class EventDataController
{
    private EventData newEventData;
    private EventData modifiedEventData;
    private EventData savedEventData;
    private EventDefaultValue eventDefaultValue;

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

    public int getEventValueAsInt(String key)
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

    public long getEventValueAsLong(String key)
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

    public boolean getEventValueAsBoolean(String key)
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

    public void putAttendees(List<ContentValues> attendees)
    {
        int dataStorageType = getDataStorageType();

        if (dataStorageType == NEW)
        {
            newEventData.getATTENDEES().clear();
            newEventData.getATTENDEES().addAll(attendees);
        } else if (dataStorageType == MODIFY)
        {
            modifiedEventData.getATTENDEES().clear();
            modifiedEventData.getATTENDEES().addAll(attendees);
        }
    }

    public void putAttendee(ContentValues attendee)
    {
        int dataStorageType = getDataStorageType();

        if (dataStorageType == NEW)
        {
            newEventData.getATTENDEES().add(attendee);
        } else if (dataStorageType == MODIFY)
        {
            modifiedEventData.getATTENDEES().add(attendee);
        }

    }

    public void putReminders(List<ContentValues> reminders)
    {
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
    }

    public void putReminder(int minutes)
    {
        int dataStorageType = getDataStorageType();

        ContentValues reminder = new ContentValues();
        reminder.put(CalendarContract.Reminders.MINUTES, minutes);
        reminder.put(CalendarContract.Reminders.CALENDAR_ID, getEventValueAsInt(CalendarContract.Events.CALENDAR_ID));
        reminder.put(CalendarContract.Reminders.METHOD, getEventValueAsInt(CalendarContract.Reminders.METHO));

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

        if (dataStorageType == NEW)
        {
            if (!newEventData.getREMINDERS().isEmpty())
            {
                List<ContentValues> reminders = newEventData.getREMINDERS();

                if (reminders.size() == 1)
                {
                    putEventValue(CalendarContract.Events.HAS_ALARM, 0);
                }

                int index = 0;
                for (ContentValues reminder : reminders)
                {
                    if (reminder.getAsInteger(CalendarContract.Reminders.MINUTES) == minutes)
                    {
                        break;
                    }
                    index++;
                }

                newEventData.getREMINDERS().remove(index);
            }
        } else if (dataStorageType == MODIFY)
        {
            if (!modifiedEventData.getREMINDERS().isEmpty())
            {
                List<ContentValues> reminders = modifiedEventData.getREMINDERS();
                int index = 0;
                for (ContentValues reminder : reminders)
                {
                    if (reminder.getAsInteger(CalendarContract.Reminders.MINUTES) == minutes)
                    {
                        break;
                    }
                    index++;
                }

                modifiedEventData.getREMINDERS().remove(index);
            }
        }

    }

    public void removeAttendee(String email)
    {
        int responseDataType = getDataStorageType();

        if (responseDataType == NEW)
        {
            if (!newEventData.getATTENDEES().isEmpty())
            {
                List<ContentValues> attendees = newEventData.getATTENDEES();
                // 참석자수가 2명이면 모두 삭제(조직자, 참석자로 구성된 상태에서 참석자를 제거하기 때문)
                if (attendees.size() == 2)
                {
                    newEventData.getATTENDEES().clear();

                    removeEventValue(CalendarContract.Events.GUESTS_CAN_MODIFY);
                    removeEventValue(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS);
                    removeEventValue(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS);
                } else
                {
                    int index = 0;
                    for (ContentValues attendee : attendees)
                    {
                        if (attendee.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL).equals(email))
                        {
                            break;
                        }
                        index++;
                    }

                    newEventData.getATTENDEES().remove(index);
                }

            }
        } else if (responseDataType == MODIFY)
        {
            if (!modifiedEventData.getATTENDEES().isEmpty())
            {
                List<ContentValues> attendees = modifiedEventData.getATTENDEES();

                if (attendees.size() == 2)
                {
                    modifiedEventData.getATTENDEES().clear();
                } else
                {
                    int index = 0;
                    for (ContentValues attendee : attendees)
                    {
                        if (attendee.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL).equals(email))
                        {
                            break;
                        }
                        index++;
                    }

                    modifiedEventData.getATTENDEES().remove(index);
                }

            }
        }

    }

    public void setCalendarValue(ContentValues calendar)
    {
        putEventValue(CalendarContract.Events.CALENDAR_ID, calendar.getAsInteger(CalendarContract.Calendars._ID));
        putEventValue(CalendarContract.Events.CALENDAR_DISPLAY_NAME, calendar.getAsString(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME));
        putEventValue(CalendarContract.Events.ACCOUNT_NAME, calendar.getAsString(CalendarContract.Calendars.ACCOUNT_NAME));
        putEventValue(CalendarContract.Events.CALENDAR_COLOR, calendar.getAsInteger(CalendarContract.Calendars.CALENDAR_COLOR));
        putEventValue(CalendarContract.Events.OWNER_ACCOUNT, calendar.getAsString(CalendarContract.Calendars.OWNER_ACCOUNT));
    }
}
