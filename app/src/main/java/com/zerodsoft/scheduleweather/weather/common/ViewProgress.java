package com.zerodsoft.scheduleweather.weather.common;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zerodsoft.scheduleweather.R;

public class ViewProgress
{
    private View dataView;
    private ProgressBar progressBar;
    private TextView errorView;

    public ViewProgress(View dataView, ProgressBar progressBar, TextView errorView)
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

    public void onCompletedProcessingData(boolean isSuccessful, String text)
    {
        onCompletedProcessingData(true);

        if (!isSuccessful)
        {
            if (text != null)
            {
                errorView.setText(text);
            } else
            {
                errorView.setText(R.string.error);
            }
        }
    }

    public void onStartedProcessingData()
    {
        errorView.setVisibility(View.GONE);
        dataView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public ProgressBar getProgressBar()
    {
        return progressBar;
    }

    public View getDataView()
    {
        return dataView;
    }

    public TextView getErrorView()
    {
        return errorView;
    }
}
