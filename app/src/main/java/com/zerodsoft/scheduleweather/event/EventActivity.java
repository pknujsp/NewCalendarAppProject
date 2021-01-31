package com.zerodsoft.scheduleweather.event;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.service.carrier.CarrierMessagingService;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.event.common.MLocActivity;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocation;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.event.EventFragment;
import com.zerodsoft.scheduleweather.event.location.PlacesAroundLocationFragment;
import com.zerodsoft.scheduleweather.event.weather.WeatherFragment;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public class EventActivity extends AppCompatActivity implements ILocation
{
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private CalendarViewModel viewModel;
    private LocationViewModel locationViewModel;
    private BottomNavigationView bottomNavigationView;
    private FragmentContainerView fragmentContainerView;

    private Bundle eventBundle;
    private ContentValues event;

    private EventFragment eventFragment;
    private WeatherFragment weatherFragment;
    private PlacesAroundLocationFragment placesAroundLocationFragment;

    private OnBackPressedCallback onBackPressedCallback;

    private FragmentManager fragmentManager;
    private Fragment currentFragment = null;

    private static final String TAG_INFO = "info";
    private static final String TAG_WEATHER = "weather";
    private static final String TAG_LOCATION = "location";
    private static final int REQUEST_SELECT_LOCATION = 10;

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        onBackPressedCallback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_info);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.schedule_bottom_nav);
        fragmentContainerView = (FragmentContainerView) findViewById(R.id.schedule_fragment_container);

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        fragmentManager = getSupportFragmentManager();

        final long eventId = getIntent().getLongExtra("eventId", 0);
        final int calendarId = getIntent().getIntExtra("calendarId", 0);

        viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        viewModel.init(getApplicationContext());
        viewModel.getEvent(calendarId, eventId);
        viewModel.getEventLiveData().observe(this, new Observer<DataWrapper<ContentValues>>()
        {
            @Override
            public void onChanged(DataWrapper<ContentValues> contentValuesDataWrapper)
            {
                if (contentValuesDataWrapper.getData() != null)
                {
                    event = contentValuesDataWrapper.getData();
                    eventBundle = new Bundle();
                    eventBundle.putParcelable("event", event);
                    setFragments();
                }
            }
        });

        locationViewModel.getLocationLiveData().observe(this, new Observer<LocationDTO>()
        {
            @Override
            public void onChanged(LocationDTO locationDTO)
            {

            }
        });


    }

    private void setFragments()
    {
        eventFragment = new EventFragment();
        weatherFragment = new WeatherFragment(this);
        placesAroundLocationFragment = new PlacesAroundLocationFragment(this);

        eventFragment.setArguments(eventBundle);
        currentFragment = eventFragment;

        fragmentManager.beginTransaction().add(R.id.schedule_fragment_container, eventFragment, TAG_INFO).hide(eventFragment)
                .add(R.id.schedule_fragment_container, weatherFragment, TAG_WEATHER).hide(placesAroundLocationFragment)
                .add(R.id.schedule_fragment_container, placesAroundLocationFragment, TAG_LOCATION).hide(weatherFragment)
                .show(eventFragment)
                .commit();
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            Fragment newFragment = null;

            switch (item.getItemId())
            {
                case R.id.schedule_info:
                    newFragment = eventFragment;
                    break;
                case R.id.schedule_weather:
                    newFragment = weatherFragment;
                    break;
                case R.id.schedule_location:
                    newFragment = placesAroundLocationFragment;
                    break;
            }

            //현재 표시된 프래그먼트와 변경할 프래그먼트가 같은 경우 변경하지 않음
            if (currentFragment != newFragment)
            {
                fragmentManager.beginTransaction().hide(currentFragment).show(newFragment).commit();
                currentFragment = newFragment;
            }
            return true;
        }
    };

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        onBackPressedCallback.remove();
    }

    @Override
    public void showRequestLocDialog()
    {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(EventActivity.this)
                .setTitle(getString(R.string.request_select_location_title))
                .setMessage(getString(R.string.request_select_location_description))
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton(getString(R.string.check), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        Intent intent = new Intent(EventActivity.this, MLocActivity.class);

                        intent.putExtra("calendarId", event.getAsInteger(CalendarContract.Events.CALENDAR_ID));
                        intent.putExtra("eventId", event.getAsInteger(CalendarContract.Events._ID));
                        intent.putExtra("location", event.getAsString(CalendarContract.Events.EVENT_LOCATION));

                        startActivityForResult(intent, REQUEST_SELECT_LOCATION);
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 상세한 위치 정보 전달
     */
    @Override
    public void getLocation(CarrierMessagingService.ResultCallback<LocationDTO> resultCallback)
    {
        locationViewModel.getLocation(event.getAsInteger(CalendarContract.Events.CALENDAR_ID),
                event.getAsLong(CalendarContract.Events._ID), resultCallback);
    }

    /**
     * 이벤트에 지정되어 있는 간단한 위치값 전달
     *
     * @return
     */
    @Override
    public boolean hasSimpleLocation()
    {
        return event.getAsString(CalendarContract.Events.EVENT_LOCATION) != null;
    }

    /**
     * 날씨, 주변 정보들을 표시하기 위해 지정한 상세 위치 정보를 가지고 있는지 확인
     *
     * @param resultCallback
     */
    @Override
    public void hasDetailLocation(CarrierMessagingService.ResultCallback<Boolean> resultCallback)
    {
        locationViewModel.hasDetailLocation(event.getAsInteger(CalendarContract.Events.CALENDAR_ID),
                event.getAsLong(CalendarContract.Events._ID), resultCallback);
    }
}