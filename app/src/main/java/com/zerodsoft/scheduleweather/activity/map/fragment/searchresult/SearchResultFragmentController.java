package com.zerodsoft.scheduleweather.activity.map.fragment.searchresult;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.map.fragment.map.MapFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.interfaces.CustomOnBackPressed;
import com.zerodsoft.scheduleweather.activity.map.fragment.search.SearchFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.ResultFragmentChanger;

import java.util.List;

public class SearchResultFragmentController extends Fragment implements CustomOnBackPressed, ResultFragmentChanger
{
    public static final String TAG = "SearchResultFragmentController";

    private SearchResultHeaderFragment headerFragment;
    private SearchResultListFragment listFragment;
    private FragmentManager fragmentManager;

    private boolean isShowHeader = true;
    private boolean isShowList = true;

    private static final int HEADER_LAYOUT_ID = R.id.fragment_search_result_header_container;
    private static final int LIST_LAYOUT_ID = R.id.fragment_search_result_list_container;

    private FrameLayout headerLayout;
    private FrameLayout listLayout;

    public static final int MAP = 0;
    public static final int LIST = 1;


    public SearchResultFragmentController(Bundle bundle)
    {
        headerFragment = new SearchResultHeaderFragment(bundle.getParcelable("searchData"), SearchResultFragmentController.this);
        listFragment = new SearchResultListFragment(bundle.getParcelable("searchData"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        headerLayout = (FrameLayout) view.findViewById(HEADER_LAYOUT_ID);
        listLayout = (FrameLayout) view.findViewById(LIST_LAYOUT_ID);

        fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(HEADER_LAYOUT_ID, headerFragment, SearchResultHeaderFragment.TAG);
        fragmentTransaction.add(LIST_LAYOUT_ID, listFragment, SearchResultListFragment.TAG);
        fragmentTransaction.show(headerFragment).show(listFragment).commit();

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }


    @Override
    public void changeFragment()
    {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        List<Fragment> fragments = fragmentManager.getFragments();
        MapFragment mapFragment = null;
        for (Fragment fragment : fragments)
        {
            if (fragment instanceof MapFragment)
            {
                mapFragment = (MapFragment) fragment;
                break;
            }
        }
        if (isShowList)
        {
            // to map
            // 버튼 이미지, 프래그먼트 숨김/보이기 설정
            headerFragment.setChangeButtonDrawable(MAP);


            fragmentTransaction.hide(listFragment).hide(headerFragment).show(mapFragment).show(headerFragment).commit();
        } else
        {
            // to list
            headerFragment.setChangeButtonDrawable(LIST);
            fragmentTransaction.hide(mapFragment).hide(headerFragment).show(listFragment).show(headerFragment).commit();
        }
    }

    @Override
    public void onBackPressed()
    {
        if (isShowList)
        {
            // list인 경우
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentManager.popBackStackImmediate();

            List<Fragment> fragments = fragmentManager.getFragments();
            SearchFragment searchFragment = null;
            for (Fragment fragment : fragments)
            {
                if (fragment instanceof SearchFragment)
                {
                    searchFragment = (SearchFragment) fragment;
                    break;
                }
            }
            fragmentTransaction.remove(headerFragment).remove(listFragment).show(searchFragment).commit();
        } else
        {
            // map인 경우
            changeFragment();
        }
    }
}
