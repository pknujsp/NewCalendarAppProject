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
    private Date downloadedDate;

    private OnItemSelectedListener onItemSelectedListener;

    public interface OnItemSelectedListener
    {
        void onItemSelected(int position, int dataType);
    }

    public SearchResultViewAdapter(Activity activity)
    {
        this.context = activity.getApplicationContext();
        this.onItemSelectedListener = (OnItemSelectedListener) activity;
        this.currentPage = 1;
    }

    public boolean isEnd()
    {
        return isEnd;
    }

    public void setAddressList(LocationSearchResult locationSearchResult)
    {
        addressList = locationSearchResult.getAddressResponse().getAddressResponseDocumentsList();
        dataType = MapController.TYPE_ADDRESS;
        totalCount = locationSearchResult.getAddressResponse().getAddressResponseMeta().getTotalCount();
        pageableCount = locationSearchResult.getAddressResponse().getAddressResponseMeta().getPageableCount();
        isEnd = locationSearchResult.getAddressResponse().getAddressResponseMeta().isEnd();
        currentPage = 1;
    }

    public void setPlaceKeywordList(LocationSearchResult locationSearchResult)
    {
        placeKeywordList = locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordDocuments();
        dataType = MapController.TYPE_PLACE_KEYWORD;
        totalCount = locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordMeta().getTotalCount();
        pageableCount = locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordMeta().getPageableCount();
        isEnd = locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordMeta().isEnd();
        currentPage = 1;
    }

    public void setPlaceCategoryList(LocationSearchResult locationSearchResult)
    {
        placeCategoryList = locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments();
        dataType = MapController.TYPE_PLACE_CATEGORY;
        totalCount = locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryMeta().getTotalCount();
        pageableCount = locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryMeta().getPageableCount();
        isEnd = locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryMeta().isEnd();
        currentPage = 1;
    }

    public void addAddressData(LocationSearchResult locationSearchResult)
    {
        currentPage++;
        isEnd = locationSearchResult.getAddressResponse().getAddressResponseMeta().isEnd();
        for (int index = 0; index < locationSearchResult.getAddressResponse().getAddressResponseDocumentsList().size(); index++)
        {
            addressList.add(locationSearchResult.getAddressResponse().getAddressResponseDocumentsList().get(index));
        }
    }

    public void addPlaceKeywordData(LocationSearchResult locationSearchResult)
    {
        currentPage++;
        isEnd = locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordMeta().isEnd();
        for (int index = 0; index < locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordDocuments().size(); index++)
        {
            placeKeywordList.add(locationSearchResult.getPlaceKeywordResponse().getPlaceKeywordDocuments().get(index));
        }
    }

    public void addPlaceCategoryData(LocationSearchResult locationSearchResult)
    {
        currentPage++;
        isEnd = locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryMeta().isEnd();
        for (int index = 0; index < locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().size(); index++)
        {
            placeCategoryList.add(locationSearchResult.getPlaceCategoryResponse().getPlaceCategoryDocuments().get(index));
        }
    }

    public int getDataType()
    {
        return dataType;
    }

    public int getCurrentPage()
    {
        return currentPage;
    }

    public void setDownloadedDate(Date downloadedDate)
    {
        this.downloadedDate = downloadedDate;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.search_recycler_view_item, parent, false);
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
                    setChoiceButtonListener();
                    break;

                case MapController.TYPE_PLACE_KEYWORD:
                case MapController.TYPE_PLACE_CATEGORY:
                    setChoicePlaceButtonListener();
                    break;
            }
        }

        private void setChoiceButtonListener()
        {
            choiceAddressButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    LonLat lonLat = LonLatConverter.convertLonLat(Double.valueOf(longitude), Double.valueOf(latitude));

                    Bundle bundle = new Bundle();

                    bundle.putInt("type", dataType);
                    AddressDTO addressDTO = new AddressDTO();
                    addressDTO.setAddressName(addressName);
                    addressDTO.setLongitude(longitude);
                    addressDTO.setLatitude(latitude);
                    addressDTO.setWeatherX(Integer.toString(lonLat.getX()));
                    addressDTO.setWeatherY(Integer.toString(lonLat.getY()));

                    bundle.putParcelable("addressDTO", addressDTO);
                    ((MapActivity) context).onChoicedLocation(bundle);
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
                    LonLat lonLat = LonLatConverter.convertLonLat(Double.valueOf(longitude), Double.valueOf(latitude));
                    Bundle bundle = new Bundle();

                    bundle.putInt("type", dataType);
                    PlaceDTO placeDTO = new PlaceDTO();
                    placeDTO.setPlaceId(placeId);
                    placeDTO.setPlaceName(placeName);
                    placeDTO.setLongitude(longitude);
                    placeDTO.setLatitude(latitude);
                    placeDTO.setWeatherX(Integer.toString(lonLat.getX()));
                    placeDTO.setWeatherY(Integer.toString(lonLat.getY()));

                    bundle.putParcelable("placeDTO", placeDTO);
                    ((MapActivity) context).onChoicedLocation(bundle);
                }
            });
        }

        public void onBindPlaceKeyword(int position)
        {
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
