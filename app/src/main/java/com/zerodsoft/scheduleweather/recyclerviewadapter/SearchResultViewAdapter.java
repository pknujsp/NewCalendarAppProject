package com.zerodsoft.scheduleweather.recyclerviewadapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.MapController;
import com.zerodsoft.scheduleweather.activity.mapactivity.MapActivity;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.LocationSearchResult;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placecategoryresponse.PlaceCategoryDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placekeywordresponse.PlaceKeywordDocuments;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.utility.LonLat;
import com.zerodsoft.scheduleweather.utility.LonLatConverter;

import java.util.Date;
import java.util.List;

public class SearchResultViewAdapter extends RecyclerView.Adapter<SearchResultViewAdapter.SearchResultViewHolder>
{
    private List<AddressResponseDocuments> addressList = null;
    private List<PlaceKeywordDocuments> placeKeywordList = null;
    private List<PlaceCategoryDocuments> placeCategoryList = null;

    private int dataType;
    private Context context;

    private int currentPage;
    private boolean isEnd;
    private int totalCount;
    private int pageableCount;

    private OnItemSelectedListener onItemSelectedListener;
    private MapController.OnChoicedListener onChoicedListener;

    public interface OnItemSelectedListener
    {
        void onItemSelected(int position, int dataType);
    }

    public SearchResultViewAdapter(Activity activity)
    {
        this.context = activity.getApplicationContext();
        this.onItemSelectedListener = (OnItemSelectedListener) activity;
        this.onChoicedListener = (MapController.OnChoicedListener) activity;
        this.currentPage = 1;
    }

    public boolean isEnd()
    {
        return isEnd;
    }

    public void setAddressList()
    {
        addressList = MapActivity.searchResult.getAddressResponse().getAddressResponseDocumentsList();
        dataType = MapController.TYPE_ADDRESS;
        totalCount = MapActivity.searchResult.getAddressResponse().getAddressResponseMeta().getTotalCount();
        pageableCount = MapActivity.searchResult.getAddressResponse().getAddressResponseMeta().getPageableCount();
        isEnd = MapActivity.searchResult.getAddressResponse().getAddressResponseMeta().isEnd();
        currentPage = 1;
    }

    public void setPlaceKeywordList()
    {
        placeKeywordList = MapActivity.searchResult.getPlaceKeywordResponse().getPlaceKeywordDocuments();
        dataType = MapController.TYPE_PLACE_KEYWORD;
        totalCount = MapActivity.searchResult.getPlaceKeywordResponse().getPlaceKeywordMeta().getTotalCount();
        pageableCount = MapActivity.searchResult.getPlaceKeywordResponse().getPlaceKeywordMeta().getPageableCount();
        isEnd = MapActivity.searchResult.getPlaceKeywordResponse().getPlaceKeywordMeta().isEnd();
        currentPage = 1;
    }

    public void setPlaceCategoryList()
    {
        placeCategoryList = MapActivity.searchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments();
        dataType = MapController.TYPE_PLACE_CATEGORY;
        totalCount = MapActivity.searchResult.getPlaceCategoryResponse().getPlaceCategoryMeta().getTotalCount();
        pageableCount = MapActivity.searchResult.getPlaceCategoryResponse().getPlaceCategoryMeta().getPageableCount();
        isEnd = MapActivity.searchResult.getPlaceCategoryResponse().getPlaceCategoryMeta().isEnd();
        currentPage = 1;
    }

    public void addAddressData()
    {
        currentPage++;
        isEnd = MapActivity.searchResult.getAddressResponse().getAddressResponseMeta().isEnd();
        addressList = MapActivity.searchResult.getAddressResponse().getAddressResponseDocumentsList();
    }

    public void addPlaceKeywordData()
    {
        currentPage++;
        isEnd = MapActivity.searchResult.getPlaceKeywordResponse().getPlaceKeywordMeta().isEnd();
        placeKeywordList = MapActivity.searchResult.getPlaceKeywordResponse().getPlaceKeywordDocuments();
    }

    public void addPlaceCategoryData()
    {
        currentPage++;
        isEnd = MapActivity.searchResult.getPlaceCategoryResponse().getPlaceCategoryMeta().isEnd();
        placeCategoryList = MapActivity.searchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments();
    }

    public int getDataType()
    {
        return dataType;
    }

