package com.zerodsoft.scheduleweather.event.foods.search.search.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.databinding.FragmentSearchRestaurantBinding;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedRestaurantItem;
import com.zerodsoft.scheduleweather.event.foods.search.searchresult.fragment.FoodRestaurantSearchResultFragment;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.SearchHistoryDTO;

public class SearchRestaurantFragment extends Fragment implements OnClickedListItem<SearchHistoryDTO>, FoodRestaurantSearchResultFragment.OnDeleteSearchView,
        OnClickedRestaurantItem, OnBackPressedCallbackController
{
    public static final String TAG = "SearchRestaurantFragment";
    private FragmentSearchRestaurantBinding binding;
    private FoodRestaurantSearchHistoryFragment foodRestaurantSearchHistoryFragment;
    private FoodRestaurantSearchResultFragment searchResultFragment;
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
                    getActivity().finish();
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

    public SearchRestaurantFragment()
    {

    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        // addOnBackPressedCallback();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        //  removeOnBackPressedCallback();
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
                search(binding.editTextSearch.getText().toString());
                foodRestaurantSearchHistoryFragment.insertHistory(binding.editTextSearch.getText().toString());
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