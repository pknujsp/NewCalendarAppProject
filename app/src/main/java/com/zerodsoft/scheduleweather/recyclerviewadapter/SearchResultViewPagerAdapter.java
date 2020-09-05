package com.zerodsoft.scheduleweather.recyclerviewadapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.MapController;
import com.zerodsoft.scheduleweather.retrofit.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.LocationSearchResult;

import java.util.ArrayList;
import java.util.List;


public class SearchResultViewPagerAdapter extends RecyclerView.Adapter<SearchResultViewPagerAdapter.SearchResultViewPagerHolder>
{
    private LocationSearchResult locationSearchResult = new LocationSearchResult();
    private LocalApiPlaceParameter parameters;
    private String searchWord;
    private boolean existingAddress = false;
    private boolean existingPlaceKeyword = false;
    private boolean existingPlaceCategory = false;

    private final int ADDRESS_VIEW_PAGER_POSITION = 0;
    private final int PLACE_VIEW_PAGER_POSITION = 1;

    private SparseArray<SearchResultViewPagerHolder> holderSparseArray = new SparseArray<>();
    private OnScrollResultList onScrollResultList;

    public interface OnScrollResultList
    {
        // 뷰페이지에서 스크롤시 추가 데이터를 불러올때 사용
        void onScroll(LocalApiPlaceParameter parameter, int type);
    }

    public void onAddExtraData(LocalApiPlaceParameter parameter, int type, LocationSearchResult locationSearchResult)
    {
        for (int i = 0; i < holderSparseArray.size(); ++i)
        {
            if (type == holderSparseArray.get(i).type)
            {
                holderSparseArray.get(i).addExtraData(locationSearchResult);
                break;
            }
        }
    }

    public void setParameters(Bundle bundle)
    {
        this.parameters = bundle.getParcelable("parameters");
    }

    public SearchResultViewPagerAdapter setParameters(LocalApiPlaceParameter parameters)
    {
        this.parameters = parameters;
        return this;
    }

    public SearchResultViewPagerAdapter setSearchWord(String searchWord)
    {
        this.searchWord = searchWord;
        return this;
    }

    public SearchResultViewPagerAdapter(Activity activity)
    {
        this.onScrollResultList = (OnScrollResultList) activity;
        this.viewHolders = new ArrayList<>();
        this.sparseIntArray = new SparseIntArray();
    }

    public void setLocationSearchResult(LocationSearchResult locationSearchResult)
    {
        this.locationSearchResult = locationSearchResult;
        List<Integer> resultTypes = locationSearchResult.getResultTypes();

        existingPlaceCategory = false;
        existingPlaceKeyword = false;
        existingAddress = false;

        for (int type : resultTypes)
        {
            if (type == MapController.TYPE_PLACE_CATEGORY)
            {
                existingPlaceCategory = true;
                break;
            } else if (type == MapController.TYPE_ADDRESS)
            {
                existingAddress = true;
            } else if (type == MapController.TYPE_PLACE_KEYWORD)
            {
                existingPlaceKeyword = true;
            }
        }
    }

    public void changeAddressSearchResult(LocationSearchResult locationSearchResult)
    {
        this.locationSearchResult = locationSearchResult;
    }

    public int getCurrentListType(int position)
    {
        return sparseIntArray.get(position);
    }

    public void setCurrentPage(int page)
    {
        for (SearchResultViewPagerHolder holder : viewHolders)
        {
            holder.setRecyclerViewCurrentPage(page);
        }
    }

