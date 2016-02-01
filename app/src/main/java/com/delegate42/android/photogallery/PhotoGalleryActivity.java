package com.delegate42.android.photogallery;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context) {
        Intent i = new Intent(context,PhotoGalleryActivity.class);
        return i;
    }

    @Override
    protected Fragment createFragment() {
        Fragment fragment = PhotoGalleryFragment.newInstance();
        return fragment;
    }


}
