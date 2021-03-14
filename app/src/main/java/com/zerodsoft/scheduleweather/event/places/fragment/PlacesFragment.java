package com.zerodsoft.scheduleweather.event.places.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.activity.placecategory.activity.PlaceCategoryActivity;
import com.zerodsoft.scheduleweather.activity.placecategory.viewmodel.PlaceCategoryViewModel;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IstartActivity;
import com.zerodsoft.scheduleweather.databinding.PlaceCategoriesFragmentBinding;
import com.zerodsoft.scheduleweather.etc.RecyclerViewItemDecoration;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocation;
import com.zerodsoft.scheduleweather.event.places.adapter.PlaceItemsAdapters;
import com.zerodsoft.scheduleweather.event.places.interfaces.IFragment;
import com.zerodsoft.scheduleweather.kakaomap.model.CoordToAddressUtil;
import com.zerodsoft.scheduleweather.kakaomap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.event.places.interfaces.IClickedPlaceItem;
import com.zerodsoft.scheduleweather.event.places.interfaces.IPlacesFragment;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import java.util.List;

public class PlacesFragment extends Fragment implements IPlacesFragment, DialogInterface.OnDismissListener, DefaultMapFragment.FullScreenButtonListener, IClickedPlaceItem
{
    public static final String TAG = "PlacesFragment";
    // 이벤트의 위치 값으로 정확한 위치를 지정하기 위해 위치 지정 액티비티 생성(카카오맵 검색 값 기반)
    // 맵 프래그먼트와 카테고리 별 데이타 목록 프래그먼트로 분리
    private final ILocation iLocation;
    private final IstartActivity istartActivity;
    private final IFragment iFragment;
    private IClickedPlaceItem iClickedPlaceItem;

    private LocationDTO selectedLocationDto;
    private PlaceCategoriesFragmentBinding binding;
    private List<PlaceCategoryDTO> placeCategoryList;
    private PlaceCategoryViewModel placeCategoryViewModel;
    private OnBackPressedCallback onBackPressedCallback;

    private CoordToAddress coordToAddressResult;
    private List<PlaceCategoryDTO> categories;
    private CustomFragmentContainerView customFragmentContainerView;

    public PlacesFragment(ILocation iLocation, IstartActivity istartActivity, IFragment iFragment)
    {
        this.iLocation = iLocation;
        this.istartActivity = istartActivity;
        this.iFragment = iFragment;
    }

