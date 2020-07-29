package com.zerodsoft.scheduleweather.Fragment;

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

public class SearchResultHeaderFragment extends Fragment
{
    public static final String TAG = "SearchResultHeaderFragment";
    private static SearchResultHeaderFragment searchResultHeaderFragment = null;

    private ImageButton listButton;
    private ImageButton closeButton;
    private TextView itemNameTextView;

    public SearchResultHeaderFragment()
    {
    }

    public static SearchResultHeaderFragment getInstance()
    {
        if (searchResultHeaderFragment == null)
        {
            searchResultHeaderFragment = new SearchResultHeaderFragment();
        }
        return searchResultHeaderFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_search_result_map_header, container, false);

        itemNameTextView = (TextView) view.findViewById(R.id.search_result_item_name);
        listButton = (ImageButton) view.findViewById(R.id.search_result_list_button);
        closeButton = (ImageButton) view.findViewById(R.id.search_result_map_close_button);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        itemNameTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });

        listButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });

        closeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });
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

    public void setSearchWord(String searchWord)
    {
        this.itemNameTextView.setText(searchWord);
    }
}