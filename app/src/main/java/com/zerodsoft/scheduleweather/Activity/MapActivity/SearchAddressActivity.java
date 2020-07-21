package com.zerodsoft.scheduleweather.Activity.MapActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.RecyclerVIewAdapter.SearchCategoryViewAdapter;
import com.zerodsoft.scheduleweather.Retrofit.DownloadData;
import com.zerodsoft.scheduleweather.Retrofit.KakaoLocalApiCategoryCode;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressSearchResult;

public class SearchAddressActivity extends AppCompatActivity implements SearchCategoryViewAdapter.OnCategoryClickListener
{
    private ImageButton backButton;
    private EditText searchAddressEditText;
    private ImageButton searchAddressButton;
    private Intent searchResultIntent;
    private RecyclerView recentRecyclerView;
    private RecyclerView categoryRecyclerView;

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
                searchResultIntent = new Intent(SearchAddressActivity.this, SearchResultActivity.class);
                searchResultIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                Bundle dataBundle = new Bundle();
                dataBundle.putParcelable("result", addressSearchResult.clone());
                searchResultIntent.putExtras(dataBundle);
                startActivity(searchResultIntent);
                addressSearchResult.clearAll();
            } else if (addressSearchResult.getResultNum() == 2)
            {
                searchResultIntent = new Intent(SearchAddressActivity.this, SearchResultActivity.class);
                searchResultIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                Bundle dataBundle = new Bundle();
                dataBundle.putParcelable("result", addressSearchResult.clone());
                searchResultIntent.putExtras(dataBundle);
                startActivity(searchResultIntent);
                addressSearchResult.clearAll();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address);

        backButton = (ImageButton) findViewById(R.id.back_button);
        searchAddressEditText = (EditText) findViewById(R.id.search_address_edittext);
        searchAddressButton = (ImageButton) findViewById(R.id.search_address_button);
        recentRecyclerView = (RecyclerView) findViewById(R.id.search_address_recent_recyclerview);
        categoryRecyclerView = (RecyclerView) findViewById(R.id.category_recyclerview);

        SearchCategoryViewAdapter searchCategoryViewAdapter = new SearchCategoryViewAdapter(this);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        categoryRecyclerView.setAdapter(searchCategoryViewAdapter);

        searchAddressButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String searchWord = searchAddressEditText.getText().toString();
                String name = getCategoryName(searchWord);

                if (name != null)
                {
                    DownloadData.searchPlaceCategory(name, handler);
                } else
                {
                    DownloadData.searchAddress(searchWord, handler);
                    DownloadData.searchPlaceKeyWord(searchWord, handler);
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

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, MapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);

        finish();
    }

    @Override
    public void selectedCategory(String name)
    {
        // 카테고리 이름을 전달받음
        DownloadData.searchPlaceCategory(name, handler);
    }
}