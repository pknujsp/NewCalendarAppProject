package com.zerodsoft.scheduleweather.event.foods.search.searchresult.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.databinding.FragmentSearchResultRestaurantBinding;
import com.zerodsoft.scheduleweather.event.foods.fragment.RestaurantListFragment;
import com.zerodsoft.scheduleweather.event.foods.interfaces.CriteriaLocationListener;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedRestaurantItem;

public class FoodRestaurantSearchResultFragment extends RestaurantListFragment implements OnBackPressedCallbackController
{
    public static final String TAG = "FoodRestaurantSearchResultFragment";
    private final OnDeleteSearchView onDeleteSearchView;

    public FoodRestaurantSearchResultFragment(String searchWord, Fragment fragment, CriteriaLocationListener criteriaLocationListener)
    {
        super((OnClickedRestaurantItem) fragment, criteriaLocationListener, searchWord);
        this.onDeleteSearchView = (OnDeleteSearchView) fragment;
    }

    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true)
    {
        @Override
        public void handleOnBackPressed()
        {
            onDeleteSearchView.deleteQuery();
            getParentFragmentManager().popBackStackImmediate();
            removeOnBackPressedCallback();
        }
    };

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        addOnBackPressedCallback();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public void search(String searchWord)
    {
        this.CATEGORY_NAME = searchWord;
        requestRestaurantList(CATEGORY_NAME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        removeOnBackPressedCallback();
    }

    @Override
    public void addOnBackPressedCallback()
    {
        getActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public void removeOnBackPressedCallback()
    {
        onBackPressedCallback.remove();
    }

    public interface OnDeleteSearchView
    {
        void deleteQuery();
    }
}