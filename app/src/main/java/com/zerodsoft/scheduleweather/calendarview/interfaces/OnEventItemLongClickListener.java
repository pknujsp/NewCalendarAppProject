package com.zerodsoft.scheduleweather.calendarview.interfaces;

import android.content.ContentValues;
import android.view.View;

public interface OnEventItemLongClickListener
{
    void createInstancePopupMenu(ContentValues instance, View anchorView, int gravity);
}
