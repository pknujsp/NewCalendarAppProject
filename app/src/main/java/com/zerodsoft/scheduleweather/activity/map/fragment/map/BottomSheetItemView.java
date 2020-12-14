package com.zerodsoft.scheduleweather.activity.map.fragment.map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

public class BottomSheetItemView extends RelativeLayout
{
    public BottomSheetItemView(Context context)
    {
        super(context);
    }

    public BottomSheetItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BottomSheetItemView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }


    public void setPlace(PlaceDocuments document)
    {
        View view = getRootView();
        TextView placeName = (TextView) view.findViewById(R.id.bottom_sheet_place_name);
        TextView category = (TextView) view.findViewById(R.id.bottom_sheet_place_category);
        TextView address = (TextView) view.findViewById(R.id.bottom_sheet_place_address_name);
        placeName.setText(document.getPlaceName());
        category.setText(document.getCategoryGroupName());
        address.setText(document.getAddressName());
    }

    public void setAddress(AddressResponseDocuments document)
    {
        View view = getRootView();
        TextView addressName = (TextView) view.findViewById(R.id.bottom_sheet_address_name);
        TextView anotherType = (TextView) view.findViewById(R.id.bottom_sheet_another_address_type);
        TextView anotherName = (TextView) view.findViewById(R.id.bottom_sheet_another_address_name);
        addressName.setText(document.getAddressName());
        if (document.getAddressResponseRoadAddress() != null)
        {
            anotherType.setText(getContext().getString(R.string.road_addr));
            anotherName.setText(document.getAddressResponseRoadAddress().getAddressName());
        } else if (document.getAddressResponseAddress() != null)
        {
            anotherType.setText(getContext().getString(R.string.region_addr));
            anotherName.setText(document.getAddressResponseAddress().getAddressName());
        }
    }
}
