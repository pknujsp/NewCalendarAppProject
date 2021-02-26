package com.zerodsoft.scheduleweather.activity.placecategory.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.widget.PopupMenu;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.placecategory.activity.CategorySettingsActivity;
import com.zerodsoft.scheduleweather.activity.placecategory.interfaces.IPlaceCategory;
import com.zerodsoft.scheduleweather.activity.placecategory.interfaces.PlaceCategoryEditPopup;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import java.util.ArrayList;
import java.util.List;

public class CategoryExpandableListAdapter extends BaseExpandableListAdapter
{
    private final List<List<PlaceCategoryDTO>> categoryList;
    private final boolean[][] savedCheckedStates;
    private final PlaceCategoryEditPopup placeCategoryEditPopup;
    private final IPlaceCategory iPlaceCategory;

    private String categoryDescription;
    private GroupViewHolder groupViewHolder;
    private ChildViewHolder childViewHolder;
    private Context context;
    private LayoutInflater layoutInflater;

    public CategoryExpandableListAdapter(Activity activity, IPlaceCategory iPlaceCategory, List<PlaceCategoryDTO> defaultCategoryList, List<PlaceCategoryDTO> customCategoryList, boolean[][] savedCheckedStates)
    {
        this.context = activity.getApplicationContext();
        this.placeCategoryEditPopup = (PlaceCategoryEditPopup) activity;
        this.iPlaceCategory = iPlaceCategory;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        categoryList = new ArrayList<>();

        categoryList.add(defaultCategoryList);
        categoryList.add(customCategoryList);

        this.savedCheckedStates = savedCheckedStates;
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

        groupViewHolder.categoryTypeCheckBox.setText(i == CategorySettingsActivity.CUSTOM_CATEGORY_INDEX ? context.getString(R.string.default_category) : context.getString(R.string.custom_category));
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

            if (groupPosition == CategorySettingsActivity.CUSTOM_CATEGORY_INDEX)
            {
                childViewHolder.editButton = (ImageButton) view.findViewById(R.id.category_edit_button);
                childViewHolder.editButton.setVisibility(View.VISIBLE);

                final EditButtonHolder editButtonHolder = new EditButtonHolder();
                editButtonHolder.placeCategoryDTO = categoryList.get(groupPosition).get(childPosition);
                childViewHolder.editButton.setTag(editButtonHolder);
            }

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
                // 실시간으로 변경 값을 적용한다
                if (isChecked)
                {
                    iPlaceCategory.insertSelected(categoryList.get(groupPosition).get(childPosition).getCode());
                } else
                {
                    iPlaceCategory.deleteSelected(categoryList.get(groupPosition).get(childPosition).getCode());
                }
            }
        });

        if (groupPosition == CategorySettingsActivity.CUSTOM_CATEGORY_INDEX)
        {
            childViewHolder.editButton.setOnClickListener(customEditOnClickListener);
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1)
    {
        return true;
    }

    public final static class GroupViewHolder
    {
        MaterialCheckBox categoryTypeCheckBox;
    }

    public final static class ChildViewHolder
    {
        MaterialCheckBox checkBox;
        ImageButton editButton;
    }


    public final static class EditButtonHolder
    {
        PlaceCategoryDTO placeCategoryDTO;

        public PlaceCategoryDTO getPlaceCategoryDTO()
        {
            return placeCategoryDTO;
        }
    }

    private final View.OnClickListener customEditOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            placeCategoryEditPopup.showPopup(v);
        }
    };
}
