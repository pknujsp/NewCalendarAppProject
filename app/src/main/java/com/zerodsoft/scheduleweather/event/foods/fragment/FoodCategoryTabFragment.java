package com.zerodsoft.scheduleweather.event.foods.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentFoodCategoryTabBinding;
import com.zerodsoft.scheduleweather.event.foods.adapter.FoodCategoryFragmentListAdapter;
import com.zerodsoft.scheduleweather.event.foods.interfaces.CriteriaLocationListener;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedRestaurantItem;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.CustomFoodCategoryViewModel;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.CustomFoodCategoryDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FoodCategoryTabFragment extends DialogFragment implements OnClickedRestaurantItem
{
    public static final String TAG = "FoodCategoryTabFragment";
    private FragmentFoodCategoryTabBinding binding;
    private final CriteriaLocationListener criteriaLocationListener;

    private CustomFoodCategoryViewModel customFoodCategoryViewModel;
    private List<String> categoryList;
    private FoodCategoryFragmentListAdapter adapter;
    private final String selectedCategoryName;

    public FoodCategoryTabFragment(Fragment fragment, String selectedCategoryName)
    {
        this.criteriaLocationListener = (CriteriaLocationListener) fragment;
        this.selectedCategoryName = selectedCategoryName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentFoodCategoryTabBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        customFoodCategoryViewModel = new ViewModelProvider(this).get(CustomFoodCategoryViewModel.class);
        customFoodCategoryViewModel.select(new CarrierMessagingService.ResultCallback<List<CustomFoodCategoryDTO>>()
        {
            @Override
            public void onReceiveResult(@NonNull List<CustomFoodCategoryDTO> resultList) throws RemoteException
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        categoryList = new ArrayList<>();

                        String[] defaultCategoryList = getResources().getStringArray(R.array.food_category_list);
                        categoryList.addAll(Arrays.asList(defaultCategoryList));

                        if (!resultList.isEmpty())
                        {
                            for (CustomFoodCategoryDTO customFoodCategory : resultList)
                            {
                                categoryList.add(customFoodCategory.getCategoryName());
                            }
                        }

                        int selectedIndex = categoryList.indexOf(selectedCategoryName);

                        adapter = new FoodCategoryFragmentListAdapter(FoodCategoryTabFragment.this);
                        adapter.init(FoodCategoryTabFragment.this, criteriaLocationListener, categoryList);
                        binding.viewpager.setAdapter(adapter);

                        new TabLayoutMediator(binding.tabs, binding.viewpager,
                                new TabLayoutMediator.TabConfigurationStrategy()
                                {
                                    @Override
                                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position)
                                    {
                                        tab.setText(categoryList.get(position));
                                    }
                                }
                        ).attach();

                        binding.tabs.selectTab(binding.tabs.getTabAt(selectedIndex));
                    }
                });
            }
        });


    }

    @Override
    public void onClickedRestaurantItem(PlaceDocuments placeDocuments)
    {

    }
}