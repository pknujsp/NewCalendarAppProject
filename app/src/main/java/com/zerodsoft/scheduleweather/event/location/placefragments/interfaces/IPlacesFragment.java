package com.zerodsoft.scheduleweather.event.location.placefragments.interfaces;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStoreOwner;

public interface IPlacesFragment
{
    LifecycleOwner getLifeCycleOwner();
    ViewModelStoreOwner getViewModelStoreOwner();
}
