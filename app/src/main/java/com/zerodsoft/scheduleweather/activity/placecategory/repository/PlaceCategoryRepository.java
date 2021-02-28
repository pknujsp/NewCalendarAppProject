package com.zerodsoft.scheduleweather.activity.placecategory.repository;

import android.content.Context;
import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.activity.placecategory.interfaces.IPlaceCategory;
import com.zerodsoft.scheduleweather.activity.placecategory.model.PlaceCategoryData;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.room.dao.CustomPlaceCategoryDAO;
import com.zerodsoft.scheduleweather.room.dto.CustomPlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.SelectedPlaceCategoryDAO;
import com.zerodsoft.scheduleweather.room.dto.SelectedPlaceCategoryDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;

public class PlaceCategoryRepository implements IPlaceCategory
{
    private final SelectedPlaceCategoryDAO selectedPlaceCategoryDAO;
    private final CustomPlaceCategoryDAO customPlaceCategoryDAO;

    public PlaceCategoryRepository(Context context)
    {
        selectedPlaceCategoryDAO = AppDb.getInstance(context).selectedPlaceCategoryDAO();
        customPlaceCategoryDAO = AppDb.getInstance(context).customPlaceCategoryDAO();
    }

    @Override
    public void insertCustom(String code, CarrierMessagingService.ResultCallback<CustomPlaceCategoryDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                //custom category에 추가
                customPlaceCategoryDAO.insert(code);
                CustomPlaceCategoryDTO customPlaceCategory = customPlaceCategoryDAO.select(code);
                callback.onReceiveResult(customPlaceCategory);
            }
        });
    }

    @Override
    public void selectCustom(CarrierMessagingService.ResultCallback<List<CustomPlaceCategoryDTO>> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                List<CustomPlaceCategoryDTO> list = customPlaceCategoryDAO.select();
                callback.onReceiveResult(list);
            }
        });

    }

    @Override
    public void updateCustom(String currentCode, String code, CarrierMessagingService.ResultCallback<CustomPlaceCategoryDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                // custom category와 selected category 모두 수정
                selectedPlaceCategoryDAO.update(currentCode, code);
                customPlaceCategoryDAO.update(currentCode, code);

                CustomPlaceCategoryDTO customPlaceCategory = customPlaceCategoryDAO.select(code);
                callback.onReceiveResult(customPlaceCategory);
            }
        });
    }

    @Override
    public void deleteCustom(String code, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                // custom category와 selected category 모두 삭제
                selectedPlaceCategoryDAO.delete(code);
                customPlaceCategoryDAO.delete(code);
                callback.onReceiveResult(true);
            }
        });
    }

    @Override
    public void deleteAllCustom(CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                // selected category에서 커스텀 모두 삭제, custom category모두 삭제
                List<CustomPlaceCategoryDTO> customs = customPlaceCategoryDAO.select();
                for (CustomPlaceCategoryDTO custom : customs)
                {
                    selectedPlaceCategoryDAO.delete(custom.getCode());
                }
                customPlaceCategoryDAO.deleteAll();
                callback.onReceiveResult(true);
            }
        });

    }

    @Override
    public void insertSelected(String code, CarrierMessagingService.ResultCallback<SelectedPlaceCategoryDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                selectedPlaceCategoryDAO.insert(code);
                SelectedPlaceCategoryDTO selectedPlaceCategory = selectedPlaceCategoryDAO.select(code);
                callback.onReceiveResult(selectedPlaceCategory);
            }
        });
    }

    @Override
    public void deleteSelected(String code, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                selectedPlaceCategoryDAO.delete(code);
                callback.onReceiveResult(true);
            }
        });
    }

    @Override
    public void deleteAllSelected(CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                selectedPlaceCategoryDAO.deleteAll();
                callback.onReceiveResult(true);
            }
        });
    }

    @Override
    public void selectSelected(CarrierMessagingService.ResultCallback<List<SelectedPlaceCategoryDTO>> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                List<SelectedPlaceCategoryDTO> list = selectedPlaceCategoryDAO.select();
                callback.onReceiveResult(list);
            }
        });

    }

    @Override
    public void getSettingsData(CarrierMessagingService.ResultCallback<PlaceCategoryData> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                //커스텀 카테고리 모든 데이터를 가져옴
                List<CustomPlaceCategoryDTO> customAllList = customPlaceCategoryDAO.select();

                //선택된 카테고리 리스트를 가져옴
                List<SelectedPlaceCategoryDTO> selectedCategorylist = selectedPlaceCategoryDAO.select();

                //기본, 커스텀 카테고리 리스트 설정
                List<PlaceCategoryDTO> defaultAllPlaceCategories = KakaoLocalApiCategoryUtil.getDefaultPlaceCategoryList();
                List<PlaceCategoryDTO> customAllCategories = new ArrayList<>();

                //커스텀 카테고리를 전체 카테고리로 포함시킨다
                for (CustomPlaceCategoryDTO customPlaceCategory : customAllList)
                {
                    PlaceCategoryDTO placeCategoryDTO = new PlaceCategoryDTO();
                    placeCategoryDTO.setCode(customPlaceCategory.getCode());
                    placeCategoryDTO.setDescription(customPlaceCategory.getCode());
                    placeCategoryDTO.setCustom(true);
                    customAllCategories.add(placeCategoryDTO);
                }

                PlaceCategoryData placeCategoryData = new PlaceCategoryData();
                placeCategoryData.setCustomCategories(customAllCategories);
                placeCategoryData.setDefaultPlaceCategories(defaultAllPlaceCategories);
                placeCategoryData.setSelectedPlaceCategories(selectedCategorylist);

                callback.onReceiveResult(placeCategoryData);
            }
        });

    }

    @Override
    public void containsCode(String code, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                boolean containsCode = customPlaceCategoryDAO.containsCode(code) == 1;

                if (!containsCode)
                {
                    Map<String, String> defaultAllPlaceCategoryMap = KakaoLocalApiCategoryUtil.getDefaultPlaceCategoryMap();

                    if (defaultAllPlaceCategoryMap.containsKey(code) || defaultAllPlaceCategoryMap.containsValue(code))
                    {
                        containsCode = true;
                    }
                }
                callback.onReceiveResult(containsCode);
            }
        });
    }

    @Override
    public void updateSelected(String currentCode, String code, CarrierMessagingService.ResultCallback<SelectedPlaceCategoryDTO> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                selectedPlaceCategoryDAO.update(currentCode, code);
                SelectedPlaceCategoryDTO selectedPlaceCategory = selectedPlaceCategoryDAO.select(code);
                callback.onReceiveResult(selectedPlaceCategory);
            }
        });
    }

    @Override
    public void selectConvertedSelected(CarrierMessagingService.ResultCallback<List<PlaceCategoryDTO>> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                //선택된 카테고리 리스트를 가져옴
                List<SelectedPlaceCategoryDTO> selectedCategoryList = selectedPlaceCategoryDAO.select();

                //기본, 커스텀 카테고리 리스트 설정
                Map<String, String> defaultCategoryMap = KakaoLocalApiCategoryUtil.getDefaultPlaceCategoryMap();
                List<PlaceCategoryDTO> categories = new ArrayList<>();

                //커스텀 카테고리를 전체 카테고리로 포함시킨다
                for (SelectedPlaceCategoryDTO selectedPlaceCategory : selectedCategoryList)
                {
                    PlaceCategoryDTO placeCategoryDTO = new PlaceCategoryDTO();
                    placeCategoryDTO.setCode(selectedPlaceCategory.getCode());

                    if (defaultCategoryMap.containsKey(selectedPlaceCategory.getCode()))
                    {
                        placeCategoryDTO.setDescription(defaultCategoryMap.get(selectedPlaceCategory.getCode()));
                    } else
                    {
                        placeCategoryDTO.setCustom(true);
                        placeCategoryDTO.setDescription(selectedPlaceCategory.getCode());
                    }
                    categories.add(placeCategoryDTO);
                }

                callback.onReceiveResult(categories);
            }
        });
    }

    @Override
    public void insertAllSelected(List<PlaceCategoryDTO> list, CarrierMessagingService.ResultCallback<List<SelectedPlaceCategoryDTO>> callback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                for (PlaceCategoryDTO placeCategoryDTO : list)
                {
                    selectedPlaceCategoryDAO.insert(placeCategoryDTO.getCode());
                }
                List<SelectedPlaceCategoryDTO> selectedPlaceCategoryList = selectedPlaceCategoryDAO.select();
                callback.onReceiveResult(selectedPlaceCategoryList);
            }
        });
    }
}
