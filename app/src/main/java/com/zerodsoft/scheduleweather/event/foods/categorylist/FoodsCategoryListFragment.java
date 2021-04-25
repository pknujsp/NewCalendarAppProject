package com.zerodsoft.scheduleweather.event.foods.categorylist;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Looper;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.Utmk;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.preferences.customfoodmenu.fragment.CustomFoodMenuSettingsFragment;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.databinding.FragmentFoodsCategoryListBinding;
import com.zerodsoft.scheduleweather.etc.AppPermission;
import com.zerodsoft.scheduleweather.etc.LocationType;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.activity.LocationSettingsActivity;
import com.zerodsoft.scheduleweather.event.foods.adapter.FoodCategoryAdapter;
import com.zerodsoft.scheduleweather.event.foods.dto.FoodCategoryItem;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedCategoryItem;
import com.zerodsoft.scheduleweather.event.foods.settings.CustomFoodMenuSettingsActivity;
import com.zerodsoft.scheduleweather.event.foods.share.CriteriaLocationRepository;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.CustomFoodMenuViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationHistoryViewModel;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.INetwork;
import com.zerodsoft.scheduleweather.kakaomap.model.CoordToAddressUtil;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.address.ReverseGeoCodingParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.address.reversegeocoding.ReverseGeoCodingResponse;
import com.zerodsoft.scheduleweather.room.dto.CustomFoodMenuDTO;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationSearchHistoryDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.sgis.SgisAddress;
import com.zerodsoft.scheduleweather.sgis.SgisAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.checkSelfPermission;

public class FoodsCategoryListFragment extends Fragment implements OnClickedCategoryItem, OnClickedListItem<FoodCategoryItem>
{
    public static final String TAG = "FoodsCategoryListFragment";
    private FragmentFoodsCategoryListBinding binding;
    private final INetwork iNetwork;

    private CustomFoodMenuViewModel customFoodCategoryViewModel;
    private LocationViewModel locationViewModel;
    private FoodCriteriaLocationInfoViewModel foodCriteriaLocationInfoViewModel;
    private FoodCriteriaLocationHistoryViewModel foodCriteriaLocationSearchHistoryViewModel;

    private LocationManager locationManager;
    private final ContentValues INSTANCE_VALUES = new ContentValues();

    private LocationDTO selectedLocationDTO;
    private FoodCriteriaLocationSearchHistoryDTO foodCriteriaLocationSearchHistoryDTO;
    private FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO;

    private boolean clickedGps = false;

