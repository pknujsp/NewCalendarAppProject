package com.zerodsoft.scheduleweather.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.zerodsoft.scheduleweather.activity.editevent.IEventRepeat;

public class RepeaterFragment extends DialogFragment
{
    private static RepeaterFragment instance;
    private IEventRepeat iEventRepeat;

    public RepeaterFragment(IEventRepeat iEventRepeat)
    {
        this.iEventRepeat = iEventRepeat;
    }

    public static RepeaterFragment getInstance()
    {
        return instance;
    }

    public static RepeaterFragment newInstance(IEventRepeat iEventRepeat)
    {
        instance = new RepeaterFragment(iEventRepeat);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }
}
