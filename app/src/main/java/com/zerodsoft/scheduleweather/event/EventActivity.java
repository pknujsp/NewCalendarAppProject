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
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.zerodsoft.scheduleweather.activity.main.AppMainActivity;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IstartActivity;
import com.zerodsoft.scheduleweather.databinding.ActivityScheduleInfoBinding;
import com.zerodsoft.scheduleweather.etc.AppPermission;
import com.zerodsoft.scheduleweather.etc.IPermission;
import com.zerodsoft.scheduleweather.event.common.MLocActivity;
import com.zerodsoft.scheduleweather.event.common.ReselectDetailLocation;
import com.zerodsoft.scheduleweather.event.common.interfaces.IFab;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocation;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.event.EventFragment;
import com.zerodsoft.scheduleweather.event.location.placefragments.fragment.PlacesFragment;
import com.zerodsoft.scheduleweather.event.location.placefragments.fragment.PlacesTransactionFragment;
import com.zerodsoft.scheduleweather.event.weather.WeatherFragment;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.RecurrenceRule;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class EventActivity extends AppCompatActivity implements ILocation, IFab, IPermission, IstartActivity
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
    private AppPermission appPermission;

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
        appPermission = new AppPermission(this);

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

        binding.selectDetailLocationFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
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
                                    locationViewModel.getLocation(calendarId, eventId, new CarrierMessagingService.ResultCallback<LocationDTO>()
                                    {
                                        @Override
                                        public void onReceiveResult(@NonNull LocationDTO locationDTO) throws RemoteException
                                        {
                                            if (!locationDTO.isEmpty())
                                            {
                                                Intent intent = new Intent(EventActivity.this, ReselectDetailLocation.class);
                                                intent.putExtra("savedLocationDto", locationDTO);
                                                startActivityForResult(intent, REQUEST_SELECT_LOCATION);
                                            }
                                        }
                                    });
                                } else
                                {
                                    clickedFragmentTag = TAG_INFO;
                                    showRequestLocDialog();
                                }
                            }
                        });


                    }
                });


            }
        });

        binding.modifyEventFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /*
                Intent intent = new Intent(EventActivity.this, EditEventActivity.class);
                intent.putExtra("requestCode", EventDataController.MODIFY_EVENT);
                intent.putExtra("calendarId", calendarId.intValue());
                intent.putExtra("eventId", eventId.longValue());

                startActivity(intent);

                 */
                Toast.makeText(EventActivity.this, "작성 중", Toast.LENGTH_SHORT).show();
            }
        });

        binding.removeEventFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                String[] items = null;
                //이번 일정만 삭제, 향후 모든 일정 삭제, 모든 일정 삭제
                /*
                반복없는 이벤트 인 경우 : 일정 삭제
                반복있는 이벤트 인 경우 : 이번 일정만 삭제, 향후 모든 일정 삭제, 모든 일정 삭제
                 */
                ContentValues instance = eventFragment.getInstance();
                if (instance.getAsString(CalendarContract.Instances.RRULE) != null)
                {
                    items = new String[]{getString(R.string.remove_this_instance), getString(R.string.remove_all_future_instance_including_current_instance)
                            , getString(R.string.remove_event)};
                } else
                {
                    items = new String[]{getString(R.string.remove_event)};
                }
                new MaterialAlertDialogBuilder(EventActivity.this).setTitle(getString(R.string.remove_event))
                        .setItems(items, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int index)
                            {
                                if (eventFragment.getInstance().getAsString(CalendarContract.Instances.RRULE) != null)
                                {
                                    switch (index)
                                    {
                                        case 0:
                                            // 이번 일정만 삭제
                                            // 완성
                                            exceptThisInstance();
                                            break;
                                        case 1:
                                            // 향후 모든 일정만 삭제
                                            // deleteSubsequentIncludingThis();
                                            Toast.makeText(EventActivity.this, "작성 중", Toast.LENGTH_SHORT).show();
                                            break;
                                        case 2:
                                            // 모든 일정 삭제
                                            deleteEvent();
                                            break;
                                    }
                                } else
                                {
                                    switch (index)
                                    {
                                        case 0:
                                            // 모든 일정 삭제
                                            deleteEvent();
                                            break;
                                    }
                                }
                            }
                        }).create().show();
            }
        });

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
        calendarViewModel.init(getApplicationContext());
    }

    private void deleteEvent()
    {
        // 참석자 - 알림 - 이벤트 순으로 삭제 (외래키 때문)
        // db column error
        if (grantedPermissions(REQUEST_DELETE_EVENT, Manifest.permission.WRITE_CALENDAR))
        {
            calendarViewModel.deleteEvent(calendarId, eventId);
            // 삭제 완료 후 캘린더 화면으로 나가고, 새로고침한다.
            setResult(AppMainActivity.DELETED_EVENT);
            finish();
        }
    }

    private void deleteSubsequentIncludingThis()
    {
        // 이벤트의 반복 UNTIL을 현재 인스턴스의 시작날짜로 수정
        if (!grantedPermissions(REQUEST_SUBSEQUENT_INCLUDING_THIS, Manifest.permission.WRITE_CALENDAR))
        {
            return;
        }
        ContentValues recurrenceData = calendarViewModel.getRecurrence(calendarId, eventId);
        RecurrenceRule recurrenceRule = new RecurrenceRule();
        recurrenceRule.separateValues(recurrenceData.getAsString(CalendarContract.Events.RRULE));

        GregorianCalendar calendar = new GregorianCalendar();
        final long thisInstanceBegin = eventFragment.getInstance().getAsLong(CalendarContract.Instances.BEGIN);
        calendar.setTimeInMillis(thisInstanceBegin);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        recurrenceRule.putValue(RecurrenceRule.UNTIL, ClockUtil.yyyyMMdd.format(calendar.getTime()));
        recurrenceRule.removeValue(RecurrenceRule.INTERVAL);

        recurrenceData.put(CalendarContract.Events.RRULE, recurrenceRule.getRule());
        calendarViewModel.updateEvent(recurrenceData);
    }

    private void exceptThisInstance()
    {
        if (grantedPermissions(REQUEST_EXCEPT_THIS_INSTANCE, Manifest.permission.WRITE_CALENDAR))
        {
            ContentValues instance = eventFragment.getInstance();
            calendarViewModel.deleteInstance(instance.getAsLong(CalendarContract.Instances.BEGIN), eventId);

            setResult(AppMainActivity.EXCEPTED_INSTANCE);
            finish();
        }
    }


    private void collapseFabs()
    {
        binding.eventFab.setImageDrawable(getDrawable(R.drawable.more_icon));

        binding.removeEventFab.animate().translationY(0);
        binding.modifyEventFab.animate().translationY(0);
        binding.selectDetailLocationFab.animate().translationY(0);
    }


    private void expandFabs()
    {
        binding.eventFab.setImageDrawable(getDrawable(R.drawable.close_icon));

        final float y = binding.eventFab.getTranslationY();
        final float margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, getResources().getDisplayMetrics());
        final float fabHeight = binding.eventFab.getHeight();

        binding.removeEventFab.animate().translationY(y - (fabHeight + margin));
        binding.modifyEventFab.animate().translationY(y - (fabHeight + margin) * 2);
        binding.selectDetailLocationFab.animate().translationY(y - (fabHeight + margin) * 3);
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
                    setFabs(TAG_INFO);
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
                                                    setFabs(TAG_WEATHER);
                                                    newFragment = weatherFragment;
                                                    break;

                                                case R.id.schedule_location:
                                                    if (placesTransactionFragment == null)
                                                    {
                                                        placesTransactionFragment = new PlacesTransactionFragment(EventActivity.this);
                                                        fragmentTransaction.add(R.id.schedule_fragment_container, placesTransactionFragment, TAG_LOCATION);
                                                    }
                                                    setFabs(TAG_LOCATION);
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

    @Override
    public void setAllVisibility(int visibility)
    {
        binding.eventFab.setVisibility(visibility);
        binding.removeEventFab.setVisibility(visibility);
        binding.modifyEventFab.setVisibility(visibility);
        binding.selectDetailLocationFab.setVisibility(visibility);
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
            case IFab.TYPE_SELECT_LOCATION:
                binding.selectDetailLocationFab.setVisibility(visibility);
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
            case IFab.TYPE_SELECT_LOCATION:
                visibility = binding.selectDetailLocationFab.getVisibility();
                break;
        }
        return visibility;
    }

    private void setFabs(String fragmentTag)
    {
        switch (fragmentTag)
        {
            case TAG_INFO:
                if (eventFragment.getInstance().getAsString(CalendarContract.Instances.EVENT_LOCATION).isEmpty())
                {
                    setVisibility(IFab.TYPE_SELECT_LOCATION, View.GONE);
                    setVisibility(IFab.TYPE_MAIN, View.VISIBLE);
                    setVisibility(IFab.TYPE_REMOVE_EVENT, View.VISIBLE);
                    setVisibility(IFab.TYPE_MODIFY_EVENT, View.VISIBLE);
                } else
                {
                    setAllVisibility(View.VISIBLE);
                }
                break;

            default:
                setAllVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void requestPermissions(int requestCode, String... permissions)
    {
        appPermission.requestPermissions(requestCode, permissions);
    }

    @Override
    public boolean grantedPermissions(int requestCode, String... permissions)
    {
        return appPermission.grantedPermissions(requestCode, permissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            // 권한 허용됨
            switch (requestCode)
            {
                case REQUEST_DELETE_EVENT:
                    deleteEvent();
                    break;
                case REQUEST_EXCEPT_THIS_INSTANCE:
                    exceptThisInstance();
                    break;
                case REQUEST_SUBSEQUENT_INCLUDING_THIS:
                    deleteSubsequentIncludingThis();
                    break;
            }
        } else
        {
            // 권한 거부됨
        }

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