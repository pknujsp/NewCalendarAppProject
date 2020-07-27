package com.zerodsoft.scheduleweather.Activity.MapActivity.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.zerodsoft.scheduleweather.Activity.MapActivity.MapActivity;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.RecyclerVIewAdapter.SearchCategoryViewAdapter;
import com.zerodsoft.scheduleweather.Retrofit.DownloadData;
import com.zerodsoft.scheduleweather.Retrofit.KakaoLocalApiCategoryCode;
import com.zerodsoft.scheduleweather.Retrofit.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressSearchResult;

import java.util.concurrent.RecursiveAction;

public class SearchFragment extends Fragment implements SearchCategoryViewAdapter.OnCategoryClickListener, MapActivity.OnBackPressedListener
{
    public static final String TAG = "Search Fragment";
    private static SearchFragment searchFragment = null;

    private ImageButton backButton;
    private EditText searchEditText;
    private ImageButton searchButton;
    private RecyclerView searchHistoryRecyclerView;
    private RecyclerView itemCategoryRecyclerView;
    private LocalApiPlaceParameter parameters;

    private String rect;

    private Handler handler = new Handler()
    {
        private AddressSearchResult addressSearchResult = null;

        @Override
        public void handleMessage(Message msg)
        {
            Bundle bundle = msg.getData();

            if (addressSearchResult == null)
            {
                addressSearchResult = new AddressSearchResult();
            }

            switch (msg.what)
            {
                case DownloadData.ADDRESS:
                    addressSearchResult.setAddressResponseDocuments(bundle.getParcelableArrayList("documents"));
                    break;
                case DownloadData.PLACE_KEYWORD:
                    addressSearchResult.setPlaceKeywordDocuments(bundle.getParcelableArrayList("documents"));
                    break;
                case DownloadData.PLACE_CATEGORY:
                    addressSearchResult.setPlaceCategoryDocuments(bundle.getParcelableArrayList("documents"));
                    break;
            }

            if (addressSearchResult.getResultNum() == 1 && !addressSearchResult.getPlaceCategoryDocuments().isEmpty())
            {
                Bundle dataBundle = new Bundle();
                dataBundle.putParcelable("result", addressSearchResult.clone());
                dataBundle.putParcelable("parameters", parameters);
                dataBundle.putBoolean("isCategory", true);

                ((MapActivity) getActivity()).onFragmentChanged(MapActivity.SEARCH_RESULT_FRAGMENT, dataBundle);
                addressSearchResult.clearAll();
            } else if (addressSearchResult.getResultNum() == 2)
            {
                Bundle dataBundle = new Bundle();
                dataBundle.putParcelable("result", addressSearchResult.clone());
                dataBundle.putParcelable("parameters", parameters);
                dataBundle.putBoolean("isCategory", false);

                ((MapActivity) getActivity()).onFragmentChanged(MapActivity.SEARCH_RESULT_FRAGMENT, dataBundle);
                addressSearchResult.clearAll();
            }
        }
    };

    public SearchFragment()
    {
    }

    public static SearchFragment getInstance()
    {
        if (searchFragment == null)
        {
            searchFragment = new SearchFragment();
        }
        return searchFragment;
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        backButton = (ImageButton) view.findViewById(R.id.back_button);
        searchEditText = (EditText) view.findViewById(R.id.search_edittext);
        searchButton = (ImageButton) view.findViewById(R.id.search_button);
        searchHistoryRecyclerView = (RecyclerView) view.findViewById(R.id.search_history_recyclerview);
        itemCategoryRecyclerView = (RecyclerView) view.findViewById(R.id.category_recyclerview);

        SearchCategoryViewAdapter searchCategoryViewAdapter = new SearchCategoryViewAdapter(this);
        itemCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        itemCategoryRecyclerView.setAdapter(searchCategoryViewAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        searchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String searchWord = searchEditText.getText().toString();
                String name = getCategoryName(searchWord);

                parameters = new LocalApiPlaceParameter().setRect(rect).setPage(LocalApiPlaceParameter.DEFAULT_PAGE)
                        .setSize(LocalApiPlaceParameter.DEFAULT_SIZE)
                        .setSort(LocalApiPlaceParameter.DEFAULT_SORT);

                if (name != null)
                {
                    parameters.setCategoryGroupCode(name);
                    DownloadData.searchPlaceCategory(handler, parameters);
                } else
                {
                    parameters.setQuery(searchWord);
                    DownloadData.searchAddress(handler, parameters);
                    DownloadData.searchPlaceKeyWord(handler, parameters);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onBackPressed();
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

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }


    @Override
    public void selectedCategory(String name)
    {
        // 카테고리 이름을 전달받음
        parameters = new LocalApiPlaceParameter().setRect(rect).setPage(LocalApiPlaceParameter.DEFAULT_PAGE)
                .setSize(LocalApiPlaceParameter.DEFAULT_SIZE)
                .setSort(LocalApiPlaceParameter.DEFAULT_SORT)
                .setCategoryGroupCode(name);

        DownloadData.searchPlaceCategory(handler, parameters);
    }

    private String getCategoryName(String searchWord)
    {
        KakaoLocalApiCategoryCode.loadCategoryMap();
        String name = KakaoLocalApiCategoryCode.getName(searchWord);

        if (name != null)
        {
            return name;
        } else
        {
            return null;
        }
    }

    public void setData(Bundle bundle)
    {
        this.rect = bundle.getString("rect");
    }

    @Override
    public void onBackPressed()
    {
        MapActivity.isMainMapActivity = true;
        ((MapActivity) getActivity()).clearAllPoiItems();
        ((MapActivity) getActivity()).setZoomGpsButtonVisibility(View.VISIBLE);
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }
}