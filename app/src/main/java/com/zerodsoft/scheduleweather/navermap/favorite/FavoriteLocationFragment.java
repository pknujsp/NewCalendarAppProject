package com.zerodsoft.scheduleweather.navermap.favorite;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.naver.maps.map.overlay.Marker;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.common.interfaces.OnBackPressedCallbackController;
import com.zerodsoft.scheduleweather.databinding.FragmentFavoriteLocationBinding;
import com.zerodsoft.scheduleweather.etc.FragmentStateCallback;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteLocationViewModel;
import com.zerodsoft.scheduleweather.event.places.interfaces.PoiItemOnClickListener;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.PoiItemType;
import com.zerodsoft.scheduleweather.navermap.fragment.search.LocationSearchFragment;
import com.zerodsoft.scheduleweather.navermap.fragment.searchresult.LocationSearchResultFragment;
import com.zerodsoft.scheduleweather.navermap.interfaces.BottomSheetController;
import com.zerodsoft.scheduleweather.navermap.interfaces.FavoriteLocationsListener;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapData;
import com.zerodsoft.scheduleweather.navermap.interfaces.IMapPoint;
import com.zerodsoft.scheduleweather.navermap.interfaces.SearchBarController;
import com.zerodsoft.scheduleweather.navermap.interfaces.SearchFragmentController;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteLocationQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.SneakyThrows;

