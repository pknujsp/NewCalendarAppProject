package com.zerodsoft.scheduleweather.event.foods.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.RemoteException;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.databinding.FragmentFoodsMainBinding;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.activity.LocationSettingsActivity;
import com.zerodsoft.scheduleweather.event.foods.adapter.FoodCategoryAdapter;
import com.zerodsoft.scheduleweather.event.foods.dto.FoodCategoryItem;
import com.zerodsoft.scheduleweather.event.foods.interfaces.CriteriaLocationListener;
import com.zerodsoft.scheduleweather.event.foods.interfaces.FragmentChanger;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedCategoryItem;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.CustomFoodCategoryViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationHistoryViewModel;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.INetwork;
import com.zerodsoft.scheduleweather.kakaomap.model.CoordToAddressUtil;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.room.dto.CustomFoodCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationSearchHistoryDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.checkSelfPermission;

public class FoodsMainFragment extends Fragment implements OnClickedCategoryItem, CriteriaLocationListener, OnBackPressedCallbackController
{
    public static final String TAG = "FoodsMainFragment";
    private FragmentFoodsMainBinding binding;
    private final INetwork iNetwork;
    private final FragmentChanger fragmentChanger;

    private CustomFoodCategoryViewModel customFoodCategoryViewModel;
    private LocationViewModel locationViewModel;
    private FoodCriteriaLocationInfoViewModel foodCriteriaLocationInfoViewModel;
    private FoodCriteriaLocationHistoryViewModel foodCriteriaLocationSearchHistoryViewModel;

    public LocationManager locationManager;

    private Integer calendarId;
    private Long instanceId;
    private Long eventId;

    private LocationDTO selectedLocationDTO;
    private LocationDTO criteriaLocationDTO;
    private FoodCriteriaLocationSearchHistoryDTO foodCriteriaLocationSearchHistoryDTO;
    private FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO;

