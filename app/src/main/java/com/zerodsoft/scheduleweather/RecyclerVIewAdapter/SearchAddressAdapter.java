package com.zerodsoft.scheduleweather.RecyclerVIewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponseDocuments;

import java.util.ArrayList;
import java.util.List;

public class SearchAddressAdapter extends RecyclerView.Adapter<SearchAddressAdapter.AddressViewHolder>
{
    private List<AddressResponseDocuments> addressList;
    private Context context;

    public SearchAddressAdapter(List<AddressResponseDocuments> list, Context context)
    {
        this.context = context;
        addressList = list;
    }

    public void setAddressList(List<AddressResponseDocuments> addressList)
    {
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.search_address_recycler_view_item, parent, false);

        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position)
    {
        holder.addressTextView.setText(addressList.get(position).getAddressName());

        switch (addressList.get(position).getAddressType())
        {
            case AddressResponseDocuments.REGION:
                //지명
                holder.anotherTypeTextView.setText(context.getString(R.string.region));
                holder.anotherTypeAddressTextView.setText(addressList.get(position).getAddressResponseAddress().getAddressName());
                break;
            case AddressResponseDocuments.REGION_ADDR:
                //지명 주소
                holder.anotherTypeTextView.setText(context.getString(R.string.road_addr));
                holder.anotherTypeAddressTextView.setText(addressList.get(position).getAddressResponseRoadAddress().getAddressName());
                break;
            case AddressResponseDocuments.ROAD:
                //도로명
                holder.anotherTypeTextView.setText(context.getString(R.string.road));
                holder.anotherTypeAddressTextView.setText(addressList.get(position).getAddressResponseRoadAddress().getAddressName());
                break;
            case AddressResponseDocuments.ROAD_ADDR:
                //도로명 주소
                holder.anotherTypeTextView.setText(context.getString(R.string.region_addr));
                holder.anotherTypeAddressTextView.setText(addressList.get(position).getAddressResponseAddress().getAddressName());
                break;
        }
    }

    @Override
    public int getItemCount()
    {
        return addressList.size();
    }

    class AddressViewHolder extends RecyclerView.ViewHolder
    {
        TextView addressTextView;
        TextView anotherTypeTextView;
        TextView anotherTypeAddressTextView;

        AddressViewHolder(View itemView)
        {
            super(itemView);
            addressTextView = (TextView) itemView.findViewById(R.id.search_address_name_textview);
            anotherTypeTextView = (TextView) itemView.findViewById(R.id.another_address_type_textview);
            anotherTypeAddressTextView = (TextView) itemView.findViewById(R.id.search_another_type_address_textview);
        }
    }
}
