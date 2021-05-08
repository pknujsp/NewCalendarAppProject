package com.zerodsoft.scheduleweather.navermap;

import android.app.Activity;
import android.content.Context;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.overlay.InfoWindow;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.navermap.favorite.FavoriteLocationFragment;
import com.zerodsoft.scheduleweather.navermap.model.CoordToAddressUtil;
import com.zerodsoft.scheduleweather.navermap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteLocationQuery;

import org.jetbrains.annotations.NotNull;

public class InfoWindowOnMarkerAdapter extends InfoWindow.DefaultViewAdapter
{
    private Activity activity;
    private LatLng latLng;
    private FavoriteLocationQuery favoriteLocationQuery;

    public InfoWindowOnMarkerAdapter(@NonNull @NotNull Context context, Activity activity, FavoriteLocationQuery favoriteLocationQuery, LatLng latLng)
    {
        super(context);
        this.activity = activity;
        this.favoriteLocationQuery = favoriteLocationQuery;
        this.latLng = latLng;
    }

    @NonNull
    @NotNull
    @Override
    protected View getContentView(@NonNull @NotNull InfoWindow infoWindow)
    {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.info_window_view_on_marker, null, false);

        TextView addressNameTextView = (TextView) view.findViewById(R.id.address_name);
        LocalApiPlaceParameter parameter = LocalParameterUtil.getCoordToAddressParameter(latLng.latitude, latLng.longitude);
        CoordToAddressUtil.coordToAddress(parameter, new JsonDownloader<CoordToAddress>()
        {
            @Override
            public void onResponseSuccessful(CoordToAddress result)
            {
                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        addressNameTextView.setText(result.getCoordToAddressDocuments().get(0).getCoordToAddressAddress().getAddressName());
                    }
                });
            }

            @Override
            public void onResponseFailed(Exception e)
            {
                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        addressNameTextView.setText(R.string.error_downloading_address);
                    }
                });
            }
        });

        TextView favoriteBtn = (TextView) view.findViewById(R.id.add_favorites_button);
        favoriteBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });

        return view;
    }

    private void addThisLocationToFavoriteLocations(String addressName, String latitude, String longitude)
    {
        FavoriteLocationDTO favoriteLocationDTO = new FavoriteLocationDTO();
        favoriteLocationDTO.setType(FavoriteLocationDTO.ADDRESS);
        favoriteLocationDTO.setAddress(addressName);
        favoriteLocationDTO.setLatitude(latitude);
        favoriteLocationDTO.setLongitude(longitude);
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
                                    activity.runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            Toast.makeText(getContext(), R.string.successful_add_favorite, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else
                        {
                            activity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    Toast.makeText(getContext(), R.string.duplicate_value, Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                });


    }
}
