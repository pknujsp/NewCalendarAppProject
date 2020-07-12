package com.zerodsoft.scheduleweather.RecyclerVIewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Retrofit.DownloadData;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressSearchResult;

import java.util.ArrayList;

public class SearchResultViewPagerAdapter extends RecyclerView.Adapter<SearchResultViewPagerAdapter.SearchResultViewPagerHolder>
{
    private Context context;
    private AddressSearchResult addressSearchResult = new AddressSearchResult();
    private boolean existingAddress = false;
    private boolean existingPlaceKeyword = false;
    private boolean existingPlaceCategory = false;

    public SearchResultViewPagerAdapter(Context context)
    {
        this.context = context;
    }

    public void setAddressSearchResult(AddressSearchResult addressSearchResult)
    {
        this.addressSearchResult = addressSearchResult;

        if (addressSearchResult.getAddressResponseDocuments() != null)
        {
            existingAddress = true;
        } else
        {
            existingAddress = false;
        }

        if (addressSearchResult.getPlaceCategoryDocuments() != null)
        {
            existingPlaceCategory = true;
        } else
        {
            existingPlaceCategory = false;
        }

        if (addressSearchResult.getPlaceKeywordDocuments() != null)
        {
            existingPlaceKeyword = true;
        } else
        {
            existingPlaceKeyword = false;
        }
    }

    @NonNull
    @Override
    public SearchResultViewPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.search_result_viewpager_item, parent, false);
        return new SearchResultViewPagerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewPagerHolder holder, int position)
    {
        if (holder instanceof SearchResultViewPagerHolder)
        {
            if (existingAddress)
            {
                holder.onBind(addressSearchResult, DownloadData.ADDRESS);
                existingAddress = false;
            } else if (existingPlaceKeyword)
            {
                holder.onBind(addressSearchResult, DownloadData.PLACE_KEYWORD);
                existingPlaceKeyword = false;
            } else if (existingPlaceCategory)
            {
                holder.onBind(addressSearchResult, DownloadData.PLACE_CATEGORY);
                existingPlaceCategory = false;
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return addressSearchResult.getResultNum();
    }

    class SearchResultViewPagerHolder extends RecyclerView.ViewHolder
    {
        private TextView addressType;
        private RecyclerView recyclerView;
        private SearchResultViewAdapter adapter;

        SearchResultViewPagerHolder(View view)
        {
            super(view);
            addressType = (TextView) view.findViewById(R.id.search_result_address_type_textview);
            recyclerView = (RecyclerView) view.findViewById(R.id.search_result_recyclerview);

            recyclerView.setLayoutManager(new LinearLayoutManager(SearchResultViewPagerAdapter.this.context));
            adapter = new SearchResultViewAdapter(SearchResultViewPagerAdapter.this.context);
            recyclerView.setAdapter(adapter);
        }

        public void onBind(AddressSearchResult addressSearchResult, int type)
        {
            if (type == DownloadData.ADDRESS)
            {
                addressType.setText(SearchResultViewPagerAdapter.this.context.getString(R.string.result_address));
                adapter.setAddressList(addressSearchResult.getAddressResponseDocuments());
            } else if (type == DownloadData.PLACE_KEYWORD)
            {
                addressType.setText(SearchResultViewPagerAdapter.this.context.getString(R.string.result_place));
                adapter.setPlaceKeywordList(addressSearchResult.getPlaceKeywordDocuments());
            } else if (type == DownloadData.PLACE_CATEGORY)
            {
                addressType.setText(SearchResultViewPagerAdapter.this.context.getString(R.string.result_place));
                adapter.setPlaceCategoryList(addressSearchResult.getPlaceCategoryDocuments());
            }
            adapter.notifyDataSetChanged();

        }
    }
}
