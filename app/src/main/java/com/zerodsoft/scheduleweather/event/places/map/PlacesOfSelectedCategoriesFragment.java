package com.zerodsoft.scheduleweather.event.places.map;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.service.carrier.CarrierMessagingService;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.zerodsoft.scheduleweather.activity.placecategory.viewmodel.PlaceCategoryViewModel;
import com.zerodsoft.scheduleweather.common.interfaces.OnProgressBarListener;
import com.zerodsoft.scheduleweather.databinding.PlacelistFragmentBinding;
import com.zerodsoft.scheduleweather.etc.CustomRecyclerViewItemDecoration;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.places.adapter.PlaceItemsAdapters;
import com.zerodsoft.scheduleweather.event.places.interfaces.OnClickedPlacesListListener;
import com.zerodsoft.scheduleweather.event.places.interfaces.PlaceItemsGetter;
import com.zerodsoft.scheduleweather.navermap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.navermap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnExtraListDataListener;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlacesOfSelectedCategoriesFragment extends Fragment implements PlaceItemsGetter, OnProgressBarListener, OnExtraListDataListener<PlaceCategoryDTO>
{
    public static final String TAG = "PlaceListFragment";
    private DecimalFormat decimalFormat = new DecimalFormat("#.#");

    private PlacelistFragmentBinding binding;
    private List<PlaceCategoryDTO> placeCategoryList;
    private LocationDTO selectedLocationDto;

    private PlaceCategoryViewModel placeCategoryViewModel;
    private LocationViewModel locationViewModel;

    private final ContentValues INSTANCE_VALUES;
    private final OnClickedPlacesListListener onClickedPlacesListListener;

    private Map<String, PlaceItemsAdapters> adaptersMap = new HashMap<>();
    private Map<String, RecyclerView> listMap = new HashMap<>();

    public PlacesOfSelectedCategoriesFragment(ContentValues instanceValues, Fragment fragment)
    {
        this.INSTANCE_VALUES = instanceValues;
        this.onClickedPlacesListListener = (OnClickedPlacesListListener) fragment;
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
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        placeCategoryViewModel = new ViewModelProvider(this).get(PlaceCategoryViewModel.class);

        binding.errorText.setVisibility(View.GONE);
        initLocation();
        makeCategoryListView();

        binding.radiusSeekbarLayout.setVisibility(View.GONE);
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        float value = Math.round((Float.parseFloat(App.getPreference_key_radius_range()) / 1000f) * 10) / 10f;
        binding.radiusSeekbar.setValue(Float.parseFloat(decimalFormat.format(value)));

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


    private void initLocation()
    {
        locationViewModel.getLocation(INSTANCE_VALUES.getAsInteger(CalendarContract.Instances.CALENDAR_ID)
                , INSTANCE_VALUES.getAsLong(CalendarContract.Instances.EVENT_ID), new CarrierMessagingService.ResultCallback<LocationDTO>()
                {
                    @Override
                    public void onReceiveResult(@NonNull LocationDTO location) throws RemoteException
                    {
                        if (!location.isEmpty())
                        {
                            selectedLocationDto = location;
                            getActivity().runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    binding.addressName.setText(selectedLocationDto.getAddressName());
                                }
                            });
                        }
                    }
                });
    }

    private void setSearchRadius()
    {
        float value = Math.round((Float.parseFloat(App.getPreference_key_radius_range()) / 1000f) * 10) / 10f;
        binding.searchRadius.setText(getString(R.string.search_radius) + " " + decimalFormat.format(value) + "km");
    }

    public void makeCategoryListView()
    {
        placeCategoryViewModel.selectConvertedSelected(new CarrierMessagingService.ResultCallback<List<PlaceCategoryDTO>>()
        {
            @Override
            public void onReceiveResult(@NonNull List<PlaceCategoryDTO> placeCategoryDTOS) throws RemoteException
            {
                placeCategoryList = placeCategoryDTOS;
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (placeCategoryList.isEmpty())
                        {
                            binding.errorText.setVisibility(View.VISIBLE);
                            return;
                        } else
                        {
                            binding.errorText.setVisibility(View.GONE);
                        }

                        binding.categoryViewlist.removeAllViews();
                        adaptersMap.clear();
                        listMap.clear();

                        LayoutInflater layoutInflater = getLayoutInflater();

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
                            viewModel.init(placeParameter, PlacesOfSelectedCategoriesFragment.this);
                            viewModel.getPagedListMutableLiveData().observe(getActivity(), new Observer<PagedList<PlaceDocuments>>()
                            {
                                @Override
                                public void onChanged(PagedList<PlaceDocuments> placeDocuments)
                                {
                                    //카테고리 뷰 어댑터에 데이터 삽입
                                    adapter.submitList(placeDocuments);
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

                            adaptersMap.put(placeCategory.getCode(), adapter);
                            listMap.put(placeCategory.getCode(), itemRecyclerView);
                            binding.categoryViewlist.addView(categoryView);
                        }
                    }
                });
            }
        });


    }

    @Override
    public List<PlaceDocuments> getPlaceItems(PlaceCategoryDTO placeCategoryDTO)
    {
        return adaptersMap.get(placeCategoryDTO.getCode()).getCurrentList().snapshot();
    }

    @Override
    public void setProgressBarVisibility(int visibility)
    {

    }

    @Override
    public void loadExtraListData(PlaceCategoryDTO e, RecyclerView.AdapterDataObserver adapterDataObserver)
    {
        adaptersMap.get(e.getCode()).registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
        {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount)
            {
                super.onItemRangeInserted(positionStart, itemCount);
                adapterDataObserver.onItemRangeInserted(positionStart, itemCount);
                adaptersMap.get(e.getCode()).unregisterAdapterDataObserver(this);
            }
        });
        RecyclerView recyclerView = listMap.get(e.getCode());
        recyclerView.scrollBy(100000, 0);
    }

    @Override
    public void loadExtraListData(RecyclerView.AdapterDataObserver adapterDataObserver)
    {

    }
}
