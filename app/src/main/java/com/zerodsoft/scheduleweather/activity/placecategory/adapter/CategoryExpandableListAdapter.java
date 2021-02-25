package com.zerodsoft.scheduleweather.activity.placecategory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.retrofit.PlaceCategory;

import java.util.ArrayList;
import java.util.List;

public class CategoryExpandableListAdapter extends BaseExpandableListAdapter
{
    private final List<List<PlaceCategory>> categoryList;
    private String categoryDescription;
    private GroupViewHolder groupViewHolder;
    private ChildViewHolder childViewHolder;
    private Context context;
    private LayoutInflater layoutInflater;
    private boolean[][] savedCheckedStates;
    private boolean[][] newCheckedStates;


    public CategoryExpandableListAdapter(Context context, List<PlaceCategory> defaultCategoryList, List<PlaceCategory> customCategoryList, boolean[][] savedCheckedStates)
    {
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        categoryList = new ArrayList<>();

        categoryList.add(defaultCategoryList);
        categoryList.add(customCategoryList);

        this.savedCheckedStates = savedCheckedStates;
        this.newCheckedStates = savedCheckedStates;
    }

    @Override
    public int getGroupCount()
    {
        return categoryList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return categoryList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int i)
    {
        return categoryList.get(i);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return categoryList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int i)
    {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return 0;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup)
    {
        if (view == null)
        {
            view = layoutInflater.inflate(R.layout.place_category_setting_row_item, null);

            groupViewHolder = new GroupViewHolder();
            groupViewHolder.categoryTypeCheckBox = (MaterialCheckBox) view.findViewById(R.id.place_category_checkbox);

            view.setTag(groupViewHolder);
        } else
        {
            groupViewHolder = (GroupViewHolder) view.getTag();
        }

        groupViewHolder.categoryTypeCheckBox.setText(i == 0 ? context.getString(R.string.default_category) : context.getString(R.string.custom_category));
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View view, ViewGroup viewGroup)
    {
        categoryDescription = categoryList.get(groupPosition).get(childPosition).getDescription();

        if (view == null)
        {
            view = layoutInflater.inflate(R.layout.place_category_setting_row_item, null);

            MaterialCheckBox checkBox = (MaterialCheckBox) view.findViewById(R.id.side_nav_calendar_checkbox);
            childViewHolder = new ChildViewHolder();
            childViewHolder.checkBox = checkBox;

            view.setTag(childViewHolder);
        } else
        {
            childViewHolder = (ChildViewHolder) view.getTag();
        }

        childViewHolder.checkBox.setText(categoryDescription);

        childViewHolder.checkBox.setOnCheckedChangeListener(null);
        childViewHolder.checkBox.setChecked(savedCheckedStates[groupPosition][childPosition]);

        childViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                newCheckedStates[groupPosition][childPosition] = isChecked;
            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1)
    {
        return true;
    }

    public final class GroupViewHolder
    {
        MaterialCheckBox categoryTypeCheckBox;
    }

    public final class ChildViewHolder
    {
        MaterialCheckBox checkBox;
    }
}
