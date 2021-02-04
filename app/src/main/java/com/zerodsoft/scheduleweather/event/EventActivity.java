package com.zerodsoft.scheduleweather.event;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.service.carrier.CarrierMessagingService;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.ActivityScheduleInfoBinding;
import com.zerodsoft.scheduleweather.event.common.MLocActivity;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocation;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.event.EventFragment;
import com.zerodsoft.scheduleweather.event.location.PlacesAroundLocationFragment;
import com.zerodsoft.scheduleweather.event.weather.WeatherFragment;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public class EventActivity extends AppCompatActivity implements ILocation
{
    private ActivityScheduleInfoBinding binding;

    private LocationViewModel locationViewModel;

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_schedule_info);

        binding.scheduleBottomNav.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        binding.eventFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (binding.eventFab.isExpanded())
                {
                    binding.eventFab.setExpanded(false);
                    collapseFabs();
                } else
                {
                    binding.eventFab.setExpanded(true);
                    expandFabs();
                }
            }
        });

        final long eventId = getIntent().getLongExtra("eventId", 0);
        final int calendarId = getIntent().getIntExtra("calendarId", 0);
        final long begin = getIntent().getLongExtra("begin", 0);
        final long end = getIntent().getLongExtra("end", 0);

        Bundle bundle = new Bundle();
        bundle.putInt("calendarId", calendarId);
        bundle.putLong("eventId", eventId);
        bundle.putLong("begin", begin);
        bundle.putLong("end", end);

        eventFragment = new EventFragment();
        eventFragment.setArguments(bundle);
        currentFragment = eventFragment;

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.schedule_fragment_container, eventFragment, TAG_INFO).commit();

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        locationViewModel.getLocationLiveData().observe(this, new Observer<LocationDTO>()
        {
            @Override
            public void onChanged(LocationDTO locationDTO)
            {

            }
        });
    }

    private void collapseFabs()
    {
        binding.eventFab.setImageDrawable(getDrawable(R.drawable.more_icon));

        binding.removeEventFab.animate().translationY(0);
        binding.modifyEventFab.animate().translationY(0);
    }

    private void expandFabs()
    {
        binding.eventFab.setImageDrawable(getDrawable(R.drawable.close_icon));

        final float y = binding.eventFab.getTranslationY();
        final float margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, getResources().getDisplayMetrics());
        final float fabHeight = binding.eventFab.getHeight();

        binding.removeEventFab.animate().translationY(y - (fabHeight + margin));
        binding.modifyEventFab.animate().translationY(y - (fabHeight + margin) * 2);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            Fragment newFragment = null;
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            switch (item.getItemId())
            {
                case R.id.schedule_info:
                    newFragment = eventFragment;
                    break;

                case R.id.schedule_weather:
                    if (weatherFragment == null)
                    {
                        weatherFragment = new WeatherFragment(EventActivity.this);
                        fragmentTransaction.add(R.id.schedule_fragment_container, weatherFragment, TAG_WEATHER);
                    }
                    newFragment = weatherFragment;
                    break;

                case R.id.schedule_location:
                    if (placesAroundLocationFragment == null)
                    {
                        placesAroundLocationFragment = new PlacesAroundLocationFragment(EventActivity.this);
                        fragmentTransaction.add(R.id.schedule_fragment_container, placesAroundLocationFragment, TAG_LOCATION);
                    }
                    newFragment = placesAroundLocationFragment;
                    break;
            }

            //현재 표시된 프래그먼트와 변경할 프래그먼트가 같은 경우 변경하지 않음
            if (currentFragment != newFragment)
            {
                fragmentTransaction.hide(currentFragment).show(newFragment).commit();
                currentFragment = newFragment;
            } else
            {
                fragmentTransaction = null;
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
                        ContentValues event = eventFragment.getInstance();

                        intent.putExtra("calendarId", event.getAsInteger(CalendarContract.Events.CALENDAR_ID));
                        intent.putExtra("eventId", event.getAsLong(CalendarContract.Events._ID));
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
        locationViewModel.getLocation(eventFragment.getInstance().getAsInteger(CalendarContract.Events.CALENDAR_ID),
                eventFragment.getInstance().getAsLong(CalendarContract.Events._ID), resultCallback);
    }

    /**
     * 이벤트에 지정되어 있는 간단한 위치값 전달
     *
     * @return
     */
    @Override
    public boolean hasSimpleLocation()
    {
        return !eventFragment.getInstance().getAsString(CalendarContract.Events.EVENT_LOCATION).isEmpty();
    }

    /**
     * 날씨, 주변 정보들을 표시하기 위해 지정한 상세 위치 정보를 가지고 있는지 확인
     *
     * @param resultCallback
     */
    @Override
    public void hasDetailLocation(CarrierMessagingService.ResultCallback<Boolean> resultCallback)
    {
        locationViewModel.hasDetailLocation(eventFragment.getInstance().getAsInteger(CalendarContract.Events.CALENDAR_ID),
                eventFragment.getInstance().getAsLong(CalendarContract.Events._ID), resultCallback);
    }
}