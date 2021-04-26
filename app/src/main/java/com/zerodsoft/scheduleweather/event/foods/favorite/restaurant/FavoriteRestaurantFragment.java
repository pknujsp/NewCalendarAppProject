package com.zerodsoft.scheduleweather.event.foods.favorite.restaurant;

import android.database.DataSetObserver;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.main.AppMainActivity;
import com.zerodsoft.scheduleweather.calendarview.CalendarsAdapter;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.common.interfaces.OnProgressBarListener;
import com.zerodsoft.scheduleweather.databinding.FragmentFavoriteRestaurantBinding;
import com.zerodsoft.scheduleweather.event.foods.adapter.FoodCategoryFragmentListAdapter;
import com.zerodsoft.scheduleweather.event.foods.categorylist.FoodCategoryTabFragment;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedFavoriteButtonListener;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedRestaurantItem;
import com.zerodsoft.scheduleweather.event.foods.searchlocation.fragment.AddressesListFragment;
import com.zerodsoft.scheduleweather.event.foods.searchlocation.fragment.LocationSearchDialogFragment;
import com.zerodsoft.scheduleweather.event.foods.searchlocation.fragment.PlacesListFragment;
import com.zerodsoft.scheduleweather.event.foods.share.FavoriteRestaurantCloud;
import com.zerodsoft.scheduleweather.kakaomap.fragment.searchresult.adapter.SearchResultListAdapter;
import com.zerodsoft.scheduleweather.kakaomap.model.SearchResultChecker;
import com.zerodsoft.scheduleweather.kakaomap.model.callback.CheckerCallback;
import com.zerodsoft.scheduleweather.kakaomap.place.PlaceInfoFragment;
import com.zerodsoft.scheduleweather.kakaomap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.kakaoplace.retrofit.KakaoPlaceDownloader;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressKakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.scheduleweather.room.dto.CustomFoodMenuDTO;
import com.zerodsoft.scheduleweather.room.dto.FavoriteRestaurantDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FavoriteRestaurantFragment extends Fragment implements OnClickedListItem<PlaceDocuments>, OnProgressBarListener, OnClickedFavoriteButtonListener
{
    public static final String TAG = "FavoriteRestaurantFragment";
    private FragmentFavoriteRestaurantBinding binding;
    private FavoriteRestaurantViewModel favoriteRestaurantViewModel;
    private FavoriteRestaurantListAdapter adapter;

    private ArrayMap<String, List<PlaceDocuments>> restaurantListMap = new ArrayMap<>();
    private List<FavoriteRestaurantDTO> favoriteRestaurantDTOList = new ArrayList<>();
    private Map<String, FavoriteRestaurantDTO> favoriteRestaurantDTOMap = new HashMap<>();

    private final KakaoPlaceDownloader kakaoPlaceDownloader = new KakaoPlaceDownloader(this::setProgressBarVisibility)
    {

        @Override
        public void onResponseSuccessful(KakaoLocalResponse result)
        {

        }

        @Override
        public void onResponseFailed(Exception e)
        {

        }

    };

    private int requestCount = 0;
    private int responseCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentFavoriteRestaurantBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        favoriteRestaurantViewModel = new ViewModelProvider(this).get(FavoriteRestaurantViewModel.class);

        binding.favoriteRestaurantList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
        {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l)
            {
                return false;
            }
        });
        adapter = new FavoriteRestaurantListAdapter(getContext(), this, this, restaurantListMap);
        adapter.registerDataSetObserver(new DataSetObserver()
        {
            @Override
            public void onChanged()
            {
                super.onChanged();
            }

            @Override
            public void onInvalidated()
            {
                super.onInvalidated();
            }
        });
        binding.favoriteRestaurantList.setAdapter(adapter);
        downloadPlaceDocuments();
    }

    private void downloadPlaceDocuments()
    {
        favoriteRestaurantViewModel.select(new CarrierMessagingService.ResultCallback<List<FavoriteRestaurantDTO>>()
        {
            @Override
            public void onReceiveResult(@NonNull List<FavoriteRestaurantDTO> list) throws RemoteException
            {
                favoriteRestaurantDTOMap.clear();
                favoriteRestaurantDTOList.clear();
                if (!restaurantListMap.isEmpty())
                {
                    restaurantListMap.clear();
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }

                favoriteRestaurantDTOList.addAll(list);

                for (FavoriteRestaurantDTO favoriteRestaurantDTO : favoriteRestaurantDTOList)
                {
                    favoriteRestaurantDTOMap.put(favoriteRestaurantDTO.getRestaurantId(), favoriteRestaurantDTO);
                }

                requestCount = list.size();
                responseCount = 0;

                for (FavoriteRestaurantDTO favoriteRestaurant : list)
                {
                    LocalApiPlaceParameter placeParameter = LocalParameterUtil.getPlaceParameterForSpecific(favoriteRestaurant.getRestaurantName()
                            , favoriteRestaurant.getLatitude(), favoriteRestaurant.getLongitude());

                    kakaoPlaceDownloader.getPlacesForSpecific(placeParameter, new JsonDownloader<PlaceKakaoLocalResponse>()
                    {
                        @Override
                        public void onResponseSuccessful(PlaceKakaoLocalResponse result)
                        {
                            //id값과 일치하는 장소 데이터 추출
                            ++responseCount;
                            List<PlaceDocuments> placeDocumentsList = result.getPlaceDocuments();
                            final String restaurantId = favoriteRestaurant.getRestaurantId();

                            int i = 0;
                            for (; i < placeDocumentsList.size(); i++)
                            {
                                if (placeDocumentsList.get(i).getId().equals(restaurantId))
                                {
                                    break;
                                }
                            }

                            createList(placeDocumentsList.get(i));

                            if (requestCount == responseCount)
                            {
                                getActivity().runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        createListView();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onResponseFailed(Exception e)
                        {

                        }
                    });
                }
            }
        });
    }


    /*
    장소를 카테고리 별로 분류해서 리스트 생성
    key : categoryname의 값이 '음식점 > 한식 > 해물, 생선' 방식이므로
    앞 2개로 구분을 짓는다
    '음식점 > 한식'
     */
    private void createList(PlaceDocuments placeDocuments)
    {
        final String category = placeDocuments.getCategoryName().split(" > ")[1];
        if (!restaurantListMap.containsKey(category))
        {
            restaurantListMap.put(category, new ArrayList<>());
        }
        restaurantListMap.get(category).add(placeDocuments);
    }


    private void createListView()
    {
        setProgressBarVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClickedListItem(PlaceDocuments e)
    {
        if (e instanceof PlaceDocuments)
        {
            PlaceInfoFragment placeInfoFragment = new PlaceInfoFragment();
            Bundle bundle = new Bundle();
            bundle.putString("placeId", ((PlaceDocuments) e).getId());
            placeInfoFragment.setArguments(bundle);

            placeInfoFragment.show(getChildFragmentManager(), PlaceInfoFragment.TAG);
        } else
        {

        }
    }

    @Override
    public void deleteListItem(PlaceDocuments e, int position)
    {

    }

    @Override
    public void setProgressBarVisibility(int visibility)
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                binding.progressBar.setVisibility(visibility);
            }
        });
    }

    public void refreshList()
    {
        //restaurantId비교
        //바뀐 부분만 수정
        setProgressBarVisibility(View.VISIBLE);
        Set<String> restaurantIdSetInCloud = FavoriteRestaurantCloud.getInstance().getSet();
        Set<String> restaurantIdSetInList = new HashSet<>();

        final Set<String> keySet = restaurantListMap.keySet();
        for (String key : keySet)
        {
            for (int i = 0; i < restaurantListMap.get(key).size(); i++)
            {
                restaurantIdSetInList.add(restaurantListMap.get(key).get(i).getId());
            }
        }

        Set<String> removedSet = new HashSet<>(restaurantIdSetInList);
        Set<String> addedSet = new HashSet<>(restaurantIdSetInCloud);

        removedSet.removeAll(restaurantIdSetInCloud);
        addedSet.removeAll(restaurantIdSetInList);

        if (removedSet.isEmpty() && addedSet.isEmpty())
        {
            setProgressBarVisibility(View.GONE);
            return;
        }

        if (!removedSet.isEmpty())
        {
            for (String id : removedSet)
            {
                for (String key : keySet)
                {
                    for (int i = restaurantListMap.get(key).size() - 1; i >= 0; i--)
                    {
                        if (restaurantListMap.get(key).get(i).getId().equals(id))
                        {
                            restaurantListMap.get(key).remove(i);
                        }
                    }
                }
            }

            for (int i = restaurantListMap.size() - 1; i >= 0; i--)
            {
                if (restaurantListMap.get(restaurantListMap.keyAt(i)).isEmpty())
                {
                    restaurantListMap.removeAt(i);
                }
            }

            adapter.notifyDataSetChanged();
            setProgressBarVisibility(View.GONE);
        }

        if (!addedSet.isEmpty())
        {
            setProgressBarVisibility(View.VISIBLE);

            favoriteRestaurantViewModel.select(new CarrierMessagingService.ResultCallback<List<FavoriteRestaurantDTO>>()
            {
                @Override
                public void onReceiveResult(@NonNull List<FavoriteRestaurantDTO> favoriteRestaurantDTOS) throws RemoteException
                {
                    Set<FavoriteRestaurantDTO> restaurantDTOSet = new HashSet<>();
                    for (FavoriteRestaurantDTO favoriteRestaurantDTO : favoriteRestaurantDTOS)
                    {
                        if (addedSet.contains(favoriteRestaurantDTO.getRestaurantId()))
                        {
                            restaurantDTOSet.add(favoriteRestaurantDTO);
                        }
                    }

                    requestCount = restaurantDTOSet.size();
                    responseCount = 0;

                    for (FavoriteRestaurantDTO favoriteRestaurant : restaurantDTOSet)
                    {
                        LocalApiPlaceParameter placeParameter = LocalParameterUtil.getPlaceParameterForSpecific(favoriteRestaurant.getRestaurantName()
                                , favoriteRestaurant.getLatitude(), favoriteRestaurant.getLongitude());

                        kakaoPlaceDownloader.getPlacesForSpecific(placeParameter, new JsonDownloader<PlaceKakaoLocalResponse>()
                        {
                            @Override
                            public void onResponseSuccessful(PlaceKakaoLocalResponse result)
                            {
                                //id값과 일치하는 장소 데이터 추출
                                ++responseCount;
                                List<PlaceDocuments> placeDocumentsList = result.getPlaceDocuments();
                                final String restaurantId = favoriteRestaurant.getRestaurantId();

                                int i = 0;
                                for (; i < placeDocumentsList.size(); i++)
                                {
                                    if (placeDocumentsList.get(i).getId().equals(restaurantId))
                                    {
                                        break;
                                    }
                                }

                                createList(placeDocumentsList.get(i));

                                if (requestCount == responseCount)
                                {
                                    getActivity().runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            createListView();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onResponseFailed(Exception e)
                            {

                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void onClickedFavoriteButton(String restaurantId, int groupPosition, int childPosition)
    {
        if (FavoriteRestaurantCloud.getInstance().contains(restaurantListMap.get(restaurantListMap.keyAt(groupPosition)).get(childPosition).getId()))
        {
            favoriteRestaurantViewModel.delete(restaurantListMap.get(restaurantListMap.keyAt(groupPosition)).get(childPosition).getId()
                    , new CarrierMessagingService.ResultCallback<Boolean>()
                    {
                        @Override
                        public void onReceiveResult(@NonNull Boolean isDeleted) throws RemoteException
                        {
                            getActivity().runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    String key = restaurantListMap.keyAt(groupPosition);
                                    restaurantListMap.get(key).remove(childPosition);
                                    if (restaurantListMap.get(key).isEmpty())
                                    {
                                        restaurantListMap.remove(key);
                                    }
                                    adapter.notifyDataSetChanged();
                                    if (restaurantListMap.size() >= 1 && !restaurantListMap.containsKey(key))
                                    {
                                        binding.favoriteRestaurantList.collapseGroup(groupPosition);
                                    }
                                }
                            });

                        }
                    });
        }
    }

    @Override
    public void onClickedFavoriteButton(PlaceDocuments placeDocuments, int position)
    {

    }
}