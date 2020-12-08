package com.zerodsoft.scheduleweather.activity.map.fragment.searchresult;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.map.MapActivity;
import com.zerodsoft.scheduleweather.activity.map.fragment.dto.SearchData;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.interfaces.ResultFragmentChanger;

public class SearchResultHeaderFragment extends Fragment
{
    public static final String TAG = "SearchResultHeaderFragment";
    private ImageButton changeButton;
    private ImageButton closeButton;
    private TextView searchWordTextView;
    private SearchData searchData;
    private ResultFragmentChanger resultFragmentChanger;

    public SearchResultHeaderFragment(SearchData searchData, Fragment fragment)
    {
        this.searchData = new SearchData(searchData.getSearchWord(), searchData.getParameter().copy());
        this.resultFragmentChanger = (ResultFragmentChanger) fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_search_result_header, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        searchWordTextView = (TextView) view.findViewById(R.id.search_result_search_word);
        changeButton = (ImageButton) view.findViewById(R.id.search_result_change_button);
        closeButton = (ImageButton) view.findViewById(R.id.search_result_map_close_button);

        searchWordTextView.setText(searchData.getSearchWord());

        searchWordTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // result fragment인 경우 검색으로 되돌아 간다
                // map 액티비티인 경우, result를 pop하고 검색으로 되돌아 간다
            }
        });

        changeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // list(map)인 경우 map(list)로
                resultFragmentChanger.changeFragment();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // list(map)인 경우 map으로
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart()
    {
        searchWordTextView.setText(searchData.getSearchWord());
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    public void setChangeButtonDrawable(int type)
    {
        switch (type)
        {
            case SearchResultFragmentController.MAP:
                changeButton.setImageDrawable(getResources().getDrawable(R.drawable.map_icon, null));
                break;
            case SearchResultFragmentController.LIST:
                changeButton.setImageDrawable(getResources().getDrawable(R.drawable.list_icon, null));
                break;
        }
    }
}