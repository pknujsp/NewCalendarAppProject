package com.zerodsoft.scheduleweather.kakaomap.fragment.searchheader;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentLocationSearchBarBinding;
import com.zerodsoft.scheduleweather.etc.FragmentStateCallback;
import com.zerodsoft.scheduleweather.event.places.interfaces.PoiItemOnClickListener;
import com.zerodsoft.scheduleweather.kakaomap.fragment.search.LocationSearchFragment;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchresult.LocationSearchResultFragment;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchresult.adapter.SearchResultListAdapter;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.PlacesListBottomSheetController;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.SearchBarController;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;

public class MapHeaderSearchFragment extends Fragment implements SearchBarController
{
    public static final String TAG = "MapSearchFragment";
    private FragmentLocationSearchBarBinding binding;

    private final IMapPoint iMapPoint;
    private final IMapData iMapData;
    private final PlacesListBottomSheetController placesListBottomSheetController;
    private final PoiItemOnClickListener poiItemOnClickListener;
    private final LocationSearchListener locationSearchListener;

    private Drawable mapDrawable;
    private Drawable listDrawable;

    public MapHeaderSearchFragment(Fragment fragment)
    {
        this.iMapPoint = (IMapPoint) fragment;
        this.iMapData = (IMapData) fragment;
        this.placesListBottomSheetController = (PlacesListBottomSheetController) fragment;
        this.poiItemOnClickListener = (PoiItemOnClickListener) fragment;
        this.locationSearchListener = (LocationSearchListener) fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = FragmentLocationSearchBarBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        binding.closeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });

        binding.viewTypeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });


        binding.edittext.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent)
            {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                {
                    //검색
                    binding.viewTypeButton.setVisibility(View.VISIBLE);
                    locationSearchListener.search(binding.edittext.getText().toString());
                   // locationSearchFragment.insertHistory(binding.edittext.getText().toString());
                    return true;
                }
                return false;
            }
        });

        binding.viewTypeButton.setVisibility(View.GONE);

        mapDrawable = ContextCompat.getDrawable(getContext(), R.drawable.map_icon);
        listDrawable = ContextCompat.getDrawable(getContext(), R.drawable.list_icon);
    }

    public FragmentLocationSearchBarBinding getBinding()
    {
        return binding;
    }

    @Override
    public void setQuery(String query, boolean submit)
    {
        if (KakaoLocalApiCategoryUtil.isCategory(query))
        {
            binding.edittext.setText(KakaoLocalApiCategoryUtil.getDefaultDescription(query));
        } else
        {
            binding.edittext.setText(query);
        }

        if (submit)
        {
            binding.viewTypeButton.setVisibility(View.VISIBLE);
            locationSearchListener.search(query);
        } else
        {

        }
    }

    @Override
    public void changeViewTypeImg(int type)
    {
        if (type == SearchBarController.MAP)
        {
            binding.viewTypeButton.setImageDrawable(mapDrawable);
        } else
        {
            binding.viewTypeButton.setImageDrawable(listDrawable);
        }
    }

    @Override
    public void setViewTypeVisibility(int visibility)
    {
        binding.viewTypeButton.setVisibility(visibility);
    }


    public interface LocationSearchListener
    {
        void search(String query);
    }

}
