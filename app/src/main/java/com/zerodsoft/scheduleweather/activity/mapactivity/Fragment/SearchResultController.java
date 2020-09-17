package com.zerodsoft.scheduleweather.activity.mapactivity.Fragment;

import android.app.Activity;
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

import com.zerodsoft.scheduleweather.activity.mapactivity.MapActivity;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.retrofit.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.LocationSearchResult;

import java.util.List;
import java.util.Map;

public class SearchResultController extends Fragment implements MapActivity.OnBackPressedListener, SearchResultHeaderFragment.CurrentListTypeGetter
{
    public static final String TAG = "SearchResult Controller";
    private static SearchResultController instance;

    private SearchResultHeaderFragment headerFragment;
    private SearchResultFragment listFragment;

    public static boolean isShowHeader = true;
    public static boolean isShowList = true;

    private static final int HEADER_LAYOUT_ID = R.id.fragment_search_result_header_container;
    private static final int LIST_LAYOUT_ID = R.id.fragment_search_result_list_container;

    private FrameLayout headerLayout;
    private FrameLayout listLayout;

    public SearchResultController(Activity activity)
    {
        headerFragment = SearchResultHeaderFragment.getInstance(activity);
        listFragment = SearchResultFragment.getInstance(activity);
        headerFragment.setCurrentListTypeGetter(this);
    }

    public static SearchResultController getInstance(Activity activity)
    {
        if (instance == null)
        {
            instance = new SearchResultController(activity);
        }
        return instance;
    }

    public void setInitialData(Bundle bundle)
    {
        listFragment.setInitialData(bundle);
        headerFragment.setInitialData(bundle);
    }

    public void setChangeButtonDrawable()
    {
        headerFragment.setChangeButtonDrawable();
    }

    public void setDownloadedData()
    {
        listFragment.setDownloadedData();
    }

    public void setDownloadedExtraData(int type)
    {
        listFragment.setDownloadedExtraData(type);
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

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();

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
    public void onBackPressed()
    {
        if (isShowList)
        {
            // list인 경우
            MapActivity.parameters.clear();
            listFragment.clearHolderSparseArr();

            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(headerFragment).remove(listFragment).commit();
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        } else
        {
            // map인 경우
            if (MapFragment.isClickedChangeButton || MapFragment.isClickedListItem)
            {
                ((MapActivity) getActivity()).changeMapOrList(MapController.TYPE_NOT);
            }
        }
    }

    public void setVisibility(String fragmentTag, int visibility)
    {
        if (fragmentTag.equals(SearchResultHeaderFragment.TAG))
        {
            headerLayout.setVisibility(visibility);
        } else if (fragmentTag.equals(SearchResultFragment.TAG))
        {
            listLayout.setVisibility(visibility);
        }
    }

    @Override
    public int getCurrentListType()
    {
        return listFragment.getCurrentListType();
    }
}
