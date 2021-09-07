package com.zerodsoft.calendarplatform.navermap.building.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.navermap.building.model.CompanyData;

import java.util.List;

public class CompanyListAdapter extends RecyclerView.Adapter<CompanyListAdapter.ViewHolder>
{
    private List<CompanyData> companyDataList;

    public CompanyListAdapter(List<CompanyData> companyDataList)
    {
        this.companyDataList = companyDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.building_floor_company_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.onBind();
    }

    @Override
    public int getItemCount()
    {
        return companyDataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView companyNameTextView;
        TextView companyThemeTextView;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            companyNameTextView = (TextView) itemView.findViewById(R.id.company_name);
            companyThemeTextView = (TextView) itemView.findViewById(R.id.company_theme);

        }

        public void onBind()
        {
            companyNameTextView.setText(companyDataList.get(getBindingAdapterPosition()).getCompanyName());
            companyThemeTextView.setText(companyDataList.get(getBindingAdapterPosition()).getCompanyTheme());
        }
    }
}
