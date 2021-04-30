package com.zerodsoft.scheduleweather.navermap.fragment.searchresult.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnClickedLocListItem;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.navermap.callback.AddressItemCallback;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;

public class AddressesAdapter extends PagedListAdapter<AddressResponseDocuments, AddressesAdapter.ItemViewHolder>
{
    private final Context context;
    private final IMapData iMapData;
    private final OnClickedLocListItem onClickedLocListItem;

    class ItemViewHolder extends RecyclerView.ViewHolder
    {
        private TextView addressName;
        private TextView anotherAddressName;
        private TextView anotherAddressType;
        private TextView addressIndex;

        public ItemViewHolder(View view)
        {
            super(view);
            addressName = (TextView) view.findViewById(R.id.address_name);
            addressIndex = (TextView) view.findViewById(R.id.address_index);
            anotherAddressName = (TextView) view.findViewById(R.id.another_address_name);
            anotherAddressType = (TextView) view.findViewById(R.id.another_address_type);
            view.getRootView().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onClickedLocListItem.onClickedLocItem(getAdapterPosition());
                }
            });
        }

        public void bind(AddressResponseDocuments item)
        {
            addressName.setText(item.getAddressName());
            addressIndex.setText(String.valueOf(getAdapterPosition() + 1));

            if (item.getAddressResponseRoadAddress() != null)
            {
                anotherAddressType.setText(context.getString(R.string.road_addr));
                anotherAddressName.setText(item.getAddressResponseRoadAddress().getAddressName());
            } else if (item.getAddressResponseAddress() != null)
            {
                anotherAddressType.setText(context.getString(R.string.region_addr));
                anotherAddressName.setText(item.getAddressResponseAddress().getAddressName());
            }
        }
    }

    public AddressesAdapter(Context context, IMapData iMapData, OnClickedLocListItem onClickedLocListItem)
    {
        super(new AddressItemCallback());
        this.context = context;
        this.iMapData = iMapData;
        this.onClickedLocListItem = onClickedLocListItem;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.address_recycler_view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position)
    {
        holder.bind(getItem(position));
    }

    @Override
    public void submitList(@Nullable PagedList<AddressResponseDocuments> pagedList)
    {
        super.submitList(pagedList);
    }

    @Override
    public void submitList(@Nullable PagedList<AddressResponseDocuments> pagedList, @Nullable Runnable commitCallback)
    {
        super.submitList(pagedList, commitCallback);
    }
}
