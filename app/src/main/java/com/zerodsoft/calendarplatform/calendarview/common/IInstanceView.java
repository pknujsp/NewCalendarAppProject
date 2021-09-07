package com.zerodsoft.calendarplatform.calendarview.common;

import android.graphics.Paint;
import android.text.TextPaint;

public interface IInstanceView
{
    int getTextSize();

    int getTextLeftMargin();

    int getTextTopBottomMargin();

    Paint getMorePaint();

    TextPaint getMoreTextPaint();
}
