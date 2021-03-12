package com.zerodsoft.scheduleweather.event.places.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public class DefaultMapDialogFragment extends DialogFragment
{
    public static final String TAG = "DefaultMapDialogFragment";
    private DefaultMapFragment defaultMapFragment;
    private static DefaultMapDialogFragment instance;

    public DefaultMapDialogFragment(LocationDTO locationDTO)
    {
        defaultMapFragment = new DefaultMapFragment(locationDTO);
    }

    public static DefaultMapDialogFragment newInstance(LocationDTO locationDTO)
    {
        instance = new DefaultMapDialogFragment(locationDTO);
        return instance;
    }

    public static DefaultMapDialogFragment getInstance()
    {
        return instance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        return new Dialog(getContext(), R.style.DialogTransparent);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.map_dialog_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getParentFragmentManager().beginTransaction().add(R.id.map_container, defaultMapFragment, DefaultMapFragment.TAG)
                .commit();
    }
}
