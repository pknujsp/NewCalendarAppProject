package com.zerodsoft.scheduleweather.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.Activity.MapActivity.MapActivity;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Retrofit.DownloadData;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceCategoryResponse.PlaceCategoryDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceKeywordResponse.PlaceKeywordDocuments;

import java.util.List;

public class MapBottomSheetFragment extends Fragment implements MapActivity.OnControlItemFragment
{
    public static final String TAG = "MAP_BOTTOM_SHEET_FRAGMENT";
    private static MapBottomSheetFragment mapBottomSheetFragment = null;

    private TextView selectedItemPlaceNameTextView;
    private TextView selectedItemPlaceCategoryTextView;
    private TextView selectedItemPlaceAddressTextView;
    private TextView selectedItemPlaceDescriptionTextView;

    private TextView selectedItemAddressNameTextView;
    private TextView selectedItemAnotherAddressNameTextView;
    private TextView selectedItemAnotherAddressTypeTextView;

    private ImageButton selectedItemFavoriteButton;
    private ImageButton selectedItemShareButton;
    private ImageButton selectedItemCheckButton;
    private ImageButton selectedItemLeftButton;
    private ImageButton selectedItemRightButton;

    private LinearLayout placeItemLayout;
    private LinearLayout addressItemLayout;

    private List<AddressResponseDocuments> addressList = null;
    private List<PlaceKeywordDocuments> placeKeywordList = null;
    private List<PlaceCategoryDocuments> placeCategoryList = null;

    private BottomSheetBehavior bottomSheetBehavior;

    private int resultType = Integer.MIN_VALUE;
    private int selectedItemPosition;

