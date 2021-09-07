package com.zerodsoft.calendarplatform.activity.placecategory.repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.calendarplatform.activity.App;
import com.zerodsoft.calendarplatform.activity.placecategory.interfaces.IPlaceCategory;
import com.zerodsoft.calendarplatform.activity.placecategory.model.PlaceCategoryData;
import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.calendarplatform.room.dao.CustomPlaceCategoryDAO;
import com.zerodsoft.calendarplatform.room.dto.CustomPlaceCategoryDTO;
import com.zerodsoft.calendarplatform.room.dto.PlaceCategoryDTO;
import com.zerodsoft.calendarplatform.room.AppDb;
import com.zerodsoft.calendarplatform.room.dao.SelectedPlaceCategoryDAO;
import com.zerodsoft.calendarplatform.room.dto.SelectedPlaceCategoryDTO;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlaceCategoryRepository implements IPlaceCategory {
	private final SelectedPlaceCategoryDAO selectedPlaceCategoryDAO;
	private final CustomPlaceCategoryDAO customPlaceCategoryDAO;
	private MutableLiveData<List<PlaceCategoryDTO>> convertedSelectedCategoriesLiveData = new MutableLiveData<>();
	private MutableLiveData<PlaceCategoryDTO> onSelectedCategoryLiveData = new MutableLiveData<>();
	private MutableLiveData<String> onUnSelectedCategoryLiveData = new MutableLiveData<>();

	public PlaceCategoryRepository(Context context) {
		selectedPlaceCategoryDAO = AppDb.getInstance(context).selectedPlaceCategoryDAO();
		customPlaceCategoryDAO = AppDb.getInstance(context).customPlaceCategoryDAO();
	}

	@Override
	public void addCustom(String code, DbQueryCallback<CustomPlaceCategoryDTO> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				//custom category에 추가
				customPlaceCategoryDAO.insert(code);
				CustomPlaceCategoryDTO customPlaceCategory = customPlaceCategoryDAO.select(code);
				callback.processResult(customPlaceCategory);
			}
		});
	}

	@Override
	public void getCustom(DbQueryCallback<List<CustomPlaceCategoryDTO>> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				List<CustomPlaceCategoryDTO> list = customPlaceCategoryDAO.select();
				callback.processResult(list);
			}
		});

	}

	@Override
	public void deleteCustom(String code, DbQueryCallback<Boolean> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				// custom category와 selected category 모두 삭제
				selectedPlaceCategoryDAO.delete(code);
				customPlaceCategoryDAO.delete(code);
				callback.processResult(true);
			}
		});
	}

	@Override
	public void deleteAllCustom(DbQueryCallback<Boolean> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				// selected category에서 커스텀 모두 삭제, custom category모두 삭제
				List<CustomPlaceCategoryDTO> customs = customPlaceCategoryDAO.select();
				for (CustomPlaceCategoryDTO custom : customs) {
					selectedPlaceCategoryDAO.delete(custom.getCode());
				}
				customPlaceCategoryDAO.deleteAll();
				callback.processResult(true);
			}
		});

	}

	@Override
	public void addSelected(String code, DbQueryCallback<SelectedPlaceCategoryDTO> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				selectedPlaceCategoryDAO.add(code);
				SelectedPlaceCategoryDTO selectedPlaceCategory = selectedPlaceCategoryDAO.get(code);
				callback.processResult(selectedPlaceCategory);

				PlaceCategoryDTO placeCategoryDTO = new PlaceCategoryDTO();
				placeCategoryDTO.setCode(selectedPlaceCategory.getCode());

				if (KakaoLocalApiCategoryUtil.containsDefault(selectedPlaceCategory.getCode())) {
					placeCategoryDTO.setDescription(KakaoLocalApiCategoryUtil.getDefaultDescription(selectedPlaceCategory.getCode()));
				} else {
					placeCategoryDTO.setCustom(true);
					placeCategoryDTO.setDescription(selectedPlaceCategory.getCode());
				}

				onSelectedCategoryLiveData.postValue(placeCategoryDTO);
			}
		});
	}

	@Override
	public void deleteSelected(String code, @Nullable DbQueryCallback<Boolean> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				selectedPlaceCategoryDAO.delete(code);
				if (callback != null) {
					callback.processResult(true);
				}
				onUnSelectedCategoryLiveData.postValue(code);
			}
		});
	}

	@Override
	public void deleteAllSelected(DbQueryCallback<Boolean> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				selectedPlaceCategoryDAO.deleteAll();
				callback.processResult(true);
			}
		});
	}

	@Override
	public void getSelected(DbQueryCallback<List<SelectedPlaceCategoryDTO>> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				List<SelectedPlaceCategoryDTO> list = selectedPlaceCategoryDAO.getAll();
				callback.processResult(list);
			}
		});

	}

	@Override
	public void getSettingsData(DbQueryCallback<PlaceCategoryData> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				//커스텀 카테고리 모든 데이터를 가져옴
				List<CustomPlaceCategoryDTO> customAllList = customPlaceCategoryDAO.select();

				//선택된 카테고리 리스트를 가져옴
				List<SelectedPlaceCategoryDTO> selectedCategorylist = selectedPlaceCategoryDAO.getAll();

				//기본, 커스텀 카테고리 리스트 설정
				List<PlaceCategoryDTO> defaultAllPlaceCategories = KakaoLocalApiCategoryUtil.getDefaultPlaceCategoryList();
				List<PlaceCategoryDTO> customAllCategories = new ArrayList<>();

				//커스텀 카테고리를 전체 카테고리로 포함시킨다
				for (CustomPlaceCategoryDTO customPlaceCategory : customAllList) {
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

				callback.processResult(placeCategoryData);
			}
		});

	}

	@Override
	public void contains(String code, DbQueryCallback<Boolean> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				boolean containsCode = customPlaceCategoryDAO.containsCode(code) == 1;

				if (!containsCode) {
					Map<String, String> defaultAllPlaceCategoryMap = KakaoLocalApiCategoryUtil.getDefaultPlaceCategoryMap();

					if (defaultAllPlaceCategoryMap.containsKey(code) || defaultAllPlaceCategoryMap.containsValue(code)) {
						containsCode = true;
					}
				}
				callback.processResult(containsCode);
			}
		});
	}

	@Override
	public void selectConvertedSelected(DbQueryCallback<List<PlaceCategoryDTO>> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				//선택된 카테고리 리스트를 가져옴
				List<SelectedPlaceCategoryDTO> selectedCategoryList = selectedPlaceCategoryDAO.getAll();

				//기본, 커스텀 카테고리 리스트 설정
				Map<String, String> defaultCategoryMap = KakaoLocalApiCategoryUtil.getDefaultPlaceCategoryMap();
				List<PlaceCategoryDTO> categories = new ArrayList<>();

				//커스텀 카테고리를 전체 카테고리로 포함시킨다
				for (SelectedPlaceCategoryDTO selectedPlaceCategory : selectedCategoryList) {
					PlaceCategoryDTO placeCategoryDTO = new PlaceCategoryDTO();
					placeCategoryDTO.setCode(selectedPlaceCategory.getCode());

					if (defaultCategoryMap.containsKey(selectedPlaceCategory.getCode())) {
						placeCategoryDTO.setDescription(defaultCategoryMap.get(selectedPlaceCategory.getCode()));
					} else {
						placeCategoryDTO.setCustom(true);
						placeCategoryDTO.setDescription(selectedPlaceCategory.getCode());
					}
					categories.add(placeCategoryDTO);
				}

				callback.processResult(categories);
			}
		});
	}

	@Override
	public void addAllSelected(List<PlaceCategoryDTO> list, DbQueryCallback<List<SelectedPlaceCategoryDTO>> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				for (PlaceCategoryDTO placeCategoryDTO : list) {
					selectedPlaceCategoryDAO.add(placeCategoryDTO.getCode());
				}
				List<SelectedPlaceCategoryDTO> selectedPlaceCategoryList = selectedPlaceCategoryDAO.getAll();
				callback.processResult(selectedPlaceCategoryList);
			}
		});
	}

	public MutableLiveData<List<PlaceCategoryDTO>> getConvertedSelectedCategoriesLiveData() {
		return convertedSelectedCategoriesLiveData;
	}

	public MutableLiveData<PlaceCategoryDTO> getOnSelectedCategoryLiveData() {
		return onSelectedCategoryLiveData;
	}

	public MutableLiveData<String> getOnUnSelectedCategoryLiveData() {
		return onUnSelectedCategoryLiveData;
	}
}
