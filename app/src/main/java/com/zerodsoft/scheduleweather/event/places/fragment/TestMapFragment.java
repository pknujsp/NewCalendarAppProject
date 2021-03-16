package com.zerodsoft.scheduleweather.event.places.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.activity.placecategory.viewmodel.PlaceCategoryViewModel;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IstartActivity;
import com.zerodsoft.scheduleweather.databinding.FragmentTestMapBinding;
import com.zerodsoft.scheduleweather.etc.RecyclerViewItemDecoration;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocation;
import com.zerodsoft.scheduleweather.event.places.adapter.PlaceItemInMapViewAdapter;
import com.zerodsoft.scheduleweather.event.places.adapter.PlaceItemsAdapters;
import com.zerodsoft.scheduleweather.event.places.bottomsheet.PlaceBottomSheetBehaviour;
import com.zerodsoft.scheduleweather.event.places.interfaces.BottomSheet;
import com.zerodsoft.scheduleweather.event.places.interfaces.FragmentController;
import com.zerodsoft.scheduleweather.event.places.interfaces.PlaceCategory;
import com.zerodsoft.scheduleweather.kakaomap.model.CoordToAddressUtil;
import com.zerodsoft.scheduleweather.kakaomap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import java.util.List;


public class TestMapFragment extends Fragment implements BottomSheet, PlaceCategory, FragmentController
{
    private FragmentTestMapBinding binding;
    private PlacesMapFragment placesMapFragment;
    private PlaceListFragment placeListFragment;
    private PlaceBottomSheetBehaviour placeListBottomSheetBehavior;

    private final ILocation iLocation;
    private final IstartActivity istartActivity;
    private CoordToAddress coordToAddressResult;
    private LocationDTO selectedLocationDto;
    private List<PlaceCategoryDTO> placeCategoryList;
    private PlaceCategoryViewModel placeCategoryViewModel;
    private RecyclerView bottomSheetRecyclerView;
    private PlaceItemInMapViewAdapter adapter;


