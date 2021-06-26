package com.zerodsoft.scheduleweather.common.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;

import java.lang.reflect.Type;

public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {
	private final int decorationHeight;
	private Context context;

	public RecyclerViewItemDecoration(Context context, int spacingDp) {
		this.context = context;
		decorationHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, spacingDp, context.getResources().getDisplayMetrics());
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		super.getItemOffsets(outRect, view, parent, state);

		if (parent != null && view != null) {
			int itemPosition = parent.getChildAdapterPosition(view);
			int totalCount = parent.getAdapter().getItemCount();

			if (itemPosition >= 0 && itemPosition < totalCount - 1) {
				outRect.bottom = decorationHeight;
			}
		}

	}

}