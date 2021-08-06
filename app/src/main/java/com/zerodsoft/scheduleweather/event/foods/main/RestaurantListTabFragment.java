package com.zerodsoft.scheduleweather.event.foods.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

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
import com.zerodsoft.scheduleweather.event.foods.interfaces.RestaurantListListener;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.CustomFoodMenuViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.RestaurantSharedViewModel;
import com.zerodsoft.scheduleweather.event.main.NewInstanceMainFragment;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnExtraListDataListener;
import com.zerodsoft.scheduleweather.navermap.place.PlaceInfoWebFragment;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RestaurantListTabFragment extends Fragment implements NewInstanceMainFragment.RestaurantsGetter, OnExtraListDataListener<Integer>,
		OnHiddenFragmentListener
		, OnClickedListItem<FoodCategoryItem>, RestaurantListListener, HeaderRestaurantListFragment.OnSelectedRestaurantTabListener {
	private FragmentFoodCategoryTabBinding binding;
	private ISetFoodMenuPoiItems ISetFoodMenuPoiItems;

	private CustomFoodMenuViewModel customFoodCategoryViewModel;
	private FavoriteLocationViewModel favoriteRestaurantViewModel;
	private RestaurantSharedViewModel restaurantSharedViewModel;

	private RestaurantListListener restaurantListListenerInMap;

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
		restaurantListListenerInMap = (RestaurantListListener) getParentFragment().getParentFragment().getParentFragment();

		Bundle bundle = getArguments();
		firstSelectedFoodMenuIndex = bundle.getInt("firstSelectedFoodMenuIndex");

		restaurantSharedViewModel = new ViewModelProvider(getParentFragment().getParentFragment()).get(RestaurantSharedViewModel.class);
		eventId = restaurantSharedViewModel.getEventId();
		ISetFoodMenuPoiItems = restaurantSharedViewModel.getISetFoodMenuPoiItems();

		favoriteRestaurantViewModel =
				new ViewModelProvider(requireActivity()).get(FavoriteLocationViewModel.class);
		customFoodCategoryViewModel = new ViewModelProvider(requireActivity()).get(CustomFoodMenuViewModel.class);

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

		binding.viewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				if (adapter.getFragments().get(position).adapter != null && headerRestaurantListFragment.getViewPagerVisibility() == View.GONE) {
					restaurantListListenerInMap.onLoadedInitialRestaurantList(null,
							adapter.getFragments().get(position).adapter.getCurrentList().snapshot());
				}
			}
		});

		headerRestaurantListFragment = new HeaderRestaurantListFragment();
		headerRestaurantListFragment.setOnSelectedRestaurantTabListener(this);
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
		final int index = binding.viewpager.getCurrentItem();
		RestaurantListFragment fragment = adapter.getFragments().get(index);

		if (fragment.adapter == null) {
			fragment.setAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
				@Override
				public void onItemRangeInserted(int positionStart, int itemCount) {
					if (itemCount > 0) {
						callback.onResultSuccessful(fragment.adapter.getCurrentList().snapshot());
					}
				}

				@Override
				public void onItemRangeChanged(int positionStart, int itemCount) {
					callback.onResultNoData();
				}
			});
		} else {
			callback.onResultSuccessful(fragment.adapter.getCurrentList().snapshot());
		}
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
		fragment.binding.recyclerView.scrollBy(0, 20000);
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

	@Override
	public void onLoadedInitialRestaurantList(String query, @Nullable List<PlaceDocuments> restaurantList) {
		if (headerRestaurantListFragment.getViewPagerVisibility() == View.GONE) {
			int index = headerRestaurantListFragment.getSelectedTabPosition();
			if (adapter.getFragments().get(index).query != null) {
				if (adapter.getFragments().get(index).query.equals(query)) {
					if (adapter.getFragments().get(index).adapter.getCurrentList().size() == 0) {
						restaurantListListenerInMap.onLoadedInitialRestaurantList(query, new ArrayList<>());
					} else {
						restaurantListListenerInMap.onLoadedInitialRestaurantList(query, adapter.getFragments().get(index)
								.adapter.getCurrentList().snapshot());
					}

				}
			}
		}

	}

	@Override
	public void onLoadedExtraRestaurantList(String query, List<PlaceDocuments> restaurantList) {
		int index = binding.viewpager.getCurrentItem();
		if (headerRestaurantListFragment.getViewPagerVisibility() == View.GONE) {
			if (adapter.getFragments().get(index).query.equals(query)) {
				restaurantListListenerInMap.onLoadedExtraRestaurantList(query, restaurantList);
			}
		}
	}

	@Override
	public void onSelectedRestaurantTab() {
		restaurantListListenerInMap.onLoadedInitialRestaurantList(null,
				adapter.getFragments().get(headerRestaurantListFragment.getSelectedTabPosition()).adapter.getCurrentList().snapshot());
	}
}
