package com.zerodsoft.scheduleweather.navermap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnClickedBottomSheetListener;
import com.zerodsoft.scheduleweather.navermap.interfaces.PlacesItemBottomSheetButtonOnClickListener;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

import java.util.ArrayList;
import java.util.List;

public class LocationItemViewPagerAdapter extends RecyclerView.Adapter<LocationItemViewPagerAdapter.PlaceItemInMapViewHolder>
{
    public static final String TAG = "LocationItemViewPagerAdapter";
    private List<KakaoLocalDocument> placeDocumentsList = new ArrayList<>();
    private PlaceDocuments placeDocuments;
    private AddressResponseDocuments addressDocuments;
    private CoordToAddressDocuments coordToAddressDocuments;
    private PlacesItemBottomSheetButtonOnClickListener placesItemBottomSheetButtonOnClickListener;
    private OnClickedBottomSheetListener onClickedBottomSheetListener;

    private int isVisibleSelectBtn;
    private int isVisibleUnSelectBtn;

    public LocationItemViewPagerAdapter()
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
        this.placeDocumentsList.addAll(placeDocumentsList);
    }

    public void setOnClickedBottomSheetListener(OnClickedBottomSheetListener onClickedBottomSheetListener)
    {
        this.onClickedBottomSheetListener = onClickedBottomSheetListener;
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

    public List<KakaoLocalDocument> getPlaceDocumentsList()
    {
        return placeDocumentsList;
    }

    @Override
    public int getItemCount()
    {
        return placeDocumentsList.size();
    }

    class PlaceItemInMapViewHolder extends RecyclerView.ViewHolder
    {
        private CardView cardView;

        private TextView placeNameTextView;
        private TextView placeAddressTextView;
        private TextView placeCategoryTextView;
        private TextView placeDistanceTextView;

        private TextView addressIndex;
        private TextView addressName;
        private TextView anotherAddressType;
        private TextView anotherAddressName;

        private LinearLayout placeLayout;
        private LinearLayout addressLayout;

        private ImageButton favoriteButton;
        private Button selectButton;
        private Button unselectButton;

        public PlaceItemInMapViewHolder(@NonNull View view)
        {
            super(view);
            placeLayout = (LinearLayout) view.findViewById(R.id.place_layout);
            addressLayout = (LinearLayout) view.findViewById(R.id.address_layout);

            placeNameTextView = (TextView) view.findViewById(R.id.place_item_name);
            placeAddressTextView = (TextView) view.findViewById(R.id.place_item_address);
            placeCategoryTextView = (TextView) view.findViewById(R.id.place_item_category);
            placeDistanceTextView = (TextView) view.findViewById(R.id.place_item_distance);

            addressIndex = (TextView) addressLayout.findViewById(R.id.address_index);
            addressName = (TextView) addressLayout.findViewById(R.id.address_name);
            anotherAddressType = (TextView) addressLayout.findViewById(R.id.another_address_type);
            anotherAddressName = (TextView) addressLayout.findViewById(R.id.another_address_name);

            favoriteButton = (ImageButton) view.findViewById(R.id.add_to_favorite_placeitem_button);
            selectButton = (Button) view.findViewById(R.id.select_this_place_button);
            unselectButton = (Button) view.findViewById(R.id.unselect_this_place_button);

            addressIndex.setVisibility(View.GONE);

            cardView = itemView.findViewById(R.id.place_item_cardview_in_bottomsheet);
            cardView.setOnClickListener(onClickListener);
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

                placeLayout.setVisibility(View.VISIBLE);
                addressLayout.setVisibility(View.GONE);
                placeDistanceTextView.setVisibility(View.VISIBLE);
            } else if (data instanceof AddressResponseDocuments)
            {
                addressDocuments = (AddressResponseDocuments) data;

                addressName.setText(addressDocuments.getAddressName());
                if (addressDocuments.getAddressResponseRoadAddress() != null)
                {
                    anotherAddressType.setText(itemView.getContext().getString(R.string.road_addr));
                    anotherAddressName.setText(addressDocuments.getAddressResponseRoadAddress().getAddressName());
                } else if (addressDocuments.getAddressResponseAddress() != null)
                {
                    anotherAddressType.setText(itemView.getContext().getString(R.string.region_addr));
                    anotherAddressName.setText(addressDocuments.getAddressResponseAddress().getAddressName());
                }

                placeLayout.setVisibility(View.GONE);
                addressLayout.setVisibility(View.VISIBLE);
                placeDistanceTextView.setVisibility(View.GONE);
            } else if (data instanceof CoordToAddressDocuments)
            {
                coordToAddressDocuments = (CoordToAddressDocuments) data;

                addressName.setText(coordToAddressDocuments.getCoordToAddressAddress().getAddressName());
                if (coordToAddressDocuments.getCoordToAddressRoadAddress() != null)
                {
                    anotherAddressType.setText(itemView.getContext().getString(R.string.road_addr));
                    anotherAddressName.setText(coordToAddressDocuments.getCoordToAddressRoadAddress().getAddressName());
                } else if (coordToAddressDocuments.getCoordToAddressAddress() != null)
                {
                    anotherAddressType.setText(itemView.getContext().getString(R.string.region_addr));
                    anotherAddressName.setText(coordToAddressDocuments.getCoordToAddressAddress().getAddressName());
                }

                placeLayout.setVisibility(View.GONE);
                addressLayout.setVisibility(View.VISIBLE);
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

            final ViewHolderData viewHolderData = new ViewHolderData(data);
            cardView.setTag(viewHolderData);
        }

    }

    static class ViewHolderData
    {
        KakaoLocalDocument kakaoLocalDocument;

        public ViewHolderData(KakaoLocalDocument kakaoLocalDocument)
        {
            this.kakaoLocalDocument = kakaoLocalDocument;
        }
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            ViewHolderData viewHolderData = (ViewHolderData) view.getTag();
            onClickedBottomSheetListener.onClickedPlaceBottomSheet(viewHolderData.kakaoLocalDocument);
        }
    };
}
