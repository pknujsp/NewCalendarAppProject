package com.zerodsoft.scheduleweather.event.location.placefragments.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.etc.RecyclerViewItemDecoration;
import com.zerodsoft.scheduleweather.event.location.placefragments.interfaces.IPlacesFragment;
import com.zerodsoft.scheduleweather.kakaomap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.event.location.placefragments.interfaces.IClickedPlaceItem;
import com.zerodsoft.scheduleweather.event.location.placefragments.interfaces.IPlaceItem;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryViewAdapter extends RecyclerView.Adapter<CategoryViewAdapter.CategoryViewHolder> implements IPlaceItem
{
    private final LocationDTO locationDTO;
    private Context context;
    private List<PlaceCategoryDTO> categories;
    private final IClickedPlaceItem iClickedPlaceItem;
    private final IPlacesFragment iPlacesFragment;
    private Map<PlaceCategoryDTO, CategoryViewHolder> viewHolderMap;

    public CategoryViewAdapter(LocationDTO locationDTO, List<PlaceCategoryDTO> categories, IClickedPlaceItem iClickedPlaceItem, IPlacesFragment iPlacesFragment)
    {
        this.locationDTO = locationDTO;
        this.categories = categories;
        this.iClickedPlaceItem = iClickedPlaceItem;
        this.viewHolderMap = new HashMap<>();
        this.iPlacesFragment = iPlacesFragment;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context = parent.getContext();
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.place_category_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position)
    {
        holder.onBind();
    }

    @Override
    public List<PlaceDocuments> getPlaceItems(PlaceCategoryDTO placeCategory)
    {
        return viewHolderMap.get(placeCategory).adapter.getCurrentList().snapshot();
    }


    @Override
    public int getPlaceItemsSize(PlaceCategoryDTO placeCategory)
    {
        return viewHolderMap.get(placeCategory).adapter.getCurrentList().size();
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
        private CircularProgressIndicator progressIndicator;

        public CategoryViewHolder(View view)
        {
            super(view);
            progressIndicator = (CircularProgressIndicator) view.findViewById(R.id.progress_indicator);
            itemRecyclerView = (RecyclerView) view.findViewById(R.id.map_category_itemsview);
            itemRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false));
            itemRecyclerView.addItemDecoration(new RecyclerViewItemDecoration((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, context.getResources().getDisplayMetrics())));
            viewModel = new ViewModelProvider(iPlacesFragment.getFragment()).get(PlacesViewModel.class);
        }

        public void onBind()
        {
            PlaceCategoryDTO placeCategoryDTO = categories.get(getAdapterPosition());

            LocalApiPlaceParameter placeParameter = LocalParameterUtil.getPlaceParameter(placeCategoryDTO.getCode(), locationDTO.getLatitude(),
                    locationDTO.getLongitude(), LocalApiPlaceParameter.DEFAULT_SIZE, LocalApiPlaceParameter.DEFAULT_PAGE,
                    LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY);

            categoryDescription = placeCategoryDTO.getDescription();

            adapter = new PlaceItemsAdapters(iClickedPlaceItem, placeCategoryDTO);
            itemRecyclerView.setAdapter(adapter);

            viewModel.init(placeParameter);
            viewModel.getPagedListMutableLiveData().observe(iPlacesFragment.getFragment(), new Observer<PagedList<PlaceDocuments>>()
            {
                @Override
                public void onChanged(PagedList<PlaceDocuments> placeDocuments)
                {
                    progressIndicator.setVisibility(View.GONE);
                    adapter.submitList(placeDocuments);
                }
            });


            ((TextView) itemView.findViewById(R.id.map_category_name)).setText(categoryDescription);
            ((Button) itemView.findViewById(R.id.map_category_more)).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    iClickedPlaceItem.onClickedMore(placeCategoryDTO, adapter.getCurrentList().snapshot());
                }
            });

            viewHolderMap.put(placeCategoryDTO, this);
        }

    }

}