    public int getCurrentPage()
    {
        return currentPage;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_recycler_view_item, parent, false);
        return new SearchResultViewHolder(view, dataType);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position)
    {
        switch (dataType)
        {
            case MapController.TYPE_ADDRESS:
                holder.onBindAddress(position);
                holder.getAddressLayout().setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        onItemSelectedListener.onItemSelected(position, dataType);
                    }
                });
                break;

            case MapController.TYPE_PLACE_KEYWORD:
                holder.onBindPlaceKeyword(position);
                holder.getPlaceLayout().setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        onItemSelectedListener.onItemSelected(position, dataType);
                    }
                });
                break;

            case MapController.TYPE_PLACE_CATEGORY:
                holder.onBindPlaceCategory(position);
                holder.getPlaceLayout().setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        onItemSelectedListener.onItemSelected(position, dataType);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount()
    {
        int size = 0;

        switch (dataType)
        {
            case MapController.TYPE_ADDRESS:
                size = addressList.size();
                break;
            case MapController.TYPE_PLACE_CATEGORY:
                size = placeCategoryList.size();
                break;
            case MapController.TYPE_PLACE_KEYWORD:
                size = placeKeywordList.size();
                break;
        }
        return size;
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
        private TextView placeDistanceTextView;

        private TextView addressListNumTextView;
        private TextView placeListNumTextView;

        private Button choiceAddressButton;
        private Button choicePlaceButton;

        private String addressName = null;
        private String longitude = null;
        private String latitude = null;

        private String placeName = null;
        private String placeId = null;

        private int position;

        SearchResultViewHolder(View itemView, int type)
        {
            super(itemView);

            switch (type)
            {
                case MapController.TYPE_ADDRESS:
                    addressLayout = (LinearLayout) itemView.findViewById(R.id.address_linearlayout);
                    addressLayout.setVisibility(View.VISIBLE);
                    placeLayout = (LinearLayout) itemView.findViewById(R.id.place_linearlayout);
                    placeLayout.setVisibility(View.GONE);

                    addressTextView = (TextView) itemView.findViewById(R.id.search_address_name_textview);
                    anotherTypeTextView = (TextView) itemView.findViewById(R.id.another_address_type_textview);
                    anotherTypeAddressTextView = (TextView) itemView.findViewById(R.id.search_another_type_address_textview);
                    choiceAddressButton = (Button) itemView.findViewById(R.id.choice_location_button);
                    choicePlaceButton = (Button) itemView.findViewById(R.id.choice_place_button);

                    addressListNumTextView = (TextView) itemView.findViewById(R.id.search_address_list_number_textview);
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
                    placeDistanceTextView = (TextView) itemView.findViewById(R.id.search_place_distance);
                    placeListNumTextView = (TextView) itemView.findViewById(R.id.search_place_list_number_textview);
                    break;
            }
            setOnClickListenerButton();
        }

        public void onBindAddress(int position)
        {
            this.position = position;
            addressListNumTextView.setText(Integer.toString(position + 1));
            addressTextView.setText(addressList.get(position).getAddressName());

            addressName = addressList.get(position).getAddressName();
            latitude = Double.toString(addressList.get(position).getY());
            longitude = Double.toString(addressList.get(position).getX());

            switch (addressList.get(position).getAddressType())
            {
                case AddressResponseDocuments.REGION:
                    //지명
                    anotherTypeTextView.setText(context.getString(R.string.region));
                    anotherTypeAddressTextView.setText(addressList.get(position).getAddressResponseAddress().getAddressName());
                    break;
                case AddressResponseDocuments.REGION_ADDR:
                    //지명 주소
                    anotherTypeTextView.setText(context.getString(R.string.road_addr));
                    anotherTypeAddressTextView.setText(addressList.get(position).getAddressResponseRoadAddress().getAddressName());
                    break;
                case AddressResponseDocuments.ROAD:
                    //도로명
                    anotherTypeTextView.setText(context.getString(R.string.road));
                    anotherTypeAddressTextView.setText(addressList.get(position).getAddressResponseRoadAddress().getAddressName());
                    break;
                case AddressResponseDocuments.ROAD_ADDR:
                    //도로명 주소
                    anotherTypeTextView.setText(context.getString(R.string.region_addr));
                    anotherTypeAddressTextView.setText(addressList.get(position).getAddressResponseAddress().getAddressName());
                    break;
            }
        }

        private void setOnClickListenerButton()
        {
            switch (dataType)
            {
                case MapController.TYPE_ADDRESS:
                    choiceAddressButton.setOnClickListener(choiceButtonListener);
                    break;

                case MapController.TYPE_PLACE_KEYWORD:
                case MapController.TYPE_PLACE_CATEGORY:
                    choicePlaceButton.setOnClickListener(choiceButtonListener);
                    break;
            }
        }


        public void onBindPlaceKeyword(int position)
        {
            this.position = position;
            placeListNumTextView.setText(Integer.toString(position + 1));
            placeNameTextView.setText(placeKeywordList.get(position).getPlaceName());
            placeCategoryTextView.setText(placeKeywordList.get(position).getCategoryName());
            placeAddressTextView.setText(placeKeywordList.get(position).getAddressName());
            placeDistanceTextView.setText(placeKeywordList.get(position).getDistance() + "M");

            placeName = placeKeywordList.get(position).getPlaceName();
            placeId = placeKeywordList.get(position).getId();
            latitude = Double.toString(placeKeywordList.get(position).getY());
            longitude = Double.toString(placeKeywordList.get(position).getX());
        }

        public void onBindPlaceCategory(int position)
        {
            this.position = position;
            placeListNumTextView.setText(Integer.toString(position + 1));
            placeNameTextView.setText(placeCategoryList.get(position).getPlaceName());
            placeCategoryTextView.setText(placeCategoryList.get(position).getCategoryName());
            placeAddressTextView.setText(placeCategoryList.get(position).getAddressName());
            placeDistanceTextView.setText(placeCategoryList.get(position).getDistance() + "M");

            placeName = placeCategoryList.get(position).getPlaceName();
            placeId = placeCategoryList.get(position).getId();
            latitude = placeCategoryList.get(position).getY();
            longitude = placeCategoryList.get(position).getX();
        }

        private final View.OnClickListener choiceButtonListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Bundle bundle = new Bundle();
                bundle.putInt("dataType", dataType);
                LonLat lonLat = null;
                double lon, lat;

                switch (dataType)
                {
                    case MapController.TYPE_ADDRESS:
                        AddressResponseDocuments addressDocument = addressList.get(position);
                        lon = addressDocument.getX();
                        lat = addressDocument.getY();
                        lonLat = LonLatConverter.convertLonLat(lon, lat);

                        AddressDTO addressDTO = new AddressDTO();
                        addressDTO.setAddressName(addressDocument.getAddressName());
                        addressDTO.setLongitude(Double.toString(lon));
                        addressDTO.setLatitude(Double.toString(lat));
                        addressDTO.setWeatherX(Integer.toString(lonLat.getX()));
                        addressDTO.setWeatherY(Integer.toString(lonLat.getY()));

                        try
                        {
                            bundle.putParcelable("address", (AddressDTO) addressDTO.clone());
                        } catch (CloneNotSupportedException e)
                        {
                            e.printStackTrace();
                        }
                        break;

                    case MapController.TYPE_PLACE_KEYWORD:
                        PlaceKeywordDocuments placeKeywordDocument = placeKeywordList.get(position);
                        lon = placeKeywordDocument.getX();
                        lat = placeKeywordDocument.getY();
                        lonLat = LonLatConverter.convertLonLat(lon, lat);

                        PlaceDTO placeDTOKeyword = new PlaceDTO();
                        placeDTOKeyword.setPlaceId(placeKeywordDocument.getId());
                        placeDTOKeyword.setPlaceName(placeKeywordDocument.getPlaceName());
                        placeDTOKeyword.setLongitude(Double.toString(lon));
                        placeDTOKeyword.setLatitude(Double.toString(lat));
                        placeDTOKeyword.setWeatherX(Integer.toString(lonLat.getX()));
                        placeDTOKeyword.setWeatherY(Integer.toString(lonLat.getY()));

                        try
                        {
                            bundle.putParcelable("place", (PlaceDTO) placeDTOKeyword.clone());
                        } catch (CloneNotSupportedException e)
                        {
                            e.printStackTrace();
                        }
                        break;

                    case MapController.TYPE_PLACE_CATEGORY:
                        PlaceCategoryDocuments placeCategoryDocument = placeCategoryList.get(position);
                        lon = Double.valueOf(placeCategoryDocument.getX());
                        lat = Double.valueOf(placeCategoryDocument.getY());
                        lonLat = LonLatConverter.convertLonLat(lon, lat);

                        PlaceDTO placeDTOCategory = new PlaceDTO();
                        placeDTOCategory.setPlaceId(placeCategoryDocument.getId());
                        placeDTOCategory.setPlaceName(placeCategoryDocument.getPlaceName());
                        placeDTOCategory.setLongitude(placeCategoryDocument.getX());
                        placeDTOCategory.setLatitude(placeCategoryDocument.getY());
                        placeDTOCategory.setWeatherX(Integer.toString(lonLat.getX()));
                        placeDTOCategory.setWeatherY(Integer.toString(lonLat.getY()));

                        try
                        {
                            bundle.putParcelable("place", (PlaceDTO) placeDTOCategory.clone());
                        } catch (CloneNotSupportedException e)
                        {
                            e.printStackTrace();
                        }
                        break;
                }
                onChoicedListener.onChoicedLocation(bundle);
            }
        };

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
