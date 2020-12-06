package com.zerodsoft.scheduleweather.kakaomap.callback;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;

public class AddressItemCallback extends DiffUtil.ItemCallback<AddressResponseDocuments>
{
    @Override
    public boolean areItemsTheSame(@NonNull AddressResponseDocuments oldItem, @NonNull AddressResponseDocuments newItem)
    {
        return oldItem.getAddressName().equals(newItem.getAddressName());
    }

    @Override
    public boolean areContentsTheSame(@NonNull AddressResponseDocuments oldItem, @NonNull AddressResponseDocuments newItem)
    {
        return oldItem.getAddressName().equals(newItem.getAddressName());
    }
}
