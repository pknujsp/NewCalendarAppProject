package com.zerodsoft.scheduleweather.event.foods.searchlocation.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.OnProgressBarListener;
import com.zerodsoft.scheduleweather.databinding.FragmentLocationSearchResultBinding;
import com.zerodsoft.scheduleweather.event.foods.searchlocation.adapter.AddressesListAdapter;
import com.zerodsoft.scheduleweather.event.foods.searchlocation.interfaces.OnClickedLocationItem;
import com.zerodsoft.scheduleweather.navermap.util.LocalParameterUtil;
import com.zerodsoft.scheduleweather.navermap.viewmodel.AddressViewModel;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;

public class AddressesListFragment extends Fragment implements OnProgressBarListener
{
    private AddressViewModel viewModel;
    private AddressesListAdapter adapter;
    private FragmentLocationSearchResultBinding binding;

    private final OnClickedLocationItem onClickedLocationItem;
    private final String searchWord;

    public AddressesListFragment(Fragment fragment, String searchWord)
    {
        this.onClickedLocationItem = (OnClickedLocationItem) fragment;
        this.searchWord = searchWord;
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
        binding = FragmentLocationSearchResultBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        binding.searchResultType.setText(getString(R.string.result_address));
        binding.mapSearchResultHeader.setVisibility(View.GONE);

        binding.searchResultRecyclerview.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
        binding.searchResultRecyclerview.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        viewModel = new ViewModelProvider(this).get(AddressViewModel.class);

        requestAddresses(searchWord);
    }

    private void requestAddresses(String searchWord)
    {
        final LocalApiPlaceParameter addressParameter = LocalParameterUtil.getAddressParameter(searchWord, LocalApiPlaceParameter.DEFAULT_SIZE
                , LocalApiPlaceParameter.DEFAULT_PAGE);

        if (KakaoLocalApiCategoryUtil.isCategory(searchWord))
        {
            binding.progressBar.setVisibility(View.GONE);
        } else
        {
            adapter = new AddressesListAdapter(onClickedLocationItem);
            binding.searchResultRecyclerview.setAdapter(adapter);
            viewModel.init(addressParameter, this);
            viewModel.getPagedListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<AddressResponseDocuments>>()
            {
                @Override
                public void onChanged(PagedList<AddressResponseDocuments> addressResponseDocuments)
                {
                    adapter.submitList(addressResponseDocuments);
                }
            });
        }
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
}