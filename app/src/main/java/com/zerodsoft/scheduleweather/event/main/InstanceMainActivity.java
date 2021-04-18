package com.zerodsoft.scheduleweather.event.main;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.service.carrier.CarrierMessagingService;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.databinding.InstanceMainActivityBinding;
import com.zerodsoft.scheduleweather.event.common.MLocActivityNaver;
import com.zerodsoft.scheduleweather.event.common.ReselectDetailLocationNaver;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.event.activity.InstanceActivity;
import com.zerodsoft.scheduleweather.event.foods.activity.FoodsActivity;
import com.zerodsoft.scheduleweather.event.places.activity.PlacesActivity;
import com.zerodsoft.scheduleweather.event.places.selectedlocation.SelectedLocationMapFragmentKakao;
import com.zerodsoft.scheduleweather.event.places.selectedlocation.SelectedLocationMapFragmentNaver;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.event.weather.activity.WeatherActivity;
import com.zerodsoft.scheduleweather.kakaomap.model.CoordToAddressUtil;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import java.util.Calendar;

public class InstanceMainActivity extends AppCompatActivity
{
    //request
    public static final int REQUEST_SELECT_LOCATION = 1000;
    public static final int REQUEST_RESELECT_LOCATION = 1100;
    public static final int REQUEST_DELETE_EVENT = 1200;
    public static final int REQUEST_EXCEPT_THIS_INSTANCE = 1300;
    public static final int REQUEST_SUBSEQUENT_INCLUDING_THIS = 1400;

    //result
    public static final int RESULT_SELECTED_LOCATION = 2000;
    public static final int RESULT_RESELECTED_LOCATION = 2100;
    public static final int RESULT_REMOVED_LOCATION = 2200;

    public static final int RESULT_REMOVED_EVENT = 3000;
    public static final int RESULT_EXCEPTED_INSTANCE = 3100;
    public static final int RESULT_UPDATED_INSTANCE = 3200;

    public static final int RESULT_EDITED_PLACE_CATEGORY = 4000;

    public static final int RESULT_UPDATED_VALUE = 5000;


    private InstanceMainActivityBinding binding;
    private CalendarViewModel calendarViewModel;
    private LocationViewModel locationViewModel;

    private Long instanceId;
    private Integer calendarId;
    private Long eventId;
    private Long originalBegin;
    private Long originalEnd;

