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

import com.zerodsoft.scheduleweather.activity.map.MapActivity;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.map.fragment.map.MapFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.interfaces.CustomOnBackPressed;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.ResultFragmentChanger;

public class SearchResultFragmentController extends Fragment implements CustomOnBackPressed, ResultFragmentChanger
{
    public static final String TAG = "SearchResultFragmentController";

    private SearchResultHeaderFragment headerFragment;
    private SearchResultFragment listFragment;
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
        listFragment = new SearchResultFragment(bundle.getParcelable("searchData"));
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
        fragmentTransaction.add(LIST_LAYOUT_ID, listFragment, SearchResultFragment.TAG);
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

        if (isShowList)
        {
            // to map
            // 버튼 이미지, 프래그먼트 숨김/보이기 설정
            headerFragment.setChangeButtonDrawable(MAP);
            fragmentTransaction.hide(listFragment).hide(headerFragment).show(MapFragment.getInstance(getActivity())).show(headerFragment).commit();
        } else
        {
            // to list
            headerFragment.setChangeButtonDrawable(LIST);
            fragmentTransaction.hide(MapFragment.getInstance(getActivity())).hide(headerFragment).show(listFragment).show(headerFragment).commit();
        }
    }

    @Override
    public void onBackPressed()
    {
        if (isShowList)
        {
            // list인 경우
            MapActivity.parameters.clear();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(headerFragment).remove(listFragment).commit();
            fragmentManager.popBackStackImmediate();
        } else
        {
            // map인 경우
            changeFragment();
        }
    }
}