    @NonNull
    @Override
    public SearchResultViewPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_viewpager_item, parent, false);
        return new SearchResultViewPagerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewPagerHolder holder, int position)
    {
        // 0번에 주소 리스트 표시, 1번에는 장소 리스트 표시
        // 주소 데이터가 없으면 0번에 장소 리스트 표시
        // 데이터 유무의 경우의 수 : 장소+주소, 장소, 주소, 카테고리
        if (position == ADDRESS_VIEW_PAGER_POSITION)
        {
            if (existingAddress)
            {
                holder.onBind(locationSearchResult, MapController.TYPE_ADDRESS, parameters);
                existingAddress = false;
            } else if (existingPlaceKeyword)
            {
                holder.onBind(locationSearchResult, MapController.TYPE_PLACE_KEYWORD, parameters);
                existingPlaceKeyword = false;
            } else if (existingPlaceCategory)
            {
                holder.onBind(locationSearchResult, MapController.TYPE_PLACE_CATEGORY, parameters);
                existingPlaceCategory = false;
            }
        } else if (position == PLACE_VIEW_PAGER_POSITION)
        {
            if (existingPlaceKeyword)
            {
                holder.onBind(locationSearchResult, MapController.TYPE_PLACE_KEYWORD, parameters);
                existingPlaceKeyword = false;
            } else if (existingPlaceCategory)
            {
                holder.onBind(locationSearchResult, MapController.TYPE_PLACE_CATEGORY, parameters);
                existingPlaceCategory = false;
            }
        }
        holderSparseArray.put(position, holder);
    }

    @Override
    public int getItemCount()
    {
        return locationSearchResult.getResultNum();
    }

    class SearchResultViewPagerHolder extends RecyclerView.ViewHolder
    {
        private TextView resultType;
        private TextView resultNum;
        private RecyclerView recyclerView;
        private SearchResultViewAdapter adapter;
        private int type;

        private LocalApiPlaceParameter parameters;

        public void addExtraData(LocationSearchResult locationSearchResult)
        {
            switch (type)
            {
                case MapController.TYPE_ADDRESS:
                    adapter.addAddressData(locationSearchResult);
                    break;
                case MapController.TYPE_PLACE_KEYWORD:
                    adapter.addPlaceKeywordData(locationSearchResult);
                    break;
                case MapController.TYPE_PLACE_CATEGORY:
                    adapter.addPlaceCategoryData(locationSearchResult);
                    break;
            }
            adapter.notifyDataSetChanged();
        }

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
                                case MapController.TYPE_ADDRESS:
                                    MapController.searchAddress(handler, parameters);
                                    break;
                                case MapController.TYPE_PLACE_KEYWORD:
                                    MapController.searchPlaceKeyWord(handler, parameters);
                                    break;
                                case MapController.TYPE_PLACE_CATEGORY:
                                    MapController.searchPlaceCategory(handler, parameters);
                                    break;
                            }
                            Toast.makeText(context, "추가 데이터를 가져오는 중", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        public void onBind(LocationSearchResult locationSearchResult, int type, LocalApiPlaceParameter parameters)
        {
            this.type = type;
            this.parameters = parameters;

            if (type == MapController.TYPE_ADDRESS)
            {
                resultType.setText(SearchResultViewPagerAdapter.this.context.getString(R.string.result_address));
                resultNum.setText(Integer.toString(locationSearchResult.getAddressResponseMeta().getTotalCount()));
                adapter.setAddressList(locationSearchResult.getAddressResponseDocuments(), locationSearchResult.getAddressResponseMeta());
            } else if (type == KakaoLocalApi.TYPE_PLACE_KEYWORD)
            {
                resultType.setText(SearchResultViewPagerAdapter.this.context.getString(R.string.result_place));
                resultNum.setText(Integer.toString(locationSearchResult.getPlaceKeywordMeta().getTotalCount()));
                adapter.setPlaceKeywordList(locationSearchResult.getPlaceKeywordDocuments(), locationSearchResult.getPlaceKeywordMeta());
            } else if (type == KakaoLocalApi.TYPE_PLACE_CATEGORY)
            {
                resultType.setText(SearchResultViewPagerAdapter.this.context.getString(R.string.result_place));
                resultNum.setText(Integer.toString(locationSearchResult.getPlaceCategoryMeta().getTotalCount()));
                adapter.setPlaceCategoryList(locationSearchResult.getPlaceCategoryDocuments(), locationSearchResult.getPlaceCategoryMeta());
            }
            adapter.setDownloadedTime(locationSearchResult.getDownloadedTime());
            adapter.notifyDataSetChanged();
        }

        public int getType()
        {
            return type;
        }

        public void setRecyclerViewCurrentPage(int page)
        {
            adapter.setCurrentPage(page);
        }
    }
}
