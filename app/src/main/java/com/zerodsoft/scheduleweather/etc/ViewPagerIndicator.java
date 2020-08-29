package com.zerodsoft.scheduleweather.etc;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.zerodsoft.scheduleweather.R;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerIndicator extends LinearLayout
{
    private Context context;
    private List<ImageView> dotList = new ArrayList<>();

    final private float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.5f, getResources().getDisplayMetrics());

    private Drawable unselectedDrawable;
    private Drawable selectedDrawable;

    public ViewPagerIndicator(Context context)
    {
        super(context);
        this.context = context;
    }

    public ViewPagerIndicator(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        TypedArray tr = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator, 0, 0);

        try
        {
            unselectedDrawable = tr.getDrawable(R.styleable.ViewPagerIndicator_IndicatorUnselectedDotDrawable);
            selectedDrawable = tr.getDrawable(R.styleable.ViewPagerIndicator_IndicatorSelectedDotDrawable);
        } finally
        {
            tr.recycle();
        }
    }

    public void createDot(int position, int length)
    {
        removeAllViews();

        for (int i = 0; i < length; i++)
        {
            ImageView imageView = new ImageView(context);
            imageView.setPadding((int) padding, 0, (int) padding, 0);
            dotList.add(imageView);

            addView(dotList.get(i));
        }

        selectDot(position);
    }

    public void selectDot(int position)
    {
        for (int i = 0; i < dotList.size(); i++)
        {
            if (i == position)
            {
                dotList.get(i).setImageDrawable(selectedDrawable);
            } else
            {
                dotList.get(i).setImageDrawable(unselectedDrawable);
            }
        }
    }

}
