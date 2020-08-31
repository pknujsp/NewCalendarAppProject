package com.zerodsoft.scheduleweather.repositories;


import android.app.Application;
import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.activity.ScheduleInfoActivity;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.LocationDAO;
import com.zerodsoft.scheduleweather.room.dao.ScheduleDAO;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.thread.RepositoryCallback;
import com.zerodsoft.scheduleweather.thread.Result;

import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScheduleRepository
{
    // db내 데이터 유무에 관계없이 LiveData는 넘어온다(null이 아님)
    private AppDb appDb;
    private ScheduleDAO scheduleDAO;
    private LocationDAO locationDAO;

    private MutableLiveData<ScheduleDTO> scheduleLiveData = new MutableLiveData<>();
    private MutableLiveData<PlaceDTO> placeLiveData = new MutableLiveData<>();
    private MutableLiveData<AddressDTO> addressLiveData = new MutableLiveData<>();

    public ScheduleRepository(Application application, int scheduleId)
    {
        appDb = AppDb.getInstance(application);
        scheduleDAO = appDb.scheduleDAO();
        locationDAO = appDb.locationDAO();
        selectSchedule(scheduleId);
    }

    public void selectSchedule(int scheduleId)
    {
        LiveData<ScheduleDTO> scheduleLive = scheduleDAO.selectSchedule(scheduleId);

        if (scheduleLive.getValue() != null)
        {
            ScheduleDTO scheduleDTO = scheduleLive.getValue();
            scheduleLiveData.setValue(scheduleDTO);

            if (scheduleDTO.getAddress() != -1)
            {
                addressLiveData.setValue(locationDAO.selectAddress(scheduleDTO.getAddress()).getValue());
            } else if (scheduleDTO.getPlace() != -1)
            {
                placeLiveData.setValue(locationDAO.selectPlace(scheduleDTO.getPlace()).getValue());
            }
        } else
        {
            scheduleLiveData.setValue(new ScheduleDTO());
        }
    }

    public MutableLiveData<ScheduleDTO> getScheduleLiveData()
    {
        return scheduleLiveData;
    }

    public MutableLiveData<AddressDTO> getAddressLiveData()
    {
        return addressLiveData;
    }

    public MutableLiveData<PlaceDTO> getPlaceLiveData()
    {
        return placeLiveData;
    }


    public void deleteSchedule()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                int scheduleId = scheduleLiveData.getValue().getId();
                scheduleDAO.deleteSchedule(scheduleId);
                locationDAO.deleteAddress(scheduleId);
                locationDAO.deletePlace(scheduleId);
            }
        }).start();
    }

    public void insertSchedule(ScheduleDTO scheduleDTO, PlaceDTO placeDTO, AddressDTO addressDTO)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Calendar calendar = Calendar.getInstance();

                scheduleDTO.setInsertedDate(calendar.getTime());
                scheduleDTO.setUpdatedDate(calendar.getTime());

                long scheduleId = scheduleDAO.insertNewSchedule(scheduleDTO);

                if (placeDTO != null)
                {
                    placeDTO.setScheduleId((int) scheduleId);
                    locationDAO.insertPlace(placeDTO);
                } else if (addressDTO != null)
                {
                    addressDTO.setScheduleId((int) scheduleId);
                    locationDAO.insertAddress(addressDTO);
                }
            }
        }).start();
    }

    public void updateSchedule(ScheduleDTO scheduleDTO, PlaceDTO placeDTO, AddressDTO addressDTO)
    {
        // 위치가 변경된 경우 -> scheduleDto의 해당 값 1유지하고 해당 위치의 정보를 받음
        // 위치가 삭제된 경우 -> scheduleDto의 해당 값 0으로 변경됨
        // 위치가 추가된 경우 -> scheduleDto의 해당 값이 그대로 0으로 유지되고 해당 위치의 정보를 받음
        // 위치가 추가되지 않은 원상태 그대로인 경우 -> scheduleDto의 해당 값이 그대로 0으로 유지되고 해당 위치의 정보를 받지않음
        // livedata의 원본 데이터와 비교
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Calendar calendar = Calendar.getInstance();

                scheduleDTO.setUpdatedDate(calendar.getTime());
                scheduleDAO.updateSchedule(scheduleDTO);

                if (scheduleDTO.getPlace() != scheduleLiveData.getValue().getPlace())
                {
                    // 위치가 추가 | 삭제된 경우
                    if (scheduleDTO.getPlace() == ScheduleDTO.SELECTED_LOCATION)
                    {
                        // 추가
                        locationDAO.insertPlace(placeDTO);
                    } else
                    {
                        // 삭제
                        locationDAO.deletePlace(scheduleDTO.getId());
                    }
                } else
                {
                    // 위치가 변경 | 변경X 인 경우
                    if (scheduleDTO.getPlace() == ScheduleDTO.SELECTED_LOCATION)
                    {
                        // 변경 | 변경X
                        if (placeDTO != null)
                        {
                            // 변경
                            locationDAO.updatePlace(placeDTO);
                        } else
                        {
                            // 변경X
                        }
                    } else
                    {
                        // 위치 추가되지 않음
                    }
                }


                if (scheduleDTO.getAddress() != scheduleLiveData.getValue().getAddress())
                {
                    // 위치가 추가 | 삭제된 경우
                    if (scheduleDTO.getAddress() == ScheduleDTO.SELECTED_LOCATION)
                    {
                        // 추가
                        locationDAO.insertAddress(addressDTO);
                    } else
                    {
                        // 삭제
                        locationDAO.deleteAddress(scheduleDTO.getId());
                    }
                } else
                {
                    // 위치가 변경 | 변경X 인 경우
                    if (scheduleDTO.getAddress() == ScheduleDTO.SELECTED_LOCATION)
                    {
                        // 변경 | 변경X
                        if (placeDTO != null)
                        {
                            // 변경
                            locationDAO.updateAddress(addressDTO);
                        } else
                        {
                            // 변경X
                        }
                    } else
                    {
                        // 위치 추가되지 않음
                    }
                }
            }
        }).start();
    }
}