    private ContentValues instance;
    private LocationDTO selectedLocationDto;
    private CoordToAddress coordToAddress;
    private SelectedLocationMapFragmentKakao selectedLocationMapFragmentKakao;
    private SelectedLocationMapFragmentNaver selectedLocationMapFragmentNaver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.instance_main_activity);

        binding.cardviewInstanceInfo.setOnClickListener(instanceCardOnClickListener);
        binding.cardviewMap.setOnClickListener(placesCardOnClickListener);
        binding.cardviewWeather.setOnClickListener(weatherCardOnClickListener);
        binding.cardviewFood.setOnClickListener(foodsCardOnClickListener);

        instanceId = getIntent().getLongExtra("instanceId", 0);
        calendarId = getIntent().getIntExtra("calendarId", 0);
        eventId = getIntent().getLongExtra("eventId", 0);
        originalBegin = getIntent().getLongExtra("begin", 0);
        originalEnd = getIntent().getLongExtra("end", 0);

        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        //인스턴스의 일부 정보(title, description, begin, end)를 표시한다.
        setSimpleInstanceData();
        setLocationData();
    }


    private void setSimpleInstanceData()
    {
        instance = calendarViewModel.getInstance(calendarId, instanceId, originalBegin, originalEnd);

        //title
        if (instance.getAsString(CalendarContract.Instances.TITLE) != null)
        {
            if (!instance.getAsString(CalendarContract.Instances.TITLE).isEmpty())
            {
                binding.instanceTitleTextview.setText(instance.getAsString(CalendarContract.Instances.TITLE));
            } else
            {
                binding.instanceTitleTextview.setText(getString(R.string.empty_title));
            }
        } else
        {
            binding.instanceTitleTextview.setText(getString(R.string.empty_title));
        }

        //description
        if (instance.getAsString(CalendarContract.Instances.DESCRIPTION) != null)
        {
            if (!instance.getAsString(CalendarContract.Instances.DESCRIPTION).isEmpty())
            {
                binding.instanceDescriptionTextview.setText(instance.getAsString(CalendarContract.Instances.DESCRIPTION));
                binding.instanceDescriptionTextview.setVisibility(View.VISIBLE);
            } else
            {
                binding.instanceDescriptionTextview.setVisibility(View.GONE);
            }
        } else
        {
            binding.instanceDescriptionTextview.setVisibility(View.GONE);
        }

        //begin, end
        final boolean allDay = instance.getAsBoolean(CalendarContract.Instances.ALL_DAY);
        long allDayBegin = 0L;
        long allDayEnd = 0L;

        if (allDay)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(originalBegin);
            calendar.add(Calendar.HOUR_OF_DAY, -9);
            allDayBegin = calendar.getTimeInMillis();

            calendar.setTimeInMillis(originalEnd);
            calendar.add(Calendar.HOUR_OF_DAY, -9);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            allDayEnd = calendar.getTimeInMillis();
        }
        String beginStr = EventUtil.convertDateTime(allDay ? allDayBegin : originalBegin, allDay, App.isPreference_key_using_24_hour_system());
        String endStr = EventUtil.convertDateTime(allDay ? allDayEnd : originalEnd, allDay, App.isPreference_key_using_24_hour_system());

        binding.instanceBeginTextview.setText(beginStr);
        binding.instanceEndTextview.setText(endStr);
    }

    private void setLocationData()
    {
        if (hasSimpleLocation())
        {
            //인스턴스내에 선택된 위치값 표시
            binding.locationTextview.setText(instance.getAsString(CalendarContract.Instances.EVENT_LOCATION));

            locationViewModel.hasDetailLocation(calendarId, eventId, new CarrierMessagingService.ResultCallback<Boolean>()
            {
                @Override
                public void onReceiveResult(@NonNull Boolean hasDetailLocation) throws RemoteException
                {
                    if (hasDetailLocation)
                    {
                        locationViewModel.getLocation(calendarId, eventId, new CarrierMessagingService.ResultCallback<LocationDTO>()
                        {
                            @Override
                            public void onReceiveResult(@NonNull LocationDTO location) throws RemoteException
                            {
                                if (location.getId() >= 0)
                                {
                                    selectedLocationDto = location;

                                    LocalApiPlaceParameter localApiPlaceParameter = new LocalApiPlaceParameter();
                                    localApiPlaceParameter.setX(String.valueOf(location.getLongitude()));
                                    localApiPlaceParameter.setY(String.valueOf(location.getLatitude()));

                                    CoordToAddressUtil.coordToAddress(localApiPlaceParameter, new CarrierMessagingService.ResultCallback<DataWrapper<CoordToAddress>>()
                                    {
                                        @Override
                                        public void onReceiveResult(@NonNull DataWrapper<CoordToAddress> coordToAddressDataWrapper) throws RemoteException
                                        {
                                            runOnUiThread(new Runnable()
                                            {
                                                @Override
                                                public void run()
                                                {
                                                    binding.detailLocationLayout.setVisibility(View.VISIBLE);

                                                    if (coordToAddressDataWrapper.getException() == null)
                                                    {
                                                        coordToAddress = coordToAddressDataWrapper.getData();
                                                    }

                                                    if (selectedLocationDto.getPlaceName() != null)
                                                    {
                                                        //장소와 주소 표기
                                                        binding.placeNameTextview.setText(selectedLocationDto.getPlaceName());
                                                        binding.placeAddressTextview.setText(coordToAddress.getCoordToAddressDocuments().get(0)
                                                                .getCoordToAddressAddress().getAddressName());

                                                        binding.placeNameTextview.setVisibility(View.VISIBLE);
                                                        binding.placeAddressTextview.setVisibility(View.VISIBLE);
                                                        binding.addressTextview.setVisibility(View.GONE);
                                                    } else
                                                    {
                                                        //주소 표기
                                                        binding.addressTextview.setText(coordToAddress.getCoordToAddressDocuments().get(0)
                                                                .getCoordToAddressAddress().getAddressName());

                                                        binding.placeNameTextview.setVisibility(View.GONE);
                                                        binding.placeAddressTextview.setVisibility(View.GONE);
                                                        binding.addressTextview.setVisibility(View.VISIBLE);
                                                    }

                                                    //mapview생성
                                                    addSelectedLocationMap();
                                                }
                                            });

                                        }
                                    });


                                }
                            }
                        });
                    } else
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                binding.detailLocationLayout.setVisibility(View.GONE);
                                binding.locationTextview.setText(instance.getAsString(CalendarContract.Instances.EVENT_LOCATION));
                            }
                        });
                    }
                }
            });
        } else
        {
            binding.detailLocationLayout.setVisibility(View.GONE);
            binding.locationTextview.setText(getString(R.string.not_selected_location_in_event));
        }
    }


    private void startActivityUsingLocation(Intent intent, ActivityResultLauncher<Intent> activityResultLauncher)
    {
        locationViewModel.hasDetailLocation(calendarId, eventId, new CarrierMessagingService.ResultCallback<Boolean>()
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
                            removeSelectedLocationMap();
                            activityResultLauncher.launch(intent);
                        } else
                        {
                            locationAbstract.showSetLocationDialog(InstanceMainActivity.this, setLocationActivityResultLauncher, instance);
                        }
                    }
                });

            }
        });
    }

    private void removeSelectedLocationMap()
    {
        /*
        kakao
         if (selectedLocationMapFragmentKakao != null)
        {
            getSupportFragmentManager().beginTransaction().remove(selectedLocationMapFragmentKakao).commit();
            selectedLocationMapFragmentKakao = null;
        }
         */

        //naver
        if (selectedLocationMapFragmentNaver != null)
        {
            getSupportFragmentManager().beginTransaction().remove(selectedLocationMapFragmentNaver).commit();
            selectedLocationMapFragmentNaver = null;
        }
    }

    private void addSelectedLocationMap()
    {
        if (selectedLocationDto != null)
        {
            /*
            kakao
                 selectedLocationMapFragmentKakao = new SelectedLocationMapFragmentKakao(selectedLocationDto);
            getSupportFragmentManager().beginTransaction().add(binding.selectedLocationMap.getId(),
                    selectedLocationMapFragmentKakao, SelectedLocationMapFragmentKakao.TAG).commit();
             */

            //naver
            selectedLocationMapFragmentNaver = new SelectedLocationMapFragmentNaver(selectedLocationDto);
            getSupportFragmentManager().beginTransaction().add(binding.selectedLocationMap.getId(),
                    selectedLocationMapFragmentNaver, SelectedLocationMapFragmentNaver.TAG).commit();
        }
    }

    public boolean hasSimpleLocation()
    {
        boolean result = false;

        if (instance.getAsString(CalendarContract.Instances.EVENT_LOCATION) != null)
        {
            result = !instance.getAsString(CalendarContract.Instances.EVENT_LOCATION).isEmpty();
        }
        return result;
    }

    private final View.OnClickListener instanceCardOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            removeSelectedLocationMap();

            Intent intent = new Intent(InstanceMainActivity.this, InstanceActivity.class);

            Bundle bundle = new Bundle();

            bundle.putLong("instanceId", instanceId);
            bundle.putLong("eventId", eventId);
            bundle.putInt("calendarId", calendarId);
            bundle.putLong("begin", originalBegin);
            bundle.putLong("end", originalEnd);

            intent.putExtras(bundle);
            instanceActivityResultLauncher.launch(intent);
        }
    };

    private final View.OnClickListener weatherCardOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (hasSimpleLocation())
            {
                Intent intent = new Intent(InstanceMainActivity.this, WeatherActivity.class);
                Bundle bundle = new Bundle();

                bundle.putLong("instanceId", instanceId);
                bundle.putLong("eventId", eventId);
                bundle.putInt("calendarId", calendarId);
                bundle.putLong("begin", originalBegin);

                intent.putExtras(bundle);
                startActivityUsingLocation(intent, weatherActivityResultLauncher);
            } else
            {
                Toast.makeText(InstanceMainActivity.this, getString(R.string.not_selected_location_in_event), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final View.OnClickListener placesCardOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (hasSimpleLocation())
            {
                Intent intent = new Intent(InstanceMainActivity.this, PlacesActivity.class);
                Bundle bundle = new Bundle();

                bundle.putLong("instanceId", instanceId);
                bundle.putLong("eventId", eventId);
                bundle.putInt("calendarId", calendarId);
                bundle.putLong("begin", originalBegin);

                intent.putExtras(bundle);
                startActivityUsingLocation(intent, mapActivityResultLauncher);
            } else
            {
                Toast.makeText(InstanceMainActivity.this, getString(R.string.not_selected_location_in_event), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final View.OnClickListener foodsCardOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (hasSimpleLocation())
            {
                Intent intent = new Intent(InstanceMainActivity.this, FoodsActivity.class);
                Bundle bundle = new Bundle();

                bundle.putLong(CalendarContract.Instances._ID, instanceId);
                bundle.putLong(CalendarContract.Instances.EVENT_ID, eventId);
                bundle.putInt(CalendarContract.Instances.CALENDAR_ID, calendarId);

                intent.putExtras(bundle);
                foodsActivityResultLauncher.launch(intent);
            } else
            {
                Toast.makeText(InstanceMainActivity.this, getString(R.string.not_selected_location_in_event), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final ActivityResultLauncher<Intent> instanceActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    switch (result.getResultCode())
                    {
                        case RESULT_REMOVED_EVENT:
                        case RESULT_EXCEPTED_INSTANCE:
                            setResult(result.getResultCode());
                            finish();
                            break;
                        case RESULT_UPDATED_VALUE:
                            setSimpleInstanceData();
                            setLocationData();
                            break;
                        case RESULT_CANCELED:
                            addSelectedLocationMap();
                            break;
                    }
                }
            });

    private final ActivityResultLauncher<Intent> mapActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    addSelectedLocationMap();
                }
            });

    private final ActivityResultLauncher<Intent> weatherActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    addSelectedLocationMap();
                }
            });

    private final ActivityResultLauncher<Intent> foodsActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    addSelectedLocationMap();
                }
            });


    private final ActivityResultLauncher<Intent> setLocationActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() == RESULT_SELECTED_LOCATION)
                    {
                        Toast.makeText(InstanceMainActivity.this, result.getData().getStringExtra("selectedLocationName"), Toast.LENGTH_SHORT).show();
                        setLocationData();
                    } else
                    {
                        // 취소, 이벤트 정보 프래그먼트로 돌아감
                    }

                }
            });

    private final ActivityResultLauncher<Intent> editLocationActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    switch (result.getResultCode())
                    {
                        case RESULT_RESELECTED_LOCATION:
                            String newLocation = result.getData().getStringExtra("selectedLocationName");
                            setLocationData();
                            Toast.makeText(InstanceMainActivity.this, newLocation + " 변경완료", Toast.LENGTH_SHORT).show();
                            break;
                        case RESULT_REMOVED_LOCATION:
                            Toast.makeText(InstanceMainActivity.this, "위치 삭제완료", Toast.LENGTH_SHORT).show();
                            setLocationData();
                            break;
                    }
                }
            });

    private final LocationAbstract locationAbstract = new LocationAbstract()
    {
        @Override
        public void onRequestedActivity()
        {
            removeSelectedLocationMap();
        }
    };

    public abstract static class LocationAbstract
    {
        public void showSetLocationDialog(Activity activity, ActivityResultLauncher<Intent> activityResultLauncher, ContentValues instance)
        {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity)
                    .setTitle(activity.getString(R.string.request_select_location_title))
                    .setMessage(activity.getString(R.string.request_select_location_description))
                    .setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            dialogInterface.cancel();
                        }
                    })
                    .setPositiveButton(activity.getString(R.string.check), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            onRequestedActivity();
                          /* kakao
                            Intent intent = new Intent(activity, MLocActivityKakao.class);
                           */
                            Intent intent = new Intent(activity, MLocActivityNaver.class);
                            Bundle bundle = new Bundle();

                            bundle.putInt("calendarId", instance.getAsInteger(CalendarContract.Instances.CALENDAR_ID));
                            bundle.putLong("eventId", instance.getAsLong(CalendarContract.Instances.EVENT_ID));
                            bundle.putString("location", instance.getAsString(CalendarContract.Instances.EVENT_LOCATION));
                            bundle.putString("ownerAccount", instance.getAsString(CalendarContract.Instances.OWNER_ACCOUNT));

                            intent.putExtras(bundle);
                            activityResultLauncher.launch(intent);
                            dialogInterface.dismiss();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        public void startEditLocationActivity(Activity activity, ActivityResultLauncher<Intent> activityResultLauncher, LocationDTO locationDTO)
        {
            onRequestedActivity();
            /* kakao
            Intent intent = new Intent(activity, ReselectDetailLocationKakao.class);
             */

            //naver
            Intent intent = new Intent(activity, ReselectDetailLocationNaver.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("savedLocationDto", locationDTO);

            intent.putExtras(bundle);
            activityResultLauncher.launch(intent);
        }

        public abstract void onRequestedActivity();
    }
}
