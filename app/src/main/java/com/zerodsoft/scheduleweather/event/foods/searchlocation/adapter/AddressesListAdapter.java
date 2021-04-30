package com.zerodsoft.scheduleweather.event.foods.searchlocation.adapter;

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
import com.zerodsoft.scheduleweather.event.foods.searchlocation.interfaces.OnClickedLocationItem;
import com.zerodsoft.scheduleweather.navermap.callback.AddressItemCallback;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;

public class AddressesListAdapter extends PagedListAdapter<AddressResponseDocuments, AddressesListAdapter.ItemViewHolder>
{
    private final OnClickedLocationItem onClickedLocationItem;

    public AddressesListAdapter(OnClickedLocationItem onClickedLocationItem)
    {
        super(new AddressItemCallback());
        this.onClickedLocationItem = onClickedLocationItem;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder
    {
        private TextView addressName;
        private TextView anotherAddressName;
        private TextView anotherAddressType;

        public ItemViewHolder(View view)
        {
            super(view);
            view.findViewById(R.id.address_index).setVisibility(View.GONE);

            addressName = (TextView) view.findViewById(R.id.address_name);
            anotherAddressName = (TextView) view.findViewById(R.id.another_address_name);
            anotherAddressType = (TextView) view.findViewById(R.id.another_address_type);
        }

        public void bind(AddressResponseDocuments item)
        {
            addressName.setText(item.getAddressName());

            if (item.getAddressResponseRoadAddress() != null)
            {
                anotherAddressType.setText(itemView.getContext().getString(R.string.road_addr));
                anotherAddressName.setText(item.getAddressResponseRoadAddress().getAddressName());
            } else if (item.getAddressResponseAddress() != null)
            {
                anotherAddressType.setText(itemView.getContext().getString(R.string.region_addr));
                anotherAddressName.setText(item.getAddressResponseAddress().getAddressName());
            }

            itemView.getRootView().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onClickedLocationItem.onClickedLocationItem(getItem(getBindingAdapterPosition()));
                }
            });
        }
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
    public void submitList(@Nullable PagedList<AddressResponseDocuments> pagedList, @Nullable Runnable commitCallback)
    {
        super.submitList(pagedList, commitCallback);
    }

    @Override
    public void submitList(@Nullable PagedList<AddressResponseDocuments> pagedList)
    {
        super.submitList(pagedList);
    }
}
