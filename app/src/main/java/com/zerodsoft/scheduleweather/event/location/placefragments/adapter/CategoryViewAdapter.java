package com.zerodsoft.scheduleweather.event.location.placefragments.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.RecyclerViewItemDecoration;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.event.location.placefragments.LocationInfo;
import com.zerodsoft.scheduleweather.event.location.placefragments.fragment.PlacesFragment;
import com.zerodsoft.scheduleweather.event.location.placefragments.interfaces.IClickedPlaceItem;
import com.zerodsoft.scheduleweather.event.location.placefragments.interfaces.IPlaceItem;
import com.zerodsoft.scheduleweather.event.location.placefragments.model.PlaceCategory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryViewAdapter extends RecyclerView.Adapter<CategoryViewAdapter.CategoryViewHolder> implements IPlaceItem
{
    private LocationInfo LocationInfo;
    private Context context;
    private List<PlaceCategory> categories;
    private IClickedPlaceItem iClickedPlaceItem;
    private PlacesFragment fragment;
    private Map<String, CategoryViewHolder> viewHolderMap;

    public CategoryViewAdapter(LocationInfo LocationInfo, List<PlaceCategory> categories, PlacesFragment fragment)
    {
        this.LocationInfo = LocationInfo;
        this.categories = categories;
        this.iClickedPlaceItem = (IClickedPlaceItem) fragment;
        this.fragment = fragment;
        this.viewHolderMap = new HashMap<>();
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
        holder.onBind();
    }

    @Override
    public List<PlaceDocuments> getPlaceItems(String categoryName)
    {
        return viewHolderMap.get(categoryName).adapter.getCurrentList().snapshot();
    }

    @Override
    public List<String> getCategoryNames()
    {
        return null;
    }

    @Override
    public int getPlaceItemsSize(String categoryName)
    {
        return viewHolderMap.get(categoryName).adapter.getCurrentList().size();
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
        private String categoryDescription;

        public CategoryViewHolder(View view)
        {
            super(view);
            itemRecyclerView = (RecyclerView) view.findViewById(R.id.map_category_itemsview);
            itemRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false));
            itemRecyclerView.addItemDecoration(new RecyclerViewItemDecoration((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, context.getResources().getDisplayMetrics())));
            viewModel = new ViewModelProvider(fragment).get(PlacesViewModel.class);
        }

        public void onBind()
        {
            LocalApiPlaceParameter placeParameter = new LocalApiPlaceParameter();
            placeParameter.setPage(LocalApiPlaceParameter.DEFAULT_PAGE).setRadius(LocalApiPlaceParameter.DEFAULT_RADIUS)
                    .setSize(LocalApiPlaceParameter.DEFAULT_SIZE).setSort(LocalApiPlaceParameter.SORT_ACCURACY)
                    .setX(Double.toString(LocationInfo.getLongitude()))
                    .setY(Double.toString(LocationInfo.getLatitude()));

            PlaceCategory placeCategory = categories.get(getAdapterPosition());
            if (placeCategory.getCategoryCode() == null)
            {
                placeParameter.setQuery(placeCategory.getCategoryName());
            } else
            {
                placeParameter.setCategoryGroupCode(placeCategory.getCategoryCode());
            }
            categoryDescription = placeCategory.getCategoryName();

            adapter = new PlaceItemsAdapters(iClickedPlaceItem);
            itemRecyclerView.setAdapter(adapter);

            viewModel.init(placeParameter);
            viewModel.getPagedListMutableLiveData().observe(fragment.getLifeCycleOwner(), adapter::submitList);

            ((TextView) itemView.findViewById(R.id.map_category_name)).setText(categoryDescription);

            ((Button) itemView.findViewById(R.id.map_category_more)).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    iClickedPlaceItem.onClickedMore(categoryDescription);
                }
            });

            viewHolderMap.put(categoryDescription, this);
        }

    }

}
