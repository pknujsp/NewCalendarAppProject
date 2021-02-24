package com.zerodsoft.scheduleweather.activity.map.fragment.search.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.map.fragment.interfaces.OnSelectedMapCategory;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;

import java.util.List;

public class PlaceCategoriesAdapter extends RecyclerView.Adapter<PlaceCategoriesAdapter.PlaceCategoryViewHolder>
{
    private List<KakaoLocalApiCategory> categoryList;
    private OnSelectedMapCategory onSelectedMapCategory;

    public PlaceCategoriesAdapter(OnSelectedMapCategory onSelectedMapCategory)
    {
        categoryList = KakaoLocalApiCategoryUtil.getList();
        this.onSelectedMapCategory = onSelectedMapCategory;
    }

    @NonNull
    @Override
    public PlaceCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new PlaceCategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_category_recyclerview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceCategoryViewHolder holder, int position)
    {
        holder.onBind(categoryList.get(position));
    }

    @Override
    public int getItemCount()
    {
        return categoryList.size();
    }

    class PlaceCategoryViewHolder extends RecyclerView.ViewHolder
    {
        private TextView categoryDescriptionTextView;
        private KakaoLocalApiCategory categoryInfo;

        PlaceCategoryViewHolder(View view)
        {
            super(view);
            categoryDescriptionTextView = (TextView) view.findViewById(R.id.category_description_textview);
            categoryDescriptionTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onSelectedMapCategory.onSelectedMapCategory(categoryInfo);
                }
            });
        }

        public void onBind(KakaoLocalApiCategory categoryInfo)
        {
            this.categoryInfo = categoryInfo;
            categoryDescriptionTextView.setText(categoryInfo.getDescription());
        }

        public KakaoLocalApiCategory getCategoryInfo()
        {
            return categoryInfo;
        }

        public TextView getCategoryDescriptionTextView()
        {
            return categoryDescriptionTextView;
        }
    }
}
