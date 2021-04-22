package com.zerodsoft.scheduleweather.event.places.placecategorylist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.common.interfaces.OnProgressBarListener;
import com.zerodsoft.scheduleweather.databinding.PlacelistFragmentBinding;
import com.zerodsoft.scheduleweather.etc.CustomRecyclerViewItemDecoration;
import com.zerodsoft.scheduleweather.event.places.adapter.PlaceItemsAdapters;
import com.zerodsoft.scheduleweather.event.places.map.PlacesMapFragmentNaver;
import com.zerodsoft.scheduleweather.event.places.interfaces.FragmentController;
import com.zerodsoft.scheduleweather.event.places.interfaces.OnClickedPlacesListListener;
import com.zerodsoft.scheduleweather.event.places.interfaces.PlaceCategory;
import com.zerodsoft.scheduleweather.event.places.interfaces.PlaceItemsGetter;
import com.zerodsoft.scheduleweather.kakaomap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceListFragment extends Fragment implements PlaceItemsGetter, OnProgressBarListener
{
    public static final String TAG = "PlaceListFragment";
    private final PlaceCategory placeCategoryInterface;

    private OnClickedPlacesListListener onClickedPlacesListListener;
    private PlacelistFragmentBinding binding;
    private List<PlaceCategoryDTO> placeCategoryList;
    private LocationDTO selectedLocationDto;
    private CoordToAddress coordToAddressResult;

    private Map<PlaceCategoryDTO, PlaceItemsAdapters> adaptersMap;

    public PlaceListFragment(Fragment fragment)
    {
        this.placeCategoryInterface = (PlaceCategory) fragment;
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

        binding.radiusSeekbarLayout.setVisibility(View.GONE);
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        binding.radiusSeekbar.setValue(Float.parseFloat(decimalFormat.format(Double.valueOf(App.getPreference_key_radius_range()) / 1000)));

        binding.searchRadius.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                binding.radiusSeekbarLayout.setVisibility(binding.radiusSeekbarLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });

        binding.applyRadius.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //변경한 값 적용
                binding.radiusSeekbarLayout.setVisibility(View.GONE);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = preferences.edit();

                final String newValueStrMeter = String.valueOf((int) (binding.radiusSeekbar.getValue() * 1000));
                editor.putString(getString(R.string.preference_key_radius_range), newValueStrMeter);
                editor.apply();

                App.setPreference_key_radius_range(newValueStrMeter);
                setSearchRadius();

                makeCategoryListView();
            }
        });

        setSearchRadius();
    }

    private void setSearchRadius()
    {
        double value = Double.parseDouble(App.getPreference_key_radius_range()) / 1000.0;
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        binding.searchRadius.setText(getString(R.string.search_radius) + " " + decimalFormat.format(value) + "km");
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
            itemRecyclerView.addItemDecoration(new CustomRecyclerViewItemDecoration((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics())));

            LocalApiPlaceParameter placeParameter = LocalParameterUtil.getPlaceParameter(placeCategory.getCode(), String.valueOf(selectedLocationDto.getLatitude()),
                    String.valueOf(selectedLocationDto.getLongitude()), LocalApiPlaceParameter.DEFAULT_SIZE, LocalApiPlaceParameter.DEFAULT_PAGE,
                    LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY);
            placeParameter.setRadius(App.getPreference_key_radius_range());

            PlaceItemsAdapters adapter = new PlaceItemsAdapters(onClickedPlacesListListener, placeCategory);
            itemRecyclerView.setAdapter(adapter);

            PlacesViewModel viewModel = new ViewModelProvider(getActivity()).get(PlacesViewModel.class);
            viewModel.init(placeParameter, this);
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
                    onClickedPlacesListListener.onClickedMoreInList(placeCategory);
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

    @Override
    public void setProgressBarVisibility(int visibility)
    {

    }

}
