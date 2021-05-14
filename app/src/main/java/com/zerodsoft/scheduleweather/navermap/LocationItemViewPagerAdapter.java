package com.zerodsoft.scheduleweather.navermap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
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
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteLocationQuery;

import java.util.ArrayList;
import java.util.List;

public class LocationItemViewPagerAdapter extends RecyclerView.Adapter<LocationItemViewPagerAdapter.PlaceItemInMapViewHolder>
{
    public static final String TAG = "LocationItemViewPagerAdapter";
    protected List<KakaoLocalDocument> placeDocumentsList = new ArrayList<>();
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

    public LocationItemViewPagerAdapter(Context context)
    {
        favoriteEnabledDrawable = ContextCompat.getDrawable(context, R.drawable.favorite_enabled);
        favoriteDisabledDrawable = ContextCompat.getDrawable(context, R.drawable.favorite_disabled);
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
                    placesItemBottomSheetButtonOnClickListener.onSelectedLocation(placeDocumentsList.get(getBindingAdapterPosition()));
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
                    FavoriteLocationDTO favoriteLocationDTO = new FavoriteLocationDTO();
                    KakaoLocalDocument data = placeDocumentsList.get(getBindingAdapterPosition());

                    if (data instanceof PlaceDocuments)
                    {
                        favoriteLocationDTO.setType(FavoriteLocationDTO.PLACE);

                        placeDocuments = (PlaceDocuments) data;
                        favoriteLocationDTO.setAddress(placeDocuments.getAddressName());
                        favoriteLocationDTO.setLongitude(String.valueOf(placeDocuments.getX()));
                        favoriteLocationDTO.setLatitude(String.valueOf(placeDocuments.getY()));
                        favoriteLocationDTO.setPlaceName(placeDocuments.getPlaceName());
                        favoriteLocationDTO.setPlaceId(placeDocuments.getId());
                    } else if (data instanceof AddressResponseDocuments)
                    {
                        favoriteLocationDTO.setType(FavoriteLocationDTO.ADDRESS);

                        addressDocuments = (AddressResponseDocuments) data;
                        favoriteLocationDTO.setAddress(addressDocuments.getAddressName());
                        favoriteLocationDTO.setLatitude(String.valueOf(addressDocuments.getY()));
                        favoriteLocationDTO.setLongitude(String.valueOf(addressDocuments.getX()));
                    } else if (data instanceof CoordToAddressDocuments)
                    {
                        favoriteLocationDTO.setType(FavoriteLocationDTO.ADDRESS);

                        coordToAddressDocuments = (CoordToAddressDocuments) data;
                        favoriteLocationDTO.setAddress(coordToAddressDocuments.getCoordToAddressAddress().getAddressName());
                        favoriteLocationDTO.setLatitude(coordToAddressDocuments.getCoordToAddressAddress().getLatitude());
                        favoriteLocationDTO.setLongitude(coordToAddressDocuments.getCoordToAddressAddress().getLongitude());
                    }
                    favoriteLocationDTO.setAddedDateTime(String.valueOf(System.currentTimeMillis()));

                    favoriteLocationQuery.contains(favoriteLocationDTO.getType(), favoriteLocationDTO.getPlaceId()
                            , favoriteLocationDTO.getAddress(), favoriteLocationDTO.getLatitude(), favoriteLocationDTO.getLongitude()
                            , new CarrierMessagingService.ResultCallback<FavoriteLocationDTO>()
                            {
                                @Override
                                public void onReceiveResult(@NonNull FavoriteLocationDTO resultFavoriteLocationDTO) throws RemoteException
                                {
                                    if (resultFavoriteLocationDTO == null)
                                    {
                                        favoriteLocationQuery.insert(favoriteLocationDTO, new CarrierMessagingService.ResultCallback<FavoriteLocationDTO>()
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
                FavoriteLocationDTO favoriteLocationDTO = new FavoriteLocationDTO();

                if (kakaoLocalDocument instanceof PlaceDocuments)
                {
                    favoriteLocationDTO.setType(FavoriteLocationDTO.PLACE);

                    placeDocuments = (PlaceDocuments) kakaoLocalDocument;
                    favoriteLocationDTO.setAddress(placeDocuments.getAddressName());
                    favoriteLocationDTO.setLongitude(String.valueOf(placeDocuments.getX()));
                    favoriteLocationDTO.setLatitude(String.valueOf(placeDocuments.getY()));
                    favoriteLocationDTO.setPlaceName(placeDocuments.getPlaceName());
                    favoriteLocationDTO.setPlaceId(placeDocuments.getId());
                } else if (kakaoLocalDocument instanceof AddressResponseDocuments)
                {
                    favoriteLocationDTO.setType(FavoriteLocationDTO.ADDRESS);

                    addressDocuments = (AddressResponseDocuments) kakaoLocalDocument;
                    favoriteLocationDTO.setAddress(addressDocuments.getAddressName());
                    favoriteLocationDTO.setLatitude(String.valueOf(addressDocuments.getY()));
                    favoriteLocationDTO.setLongitude(String.valueOf(addressDocuments.getX()));
                } else if (kakaoLocalDocument instanceof CoordToAddressDocuments)
                {
                    favoriteLocationDTO.setType(FavoriteLocationDTO.ADDRESS);

                    coordToAddressDocuments = (CoordToAddressDocuments) kakaoLocalDocument;
                    favoriteLocationDTO.setAddress(coordToAddressDocuments.getCoordToAddressAddress().getAddressName());
                    favoriteLocationDTO.setLatitude(coordToAddressDocuments.getCoordToAddressAddress().getLatitude());
                    favoriteLocationDTO.setLongitude(coordToAddressDocuments.getCoordToAddressAddress().getLongitude());
                }

                favoriteLocationQuery.contains(favoriteLocationDTO.getType(), favoriteLocationDTO.getPlaceId()
                        , favoriteLocationDTO.getAddress(), favoriteLocationDTO.getLatitude(), favoriteLocationDTO.getLongitude()
                        , new CarrierMessagingService.ResultCallback<FavoriteLocationDTO>()
                        {
                            @Override
                            public void onReceiveResult(@NonNull FavoriteLocationDTO favoriteLocationDTO) throws RemoteException
                            {
                                if (favoriteLocationDTO != null)
                                {
                                    favoriteLocationId = favoriteLocationDTO.getId();
                                }
                                binding.addToFavoritePlaceitemButton.setImageDrawable(favoriteLocationDTO == null ? favoriteDisabledDrawable : favoriteEnabledDrawable);
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
