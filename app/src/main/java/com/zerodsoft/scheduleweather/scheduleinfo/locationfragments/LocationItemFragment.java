package com.zerodsoft.scheduleweather.scheduleinfo.locationfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.scheduleinfo.locationfragments.categoryfragments.PlaceItemsFragment;

import java.util.LinkedList;
import java.util.List;

public class LocationItemFragment extends Fragment implements LocationInfoGetter
{
    private final LocationInfo locationInfo;

    public LocationItemFragment(String locationName, double latitude, double longitude)
    {
        locationInfo = new LocationInfo(latitude, longitude, locationName);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.around_location_viewpager_item, container, false);
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
        LinearLayout categoryFragmentContainerView = (LinearLayout) view.findViewById(R.id.map_category_fragment_container);

        // 위치 이름 표시
        ((TextView) view.findViewById(R.id.location_name)).setText(locationInfo.getLocationName() + " " + getString(R.string.info_around_location));
        // 표시할 정보를 가져옴

        // 정보를 표시할 프래그먼트를 각각 생성
        // 편의점, ATM 정보를 보여주기로 했다고 가정
        List<String> categoryNames = new LinkedList<>();
        categoryNames.add(getString(R.string.atm));
        categoryNames.add(getString(R.string.convenience_store));

        List<PlaceItemsFragment> categoryFragments = new LinkedList<>();
        for (String categoryName : categoryNames)
        {
            categoryFragments.add(new PlaceItemsFragment(categoryName, this));
        }

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // fragment container view를 추가하고 프래그먼트를 추가
        for (PlaceItemsFragment fragment : categoryFragments)
        {
            FragmentContainerView containerView = new FragmentContainerView(getContext());
            containerView.setId(View.generateViewId());
            categoryFragmentContainerView.addView(containerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            fragmentTransaction.add(containerView.getId(), fragment);
        }
        fragmentTransaction.commit();
    }

    @Override
    public LocationInfo getLocationInfo()
    {
        return locationInfo.copy();
    }
}
