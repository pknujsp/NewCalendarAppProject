package com.zerodsoft.scheduleweather.event.foods.favorite;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.databinding.FragmentFavoritesMainBinding;
import com.zerodsoft.scheduleweather.event.foods.categorylist.FoodCategoryTabFragment;
import com.zerodsoft.scheduleweather.event.foods.categorylist.FoodsCategoryListFragment;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteRestaurantFragment;


public class FavoritesMainFragment extends Fragment implements OnBackPressedCallbackController
{
    public static final String TAG = "FavoritesMainFragment";
    private FragmentFavoritesMainBinding binding;
    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true)
    {
        @Override
        public void handleOnBackPressed()
        {
            FragmentManager fragmentManager = getChildFragmentManager();

            if (fragmentManager.findFragmentByTag(FavoriteRestaurantFragment.TAG) != null)
            {
                if (fragmentManager.findFragmentByTag(FavoriteRestaurantFragment.TAG).isVisible())
                {
                    getActivity().finish();
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentFavoritesMainBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        FavoriteRestaurantFragment favoriteRestaurantFragment = new FavoriteRestaurantFragment();
        getChildFragmentManager().beginTransaction().add(binding.favoritesFragmentContainer.getId(),
                favoriteRestaurantFragment, FavoriteRestaurantFragment.TAG).commit();
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
            //데이터 리스트 갱신
            ((FavoriteRestaurantFragment) getChildFragmentManager().findFragmentByTag(FavoriteRestaurantFragment.TAG)).refreshList();
        }
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