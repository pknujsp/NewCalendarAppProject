package com.zerodsoft.scheduleweather.event.places.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.event.places.interfaces.OnClickedPlacesListListener;
import com.zerodsoft.scheduleweather.kakaomap.callback.PlaceItemCallback;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import java.util.ArrayList;
import java.util.List;

public class PlaceItemInMapViewAdapter extends RecyclerView.Adapter<PlaceItemInMapViewAdapter.PlaceItemInMapViewHolder>
{
    private List<? extends KakaoLocalDocument> placeDocumentsList = new ArrayList<>();
    private PlaceDocuments placeDocuments;
    private AddressResponseDocuments addressDocuments;

    public PlaceItemInMapViewAdapter()
    {

    }

    public void setPlaceDocumentsList(List<? extends KakaoLocalDocument> placeDocumentsList)
    {
        this.placeDocumentsList = placeDocumentsList;
    }

    @NonNull
    @Override
    public PlaceItemInMapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item_around_location, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        view.setLayoutParams(layoutParams);

        return new PlaceItemInMapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceItemInMapViewHolder holder, int position)
    {
        holder.bind(placeDocumentsList.get(position));
    }


    @Override
    public int getItemCount()
    {
        return placeDocumentsList.size();
    }

    class PlaceItemInMapViewHolder extends RecyclerView.ViewHolder
    {
        private TextView placeNameTextView;
        private TextView placeAddressTextView;
        private TextView placeCategoryTextView;
        private TextView placeDistanceTextView;

        public PlaceItemInMapViewHolder(@NonNull View view)
        {
            super(view);
            placeNameTextView = (TextView) view.findViewById(R.id.place_item_name);
            placeAddressTextView = (TextView) view.findViewById(R.id.place_item_address);
            placeCategoryTextView = (TextView) view.findViewById(R.id.place_item_category);
            placeDistanceTextView = (TextView) view.findViewById(R.id.place_item_distance);

            view.getRootView().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                }
            });
        }

        public void bind(KakaoLocalDocument data)
        {
            if (data instanceof PlaceDocuments)
            {
                placeDocuments = (PlaceDocuments) data;

                placeNameTextView.setText(placeDocuments.getPlaceName());
                placeAddressTextView.setText(placeDocuments.getAddressName());
                placeCategoryTextView.setText(placeDocuments.getCategoryName());
                placeDistanceTextView.setText(placeDocuments.getDistance() + "m");
            } else
            {
                addressDocuments = (AddressResponseDocuments) data;

                placeNameTextView.setText(addressDocuments.getAddressName());
            }
        }
    }
}
