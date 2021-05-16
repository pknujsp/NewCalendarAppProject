package com.zerodsoft.scheduleweather.navermap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.CardviewPlacesItemBinding;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnClickedBottomSheetListener;
import com.zerodsoft.scheduleweather.navermap.interfaces.PlacesItemBottomSheetButtonOnClickListener;
import com.zerodsoft.scheduleweather.navermap.util.LocationUtil;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteLocationQuery;

import java.util.List;

public class LocationItemViewPagerAdapter extends RecyclerView.Adapter<LocationItemViewPagerAdapter.PlaceItemInMapViewHolder>
{
    public static final String TAG = "LocationItemViewPagerAdapter";
    protected SparseArray<KakaoLocalDocument> placeDocumentsSparseArr = new SparseArray<>();
    protected PlaceDocuments placeDocuments;
    protected AddressResponseDocuments addressDocuments;
    protected CoordToAddressDocuments coordToAddressDocuments;
    protected PlacesItemBottomSheetButtonOnClickListener placesItemBottomSheetButtonOnClickListener;
    protected OnClickedBottomSheetListener onClickedBottomSheetListener;
    protected FavoriteLocationQuery favoriteLocationQuery;

    protected int isVisibleSelectBtn = View.GONE;
    protected int isVisibleUnSelectBtn = View.GONE;
    protected int isVisibleFavoriteBtn = View.VISIBLE;

    protected String itemPosition;

    protected final Drawable favoriteEnabledDrawable;
    protected final Drawable favoriteDisabledDrawable;

    protected final MarkerType MARKER_TYPE;

    public LocationItemViewPagerAdapter(Context context, MarkerType MARKER_TYPE)
    {
        favoriteEnabledDrawable = ContextCompat.getDrawable(context, R.drawable.favorite_enabled);
        favoriteDisabledDrawable = ContextCompat.getDrawable(context, R.drawable.favorite_disabled);
        this.MARKER_TYPE = MARKER_TYPE;
    }

    public void setFavoriteLocationQuery(FavoriteLocationQuery favoriteLocationQuery)
    {
        this.favoriteLocationQuery = favoriteLocationQuery;
    }

    public void setPlacesItemBottomSheetButtonOnClickListener(PlacesItemBottomSheetButtonOnClickListener placesItemBottomSheetButtonOnClickListener)
    {
        this.placesItemBottomSheetButtonOnClickListener = placesItemBottomSheetButtonOnClickListener;
    }

    public void setIsVisibleFavoriteBtn(int isVisibleFavoriteBtn)
    {
        this.isVisibleFavoriteBtn = isVisibleFavoriteBtn;
    }

    public LocationItemViewPagerAdapter setVisibleSelectBtn(int visibleSelectBtn)
    {
        isVisibleSelectBtn = visibleSelectBtn;
        return this;
    }

    public LocationItemViewPagerAdapter setVisibleUnSelectBtn(int visibleUnSelectBtn)
    {
        isVisibleUnSelectBtn = visibleUnSelectBtn;
        return this;

    }

    public void setPlaceDocumentsSparseArr(List<? extends KakaoLocalDocument> placeDocumentsSparseArr)
    {
        int listIndex = this.placeDocumentsSparseArr.size();
        for (int i = 0; i < placeDocumentsSparseArr.size(); i++)
        {
            this.placeDocumentsSparseArr.put(listIndex++, placeDocumentsSparseArr.get(i));
        }
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
        holder.bind(placeDocumentsSparseArr.get(position));
    }

    public SparseArray<KakaoLocalDocument> getPlaceDocumentsSparseArr()
    {
        return placeDocumentsSparseArr;
    }

    @Override
    public int getItemCount()
    {
        return placeDocumentsSparseArr.size();
    }

    public class PlaceItemInMapViewHolder extends RecyclerView.ViewHolder
    {
        protected CardviewPlacesItemBinding binding;
        protected Integer favoriteLocationId;

