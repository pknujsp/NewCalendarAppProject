package com.zerodsoft.scheduleweather.event.foods.header;

import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.DataProcessingCallback;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.databinding.FragmentHeaderRestaurantListBinding;
import com.zerodsoft.scheduleweather.event.foods.dto.FoodCategoryItem;
import com.zerodsoft.scheduleweather.event.foods.interfaces.IOnSetView;
import com.zerodsoft.scheduleweather.event.foods.interfaces.ISetFoodMenuPoiItems;
import com.zerodsoft.scheduleweather.event.foods.interfaces.RestaurantListListener;
import com.zerodsoft.scheduleweather.event.foods.main.RestaurantListTabFragment;
import com.zerodsoft.scheduleweather.event.foods.share.CriteriaLocationCloud;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.CustomFoodMenuViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.RestaurantSharedViewModel;
import com.zerodsoft.scheduleweather.event.main.NewInstanceMainFragment;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnExtraListDataListener;
import com.zerodsoft.scheduleweather.navermap.viewmodel.MapSharedViewModel;
import com.zerodsoft.scheduleweather.room.dto.CustomFoodMenuDTO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HeaderRestaurantListFragment extends Fragment {
	private FragmentHeaderRestaurantListBinding binding;
	private CustomFoodMenuViewModel customFoodCategoryViewModel;
	private RestaurantSharedViewModel restaurantSharedViewModel;
	private ViewPager2 viewPager2;
	private Integer firstSelectedFoodMenuIndex;
	private IOnSetView iOnSetView;
	private DataProcessingCallback<List<FoodCategoryItem>> foodMenuListDataProcessingCallback;
	private ISetFoodMenuPoiItems iSetFoodMenuPoiItems;
	private BottomSheetController bottomSheetController;
	private MapSharedViewModel mapSharedViewModel;
	private OnSelectedRestaurantTabListener onSelectedRestaurantTabListener;

	private int viewPagerVisibility = View.VISIBLE;

	private View.OnClickListener viewChangeBtnClickedListener;

	public void setViewChangeBtnClickedListener(View.OnClickListener viewChangeBtnClickedListener) {
		this.viewChangeBtnClickedListener = viewChangeBtnClickedListener;
	}

	public int getViewPagerVisibility() {
		return viewPagerVisibility;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		customFoodCategoryViewModel = new ViewModelProvider(requireActivity()).get(CustomFoodMenuViewModel.class);
		restaurantSharedViewModel = new ViewModelProvider(getParentFragment().getParentFragment()).get(RestaurantSharedViewModel.class);
		mapSharedViewModel = new ViewModelProvider(getParentFragment().getParentFragment().getParentFragment()).get(MapSharedViewModel.class);

		bottomSheetController = mapSharedViewModel.getBottomSheetController();
		iSetFoodMenuPoiItems = restaurantSharedViewModel.getISetFoodMenuPoiItems();
		iOnSetView = (IOnSetView) getParentFragment();
		iOnSetView.setFragmentContainerHeight((int) getResources().getDimension(R.dimen.restaurant_list_header_height));
		Bundle bundle = getArguments();

		firstSelectedFoodMenuIndex = bundle.getInt("firstSelectedFoodMenuIndex");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentHeaderRestaurantListBinding.inflate(inflater);
		return binding.getRoot();
	}

	public int getSelectedTabPosition() {
		return binding.tabLayout.getSelectedTabPosition();
	}


	public void setOnSelectedRestaurantTabListener(OnSelectedRestaurantTabListener onSelectedRestaurantTabListener) {
		this.onSelectedRestaurantTabListener = onSelectedRestaurantTabListener;
	}

	public void setViewPager2(ViewPager2 viewPager2) {
		this.viewPager2 = viewPager2;
	}

	public void setFoodMenuListDataProcessingCallback(DataProcessingCallback<List<FoodCategoryItem>> foodMenuListDataProcessingCallback) {
		this.foodMenuListDataProcessingCallback = foodMenuListDataProcessingCallback;
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		RestaurantListTabFragment listTabFragment =
				(RestaurantListTabFragment) getParentFragmentManager().findFragmentByTag(getString(R.string.tag_restaurant_list_tab_fragment));
		iSetFoodMenuPoiItems.createRestaurantPoiItems((NewInstanceMainFragment.RestaurantsGetter) listTabFragment
				, (OnExtraListDataListener<Integer>) listTabFragment);
		iSetFoodMenuPoiItems.createCriteriaLocationMarker(CriteriaLocationCloud.getName(), CriteriaLocationCloud.getLatitude(),
				CriteriaLocationCloud.getLongitude());

		binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				if (viewPagerVisibility == View.GONE) {
					bottomSheetController.setStateOfBottomSheet(BottomSheetType.LOCATION_ITEM, BottomSheetBehavior.STATE_COLLAPSED);
				}
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});

		binding.viewChangeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				bottomSheetController.setStateOfBottomSheet(BottomSheetType.LOCATION_ITEM, BottomSheetBehavior.STATE_COLLAPSED);
				viewPagerVisibility = viewPagerVisibility == View.VISIBLE ? View.GONE : View.VISIBLE;
				onClickedChangeBtn();
			}
		});

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

				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (foodMenuListDataProcessingCallback != null) {
							foodMenuListDataProcessingCallback.processResult(itemsList);
						}

						new TabLayoutMediator(binding.tabLayout, viewPager2,
								new TabLayoutMediator.TabConfigurationStrategy() {
									@Override
									public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
									}
								}
						).attach();
						setupTabCustomView(itemsList);
						binding.tabLayout.selectTab(binding.tabLayout.getTabAt(firstSelectedFoodMenuIndex));
					}
				});

			}

			@Override
			public void onResultNoData() {

			}
		});

	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);

		if (hidden) {
			iSetFoodMenuPoiItems.removeRestaurantPoiItems();
		} else {
			RestaurantListTabFragment listTabFragment =
					(RestaurantListTabFragment) getParentFragmentManager().findFragmentByTag(getString(R.string.tag_restaurant_list_tab_fragment));

			iSetFoodMenuPoiItems.createRestaurantPoiItems(listTabFragment, listTabFragment);
			iSetFoodMenuPoiItems.createCriteriaLocationMarker(CriteriaLocationCloud.getName(), CriteriaLocationCloud.getLatitude(),
					CriteriaLocationCloud.getLongitude());

			binding.tabLayout.selectTab(binding.tabLayout.getTabAt(binding.tabLayout.getSelectedTabPosition()));
		}
	}

	public void onClickedChangeBtn() {
		FragmentManager fragmentManager = getParentFragmentManager();

		if (viewPagerVisibility == View.GONE) {
			onSelectedRestaurantTabListener.onSelectedRestaurantTab();

			setTextOfChangeBtn(viewPagerVisibility);
			iOnSetView.setFragmentContainerVisibility(IOnSetView.ViewType.CONTENT, viewPagerVisibility);

			Fragment contentFragment = fragmentManager.findFragmentByTag(getString(R.string.tag_restaurant_list_tab_fragment));
			fragmentManager.beginTransaction().hide(contentFragment)
					.addToBackStack(getString(R.string.tag_showing_restaurant_list_on_map)).commit();

		} else {
			fragmentManager.popBackStackImmediate();
			iSetFoodMenuPoiItems.removeRestaurantPoiItems();
		}
	}

	public void setTextOfChangeBtn(int visibility) {
		binding.viewChangeBtn.setText(visibility == View.GONE ? R.string.open_list : R.string.open_map);
	}

	public void popedBackStack() {
		viewPagerVisibility = View.VISIBLE;
		bottomSheetController.setStateOfBottomSheet(BottomSheetType.LOCATION_ITEM, BottomSheetBehavior.STATE_COLLAPSED);
		iOnSetView.setFragmentContainerVisibility(IOnSetView.ViewType.CONTENT, viewPagerVisibility);
		setTextOfChangeBtn(viewPagerVisibility);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		iSetFoodMenuPoiItems.removeRestaurantPoiItems();
		iOnSetView.setFragmentContainerHeight((int) getResources().getDimension(R.dimen.restaurant_header_height));
	}

	private void setupTabCustomView(List<FoodCategoryItem> foodCategoryItemList) {
		final int itemViewSize = binding.tabLayout.getHeight();

		int index = 0;
		for (FoodCategoryItem foodCategoryItem : foodCategoryItemList) {
			LinearLayout itemView = (LinearLayout) getLayoutInflater().inflate(R.layout.food_category_item, binding.tabLayout, false);

			itemView.getLayoutParams().width = itemViewSize;
			itemView.getLayoutParams().height = itemViewSize;

			ImageView foodImgView = (ImageView) itemView.findViewById(R.id.food_category_image);
			TextView foodMenuNameTextView = (TextView) itemView.findViewById(R.id.food_category_name);
			foodMenuNameTextView.setText(foodCategoryItem.getCategoryName());

			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) foodMenuNameTextView.getLayoutParams();

			if (foodCategoryItem.isDefault()) {
				foodImgView.setImageDrawable(foodCategoryItem.getCategoryMainImage());
				Glide.with(itemView).load(foodCategoryItem.getCategoryMainImage()).circleCrop().into(foodImgView);

				layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
				foodImgView.setVisibility(View.VISIBLE);
			} else {
				layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
				foodImgView.setVisibility(View.GONE);
			}
			binding.tabLayout.getTabAt(index++).setCustomView(itemView);
		}
	}

	public interface OnSelectedRestaurantTabListener {
		void onSelectedRestaurantTab();
	}
}