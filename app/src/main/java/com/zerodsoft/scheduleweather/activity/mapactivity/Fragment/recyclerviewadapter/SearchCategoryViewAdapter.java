package com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.recyclerviewadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.SearchFragment;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategory;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryCode;

import java.util.List;

public class SearchCategoryViewAdapter extends RecyclerView.Adapter<SearchCategoryViewAdapter.SearchCategoryViewHolder>
{
    private List<KakaoLocalApiCategory> categoryList;

    public interface OnCategoryClickListener
    {
        void selectedCategory(String name, String description);
    }

    private OnCategoryClickListener onCategoryClickListener;

    public SearchCategoryViewAdapter(SearchFragment searchFragment)
    {
        categoryList = KakaoLocalApiCategoryCode.toArrayList();
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
                onCategoryClickListener.selectedCategory(holder.getName(), holder.getDescription());
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

        public String getName()
        {
            return categoryInfo.getName();
        }

        public String getDescription()
        {
            return categoryDescriptionTextView.getText().toString();
        }

        public TextView getCategoryDescriptionTextView()
        {
            return categoryDescriptionTextView;
        }
    }
}
