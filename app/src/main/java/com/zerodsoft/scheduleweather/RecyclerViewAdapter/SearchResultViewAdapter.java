package com.zerodsoft.scheduleweather.RecyclerViewAdapter;

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

import com.zerodsoft.scheduleweather.Activity.MapActivity.MapActivity;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Retrofit.DownloadData;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponse.AddressResponseMeta;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceCategoryResponse.PlaceCategoryDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceCategoryResponse.PlaceCategoryMeta;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceKeywordResponse.PlaceKeywordDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceKeywordResponse.PlaceKeywordMeta;
import com.zerodsoft.scheduleweather.Room.DTO.AddressDTO;
import com.zerodsoft.scheduleweather.Room.DTO.PlaceDTO;
import com.zerodsoft.scheduleweather.Utility.LonLat;
import com.zerodsoft.scheduleweather.Utility.LonLatConverter;

import java.util.List;

public class SearchResultViewAdapter extends RecyclerView.Adapter<SearchResultViewAdapter.SearchResultViewHolder>
{
    private List<AddressResponseDocuments> addressList = null;
    private List<PlaceKeywordDocuments> placeKeywordList = null;
    private List<PlaceCategoryDocuments> placeCategoryList = null;

    private int type;
    private Activity context;
    private long downloadedTime;
    private int currentPage;

    private int totalCount;
    private int pageableCount;
    private boolean isEnd;

    private OnItemClickedListener onItemClickedListener;

    public interface OnItemClickedListener
    {
        void onItemClicked(int position, int type);
    }

    public SearchResultViewAdapter(Activity activity)
    {
        this.context = activity;
        this.onItemClickedListener = (MapActivity) activity;
        this.currentPage = 1;
    }

    public boolean isEnd()
    {
        return isEnd;
    }

    public void setAddressList(List<AddressResponseDocuments> addressList, AddressResponseMeta meta)
    {
        this.addressList = addressList;
        type = DownloadData.ADDRESS;
        totalCount = meta.getTotalCount();
        pageableCount = meta.getPageableCount();
        isEnd = meta.isEnd();
    }

    public void setPlaceKeywordList(List<PlaceKeywordDocuments> placeKeywordList, PlaceKeywordMeta meta)
    {
        this.placeKeywordList = placeKeywordList;
        type = DownloadData.PLACE_KEYWORD;
        totalCount = meta.getTotalCount();
        pageableCount = meta.getPageableCount();
        isEnd = meta.isEnd();
    }

    public void setPlaceCategoryList(List<PlaceCategoryDocuments> placeCategoryList, PlaceCategoryMeta meta)
    {
        this.placeCategoryList = placeCategoryList;
        type = DownloadData.PLACE_CATEGORY;
        totalCount = meta.getTotalCount();
        pageableCount = meta.getPageableCount();
        isEnd = meta.isEnd();
    }

    public void addAddressData(List<AddressResponseDocuments> addressList, AddressResponseMeta meta)
    {
        isEnd = meta.isEnd();
        for (int index = 0; index < addressList.size(); index++)
        {
            this.addressList.add(addressList.get(index));
        }
    }

    public void addPlaceKeywordData(List<PlaceKeywordDocuments> placeKeywordList, PlaceKeywordMeta meta)
    {
        isEnd = meta.isEnd();
        for (int index = 0; index < placeKeywordList.size(); index++)
        {
            this.placeKeywordList.add(placeKeywordList.get(index));
        }
    }

    public void addPlaceCategoryData(List<PlaceCategoryDocuments> placeCategoryList, PlaceCategoryMeta meta)
    {
        isEnd = meta.isEnd();
        for (int index = 0; index < placeCategoryList.size(); index++)
        {
            this.placeCategoryList.add(placeCategoryList.get(index));
        }
    }

    public int getType()
    {
        return type;
    }

    public void setCurrentPage(int currentPage)
    {
        this.currentPage = currentPage;
    }

    public int getCurrentPage()
    {
        return currentPage;
    }

    public void setDownloadedTime(long downloadedTime)
    {
        this.downloadedTime = downloadedTime;
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
                        onItemClickedListener.onItemClicked(position, type);
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
                        onItemClickedListener.onItemClicked(position, type);
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
                        onItemClickedListener.onItemClicked(position, type);
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
                    placeDistanceTextView = (TextView) itemView.findViewById(R.id.search_place_distance);
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
                    LonLat lonLat = LonLatConverter.convertLonLat(Double.valueOf(longitude), Double.valueOf(latitude));

                    Bundle bundle = new Bundle();

                    bundle.putInt("type", type);
                    AddressDTO addressDTO = new AddressDTO();
                    addressDTO.setAddressName(addressName);
                    addressDTO.setLongitude(longitude);
                    addressDTO.setLatitude(latitude);
                    addressDTO.setWeatherX(Integer.toString(lonLat.getX()));
                    addressDTO.setWeatherY(Integer.toString(lonLat.getY()));

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
                    LonLat lonLat = LonLatConverter.convertLonLat(Double.valueOf(longitude), Double.valueOf(latitude));
                    Bundle bundle = new Bundle();

                    bundle.putInt("type", type);
                    PlaceDTO placeDTO = new PlaceDTO();
                    placeDTO.setPlaceId(placeId);
                    placeDTO.setPlaceName(placeName);
                    placeDTO.setLongitude(longitude);
                    placeDTO.setLatitude(latitude);
                    placeDTO.setWeatherX(Integer.toString(lonLat.getX()));
                    placeDTO.setWeatherY(Integer.toString(lonLat.getY()));

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
            placeDistanceTextView.setText(data.getDistance() + "M");

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
            placeDistanceTextView.setText(data.getDistance() + "M");

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