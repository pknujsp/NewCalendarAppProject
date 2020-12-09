package com.zerodsoft.scheduleweather.scheduleinfo.placefragments.categoryview;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.RecyclerViewItemDecoration;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.scheduleinfo.placefragments.LocationInfo;
import com.zerodsoft.scheduleweather.scheduleinfo.placefragments.PlacesFragment;
import com.zerodsoft.scheduleweather.scheduleinfo.placefragments.categoryview.adapter.PlaceItemsAdapters;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.PlacesViewModel;

import java.util.List;

public class CategoryViewAdapter extends RecyclerView.Adapter<CategoryViewAdapter.CategoryViewHolder>
{
    private LocationInfo locationInfo;
    private Context context;
    private List<String> categories;
    private PlacesFragment fragment;

    public CategoryViewAdapter(LocationInfo locationInfo, List<String> categories, PlacesFragment fragment)
    {
        this.locationInfo = locationInfo;
        this.categories = categories;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        this.context = parent.getContext();
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.place_category_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position)
    {
        holder.onBind(categories.get(position));
    }

    @Override
    public int getItemCount()
    {
        return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder
    {
        private RecyclerView itemRecyclerView;
        private PlacesViewModel viewModel;
        private PlaceItemsAdapters adapter;

        public CategoryViewHolder(View view)
        {
            super(view);
            itemRecyclerView = (RecyclerView) view.findViewById(R.id.map_category_itemsview);
            itemRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false));
            itemRecyclerView.addItemDecoration(new RecyclerViewItemDecoration((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, context.getResources().getDisplayMetrics())));
            viewModel = new ViewModelProvider(fragment).get(PlacesViewModel.class);
        }

        public void onBind(String query)
        {
            ((TextView) itemView.findViewById(R.id.map_category_name)).setText(query);

            LocalApiPlaceParameter placeParameter = new LocalApiPlaceParameter();
            placeParameter.setPage(LocalApiPlaceParameter.DEFAULT_PAGE).setRadius(LocalApiPlaceParameter.DEFAULT_RADIUS)
                    .setSize(LocalApiPlaceParameter.DEFAULT_SIZE).setSort(LocalApiPlaceParameter.SORT_ACCURACY)
                    .setX(Double.toString(locationInfo.getLongitude()))
                    .setY(Double.toString(locationInfo.getLatitude()));

            if (KakaoLocalApiCategoryUtil.isCategory(query))
            {
                placeParameter.setCategoryGroupCode(KakaoLocalApiCategoryUtil.getName(Integer.parseInt(query)));
            } else
            {
                placeParameter.setQuery(query);
            }
            adapter = new PlaceItemsAdapters(context);

            viewModel.init(placeParameter);
            viewModel.getPagedListMutableLiveData().observe(fragment.getViewLifecycleOwner(), adapter::submitList);
            itemRecyclerView.setAdapter(adapter);
        }

    }


}
