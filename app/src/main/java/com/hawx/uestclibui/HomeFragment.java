package com.hawx.uestclibui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by Administrator on 2015/11/28.
 */
public class HomeFragment extends Fragment {
    private final static String KEY_TITLE="key_title";

    public static ContentFragment newInstance(String title) {
        ContentFragment fragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title);
        fragment.setArguments(bundle);
        return fragment;
    }
}
