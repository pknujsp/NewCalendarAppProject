package com.zerodsoft.scheduleweather.activity.mapactivity.Fragment;

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

public class SearchResultHeaderFragment extends Fragment
{
    public static final String TAG = "SearchResultHeaderFragment";
    private static SearchResultHeaderFragment instance;

    private ImageButton changeButton;
    private ImageButton closeButton;
    private TextView searchWordTextView;
    private String searchWord;

    public static final int CURRENT_MAP = 0;
    public static final int CURRENT_LIST = 1;

    private CurrentListTypeGetter currentListTypeGetter;

    public SearchResultHeaderFragment(Activity activity)
    {

    }

    public static SearchResultHeaderFragment getInstance(Activity activity)
    {
        if (instance == null)
        {
            instance = new SearchResultHeaderFragment(activity);
        }
        return instance;
    }

    public interface CurrentListTypeGetter
    {
        int getCurrentListType();
    }

    public SearchResultHeaderFragment setCurrentListTypeGetter(CurrentListTypeGetter currentListTypeGetter)
    {
        this.currentListTypeGetter = currentListTypeGetter;
        return this;
    }

    public SearchResultHeaderFragment()
    {
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
                if (SearchResultController.isShowList)
                {
                    // map으로
                    setChangeButtonDrawable(CURRENT_LIST);
                } else
                {
                    setChangeButtonDrawable(CURRENT_MAP);
                }
                //  ((MapActivity) getActivity()).onChangeButtonClicked(currentListTypeGetter.getCurrentListType());
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
        searchWordTextView.setText(searchWord);
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    public void setSearchWord(String searchWord)
    {
        this.searchWord = searchWord;
    }

    public void setChangeButtonDrawable(int state)
    {
        if (state == CURRENT_MAP)
        {
            changeButton.setImageDrawable(getResources().getDrawable(R.drawable.map_icon, null));
        } else
        {
            // list
            changeButton.setImageDrawable(getResources().getDrawable(R.drawable.list_icon, null));
        }
    }
}