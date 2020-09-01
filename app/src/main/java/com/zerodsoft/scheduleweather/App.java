package com.zerodsoft.scheduleweather;

import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends android.app.Application
{
    public static ExecutorService executorService = Executors.newSingleThreadExecutor();
    public static Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());
}
