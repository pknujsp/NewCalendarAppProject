package com.zerodsoft.scheduleweather.activity.placecategory.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

public class PlaceCategoryRepository implements IPlaceCategory
{
    private final SelectedPlaceCategoryDAO selectedPlaceCategoryDAO;
    private final CustomPlaceCategoryDAO customPlaceCategoryDAO;
    private MutableLiveData<List<SelectedPlaceCategoryDTO>> selectedPlaceCategoryListLiveData = new MutableLiveData<>();
    private MutableLiveData<List<CustomPlaceCategoryDTO>> customPlaceCategoryListLiveData = new MutableLiveData<>();
    private MutableLiveData<PlaceCategoryData> placeCategoryDataLiveData = new MutableLiveData<>();

    public PlaceCategoryRepository(Context context)
    {
        selectedPlaceCategoryDAO = AppDb.getInstance(context).selectedPlaceCategoryDAO();
        customPlaceCategoryDAO = AppDb.getInstance(context).customPlaceCategoryDAO();
    }

    public MutableLiveData<List<CustomPlaceCategoryDTO>> getCustomPlaceCategoryListLiveData()
    {
        return customPlaceCategoryListLiveData;
    }

    public MutableLiveData<List<SelectedPlaceCategoryDTO>> getSelectedPlaceCategoryListLiveData()
    {
        return selectedPlaceCategoryListLiveData;
    }

    public MutableLiveData<PlaceCategoryData> getPlaceCategoryDataLiveData()
    {
        return placeCategoryDataLiveData;
    }

    @Override
    public void insertCustom(String code)
    {
        customPlaceCategoryDAO.insert(code);
    }

    @Override
    public void selectCustom()
    {
        App.executorService.execute(new Runnable()
        {
            @Override
            public void run()
            {
                List<CustomPlaceCategoryDTO> list = customPlaceCategoryDAO.select();
                customPlaceCategoryListLiveData.postValue(list);
            }
        });

    }

    @Override
    public void updateCustom(int id, String code)
    {
        customPlaceCategoryDAO.update(id, code);
    }

    @Override
    public void deleteCustom(int id)
    {
        customPlaceCategoryDAO.delete(id);
    }

    @Override
    public void deleteAllCustom()
    {
        customPlaceCategoryDAO.deleteAll();
    }

    @Override
    public void insertSelected(String code)
    {
        selectedPlaceCategoryDAO.insert(code);
    }

    @Override
    public void deleteSelected(String code)
    {
        selectedPlaceCategoryDAO.delete(code);
    }

    @Override
    public void deleteAllSelected()
    {
        selectedPlaceCategoryDAO.deleteAll();
    }

    @Override
    public void selectSelected()
    {
        App.executorService.execute(new Runnable()
        {
            @Override
            public void run()
            {
                List<SelectedPlaceCategoryDTO> list = selectedPlaceCategoryDAO.select();
                selectedPlaceCategoryListLiveData.postValue(list);
            }
        });

    }

    @Override
    public void getSettingsData()
    {
        App.executorService.execute(new Runnable()
        {
            @Override
            public void run()
            {
                //커스텀 카테고리 모든 데이터를 가져옴
                List<CustomPlaceCategoryDTO> customAllList = customPlaceCategoryDAO.select();
                customPlaceCategoryListLiveData.postValue(customAllList);

                //선택된 카테고리 리스트를 가져옴
                List<SelectedPlaceCategoryDTO> selectedCategorylist = selectedPlaceCategoryDAO.select();
                selectedPlaceCategoryListLiveData.postValue(selectedCategorylist);

                //기본, 커스텀 카테고리 리스트 설정
                List<PlaceCategoryDTO> defaultAllPlaceCategories = KakaoLocalApiCategoryUtil.getList();
                List<PlaceCategoryDTO> customAllCategories = new ArrayList<>();

                //커스텀 카테고리를 전체 카테고리로 포함시킨다
                for (CustomPlaceCategoryDTO customPlaceCategory : customAllList)
                {
                    PlaceCategoryDTO placeCategoryDTO = new PlaceCategoryDTO();
                    placeCategoryDTO.setCode(customPlaceCategory.getCode());
                    placeCategoryDTO.setCustom(true);
                    customAllCategories.add(placeCategoryDTO);
                }

                PlaceCategoryData placeCategoryData = new PlaceCategoryData();
                placeCategoryData.setCustomCategories(customAllCategories);
                placeCategoryData.setDefaultPlaceCategories(defaultAllPlaceCategories);
                placeCategoryData.setSelectedPlaceCategories(selectedCategorylist);

                placeCategoryDataLiveData.postValue(placeCategoryData);
            }
        });

    }
}
