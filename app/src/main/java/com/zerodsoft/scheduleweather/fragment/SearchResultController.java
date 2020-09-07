package com.zerodsoft.scheduleweather.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.SearchFragment;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.SearchResultFragment;
import com.zerodsoft.scheduleweather.activity.mapactivity.MapActivity;
import com.zerodsoft.scheduleweather.R;

import java.util.List;

public class SearchResultController extends Fragment implements MapActivity.OnBackPressedListener, SearchResultHeaderFragment.CurrentListTypeGetter
{
    public static final String TAG = "SearchResultController";

    private SearchResultHeaderFragment headerFragment;
    private SearchResultFragment listFragment;

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
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.fragment_search_result_header_container, headerFragment, SearchResultHeaderFragment.TAG);
        fragmentTransaction.add(R.id.fragment_search_result_list_container, listFragment, SearchResultFragment.TAG);
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
            listFragment = new SearchResultFragment();
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

            List<Fragment> fragments = fragmentManager.getFragments();

            int i = 0;
            for (; i < fragments.size(); i++)
            {
                if (fragments.get(i) instanceof SearchFragment)
                {
                    break;
                }
            }
            fragmentTransaction.remove(headerFragment);
            fragmentTransaction.remove(listFragment);
            fragmentTransaction.remove(SearchResultController.this);
            fragmentTransaction.show(fragments.get(i)).commit();
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
