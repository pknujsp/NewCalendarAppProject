package com.zerodsoft.scheduleweather.activity.map.fragment.search;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.SearchResultListFragment;
import com.zerodsoft.scheduleweather.etc.FragmentStateCallback;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.MapBottomSheetController;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.activity.map.fragment.interfaces.OnSelectedMapCategory;
import com.zerodsoft.scheduleweather.activity.map.fragment.search.adapter.PlaceCategoriesAdapter;
import com.zerodsoft.scheduleweather.databinding.FragmentSearchBinding;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapToolbar;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

public class SearchFragment extends Fragment implements OnSelectedMapCategory
{
    public static final String TAG = "SearchFragment";
    private static SearchFragment instance;

    private FragmentSearchBinding binding;
    private PlaceCategoriesAdapter categoriesAdapter;
    private IMapPoint iMapPoint;
    private IMapData iMapData;
    private IMapToolbar iMapToolbar;
    private MapBottomSheetController mapBottomSheetController;
    private OnBackPressedCallback onBackPressedCallback;
    private FragmentManager fragmentManager;
    private FragmentStateCallback fragmentStateCallback;

    public SearchFragment(Fragment fragment, FragmentStateCallback fragmentStateCallback)
    {
        this.iMapPoint = (IMapPoint) fragment;
        this.iMapData = (IMapData) fragment;
        this.iMapToolbar = (IMapToolbar) fragment;
        this.mapBottomSheetController = (MapBottomSheetController) fragment;
        this.fragmentStateCallback = fragmentStateCallback;
    }

    public static SearchFragment getInstance()
    {
        return instance;
    }

    public static SearchFragment newInstance(Fragment fragment, FragmentStateCallback fragmentStateCallback)
    {
        instance = new SearchFragment(fragment, fragmentStateCallback);
        return instance;
    }

    public static void close()
    {
        instance = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        onBackPressedCallback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                getParentFragmentManager().beginTransaction().remove(instance).commitNow();
                mapBottomSheetController.closeSearchView(MapBottomSheetController.SEARCH_VIEW);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(SearchFragment.this, onBackPressedCallback);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        onBackPressedCallback.remove();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        fragmentManager = requireActivity().getSupportFragmentManager();
        categoriesAdapter = new PlaceCategoriesAdapter(this);
        binding.categoriesRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        binding.categoriesRecyclerview.setAdapter(categoriesAdapter);
    }


    public void search(String searchWord)
    {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.search_bottom_sheet_fragment_container, SearchResultListFragment.newInstance(searchWord, iMapPoint, iMapData, iMapToolbar, mapBottomSheetController)
                , SearchResultListFragment.TAG).hide(SearchFragment.this).addToBackStack(SearchResultListFragment.TAG).commit();
    }

    @Override
    public void onSelectedMapCategory(PlaceCategoryDTO category)
    {
        iMapToolbar.setText(category.getDescription());
        search(category.getCode());
    }

}