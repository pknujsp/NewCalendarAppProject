package com.zerodsoft.scheduleweather.navermap.place;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zerodsoft.scheduleweather.databinding.FragmentPlaceInfoWebBinding;

public class PlaceInfoWebFragment extends Fragment {
	private FragmentPlaceInfoWebBinding binding;
	private String placeId;

	public final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
		@Override
		public void handleOnBackPressed() {
			if (binding.webview.canGoBack()) {
				binding.webview.goBack();
			} else {
				getParentFragmentManager().popBackStackImmediate();
			}
		}
	};

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
		Bundle bundle = getArguments();
		placeId = bundle.getString("placeId");
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
	}

	@Override
	public void onDestroy() {
		onBackPressedCallback.remove();
		super.onDestroy();
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

		});

		// 2. WebSettings: 웹뷰의 각종 설정을 정할 수 있다.
		WebSettings ws = binding.webview.getSettings();
		ws.setJavaScriptEnabled(true); // 자바스크립트 사용 허가
		// 3. 웹페이지 호출
		binding.webview.loadUrl(KakaoPlace.WEB_URL + placeId);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}
}