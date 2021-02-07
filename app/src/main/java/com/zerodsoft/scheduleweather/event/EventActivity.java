package com.zerodsoft.scheduleweather.event;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.service.carrier.CarrierMessagingService;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.ActivityScheduleInfoBinding;
import com.zerodsoft.scheduleweather.event.common.MLocActivity;
import com.zerodsoft.scheduleweather.event.common.interfaces.IFab;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocation;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.event.EventFragment;
import com.zerodsoft.scheduleweather.event.location.PlacesAroundLocationFragment;
import com.zerodsoft.scheduleweather.event.weather.WeatherFragment;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public class EventActivity extends AppCompatActivity implements ILocation, IFab
{
    private ActivityScheduleInfoBinding binding;
    private LocationViewModel locationViewModel;

    private EventFragment eventFragment;
    private WeatherFragment weatherFragment;
    private PlacesAroundLocationFragment placesAroundLocationFragment;

    private OnBackPressedCallback onBackPressedCallback;

    private FragmentManager fragmentManager;
    private Fragment currentFragment;

    private static final String TAG_INFO = "info";
    private static final String TAG_WEATHER = "weather";
    private static final String TAG_LOCATION = "location";
    private String clickedFragmentTag;

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
        fragmentManager = getSupportFragmentManager();

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

        final long instanceId = getIntent().getLongExtra("instanceId", 0);
        final int calendarId = getIntent().getIntExtra("calendarId", 0);
        final long begin = getIntent().getLongExtra("begin", 0);
        final long end = getIntent().getLongExtra("end", 0);

        Bundle bundle = new Bundle();
        bundle.putInt("calendarId", calendarId);
        bundle.putLong("instanceId", instanceId);
        bundle.putLong("begin", begin);
        bundle.putLong("end", end);

        eventFragment = new EventFragment(this);
        eventFragment.setArguments(bundle);

        binding.scheduleBottomNav.setSelectedItemId(R.id.schedule_info);

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
        binding.reselectLocationFab.animate().translationY(0);
    }

    private void expandFabs()
    {
        binding.eventFab.setImageDrawable(getDrawable(R.drawable.close_icon));

        final float y = binding.eventFab.getTranslationY();
        final float margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, getResources().getDisplayMetrics());
        final float fabHeight = binding.eventFab.getHeight();

        binding.removeEventFab.animate().translationY(y - (fabHeight + margin));
        binding.modifyEventFab.animate().translationY(y - (fabHeight + margin) * 2);
        binding.reselectLocationFab.animate().translationY(y - (fabHeight + margin) * 3);
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
                    if (currentFragment == null)
                    {
                        currentFragment = eventFragment;
                        fragmentTransaction.add(R.id.schedule_fragment_container, eventFragment, TAG_INFO).commit();
                        return true;
                    }
                    newFragment = eventFragment;
                    if (eventFragment.getInstance().getAsString(CalendarContract.Instances.EVENT_LOCATION).isEmpty())
                    {
                        setVisibility(IFab.TYPE_RESELECT_LOCATION, View.GONE);
                        setVisibility(IFab.TYPE_MAIN, View.VISIBLE);
                        setVisibility(IFab.TYPE_REMOVE_EVENT, View.VISIBLE);
                        setVisibility(IFab.TYPE_MODIFY_EVENT, View.VISIBLE);
                    } else
                    {
                        setAllVisibility(View.VISIBLE);
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

                default:
                    if (!hasSimpleLocation())
                    {
                        // 이벤트에서 위치가 지정되지 않음
                        Toast.makeText(EventActivity.this, getString(R.string.not_selected_location_in_event), Toast.LENGTH_SHORT).show();
                        return false;
                    } else
                    {
                        hasDetailLocation(new CarrierMessagingService.ResultCallback<Boolean>()
                        {
                            @Override
                            public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException
                            {
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        if (aBoolean)
                                        {
                                            binding.scheduleBottomNav.getMenu().findItem(item.getItemId()).setChecked(true);

                                            Fragment newFragment = null;
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                            switch (item.getItemId())
                                            {
                                                case R.id.schedule_weather:
                                                    if (weatherFragment == null)
                                                    {
                                                        weatherFragment = new WeatherFragment(EventActivity.this);
                                                        fragmentTransaction.add(R.id.schedule_fragment_container, weatherFragment, TAG_WEATHER);
                                                    }
                                                    newFragment = weatherFragment;
                                                    setAllVisibility(View.GONE);
                                                    break;
                                                case R.id.schedule_location:
                                                    if (placesAroundLocationFragment == null)
                                                    {
                                                        placesAroundLocationFragment = new PlacesAroundLocationFragment(EventActivity.this);
                                                        fragmentTransaction.add(R.id.schedule_fragment_container, placesAroundLocationFragment, TAG_LOCATION);
                                                    }
                                                    newFragment = placesAroundLocationFragment;
                                                    setAllVisibility(View.GONE);
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

                                        } else
                                        {
                                            switch (item.getItemId())
                                            {
                                                case R.id.schedule_weather:
                                                    clickedFragmentTag = TAG_WEATHER;
                                                    break;
                                                case R.id.schedule_location:
                                                    clickedFragmentTag = TAG_LOCATION;
                                                    break;
                                            }
                                            showRequestLocDialog();
                                        }
                                    }
                                });

                            }
                        });
                    }

                    return false;
            }

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case MLocActivity.REQUEST_SELECT_LOCATION:
            {
                if (resultCode == MLocActivity.RESULT_SELECTED_LOCATION)
                {
                    switch (clickedFragmentTag)
                    {
                        case TAG_WEATHER:
                            binding.scheduleBottomNav.setSelectedItemId(R.id.schedule_weather);
                            break;
                        case TAG_LOCATION:
                            binding.scheduleBottomNav.setSelectedItemId(R.id.schedule_location);
                            break;
                    }
                } else
                {
                    // 취소, 이벤트 정보 프래그먼트로 돌아감
                }
            }
        }
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
                        ContentValues instance = eventFragment.getInstance();

                        intent.putExtra("calendarId", instance.getAsInteger(CalendarContract.Instances.CALENDAR_ID));
                        intent.putExtra("eventId", instance.getAsLong(CalendarContract.Instances.EVENT_ID));
                        intent.putExtra("location", instance.getAsString(CalendarContract.Instances.EVENT_LOCATION));
                        intent.putExtra("ownerAccount", instance.getAsString(CalendarContract.Instances.OWNER_ACCOUNT));

                        startActivityForResult(intent, MLocActivity.REQUEST_SELECT_LOCATION);
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
        locationViewModel.getLocation(eventFragment.getInstance().getAsInteger(CalendarContract.Instances.CALENDAR_ID),
                eventFragment.getInstance().getAsLong(CalendarContract.Instances.EVENT_ID), resultCallback);
    }

    /**
     * 이벤트에 지정되어 있는 간단한 위치값 전달
     *
     * @return
     */
    @Override
    public boolean hasSimpleLocation()
    {
        return !eventFragment.getInstance().getAsString(CalendarContract.Instances.EVENT_LOCATION).isEmpty();
    }

    /**
     * 날씨, 주변 정보들을 표시하기 위해 지정한 상세 위치 정보를 가지고 있는지 확인
     *
     * @param resultCallback
     */
    @Override
    public void hasDetailLocation(CarrierMessagingService.ResultCallback<Boolean> resultCallback)
    {
        locationViewModel.hasDetailLocation(eventFragment.getInstance().getAsInteger(CalendarContract.Instances.CALENDAR_ID),
                eventFragment.getInstance().getAsLong(CalendarContract.Instances.EVENT_ID), resultCallback);
    }

    @Override
    public void setAllVisibility(int visibility)
    {
        binding.eventFab.setVisibility(visibility);
        binding.removeEventFab.setVisibility(visibility);
        binding.modifyEventFab.setVisibility(visibility);
        binding.reselectLocationFab.setVisibility(visibility);
    }

    @Override
    public void setVisibility(int type, int visibility)
    {
        switch (type)
        {
            case IFab.TYPE_MAIN:
                binding.eventFab.setVisibility(visibility);
                break;
            case IFab.TYPE_REMOVE_EVENT:
                binding.removeEventFab.setVisibility(visibility);
                break;
            case IFab.TYPE_MODIFY_EVENT:
                binding.modifyEventFab.setVisibility(visibility);
                break;
            case IFab.TYPE_RESELECT_LOCATION:
                binding.reselectLocationFab.setVisibility(visibility);
                break;
        }
    }


    @Override
    public int getVisibility(int type)
    {
        int visibility = 0;

        switch (type)
        {
            case IFab.TYPE_MAIN:
                visibility = binding.eventFab.getVisibility();
                break;
            case IFab.TYPE_REMOVE_EVENT:
                visibility = binding.removeEventFab.getVisibility();
                break;
            case IFab.TYPE_MODIFY_EVENT:
                visibility = binding.modifyEventFab.getVisibility();
                break;
            case IFab.TYPE_RESELECT_LOCATION:
                visibility = binding.reselectLocationFab.getVisibility();
                break;
        }
        return visibility;
    }
}