    public FoodsCategoryListFragment(INetwork iNetwork)
    {
        this.iNetwork = iNetwork;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        Bundle bundle = getArguments();

        INSTANCE_VALUES.put(CalendarContract.Instances.CALENDAR_ID, bundle.getInt(CalendarContract.Instances.CALENDAR_ID));
        INSTANCE_VALUES.put(CalendarContract.Instances._ID, bundle.getLong(CalendarContract.Instances._ID));
        INSTANCE_VALUES.put(CalendarContract.Instances.EVENT_ID, bundle.getLong(CalendarContract.Instances.EVENT_ID));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentFoodsCategoryListBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        foodCriteriaLocationInfoViewModel = new ViewModelProvider(this).get(FoodCriteriaLocationInfoViewModel.class);
        foodCriteriaLocationSearchHistoryViewModel = new ViewModelProvider(this).get(FoodCriteriaLocationHistoryViewModel.class);
        customFoodCategoryViewModel = new ViewModelProvider(this).get(CustomFoodMenuViewModel.class);

        //기준 주소 표시
        setSelectedLocation();
        setCategories();

        binding.criteriaLocation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), LocationSettingsActivity.class);
                Bundle bundle = new Bundle();

                ContentValues instanceValues = new ContentValues();
                instanceValues.putAll(INSTANCE_VALUES);
                bundle.putParcelable("INSTANCE_VALUES", instanceValues);

                intent.putExtras(bundle);
                locationSettingsActivityResultLauncher.launch(intent);
            }
        });
    }

    private void setSelectedLocation()
    {
        //지정한 위치정보를 가져온다
        locationViewModel.getLocation(INSTANCE_VALUES.getAsInteger(CalendarContract.Instances.CALENDAR_ID)
                , INSTANCE_VALUES.getAsLong(CalendarContract.Instances.EVENT_ID), new CarrierMessagingService.ResultCallback<LocationDTO>()
                {
                    @Override
                    public void onReceiveResult(@NonNull LocationDTO locationDTO) throws RemoteException
                    {
                        //가져온 위치 정보를 저장
                        FoodsCategoryListFragment.this.selectedLocationDTO = locationDTO;
                        //지정한 위치 정보 데이터를 가져왔으면 기준 위치 선택정보를 가져온다.
                        setCriteriaLocation();
                    }
                });
    }

    private void setCriteriaLocation()
    {
        foodCriteriaLocationInfoViewModel.selectByEventId(INSTANCE_VALUES.getAsInteger(CalendarContract.Instances.CALENDAR_ID)
                , INSTANCE_VALUES.getAsLong(CalendarContract.Instances.EVENT_ID), new CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO>()
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
                                FoodsCategoryListFragment.this.foodCriteriaLocationInfoDTO = foodCriteriaLocationInfoDTO;

                                switch (foodCriteriaLocationInfoDTO.getUsingType())
                                {
                                    case FoodCriteriaLocationInfoDTO.TYPE_SELECTED_LOCATION:
                                    {
                                        LocationDTO criteriaLocationDTO = null;
                                        try
                                        {
                                            criteriaLocationDTO = selectedLocationDTO.clone();
                                        } catch (CloneNotSupportedException e)
                                        {
                                            e.printStackTrace();
                                        }
                                        CriteriaLocationRepository.setRestaurantCriteriaLocation(criteriaLocationDTO);

                                        if (criteriaLocationDTO.getLocationType() == LocationType.PLACE)
                                        {
                                            binding.criteriaLocation.setText(criteriaLocationDTO.getPlaceName());
                                        } else
                                        {
                                            binding.criteriaLocation.setText(criteriaLocationDTO.getAddressName());
                                        }
                                        binding.progressBar.setVisibility(View.GONE);
                                        break;
                                    }

                                    case FoodCriteriaLocationInfoDTO.TYPE_CURRENT_LOCATION:
                                    {
                                        //현재 위치 파악
                                        gps();
                                        break;
                                    }

                                    case FoodCriteriaLocationInfoDTO.TYPE_CUSTOM_SELECTED_LOCATION:
                                    {
                                        //지정 위치 파악
                                        foodCriteriaLocationSearchHistoryViewModel.select(foodCriteriaLocationInfoDTO.getHistoryLocationId(), new CarrierMessagingService.ResultCallback<FoodCriteriaLocationSearchHistoryDTO>()
                                        {
                                            @Override
                                            public void onReceiveResult(@NonNull FoodCriteriaLocationSearchHistoryDTO foodCriteriaLocationSearchHistoryDTO) throws RemoteException
                                            {
                                                FoodsCategoryListFragment.this.foodCriteriaLocationSearchHistoryDTO = foodCriteriaLocationSearchHistoryDTO;
                                                LocationDTO criteriaLocationDTO = new LocationDTO();
                                                criteriaLocationDTO.setAddressName(foodCriteriaLocationSearchHistoryDTO.getAddressName());
                                                criteriaLocationDTO.setPlaceName(foodCriteriaLocationSearchHistoryDTO.getPlaceName());
                                                criteriaLocationDTO.setLatitude(Double.parseDouble(foodCriteriaLocationSearchHistoryDTO.getLatitude()));
                                                criteriaLocationDTO.setLongitude(Double.parseDouble(foodCriteriaLocationSearchHistoryDTO.getLongitude()));
                                                criteriaLocationDTO.setLocationType(foodCriteriaLocationSearchHistoryDTO.getLocationType());

                                                CriteriaLocationRepository.setRestaurantCriteriaLocation(criteriaLocationDTO);

                                                getActivity().runOnUiThread(new Runnable()
                                                {
                                                    @Override
                                                    public void run()
                                                    {
                                                        if (criteriaLocationDTO.getLocationType() == LocationType.PLACE)
                                                        {
                                                            binding.criteriaLocation.setText(criteriaLocationDTO.getPlaceName());
                                                        } else
                                                        {
                                                            binding.criteriaLocation.setText(criteriaLocationDTO.getAddressName());
                                                        }
                                                        binding.progressBar.setVisibility(View.GONE);
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

    private void gps()
    {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.criteriaLocation.setText(getString(R.string.finding_current_location));

        clickedGps = true;

        //권한 확인
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if (iNetwork.networkAvailable())
            {
                if (isGpsEnabled)
                {
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener()
                    {
                        @Override
                        public void onLocationChanged(Location location)
                        {
                            locationManager.removeUpdates(this);

                            if (clickedGps)
                            {
                                clickedGps = false;
                                onCatchedGps(location);
                            }
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
                    }, null);

                    locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener()
                    {
                        @Override
                        public void onLocationChanged(Location location)
                        {
                            locationManager.removeUpdates(this);

                            if (clickedGps)
                            {
                                clickedGps = false;
                                onCatchedGps(location);
                            }
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
                    }, null);
                } else
                {
                    binding.progressBar.setVisibility(View.GONE);
                    showRequestGpsDialog();
                }
            }
        } else
        {
            permissionsResultLauncher.launch(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION});
        }

    }

    private void showRequestGpsDialog()
    {
        new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.request_to_make_gps_on))
                .setPositiveButton(getString(R.string.check), new
                        DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt)
                            {
                                gpsOnResultLauncher.launch(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        binding.criteriaLocation.callOnClick();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private final ActivityResultLauncher<Intent> gpsOnResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (AppPermission.grantedPermissions(getContext(), Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                    {
                        gps();
                    } else
                    {
                        binding.criteriaLocation.callOnClick();
                    }
                }
            });

    private final ActivityResultLauncher<String[]> permissionsResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>()
            {
                @Override
                public void onActivityResult(Map<String, Boolean> result)
                {
                    if (result.get(Manifest.permission.ACCESS_COARSE_LOCATION) &&
                            result.get(Manifest.permission.ACCESS_FINE_LOCATION))
                    {
                        // 권한 허용됨
                        gps();
                    } else
                    {
                        // 권한 거부됨
                        Toast.makeText(getContext(), R.string.message_needs_location_permission, Toast.LENGTH_SHORT).show();
                        foodCriteriaLocationInfoViewModel.updateByEventId(INSTANCE_VALUES.getAsInteger(CalendarContract.Instances.CALENDAR_ID)
                                , INSTANCE_VALUES.getAsLong(CalendarContract.Instances.EVENT_ID)
                                , FoodCriteriaLocationInfoDTO.TYPE_SELECTED_LOCATION, null, new CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO>()
                                {
                                    @Override
                                    public void onReceiveResult(@NonNull FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO) throws RemoteException
                                    {
                                        getActivity().runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                binding.progressBar.setVisibility(View.GONE);
                                                setCriteriaLocation();
                                            }
                                        });
                                    }
                                });
                    }
                }
            });

    private void onCatchedGps(Location location)
    {
        LocationDTO criteriaLocationDTO = new LocationDTO();

        criteriaLocationDTO.setLatitude(location.getLatitude());
        criteriaLocationDTO.setLongitude(location.getLongitude());
        criteriaLocationDTO.setLocationType(LocationType.ADDRESS);
        setCurrentLocationData(criteriaLocationDTO);
    }


    private void setCurrentLocationData(LocationDTO criteriaLocationDTO)
    {
        CriteriaLocationRepository.setRestaurantCriteriaLocation(criteriaLocationDTO);

        //주소 reverse geocoding
        ReverseGeoCodingParameter parameter = new ReverseGeoCodingParameter();
        parameter.setAccessToken(SgisAuth.getSgisAuthResponse().getResult().getAccessToken());
        parameter.setAddrType(ReverseGeoCodingParameter.AddressType.ADM_EUP_MYEON_DONG);
        parameter.setCoord(criteriaLocationDTO.getLatitude(), criteriaLocationDTO.getLongitude());

        SgisAddress.reverseGeoCoding(parameter, new CarrierMessagingService.ResultCallback<DataWrapper<ReverseGeoCodingResponse>>()
        {
            @Override
            public void onReceiveResult(@NonNull DataWrapper<ReverseGeoCodingResponse> reverseGeoCodingResponseDataWrapper) throws RemoteException
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (reverseGeoCodingResponseDataWrapper.getException() == null)
                        {
                            ReverseGeoCodingResponse reverseGeoCodingResponse = reverseGeoCodingResponseDataWrapper.getData();
                            criteriaLocationDTO.setAddressName(reverseGeoCodingResponse.getResult().get(0).getFullAddress());
                            CriteriaLocationRepository.setRestaurantCriteriaLocation(criteriaLocationDTO);

                            binding.criteriaLocation.setText(criteriaLocationDTO.getAddressName());
                            binding.progressBar.setVisibility(View.GONE);
                            binding.categoryGridview.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }


    private void setCategories()
    {
        customFoodCategoryViewModel.select(new CarrierMessagingService.ResultCallback<List<CustomFoodMenuDTO>>()
        {
            @Override
            public void onReceiveResult(@NonNull List<CustomFoodMenuDTO> resultList) throws RemoteException
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        final int columnCount = 4;
                        FoodCategoryAdapter foodCategoryAdapter = new FoodCategoryAdapter(FoodsCategoryListFragment.this, columnCount);

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
                            for (CustomFoodMenuDTO customFoodCategory : resultList)
                            {
                                itemsList.add(new FoodCategoryItem(customFoodCategory.getMenuName(), null, false));
                            }
                        }
                        itemsList.add(new FoodCategoryItem(getString(R.string.add_custom_food_menu), null, false));

                        foodCategoryAdapter.setItems(itemsList);
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), columnCount);
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

    }

    private final ActivityResultLauncher<Intent> locationSettingsActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        setCriteriaLocation();
                    }
                }
            });


    @Override
    public void onClickedListItem(FoodCategoryItem e)
    {
        if (!e.isDefault() && e.getCategoryName().equals(getString(R.string.add_custom_food_menu)))
        {
            customFoodSettingsActivityResultLauncher.launch(new Intent(getActivity(), CustomFoodMenuSettingsActivity.class));
        } else
        {
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().hide(this);

            FoodCategoryTabFragment foodCategoryTabFragment = new FoodCategoryTabFragment(e.getCategoryName());
            fragmentTransaction.add(R.id.foods_main_fragment_container, foodCategoryTabFragment, FoodCategoryTabFragment.TAG).addToBackStack(null).commit();
        }
    }

    @Override
    public void deleteListItem(FoodCategoryItem e, int position)
    {

    }

    private final ActivityResultLauncher<Intent> customFoodSettingsActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        setCategories();
                    }
                }
            });
}