        public PlaceItemInMapViewHolder(@NonNull View view)
        {
            super(view);
            binding = CardviewPlacesItemBinding.bind(view);
            binding.addressLayout.addressIndex.setVisibility(View.GONE);
            binding.placeItemCardviewInBottomsheet.setOnClickListener(onClickListener);

            binding.selectThisPlaceButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    placesItemBottomSheetButtonOnClickListener.onSelectedLocation(placeDocumentsSparseArr.get(getBindingAdapterPosition()));
                }
            });

            binding.unselectThisPlaceButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    placesItemBottomSheetButtonOnClickListener.onRemovedLocation();
                }
            });

            binding.addToFavoritePlaceitemButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    FavoriteLocationDTO newFavoriteLocationDTO = new FavoriteLocationDTO();
                    KakaoLocalDocument data = placeDocumentsSparseArr.get(getBindingAdapterPosition());

                    if (data instanceof PlaceDocuments)
                    {
                        placeDocuments = (PlaceDocuments) data;

                        newFavoriteLocationDTO.setType(LocationUtil.isRestaurant(placeDocuments.getCategoryName()) ? FavoriteLocationDTO.RESTAURANT : FavoriteLocationDTO.PLACE);
                        newFavoriteLocationDTO.setAddress(placeDocuments.getAddressName());
                        newFavoriteLocationDTO.setLongitude(String.valueOf(placeDocuments.getX()));
                        newFavoriteLocationDTO.setLatitude(String.valueOf(placeDocuments.getY()));
                        newFavoriteLocationDTO.setPlaceName(placeDocuments.getPlaceName());
                        newFavoriteLocationDTO.setPlaceId(placeDocuments.getId());
                    } else if (data instanceof AddressResponseDocuments)
                    {
                        addressDocuments = (AddressResponseDocuments) data;

                        newFavoriteLocationDTO.setType(FavoriteLocationDTO.ADDRESS);
                        newFavoriteLocationDTO.setAddress(addressDocuments.getAddressName());
                        newFavoriteLocationDTO.setLatitude(String.valueOf(addressDocuments.getY()));
                        newFavoriteLocationDTO.setLongitude(String.valueOf(addressDocuments.getX()));
                    } else if (data instanceof CoordToAddressDocuments)
                    {
                        coordToAddressDocuments = (CoordToAddressDocuments) data;

                        newFavoriteLocationDTO.setType(FavoriteLocationDTO.ADDRESS);
                        newFavoriteLocationDTO.setAddress(coordToAddressDocuments.getCoordToAddressAddress().getAddressName());
                        newFavoriteLocationDTO.setLatitude(coordToAddressDocuments.getCoordToAddressAddress().getLatitude());
                        newFavoriteLocationDTO.setLongitude(coordToAddressDocuments.getCoordToAddressAddress().getLongitude());
                    }
                    newFavoriteLocationDTO.setAddedDateTime(String.valueOf(System.currentTimeMillis()));

                    favoriteLocationQuery.contains(newFavoriteLocationDTO.getPlaceId()
                            , newFavoriteLocationDTO.getAddress(), newFavoriteLocationDTO.getLatitude(), newFavoriteLocationDTO.getLongitude()
                            , new CarrierMessagingService.ResultCallback<FavoriteLocationDTO>()
                            {
                                @Override
                                public void onReceiveResult(@NonNull FavoriteLocationDTO resultFavoriteLocationDTO) throws RemoteException
                                {
                                    if (resultFavoriteLocationDTO == null)
                                    {
                                        favoriteLocationQuery.insert(newFavoriteLocationDTO, new CarrierMessagingService.ResultCallback<FavoriteLocationDTO>()
                                        {
                                            @Override
                                            public void onReceiveResult(@NonNull FavoriteLocationDTO insertedFavoriteLocationDTO) throws RemoteException
                                            {
                                                favoriteLocationId = insertedFavoriteLocationDTO.getId();
                                                binding.addToFavoritePlaceitemButton.setImageDrawable(favoriteEnabledDrawable);
                                            }
                                        });
                                    } else
                                    {
                                        favoriteLocationQuery.delete(favoriteLocationId, new CarrierMessagingService.ResultCallback<Boolean>()
                                        {
                                            @Override
                                            public void onReceiveResult(@NonNull Boolean isDeleted) throws RemoteException
                                            {
                                                binding.addToFavoritePlaceitemButton.setImageDrawable(favoriteDisabledDrawable);
                                            }
                                        });
                                    }
                                }
                            });


                }
            });
        }

        public void bind(KakaoLocalDocument kakaoLocalDocument)
        {
            setDataView(kakaoLocalDocument);
        }


        public void setDataView(KakaoLocalDocument kakaoLocalDocument)
        {
            final int position = getBindingAdapterPosition();
            itemPosition = (position + 1) + " / " + getItemCount();
            binding.itemPosition.setText(itemPosition);

            final ViewHolderData viewHolderData = new ViewHolderData(kakaoLocalDocument);
            binding.placeItemCardviewInBottomsheet.setTag(viewHolderData);

            if (kakaoLocalDocument instanceof PlaceDocuments)
            {
                placeDocuments = (PlaceDocuments) kakaoLocalDocument;

                binding.placeLayout.placeItemName.setText(placeDocuments.getPlaceName());
                binding.placeLayout.placeItemAddress.setText(placeDocuments.getAddressName());
                binding.placeLayout.placeItemCategory.setText(placeDocuments.getCategoryName());
                binding.placeItemDistance.setText(placeDocuments.getDistance() + "m");

                binding.placeLayout.getRoot().setVisibility(View.VISIBLE);
                binding.addressLayout.getRoot().setVisibility(View.GONE);
                binding.placeItemDistance.setVisibility(View.VISIBLE);
            } else if (kakaoLocalDocument instanceof AddressResponseDocuments)
            {
                addressDocuments = (AddressResponseDocuments) kakaoLocalDocument;

                binding.addressLayout.addressName.setText(addressDocuments.getAddressName());
                if (addressDocuments.getAddressResponseRoadAddress() != null)
                {
                    binding.addressLayout.anotherAddressType.setText(itemView.getContext().getString(R.string.road_addr));
                    binding.addressLayout.anotherAddressName.setText(addressDocuments.getAddressResponseRoadAddress().getAddressName());
                } else if (addressDocuments.getAddressResponseAddress() != null)
                {
                    binding.addressLayout.anotherAddressType.setText(itemView.getContext().getString(R.string.region_addr));
                    binding.addressLayout.anotherAddressName.setText(addressDocuments.getAddressResponseAddress().getAddressName());
                }

                binding.placeLayout.getRoot().setVisibility(View.GONE);
                binding.addressLayout.getRoot().setVisibility(View.VISIBLE);
                binding.placeItemDistance.setVisibility(View.GONE);
            } else if (kakaoLocalDocument instanceof CoordToAddressDocuments)
            {
                coordToAddressDocuments = (CoordToAddressDocuments) kakaoLocalDocument;

                binding.addressLayout.addressName.setText(coordToAddressDocuments.getCoordToAddressAddress().getAddressName());
                if (coordToAddressDocuments.getCoordToAddressRoadAddress() != null)
                {
                    binding.addressLayout.anotherAddressType.setText(itemView.getContext().getString(R.string.road_addr));
                    binding.addressLayout.anotherAddressName.setText(coordToAddressDocuments.getCoordToAddressRoadAddress().getAddressName());
                } else if (coordToAddressDocuments.getCoordToAddressAddress() != null)
                {
                    binding.addressLayout.anotherAddressType.setText(itemView.getContext().getString(R.string.region_addr));
                    binding.addressLayout.anotherAddressName.setText(coordToAddressDocuments.getCoordToAddressAddress().getAddressName());
                }

                binding.placeLayout.getRoot().setVisibility(View.GONE);
                binding.addressLayout.getRoot().setVisibility(View.VISIBLE);
                binding.placeItemDistance.setVisibility(View.GONE);
            }

            binding.selectThisPlaceButton.setVisibility(isVisibleSelectBtn);
            binding.unselectThisPlaceButton.setVisibility(isVisibleUnSelectBtn);
            binding.addToFavoritePlaceitemButton.setVisibility(isVisibleFavoriteBtn);

            if (isVisibleFavoriteBtn == View.VISIBLE)
            {
                String placeId = null;
                String address = null;
                String latitude = null;
                String longitude = null;

                if (kakaoLocalDocument instanceof PlaceDocuments)
                {
                    placeDocuments = (PlaceDocuments) kakaoLocalDocument;
                    address = placeDocuments.getAddressName();
                    longitude = String.valueOf(placeDocuments.getX());
                    latitude = String.valueOf(placeDocuments.getY());
                    placeId = placeDocuments.getId();
                } else if (kakaoLocalDocument instanceof AddressResponseDocuments)
                {
                    addressDocuments = (AddressResponseDocuments) kakaoLocalDocument;
                    address = addressDocuments.getAddressName();
                    latitude = String.valueOf(addressDocuments.getY());
                    longitude = String.valueOf(addressDocuments.getX());
                } else if (kakaoLocalDocument instanceof CoordToAddressDocuments)
                {
                    coordToAddressDocuments = (CoordToAddressDocuments) kakaoLocalDocument;
                    address = coordToAddressDocuments.getCoordToAddressAddress().getAddressName();
                    latitude = coordToAddressDocuments.getCoordToAddressAddress().getLatitude();
                    longitude = coordToAddressDocuments.getCoordToAddressAddress().getLongitude();
                }

                favoriteLocationQuery.contains(placeId, address, latitude, longitude
                        , new CarrierMessagingService.ResultCallback<FavoriteLocationDTO>()
                        {
                            @Override
                            public void onReceiveResult(@NonNull FavoriteLocationDTO resultFavoriteLocationDTO) throws RemoteException
                            {
                                if (resultFavoriteLocationDTO != null)
                                {
                                    favoriteLocationId = resultFavoriteLocationDTO.getId();
                                    binding.addToFavoritePlaceitemButton.setImageDrawable(favoriteEnabledDrawable);
                                } else
                                {
                                    binding.addToFavoritePlaceitemButton.setImageDrawable(favoriteDisabledDrawable);
                                }
                            }
                        });
            }


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
