package com.zerodsoft.scheduleweather.navermap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnCoordToAddressListener;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;

import org.jetbrains.annotations.NotNull;

public class OnLongClickMapLocationItemAdapter extends LocationItemViewPagerAdapter
{
    private final OnCoordToAddressListener onCoordToAddressListener;
    private String latitude;
    private String longitude;

    public OnLongClickMapLocationItemAdapter(Context context, OnCoordToAddressListener onCoordToAddressListener)
    {
        super(context);
        this.onCoordToAddressListener = onCoordToAddressListener;
    }

    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }

    @Override
    public int getItemCount()
    {
        return 1;
    }

    @NonNull
    @NotNull
    @Override
    public OnLongClickMapLocationItemViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType)
    {
        return new OnLongClickMapLocationItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_places_item, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull PlaceItemInMapViewHolder holder, int position)
    {
        ((OnLongClickMapLocationItemViewHolder) holder).bind(null);
    }

    class OnLongClickMapLocationItemViewHolder extends PlaceItemInMapViewHolder
    {

        public OnLongClickMapLocationItemViewHolder(@NonNull @NotNull View view)
        {
            super(view);
        }

        @Override
        public void bind(KakaoLocalDocument kakaoLocalDocument)
        {
            onCoordToAddressListener.coordToAddress(latitude, longitude, new JsonDownloader<CoordToAddressDocuments>()
            {
                @Override
                public void onResponseSuccessful(CoordToAddressDocuments result)
                {
                    result.getCoordToAddressAddress().setLatitude(latitude);
                    result.getCoordToAddressAddress().setLongitude(longitude);
                    placeDocumentsList.clear();
                    placeDocumentsList.add(result);
                    OnLongClickMapLocationItemViewHolder.super.bind(placeDocumentsList.get(0));
                }

                @Override
                public void onResponseFailed(Exception e)
                {

                }
            });
        }
    }

}
