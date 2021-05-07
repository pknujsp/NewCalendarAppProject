package com.zerodsoft.scheduleweather.weather.common;

import android.view.View;
import android.widget.ProgressBar;

public class ViewProgress
{
    private View dataView;
    private ProgressBar progressBar;
    private View errorView;

    public ViewProgress(View dataView, ProgressBar progressBar, View errorView)
    {
        this.dataView = dataView;
        this.progressBar = progressBar;
        this.errorView = errorView;
    }

    public void onCompletedProcessingData(boolean isSuccessful)
    {
        if (isSuccessful)
        {
            errorView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            dataView.setVisibility(View.VISIBLE);
        } else
        {
            errorView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            dataView.setVisibility(View.GONE);
        }
    }

    public void onStartedProcessingData()
    {
        errorView.setVisibility(View.GONE);
        dataView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }
}
