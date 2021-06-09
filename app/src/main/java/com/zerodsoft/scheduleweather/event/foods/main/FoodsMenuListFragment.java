package com.zerodsoft.scheduleweather.event.foods.main;

import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.preferences.customfoodmenu.fragment.CustomFoodMenuSettingsFragment;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IRefreshView;
import com.zerodsoft.scheduleweather.common.interfaces.DataProcessingCallback;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.databinding.FragmentFoodMenusBinding;
import com.zerodsoft.scheduleweather.event.foods.adapter.FoodCategoryAdapter;
import com.zerodsoft.scheduleweather.event.foods.dto.FoodCategoryItem;
import com.zerodsoft.scheduleweather.event.foods.header.HeaderCriteriaLocationFragment;
import com.zerodsoft.scheduleweather.event.foods.interfaces.IOnSetView;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedCategoryItem;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.CustomFoodMenuViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.RestaurantSharedViewModel;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.room.dto.CustomFoodMenuDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FoodsMenuListFragment extends Fragment implements OnClickedCategoryItem, OnClickedListItem<FoodCategoryItem>,
		IRefreshView {
	public static final String TAG = "FoodsMenuListFragment";
	private final int COLUMN_COUNT = 5;
	private Long eventId;

	private FragmentFoodMenusBinding binding;
	private IMapPoint iMapPoint;

	private CustomFoodMenuViewModel customFoodCategoryViewModel;
	private RestaurantSharedViewModel sharedViewModel;
	private HeaderCriteriaLocationFragment headerCriteriaLocationFragment;

	private FoodCategoryAdapter foodCategoryAdapter;

	private IOnSetView iOnSetView;

	private FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
		@Override
		public void onFragmentCreated(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
			super.onFragmentCreated(fm, f, savedInstanceState);
		}

		@Override
		public void onFragmentDestroyed(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f) {
			super.onFragmentDestroyed(fm, f);
			if (f instanceof RestaurantListTabFragment) {
				fm.beginTransaction().remove(fm.findFragmentByTag(getString(R.string.tag_header_food_menu_list_fragment)))
						.show(headerCriteriaLocationFragment).commit();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getParentFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, false);

		sharedViewModel = new ViewModelProvider(requireActivity()).get(RestaurantSharedViewModel.class);
		customFoodCategoryViewModel = new ViewModelProvider(requireActivity()).get(CustomFoodMenuViewModel.class);

		eventId = sharedViewModel.getEventId();
		iMapPoint = sharedViewModel.getiMapPoint();

		iOnSetView = (IOnSetView) getParentFragment();
		iOnSetView.setFragmentContainerVisibility(IOnSetView.ViewType.HEADER, View.VISIBLE);

		headerCriteriaLocationFragment = new HeaderCriteriaLocationFragment();
		headerCriteriaLocationFragment.setFoodCriteriaLocationCallback(new DataProcessingCallback<LocationDTO>() {
			@Override
			public void onResultSuccessful(LocationDTO result) {
				setCategories();
			}

			@Override
			public void onResultNoData() {

			}
		});

		customFoodCategoryViewModel.getOnAddedCustomFoodMenuLiveData().observe(this, new Observer<CustomFoodMenuDTO>() {
			@Override
			public void onChanged(CustomFoodMenuDTO customFoodMenuDTO) {
				setCategories();
			}
		});

		customFoodCategoryViewModel.getOnRemovedCustomFoodMenuLiveData().observe(this, new Observer<Integer>() {
			@Override
			public void onChanged(Integer integer) {
				setCategories();
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentFoodMenusBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		FragmentManager parentFragmentManager = getParentFragmentManager();
		if (parentFragmentManager.findFragmentByTag(getString(R.string.tag_restaurant_header_criteria_location_fragment)) == null) {
			parentFragmentManager.beginTransaction()
					.replace(R.id.header_fragment_container, headerCriteriaLocationFragment, getString(R.string.tag_restaurant_header_criteria_location_fragment)).commit();
		}
		GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), COLUMN_COUNT);
		binding.categoryGridview.setLayoutManager(gridLayoutManager);
		foodCategoryAdapter = new FoodCategoryAdapter(FoodsMenuListFragment.this, COLUMN_COUNT);
		binding.categoryGridview.setAdapter(foodCategoryAdapter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getParentFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
	}

	private void setCategories() {
		customFoodCategoryViewModel.select(new DbQueryCallback<List<CustomFoodMenuDTO>>() {
			@Override
			public void onResultSuccessful(List<CustomFoodMenuDTO> resultList) {

				final String[] DEFAULT_FOOD_MENU_NAME_ARR = getResources().getStringArray(R.array.food_menu_list);
				List<FoodCategoryItem> itemsList = new ArrayList<>();

				TypedArray imgs = getResources().obtainTypedArray(R.array.food_menu_image_list);

				for (int index = 0; index < DEFAULT_FOOD_MENU_NAME_ARR.length; index++) {
					itemsList.add(new FoodCategoryItem(DEFAULT_FOOD_MENU_NAME_ARR[index]
							, imgs.getDrawable(index), true));
				}

				if (!resultList.isEmpty()) {
					for (CustomFoodMenuDTO customFoodCategory : resultList) {
						itemsList.add(new FoodCategoryItem(customFoodCategory.getMenuName(), null, false));
					}
				}
				itemsList.add(new FoodCategoryItem(getString(R.string.add_custom_food_menu), null, false));
				foodCategoryAdapter.setItems(itemsList);

				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						foodCategoryAdapter.notifyDataSetChanged();
					}
				});
			}

			@Override
			public void onResultNoData() {

			}
		});


	}

	@Override
	public void onClickedFoodCategory(FoodCategoryItem foodCategoryItem) {
	}


	@Override
	public void onClickedListItem(FoodCategoryItem e, int position) {
		FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
		fragmentTransaction.hide(this);
		String tag = null;

		if (binding.categoryGridview.getAdapter().getItemCount() - 1 == position) {
			CustomFoodMenuSettingsFragment customFoodMenuSettingsFragment = new CustomFoodMenuSettingsFragment();
			tag = getString(R.string.tag_custom_food_menu_settings_fragment);

			fragmentTransaction.add(R.id.content_fragment_container, customFoodMenuSettingsFragment, tag);
		} else {
			tag = getString(R.string.tag_restaurant_list_tab_fragment);

			RestaurantListTabFragment restaurantListTabFragment = new RestaurantListTabFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("firstSelectedFoodMenuIndex", position);
			restaurantListTabFragment.setArguments(bundle);

			fragmentTransaction.add(R.id.content_fragment_container, restaurantListTabFragment, tag);

			getParentFragmentManager().beginTransaction()
					.hide(headerCriteriaLocationFragment).commit();
		}

		fragmentTransaction.addToBackStack(tag).commit();
	}

	@Override
	public void deleteListItem(FoodCategoryItem e, int position) {

	}

	@Override
	public void refreshView() {

	}
}