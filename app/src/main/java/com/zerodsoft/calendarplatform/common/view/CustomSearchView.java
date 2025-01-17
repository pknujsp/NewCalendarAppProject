package com.zerodsoft.calendarplatform.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

import com.zerodsoft.calendarplatform.R;

import java.util.Objects;

public class CustomSearchView extends LinearLayout {
	private ImageView backBtn;
	private ImageView searchBtn;
	private CustomEditText searchEditText;
	private SearchView.OnQueryTextListener onQueryTextListener;

	public CustomSearchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public CustomSearchView(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(attrs);
	}

	public CustomSearchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(attrs);
	}

	public CustomSearchView(Context context) {
		super(context);
	}

	private void init(AttributeSet attrs) {
		int backBtnVisibility = 0;
		int searchBtnVisibility = 0;
		boolean clickable = false;
		boolean enabled = false;
		boolean focusable = false;
		String hint = null;

		TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.CustomSearchView, 0, 0);
		try {
			backBtnVisibility = a.getInt(R.styleable.CustomSearchView_backBtnVisibility, View.VISIBLE);
			searchBtnVisibility = a.getInt(R.styleable.CustomSearchView_searchBtnVisibility, View.VISIBLE);
			clickable = a.getBoolean(R.styleable.CustomSearchView_clickable, true);
			focusable = a.getBoolean(R.styleable.CustomSearchView_focusable, true);
			enabled = a.getBoolean(R.styleable.CustomSearchView_enabled, true);
			hint = a.getString(R.styleable.CustomSearchView_hint);
		} finally {
			a.recycle();
		}

		final int paddingLR = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, getResources().getDisplayMetrics());
		final int paddingTB = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics());
		setOrientation(HORIZONTAL);
		setPadding(paddingLR, paddingTB, paddingLR, paddingTB);
		setGravity(Gravity.CENTER_VERTICAL);

		backBtn = new ImageView(getContext());
		searchBtn = new ImageView(getContext());

		TypedValue backgroundValue = new TypedValue();
		getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, backgroundValue, true);

		backBtn.setClickable(clickable);
		backBtn.setEnabled(enabled);
		backBtn.setFocusable(focusable);
		backBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.arrow_back_icon));
		backBtn.setBackgroundResource(backgroundValue.resourceId);
		backBtn.setVisibility(backBtnVisibility);

		searchBtn.setClickable(clickable);
		searchBtn.setEnabled(enabled);
		searchBtn.setFocusable(focusable);
		searchBtn.setVisibility(searchBtnVisibility);
		searchBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.search_icon));
		searchBtn.setBackgroundResource(backgroundValue.resourceId);

		searchEditText = new CustomEditText(getContext());
		searchEditText.setHint(hint);
		searchEditText.setClickable(clickable);
		searchEditText.setEnabled(enabled);
		searchEditText.setFocusable(focusable);
		searchEditText.setBackground(null);
		searchEditText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);

		final int btnSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, getResources().getDisplayMetrics());
		final int btnMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics());

		LinearLayout.LayoutParams backBtnParams = new LinearLayout.LayoutParams(btnSize, btnSize);
		backBtnParams.setMargins(0, 0, btnMargin, 0);
		addView(backBtn, backBtnParams);

		LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
		editTextParams.weight = 1;
		editTextParams.gravity = Gravity.CENTER_VERTICAL;
		addView(searchEditText, editTextParams);

		LinearLayout.LayoutParams searchBtnParams = new LinearLayout.LayoutParams(btnSize, btnSize);
		searchBtnParams.setMargins(btnMargin, 0, 0, 0);
		addView(searchBtn, searchBtnParams);

		//searchEditText.setTextCursorDrawable(ContextCompat.getDrawable(getContext(), R.drawable.edittext_cursor));
		searchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (onQueryTextListener != null) {
					onQueryTextListener.onQueryTextSubmit(Objects.requireNonNull(searchEditText.getText()).length() > 0 ? searchEditText.getText().toString() : "");
				}
			}
		});

		searchEditText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
					searchBtn.callOnClick();
					return true;
				}
				return false;
			}
		});
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36f, getResources().getDisplayMetrics()));
	}

	@Override
	public void setOnTouchListener(OnTouchListener l) {
		super.setOnTouchListener(l);
	}

	public void setOnQueryTextListener(SearchView.OnQueryTextListener onQueryTextListener) {
		this.onQueryTextListener = onQueryTextListener;
		searchEditText.setOnQueryTextListener(onQueryTextListener);
	}

	public void setOnBackClickListener(View.OnClickListener onBackClickListener) {
		backBtn.setOnClickListener(onBackClickListener);
	}

	public void setQuery(String query, boolean submit) {
		searchEditText.setText(query);
		if (submit) {
			searchBtn.callOnClick();
		}
	}

	public String getQuery() {
		return searchEditText.getText().length() > 0 ? searchEditText.getText().toString() : "";
	}

}