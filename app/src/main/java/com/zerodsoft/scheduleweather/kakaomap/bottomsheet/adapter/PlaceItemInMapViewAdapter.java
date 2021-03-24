package com.zerodsoft.scheduleweather.kakaomap.bottomsheet.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.PlacesItemBottomSheetButtonOnClickListener;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

import java.util.ArrayList;
import java.util.List;

public class PlaceItemInMapViewAdapter extends RecyclerView.Adapter<PlaceItemInMapViewAdapter.PlaceItemInMapViewHolder>
{
    private List<? extends KakaoLocalDocument> placeDocumentsList = new ArrayList<>();
    private PlaceDocuments placeDocuments;
    private AddressResponseDocuments addressDocuments;
    private PlacesItemBottomSheetButtonOnClickListener placesItemBottomSheetButtonOnClickListener;

    private int isVisibleSelectBtn;
    private int isVisibleUnSelectBtn;

    public PlaceItemInMapViewAdapter()
    {

    }

    public void setPlacesItemBottomSheetButtonOnClickListener(PlacesItemBottomSheetButtonOnClickListener placesItemBottomSheetButtonOnClickListener)
    {
        this.placesItemBottomSheetButtonOnClickListener = placesItemBottomSheetButtonOnClickListener;
    }

    public void setVisibleSelectBtn(int visibleSelectBtn)
    {
        isVisibleSelectBtn = visibleSelectBtn;
    }

    public void setVisibleUnSelectBtn(int visibleUnSelectBtn)
    {
        isVisibleUnSelectBtn = visibleUnSelectBtn;
    }

    public void setPlaceDocumentsList(List<? extends KakaoLocalDocument> placeDocumentsList)
    {
        this.placeDocumentsList = placeDocumentsList;
    }

    @NonNull
    @Override
    public PlaceItemInMapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new PlaceItemInMapViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_places_item, parent, false));
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

        private ImageButton favoriteButton;
        private Button selectButton;
        private Button unselectButton;

        public PlaceItemInMapViewHolder(@NonNull View view)
        {
            super(view);
            placeNameTextView = (TextView) view.findViewById(R.id.place_item_name);
            placeAddressTextView = (TextView) view.findViewById(R.id.place_item_address);
            placeCategoryTextView = (TextView) view.findViewById(R.id.place_item_category);
            placeDistanceTextView = (TextView) view.findViewById(R.id.place_item_distance);

            favoriteButton = (ImageButton) view.findViewById(R.id.add_to_favorite_placeitem_button);
            selectButton = (Button) view.findViewById(R.id.select_this_place_button);
            unselectButton = (Button) view.findViewById(R.id.unselect_this_place_button);

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

                placeCategoryTextView.setVisibility(View.VISIBLE);
                placeDistanceTextView.setVisibility(View.VISIBLE);
            } else
            {
                addressDocuments = (AddressResponseDocuments) data;

                placeNameTextView.setText(addressDocuments.getAddressName());
                placeCategoryTextView.setVisibility(View.GONE);
                placeDistanceTextView.setVisibility(View.GONE);
            }

            selectButton.setVisibility(isVisibleSelectBtn);
            unselectButton.setVisibility(isVisibleUnSelectBtn);

            selectButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    placesItemBottomSheetButtonOnClickListener.onSelectedLocation();
                }
            });

            unselectButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    placesItemBottomSheetButtonOnClickListener.onRemovedLocation();
                }
            });
        }

    }
}