public class FavoriteLocationFragment extends Fragment implements OnBackPressedCallbackController, OnClickedFavoriteItem, FavoriteLocationQuery
        , SharedPreferences.OnSharedPreferenceChangeListener
{
    private FragmentFavoriteLocationBinding binding;
    public static final String TAG = "FavoriteLocationFragment";

    private final FavoriteLocationsListener favoriteLocationsListener;
    private final PoiItemOnClickListener<Marker> poiItemOnClickListener;
    private final OnBackPressedCallbackController mainFragmentOnBackPressedCallbackController;
    private final BottomSheetController bottomSheetController;
    private final IMapData iMapData;

    private FavoriteLocationViewModel favoriteLocationViewModel;
    private FavoriteLocationAdapter favoriteLocationAdapter;

    private SharedPreferences sharedPreferences;

    public FavoriteLocationFragment(FavoriteLocationsListener favoriteLocationsListener, OnBackPressedCallbackController onBackPressedCallbackController
            , BottomSheetController bottomSheetController
            , PoiItemOnClickListener<Marker> poiItemOnClickListener, IMapData iMapData)
    {
        this.mainFragmentOnBackPressedCallbackController = onBackPressedCallbackController;
        this.bottomSheetController = bottomSheetController;
        this.favoriteLocationsListener = favoriteLocationsListener;
        this.poiItemOnClickListener = poiItemOnClickListener;
        this.iMapData = iMapData;
    }

    public FavoriteLocationViewModel getFavoriteLocationViewModel()
    {
        return favoriteLocationViewModel;
    }

    public OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true)
    {
        @Override
        public void handleOnBackPressed()
        {
            getParentFragmentManager().popBackStack();
        }
    };

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        super.onHiddenChanged(hidden);

        if (hidden)
        {
            removeOnBackPressedCallback();
            if (getParentFragmentManager().getBackStackEntryCount() == 0)
            {
                mainFragmentOnBackPressedCallbackController.addOnBackPressedCallback();
            }
            bottomSheetController.setStateOfBottomSheet(BottomSheetType.FAVORITE_LOCATIONS, BottomSheetBehavior.STATE_COLLAPSED);
        } else
        {
            addOnBackPressedCallback();
            if (getParentFragmentManager().getBackStackEntryCount() == 0)
            {
                mainFragmentOnBackPressedCallbackController.removeOnBackPressedCallback();
            }
            bottomSheetController.setStateOfBottomSheet(BottomSheetType.FAVORITE_LOCATIONS, BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentFavoriteLocationBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        boolean showFavoriteLocationsMarkersOnMap = App.isPreference_key_show_favorite_locations_markers_on_map();
        binding.switchShowFavoriteLocationsMarkerOnMap.setChecked(showFavoriteLocationsMarkersOnMap);

        binding.switchShowFavoriteLocationsMarkerOnMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getString(R.string.preference_key_show_favorite_locations_markers_on_map), isChecked);
                App.setPreference_key_show_favorite_locations_markers_on_map(isChecked);
                editor.commit();

                iMapData.showPoiItems(PoiItemType.FAVORITE, isChecked);
            }
        });

        favoriteLocationViewModel = new ViewModelProvider(this).get(FavoriteLocationViewModel.class);
        binding.favoriteLocationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.favoriteLocationRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        favoriteLocationAdapter = new FavoriteLocationAdapter(this);
        binding.favoriteLocationRecyclerView.setAdapter(favoriteLocationAdapter);

        setFavoriteLocationList();
    }

    private void setFavoriteLocationList()
    {
        favoriteLocationViewModel.select(FavoriteLocationDTO.ONLY_FOR_MAP, new CarrierMessagingService.ResultCallback<List<FavoriteLocationDTO>>()
        {
            @Override
            public void onReceiveResult(@NonNull List<FavoriteLocationDTO> list) throws RemoteException
            {
                favoriteLocationAdapter.setList(list);
                requireActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        favoriteLocationsListener.createFavoriteLocationsPoiItems(list);
                        iMapData.showPoiItems(PoiItemType.FAVORITE, App.isPreference_key_show_favorite_locations_markers_on_map());
                        favoriteLocationAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    public void refresh()
    {
        //추가,삭제 된 경우만 동작시킨다
        favoriteLocationViewModel.select(FavoriteLocationDTO.ONLY_FOR_MAP, new CarrierMessagingService.ResultCallback<List<FavoriteLocationDTO>>()
        {
            @Override
            public void onReceiveResult(@NonNull List<FavoriteLocationDTO> newList) throws RemoteException
            {
                Set<FavoriteLocationDTO> currentSet = new HashSet<>(favoriteLocationAdapter.getList());
                Set<FavoriteLocationDTO> newSet = new HashSet<>(newList);

                Set<FavoriteLocationDTO> addedSet = new HashSet<>(newSet);
                Set<FavoriteLocationDTO> removedSet = new HashSet<>(currentSet);

                addedSet.removeAll(currentSet);
                removedSet.removeAll(newSet);

                if (!addedSet.isEmpty() || !removedSet.isEmpty())
                {
                    if (!addedSet.isEmpty())
                    {
                        favoriteLocationAdapter.getList().addAll(addedSet);
                    }

                    if (!removedSet.isEmpty())
                    {
                        favoriteLocationAdapter.getList().removeAll(removedSet);
                    }

                    requireActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            favoriteLocationAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void addOnBackPressedCallback()
    {
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public void removeOnBackPressedCallback()
    {
        onBackPressedCallback.remove();
    }

    @Override
    public void onClickedListItem(FavoriteLocationDTO e)
    {

    }

    @Override
    public void deleteListItem(FavoriteLocationDTO e, int position)
    {

    }

    @Override
    public void onClickedEditButton(FavoriteLocationDTO e)
    {

    }

    @Override
    public void onClickedShareButton(FavoriteLocationDTO e)
    {

    }

    @Override
    public void insert(FavoriteLocationDTO favoriteLocationDTO, CarrierMessagingService.ResultCallback<FavoriteLocationDTO> callback)
    {
        favoriteLocationViewModel.insert(favoriteLocationDTO, new CarrierMessagingService.ResultCallback<FavoriteLocationDTO>()
        {
            @Override
            public void onReceiveResult(@NonNull FavoriteLocationDTO insertedFavoriteLocationDTO) throws RemoteException
            {
                requireActivity().runOnUiThread(new Runnable()
                {
                    @SneakyThrows
                    @Override
                    public void run()
                    {
                        callback.onReceiveResult(insertedFavoriteLocationDTO);
                        favoriteLocationsListener.addFavoriteLocationsPoiItem(insertedFavoriteLocationDTO);
                    }
                });
            }
        });
    }

    @Override
    public void select(Integer type, CarrierMessagingService.ResultCallback<List<FavoriteLocationDTO>> callback)
    {
        favoriteLocationViewModel.select(type, new CarrierMessagingService.ResultCallback<List<FavoriteLocationDTO>>()
        {
            @Override
            public void onReceiveResult(@NonNull List<FavoriteLocationDTO> favoriteLocationDTOS) throws RemoteException
            {
                requireActivity().runOnUiThread(new Runnable()
                {
                    @SneakyThrows
                    @Override
                    public void run()
                    {
                        callback.onReceiveResult(favoriteLocationDTOS);
                    }
                });
            }
        });
    }

    @Override
    public void select(Integer type, Integer id, CarrierMessagingService.ResultCallback<FavoriteLocationDTO> callback)
    {
        favoriteLocationViewModel.select(type, id, new CarrierMessagingService.ResultCallback<FavoriteLocationDTO>()
        {
            @Override
            public void onReceiveResult(@NonNull FavoriteLocationDTO favoriteLocationDTO) throws RemoteException
            {
                requireActivity().runOnUiThread(new Runnable()
                {
                    @SneakyThrows
                    @Override
                    public void run()
                    {
                        callback.onReceiveResult(favoriteLocationDTO);
                    }
                });
            }
        });
    }

    @Override
    public void delete(Integer id, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        favoriteLocationViewModel.select(null, id, new CarrierMessagingService.ResultCallback<FavoriteLocationDTO>()
        {
            @Override
            public void onReceiveResult(@NonNull FavoriteLocationDTO favoriteLocationDTO) throws RemoteException
            {
                favoriteLocationViewModel.delete(id, new CarrierMessagingService.ResultCallback<Boolean>()
                {
                    @Override
                    public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException
                    {
                        requireActivity().runOnUiThread(new Runnable()
                        {
                            @SneakyThrows
                            @Override
                            public void run()
                            {
                                callback.onReceiveResult(aBoolean);
                                favoriteLocationsListener.removeFavoriteLocationsPoiItem(favoriteLocationDTO);
                            }
                        });
                    }
                });
            }
        });


    }

    @Override
    public void deleteAll(Integer type, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        favoriteLocationViewModel.deleteAll(type, new CarrierMessagingService.ResultCallback<Boolean>()
        {
            @Override
            public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException
            {
                requireActivity().runOnUiThread(new Runnable()
                {
                    @SneakyThrows
                    @Override
                    public void run()
                    {
                        callback.onReceiveResult(aBoolean);
                    }
                });
            }
        });
    }

    @Override
    public void contains(Integer type, String placeId, String address, String latitude, String longitude, CarrierMessagingService.ResultCallback<FavoriteLocationDTO> callback)
    {
        favoriteLocationViewModel.contains(type, placeId, address, latitude, longitude, new CarrierMessagingService.ResultCallback<FavoriteLocationDTO>()
        {
            @Override
            public void onReceiveResult(@NonNull FavoriteLocationDTO favoriteLocationDTO) throws RemoteException
            {
                requireActivity().runOnUiThread(new Runnable()
                {
                    @SneakyThrows
                    @Override
                    public void run()
                    {
                        callback.onReceiveResult(favoriteLocationDTO);
                    }
                });
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {

    }
}