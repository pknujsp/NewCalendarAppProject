package com.zerodsoft.scheduleweather.event.places.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.kakaomap.fragment.KakaoMapFragment;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public class DefaultMapDialogFragment extends DialogFragment
{
    public static final String TAG = "DefaultMapDialogFragment";
    private static DefaultMapDialogFragment instance;
    private DefaultMapFragment defaultMapFragment;


    public DefaultMapDialogFragment()
    {
    }

    public static DefaultMapDialogFragment newInstance()
    {
        instance = new DefaultMapDialogFragment();
        return instance;
    }

    public static DefaultMapDialogFragment getInstance()
    {
        return instance;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
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
        getChildFragmentManager().beginTransaction().add(R.id.map_fragment_container, DefaultMapFragment.getInstance(), DefaultMapFragment.TAG).commit();
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }


    @Override
    public void onPause()
    {
        super.onPause();
        //카테고리 목록 프래그먼트의 맵뷰 재 활성화
    }

    @Override
    public void onDismiss(final DialogInterface dialog)
    {
        getDialog().hide();
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof DialogInterface.OnDismissListener)
        {
            ((DialogInterface.OnDismissListener) parentFragment).onDismiss(dialog);
        }
    }

}
