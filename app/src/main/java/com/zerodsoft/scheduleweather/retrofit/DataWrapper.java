package com.zerodsoft.scheduleweather.retrofit;

public class DataWrapper<T>
{
    private Exception exception;
    private T data;

    public DataWrapper(T data)
    {
        this.data = data;
    }

    public DataWrapper(Exception exception)
    {
        this.exception = exception;
    }


    public Exception getException()
    {
        return exception;
    }

    public DataWrapper<T> setException(Exception exception)
    {
        this.exception = exception;
        return this;
    }

    public T getData()
    {
        return data;
    }

    public DataWrapper<T> setData(T data)
    {
        this.data = data;
        return this;
    }
}

