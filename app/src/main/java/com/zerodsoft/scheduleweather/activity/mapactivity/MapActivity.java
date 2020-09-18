package com.zerodsoft.scheduleweather.activity.mapactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.zerodsoft.scheduleweather.activity.ScheduleInfoActivity;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.MapController;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.MapFragment;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.SearchFragment;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.SearchResultController;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.SearchResultFragment;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.SearchResultHeaderFragment;
import com.zerodsoft.scheduleweather.recyclerviewadapter.SearchResultViewAdapter;
import com.zerodsoft.scheduleweather.recyclerviewadapter.SearchResultViewPagerAdapter;
import com.zerodsoft.scheduleweather.retrofit.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.LocationSearchResult;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placecategoryresponse.PlaceCategory;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placecategoryresponse.PlaceCategoryDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placekeywordresponse.PlaceKeyword;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placekeywordresponse.PlaceKeywordDocuments;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;

import net.daum.mf.map.api.MapPoint;

import java.util.List;

public class MapActivity extends AppCompatActivity implements MapController.OnDownloadListener, SearchResultViewAdapter.OnItemSelectedListener
{
    /*
    오류 :

     */
    public static int requestCode = 0;
    public static boolean isSelectedLocation = false;
    private final int fragmentViewId;
    private MapController mapController;

    public static final LocalApiPlaceParameter parameters = new LocalApiPlaceParameter();
    public static LocationSearchResult searchResult = new LocationSearchResult();

    public MapActivity()
    {
        fragmentViewId = R.id.map_activity_fragment_layout;
        mapController = new MapController(this);
    }

    @Override
    public void onDownloadedData(int dataType, LocationSearchResult downloadedResult, String fragmentTag)
    {
        // 다운로드된 데이터를 전달
        if (fragmentTag.equals(SearchResultFragment.TAG))
        {
            searchResult = downloadedResult;
            // 초기 검색 결과
            SearchResultController searchResultController = SearchResultController.getInstance(this);
            searchResultController.setDownloadedData();

            MapFragment mapFragment = MapFragment.getInstance(this);
            mapFragment.setPoiItems();

        } else if (fragmentTag.equals(SearchResultViewPagerAdapter.TAG))
        {
            // 해당 페이지에 해당되는 데이터만 가져오기 때문에 새로운 result객체를 저장하면
            // 이전 데이터가 삭제됨
            // 스크롤 후 무한 로딩
            switch (dataType)
            {
                case MapController.TYPE_ADDRESS:
                    AddressResponse addressResponse = searchResult.getAddressResponse();
                    List<AddressResponseDocuments> addressExtraDocuments = downloadedResult.getAddressResponse().getAddressResponseDocumentsList();
                    List<AddressResponseDocuments> addressExistingDocuments = addressResponse.getAddressResponseDocumentsList();

                    for (AddressResponseDocuments document : addressExtraDocuments)
                    {
                        addressExistingDocuments.add(document);
                    }

                    addressResponse.setAddressResponseMeta(downloadedResult.getAddressResponse().getAddressResponseMeta());
                    break;
                case MapController.TYPE_PLACE_CATEGORY:
                    PlaceCategory categoryResponse = searchResult.getPlaceCategoryResponse();
                    List<PlaceCategoryDocuments> keywordExtraDocuments = downloadedResult.getPlaceCategoryResponse().getPlaceCategoryDocuments();
                    List<PlaceCategoryDocuments> keywordExistingDocuments = categoryResponse.getPlaceCategoryDocuments();

                    for (PlaceCategoryDocuments document : keywordExtraDocuments)
                    {
                        keywordExistingDocuments.add(document);
                    }

                    categoryResponse.setPlaceCategoryMeta(downloadedResult.getPlaceCategoryResponse().getPlaceCategoryMeta());
                    break;
                case MapController.TYPE_PLACE_KEYWORD:
                    PlaceKeyword keywordResponse = searchResult.getPlaceKeywordResponse();
                    List<PlaceKeywordDocuments> categoryExtraDocuments = downloadedResult.getPlaceKeywordResponse().getPlaceKeywordDocuments();
                    List<PlaceKeywordDocuments> categoryExistingDocuments = keywordResponse.getPlaceKeywordDocuments();

                    for (PlaceKeywordDocuments document : categoryExtraDocuments)
                    {
                        categoryExistingDocuments.add(document);
                    }

                    keywordResponse.setPlaceKeywordMeta(downloadedResult.getPlaceKeywordResponse().getPlaceKeywordMeta());
                    break;
            }
            SearchResultController searchResultController = SearchResultController.getInstance(this);
            // 스크롤하면서 추가 데이터가 필요한 경우
            searchResultController.setDownloadedExtraData(dataType);

            MapFragment mapFragment = MapFragment.getInstance(this);
            mapFragment.setPoiItems();

        } else if (fragmentTag.equals(MapFragment.TAG))
        {
            // 지정된 주소 검색 완료
            MapFragment mapFragment = MapFragment.getInstance(this);
        }
    }

    @Override
    public void requestData(int dataType, String fragmentTag)
    {
        mapController.selectLocation(dataType, fragmentTag);
    }

