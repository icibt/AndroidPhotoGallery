package com.delegate42.android.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

/**
 * Created by Ici on 31.1.2016.
 */
public class PhotoPageActivity extends SingleFragmentActivity {
    private WebViewHistory mWebViewHistory;

    public static Intent newIntent(Context c,Uri photoPageUri) {
        Intent i = new Intent(c, PhotoPageActivity.class);
        i.setData(photoPageUri);
        return i;
    }
    @Override
    protected Fragment createFragment() {
        Fragment fragment = PhotoPageFragment.newInstance(getIntent().getData());
        mWebViewHistory = (WebViewHistory) fragment;
        return fragment;
    }

    @Override
    public void onBackPressed() {
        if(!mWebViewHistory.canGoBack()) {
            super.onBackPressed();
        }
        mWebViewHistory.goBack();
    }
}
