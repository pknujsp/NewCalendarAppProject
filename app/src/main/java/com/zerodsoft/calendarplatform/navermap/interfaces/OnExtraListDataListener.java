package com.zerodsoft.calendarplatform.navermap.interfaces;

import androidx.recyclerview.widget.RecyclerView;

public interface OnExtraListDataListener<T>
{
    void loadExtraListData(T e, RecyclerView.AdapterDataObserver adapterDataObserver);

    void loadExtraListData(RecyclerView.AdapterDataObserver adapterDataObserver);
}
