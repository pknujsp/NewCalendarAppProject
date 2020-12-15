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
import com.zerodsoft.scheduleweather.activity.map.fragment.interfaces.IMapData;
import com.zerodsoft.scheduleweather.activity.map.fragment.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.activity.map.fragment.map.MapFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.search.SearchFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.ResultFragmentChanger;

public class SearchResultFragmentController extends Fragment implements ResultFragmentChanger
{
    public static final String TAG = "SearchResultFragmentController";
    private static SearchResultFragmentController instance;
    private OnBackPressedCallback onBackPressedCallback;

    private SearchResultHeaderFragment headerFragment;
    private SearchResultListFragment listFragment;
    private FragmentManager fragmentManager;

    private boolean isShowList = true;

    public static final int MAP = 0;
    public static final int LIST = 1;

    private IMapData iMapData;

    public SearchResultFragmentController(Bundle bundle, IMapPoint iMapPoint, IMapData iMapData)
    {
        String searchWord = bundle.getString("searchWord");
        headerFragment = SearchResultHeaderFragment.newInstance(searchWord, SearchResultFragmentController.this);
        listFragment = SearchResultListFragment.newInstance(searchWord, iMapPoint, iMapData);
        this.iMapData = iMapData;
    }

    public static SearchResultFragmentController getInstance()
    {
        return instance;
    }

    public static SearchResultFragmentController newInstance(Bundle bundle, IMapPoint iMapPoint, IMapData iMapData)
    {
        instance = new SearchResultFragmentController(bundle, iMapPoint, iMapData);
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
                    fragmentManager.beginTransaction().remove(SearchResultFragmentController.this).show(SearchFragment.getInstance()).commit();
                    iMapData.removeAllPoiItems();
                } else
                {
                    // map인 경우
                    changeFragment();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        onBackPressedCallback.remove();
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
            iMapData.showAllPoiItems();
            fragmentTransaction.hide(listFragment).hide(headerFragment).show(MapFragment.getInstance()).show(headerFragment).commit();
            isShowList = false;
        } else
        {
            // to list
            iMapData.deselectPoiItem();
            headerFragment.setChangeButtonDrawable(LIST);
            fragmentTransaction.hide(headerFragment).hide(MapFragment.getInstance()).show(listFragment).show(headerFragment).commit();
            isShowList = true;
        }
    }

}
