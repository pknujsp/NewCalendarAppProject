package com.zerodsoft.scheduleweather.event.foods.search.search.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.databinding.FragmentFoodRestaurantSearchHistoryBinding;
import com.zerodsoft.scheduleweather.etc.CustomRecyclerViewItemDecoration;
import com.zerodsoft.scheduleweather.event.foods.search.search.adapter.FoodRestaurantSearchHistoryAdapter;
import com.zerodsoft.scheduleweather.navermap.viewmodel.SearchHistoryViewModel;
import com.zerodsoft.scheduleweather.room.dto.SearchHistoryDTO;

import java.util.List;

public class FoodRestaurantSearchHistoryFragment extends Fragment implements OnClickedListItem<SearchHistoryDTO>
{
    public static final String TAG = "FoodRestaurantSearchHistoryFragment";
    private FragmentFoodRestaurantSearchHistoryBinding binding;
    private OnClickedListItem<SearchHistoryDTO> onClickedListItem;
    private SearchHistoryViewModel viewModel;
    private FoodRestaurantSearchHistoryAdapter adapter;


    public FoodRestaurantSearchHistoryFragment(OnClickedListItem<SearchHistoryDTO> onClickedListItem)
    {
        this.onClickedListItem = onClickedListItem;
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
        binding = FragmentFoodRestaurantSearchHistoryBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        binding.notHistory.setVisibility(View.GONE);

        binding.searchHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.searchHistoryRecyclerView.addItemDecoration
                (new CustomRecyclerViewItemDecoration((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics())));

        binding.deleteAllSearchHistory.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (adapter.getItemCount() > 0)
                {
                    viewModel.deleteAll(SearchHistoryDTO.FOOD_RESTAURANT_SEARCH, new CarrierMessagingService.ResultCallback<Boolean>()
                    {
                        @Override
                        public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException
                        {
                            getActivity().runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    adapter.getHistoryList().clear();
                                    adapter.notifyDataSetChanged();
                                }
                            });

                        }
                    });
                }
            }
        });

        adapter = new FoodRestaurantSearchHistoryAdapter(FoodRestaurantSearchHistoryFragment.this);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
        {
            @Override
            public void onChanged()
            {
                super.onChanged();
                if (adapter.getItemCount() > 0)
                {
                    binding.searchHistoryRecyclerView.setVisibility(View.VISIBLE);
                    binding.notHistory.setVisibility(View.GONE);
                } else
                {
                    binding.searchHistoryRecyclerView.setVisibility(View.GONE);
                    binding.notHistory.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount)
            {
                super.onItemRangeInserted(positionStart, itemCount);
                if (adapter.getItemCount() > 0)
                {
                    binding.searchHistoryRecyclerView.setVisibility(View.VISIBLE);
                    binding.notHistory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount)
            {
                super.onItemRangeRemoved(positionStart, itemCount);
                if (adapter.getItemCount() > 0)
                {
                    binding.searchHistoryRecyclerView.setVisibility(View.VISIBLE);
                    binding.notHistory.setVisibility(View.GONE);
                } else
                {
                    binding.searchHistoryRecyclerView.setVisibility(View.GONE);
                    binding.notHistory.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.searchHistoryRecyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(SearchHistoryViewModel.class);
        viewModel.select(SearchHistoryDTO.FOOD_RESTAURANT_SEARCH, new CarrierMessagingService.ResultCallback<List<SearchHistoryDTO>>()
        {
            @Override
            public void onReceiveResult(@NonNull List<SearchHistoryDTO> searchHistoryDTOS) throws RemoteException
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        adapter.setHistoryList(searchHistoryDTOS);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });


    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    public void insertHistory(String value)
    {
        viewModel.insert(SearchHistoryDTO.FOOD_RESTAURANT_SEARCH, value, new CarrierMessagingService.ResultCallback<SearchHistoryDTO>()
        {
            @Override
            public void onReceiveResult(@NonNull SearchHistoryDTO searchHistoryDTO) throws RemoteException
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        adapter.getHistoryList().add(searchHistoryDTO);
                        adapter.notifyItemInserted(adapter.getItemCount());
                    }
                });
            }
        });

    }


    @Override
    public void onClickedListItem(SearchHistoryDTO e)
    {
        onClickedListItem.onClickedListItem(e);
    }

    @Override
    public void deleteListItem(SearchHistoryDTO e, int position)
    {
        viewModel.delete(e.getId(), new CarrierMessagingService.ResultCallback<Boolean>()
        {
            @Override
            public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (aBoolean)
                        {
                            adapter.getHistoryList().remove(position);
                            adapter.notifyItemRemoved(position);
                        }
                    }

                });

            }
        });
    }

}