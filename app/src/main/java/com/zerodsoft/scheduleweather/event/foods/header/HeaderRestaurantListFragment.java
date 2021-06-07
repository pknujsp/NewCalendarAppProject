package com.zerodsoft.scheduleweather.event.foods.header;

import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.DataProcessingCallback;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.databinding.FragmentHeaderRestaurantListBinding;
import com.zerodsoft.scheduleweather.event.foods.dto.FoodCategoryItem;
import com.zerodsoft.scheduleweather.event.foods.interfaces.IOnSetView;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.CustomFoodMenuViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.RestaurantSharedViewModel;
import com.zerodsoft.scheduleweather.room.dto.CustomFoodMenuDTO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HeaderRestaurantListFragment extends Fragment {
	private FragmentHeaderRestaurantListBinding binding;
	private CustomFoodMenuViewModel customFoodCategoryViewModel;
	private RestaurantSharedViewModel restaurantSharedViewModel;
	private OnClickedListItem<FoodCategoryItem> onClickedListItem;
	private ViewPager2 viewPager2;
	private Integer firstSelectedFoodMenuIndex;
	private IOnSetView iOnSetView;
	private DataProcessingCallback<List<FoodCategoryItem>> foodMenuListDataProcessingCallback;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		customFoodCategoryViewModel = new ViewModelProvider(requireActivity()).get(CustomFoodMenuViewModel.class);
		restaurantSharedViewModel = new ViewModelProvider(requireActivity()).get(RestaurantSharedViewModel.class);

		iOnSetView = (IOnSetView) getParentFragment();
		iOnSetView.setHeaderHeight((int) getResources().getDimension(R.dimen.restaurant_list_header_height));
		Bundle bundle = getArguments();

		onClickedListItem = (OnClickedListItem<FoodCategoryItem>) bundle.getSerializable("OnClickedListItem");
		firstSelectedFoodMenuIndex = bundle.getInt("firstSelectedFoodMenuIndex");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentHeaderRestaurantListBinding.inflate(inflater);
		return binding.getRoot();
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

		binding.viewChangeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

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
	public void onDestroy() {
		super.onDestroy();
		iOnSetView.setHeaderHeight((int) getResources().getDimension(R.dimen.restaurant_header_height));
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

}