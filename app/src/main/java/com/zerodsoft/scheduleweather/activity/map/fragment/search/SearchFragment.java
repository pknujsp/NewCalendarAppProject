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
import com.zerodsoft.scheduleweather.etc.FragmentStateCallback;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.activity.map.fragment.interfaces.OnSelectedMapCategory;
import com.zerodsoft.scheduleweather.activity.map.fragment.map.MapFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.search.adapter.PlaceCategoriesAdapter;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.SearchResultFragmentController;
import com.zerodsoft.scheduleweather.databinding.FragmentSearchBinding;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategory;

public class SearchFragment extends Fragment implements OnSelectedMapCategory
{
    public static final String TAG = "SearchFragment";
    private static SearchFragment instance;
    private FragmentSearchBinding binding;
    private PlaceCategoriesAdapter categoriesAdapter;
    private IMapPoint iMapPoint;
    private IMapData iMapData;
    private OnBackPressedCallback onBackPressedCallback;
    private FragmentManager fragmentManager;
    private FragmentStateCallback fragmentStateCallback;

    public SearchFragment(Fragment fragment, FragmentStateCallback fragmentStateCallback)
    {
        this.iMapPoint = (IMapPoint) fragment;
        this.iMapData = (IMapData) fragment;
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
                // fragmentManager.beginTransaction().remove(SearchFragment.this).show(MapFragment.getInstance()).commit();
                fragmentManager.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(SearchFragment.this, onBackPressedCallback);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        fragmentStateCallback.onChangedState(FragmentStateCallback.ON_DETACH);
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

        binding.searchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                search(binding.searchEdittext.getText().toString());
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onBackPressedCallback.handleOnBackPressed();
            }
        });
    }


    private void search(String searchWord)
    {
        Bundle bundle = new Bundle();
        bundle.putString("searchWord", searchWord);

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.map_activity_fragment_container, SearchResultFragmentController.newInstance(bundle, iMapPoint, iMapData), SearchResultFragmentController.TAG)
                .hide(SearchFragment.this).addToBackStack(null).commit();
    }

    @Override
    public void onSelectedMapCategory(KakaoLocalApiCategory category)
    {
        search(Integer.toString(category.getId()));
    }

}