    public FoodsMainFragment(Activity activity)
    {
        this.iNetwork = (INetwork) activity;
        this.fragmentChanger = (FragmentChanger) activity;
    }

    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true)
    {
        @Override
        public void handleOnBackPressed()
        {
            getActivity().finish();
        }
    };

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        addOnBackPressedCallback();
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        Bundle bundle = getArguments();

        calendarId = bundle.getInt(CalendarContract.Instances.CALENDAR_ID);
        instanceId = bundle.getLong(CalendarContract.Instances._ID);
        eventId = bundle.getLong(CalendarContract.Instances.EVENT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentFoodsMainBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        binding.progressBar.setVisibility(View.GONE);

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        foodCriteriaLocationInfoViewModel = new ViewModelProvider(this).get(FoodCriteriaLocationInfoViewModel.class);
        foodCriteriaLocationSearchHistoryViewModel = new ViewModelProvider(this).get(FoodCriteriaLocationHistoryViewModel.class);
        customFoodCategoryViewModel = new ViewModelProvider(this).get(CustomFoodCategoryViewModel.class);

        //기준 주소 표시
        setCriteriaLocation();

        setCategories();
        binding.criteriaLocation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), LocationSettingsActivity.class);
                Bundle bundle = new Bundle();

                bundle.putInt(CalendarContract.Instances.CALENDAR_ID, calendarId);
                bundle.putLong(CalendarContract.Instances.EVENT_ID, eventId);
                bundle.putLong(CalendarContract.Instances._ID, instanceId);

                intent.putExtras(bundle);
                locationSettingsActivityResultLauncher.launch(intent);
            }
        });
    }

    private void setCriteriaLocation()
    {
        //지정한 위치정보를 가져온다
        locationViewModel.getLocation(calendarId, eventId, new CarrierMessagingService.ResultCallback<LocationDTO>()
        {
            @Override
            public void onReceiveResult(@NonNull LocationDTO locationDTO) throws RemoteException
            {
                //address, place 구분
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //가져온 위치 정보를 저장
                        FoodsMainFragment.this.selectedLocationDTO = locationDTO;

                        //지정한 위치 정보 데이터를 가져왔으면 기준 위치 선택정보를 가져온다.
                        foodCriteriaLocationInfoViewModel.selectByEventId(calendarId, eventId, new CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO>()
                        {
                            @Override
                            public void onReceiveResult(@NonNull FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO) throws RemoteException
                            {
                                getActivity().runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        //기준 설정 정보 객체 저장
                                        FoodsMainFragment.this.foodCriteriaLocationInfoDTO = foodCriteriaLocationInfoDTO;

                                        switch (foodCriteriaLocationInfoDTO.getUsingType())
                                        {
                                            case FoodCriteriaLocationInfoDTO.TYPE_SELECTED_LOCATION:
                                                criteriaLocationDTO = locationDTO.copy();

                                                if (criteriaLocationDTO.getPlaceName() != null)
                                                {
                                                    binding.criteriaLocation.setText(criteriaLocationDTO.getPlaceName());
                                                } else
                                                {
                                                    binding.criteriaLocation.setText(criteriaLocationDTO.getAddressName());
                                                }
                                                break;
                                            case FoodCriteriaLocationInfoDTO.TYPE_CURRENT_LOCATION:
                                                //현재 위치 파악
                                                gps();
                                                break;
                                            case FoodCriteriaLocationInfoDTO.TYPE_CUSTOM_SELECTED_LOCATION:
                                            {
                                                //지정 위치 파악
                                                foodCriteriaLocationSearchHistoryViewModel.select(foodCriteriaLocationInfoDTO.getHistoryLocationId(), new CarrierMessagingService.ResultCallback<FoodCriteriaLocationSearchHistoryDTO>()
                                                {
                                                    @Override
                                                    public void onReceiveResult(@NonNull FoodCriteriaLocationSearchHistoryDTO foodCriteriaLocationSearchHistoryDTO) throws RemoteException
                                                    {
                                                        getActivity().runOnUiThread(new Runnable()
                                                        {
                                                            @Override
                                                            public void run()
                                                            {
                                                                FoodsMainFragment.this.foodCriteriaLocationSearchHistoryDTO = foodCriteriaLocationSearchHistoryDTO;
                                                                criteriaLocationDTO = new LocationDTO();
                                                                criteriaLocationDTO.setAddressName(foodCriteriaLocationSearchHistoryDTO.getAddressName());
                                                                criteriaLocationDTO.setPlaceName(foodCriteriaLocationSearchHistoryDTO.getPlaceName());
                                                                criteriaLocationDTO.setLatitude(Double.parseDouble(foodCriteriaLocationSearchHistoryDTO.getLatitude()));
                                                                criteriaLocationDTO.setLongitude(Double.parseDouble(foodCriteriaLocationSearchHistoryDTO.getLongitude()));

                                                                if (criteriaLocationDTO.getPlaceName() != null)
                                                                {
                                                                    binding.criteriaLocation.setText(criteriaLocationDTO.getPlaceName());
                                                                } else
                                                                {
                                                                    binding.criteriaLocation.setText(criteriaLocationDTO.getAddressName());
                                                                }
                                                            }
                                                        });

                                                    }
                                                });

                                            }
                                            break;
                                        }
                                    }
                                });

                            }
                        });

                    }
                });

            }
        });

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        removeOnBackPressedCallback();
    }

    private void gps()
    {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.categoryGridview.setVisibility(View.GONE);
        binding.criteriaLocation.setText(getString(R.string.finding_current_location));

        //권한 확인
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

        if (iNetwork.networkAvailable())
        {
            if (isGpsEnabled && isNetworkEnabled)
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            } else if (!isGpsEnabled)
            {
                showRequestGpsDialog();
            }
        }

    }

    public void showRequestGpsDialog()
    {
        new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.request_to_make_gps_on))
                .setPositiveButton(getString(R.string.check), new
                        DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt)
                            {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                    }
                })
                .setCancelable(false)
                .show();
    }

    public final LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            locationManager.removeUpdates(locationListener);

            criteriaLocationDTO = new LocationDTO();
            criteriaLocationDTO.setLatitude(location.getLatitude());
            criteriaLocationDTO.setLongitude(location.getLongitude());

            //주소 reverse geocoding
            LocalApiPlaceParameter localApiPlaceParameter = new LocalApiPlaceParameter();
            localApiPlaceParameter.setX(String.valueOf(criteriaLocationDTO.getLongitude()));
            localApiPlaceParameter.setY(String.valueOf(criteriaLocationDTO.getLatitude()));

            CoordToAddressUtil.coordToAddress(localApiPlaceParameter, new CarrierMessagingService.ResultCallback<DataWrapper<CoordToAddress>>()
            {
                @Override
                public void onReceiveResult(@NonNull DataWrapper<CoordToAddress> coordToAddressDataWrapper) throws RemoteException
                {
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (coordToAddressDataWrapper.getException() == null)
                            {
                                binding.progressBar.setVisibility(View.GONE);
                                binding.categoryGridview.setVisibility(View.VISIBLE);

                                CoordToAddress coordToAddress = coordToAddressDataWrapper.getData();
                                criteriaLocationDTO.setAddressName(coordToAddress.getCoordToAddressDocuments().get(0).getCoordToAddressAddress()
                                        .getAddressName());

                                binding.criteriaLocation.setText(criteriaLocationDTO.getAddressName());
                            }
                        }
                    });

                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle)
        {

        }

        @Override
        public void onProviderEnabled(String s)
        {

        }

        @Override
        public void onProviderDisabled(String s)
        {

        }
    };


    private void setCategories()
    {
        customFoodCategoryViewModel.select(new CarrierMessagingService.ResultCallback<List<CustomFoodCategoryDTO>>()
        {
            @Override
            public void onReceiveResult(@NonNull List<CustomFoodCategoryDTO> resultList) throws RemoteException
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        FoodCategoryAdapter foodCategoryAdapter = new FoodCategoryAdapter(getContext(), FoodsMainFragment.this);

                        Context context = getContext();
                        List<FoodCategoryItem> itemsList = new ArrayList<>();

                        itemsList.add(new FoodCategoryItem(getString(R.string.hansik), context.getDrawable(R.drawable.hansik_kimchi_jjigae), true));
                        itemsList.add(new FoodCategoryItem(getString(R.string.jungsik), context.getDrawable(R.drawable.jungsik_jjajangmyeon), true));
                        itemsList.add(new FoodCategoryItem(getString(R.string.illsik), context.getDrawable(R.drawable.illsik_chobab), true));
                        itemsList.add(new FoodCategoryItem(getString(R.string.sashimi), context.getDrawable(R.drawable.sashimi), true));
                        itemsList.add(new FoodCategoryItem(getString(R.string.yangsik), context.getDrawable(R.drawable.yangsik_barbeque), true));
                        itemsList.add(new FoodCategoryItem(getString(R.string.asian), context.getDrawable(R.drawable.asain_ssalguksoo), true));
                        itemsList.add(new FoodCategoryItem(getString(R.string.chicken), context.getDrawable(R.drawable.chicken), true));
                        itemsList.add(new FoodCategoryItem(getString(R.string.fastfood), context.getDrawable(R.drawable.hamburger), true));
                        itemsList.add(new FoodCategoryItem(getString(R.string.donkartz), context.getDrawable(R.drawable.donkartz), true));
                        itemsList.add(new FoodCategoryItem(getString(R.string.jjim), context.getDrawable(R.drawable.jjim_galbijjim), true));
                        itemsList.add(new FoodCategoryItem(getString(R.string.tang), context.getDrawable(R.drawable.tang_maewoontang), true));
                        itemsList.add(new FoodCategoryItem(getString(R.string.bunsik), context.getDrawable(R.drawable.bunsik_ddeokboggi), true));
                        itemsList.add(new FoodCategoryItem(getString(R.string.juk), context.getDrawable(R.drawable.juk), true));

                        if (!resultList.isEmpty())
                        {
                            for (CustomFoodCategoryDTO customFoodCategory : resultList)
                            {
                                itemsList.add(new FoodCategoryItem(customFoodCategory.getCategoryName(), null, false));
                            }
                        }

                        foodCategoryAdapter.setItems(itemsList);
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 5);
                        binding.categoryGridview.setLayoutManager(gridLayoutManager);
                        binding.categoryGridview.setAdapter(foodCategoryAdapter);
                    }
                });
            }
        });


    }

    @Override
    public void onClickedFoodCategory(FoodCategoryItem foodCategoryItem)
    {
        onBackPressedCallback.remove();
        FoodCategoryTabFragment foodCategoryTabFragment = new FoodCategoryTabFragment(FoodsMainFragment.this, fragmentChanger, foodCategoryItem.getCategoryName());
        fragmentChanger.changeFragment(foodCategoryTabFragment, FoodCategoryTabFragment.TAG);
    }

    private final ActivityResultLauncher<Intent> locationSettingsActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        foodCriteriaLocationInfoViewModel.selectByEventId(calendarId, eventId, new CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO>()
                        {
                            @Override
                            public void onReceiveResult(@NonNull FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO) throws RemoteException
                            {
                                getActivity().runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        FoodsMainFragment.this.foodCriteriaLocationInfoDTO = foodCriteriaLocationInfoDTO;

                                        if (foodCriteriaLocationInfoDTO.getUsingType() == FoodCriteriaLocationInfoDTO.TYPE_SELECTED_LOCATION)
                                        {
                                            criteriaLocationDTO = selectedLocationDTO;

                                            if (criteriaLocationDTO.getPlaceName() != null)
                                            {
                                                binding.criteriaLocation.setText(criteriaLocationDTO.getPlaceName());
                                            } else
                                            {
                                                binding.criteriaLocation.setText(criteriaLocationDTO.getAddressName());
                                            }
                                        } else if (foodCriteriaLocationInfoDTO.getUsingType() == FoodCriteriaLocationInfoDTO.TYPE_CURRENT_LOCATION)
                                        {
                                            gps();
                                        } else if (foodCriteriaLocationInfoDTO.getUsingType() == FoodCriteriaLocationInfoDTO.TYPE_CUSTOM_SELECTED_LOCATION)
                                        {
                                            foodCriteriaLocationSearchHistoryViewModel.select(foodCriteriaLocationInfoDTO.getHistoryLocationId(), new CarrierMessagingService.ResultCallback<FoodCriteriaLocationSearchHistoryDTO>()
                                            {
                                                @Override
                                                public void onReceiveResult(@NonNull FoodCriteriaLocationSearchHistoryDTO foodCriteriaLocationSearchHistoryDTO) throws RemoteException
                                                {
                                                    getActivity().runOnUiThread(new Runnable()
                                                    {
                                                        @Override
                                                        public void run()
                                                        {
                                                            FoodsMainFragment.this.foodCriteriaLocationSearchHistoryDTO = foodCriteriaLocationSearchHistoryDTO;

                                                            criteriaLocationDTO = new LocationDTO();
                                                            criteriaLocationDTO.setAddressName(foodCriteriaLocationSearchHistoryDTO.getAddressName());
                                                            criteriaLocationDTO.setPlaceName(foodCriteriaLocationSearchHistoryDTO.getPlaceName());
                                                            criteriaLocationDTO.setLatitude(Double.parseDouble(foodCriteriaLocationSearchHistoryDTO.getLatitude()));
                                                            criteriaLocationDTO.setLongitude(Double.parseDouble(foodCriteriaLocationSearchHistoryDTO.getLongitude()));

                                                            if (criteriaLocationDTO.getPlaceName() != null)
                                                            {
                                                                binding.criteriaLocation.setText(criteriaLocationDTO.getPlaceName());
                                                            } else
                                                            {
                                                                binding.criteriaLocation.setText(criteriaLocationDTO.getAddressName());
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });

    @Override
    public LocationDTO getCriteriaLocation()
    {
        return criteriaLocationDTO;
    }

    @Override
    public void addOnBackPressedCallback()
    {
        getActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public void removeOnBackPressedCallback()
    {
        onBackPressedCallback.remove();
    }
}