    public void setiClickedPlaceItem(IClickedPlaceItem iClickedPlaceItem)
    {
        this.iClickedPlaceItem = iClickedPlaceItem;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = PlaceCategoriesFragmentBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        onBackPressedCallback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                requireActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        customFragmentContainerView = new CustomFragmentContainerView(getContext());
        customFragmentContainerView.setId(R.id.map_fragment_container_view);

        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.place_item_cardview_height));
        customFragmentContainerView.setLayoutParams(layoutParams);
        customFragmentContainerView.setBackground(getResources().getDrawable(R.drawable.sky_background, null));
        customFragmentContainerView.setPadding(padding, padding, padding, padding);
        customFragmentContainerView.setClickable(true);

        customFragmentContainerView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showMap();
            }
        });

        binding.locationInfoLayout.addView(customFragmentContainerView);

        placeCategoryViewModel = new ViewModelProvider(this).get(PlaceCategoryViewModel.class);
        binding.categorySettingsFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), PlaceCategoryActivity.class);
                istartActivity.startActivityResult(intent, 0);
            }
        });

        initLocation();
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
                                    if (!placeCategoryList.isEmpty())
                                    {
                                        binding.notSelectedCategory.setVisibility(View.GONE);

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

                                                            if (location.getPlaceName() != null)
                                                            {
                                                                //장소와 주소 표기
                                                                binding.locationName.setText(location.getPlaceName());
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
                                                        } else
                                                        {
                                                            Toast.makeText(getActivity(), coordToAddressDataWrapper.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                });

                                            }
                                        });
                                    }
                                    FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

                                    fragmentTransaction.add(customFragmentContainerView.getId(), new SelectedLocationMapFragment(selectedLocationDto), SelectedLocationMapFragment.TAG).commit();

                                    categories = placeCategoryList;
                                    makeCategoryListView();
                                }
                            });

                        }
                    });
                }
            }
        });
    }

    private void makeCategoryListView()
    {
        binding.categoryViewlist.removeAllViews();
        LayoutInflater layoutInflater = getLayoutInflater();

        for (PlaceCategoryDTO placeCategory : categories)
        {
            LinearLayout categoryView = (LinearLayout) layoutInflater.inflate(R.layout.place_category_view, null);

            RecyclerView itemRecyclerView = (RecyclerView) categoryView.findViewById(R.id.map_category_itemsview);
            itemRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
            itemRecyclerView.addItemDecoration(new RecyclerViewItemDecoration((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics())));

            LocalApiPlaceParameter placeParameter = LocalParameterUtil.getPlaceParameter(placeCategory.getCode(), selectedLocationDto.getLatitude(),
                    selectedLocationDto.getLongitude(), LocalApiPlaceParameter.DEFAULT_SIZE, LocalApiPlaceParameter.DEFAULT_PAGE,
                    LocalApiPlaceParameter.SEARCH_CRITERIA_SORT_TYPE_ACCURACY);

            placeParameter.setRadius(App.getPreference_key_radius_range());

            ((TextView) categoryView.findViewById(R.id.map_category_name)).setText(placeCategory.getDescription());

            PlaceItemsAdapters adapter = new PlaceItemsAdapters(PlacesFragment.this, placeCategory);
            itemRecyclerView.setAdapter(adapter);

            PlacesViewModel viewModel = new ViewModelProvider(getActivity()).get(PlacesViewModel.class);
            viewModel.init(placeParameter);
            viewModel.getPagedListMutableLiveData().observe(getActivity(), new Observer<PagedList<PlaceDocuments>>()
            {
                @Override
                public void onChanged(PagedList<PlaceDocuments> placeDocuments)
                {
                    adapter.submitList(placeDocuments);
                }
            });


            ((Button) categoryView.findViewById(R.id.map_category_more)).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onClickedMore(placeCategory, adapter.getCurrentList().snapshot());
                }
            });

            binding.categoryViewlist.addView(categoryView);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }


    @Override
    public void onDetach()
    {
        super.onDetach();
        onBackPressedCallback.remove();
    }


    @Override
    public LifecycleOwner getLifeCycleOwner()
    {
        return PlacesFragment.this;
    }

    @Override
    public ViewModelStoreOwner getViewModelStoreOwner()
    {
        return PlacesFragment.this;
    }

    @Override
    public Fragment getFragment()
    {
        return this;
    }

    public void refresh()
    {
        initLocation();
    }

    @Override
    public void onClicked()
    {
        showMap();
    }

    private void showMap()
    {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(SelectedLocationMapFragment.TAG);
        getChildFragmentManager().beginTransaction().remove(fragment).commitNow();

        if (DefaultMapDialogFragment.getInstance() == null)
        {
            DefaultMapDialogFragment.newInstance();
        }
        DefaultMapFragment.newInstance(PlacesFragment.this, selectedLocationDto);
        DefaultMapDialogFragment.getInstance().show(getChildFragmentManager(), DefaultMapDialogFragment.TAG);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface)
    {
        DefaultMapFragment.close();
        getChildFragmentManager().beginTransaction().add(customFragmentContainerView.getId(), new SelectedLocationMapFragment(selectedLocationDto), SelectedLocationMapFragment.TAG).commitNow();
    }

    @Override
    public void onClickedItem(int index, PlaceCategoryDTO placeCategory, List<PlaceDocuments> placeDocumentsList)
    {
/*
       // iFragment.replaceFragment(MorePlacesFragment.TAG);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        // categoryButton.setText(placeCategory.getDescription());
        createPlacesPoiItems(placeDocumentsList);
        selectPoiItem(index);
 */
        showMap();
        //맵에서 해당 아이템 카테고리를 선택하고, 선택된 아이템을 보여준다
    }

    @Override
    public void onClickedMore(PlaceCategoryDTO placeCategory, List<PlaceDocuments> placeDocumentsList)
    {
/*
        // iFragment.replaceFragment(MorePlacesFragment.TAG);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        //  categoryButton.setText(placeCategory.getDescription());
        createPlacesPoiItems(placeDocumentsList);
        mapView.fitMapViewAreaToShowAllPOIItems();
 */
        showMap();
        //해당 카테고리의 모든 아이템을 보여준다
    }

    static final class CustomFragmentContainerView extends FrameLayout
    {
        public CustomFragmentContainerView(@NonNull Context context)
        {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev)
        {
            getParent().requestDisallowInterceptTouchEvent(true);
            return super.dispatchTouchEvent(ev);
        }
    }
}