    public static MapBottomSheetFragment getInstance()
    {
        if (mapBottomSheetFragment == null)
        {
            mapBottomSheetFragment = new MapBottomSheetFragment();
        }
        return mapBottomSheetFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        LinearLayout bottomSheet = (LinearLayout) getActivity().findViewById(R.id.map_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        return bottomSheet;
    }

    @Override
    public void onStart()
    {
        switch (resultType)
        {
            case DownloadData.ADDRESS:
                setLayoutVisibility();
                displayAddressInfo();
                break;
            case Integer.MIN_VALUE:
                break;
            default:
                setLayoutVisibility();
                displayPlaceInfo();
                break;
        }
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        placeItemLayout = (LinearLayout) view.findViewById(R.id.item_place_layout);
        addressItemLayout = (LinearLayout) view.findViewById(R.id.item_address_layout);

        selectedItemPlaceNameTextView = (TextView) view.findViewById(R.id.selected_place_name_textview);
        selectedItemPlaceCategoryTextView = (TextView) view.findViewById(R.id.selected_place_category_textview);
        selectedItemPlaceAddressTextView = (TextView) view.findViewById(R.id.selected_place_address_textview);
        selectedItemPlaceDescriptionTextView = (TextView) view.findViewById(R.id.selected_place_description_textview);

        selectedItemFavoriteButton = (ImageButton) view.findViewById(R.id.add_favorite_address_button);
        selectedItemShareButton = (ImageButton) view.findViewById(R.id.share_address_button);
        selectedItemCheckButton = (ImageButton) view.findViewById(R.id.check_address_button);
        selectedItemLeftButton = (ImageButton) view.findViewById(R.id.left_address_button);
        selectedItemRightButton = (ImageButton) view.findViewById(R.id.right_address_button);

        selectedItemAddressNameTextView = (TextView) view.findViewById(R.id.selected_address_name_textview);
        selectedItemAnotherAddressNameTextView = (TextView) view.findViewById(R.id.selected_another_address_textview);
        selectedItemAnotherAddressTypeTextView = (TextView) view.findViewById(R.id.selected_another_address_type_textview);
    }

    private void displayPlaceInfo()
    {
        switch (resultType)
        {
            case DownloadData.PLACE_KEYWORD:
                selectedItemPlaceNameTextView.setText(placeKeywordList.get(selectedItemPosition).getPlaceName());
                selectedItemPlaceCategoryTextView.setText(placeKeywordList.get(selectedItemPosition).getCategoryName());

                if (placeKeywordList.get(selectedItemPosition).getRoadAddressName() != null)
                {
                    selectedItemPlaceAddressTextView.setText(placeKeywordList.get(selectedItemPosition).getRoadAddressName());
                } else
                {
                    // 지번주소만 있는 경우
                    selectedItemPlaceAddressTextView.setText(placeKeywordList.get(selectedItemPosition).getAddressName());
                }
                selectedItemPlaceDescriptionTextView.setText("TEST");
                break;

            case DownloadData.PLACE_CATEGORY:
                selectedItemPlaceNameTextView.setText(placeCategoryList.get(selectedItemPosition).getPlaceName());
                selectedItemPlaceCategoryTextView.setText(placeCategoryList.get(selectedItemPosition).getCategoryName());

                if (placeCategoryList.get(selectedItemPosition).getRoadAddressName() != null)
                {
                    selectedItemPlaceAddressTextView.setText(placeCategoryList.get(selectedItemPosition).getRoadAddressName());
                } else
                {
                    // 지번주소만 있는 경우
                    selectedItemPlaceAddressTextView.setText(placeCategoryList.get(selectedItemPosition).getAddressName());
                }
                selectedItemPlaceDescriptionTextView.setText("TEST");
                break;
        }
    }

    private void displayAddressInfo()
    {
        selectedItemAddressNameTextView.setText(addressList.get(selectedItemPosition).getAddressName());

        switch (addressList.get(selectedItemPosition).getAddressType())
        {
            case AddressResponseDocuments.REGION:
                //지명
                selectedItemAnotherAddressTypeTextView.setText(getString(R.string.region));
                selectedItemAnotherAddressNameTextView.setText(addressList.get(selectedItemPosition).getAddressResponseAddress().getAddressName());
                break;
            case AddressResponseDocuments.REGION_ADDR:
                //지명 주소
                selectedItemAnotherAddressTypeTextView.setText(getString(R.string.road_addr));
                selectedItemAnotherAddressNameTextView.setText(addressList.get(selectedItemPosition).getAddressResponseRoadAddress().getAddressName());
                break;
            case AddressResponseDocuments.ROAD:
                //도로명
                selectedItemAnotherAddressTypeTextView.setText(getString(R.string.road));
                selectedItemAnotherAddressNameTextView.setText(addressList.get(selectedItemPosition).getAddressResponseRoadAddress().getAddressName());
                break;
            case AddressResponseDocuments.ROAD_ADDR:
                //도로명 주소
                selectedItemAnotherAddressTypeTextView.setText(getString(R.string.region_addr));
                selectedItemAnotherAddressNameTextView.setText(addressList.get(selectedItemPosition).getAddressResponseAddress().getAddressName());
                break;
        }
    }


    private void setLayoutVisibility()
    {
        switch (resultType)
        {
            case DownloadData.ADDRESS:
                addressItemLayout.setVisibility(View.VISIBLE);
                placeItemLayout.setVisibility(View.GONE);
                break;

            default:
                placeItemLayout.setVisibility(View.VISIBLE);
                addressItemLayout.setVisibility(View.GONE);
                break;
        }
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onChangeFragment(Bundle bundle)
    {
        resultType = bundle.getInt("type");
        selectedItemPosition = bundle.getInt("position");

        switch (resultType)
        {
            case DownloadData.ADDRESS:
                addressList = bundle.getParcelableArrayList("itemList");
                break;
            case DownloadData.PLACE_KEYWORD:
                placeKeywordList = bundle.getParcelableArrayList("itemList");
                break;
            case DownloadData.PLACE_CATEGORY:
                placeCategoryList = bundle.getParcelableArrayList("itemList");
                break;
        }
    }

    @Override
    public void setBehaviorState(int state)
    {
        bottomSheetBehavior.setState(state);
    }

    @Override
    public boolean getBehaviorStateExpand()
    {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
        {
            return true;
        } else
        {
            return false;
        }
    }

    @Override
    public void onShowItemInfo(int position)
    {
        selectedItemPosition = position;
        setLayoutVisibility();
        switch (resultType)
        {
            case DownloadData.ADDRESS:
                displayAddressInfo();
                break;
            default:
                displayPlaceInfo();
                break;
        }
    }
}