    public TestMapFragment(ILocation iLocation, IstartActivity istartActivity)
    {
        this.iLocation = iLocation;
        this.istartActivity = istartActivity;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentTestMapBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        placeCategoryViewModel = new ViewModelProvider(this).get(PlaceCategoryViewModel.class);

        initLocation();

        //set bottomsheet
        FrameLayout customBottomSheet = (FrameLayout) view.findViewById(R.id.place_list_bottom_sheet_view);
        bottomSheetRecyclerView = (RecyclerView) customBottomSheet.findViewById(R.id.place_item_recycler_view);
        bottomSheetRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        PlacesMapFragment.HorizontalMarginItemDecoration horizontalMarginItemDecoration = new PlacesMapFragment.HorizontalMarginItemDecoration(getContext());
        bottomSheetRecyclerView.addItemDecoration(horizontalMarginItemDecoration);

        bottomSheetRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                if (!bottomSheetRecyclerView.canScrollHorizontally(dx))
                {
                    if (dx > 0)
                    {
                        //right
                        bottomSheetRecyclerView.scrollToPosition(0);
                    } else if (dx < 0)
                    {
                        //left
                        bottomSheetRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    }
                }
            }
        });

        placeListBottomSheetBehavior = PlaceBottomSheetBehaviour.from(customBottomSheet);
        placeListBottomSheetBehavior.setPeekHeight(0);
        placeListBottomSheetBehavior.setDraggable(false);
        placeListBottomSheetBehavior.setState(PlaceBottomSheetBehaviour.STATE_COLLAPSED);

        placeListBottomSheetBehavior.setAnchorOffset(0.5f);
        placeListBottomSheetBehavior.setAnchorSheetCallback(new PlaceBottomSheetBehaviour.AnchorSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                {
                } else if (newState == PlaceBottomSheetBehaviour.STATE_EXPANDED)
                {
                } else if (newState == PlaceBottomSheetBehaviour.STATE_DRAGGING)
                {
                } else if (newState == PlaceBottomSheetBehaviour.STATE_HALF_EXPANDED)
                {
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {

                float h = bottomSheet.getHeight();
                float off = h * slideOffset;

                switch (placeListBottomSheetBehavior.getState())
                {
                    case PlaceBottomSheetBehaviour.STATE_DRAGGING:
                        setMapPaddingBottom(off);
                        break;
                    case PlaceBottomSheetBehaviour.STATE_SETTLING:
                        setMapPaddingBottom(off);
                        break;
                    case PlaceBottomSheetBehaviour.STATE_HIDDEN:
                        break;
                    case PlaceBottomSheetBehaviour.STATE_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                }
            }
        });
    }

    private void initLocation()
    {
        iLocation.getLocation(new CarrierMessagingService.ResultCallback<LocationDTO>()
        {
            @Override
            public void onReceiveResult(@NonNull LocationDTO location) throws RemoteException
            {
                if (location.getId() >= 0)
                {
                    selectedLocationDto = location;

                    placeCategoryViewModel.selectConvertedSelected(new CarrierMessagingService.ResultCallback<List<PlaceCategoryDTO>>()
                    {
                        @Override
                        public void onReceiveResult(@NonNull List<PlaceCategoryDTO> result) throws RemoteException
                        {
                            placeCategoryList = result;

                            getActivity().runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    LocalApiPlaceParameter localApiPlaceParameter = new LocalApiPlaceParameter();
                                    localApiPlaceParameter.setX(String.valueOf(location.getLongitude()));
                                    localApiPlaceParameter.setY(String.valueOf(location.getLatitude()));

                                    CoordToAddressUtil.coordToAddress(localApiPlaceParameter, new CarrierMessagingService.ResultCallback<DataWrapper<CoordToAddress>>()
                                    {
                                        @Override
                                        public void onReceiveResult(@NonNull DataWrapper<CoordToAddress> coordToAddressDataWrapper) throws RemoteException
                                        {
                                            getActivity().runOnUiThread(new Runnable()
                                            {
                                                @Override
                                                public void run()
                                                {
                                                    if (coordToAddressDataWrapper.getException() == null)
                                                    {
                                                        coordToAddressResult = coordToAddressDataWrapper.getData();
                                                    } else
                                                    {
                                                        Toast.makeText(getActivity(), coordToAddressDataWrapper.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }

                                                    placesMapFragment = new PlacesMapFragment(TestMapFragment.this);
                                                    placesMapFragment.setIstartActivity(istartActivity);
                                                    placesMapFragment.setSelectedLocationDto(selectedLocationDto);

                                                    placeListFragment = new PlaceListFragment(TestMapFragment.this);
                                                    placeListFragment.setOnClickedPlacesListListener(placesMapFragment);
                                                    placeListFragment.setSelectedLocationDto(selectedLocationDto);
                                                    placeListFragment.setCoordToAddressResult(coordToAddressResult);

                                                    placesMapFragment.setPlaceItemsGetter(placeListFragment);

                                                    FragmentManager fragmentManager = getChildFragmentManager();
                                                    fragmentManager.beginTransaction().add(binding.placesMapFragmentContainer.getId(), placesMapFragment, PlacesMapFragment.TAG)
                                                            .add(binding.placesListFragment.getId(), placeListFragment, PlaceListFragment.TAG)
                                                            .hide(placeListFragment)
                                                            .commit();

                                                }
                                            });

                                        }
                                    });


                                }
                            });
                        }
                    });
                }
            }
        });
    }


    private void setMapPaddingBottom(Float offset)
    {
        //From 0.0 (min) - 1.0 (max) // bsExpanded - bsCollapsed;
        Float maxMapPaddingBottom = 1.0f;
        binding.placesMapFragmentContainer.setPadding(0, 0, 0, Math.round(offset * maxMapPaddingBottom));
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void setBottomSheetState(int state)
    {
        placeListBottomSheetBehavior.setState(state);
    }

    @Override
    public void setPlacesItems(List<PlaceDocuments> placeDocumentsList)
    {
        PlaceItemInMapViewAdapter adapter = new PlaceItemInMapViewAdapter(placeDocumentsList);
        bottomSheetRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onClickedItem(int index)
    {
        bottomSheetRecyclerView.scrollToPosition(index);
    }


    @Override
    public List<PlaceCategoryDTO> getPlaceCategoryList()
    {
        return placeCategoryList;
    }

    @Override
    public void replaceFragment(String tag)
    {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        if (tag.equals(PlaceListFragment.TAG))
        {
            fragmentTransaction.hide(placesMapFragment).show(placeListFragment).addToBackStack(PlaceListFragment.TAG).commit();
        } else if (tag.equals(PlacesMapFragment.TAG))
        {
            fragmentTransaction.hide(placeListFragment).show(placesMapFragment).commit();
        }
    }
}