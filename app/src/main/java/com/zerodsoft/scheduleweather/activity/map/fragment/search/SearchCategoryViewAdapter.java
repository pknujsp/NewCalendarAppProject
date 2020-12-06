package com.zerodsoft.scheduleweather.activity.map.fragment.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategory;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;

import java.util.List;

public class SearchCategoryViewAdapter extends RecyclerView.Adapter<SearchCategoryViewAdapter.SearchCategoryViewHolder>
{
    private List<KakaoLocalApiCategory> categoryList;

    public interface OnCategoryClickListener
    {
        void selectedCategory(KakaoLocalApiCategory category);
    }

    private OnCategoryClickListener onCategoryClickListener;

    public SearchCategoryViewAdapter(SearchFragment searchFragment)
    {
        categoryList = KakaoLocalApiCategoryUtil.getList();
        this.onCategoryClickListener = searchFragment;
    }

    @NonNull
    @Override
    public SearchCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.search_category_recyclerview_item, parent, false);
        return new SearchCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchCategoryViewHolder holder, int position)
    {
        holder.onBindView(categoryList.get(position));
        holder.getCategoryDescriptionTextView().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onCategoryClickListener.selectedCategory(holder.getCategoryInfo());
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return categoryList.size();
    }

    class SearchCategoryViewHolder extends RecyclerView.ViewHolder
    {
        private TextView categoryDescriptionTextView;
        private KakaoLocalApiCategory categoryInfo;

        SearchCategoryViewHolder(View view)
        {
            super(view);
            categoryDescriptionTextView = (TextView) view.findViewById(R.id.category_description_textview);
        }

        public void onBindView(KakaoLocalApiCategory categoryInfo)
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
