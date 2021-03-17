package com.zerodsoft.scheduleweather.event.places.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.databinding.PlacelistFragmentBinding;
import com.zerodsoft.scheduleweather.etc.RecyclerViewItemDecoration;
import com.zerodsoft.scheduleweather.event.places.adapter.PlaceItemsAdapters;
import com.zerodsoft.scheduleweather.event.places.interfaces.FragmentController;
import com.zerodsoft.scheduleweather.event.places.interfaces.OnClickedPlacesListListener;
import com.zerodsoft.scheduleweather.event.places.interfaces.PlaceCategory;
import com.zerodsoft.scheduleweather.event.places.interfaces.PlaceItemsGetter;
import com.zerodsoft.scheduleweather.kakaomap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceListFragment extends Fragment implements PlaceItemsGetter
{
    public static final String TAG = "PlaceListFragment";
    private final PlaceCategory placeCategoryInterface;
    private final FragmentController fragmentController;
    private OnClickedPlacesListListener onClickedPlacesListListener;
    private PlacelistFragmentBinding binding;
    private List<PlaceCategoryDTO> placeCategoryList;
    private LocationDTO selectedLocationDto;
    private CoordToAddress coordToAddressResult;
    private Button mapButton;

    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true)
    {
        @Override
        public void handleOnBackPressed()
        {
            fragmentController.replaceFragment(PlacesMapFragment.TAG);
            onBackPressedCallback.remove();
        }
    };

    private Map<PlaceCategoryDTO, PlaceItemsAdapters> adaptersMap;

    public PlaceListFragment(Fragment fragment)
    {
        this.placeCategoryInterface = (PlaceCategory) fragment;
        this.fragmentController = (FragmentController) fragment;
    }

    public void setSelectedLocationDto(LocationDTO selectedLocationDto)
    {
        this.selectedLocationDto = selectedLocationDto;
    }

    public void setOnClickedPlacesListListener(OnClickedPlacesListListener onClickedPlacesListListener)
    {
        this.onClickedPlacesListListener = onClickedPlacesListListener;
    }

    public void setCoordToAddressResult(CoordToAddress coordToAddressResult)
    {
        this.coordToAddressResult = coordToAddressResult;
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
        binding = PlacelistFragmentBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mapButton = new MaterialButton(getContext());
        mapButton.setText(R.string.open_map);
        mapButton.setTextColor(Color.WHITE);
        mapButton.setBackgroundColor(Color.GREEN);

        FrameLayout.LayoutParams buttonLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        mapButton.setLayoutParams(buttonLayoutParams);
        binding.rootLayout.addView(mapButton);

        mapButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //목록 열고(리스트), 닫기(맵)
                fragmentController.replaceFragment(PlacesMapFragment.TAG);
            }
        });

        binding.scrollview.setOnScrollChangeListener(new View.OnScrollChangeListener()
        {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
            {
                Log.e(TAG, "scrollY : " + scrollY + " , oldScrollY : " + oldScrollY);
                if (scrollY - oldScrollY > 0)
                {
                    // 아래로 스크롤
                    if (mapButton.getVisibility() == View.VISIBLE)
                    {
                        mapButton.setVisibility(View.GONE);
                    }
                } else if (scrollY - oldScrollY < 0)
                {
                    // 위로 스크롤
                    if (mapButton.getVisibility() != View.VISIBLE)
                    {
                        mapButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        if (selectedLocationDto.getPlaceName() != null)
        {
            //장소와 주소 표기
            binding.locationName.setText(selectedLocationDto.getPlaceName());
            binding.placeAddressName.setVisibility(View.VISIBLE);
            binding.placeAddressName.setText(coordToAddressResult.getCoordToAddressDocuments().get(0)
                    .getCoordToAddressAddress().getAddressName());
        } else
        {
            //주소 표기
            binding.locationName.setText(coordToAddressResult.getCoordToAddressDocuments().get(0)
                    .getCoordToAddressAddress().getAddressName());
            binding.placeAddressName.setVisibility(View.GONE);
        }

        makeCategoryListView();
    }

    public void setOnBackPressedCallback()
    {
        requireActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
    }

    public void makeCategoryListView()
    {
        binding.categoryViewlist.removeAllViews();
        LayoutInflater layoutInflater = getLayoutInflater();

        placeCategoryList = placeCategoryInterface.getPlaceCategoryList();
        adaptersMap = new HashMap<>();

        for (PlaceCategoryDTO placeCategory : placeCategoryList)
        {
            LinearLayout categoryView = (LinearLayout) layoutInflater.inflate(R.layout.place_category_view, null);
            ((TextView) categoryView.findViewById(R.id.map_category_name)).setText(placeCategory.getDescription());

            RecyclerView itemRecyclerView = (RecyclerView) categoryView.findViewById(R.id.map_category_itemsview);

            itemRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
            itemRecyclerView.addItemDecoration(new RecyclerViewItemDecoration((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics())));

            LocalApiPlaceParameter placeParameter = LocalParameterUtil.getPlaceParameter(placeCategory.getCode(), selectedLocationDto.getLatitude(),
                    selectedLocationDto.getLongitude(), LocalApiPlaceParameter.DEFAULT_SIZE, LocalApiPlaceParameter.DEFAULT_PAGE,
                    LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY);
            placeParameter.setRadius(App.getPreference_key_radius_range());

            PlaceItemsAdapters adapter = new PlaceItemsAdapters(onClickedPlacesListListener, placeCategory);
            itemRecyclerView.setAdapter(adapter);

            PlacesViewModel viewModel = new ViewModelProvider(getActivity()).get(PlacesViewModel.class);
            viewModel.init(placeParameter);
            viewModel.getPagedListMutableLiveData().observe(getActivity(), new Observer<PagedList<PlaceDocuments>>()
            {
                @Override
                public void onChanged(PagedList<PlaceDocuments> placeDocuments)
                {
                    //카테고리 뷰 어댑터에 데이터 삽입
                    adapter.submitList(placeDocuments);
                    //맵 뷰의 어댑터에 데이터 삽입
                }
            });

            ((Button) categoryView.findViewById(R.id.map_category_more)).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onClickedPlacesListListener.onClickedMore(placeCategory);
                }
            });

            adaptersMap.put(placeCategory, adapter);
            binding.categoryViewlist.addView(categoryView);
        }
    }

    @Override
    public List<PlaceDocuments> getPlaceItems(PlaceCategoryDTO placeCategoryDTO)
    {
        return adaptersMap.get(placeCategoryDTO).getCurrentList().snapshot();
    }
}