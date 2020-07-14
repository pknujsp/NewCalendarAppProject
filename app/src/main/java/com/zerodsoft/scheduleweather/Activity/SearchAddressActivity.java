package com.zerodsoft.scheduleweather.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.RecyclerVIewAdapter.SearchResultViewPagerAdapter;
import com.zerodsoft.scheduleweather.Retrofit.DownloadData;
import com.zerodsoft.scheduleweather.Retrofit.KakaoLocalApiCategoryCode;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressSearchResult;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceCategoryResponse.PlaceCategoryDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceKeywordResponse.PlaceKeywordDocuments;

import java.util.ArrayList;

public class SearchAddressActivity extends AppCompatActivity
{
    private ImageButton backButton;
    private EditText searchAddressEditText;
    private ImageButton searchAddressButton;
    private Intent searchResultIntent;
    private RecyclerView recentRecyclerView;

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

                Bundle dataBundle = new Bundle();
                dataBundle.putParcelable("result", addressSearchResult.clone());
                searchResultIntent.putExtras(dataBundle);
                startActivity(searchResultIntent);
                addressSearchResult.clearAll();
            } else if (addressSearchResult.getResultNum() == 2)
            {
                searchResultIntent = new Intent(SearchAddressActivity.this, SearchResultActivity.class);

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

        searchAddressButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String searchWord = searchAddressEditText.getText().toString();
                String code = getCategoryCode(searchWord);

                if (code != null)
                {
                    DownloadData.searchPlaceCategory(code, handler);
                } else
                {
                    DownloadData.searchAddress(searchWord, handler);
                    DownloadData.searchPlaceKeyWord(searchWord, handler);
                }
            }
        });
    }

    private String getCategoryCode(String searchWord)
    {
        KakaoLocalApiCategoryCode.loadCategoryMap();
        String code = KakaoLocalApiCategoryCode.getCode(searchWord);

        if (code != null)
        {
            return code;
        } else
        {
            return null;
        }
    }
}