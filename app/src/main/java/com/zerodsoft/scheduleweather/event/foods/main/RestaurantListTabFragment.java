package com.zerodsoft.scheduleweather.event.foods.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.DataProcessingCallback;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.common.interfaces.OnHiddenFragmentListener;
import com.zerodsoft.scheduleweather.databinding.FragmentFoodCategoryTabBinding;
import com.zerodsoft.scheduleweather.event.foods.adapter.FoodCategoryFragmentListAdapter;
import com.zerodsoft.scheduleweather.event.foods.dto.FoodCategoryItem;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteLocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.header.HeaderRestaurantListFragment;
import com.zerodsoft.scheduleweather.event.foods.interfaces.FoodMenuChipsViewController;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnSetViewVisibility;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.CustomFoodMenuViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.RestaurantSharedViewModel;
import com.zerodsoft.scheduleweather.event.main.NewInstanceMainFragment;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnExtraListDataListener;
import com.zerodsoft.scheduleweather.navermap.place.PlaceInfoWebFragment;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.CustomFoodMenuDTO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.SneakyThrows;

public class RestaurantListTabFragment extends Fragment implements NewInstanceMainFragment.RestaurantsGetter, OnExtraListDataListener<String>, OnHiddenFragmentListener
		, OnClickedListItem<FoodCategoryItem> {
	private FragmentFoodCategoryTabBinding binding;
	private FoodMenuChipsViewController foodMenuChipsViewController;

	private CustomFoodMenuViewModel customFoodCategoryViewModel;
	private FavoriteLocationViewModel favoriteRestaurantViewModel;
	private RestaurantSharedViewModel restaurantSharedViewModel;

	private FoodCategoryFragmentListAdapter adapter;
	private String firstSelectedFoodMenuName;
	private Long eventId;

	private OnSetViewVisibility onSetViewVisibility;

	private List<String> categoryList;

	private HeaderRestaurantListFragment headerRestaurantListFragment;

	private final FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
		@Override
		public void onFragmentCreated(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
			super.onFragmentCreated(fm, f, savedInstanceState);
			if (f instanceof PlaceInfoWebFragment) {
				onSetViewVisibility.setVisibility(OnSetViewVisibility.ViewType.HEADER, View.GONE);
			}
		}

		@Override
		public void onFragmentDestroyed(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f) {
			super.onFragmentDestroyed(fm, f);
			if (f instanceof PlaceInfoWebFragment) {
				onSetViewVisibility.setVisibility(OnSetViewVisibility.ViewType.HEADER, View.VISIBLE);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getParentFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);

		Bundle bundle = getArguments();
		firstSelectedFoodMenuName = bundle.getString("foodMenuName");

		restaurantSharedViewModel = new ViewModelProvider(requireActivity()).get(RestaurantSharedViewModel.class);
		eventId = restaurantSharedViewModel.getEventId();
		foodMenuChipsViewController = restaurantSharedViewModel.getFoodMenuChipsViewController();

		favoriteRestaurantViewModel = new ViewModelProvider(requireActivity()).get(FavoriteLocationViewModel.class);
		customFoodCategoryViewModel = new ViewModelProvider(requireActivity()).get(CustomFoodMenuViewModel.class);

		onSetViewVisibility = restaurantSharedViewModel.getOnSetViewVisibility();
		onSetViewVisibility.setVisibility(OnSetViewVisibility.ViewType.HEADER, View.VISIBLE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		onSetViewVisibility.setVisibility(OnSetViewVisibility.ViewType.HEADER, View.VISIBLE);
		FragmentManager fragmentManager = getParentFragment().getParentFragmentManager();

		Fragment criteriaHeaderFragment =
				fragmentManager.findFragmentByTag(getString(R.string.tag_restaurant_header_criteria_location_fragment));
		fragmentManager.beginTransaction().remove(headerRestaurantListFragment)
				.show(criteriaHeaderFragment).commit();

		getParentFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentFoodCategoryTabBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		adapter = new FoodCategoryFragmentListAdapter(RestaurantListTabFragment.this);
		binding.viewpager.setAdapter(adapter);

		headerRestaurantListFragment = new HeaderRestaurantListFragment();
		headerRestaurantListFragment.setViewPager2(binding.viewpager);
		headerRestaurantListFragment.setFoodMenuListDataProcessingCallback(new DataProcessingCallback<List<FoodCategoryItem>>() {
			@Override
			public void onResultSuccessful(List<FoodCategoryItem> result) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						adapter.init(result);
						adapter.notifyDataSetChanged();
					}
				});

			}

			@Override
			public void onResultNoData() {

			}
		});

		Bundle bundle = new Bundle();
		bundle.putSerializable("OnClickedListItem", (OnClickedListItem<FoodCategoryItem>) this);
		bundle.putInt("firstSelectedFoodMenuIndex", 0);
		headerRestaurantListFragment.setArguments(bundle);

		getParentFragment().getParentFragmentManager().beginTransaction()
				.add(R.id.header_fragment_container, headerRestaurantListFragment).commit();
	}

	@Override
	public void getRestaurants(String foodName, CarrierMessagingService.ResultCallback<List<PlaceDocuments>> callback) {
		final int index = categoryList.indexOf(foodName);
		RestaurantListFragment fragment = adapter.getFragments().get(index);

		if (fragment.adapter == null) {
			fragment.setAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
				@Override
				public void onItemRangeInserted(int positionStart, int itemCount) {
					requireActivity().runOnUiThread(new Runnable() {
						@SneakyThrows
						@Override
						public void run() {
							callback.onReceiveResult(fragment.adapter.getCurrentList().snapshot());
						}
					});

					fragment.adapterDataObserver = null;
				}
			});
			binding.viewpager.setCurrentItem(index);
		} else {
			requireActivity().runOnUiThread(new Runnable() {
				@SneakyThrows
				@Override
				public void run() {
					callback.onReceiveResult(fragment.adapter.getCurrentList().snapshot());
				}
			});
		}
	}

	@Override
	public void loadExtraListData(String foodMenuName, RecyclerView.AdapterDataObserver adapterDataObserver) {
		final int index = categoryList.indexOf(foodMenuName);
		RestaurantListFragment fragment = adapter.getFragments().get(index);

		fragment.adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				super.onItemRangeInserted(positionStart, itemCount);
				adapterDataObserver.onItemRangeInserted(positionStart, itemCount);
				fragment.adapter.unregisterAdapterDataObserver(this);
			}
		});
		fragment.binding.recyclerView.scrollBy(0, 10000);
	}

	@Override
	public void loadExtraListData(RecyclerView.AdapterDataObserver adapterDataObserver) {

	}

	@Override
	public void onHiddenChangedFragment(boolean hidden) {
		if (hidden) {

		} else {

		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}


	@Override
	public void onClickedListItem(FoodCategoryItem e, int position) {

	}

	@Override
	public void deleteListItem(FoodCategoryItem e, int position) {

	}
}
