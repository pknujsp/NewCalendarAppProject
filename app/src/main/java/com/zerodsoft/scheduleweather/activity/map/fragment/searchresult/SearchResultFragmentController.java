package com.zerodsoft.scheduleweather.activity.map.fragment.searchresult;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.map.fragment.map.MapFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.search.SearchFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.ResultFragmentChanger;

import java.util.List;

public class SearchResultFragmentController extends Fragment implements ResultFragmentChanger
{
    public static final String TAG = "SearchResultFragmentController";
    private static SearchResultFragmentController instance;
    private OnBackPressedCallback onBackPressedCallback;

    private SearchResultHeaderFragment headerFragment;
    private SearchResultListFragment listFragment;
    private FragmentManager fragmentManager;

    private boolean isShowHeader = true;
    private boolean isShowList = true;

    private FrameLayout headerLayout;
    private FrameLayout listLayout;

    public static final int MAP = 0;
    public static final int LIST = 1;


    public SearchResultFragmentController(Bundle bundle)
    {
        headerFragment = SearchResultHeaderFragment.newInstance(bundle.getParcelable("searchData"), SearchResultFragmentController.this);
        listFragment = SearchResultListFragment.newInstance(bundle.getParcelable("searchData"));
    }

    public static SearchResultFragmentController getInstance()
    {
        return instance;
    }

    public static SearchResultFragmentController newInstance(Bundle bundle)
    {
        instance = new SearchResultFragmentController(bundle);
        return instance;
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
                if (isShowList)
                {
                    // list인 경우
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentManager.popBackStackImmediate();

                    fragmentTransaction.show(SearchFragment.getInstance()).commit();
                } else
                {
                    // map인 경우
                    changeFragment();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
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
        super.onViewCreated(view, savedInstanceState);

        fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.fragment_search_result_header_container, headerFragment, SearchResultHeaderFragment.TAG)
                .add(R.id.fragment_search_result_list_container, listFragment, SearchResultListFragment.TAG).commit();
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

            fragmentTransaction.hide(listFragment).hide(headerFragment).show(MapFragment.getInstance()).show(headerFragment).commit();
        } else
        {
            // to list
            headerFragment.setChangeButtonDrawable(LIST);
            fragmentTransaction.hide(MapFragment.getInstance()).hide(headerFragment).show(listFragment).show(headerFragment).commit();
        }
    }

}
