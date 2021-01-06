package com.zerodsoft.scheduleweather.utility;

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


    private final Map<String, String> RULE_MAP;

    public RecurrenceRule()
    {
        RULE_MAP = new HashMap<>();
    }

    public void putValue(String type, String value)
    {
        RULE_MAP.put(type, value);
    }

    public void putValue(String type, String... value)
    {
        String[] values = new String[value.length];

        for (int i = 1; i < values.length; i++)
        {
            values[i] = value[i];
            if (i != values.length - 1)
            {
                values[i] = values[i] + ",";
            }
        }

        RULE_MAP.put(type, values.toString());
    }

    public void removeValue(String type)
    {
        RULE_MAP.remove(type);
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
}