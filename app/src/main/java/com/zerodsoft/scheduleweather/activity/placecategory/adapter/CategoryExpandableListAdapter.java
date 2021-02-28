package com.zerodsoft.scheduleweather.activity.placecategory.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.placecategory.activity.CategorySettingsActivity;
import com.zerodsoft.scheduleweather.activity.placecategory.interfaces.IPlaceCategory;
import com.zerodsoft.scheduleweather.activity.placecategory.interfaces.PlaceCategoryEditPopup;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.SelectedPlaceCategoryDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryExpandableListAdapter extends BaseExpandableListAdapter
{
    private final Map<String, List<PlaceCategoryDTO>> categoryMap;
    private final List<String> categoryTypes;
    private Map<Integer, List<Boolean>> checkedStatesMap;
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
        this.layoutInflater = LayoutInflater.from(context);
        categoryMap = new HashMap<>();
        categoryTypes = new ArrayList<>();

        categoryTypes.add("DEFAULT");
        categoryTypes.add("CUSTOM");

        categoryMap.put(categoryTypes.get(0), defaultCategoryList);
        categoryMap.put(categoryTypes.get(1), customCategoryList);

        checkedStatesMap = new HashMap<>();
        for (int row = 0; row < savedCheckedStates.length; row++)
        {
            List<Boolean> checkedStates = new ArrayList<>();
            for (int column = 0; column < savedCheckedStates[row].length; column++)
            {
                checkedStates.add(savedCheckedStates[row][column]);
            }
            checkedStatesMap.put(row, checkedStates);
        }
    }

    public Map<Integer, List<Boolean>> getCheckedStatesMap()
    {
        return checkedStatesMap;
    }

    @Override
    public int getGroupCount()
    {
        return categoryMap.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return categoryMap.get(categoryTypes.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return categoryTypes.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return categoryMap.get(categoryTypes.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return (long) categoryMap.get(categoryTypes.get(groupPosition)).get(childPosition).hashCode();
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
            view = layoutInflater.inflate(R.layout.place_category_settings_parent_item, null);

            groupViewHolder = new GroupViewHolder();
            groupViewHolder.categoryTypeTextView = (TextView) view.findViewById(R.id.place_category_type);

            view.setTag(groupViewHolder);
        } else
        {
            groupViewHolder = (GroupViewHolder) view.getTag();
        }

        groupViewHolder.categoryTypeTextView.setText(i == CategorySettingsActivity.CUSTOM_CATEGORY_INDEX ? context.getString(R.string.custom_category) : context.getString(R.string.default_category));
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View view, ViewGroup viewGroup)
    {
        categoryDescription = categoryMap.get(categoryTypes.get(groupPosition)).get(childPosition).getDescription();

        if (view == null)
        {
            view = layoutInflater.inflate(R.layout.place_category_settings_child_item, null);

            childViewHolder = new ChildViewHolder();
            childViewHolder.checkBox = (CheckBox) view.findViewById(R.id.place_category_checkbox);
            childViewHolder.checkBox.setTag(new EditButtonHolder());

            view.setTag(childViewHolder);
        } else
        {
            childViewHolder = (ChildViewHolder) view.getTag();
        }

        childViewHolder.checkBox.setText(categoryDescription);

        childViewHolder.checkBox.setOnCheckedChangeListener(null);
        if (checkedStatesMap.containsKey(groupPosition))
        {
            childViewHolder.checkBox.setChecked(checkedStatesMap.get(groupPosition).get(childPosition));
        } else
        {
            checkedStatesMap.get(groupPosition).add(false);
            childViewHolder.checkBox.setChecked(false);
        }

        childViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                // 실시간으로 변경 값을 적용한다
                checkedStatesMap.get(groupPosition).remove(childPosition);
                checkedStatesMap.get(groupPosition).add(childPosition, isChecked);

                if (isChecked)
                {
                    iPlaceCategory.insertSelected(categoryMap.get(categoryTypes.get(groupPosition)).get(childPosition).getCode(), new CarrierMessagingService.ResultCallback<SelectedPlaceCategoryDTO>()
                    {
                        @Override
                        public void onReceiveResult(@NonNull SelectedPlaceCategoryDTO selectedPlaceCategoryDTO) throws RemoteException
                        {

                        }
                    });
                } else
                {
                    iPlaceCategory.deleteSelected(categoryMap.get(categoryTypes.get(groupPosition)).get(childPosition).getCode(), new CarrierMessagingService.ResultCallback<Boolean>()
                    {
                        @Override
                        public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException
                        {

                        }
                    });
                }
            }
        });

        if (groupPosition == CategorySettingsActivity.CUSTOM_CATEGORY_INDEX)
        {
            EditButtonHolder editButtonHolder = (EditButtonHolder) childViewHolder.checkBox.getTag();
            editButtonHolder.placeCategoryDTO = categoryMap.get(categoryTypes.get(groupPosition)).get(childPosition);
            childViewHolder.checkBox.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View view)
                {
                    placeCategoryEditPopup.showPopup(view);
                    return true;
                }
            });
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
        TextView categoryTypeTextView;
    }

    public final static class ChildViewHolder
    {
        CheckBox checkBox;
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
