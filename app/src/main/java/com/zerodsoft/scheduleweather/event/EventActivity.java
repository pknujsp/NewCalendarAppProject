package com.zerodsoft.scheduleweather.event;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.service.carrier.CarrierMessagingService;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IstartActivity;
import com.zerodsoft.scheduleweather.databinding.ActivityScheduleInfoBinding;
import com.zerodsoft.scheduleweather.event.common.MLocActivity;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocation;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.event.EventFragment;
import com.zerodsoft.scheduleweather.event.places.fragment.PlacesTransactionFragment;
import com.zerodsoft.scheduleweather.event.weather.WeatherFragment;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public class EventActivity extends AppCompatActivity implements ILocation, IstartActivity
{
    private ActivityScheduleInfoBinding binding;
    private LocationViewModel locationViewModel;
    private CalendarViewModel calendarViewModel;

    private EventFragment eventFragment;
    private WeatherFragment weatherFragment;
    private PlacesTransactionFragment placesTransactionFragment;

    private OnBackPressedCallback onBackPressedCallback;

    private FragmentManager fragmentManager;
    private Fragment currentFragment;

    private static final String TAG_INFO = "info";
    private static final String TAG_WEATHER = "weather";
    private static final String TAG_LOCATION = "location";

    public static final int REQUEST_SELECT_LOCATION = 3000;
    public static final int REQUEST_RESELECT_LOCATION = 3100;

    public static final int RESULT_SELECTED_LOCATION = 3200;
    public static final int RESULT_RESELECTED_LOCATION = 3300;
    public static final int RESULT_REMOVED_LOCATION = 3400;

    public static final int REQUEST_DELETE_EVENT = 5000;
    public static final int REQUEST_EXCEPT_THIS_INSTANCE = 5100;
    public static final int REQUEST_SUBSEQUENT_INCLUDING_THIS = 5200;
    public static final int RESULT_EDITED_PLACE_CATEGORY = 6000;


    private String clickedFragmentTag;

    private Integer calendarId;
    private Long eventId;
    private Long instanceId;

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


        instanceId = getIntent().getLongExtra("instanceId", 0);
        calendarId = getIntent().getIntExtra("calendarId", 0);
        eventId = getIntent().getLongExtra("eventId", 0);
        final long begin = getIntent().getLongExtra("begin", 0);
        final long end = getIntent().getLongExtra("end", 0);

        Bundle bundle = new Bundle();
        bundle.putInt("calendarId", calendarId);
        bundle.putLong("instanceId", instanceId);
        bundle.putLong("eventId", eventId);
        bundle.putLong("begin", begin);
        bundle.putLong("end", end);

        eventFragment = new EventFragment(this);
        eventFragment.setArguments(bundle);

        binding.scheduleBottomNav.setSelectedItemId(R.id.schedule_info);

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
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
                {
                    if (currentFragment == null)
                    {
                        currentFragment = eventFragment;
                        fragmentTransaction.add(R.id.schedule_fragment_container, eventFragment, TAG_INFO).commit();
                        return true;
                    }
                    newFragment = eventFragment;

                    //현재 표시된 프래그먼트와 변경할 프래그먼트가 같은 경우 변경하지 않음
                    if (currentFragment != newFragment)
                    {
                        fragmentTransaction.hide(currentFragment).show(newFragment).commit();
                        currentFragment = newFragment;
                    } else
                    {
                        fragmentTransaction = null;
                    }
                    clickedFragmentTag = TAG_INFO;
                    return true;
                }
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
                            public void onReceiveResult(@NonNull Boolean hasDetailLocation) throws RemoteException
                            {
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        if (hasDetailLocation)
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
                                                    break;

                                                case R.id.schedule_location:
                                                    if (placesTransactionFragment == null)
                                                    {
                                                        placesTransactionFragment = new PlacesTransactionFragment(EventActivity.this);
                                                        fragmentTransaction.add(R.id.schedule_fragment_container, placesTransactionFragment, TAG_LOCATION);
                                                    }
                                                    newFragment = placesTransactionFragment;
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
            case REQUEST_SELECT_LOCATION:
            {
                if (resultCode == RESULT_SELECTED_LOCATION)
                {
                    Toast.makeText(EventActivity.this, data.getStringExtra("selectedLocationName"), Toast.LENGTH_SHORT).show();
                    switch (clickedFragmentTag)
                    {
                        case TAG_INFO:
                            // binding.scheduleBottomNav.setSelectedItemId(R.id.schedule_info);
                            break;
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
                break;
            }

            case REQUEST_RESELECT_LOCATION:
            {
                if (resultCode == RESULT_RESELECTED_LOCATION)
                {
                    Toast.makeText(EventActivity.this, data.getStringExtra("selectedLocationName"), Toast.LENGTH_SHORT).show();
                } else if (resultCode == RESULT_REMOVED_LOCATION)
                {

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
        locationViewModel.getLocation(calendarId, eventId, resultCallback);
    }


    /**
     * 이벤트에 지정되어 있는 간단한 위치값 전달
     *
     * @return
     */
    @Override
    public boolean hasSimpleLocation()
    {
        boolean result = false;

        if (eventFragment.getInstance().getAsString(CalendarContract.Instances.EVENT_LOCATION) != null)
        {
            result = !eventFragment.getInstance().getAsString(CalendarContract.Instances.EVENT_LOCATION).isEmpty();
        }
        return result;
    }


    /**
     * 날씨, 주변 정보들을 표시하기 위해 지정한 상세 위치 정보를 가지고 있는지 확인
     *
     * @param resultCallback
     */
    @Override
    public void hasDetailLocation(CarrierMessagingService.ResultCallback<Boolean> resultCallback)
    {
        locationViewModel.hasDetailLocation(calendarId, eventId, resultCallback);
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    final int resultCode = result.getResultCode();
                    if (resultCode == RESULT_EDITED_PLACE_CATEGORY || resultCode == RESULT_RESELECTED_LOCATION || resultCode == RESULT_SELECTED_LOCATION)
                    {
                        placesTransactionFragment.refresh();
                    }
                }
            }
    );

    @Override
    public void startActivityResult(Intent intent, int requestCode)
    {
        activityResultLauncher.launch(intent);
    }
}