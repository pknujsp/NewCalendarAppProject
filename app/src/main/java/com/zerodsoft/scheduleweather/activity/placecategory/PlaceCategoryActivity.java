package com.zerodsoft.scheduleweather.activity.placecategory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.ActivityPlaceCategoryBinding;

public class PlaceCategoryActivity extends AppCompatActivity implements PlaceCategoryAdapter.OnStartDragListener
{
    private ItemTouchHelper itemTouchHelper;
    private PlaceCategoryAdapter adapter;
    private ActivityPlaceCategoryBinding binding;

    @Override
    public void onStartDrag(PlaceCategoryAdapter.CategoryViewHolder viewHolder)
    {
        itemTouchHelper.startDrag(viewHolder);
    }

    public class ItemTouchHelperCallback extends ItemTouchHelper.Callback
    {
        private final OnItemMoveListener onItemMoveListener;

        public ItemTouchHelperCallback(OnItemMoveListener onItemMoveListener)
        {
            this.onItemMoveListener = onItemMoveListener;
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder)
        {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target)
        {
            // 움직이면 어떻게 할것인지 구현
            onItemMoveListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
        {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_place_category);
        binding.placeCategoryList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        adapter = new PlaceCategoryAdapter(null, this);

        ItemTouchHelperCallback itemTouchHelperCallback = new ItemTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(binding.placeCategoryList);

        binding.placeCategoryList.setAdapter(adapter);
    }

    /*
    카테고리 데이터를 가져온다, DB에서도 가져옴
     */
    private void init()
    {

    }
}