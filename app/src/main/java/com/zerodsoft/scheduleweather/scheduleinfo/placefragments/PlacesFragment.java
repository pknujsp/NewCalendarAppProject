package com.zerodsoft.scheduleweather.scheduleinfo.placefragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.scheduleinfo.placefragments.categoryview.CategoryViewAdapter;

import java.util.LinkedList;
import java.util.List;

public class PlacesFragment extends Fragment implements LocationInfoGetter
{
    private final LocationInfo locationInfo;
    private RecyclerView categoryRecyclerView;
    private CategoryViewAdapter adapter;

    public PlacesFragment(LocationInfo locationInfo)
    {
        this.locationInfo = locationInfo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.place_categories_fragment, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        // 위치 이름 표시
        ((TextView) view.findViewById(R.id.location_name)).setText(locationInfo.getLocationName() + " " + getString(R.string.info_around_location));

        categoryRecyclerView = (RecyclerView) view.findViewById(R.id.map_category_view_container);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        // 편의점, ATM 정보를 보여주기로 했다고 가정
        List<String> categories = new LinkedList<>();
        categories.add(getString(R.string.atm));
        categories.add(getString(R.string.convenience_store));

        adapter = new CategoryViewAdapter(locationInfo.copy(), categories, this);
        categoryRecyclerView.setAdapter(adapter);
    }

    @Override
    public LocationInfo getLocationInfo()
    {
        return locationInfo.copy();
    }
}
