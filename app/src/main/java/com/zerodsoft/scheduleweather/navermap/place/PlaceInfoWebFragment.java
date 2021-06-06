package com.zerodsoft.scheduleweather.navermap.place;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentPlaceInfoWebBinding;
import com.zerodsoft.scheduleweather.databinding.PlaceInfoDialogFragmentBinding;
import com.zerodsoft.scheduleweather.databinding.PlaceInfoViewBinding;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnSetViewVisibility;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.RestaurantSharedViewModel;

public class PlaceInfoWebFragment extends Fragment {
	private FragmentPlaceInfoWebBinding binding;
	private String placeId;
	private RestaurantSharedViewModel restaurantSharedViewModel;
	private OnSetViewVisibility onSetViewVisibility;

	private View.OnKeyListener onKeyListener = new View.OnKeyListener() {
		@Override
		public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
			if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
				if (binding.webview.canGoBack()) {
					binding.webview.goBack();
				} else {
					requireActivity().getOnBackPressedDispatcher().onBackPressed();
					return false;
				}
			}
			return true;
		}
	};

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		placeId = bundle.getString("placeId");

		restaurantSharedViewModel = new ViewModelProvider(requireActivity()).get(RestaurantSharedViewModel.class);
		onSetViewVisibility = restaurantSharedViewModel.getOnSetViewVisibility();
		onSetViewVisibility.setVisibility(OnSetViewVisibility.ViewType.HEADER, View.GONE);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = FragmentPlaceInfoWebBinding.inflate(inflater, container, false);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initWebView();
		binding.webview.setOnKeyListener(onKeyListener);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		onSetViewVisibility.setVisibility(OnSetViewVisibility.ViewType.HEADER, View.VISIBLE);
	}

	public boolean webCanGoBack() {
		return binding.webview.canGoBack();
	}

	public void webGoBack() {
		binding.webview.goBack();
	}

	// 웹뷰 초기화 함수
	private void initWebView() {
		// 1. 웹뷰클라이언트 연결 (로딩 시작/끝 받아오기)
		binding.webview.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				binding.progressIndicator.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				binding.progressIndicator.setVisibility(View.GONE);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		// 2. WebSettings: 웹뷰의 각종 설정을 정할 수 있다.
		WebSettings ws = binding.webview.getSettings();
		ws.setJavaScriptEnabled(true); // 자바스크립트 사용 허가
		// 3. 웹페이지 호출
		binding.webview.loadUrl(KakaoPlace.WEB_URL + placeId);
	}

}