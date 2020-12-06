package com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.adapter;

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
import com.zerodsoft.scheduleweather.kakaomap.callback.AddressItemCallback;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;

public class AddressesAdapter extends PagedListAdapter<AddressResponseDocuments, AddressesAdapter.ItemViewHolder>
{
    private Context context;

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
        }

        public void bind(AddressResponseDocuments item)
        {
            addressName.setText(item.getAddressName());
            addressIndex.setText(Integer.toString(getAdapterPosition()));

            if (item.getAddressResponseRoadAddress() != null)
            {
                anotherAddressType.setText((R.string.region_addr));
                anotherAddressName.setText(item.getAddressResponseAddress().getAddressName());
            } else if (item.getAddressResponseAddress() != null)
            {
                anotherAddressType.setText(context.getString(R.string.road_addr));
                anotherAddressName.setText(item.getAddressResponseRoadAddress().getAddressName());
            }

        }
    }

    public AddressesAdapter(Context context)
    {
        super(new AddressItemCallback());
        this.context = context;
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
