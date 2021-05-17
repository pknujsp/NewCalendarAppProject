package com.zerodsoft.scheduleweather.activity;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.notification.EventNotificationHelper;
import com.zerodsoft.scheduleweather.notification.receiver.EventAlarmReceiver;

import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends android.app.Application
{
    public static ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static boolean preference_key_using_timezone_of_device = true;
    private static TimeZone preference_key_custom_timezone = null;
    private static boolean preference_key_show_canceled_instances = true;
    private static boolean preference_key_show_week_of_year = true;
    private static boolean preference_key_using_24_hour_system = true;
    private static String preference_key_radius_range = "";
    private static String preference_key_range_meter_for_search_buildings = "";
    private static boolean preference_key_show_favorite_locations_markers_on_map = true;

    public static void initNotifications(Context context)
    {
        EventAlarmReceiver.createNotificationChannel(context);
        EventNotificationHelper eventNotificationHelper = EventNotificationHelper.newInstance();
        eventNotificationHelper.setEventNotifications(context);
    }

    public static void setAppSettings(Context context)
    {
        initNotifications(context);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getAll().isEmpty())
        {
            //앱 처음 실행 하는 경우 - 기본 값을 설정해준다.
            SharedPreferences.Editor editor = preferences.edit();

            //기본 시간대 사용여부
            preference_key_using_timezone_of_device = true;
            editor.putBoolean(context.getString(R.string.preference_key_using_timezone_of_device), preference_key_using_timezone_of_device);

            //기기 시간대 설정
            preference_key_custom_timezone = TimeZone.getDefault();
            editor.putString(context.getString(R.string.preference_key_custom_timezone), preference_key_custom_timezone.getID());

            //거절한 일정 표시 여부
            preference_key_show_canceled_instances = true;
            editor.putBoolean(context.getString(R.string.preference_key_show_canceled_instances), preference_key_show_canceled_instances);

            //주차 표시 여부
            preference_key_show_week_of_year = true;
            editor.putBoolean(context.getString(R.string.preference_key_show_week_of_year), preference_key_show_week_of_year);

            //12/24시간제 설정
            preference_key_using_24_hour_system = true;
            editor.putBoolean(context.getString(R.string.preference_key_using_24_hour_system), preference_key_using_24_hour_system);

            //기본 장소 검색 범위(반지름)길이 설정
            preference_key_radius_range = "1500";
            editor.putString(context.getString(R.string.preference_key_radius_range), preference_key_radius_range);

            //빌딩 검색 범위(반지름)길이 설정
            preference_key_range_meter_for_search_buildings = "500";
            editor.putString(context.getString(R.string.preference_key_range_meter_for_search_buildings), preference_key_range_meter_for_search_buildings);

            preference_key_show_favorite_locations_markers_on_map = true;
            editor.putBoolean(context.getString(R.string.preference_key_show_favorite_locations_markers_on_map), preference_key_show_favorite_locations_markers_on_map);

            editor.commit();
        } else
        {
            preference_key_using_timezone_of_device = preferences.getBoolean(context.getString(R.string.preference_key_using_timezone_of_device), false);
            preference_key_custom_timezone = TimeZone.getTimeZone(preferences.getString(context.getString(R.string.preference_key_custom_timezone), ""));
            preference_key_show_canceled_instances = preferences.getBoolean(context.getString(R.string.preference_key_show_canceled_instances), false);
            preference_key_show_week_of_year = preferences.getBoolean(context.getString(R.string.preference_key_show_week_of_year), false);
            preference_key_using_24_hour_system = preferences.getBoolean(context.getString(R.string.preference_key_using_24_hour_system), false);
            preference_key_radius_range = preferences.getString(context.getString(R.string.preference_key_radius_range), "");
            preference_key_range_meter_for_search_buildings = preferences.getString(context.getString(R.string.preference_key_range_meter_for_search_buildings), "");
            preference_key_show_favorite_locations_markers_on_map = preferences.getBoolean(context.getString(R.string.preference_key_show_favorite_locations_markers_on_map), false);
        }
    }

    public static boolean isPreference_key_using_timezone_of_device()
    {
        return preference_key_using_timezone_of_device;
    }

    public static TimeZone getPreference_key_custom_timezone()
    {
        return preference_key_custom_timezone;
    }

    public static boolean isPreference_key_show_canceled_instances()
    {
        return preference_key_show_canceled_instances;
    }

    public static boolean isPreference_key_show_week_of_year()
    {
        return preference_key_show_week_of_year;
    }

    public static boolean isPreference_key_using_24_hour_system()
    {
        return preference_key_using_24_hour_system;
    }

    public static String getPreference_key_radius_range()
    {
        return preference_key_radius_range;
    }

    public static String getPreference_key_range_meter_for_search_buildings()
    {
        return preference_key_range_meter_for_search_buildings;
    }

    public static void setPreference_key_using_timezone_of_device(boolean preference_key_using_timezone_of_device)
    {
        App.preference_key_using_timezone_of_device = preference_key_using_timezone_of_device;
    }

    public static void setPreference_key_custom_timezone(TimeZone timezone)
    {
        App.preference_key_custom_timezone = timezone;
    }

    public static void setPreference_key_show_canceled_instances(boolean preference_key_show_canceled_instances)
    {
        App.preference_key_show_canceled_instances = preference_key_show_canceled_instances;
    }

    public static void setPreference_key_show_week_of_year(boolean preference_key_show_week_of_year)
    {
        App.preference_key_show_week_of_year = preference_key_show_week_of_year;
    }

    public static void setPreference_key_settings_hour_system(boolean value)
    {
        App.preference_key_using_24_hour_system = value;
    }

    public static void setPreference_key_radius_range(String preference_key_radius_range)
    {
        App.preference_key_radius_range = preference_key_radius_range;
    }

    public static void setPreference_key_range_meter_for_search_buildings(String preference_key_range_meter_for_search_buildings)
    {
        App.preference_key_range_meter_for_search_buildings = preference_key_range_meter_for_search_buildings;
    }

    public static void setPreference_key_show_favorite_locations_markers_on_map(boolean preference_key_show_favorite_locations_markers_on_map)
    {
        App.preference_key_show_favorite_locations_markers_on_map = preference_key_show_favorite_locations_markers_on_map;
    }

    public static boolean isPreference_key_show_favorite_locations_markers_on_map()
    {
        return preference_key_show_favorite_locations_markers_on_map;
    }
}
