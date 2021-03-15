package com.zerodsoft.scheduleweather.event.places.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.activity.placecategory.activity.PlaceCategoryActivity;
import com.zerodsoft.scheduleweather.activity.placecategory.viewmodel.PlaceCategoryViewModel;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IstartActivity;
import com.zerodsoft.scheduleweather.databinding.PlacelistFragmentBinding;
import com.zerodsoft.scheduleweather.etc.RecyclerViewItemDecoration;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocation;
import com.zerodsoft.scheduleweather.event.places.adapter.PlaceItemsAdapters;
import com.zerodsoft.scheduleweather.event.places.interfaces.IClickedPlaceItem;
import com.zerodsoft.scheduleweather.event.places.interfaces.IFragment;
import com.zerodsoft.scheduleweather.kakaomap.model.CoordToAddressUtil;
import com.zerodsoft.scheduleweather.kakaomap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.kakaomap.viewmodel.PlacesViewModel;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import java.util.ArrayList;
import java.util.List;

public class PlaceListFragment extends Fragment
{
    /*
    public static final String TAG = "PlaceListFragment";

    private PlacelistFragmentBinding binding;
    private LocationDTO selectedLocationDto;
    private List<PlaceCategoryDTO> placeCategoryList;

    private CoordToAddress coordToAddressResult;
    private List<PlaceCategoryDTO> categories;

    private final IstartActivity istartActivity;
    private IClickedPlaceItem iClickedPlaceItem;

    public PlaceListFragment(IstartActivity istartActivity, IClickedPlaceItem iClickedPlaceItem)
    {
        super();
        this.istartActivity = istartActivity;
        this.iClickedPlaceItem = iClickedPlaceItem;
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

        binding.categorySettingsFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), PlaceCategoryActivity.class);
                istartActivity.startActivityResult(intent, 0);
            }
        });

        binding.scrollview.setOnScrollChangeListener(new View.OnScrollChangeListener()
        {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
            {
                if (scrollY > 0)
                {
                    // 아래로 스크롤
                    binding.categorySettingsFab.hide();
                } else if (scrollY < 0)
                {
                    // 위로 스크롤
                    binding.categorySettingsFab.show();
                }

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

            PlaceItemsAdapters adapter = new PlaceItemsAdapters(iClickedPlaceItem, placeCategory);
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
                    iClickedPlaceItem.onClickedMore(placeCategory, adapter.getCurrentList().snapshot());
                }
            });

            binding.categoryViewlist.addView(categoryView);
        }
    }

    public void refresh()
    {
        initLocation();
    }

     */
}
