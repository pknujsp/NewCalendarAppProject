package com.zerodsoft.scheduleweather.event.foods.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.zerodsoft.scheduleweather.event.foods.interfaces.ISetFoodMenuPoiItems;
import com.zerodsoft.scheduleweather.event.foods.interfaces.IOnSetView;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.CustomFoodMenuViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.RestaurantSharedViewModel;
import com.zerodsoft.scheduleweather.event.main.NewInstanceMainFragment;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnExtraListDataListener;
import com.zerodsoft.scheduleweather.navermap.place.PlaceInfoWebFragment;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RestaurantListTabFragment extends Fragment implements NewInstanceMainFragment.RestaurantsGetter, OnExtraListDataListener<Integer>,
		OnHiddenFragmentListener
		, OnClickedListItem<FoodCategoryItem> {
	private FragmentFoodCategoryTabBinding binding;
	private ISetFoodMenuPoiItems ISetFoodMenuPoiItems;

	private CustomFoodMenuViewModel customFoodCategoryViewModel;
	private FavoriteLocationViewModel favoriteRestaurantViewModel;
	private RestaurantSharedViewModel restaurantSharedViewModel;

	private FoodCategoryFragmentListAdapter adapter;
	private int firstSelectedFoodMenuIndex;
	private Long eventId;

	private IOnSetView iOnSetView;

	private HeaderRestaurantListFragment headerRestaurantListFragment;

	private final FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
		@Override
		public void onFragmentCreated(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
			super.onFragmentCreated(fm, f, savedInstanceState);
			if (f instanceof PlaceInfoWebFragment) {
				iOnSetView.setFragmentContainerVisibility(IOnSetView.ViewType.HEADER, View.GONE);
			}
		}

		@Override
		public void onFragmentDestroyed(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f) {
			super.onFragmentDestroyed(fm, f);
			if (f instanceof PlaceInfoWebFragment) {
				iOnSetView.setFragmentContainerVisibility(IOnSetView.ViewType.HEADER, View.VISIBLE);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		iOnSetView = (IOnSetView) getParentFragment();

		getParentFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true);

		Bundle bundle = getArguments();
		firstSelectedFoodMenuIndex = bundle.getInt("firstSelectedFoodMenuIndex");

		restaurantSharedViewModel = new ViewModelProvider(getParentFragment().getParentFragment()).get(RestaurantSharedViewModel.class);
		eventId = restaurantSharedViewModel.getEventId();
		ISetFoodMenuPoiItems = restaurantSharedViewModel.getISetFoodMenuPoiItems();

		favoriteRestaurantViewModel =
				new ViewModelProvider(getParentFragment().getParentFragment().getParentFragment()).get(FavoriteLocationViewModel.class);
		customFoodCategoryViewModel = new ViewModelProvider(this).get(CustomFoodMenuViewModel.class);

		iOnSetView.setFragmentContainerVisibility(IOnSetView.ViewType.HEADER, View.VISIBLE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		iOnSetView.setFragmentContainerVisibility(IOnSetView.ViewType.HEADER, View.VISIBLE);
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
		bundle.putInt("firstSelectedFoodMenuIndex", firstSelectedFoodMenuIndex);
		headerRestaurantListFragment.setArguments(bundle);

		getParentFragmentManager().beginTransaction()
				.add(R.id.header_fragment_container, headerRestaurantListFragment, getString(R.string.tag_header_food_menu_list_fragment)).commit();
	}

	@Override
	public void getRestaurants(DbQueryCallback<List<PlaceDocuments>> callback) {
		final int index = headerRestaurantListFragment.getSelectedTabPosition();
		RestaurantListFragment fragment = adapter.getFragments().get(index);

		if (fragment.adapter == null) {
			fragment.setAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
				@Override
				public void onItemRangeInserted(int positionStart, int itemCount) {
					if (itemCount > 0) {
						callback.onResultSuccessful(fragment.adapter.getCurrentList().snapshot());
						fragment.adapterDataObserver = null;
					}
				}

				@Override
				public void onItemRangeChanged(int positionStart, int itemCount) {
					super.onItemRangeChanged(positionStart, itemCount);
					callback.onResultNoData();
					fragment.adapterDataObserver = null;
				}
			});
		} else {
			callback.processResult(fragment.adapter.getCurrentList().snapshot());
		}
		binding.viewpager.setCurrentItem(index, false);
	}

	@Override
	public void loadExtraListData(Integer index, RecyclerView.AdapterDataObserver adapterDataObserver) {
		RestaurantListFragment fragment = adapter.getFragments().get(headerRestaurantListFragment.getSelectedTabPosition());

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
		if (hidden) {

		} else {
			headerRestaurantListFragment.popedBackStack();
		}
	}


	@Override
	public void onClickedListItem(FoodCategoryItem e, int position) {

	}

	@Override
	public void deleteListItem(FoodCategoryItem e, int position) {

	}
}
