package com.zerodsoft.scheduleweather.event.foods.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentFoodsMainBinding;
import com.zerodsoft.scheduleweather.event.foods.adapter.FoodCategoryAdapter;
import com.zerodsoft.scheduleweather.event.foods.dto.FoodCategoryItem;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedCategoryItem;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.CustomFoodCategoryViewModel;
import com.zerodsoft.scheduleweather.room.dto.CustomFoodCategoryDTO;

import java.util.ArrayList;
import java.util.List;

public class FoodsMainFragment extends Fragment implements OnClickedCategoryItem
{
    public static final String TAG = "FoodsMainFragment";
    private FragmentFoodsMainBinding binding;
    private CustomFoodCategoryViewModel customFoodCategoryViewModel;

    public FoodsMainFragment()
    {

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
        binding = FragmentFoodsMainBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        customFoodCategoryViewModel = new ViewModelProvider(this).get(CustomFoodCategoryViewModel.class);
        //기준 주소 표시
        setCategories();
    }

    private void setCategories()
    {
        customFoodCategoryViewModel.select(new CarrierMessagingService.ResultCallback<List<CustomFoodCategoryDTO>>()
        {
            @Override
            public void onReceiveResult(@NonNull List<CustomFoodCategoryDTO> resultList) throws RemoteException
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        FoodCategoryAdapter foodCategoryAdapter = new FoodCategoryAdapter(getContext(), FoodsMainFragment.this);

                        Context context = getContext();
                        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.hansik), context.getDrawable(R.drawable.cloud_day_icon), true));
                        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.jungsik), context.getDrawable(R.drawable.cloud_day_icon), true));
                        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.illsik), context.getDrawable(R.drawable.cloud_day_icon), true));
                        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.sashimi), context.getDrawable(R.drawable.cloud_day_icon), true));
                        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.yangsik), context.getDrawable(R.drawable.cloud_day_icon), true));
                        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.asian), context.getDrawable(R.drawable.cloud_day_icon), true));
                        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.chicken), context.getDrawable(R.drawable.cloud_day_icon), true));
                        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.fastfood), context.getDrawable(R.drawable.cloud_day_icon), true));
                        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.donkartz), context.getDrawable(R.drawable.cloud_day_icon), true));
                        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.jjim), context.getDrawable(R.drawable.cloud_day_icon), true));
                        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.tang), context.getDrawable(R.drawable.cloud_day_icon), true));
                        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.bunsik), context.getDrawable(R.drawable.cloud_day_icon), true));
                        foodCategoryAdapter.addItem(new FoodCategoryItem(getString(R.string.juk), context.getDrawable(R.drawable.cloud_day_icon), true));

                        if (!resultList.isEmpty())
                        {
                            for (CustomFoodCategoryDTO customFoodCategory : resultList)
                            {
                                foodCategoryAdapter.addItem(new FoodCategoryItem(customFoodCategory.getCategoryName(), null, false));
                            }
                        }

                        binding.foodCategoryGridview.setAdapter(foodCategoryAdapter);
                    }
                });
            }
        });

        /*
          <string name="hansik">한식</string>
    <string name="jungsik">중식</string>
    <string name="illsik">일식</string>
    <string name="sashimi">회</string>
    <string name="yangsik">양식</string>
    <string name="asian">아시안</string>
    <string name="chicken">치킨</string>
    <string name="fastfood">패스트푸드</string>
    <string name="donkartz">돈까스</string>
    <string name="jjim">찜</string>
    <string name="tang">탕</string>
    <string name="bunsik">분식</string>
    <string name="juk">죽</string>
         */
    }

    @Override
    public void onClickedFoodCategory(FoodCategoryItem foodCategoryItem)
    {
        Toast.makeText(getActivity(), foodCategoryItem.getCategoryName(), Toast.LENGTH_SHORT).show();
        //카테고리 리스트 프래그먼트로 이동

    }
}