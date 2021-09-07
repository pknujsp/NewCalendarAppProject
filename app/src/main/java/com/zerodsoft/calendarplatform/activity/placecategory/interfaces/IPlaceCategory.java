package com.zerodsoft.calendarplatform.activity.placecategory.interfaces;

import com.zerodsoft.calendarplatform.activity.placecategory.model.PlaceCategoryData;
import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.room.dto.CustomPlaceCategoryDTO;
import com.zerodsoft.calendarplatform.room.dto.PlaceCategoryDTO;
import com.zerodsoft.calendarplatform.room.dto.SelectedPlaceCategoryDTO;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IPlaceCategory {
	//custom
	void addCustom(String code, DbQueryCallback<CustomPlaceCategoryDTO> callback);

	void getCustom(DbQueryCallback<List<CustomPlaceCategoryDTO>> callback);

	void deleteCustom(String code, DbQueryCallback<Boolean> callback);

	void deleteAllCustom(DbQueryCallback<Boolean> callback);

	void contains(String code, DbQueryCallback<Boolean> callback);

	//selected
	void addSelected(String code, DbQueryCallback<SelectedPlaceCategoryDTO> callback);

	void addAllSelected(List<PlaceCategoryDTO> list, DbQueryCallback<List<SelectedPlaceCategoryDTO>> callback);

	void deleteSelected(String code, @Nullable DbQueryCallback<Boolean> callback);

	void deleteAllSelected(DbQueryCallback<Boolean> callback);

	void getSelected(DbQueryCallback<List<SelectedPlaceCategoryDTO>> callback);

	void getSettingsData(DbQueryCallback<PlaceCategoryData> callback);

	void selectConvertedSelected(DbQueryCallback<List<PlaceCategoryDTO>> callback);
}
