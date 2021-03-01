package com.zerodsoft.scheduleweather.event.location.placefragments.interfaces;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStoreOwner;

public interface IPlacesFragment
{
    LifecycleOwner getLifeCycleOwner();
    ViewModelStoreOwner getViewModelStoreOwner();
    Fragment getFragment();
}
