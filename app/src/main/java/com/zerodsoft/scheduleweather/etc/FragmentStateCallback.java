package com.zerodsoft.scheduleweather.etc;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public abstract class FragmentStateCallback
{
    static public final int ON_VIEW_CREATED = 0;
    static public final int ON_ACTIVITY_CREATED = 1;
    static public final int ON_DETACH = 2;
    static public final int ON_ATTACH = 3;
    static public final int ON_REMOVED = 4;

    public void onChangedState(int state)
    {
    }


}
