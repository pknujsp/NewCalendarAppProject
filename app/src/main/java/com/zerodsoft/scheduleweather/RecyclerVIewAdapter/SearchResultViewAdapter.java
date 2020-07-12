package com.zerodsoft.scheduleweather.RecyclerVIewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.Activity.AddScheduleActivity;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Retrofit.DownloadData;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceCategoryResponse.PlaceCategoryDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceKeywordResponse.PlaceKeywordDocuments;

import java.util.List;

public class SearchResultViewAdapter extends RecyclerView.Adapter<SearchResultViewAdapter.SearchResultViewHolder>
{
    private List<AddressResponseDocuments> addressList = null;
    private List<PlaceKeywordDocuments> placeKeywordList = null;
    private List<PlaceCategoryDocuments> placeCategoryList = null;
    private int type;
    private Context context;

    public SearchResultViewAdapter(Context context)
    {
        this.context = context;
    }

    public void setAddressList(List<AddressResponseDocuments> addressList)
    {
        this.addressList = addressList;
        type = DownloadData.ADDRESS;
    }

    public void setPlaceKeywordList(List<PlaceKeywordDocuments> placeKeywordList)
    {
        this.placeKeywordList = placeKeywordList;
        type = DownloadData.PLACE_KEYWORD;
    }

    public void setPlaceCategoryList(List<PlaceCategoryDocuments> placeCategoryList)
    {
        this.placeCategoryList = placeCategoryList;
        type = DownloadData.PLACE_CATEGORY;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.search_address_recycler_view_item, parent, false);

        return new SearchResultViewHolder(view, type);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position)
    {
        switch (type)
        {
            case DownloadData.ADDRESS:
                holder.onBindAddress(addressList.get(position));
                break;

            case DownloadData.PLACE_KEYWORD:
                holder.onBindPlaceKeyword(placeKeywordList.get(position));
                break;

            case DownloadData.PLACE_CATEGORY:
                holder.onBindPlaceCategory(placeCategoryList.get(position));
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

        SearchResultViewHolder(View itemView, int type)
        {
            super(itemView);

            switch (type)
            {
                case DownloadData.ADDRESS:
                    addressLayout = (LinearLayout) itemView.findViewById(R.id.address_linearlayout);
                    addressLayout.setVisibility(View.VISIBLE);
                    addressTextView = (TextView) itemView.findViewById(R.id.search_address_name_textview);
                    anotherTypeTextView = (TextView) itemView.findViewById(R.id.another_address_type_textview);
                    anotherTypeAddressTextView = (TextView) itemView.findViewById(R.id.search_another_type_address_textview);
                    break;

                case DownloadData.PLACE_KEYWORD:
                    placeLayout = (LinearLayout) itemView.findViewById(R.id.place_linearlayout);
                    placeLayout.setVisibility(View.VISIBLE);
                    placeNameTextView = (TextView) itemView.findViewById(R.id.search_place_name_textview);
                    placeCategoryTextView = (TextView) itemView.findViewById(R.id.search_place_category_name_textview);
                    placeAddressTextView = (TextView) itemView.findViewById(R.id.search_place_addess_name_textview);
                    break;
            }
        }

        public void onBindAddress(AddressResponseDocuments data)
        {
            addressTextView.setText(data.getAddressName());

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

        public void onBindPlaceKeyword(PlaceKeywordDocuments data)
        {
            placeNameTextView.setText(data.getPlaceName());
            placeCategoryTextView.setText(data.getCategoryName());
            placeAddressTextView.setText(data.getRoadAddressName());
        }

        public void onBindPlaceCategory(PlaceCategoryDocuments data)
        {
            placeNameTextView.setText(data.getPlaceName());
            placeCategoryTextView.setText(data.getCategoryName());
            placeAddressTextView.setText(data.getRoadAddressName());
        }
    }
}
