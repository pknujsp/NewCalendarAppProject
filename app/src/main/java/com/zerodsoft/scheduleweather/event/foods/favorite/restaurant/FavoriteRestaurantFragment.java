package com.zerodsoft.scheduleweather.event.foods.favorite.restaurant;

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

public class FavoriteRestaurantFragment extends Fragment implements OnClickedListItem<PlaceDocuments>, OnProgressBarListener
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
        adapter = new FavoriteRestaurantListAdapter(getActivity(), this, favoriteRestaurantViewModel, restaurantListMap);
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
        binding.progressBar.setVisibility(View.GONE);
        adapter.setRestaurantListMap(restaurantListMap);
        adapter.notifyDataSetChanged();
        binding.favoriteRestaurantList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
        {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l)
            {
                return false;
            }
        });
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
        Set<String> keySet = restaurantListMap.keySet();
        Set<String> restaurantIdSetInList = new HashSet<>();

        for (String key : keySet)
        {
            for (int i = 0; i < restaurantListMap.get(key).size(); i++)
            {
                restaurantIdSetInList.add(restaurantListMap.get(key).get(i).getId());
            }
        }

        Set<String> restaurantIdInCloud = FavoriteRestaurantCloud.getInstance().getSet();

        if (!restaurantIdInCloud.equals(restaurantIdSetInList))
        {
            downloadPlaceDocuments();
        }
    }
}