package com.zerodsoft.scheduleweather.RecyclerVIewAdapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.Activity.MapActivity.MapActivity;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Retrofit.DownloadData;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceCategoryResponse.PlaceCategoryDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceKeywordResponse.PlaceKeywordDocuments;
import com.zerodsoft.scheduleweather.Room.DTO.AddressDTO;
import com.zerodsoft.scheduleweather.Room.DTO.PlaceDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchResultViewAdapter extends RecyclerView.Adapter<SearchResultViewAdapter.SearchResultViewHolder>
{
    private List<AddressResponseDocuments> addressList = null;
    private List<PlaceKeywordDocuments> placeKeywordList = null;
    private List<PlaceCategoryDocuments> placeCategoryList = null;
    private int type;
    private Activity context;
    private long downloadedTime;

    private OnItemClickedListener onItemClickedListener;

    public interface OnItemClickedListener
    {
        void onItemClicked(Bundle bundle);
    }

    public SearchResultViewAdapter(Activity activity)
    {
        this.context = activity;
        this.onItemClickedListener = (MapActivity) activity;
    }

    public void setAddressList(List<AddressResponseDocuments> addressList)
    {
        this.addressList = addressList;
        type = DownloadData.ADDRESS;
        downloadedTime = System.currentTimeMillis();
    }

    public void setPlaceKeywordList(List<PlaceKeywordDocuments> placeKeywordList)
    {
        this.placeKeywordList = placeKeywordList;
        type = DownloadData.PLACE_KEYWORD;
        downloadedTime = System.currentTimeMillis();
    }

    public void setPlaceCategoryList(List<PlaceCategoryDocuments> placeCategoryList)
    {
        this.placeCategoryList = placeCategoryList;
        type = DownloadData.PLACE_CATEGORY;
        downloadedTime = System.currentTimeMillis();
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.search_recycler_view_item, parent, false);

        return new SearchResultViewHolder(view, type);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position)
    {
        switch (type)
        {
            case DownloadData.ADDRESS:
                holder.onBindAddress(addressList.get(position));
                holder.getAddressLayout().setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Bundle bundle = new Bundle();

                        List<AddressResponseDocuments> copiedList = new ArrayList<>(addressList);
                        Collections.copy(copiedList, addressList);

                        bundle.putParcelableArrayList("itemsInfo", (ArrayList<? extends Parcelable>) copiedList);
                        bundle.putInt("type", DownloadData.ADDRESS);
                        bundle.putInt("position", position);
                        bundle.putLong("downloadedTime", downloadedTime);

                        onItemClickedListener.onItemClicked(bundle);
                    }
                });
                break;

            case DownloadData.PLACE_KEYWORD:
                holder.onBindPlaceKeyword(placeKeywordList.get(position));
                holder.getPlaceLayout().setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Bundle bundle = new Bundle();

                        List<PlaceKeywordDocuments> copiedList = new ArrayList<>(placeKeywordList);
                        Collections.copy(copiedList, placeKeywordList);

                        bundle.putParcelableArrayList("itemsInfo", (ArrayList<? extends Parcelable>) copiedList);
                        bundle.putInt("type", DownloadData.PLACE_KEYWORD);
                        bundle.putInt("position", position);
                        bundle.putLong("downloadedTime", downloadedTime);

                        onItemClickedListener.onItemClicked(bundle);
                    }
                });
                break;

            case DownloadData.PLACE_CATEGORY:
                holder.onBindPlaceCategory(placeCategoryList.get(position));
                holder.getPlaceLayout().setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Bundle bundle = new Bundle();

                        List<PlaceCategoryDocuments> copiedList = new ArrayList<>(placeCategoryList);
                        Collections.copy(copiedList, placeCategoryList);

                        bundle.putParcelableArrayList("itemsInfo", (ArrayList<? extends Parcelable>) copiedList);
                        bundle.putInt("type", DownloadData.PLACE_CATEGORY);
                        bundle.putInt("position", position);
                        bundle.putLong("downloadedTime", downloadedTime);

                        onItemClickedListener.onItemClicked(bundle);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount()
    {
        if (addressList != null)
        {
            return addressList.size();
        } else if (placeCategoryList != null)
        {
            return placeCategoryList.size();
        } else if (placeKeywordList != null)
        {
            return placeKeywordList.size();
        } else
        {
            return 0;
        }
    }

    class SearchResultViewHolder extends RecyclerView.ViewHolder
    {
        private LinearLayout addressLayout;
        private LinearLayout placeLayout;

        private TextView addressTextView;
        private TextView anotherTypeTextView;
        private TextView anotherTypeAddressTextView;
        private TextView placeNameTextView;
        private TextView placeCategoryTextView;
        private TextView placeAddressTextView;

        private Button choiceAddressButton;
        private Button choicePlaceButton;

        private String addressName = null;
        private String longitude = null;
        private String latitude = null;

        private String placeName = null;
        private String placeId = null;

        SearchResultViewHolder(View itemView, int type)
        {
            super(itemView);

            switch (type)
            {
                case DownloadData.ADDRESS:
                    addressLayout = (LinearLayout) itemView.findViewById(R.id.address_linearlayout);
                    addressLayout.setVisibility(View.VISIBLE);
                    placeLayout = (LinearLayout) itemView.findViewById(R.id.place_linearlayout);
                    placeLayout.setVisibility(View.GONE);

                    addressTextView = (TextView) itemView.findViewById(R.id.search_address_name_textview);
                    anotherTypeTextView = (TextView) itemView.findViewById(R.id.another_address_type_textview);
                    anotherTypeAddressTextView = (TextView) itemView.findViewById(R.id.search_another_type_address_textview);
                    choiceAddressButton = (Button) itemView.findViewById(R.id.choice_address_button);
                    choicePlaceButton = (Button) itemView.findViewById(R.id.choice_place_button);
                    break;

                default:
                    addressLayout = (LinearLayout) itemView.findViewById(R.id.address_linearlayout);
                    addressLayout.setVisibility(View.GONE);
                    placeLayout = (LinearLayout) itemView.findViewById(R.id.place_linearlayout);
                    placeLayout.setVisibility(View.VISIBLE);

                    placeNameTextView = (TextView) itemView.findViewById(R.id.search_place_name_textview);
                    placeCategoryTextView = (TextView) itemView.findViewById(R.id.search_place_category_name_textview);
                    placeAddressTextView = (TextView) itemView.findViewById(R.id.search_place_addess_name_textview);
                    choicePlaceButton = (Button) itemView.findViewById(R.id.choice_place_button);
                    break;
            }
            setOnClickListenerButton();
        }

        public void onBindAddress(AddressResponseDocuments data)
        {
            addressTextView.setText(data.getAddressName());

            addressName = data.getAddressName();
            latitude = Double.toString(data.getY());
            longitude = Double.toString(data.getX());

            switch (data.getAddressType())
            {
                case AddressResponseDocuments.REGION:
                    //지명
                    anotherTypeTextView.setText(context.getString(R.string.region));
                    anotherTypeAddressTextView.setText(data.getAddressResponseAddress().getAddressName());
                    break;
                case AddressResponseDocuments.REGION_ADDR:
                    //지명 주소
                    anotherTypeTextView.setText(context.getString(R.string.road_addr));
                    anotherTypeAddressTextView.setText(data.getAddressResponseRoadAddress().getAddressName());
                    break;
                case AddressResponseDocuments.ROAD:
                    //도로명
                    anotherTypeTextView.setText(context.getString(R.string.road));
                    anotherTypeAddressTextView.setText(data.getAddressResponseRoadAddress().getAddressName());
                    break;
                case AddressResponseDocuments.ROAD_ADDR:
                    //도로명 주소
                    anotherTypeTextView.setText(context.getString(R.string.region_addr));
                    anotherTypeAddressTextView.setText(data.getAddressResponseAddress().getAddressName());
                    break;
            }
        }

        private void setOnClickListenerButton()
        {
            switch (type)
            {
                case DownloadData.ADDRESS:
                    setChoiceAddressButtonListener();
                    break;

                case DownloadData.PLACE_KEYWORD:
                case DownloadData.PLACE_CATEGORY:
                    setChoicePlaceButtonListener();
                    break;

                case DownloadData.ADDRESS_AND_PLACE_KEYWORD:
                    setChoicePlaceButtonListener();
                    setChoiceAddressButtonListener();
                    break;
            }
        }

        private void setChoiceAddressButtonListener()
        {
            choiceAddressButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Bundle bundle = new Bundle();

                    bundle.putInt("type", type);
                    AddressDTO addressDTO = new AddressDTO();
                    addressDTO.setAddressName(addressName);
                    addressDTO.setLongitude(longitude);
                    addressDTO.setLatitude(latitude);

                    bundle.putParcelable("addressDTO", addressDTO);
                    ((MapActivity) context).onChoicedLoc(bundle);
                }
            });
        }

        private void setChoicePlaceButtonListener()
        {
            choicePlaceButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Bundle bundle = new Bundle();

                    bundle.putInt("type", type);
                    PlaceDTO placeDTO = new PlaceDTO();
                    placeDTO.setPlaceId(placeId);
                    placeDTO.setPlaceName(placeName);
                    placeDTO.setLongitude(longitude);
                    placeDTO.setLatitude(latitude);

                    bundle.putParcelable("placeDTO", placeDTO);
                    ((MapActivity) context).onChoicedLoc(bundle);
                }
            });
        }

        public void onBindPlaceKeyword(PlaceKeywordDocuments data)
        {
            placeNameTextView.setText(data.getPlaceName());
            placeCategoryTextView.setText(data.getCategoryName());
            placeAddressTextView.setText(data.getAddressName());

            placeName = data.getPlaceName();
            placeId = data.getId();
            latitude = Double.toString(data.getY());
            longitude = Double.toString(data.getX());
        }

        public void onBindPlaceCategory(PlaceCategoryDocuments data)
        {
            placeNameTextView.setText(data.getPlaceName());
            placeCategoryTextView.setText(data.getCategoryName());
            placeAddressTextView.setText(data.getAddressName());

            placeName = data.getPlaceName();
            placeId = data.getId();
            latitude = data.getY();
            longitude = data.getX();
        }

        public LinearLayout getAddressLayout()
        {
            return addressLayout;
        }

        public LinearLayout getPlaceLayout()
        {
            return placeLayout;
        }
    }
}
