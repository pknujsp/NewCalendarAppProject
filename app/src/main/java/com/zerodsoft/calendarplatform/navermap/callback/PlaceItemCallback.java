package com.zerodsoft.calendarplatform.navermap.callback;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

public class PlaceItemCallback extends DiffUtil.ItemCallback<PlaceDocuments>
{
    @Override
    public boolean areItemsTheSame(@NonNull PlaceDocuments oldItem, @NonNull PlaceDocuments newItem)
    {
        return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull PlaceDocuments oldItem, @NonNull PlaceDocuments newItem)
    {
        return oldItem.getId().equals(newItem.getId());
    }

}
