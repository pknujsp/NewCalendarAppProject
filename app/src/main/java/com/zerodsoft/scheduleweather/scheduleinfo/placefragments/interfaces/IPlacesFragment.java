package com.zerodsoft.scheduleweather.scheduleinfo.placefragments.interfaces;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStoreOwner;

public interface IPlacesFragment
{
    LifecycleOwner getLifeCycleOwner();
    ViewModelStoreOwner getViewModelStoreOwner();
}
