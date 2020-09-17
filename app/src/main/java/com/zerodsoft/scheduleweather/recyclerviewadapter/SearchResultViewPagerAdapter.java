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
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.SearchFragment;
import com.zerodsoft.scheduleweather.activity.mapactivity.MapActivity;
import com.zerodsoft.scheduleweather.retrofit.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.LocationSearchResult;

import java.util.List;


public class SearchResultViewPagerAdapter extends RecyclerView.Adapter<SearchResultViewPagerAdapter.SearchResultViewPagerHolder>
{
    public static final String TAG = "SearchResultViewPagerAdapter";

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
        MapActivity.searchResult = new LocationSearchResult();
    }

    public void clearHolderSparseArr()
    {
        holderSparseArray.clear();
    }

    public void addExtraData(int dataType)
    {
        // 스크롤할때 추가 데이터를 받아옴
        for (int i = 0; i < holderSparseArray.size(); ++i)
        {
            if (dataType == holderSparseArray.get(i).dataType)
            {
                holderSparseArray.get(i).addExtraData();
                break;
            }
        }
    }

    public void setData()
    {
        // 데이터 설정
        List<Integer> resultTypes = MapActivity.searchResult.getResultTypes();

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

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView)
    {
        // recyclerview가 파괴될 경우에 호출된다
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public SearchResultViewPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new SearchResultViewPagerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_viewpager_item, parent, false));
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
                holder.onBind(MapController.TYPE_ADDRESS);
                existingAddress = false;
            } else if (existingPlaceKeyword)
            {
                holder.onBind(MapController.TYPE_PLACE_KEYWORD);
                existingPlaceKeyword = false;
            } else if (existingPlaceCategory)
            {
                holder.onBind(MapController.TYPE_PLACE_CATEGORY);
                existingPlaceCategory = false;
            }
        } else if (position == PLACE_VIEW_PAGER_POSITION)
        {
            if (existingPlaceKeyword)
            {
                holder.onBind(MapController.TYPE_PLACE_KEYWORD);
                existingPlaceKeyword = false;
            } else if (existingPlaceCategory)
            {
                holder.onBind(MapController.TYPE_PLACE_CATEGORY);
                existingPlaceCategory = false;
            }
        }
        holderSparseArray.put(position, holder);
    }

    @Override
    public int getItemCount()
    {
        return MapActivity.searchResult.getResultNum();
    }

    class SearchResultViewPagerHolder extends RecyclerView.ViewHolder
    {
        private TextView dataTypeTextView;
        private TextView resultNum;
        private RecyclerView recyclerView;
        private SearchResultViewAdapter adapter;
        private int dataType;

        public void addExtraData()
        {
            switch (dataType)
            {
                case MapController.TYPE_ADDRESS:
                    adapter.addAddressData();
                    break;
                case MapController.TYPE_PLACE_KEYWORD:
                    adapter.addPlaceKeywordData();
                    break;
                case MapController.TYPE_PLACE_CATEGORY:
                    adapter.addPlaceCategoryData();
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

            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            adapter = new SearchResultViewAdapter(activity);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
            {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
                {
                    // 스크롤시 추가 데이터를 다운로드
                    if (!recyclerView.canScrollVertically(1))
                    {
                        int page = adapter.getCurrentPage();
                        if (page < 45 && !adapter.isEnd())
                        {
                            MapActivity.parameters.setPage(Integer.toString(++page));
                            onDownloadListener.requestData(dataType, TAG);
                            Toast.makeText(activity, "추가 데이터를 가져오는 중", Toast.LENGTH_SHORT).show();
                        }
                    }
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
        }

        public void onBind(int type)
        {
            this.dataType = type;

            if (type == MapController.TYPE_ADDRESS)
            {
                dataTypeTextView.setText(activity.getString(R.string.result_address));
                resultNum.setText(Integer.toString(MapActivity.searchResult.getAddressResponse().getAddressResponseMeta().getTotalCount()));
                adapter.setAddressList();
            } else if (type == MapController.TYPE_PLACE_KEYWORD)
            {
                dataTypeTextView.setText(activity.getString(R.string.result_place));
                resultNum.setText(Integer.toString(MapActivity.searchResult.getPlaceKeywordResponse().getPlaceKeywordMeta().getTotalCount()));
                adapter.setPlaceKeywordList();
            } else if (type == MapController.TYPE_PLACE_CATEGORY)
            {
                dataTypeTextView.setText(activity.getString(R.string.result_place));
                resultNum.setText(Integer.toString(MapActivity.searchResult.getPlaceCategoryResponse().getPlaceCategoryMeta().getTotalCount()));
                adapter.setPlaceCategoryList();
            }
            adapter.setDownloadedDate(MapActivity.searchResult.getDownloadedDate());
            adapter.notifyDataSetChanged();
        }
    }
}