    public MapPoint.GeoCoordinate getMapCenterPoint()
    {
        MapFragment mapFragment = MapFragment.getInstance(this);
        return mapFragment.getMapCenterPoint();
    }

    @Override
    public void onItemSelected(int position, int dataType)
    {
        // 위치 검색 결과 리스트에서 하나를 선택한 경우 수행된다
        SearchResultController searchResultController = SearchResultController.getInstance(this);
        searchResultController.setChangeButtonDrawable();

        MapFragment mapFragment = MapFragment.getInstance(this);
        MapFragment.isClickedListItem = true;
        MapFragment.isMain = false;
        SearchResultController.isShowList = false;

        mapFragment.setDataType(dataType).onItemSelected(position);
        searchResultController.setVisibility(SearchResultFragment.TAG, View.GONE);
        getSupportFragmentManager().beginTransaction().show(mapFragment).commit();
    }

    public interface OnBackPressedListener
    {
        void onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        requestCode = intent.getIntExtra("requestCode", 0);
        Bundle bundle = new Bundle();

        switch (requestCode)
        {
            case ScheduleInfoActivity.ADD_LOCATION:
                isSelectedLocation = false;
                break;

            case ScheduleInfoActivity.EDIT_LOCATION:
                isSelectedLocation = true;
                bundle.putParcelable("selectedPlace", intent.getParcelableExtra("place"));
                bundle.putParcelable("selectedAddress", intent.getParcelableExtra("address"));
                break;
        }
        onFragmentChanged(MapFragment.TAG, bundle);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    public void onFragmentChanged(String fragmentTag, Bundle bundle)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (fragmentTag.equals(MapFragment.TAG))
        {
            MapFragment mapFragment = MapFragment.getInstance(this);
            mapFragment.setInitialData(bundle);
            fragmentTransaction.add(fragmentViewId, mapFragment, MapFragment.TAG);
        } else if (fragmentTag.equals(SearchFragment.TAG))
        {
            // Map프래그먼트에서 상단 바를 터치한 경우
            SearchFragment searchFragment = SearchFragment.getInstance(this);
            fragmentTransaction.hide(MapFragment.getInstance(this)).add(fragmentViewId, searchFragment, SearchFragment.TAG)
                    .addToBackStack(SearchFragment.TAG);
        } else if (fragmentTag.equals(SearchResultFragment.TAG))
        {
            SearchResultController searchResultController = SearchResultController.getInstance(this);
            searchResultController.setInitialData(bundle);

            fragmentTransaction.hide(SearchFragment.getInstance(this)).add(fragmentViewId, searchResultController, SearchResultController.TAG)
                    .addToBackStack(SearchResultController.TAG);
        }
        fragmentTransaction.commit();
    }

    public void changeMapOrList(int dataType)
    {
        SearchResultController searchResultController = SearchResultController.getInstance(this);
        searchResultController.setChangeButtonDrawable();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        MapFragment mapFragment = MapFragment.getInstance(this);

        if (SearchResultController.isShowList)
        {
            // to map
            // result header가 원래 자리에 있어야함
            mapFragment.setDataType(dataType).clickedMapButton();
            MapFragment.isClickedChangeButton = true;
            MapFragment.isMain = false;

            searchResultController.setVisibility(SearchResultFragment.TAG, View.GONE);
            fragmentTransaction.show(mapFragment).commit();
            SearchResultController.isShowList = false;
        } else
        {
            // to list
            searchResultController.setVisibility(SearchResultFragment.TAG, View.VISIBLE);
            fragmentTransaction.hide(mapFragment).commit();
            SearchResultController.isShowList = true;
        }
    }

    public void onChoicedLocation(Bundle bundle)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        List<Fragment> fragments = fragmentManager.getFragments();

        for (Fragment fragment : fragments)
        {
            if (fragment instanceof SearchResultFragment || fragment instanceof MapFragment)
            {
                fragmentTransaction.remove(fragment).commit();
                break;
            }
        }

        getIntent().putExtras(bundle);
        setResult(RESULT_OK, getIntent());
        finish();
    }

    @Override
    public void onBackPressed()
    {
        /*
        - mapfragment의 경우

        1. main인 경우
        액티비티 종료

        2. main이 아닌 경우
        2-1. 이전에 선택된 위치의 정보를 표시하는 경우
        back시에 액티비티 종료

        2-2. poiitem을 표시중인 경우
        result list를 재표시

        - resultfragment의 경우
        searchfragment를 재표시

        - searchfragment의 경우
        mapfragment를 재표시

        searchresult header와 list는 인터페이스를 구현하지 않음
         */
        List<Fragment> fragments = getSupportFragmentManager().getFragments();

        for (int i = fragments.size() - 1; i >= 0; i--)
        {
            if (fragments.get(i) instanceof OnBackPressedListener)
            {
                ((OnBackPressedListener) fragments.get(i)).onBackPressed();
                return;
            }
        }

        // MapFragment가 Main인 경우에 수행됨
        setResult(RESULT_CANCELED);
        finish();
    }
}