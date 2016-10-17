package com.lht.paintviewdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by lht on 16/10/17.
 */

public class CustomWebView extends WebView {
    public CustomWebView(Context context) {
        super(context);
        init();
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        getSettings().setJavaScriptEnabled(true);
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    /**
     * 截取webView可视区域的截图
     *
     * @return
     */
    public Bitmap captureWebViewVisibleSize(float scale) {
        setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        setDrawingCacheEnabled(true);
        buildDrawingCache();

        Bitmap bmp = getDrawingCache();
        bmp = Bitmap.createScaledBitmap(bmp,
                (int)(scale * bmp.getWidth()),
                (int)(scale * bmp.getHeight()),
                true);

        destroyDrawingCache();
        setDrawingCacheEnabled(false);
        return bmp;
    }
}
