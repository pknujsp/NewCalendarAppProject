package com.zerodsoft.scheduleweather.navermap.place;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.zerodsoft.scheduleweather.databinding.PlaceInfoDialogFragmentBinding;

public class PlaceInfoFragment extends DialogFragment
{
    public static final String TAG = "PlaceInfoFragment";
    private PlaceInfoDialogFragmentBinding binding;
    private String placeId;

    public PlaceInfoFragment()
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        placeId = bundle.getString("placeId");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = PlaceInfoDialogFragmentBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        initWebView();
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                {
                    if (binding.webview.canGoBack())
                    {
                        binding.webview.goBack();
                    } else
                    {
                        dismiss();
                    }
                    return true;
                }
                return false;

            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        // Copy the alert dialog window attributes to new layout parameter instance
        layoutParams.copyFrom(getDialog().getWindow().getAttributes());

        // Set the width and height for the layout parameters
        // This will bet the width and height of alert dialog
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        // Apply the newly created layout parameters to the alert dialog window
        getDialog().getWindow().setAttributes(layoutParams);
    }

    // 웹뷰 초기화 함수
    private void initWebView()
    {
        // 1. 웹뷰클라이언트 연결 (로딩 시작/끝 받아오기)
        binding.webview.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                super.onPageStarted(view, url, favicon);
                binding.progressIndicator.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);
                binding.progressIndicator.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);
                return true;
            }
        });

        binding.webview.setOnScrollChangeListener(new View.OnScrollChangeListener()
        {
            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
            {
                if (scrollY - oldScrollY > 0)
                {
                    // 아래로 스크롤
                    binding.dismissDialogButton.setVisibility(View.GONE);
                } else if (scrollY - oldScrollY < 0)
                {
                    // 위로 스크롤
                    binding.dismissDialogButton.setVisibility(View.VISIBLE);
                }
            }
        });

        // 2. WebSettings: 웹뷰의 각종 설정을 정할 수 있다.
        WebSettings ws = binding.webview.getSettings();
        ws.setJavaScriptEnabled(true); // 자바스크립트 사용 허가
        // 3. 웹페이지 호출
        binding.webview.loadUrl(KakaoPlace.WEB_URL + placeId);

        binding.dismissDialogButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dismiss();
            }
        });
    }

}
