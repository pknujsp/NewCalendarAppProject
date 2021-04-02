package com.zerodsoft.scheduleweather.event.foods.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.service.carrier.CarrierMessagingService;
import android.view.View;
import android.widget.RadioGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.ActivityLocationSettingsBinding;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.adapter.FoodCriteriaLocationHistoryAdapter;
import com.zerodsoft.scheduleweather.event.foods.interfaces.LocationHistoryController;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationHistoryViewModel;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationSearchHistoryDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import java.util.List;

public class LocationSettingsActivity extends AppCompatActivity implements LocationHistoryController
{
    private ActivityLocationSettingsBinding binding;
    private LocationViewModel locationViewModel;
    private FoodCriteriaLocationInfoViewModel foodCriteriaLocationInfoViewModel;
    private FoodCriteriaLocationHistoryViewModel foodCriteriaLocationSearchHistoryViewModel;

    private int calendarId;
    private long instanceId;
    private long eventId;

    private LocationDTO locationDTO;
    private List<FoodCriteriaLocationSearchHistoryDTO> foodCriteriaLocationHistoryList;
    private FoodCriteriaLocationSearchHistoryDTO selectedFoodCriteriaLocationSearchHistoryDTO;
    private FoodCriteriaLocationHistoryAdapter foodCriteriaLocationHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location_settings);

        Bundle bundle = getIntent().getExtras();
        calendarId = bundle.getInt(CalendarContract.Instances.CALENDAR_ID);
        instanceId = bundle.getLong(CalendarContract.Instances._ID);
        eventId = bundle.getLong(CalendarContract.Instances.EVENT_ID);

        binding.addressHistoryRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.addressHistoryRecyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        binding.radioGroup.setOnCheckedChangeListener(radioOnCheckedChangeListener);

        setSearchView();

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        foodCriteriaLocationInfoViewModel = new ViewModelProvider(this).get(FoodCriteriaLocationInfoViewModel.class);
        foodCriteriaLocationSearchHistoryViewModel = new ViewModelProvider(this).get(FoodCriteriaLocationHistoryViewModel.class);

        locationViewModel.getLocation(calendarId, eventId, new CarrierMessagingService.ResultCallback<LocationDTO>()
        {
            @Override
            public void onReceiveResult(@NonNull LocationDTO locationDTO) throws RemoteException
            {
                //address, place 구분
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        LocationSettingsActivity.this.locationDTO = locationDTO;

                        if (locationDTO.getPlaceName() != null)
                        {
                            binding.radioUseSelectedLocation.setText(locationDTO.getPlaceName());
                        } else
                        {
                            binding.radioUseSelectedLocation.setText(locationDTO.getAddressName());
                        }

                        //지정한 위치 정보 데이터를 가져왔으면 기준 위치 선택정보를 가져온다.
                        foodCriteriaLocationInfoViewModel.selectByEventId(calendarId, eventId, new CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO>()
                        {
                            @Override
                            public void onReceiveResult(@NonNull FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO) throws RemoteException
                            {
                                switch (foodCriteriaLocationInfoDTO.getUsingType())
                                {
                                    case FoodCriteriaLocationInfoDTO.TYPE_SELECTED_LOCATION:
                                        binding.radioUseSelectedLocation.setSelected(true);
                                        break;
                                    case FoodCriteriaLocationInfoDTO.TYPE_CURRENT_LOCATION:
                                        binding.radioCurrentLocation.setSelected(true);
                                        break;
                                    case FoodCriteriaLocationInfoDTO.TYPE_CUSTOM_SELECTED_LOCATION:
                                        binding.radioCustomSelection.setSelected(true);
                                        foodCriteriaLocationSearchHistoryViewModel.select(foodCriteriaLocationInfoDTO.getId(),
                                                new CarrierMessagingService.ResultCallback<FoodCriteriaLocationSearchHistoryDTO>()
                                                {
                                                    @Override
                                                    public void onReceiveResult(@NonNull FoodCriteriaLocationSearchHistoryDTO result) throws RemoteException
                                                    {
                                                        LocationSettingsActivity.this.selectedFoodCriteriaLocationSearchHistoryDTO =
                                                                result;
                                                    }
                                                });
                                        break;
                                }
                            }
                        });

                    }
                });

            }
        });

        foodCriteriaLocationSearchHistoryViewModel.selectByEventId(calendarId, eventId, new CarrierMessagingService.ResultCallback<List<FoodCriteriaLocationSearchHistoryDTO>>()
        {
            @Override
            public void onReceiveResult(@NonNull List<FoodCriteriaLocationSearchHistoryDTO> result) throws RemoteException
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        LocationSettingsActivity.this.foodCriteriaLocationHistoryList = result;

                        foodCriteriaLocationHistoryAdapter = new FoodCriteriaLocationHistoryAdapter(LocationSettingsActivity.this);
                        foodCriteriaLocationHistoryAdapter.setFoodCriteriaLocationHistoryList(foodCriteriaLocationHistoryList);

                        binding.addressHistoryRecyclerview.setAdapter(foodCriteriaLocationHistoryAdapter);
                    }
                });
            }
        });
    }

    private RadioGroup.OnCheckedChangeListener radioOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId)
        {
            if (checkedId == binding.radioUseSelectedLocation.getId())
            {
                //지정한 위치사용
                binding.addressHistoryRecyclerview.setVisibility(View.GONE);
                binding.searchView.setVisibility(View.GONE);
            } else if (checkedId == binding.radioCurrentLocation.getId())
            {
                binding.addressHistoryRecyclerview.setVisibility(View.GONE);
                binding.searchView.setVisibility(View.GONE);
            } else if (checkedId == binding.radioCustomSelection.getId())
            {
                binding.addressHistoryRecyclerview.setVisibility(View.VISIBLE);
                binding.searchView.setVisibility(View.VISIBLE);
            }
        }
    };

    private void setSearchView()
    {
        binding.searchView.setVisibility(View.VISIBLE);

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                //검색 결과 목록 표시
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                return false;
            }
        });

        binding.searchView.setOnCloseListener(new SearchView.OnCloseListener()
        {
            @Override
            public boolean onClose()
            {
                return false;
            }
        });

        binding.searchView.setOnSearchClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });
    }

    @Override
    public void onBackPressed()
    {
        /*
        지정한 위치인 경우 : 해당 이벤트 인스턴스가 지정된 위치를 기준으로 검색한다고 DB에 입력
        현재 위치인 경우 : 해당 이벤트 인스턴스가 현재 위치를 기준으로 검색한다고 DB에 입력
        직접 검색 후 지정한 위치인 경우 : 해당 이벤트 인스턴스가 커스텀 위치를 기준으로 검색한다고 DB에 입력하고,
        커스텀 위치 정보를 DB에 입력
         */
        final int checkedRadioId = binding.radioGroup.getCheckedRadioButtonId();
        int usingType = 0;

        if (checkedRadioId == binding.radioUseSelectedLocation.getId())
        {
            usingType = FoodCriteriaLocationInfoDTO.TYPE_SELECTED_LOCATION;
        } else if (checkedRadioId == binding.radioCurrentLocation.getId())
        {
            usingType = FoodCriteriaLocationInfoDTO.TYPE_CURRENT_LOCATION;
        } else if (checkedRadioId == binding.radioCustomSelection.getId())
        {
            usingType = FoodCriteriaLocationInfoDTO.TYPE_CUSTOM_SELECTED_LOCATION;
            //선택된 정보가 없는 경우 : 이벤트에서 지정한 위치를 기준으로 강제설정
            if (selectedFoodCriteriaLocationSearchHistoryDTO == null)
            {
                usingType = FoodCriteriaLocationInfoDTO.TYPE_SELECTED_LOCATION;
                foodCriteriaLocationInfoViewModel.updateByEventId(calendarId, eventId, usingType, null,
                        new CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO>()
                        {
                            @Override
                            public void onReceiveResult(@NonNull FoodCriteriaLocationInfoDTO result) throws RemoteException
                            {
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
            }
        }

        //변경 타입 업데이트
        foodCriteriaLocationInfoViewModel.updateByEventId(calendarId, eventId, usingType, null, new CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO>()
        {
            @Override
            public void onReceiveResult(@NonNull FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO) throws RemoteException
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }
        });


    }

    @Override
    public void onClickedLocationHistoryItem(FoodCriteriaLocationSearchHistoryDTO foodCriteriaLocationSearchHistoryDTO)
    {
        foodCriteriaLocationSearchHistoryViewModel.deleteByEventId(calendarId, eventId, new CarrierMessagingService.ResultCallback<Boolean>()
        {
            @Override
            public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException
            {
                //리스트에서 선택된 라디오의 데이터를 가져온다.
                if (aBoolean)
                {
                    foodCriteriaLocationSearchHistoryViewModel.insertByEventId(calendarId, eventId, foodCriteriaLocationSearchHistoryDTO.getPlaceName(), foodCriteriaLocationSearchHistoryDTO.getAddressName(),
                            foodCriteriaLocationSearchHistoryDTO.getRoadAddressName()
                            , foodCriteriaLocationSearchHistoryDTO.getLatitude(), foodCriteriaLocationSearchHistoryDTO.getLongitude(),
                            new CarrierMessagingService.ResultCallback<List<FoodCriteriaLocationSearchHistoryDTO>>()
                            {
                                @Override
                                public void onReceiveResult(@NonNull List<FoodCriteriaLocationSearchHistoryDTO> result) throws RemoteException
                                {
                                    //변경 타입 업데이트
                                    int id = result.get(result.size() - 1).getId();
                                    foodCriteriaLocationInfoViewModel.updateByEventId(calendarId, eventId, FoodCriteriaLocationInfoDTO.TYPE_CUSTOM_SELECTED_LOCATION, id, new CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO>()
                                    {
                                        @Override
                                        public void onReceiveResult(@NonNull FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO) throws RemoteException
                                        {
                                            runOnUiThread(new Runnable()
                                            {
                                                @Override
                                                public void run()
                                                {
                                                    setResult(RESULT_OK);
                                                    finish();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                }
            }
        });
    }

    @Override
    public void delete(int id)
    {
        foodCriteriaLocationSearchHistoryViewModel.delete(id, new CarrierMessagingService.ResultCallback<Boolean>()
        {
            @Override
            public void onReceiveResult(@NonNull Boolean result) throws RemoteException
            {
                if (result)
                {
                    foodCriteriaLocationSearchHistoryViewModel.selectByEventId(calendarId, eventId, new CarrierMessagingService.ResultCallback<List<FoodCriteriaLocationSearchHistoryDTO>>()
                    {
                        @Override
                        public void onReceiveResult(@NonNull List<FoodCriteriaLocationSearchHistoryDTO> result) throws RemoteException
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    LocationSettingsActivity.this.foodCriteriaLocationHistoryList = result;

                                    foodCriteriaLocationHistoryAdapter.setFoodCriteriaLocationHistoryList(foodCriteriaLocationHistoryList);
                                    foodCriteriaLocationHistoryAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                }
            }
        });

        if (selectedFoodCriteriaLocationSearchHistoryDTO != null)
        {
            if (selectedFoodCriteriaLocationSearchHistoryDTO.getId() == id)
            {
                foodCriteriaLocationInfoViewModel.deleteByEventId(calendarId, eventId, new CarrierMessagingService.ResultCallback<Boolean>()
                {
                    @Override
                    public void onReceiveResult(@NonNull Boolean result) throws RemoteException
                    {
                        selectedFoodCriteriaLocationSearchHistoryDTO = null;
                    }
                });
            }
        }
    }

}