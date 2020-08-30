package com.zerodsoft.scheduleweather.thread;

public interface RepositoryCallback<T>
{
    void onComplete(Result<T> result);
}