package com.zerodsoft.calendarplatform.thread;

public interface RepositoryCallback<T>
{
    void onComplete(Result<T> result);
}