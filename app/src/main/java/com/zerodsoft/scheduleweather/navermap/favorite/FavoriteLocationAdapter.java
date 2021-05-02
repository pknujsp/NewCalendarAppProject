package com.zerodsoft.scheduleweather.navermap.favorite;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FavoriteLocationItemBinding;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

import java.util.ArrayList;
import java.util.List;

public class FavoriteLocationAdapter extends RecyclerView.Adapter<FavoriteLocationAdapter.ViewHolder>
{
    private List<FavoriteLocationDTO> list = new ArrayList<>();
    private final OnClickedFavoriteItem onClickedFavoriteItem;

    public FavoriteLocationAdapter(OnClickedFavoriteItem onClickedFavoriteItem)
    {
        this.onClickedFavoriteItem = onClickedFavoriteItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_location_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.onBind();
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        FavoriteLocationItemBinding binding;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            binding = FavoriteLocationItemBinding.bind(itemView);
        }

        public void onBind()
        {
            final int position = getBindingAdapterPosition();
            FavoriteLocationDTO favoriteLocationDTO = list.get(position);

            binding.getRoot().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onClickedFavoriteItem.onClickedListItem(favoriteLocationDTO);
                }
            });

            binding.moreButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onClickedFavoriteItem.onClickedEditButton(favoriteLocationDTO);
                }
            });

            String distance = "";

            if (favoriteLocationDTO.getType() == FavoriteLocationDTO.PLACE)
            {
                binding.placeItemLayout.placeName.setText(favoriteLocationDTO.getPlaceName());
                binding.placeItemLayout.addressName.setText(favoriteLocationDTO.getAddress());
                binding.placeItemLayout.distance.setText(distance);

                binding.addressItemLayout.getRoot().setVisibility(View.GONE);
                binding.placeItemLayout.getRoot().setVisibility(View.VISIBLE);
            } else if (favoriteLocationDTO.getType() == FavoriteLocationDTO.ADDRESS)
            {
                binding.addressItemLayout.addressName.setText(favoriteLocationDTO.getAddress());
                binding.addressItemLayout.distance.setText(distance);

                binding.addressItemLayout.getRoot().setVisibility(View.VISIBLE);
                binding.placeItemLayout.getRoot().setVisibility(View.GONE);
            }
        }
    }
}
