package com.zerodsoft.scheduleweather.event.places.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.event.places.adapter.PlaceItemInMapViewAdapter;


public class PlaceListBottomSheetView
{
    private RecyclerView recyclerView;
    private PlaceItemInMapViewAdapter adapter;
    private View bottomSheetView;

    public PlaceListBottomSheetView(View view)
    {
        this.bottomSheetView = view;
        recyclerView = (RecyclerView) bottomSheetView.findViewById(R.id.place_item_recycler_view);
    }

}