package com.zerodsoft.scheduleweather.recyclerviewadapter;

import android.app.Activity;
import android.util.SparseArray;
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

import java.util.List;


public class SearchResultViewPagerAdapter extends RecyclerView.Adapter<SearchResultViewPagerAdapter.SearchResultViewPagerHolder>
{
    public static final String TAG = "SearchResultViewPagerAdapter";

    private LocationSearchResult locationSearchResult;
    private LocalApiPlaceParameter parameters;
    private boolean existingAddress = false;
    private boolean existingPlaceKeyword = false;
    private boolean existingPlaceCategory = false;

    private Activity activity;

    private final int ADDRESS_VIEW_PAGER_POSITION = 0;
    private final int PLACE_VIEW_PAGER_POSITION = 1;

    private SparseArray<SearchResultViewPagerHolder> holderSparseArray = new SparseArray<>();

    private MapController.OnDownloadListener onDownloadListener;

    public SearchResultViewPagerAdapter(Activity activity)
    {
        this.activity = activity;
        onDownloadListener = (MapController.OnDownloadListener) activity;
        locationSearchResult = new LocationSearchResult();
        locationSearchResult.setResultNum(0);
    }

    public void addExtraData(LocalApiPlaceParameter parameter, int type, LocationSearchResult locationSearchResult)
    {
        // 스크롤할때 추가 데이터를 받아옴
        for (int i = 0; i < holderSparseArray.size(); ++i)
        {
            if (type == holderSparseArray.get(i).dataType)
            {
                holderSparseArray.get(i).addExtraData(locationSearchResult);
                break;
            }
        }
    }

    public void setData(LocalApiPlaceParameter parameter, LocationSearchResult locationSearchResult)
    {
        // 데이터 설정
        this.parameters = parameter;
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

    public int getCurrentListType(int position)
    {
        return holderSparseArray.get(position).dataType;
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
        private TextView dataTypeTextView;
        private TextView resultNum;
        private RecyclerView recyclerView;
        private SearchResultViewAdapter adapter;
        private int dataType;

        private LocalApiPlaceParameter parameters;

        public void addExtraData(LocationSearchResult locationSearchResult)
        {
            switch (dataType)
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
            dataTypeTextView = (TextView) view.findViewById(R.id.search_result_type_textview);
            resultNum = (TextView) view.findViewById(R.id.search_result_items_num_textview);
            recyclerView = (RecyclerView) view.findViewById(R.id.search_result_recyclerview);

            recyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));
            adapter = new SearchResultViewAdapter(activity);
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

                            onDownloadListener.requestData(parameters, dataType, TAG);
                            Toast.makeText(activity, "추가 데이터를 가져오는 중", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        public void onBind(LocationSearchResult locationSearchResult, int type, LocalApiPlaceParameter parameters)
        {
            this.dataType = type;
            this.parameters = parameters;

            if (type == MapController.TYPE_ADDRESS)
            {
                dataTypeTextView.setText(activity.getString(R.string.result_address));
                resultNum.setText(Integer.toString(locationSearchResult.getAddressResponse().getAddressResponseMeta().getTotalCount()));
                adapter.setAddressList(locationSearchResult);
            } else if (type == MapController.TYPE_PLACE_KEYWORD)
            {
                dataTypeTextView.setText(activity.getString(R.string.result_place));
                resultNum.setText(Integer.toString(locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordMeta().getTotalCount()));
                adapter.setPlaceKeywordList(locationSearchResult);
            } else if (type == MapController.TYPE_PLACE_CATEGORY)
            {
                dataTypeTextView.setText(activity.getString(R.string.result_place));
                resultNum.setText(Integer.toString(locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryMeta().getTotalCount()));
                adapter.setPlaceCategoryList(locationSearchResult);
            }
            adapter.setDownloadedDate(locationSearchResult.getDownloadedDate());
            adapter.notifyDataSetChanged();
        }
    }
}
