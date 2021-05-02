package com.zerodsoft.scheduleweather.common.interfaces;

import retrofit2.Response;

public interface OnDbQueryListener<T>
{
    void onSuccessful(T result);

    void onFailed(Exception e);
}
