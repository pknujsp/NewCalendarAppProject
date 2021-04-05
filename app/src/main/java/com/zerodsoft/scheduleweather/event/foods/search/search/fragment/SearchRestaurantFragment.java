package com.zerodsoft.scheduleweather.event.foods.search.search.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentSearchResultBinding;
import com.zerodsoft.scheduleweather.event.foods.search.search.adapter.SearchRestaurantHistoryAdapter;

public class SearchRestaurantFragment extends Fragment
{
    private FragmentSearchResultBinding binding;
    private SearchRestaurantHistoryAdapter historyAdapter;

    public SearchRestaurantFragment()
    {

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
        binding = FragmentSearchResultBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        //db에서 검색 기록을 가져오고 adapter설정
    }
}