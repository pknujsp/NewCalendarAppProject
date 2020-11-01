package com.zerodsoft.scheduleweather.retrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class RetrofitCallback<T> implements Callback<T>
{
    @Override
    public void onResponse(Call<T> call, Response<T> response)
    {
        if (response.body() != null)
        {
            handleResponse(response.body());
        } else
        {
            handleError(response);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t)
    {
        if (t instanceof Exception)
        {
            handleFailure((Exception) t);
        }
    }

    abstract protected void handleResponse(T data);

    abstract protected void handleError(Response<T> response);

    abstract protected void handleFailure(Exception e);
}
