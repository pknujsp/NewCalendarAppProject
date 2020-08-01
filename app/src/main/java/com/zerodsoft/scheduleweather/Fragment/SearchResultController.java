package com.zerodsoft.scheduleweather.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.zerodsoft.scheduleweather.Activity.MapActivity.Fragment.SearchFragment;
import com.zerodsoft.scheduleweather.Activity.MapActivity.Fragment.SearchResultListFragment;
import com.zerodsoft.scheduleweather.Activity.MapActivity.MapActivity;
import com.zerodsoft.scheduleweather.R;

public class SearchResultController extends Fragment implements MapActivity.OnBackPressedListener, SearchResultHeaderFragment.CurrentListTypeGetter
{
    public static final String TAG = "SearchResultController";

    private SearchResultHeaderFragment headerFragment;
    private SearchResultListFragment listFragment;

    public static boolean isShowHeader = true;
    public static boolean isShowList = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        headerFragment = (SearchResultHeaderFragment) fragmentManager.findFragmentById(R.id.fragment_search_result_header);
        listFragment = (SearchResultListFragment) fragmentManager.findFragmentById(R.id.fragment_search_result_list);
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

    public void setResultData(Bundle bundle)
    {
        if (headerFragment == null)
        {
            headerFragment = new SearchResultHeaderFragment();
            headerFragment.setCurrentListTypeGetter(this);
        }
        if (listFragment == null)
        {
            listFragment = new SearchResultListFragment();
        }

        headerFragment.setSearchWord(bundle.getString("searchWord"));
        listFragment.setData(bundle);
        isShowHeader = true;
        isShowList = true;
    }

    public void setHeaderVisibility(boolean value)
    {
        if (isShowHeader == value)
        {
            return;
        } else
        {
            isShowHeader = value;

            if (isShowHeader)
            {
                getActivity().getSupportFragmentManager().beginTransaction().show(headerFragment).commit();
            } else
            {
                getActivity().getSupportFragmentManager().beginTransaction().hide(headerFragment).commit();
            }
        }
    }

    public void setListVisibility(boolean value)
    {
        if (isShowList == value)
        {
            return;
        } else
        {
            isShowList = value;

            if (isShowList)
            {
                getActivity().getSupportFragmentManager().beginTransaction().show(listFragment).commit();
                headerFragment.setChangeButtonDrawable(SearchResultHeaderFragment.CURRENT_LIST);
            } else
            {
                getActivity().getSupportFragmentManager().beginTransaction().hide(listFragment).commit();
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isShowList)
        {
            // list인 경우
            setHeaderVisibility(false);
            setListVisibility(false);
            SearchFragment searchFragment = (SearchFragment) fragmentManager.findFragmentById(R.id.fragment_search);
            fragmentTransaction.show(searchFragment);
            fragmentTransaction.remove(this).commit();
        } else
        {
            // map인 경우
            setHeaderVisibility(true);
            setListVisibility(true);
            ((MapActivity) getActivity()).setZoomGpsButtonVisibility(View.GONE);
        }
    }

    @Override
    public int getCurrentListType()
    {
        return listFragment.getCurrentListType();
    }

}
