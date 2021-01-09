package com.zerodsoft.scheduleweather.utility;

import android.content.Context;

import com.zerodsoft.scheduleweather.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class RecurrenceRule
{
    public static final String FREQ = "FREQ";
    public static final String UNTIL = "UNTIL";
    public static final String COUNT = "COUNT";
    public static final String INTERVAL = "INTERVAL";
    public static final String BYSECOND = "BYSECOND";
    public static final String BYMINUTE = "BYMINUTE";
    public static final String BYHOUR = "BYHOUR";
    public static final String BYDAY = "BYDAY";
    public static final String BYMONTHDAY = "BYMONTHDAY";
    public static final String BYYEARDAY = "BYYEARDAY";
    public static final String BYWEEKNO = "BYWEEKNO";
    public static final String BYMONTH = "BYMONTH";
    public static final String BYSETPOS = "BYSETPOS";
    public static final String WKST = "WKST";

    public static final String FREQ_SECONDLY = "SECONDLY";
    public static final String FREQ_MINUTELY = "MINUTELY";
    public static final String FREQ_HOURLY = "HOURLY";
    public static final String FREQ_DAILY = "DAILY";
    public static final String FREQ_WEEKLY = "WEEKLY";
    public static final String FREQ_MONTHLY = "MONTHLY";
    public static final String FREQ_YEARLY = "YEARLY";

    public static final String WEEKDAY_SUNDAY = "SU";
    public static final String WEEKDAY_MONDAY = "MO";
    public static final String WEEKDAY_TUESDAY = "TU";
    public static final String WEEKDAY_WEDNESDAY = "WE";
    public static final String WEEKDAY_THURSDAY = "TH";
    public static final String WEEKDAY_FRIDAY = "FT";
    public static final String WEEKDAY_SATURDAY = "SA";

    private static final String[] WEEKDAYS = {WEEKDAY_SUNDAY, WEEKDAY_MONDAY, WEEKDAY_TUESDAY, WEEKDAY_WEDNESDAY
            , WEEKDAY_THURSDAY, WEEKDAY_FRIDAY, WEEKDAY_SATURDAY};

    private final Map<String, String> RULE_MAP;

    public RecurrenceRule()
    {
        RULE_MAP = new HashMap<>();
    }

    public void putValue(String type, String value)
    {
        RULE_MAP.put(type, value);
    }

    public void putValue(String type, int value)
    {
        putValue(type, Integer.toString(value));
    }


    public void putValue(String type, String... value)
    {
        String[] values = value.clone();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < values.length - 1; i++)
        {
            stringBuilder.append(values[i]).append(",");
        }

        stringBuilder.append(values[values.length - 1]);

        RULE_MAP.put(type, stringBuilder.toString());
    }

    public void putValue(String type, int... value)
    {
        int[] values = value.clone();
        String[] strValues = new String[values.length];

        for (int i = 0; i < values.length; i++)
        {
            strValues[i] = Integer.toString(values[i]);
        }

        putValue(type, strValues);
    }

    public void removeValue(String type)
    {
        RULE_MAP.remove(type);
    }

    public void removeValue(String... types)
    {
        for (String type : types)
        {
            RULE_MAP.remove(type);
        }
    }

    public String getRule()
    {
        if (RULE_MAP.isEmpty())
        {
            return "";
        } else
        {
            StringBuilder rule = new StringBuilder();
            Set<String> keySet = RULE_MAP.keySet();
            final int maxIndex = keySet.size() - 1;
            int index = 0;

            for (String key : keySet)
            {
                if (index == 0)
                {
                    rule.append(key).append("=").append(RULE_MAP.get(key));
                    if (maxIndex >= 1)
                    {
                        rule.append(";");
                    }
                } else if (index == maxIndex)
                {
                    rule.append(key).append("=").append(RULE_MAP.get(key));
                } else
                {
                    rule.append(key).append("=").append(RULE_MAP.get(key)).append(";");
                }
                index++;
            }
            return rule.toString();
        }
    }

    public String getValue(String type)
    {
        return RULE_MAP.get(type);
    }

    public int count()
    {
        return RULE_MAP.size();
    }

    public boolean isEmpty()
    {
        return RULE_MAP.isEmpty();
    }

    public void clear()
    {
        RULE_MAP.clear();
    }

    public boolean containsKey(String type)
    {
        return RULE_MAP.containsKey(type);
    }

    public static String getDayOfWeek(int index)
    {
        return WEEKDAYS[index];
    }

    public static int getDayOfWeek(String day)
    {
        for (int i = 0; i < WEEKDAYS.length; i++)
        {
            if (WEEKDAYS[i].equals(day))
            {
                return i;
            }
        }
        return 0;
    }

    public String interpret(Context context)
    {
        if (isEmpty())
        {
            return "";
        } else
        {
            String freq = null;
            final String until = containsKey(UNTIL) ? getValue(UNTIL) + context.getString(R.string.up_to) : "";
            final String count = containsKey(COUNT) ? getValue(COUNT) + context.getString(R.string.count) : "";

            if (containsKey(INTERVAL))
            {

                if (getValue(INTERVAL).equals("1"))
                {
                    switch (getValue(FREQ))
                    {
                        case FREQ_DAILY:
                            freq = context.getString(R.string.recurrence_daily);
                            break;
                        case FREQ_WEEKLY:
                            freq = context.getString(R.string.recurrence_weekly) + getValue(BYDAY);
                            break;
                        case FREQ_MONTHLY:
                            if (containsKey(BYDAY))
                            {
                                freq = context.getString(R.string.recurrence_monthly) + getValue(BYDAY);
                            } else
                            {
                                freq = context.getString(R.string.recurrence_monthly);
                            }
                            break;
                        case FREQ_YEARLY:
                            freq = context.getString(R.string.recurrence_yearly);
                            break;
                    }
                } else
                {
                    switch (getValue(FREQ))
                    {
                        case FREQ_DAILY:
                            freq = getValue(INTERVAL) + context.getString(R.string.repeat_days);
                            break;
                        case FREQ_WEEKLY:
                            freq = getValue(INTERVAL) + context.getString(R.string.repeat_weeks) + getValue(BYDAY);
                            break;
                        case FREQ_MONTHLY:
                            if (containsKey(BYDAY))
                            {
                                freq = getValue(INTERVAL) + context.getString(R.string.repeat_months) + getValue(BYDAY);
                            } else
                            {
                                freq = getValue(INTERVAL) + context.getString(R.string.repeat_months);
                            }
                            break;
                        case FREQ_YEARLY:
                            freq = getValue(INTERVAL) + context.getString(R.string.repeat_years);
                            break;
                    }
                }

            } else
            {

                switch (getValue(FREQ))
                {
                    case FREQ_DAILY:
                        freq = context.getString(R.string.recurrence_daily);
                        break;
                    case FREQ_WEEKLY:
                        freq = context.getString(R.string.recurrence_weekly) + getValue(BYDAY);
                        break;
                    case FREQ_MONTHLY:
                        if (containsKey(BYDAY))
                        {
                            freq = context.getString(R.string.recurrence_monthly) + getValue(BYDAY);
                        } else
                        {
                            freq = context.getString(R.string.recurrence_monthly);
                        }
                        break;
                    case FREQ_YEARLY:
                        freq = context.getString(R.string.recurrence_yearly);
                        break;
                }

            }

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(freq);
            if (!until.isEmpty())
            {
                stringBuilder.append(", ").append(until);
            }
            if (!count.isEmpty())
            {
                stringBuilder.append(", ").append(count);
            }
            stringBuilder.append(context.getString(R.string.recurrence));

            return stringBuilder.toString();
        }
    }

    public void separateValues(String rule)
    {
        if (!rule.isEmpty())
        {
            final String COLON = ";";
            final String EQUALS_SIGN = "=";
            String[] separatedKeyValues = null;

            if (rule.contains(COLON))
            {
                String[] separatedStr = null;
                separatedStr = rule.split(COLON);

                for (String s : separatedStr)
                {
                    separatedKeyValues = s.split(EQUALS_SIGN);
                    putValue(separatedKeyValues[0], separatedKeyValues[1]);
                }
            } else
            {
                separatedKeyValues = rule.split(EQUALS_SIGN);
                putValue(separatedKeyValues[0], separatedKeyValues[1]);
            }
        }
    }
}