package com.zerodsoft.scheduleweather.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

import androidx.core.os.HandlerCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends android.app.Application
{
    public static ExecutorService executorService = Executors.newSingleThreadExecutor();
    public static Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());
    public static boolean is24HourSystem = true;
}
