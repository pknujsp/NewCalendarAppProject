package com.zerodsoft.scheduleweather.RecyclerVIewAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Retrofit.DownloadData;
import com.zerodsoft.scheduleweather.Retrofit.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressSearchResult;

import java.util.List;


public class SearchResultViewPagerAdapter extends RecyclerView.Adapter<SearchResultViewPagerAdapter.SearchResultViewPagerHolder>
{
    private Activity context;
    private AddressSearchResult addressSearchResult = new AddressSearchResult();
    private LocalApiPlaceParameter parameters;
    private String searchWord;
    private boolean existingAddress = false;
    private boolean existingPlaceKeyword = false;
    private boolean existingPlaceCategory = false;


    public void setParameters(Bundle bundle)
    {
        this.parameters = bundle.getParcelable("parameters");
    }

    public SearchResultViewPagerAdapter setSearchWord(String searchWord)
    {
        this.searchWord = searchWord;
        return this;
    }

    public SearchResultViewPagerAdapter(Activity activity)
    {
        this.context = activity;
    }

    public void setAddressSearchResult(AddressSearchResult addressSearchResult)
    {
        this.addressSearchResult = addressSearchResult;
        List<Integer> resultTypes = addressSearchResult.getResultTypes();

        existingPlaceCategory = false;
        existingPlaceKeyword = false;
        existingAddress = false;

        for (int type : resultTypes)
        {
            if (type == DownloadData.PLACE_CATEGORY)
            {
                existingPlaceCategory = true;
                break;
            } else if (type == DownloadData.ADDRESS)
            {
                existingAddress = true;
            } else if (type == DownloadData.PLACE_KEYWORD)
            {
                existingPlaceKeyword = true;
            }
        }
    }

    public void changeAddressSearchResult(AddressSearchResult addressSearchResult)
    {
        this.addressSearchResult = addressSearchResult;
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


        if (existingAddress)
        {
            holder.onBind(addressSearchResult, DownloadData.ADDRESS, parameters);
            existingAddress = false;
        } else if (existingPlaceKeyword)
        {
            holder.onBind(addressSearchResult, DownloadData.PLACE_KEYWORD, parameters);
            existingPlaceKeyword = false;
        } else if (existingPlaceCategory)
        {
            holder.onBind(addressSearchResult, DownloadData.PLACE_CATEGORY, parameters);
            existingPlaceCategory = false;
        }
    }

    @Override
    public int getItemCount()
    {
        return addressSearchResult.getResultNum();
    }

    class SearchResultViewPagerHolder extends RecyclerView.ViewHolder
    {
        private TextView resultType;
        private TextView resultNum;
        private RecyclerView recyclerView;
        private SearchResultViewAdapter adapter;
        private int type;

        private LocalApiPlaceParameter parameters;

        @SuppressLint("HandlerLeak")
        private Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                Bundle bundle = msg.getData();

                switch (msg.what)
                {
                    case DownloadData.ADDRESS:
                        adapter.addAddressData(bundle.getParcelableArrayList("documents"), bundle.getParcelable("meta"));
                        break;
                    case DownloadData.PLACE_KEYWORD:
                        adapter.addPlaceKeywordData(bundle.getParcelableArrayList("documents"), bundle.getParcelable("meta"));
                        break;
                    case DownloadData.PLACE_CATEGORY:
                        adapter.addPlaceCategoryData(bundle.getParcelableArrayList("documents"), bundle.getParcelable("meta"));
                        break;
                }
                adapter.notifyDataSetChanged();
            }
        };

        SearchResultViewPagerHolder(View view)
        {
            super(view);
            resultType = (TextView) view.findViewById(R.id.search_result_type_textview);
            resultNum = (TextView) view.findViewById(R.id.search_result_items_num_textview);
            recyclerView = (RecyclerView) view.findViewById(R.id.search_result_recyclerview);

            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new SearchResultViewAdapter(context);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
            {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
                {
                    super.onScrolled(recyclerView, dx, dy);
                    if (!recyclerView.canScrollVertically(1))
                    {
                        int page = adapter.getCurrentPage();
                        if (page < 45 && !adapter.isEnd())
                        {
                            parameters.setPage(Integer.toString(++page));
                            adapter.setCurrentPage(page);

                            switch (type)
                            {
                                case DownloadData.ADDRESS:
                                    DownloadData.searchAddress(handler, parameters);
                                    break;
                                case DownloadData.PLACE_KEYWORD:
                                    DownloadData.searchPlaceKeyWord(handler, parameters);
                                    break;
                                case DownloadData.PLACE_CATEGORY:
                                    DownloadData.searchPlaceCategory(handler, parameters);
                                    break;
                            }
                            Toast.makeText(context, "추가 데이터를 가져오는 중", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        public void onBind(AddressSearchResult addressSearchResult, int type, LocalApiPlaceParameter parameters)
        {
            this.type = type;
            this.parameters = parameters;

            if (type == DownloadData.ADDRESS)
            {
                resultType.setText(SearchResultViewPagerAdapter.this.context.getString(R.string.result_address));
                resultNum.setText(Integer.toString(addressSearchResult.getAddressResponseMeta().getTotalCount()));
                adapter.setAddressList(addressSearchResult.getAddressResponseDocuments(), addressSearchResult.getAddressResponseMeta());
            } else if (type == DownloadData.PLACE_KEYWORD)
            {
                resultType.setText(SearchResultViewPagerAdapter.this.context.getString(R.string.result_place));
                resultNum.setText(Integer.toString(addressSearchResult.getPlaceKeywordMeta().getTotalCount()));
                adapter.setPlaceKeywordList(addressSearchResult.getPlaceKeywordDocuments(), addressSearchResult.getPlaceKeywordMeta());
            } else if (type == DownloadData.PLACE_CATEGORY)
            {
                resultType.setText(SearchResultViewPagerAdapter.this.context.getString(R.string.result_place));
                resultNum.setText(Integer.toString(addressSearchResult.getPlaceCategoryMeta().getTotalCount()));
                adapter.setPlaceCategoryList(addressSearchResult.getPlaceCategoryDocuments(), addressSearchResult.getPlaceCategoryMeta());
            }
            adapter.setSearchWord(searchWord);
            adapter.setDownloadedTime(addressSearchResult.getDownloadedTime());
            adapter.notifyDataSetChanged();
        }
    }
}
