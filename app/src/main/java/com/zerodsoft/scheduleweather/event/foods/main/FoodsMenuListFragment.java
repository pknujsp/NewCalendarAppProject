package com.zerodsoft.scheduleweather.event.foods.main;

import android.content.Context;
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
import com.zerodsoft.scheduleweather.event.foods.interfaces.CriteriaLocationListener;
import com.zerodsoft.scheduleweather.event.foods.interfaces.IOnSetView;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedCategoryItem;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.CustomFoodMenuViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.RestaurantSharedViewModel;
import com.zerodsoft.scheduleweather.room.dto.CustomFoodMenuDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FoodsMenuListFragment extends Fragment implements OnClickedCategoryItem, OnClickedListItem<FoodCategoryItem>,
		IRefreshView {
	private final int COLUMN_COUNT = 4;

	private FragmentFoodMenusBinding binding;

	private CustomFoodMenuViewModel customFoodCategoryViewModel;
	private RestaurantSharedViewModel restaurantSharedViewModel;
	private HeaderCriteriaLocationFragment headerCriteriaLocationFragment;

	private FoodCategoryAdapter foodCategoryAdapter;
	private IOnSetView iOnSetView;

	private boolean initializing = true;

	private FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
		@Override
		public void onFragmentAttached(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f, @NonNull @NotNull Context context) {
			super.onFragmentAttached(fm, f, context);
			if (f instanceof CustomFoodMenuSettingsFragment) {
				iOnSetView.setFragmentContainerVisibility(IOnSetView.ViewType.HEADER, View.GONE);
			}
		}


		@Override
		public void onFragmentDestroyed(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f) {
			super.onFragmentDestroyed(fm, f);
			if (f instanceof RestaurantListTabFragment) {
				fm.beginTransaction().remove(fm.findFragmentByTag(getString(R.string.tag_header_food_menu_list_fragment)))
						.show(headerCriteriaLocationFragment).commit();
			} else if (f instanceof CustomFoodMenuSettingsFragment) {
				iOnSetView.setFragmentContainerVisibility(IOnSetView.ViewType.HEADER, View.VISIBLE);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getParentFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, false);

		restaurantSharedViewModel = new ViewModelProvider(getParentFragment().getParentFragment()).get(RestaurantSharedViewModel.class);
		customFoodCategoryViewModel = new ViewModelProvider(requireActivity()).get(CustomFoodMenuViewModel.class);

		iOnSetView = (IOnSetView) getParentFragment();
		iOnSetView.setFragmentContainerVisibility(IOnSetView.ViewType.HEADER, View.VISIBLE);

		customFoodCategoryViewModel.getOnAddedCustomFoodMenuLiveData().observe(this, new Observer<CustomFoodMenuDTO>() {
			@Override
			public void onChanged(CustomFoodMenuDTO customFoodMenuDTO) {
				if (!initializing) {
					setCategories();
				}
			}
		});

		customFoodCategoryViewModel.getOnRemovedCustomFoodMenuLiveData().observe(this, new Observer<Integer>() {
			@Override
			public void onChanged(Integer integer) {
				if (!initializing) {
					setCategories();
				}
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
		binding.customProgressView.setContentView(binding.categoryGridview);

		GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), COLUMN_COUNT);
		binding.categoryGridview.setLayoutManager(gridLayoutManager);
		foodCategoryAdapter = new FoodCategoryAdapter(FoodsMenuListFragment.this, COLUMN_COUNT);
		binding.categoryGridview.setAdapter(foodCategoryAdapter);

		setCategories();

		headerCriteriaLocationFragment = new HeaderCriteriaLocationFragment();
		headerCriteriaLocationFragment.setCriteriaLocationListener(new CriteriaLocationListener() {
			@Override
			public void onStartedGettingCriteriaLocation() {
				binding.customProgressView.onStartedProcessingData();
			}

			@Override
			public void onFinishedGettingCriteriaLocation(LocationDTO criteriaLocation) {
				binding.customProgressView.onSuccessfulProcessingData();
			}
		});

		FragmentManager parentFragmentManager = getParentFragmentManager();
		if (parentFragmentManager.findFragmentByTag(getString(R.string.tag_restaurant_header_criteria_location_fragment)) == null) {
			parentFragmentManager.beginTransaction()
					.replace(R.id.header_fragment_container, headerCriteriaLocationFragment, getString(R.string.tag_restaurant_header_criteria_location_fragment)).commit();
		}

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

				initializing = false;
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

			fragmentTransaction.hide(this).add(R.id.content_fragment_container, customFoodMenuSettingsFragment, tag);
		} else {
			tag = getString(R.string.tag_restaurant_list_tab_fragment);

			RestaurantListTabFragment restaurantListTabFragment = new RestaurantListTabFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("firstSelectedFoodMenuIndex", position);
			restaurantListTabFragment.setArguments(bundle);

			fragmentTransaction.hide(this).add(R.id.content_fragment_container, restaurantListTabFragment, tag);

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

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (hidden) {

		} else {

		}
	}
}