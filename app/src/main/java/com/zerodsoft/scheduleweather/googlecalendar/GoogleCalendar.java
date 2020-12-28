package com.zerodsoft.scheduleweather.googlecalendar;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.zerodsoft.scheduleweather.AppMainActivity;
import com.zerodsoft.scheduleweather.R;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GoogleCalendar
{
    public static final int REQ_SIGN_GOOGLE = 100;
    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS_AUTO = 1003;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS_SELF = 1004;
    public static final String GOOGLE_ACCOUNT_NAME = "GOOGLE_ACCOUNT_NAME";
    private final String[] SCOPES = {CalendarScopes.CALENDAR};

    private final String APPLICATION_NAME = "test calendar";
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final String TOKENS_DIRECTORY_PATH = "tokens";
    private final String CREDENTIALS_FILE_PATH = "/client_secret.json";

    private GoogleAccountCredential googleAccountCredential;
    private Calendar calendarService;
    private Activity activity;

    private static GoogleCalendar instance;

    public GoogleCalendar(Activity activity)
    {
        this.activity = activity;
        init();
    }

    public static GoogleCalendar newInstance(Activity activity)
    {
        instance = new GoogleCalendar(activity);
        return instance;
    }

    public static GoogleCalendar getInstance()
    {
        return instance;
    }

    public void init()
    {
        googleAccountCredential = GoogleAccountCredential.usingOAuth2(
                activity.getApplicationContext(),
                Arrays.asList(SCOPES)
        ).setBackOff(new ExponentialBackOff());
    }

    public void disconnect()
    {
        calendarService = null;
        googleAccountCredential = null;
        activity = null;
    }

    public void requestAccountPicker()
    {
        activity.startActivityForResult(googleAccountCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    private String getCalendarID(String calendarTitle)
    {
        String id = null;
        String pageToken = null;

        do
        {
            CalendarList calendarList = null;
            try
            {
                calendarList = calendarService.calendarList().list().setPageToken(pageToken).execute();
            } catch (UserRecoverableAuthIOException e)
            {
                activity.startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            List<CalendarListEntry> items = calendarList.getItems();

            for (CalendarListEntry calendarListEntry : items)
            {
                if (calendarListEntry.getSummary().equals(calendarTitle))
                {
                    id = calendarListEntry.getId();
                }
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);
        return id;
    }

    public void connect(String keyAccountName) throws IOException, GeneralSecurityException
    {
        googleAccountCredential.setSelectedAccountName(keyAccountName);
        initCalendarService();
    }

    public List<Event> getEvents(String calendarId) throws IOException
    {
        // DateTime now = new DateTime(System.currentTimeMillis());
        // String calendarId = getCalendarID(googleAccountCredential.getSelectedAccountName());

        Events events = calendarService.events().list(calendarId)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        return events.getItems();
    }

    public List<CalendarListEntry> getCalendarList() throws IOException
    {
        CalendarList calendarList = calendarService.calendarList().list().execute();
        return calendarList.getItems();
    }

    public com.google.api.services.calendar.model.Calendar getCalendar(String calendarId) throws IOException
    {
        com.google.api.services.calendar.model.Calendar calendar = calendarService.calendars().get(calendarId).execute();
        return calendar;
    }

    public void initCalendarService() throws IOException, GeneralSecurityException
    {
        final NetHttpTransport NET_HTTP_TRANSPORT = new NetHttpTransport.Builder().build();
        final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

        calendarService = new Calendar
                .Builder(NET_HTTP_TRANSPORT, JSON_FACTORY, googleAccountCredential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
