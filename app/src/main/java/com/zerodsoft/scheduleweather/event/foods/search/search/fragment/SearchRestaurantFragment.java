package com.zerodsoft.scheduleweather.event.foods.search.search.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.databinding.FragmentSearchRestaurantBinding;
import com.zerodsoft.scheduleweather.event.foods.categorylist.FoodCategoryTabFragment;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedRestaurantItem;
import com.zerodsoft.scheduleweather.event.foods.main.fragment.NewFoodsMainFragment;
import com.zerodsoft.scheduleweather.event.foods.search.searchresult.fragment.FoodRestaurantSearchResultFragment;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.SearchHistoryViewModel;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.SearchHistoryDTO;

public class SearchRestaurantFragment extends Fragment implements OnClickedListItem<SearchHistoryDTO>, FoodRestaurantSearchResultFragment.OnDeleteSearchView,
        OnClickedRestaurantItem, OnBackPressedCallbackController
{
    public static final String TAG = "SearchRestaurantFragment";
    private final BottomSheetController bottomSheetController;
    private FragmentSearchRestaurantBinding binding;
    private FoodRestaurantSearchHistoryFragment foodRestaurantSearchHistoryFragment;
    private FoodRestaurantSearchResultFragment searchResultFragment;
    private SearchHistoryViewModel searchHistoryViewModel;
    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true)
    {
        @Override
        public void handleOnBackPressed()
        {
            FragmentManager fragmentManager = getChildFragmentManager();
            if (fragmentManager.findFragmentByTag(FoodRestaurantSearchHistoryFragment.TAG) != null)
            {
                if (fragmentManager.findFragmentByTag(FoodRestaurantSearchHistoryFragment.TAG).isVisible())
                {
                    getParentFragment().getParentFragmentManager().popBackStack();

                    bottomSheetController.setStateOfBottomSheet(NewFoodsMainFragment.TAG, BottomSheetBehavior.STATE_COLLAPSED);
                } else if (fragmentManager.findFragmentByTag(FoodRestaurantSearchResultFragment.TAG) != null)
                {
                    binding.deleteQueryButton.setVisibility(View.GONE);
                    binding.searchButton.setVisibility(View.VISIBLE);
                    deleteQuery();
                    fragmentManager.popBackStackImmediate();
                }
            }
        }
    };

    public SearchRestaurantFragment(BottomSheetController bottomSheetController)
    {
        this.bottomSheetController = bottomSheetController;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentSearchRestaurantBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        searchHistoryViewModel = new ViewModelProvider(this).get(SearchHistoryViewModel.class);
        //검색 기록 프래그먼트 표시
        foodRestaurantSearchHistoryFragment = new FoodRestaurantSearchHistoryFragment(this);
        getChildFragmentManager().beginTransaction().add(binding.searchFoodRestaurantFragmentContainer.getId(),
                foodRestaurantSearchHistoryFragment, FoodRestaurantSearchHistoryFragment.TAG).commitNow();

        binding.editTextSearch.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent)
            {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                {
                    //검색
                    binding.searchButton.callOnClick();
                    return true;
                }
                return false;
            }
        });

        binding.searchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String value = binding.editTextSearch.getText().toString();
                searchHistoryViewModel.contains(SearchHistoryDTO.FOOD_RESTAURANT_SEARCH, value, new CarrierMessagingService.ResultCallback<Boolean>()
                {
                    @Override
                    public void onReceiveResult(@NonNull Boolean isDuplicate) throws RemoteException
                    {
                        getActivity().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (isDuplicate)
                                {
                                    Toast.makeText(getContext(), R.string.duplicate_value, Toast.LENGTH_SHORT).show();
                                } else
                                {
                                    search(binding.editTextSearch.getText().toString());
                                    foodRestaurantSearchHistoryFragment.insertHistory(value);
                                }
                            }
                        });

                    }
                });

            }
        });

        binding.deleteQueryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onBackPressedCallback.handleOnBackPressed();
            }
        });

        binding.deleteQueryButton.setVisibility(View.GONE);
        binding.searchButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        super.onHiddenChanged(hidden);

        if (hidden)
        {
            removeOnBackPressedCallback();
        } else
        {
            addOnBackPressedCallback();
            FoodRestaurantSearchResultFragment foodRestaurantSearchResultFragment =
                    (FoodRestaurantSearchResultFragment) getChildFragmentManager().findFragmentByTag(FoodRestaurantSearchResultFragment.TAG);
            if (foodRestaurantSearchResultFragment != null)
            {
                foodRestaurantSearchResultFragment.refreshFavorites();
            }
        }
    }

    private void search(String value)
    {
        binding.deleteQueryButton.setVisibility(View.VISIBLE);
        binding.searchButton.setVisibility(View.GONE);

        if (getChildFragmentManager().findFragmentByTag(FoodRestaurantSearchResultFragment.TAG) == null)
        {
            searchResultFragment = new FoodRestaurantSearchResultFragment(value, this);

            getChildFragmentManager().beginTransaction().hide(foodRestaurantSearchHistoryFragment)
                    .add(binding.searchFoodRestaurantFragmentContainer.getId(),
                            searchResultFragment, FoodRestaurantSearchResultFragment.TAG).addToBackStack(null).commit();

            // foodRestaurantSearchHistoryFragment.removeOnBackPressedCallback();
        } else
        {
            searchResultFragment.search(value);
        }
    }

    @Override
    public void onClickedListItem(SearchHistoryDTO e)
    {
        binding.editTextSearch.setText(e.getValue());
        search(e.getValue());
    }

    @Override
    public void deleteListItem(SearchHistoryDTO e, int position)
    {

    }

    @Override
    public void deleteQuery()
    {
        binding.editTextSearch.setText("");
    }

    @Override
    public void onClickedRestaurantItem(PlaceDocuments placeDocuments)
    {

    }

    @Override
    public void addOnBackPressedCallback()
    {
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public void removeOnBackPressedCallback()
    {
        onBackPressedCallback.remove();
    }
}