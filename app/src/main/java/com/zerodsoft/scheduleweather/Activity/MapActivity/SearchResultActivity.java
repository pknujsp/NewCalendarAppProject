package com.zerodsoft.scheduleweather.Activity.MapActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.RecyclerVIewAdapter.SearchResultViewPagerAdapter;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressSearchResult;

public class SearchResultActivity extends AppCompatActivity
{

    private ImageButton closeButton;
    private ImageButton goToMapButton;
    private ViewPager2 viewPager2;
    private SearchResultViewPagerAdapter searchResultViewPagerAdapter;
    private AddressSearchResult result;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        closeButton = (ImageButton) findViewById(R.id.search_result_close_button);
        goToMapButton = (ImageButton) findViewById(R.id.search_result_map_button);
        viewPager2 = (ViewPager2) findViewById(R.id.search_address_viewpager);

        result = (AddressSearchResult) getIntent().getExtras().getParcelable("result");

        searchResultViewPagerAdapter = new SearchResultViewPagerAdapter(SearchResultActivity.this);
        searchResultViewPagerAdapter.setAddressSearchResult(result);
        viewPager2.setAdapter(searchResultViewPagerAdapter);

        closeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onBackPressed();
            }
        });

        goToMapButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }
}