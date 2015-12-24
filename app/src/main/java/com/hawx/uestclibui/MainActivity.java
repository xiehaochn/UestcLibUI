package com.hawx.uestclibui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private Toolbar mtoolBar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private String mTitle;
    private final static String KEY_TITLLE="key_title";
    private ContentFragment mCurrentFragment;
    private LeftMenuFragment mLeftMenuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolBar();
        initView();
        restoreTitle(savedInstanceState);
        FragmentManager fm = getSupportFragmentManager();
        //查找当前显示的Fragment
        mCurrentFragment = (ContentFragment) fm.findFragmentByTag(mTitle);
        if (mCurrentFragment == null) {
            mCurrentFragment = ContentFragment.newInstance(mTitle);
            fm.beginTransaction().add(R.id.id_content_container, mCurrentFragment, mTitle).commit();
        }
        mLeftMenuFragment = (LeftMenuFragment) fm.findFragmentById(R.id.id_left_menu_container);
        if (mLeftMenuFragment == null) {
            mLeftMenuFragment = new LeftMenuFragment();
            fm.beginTransaction().add(R.id.id_left_menu_container, mLeftMenuFragment,"LeftMenuFragment").commit();
        }
        List<Fragment> fragments = fm.getFragments();
        if (fragments != null)

            for (Fragment fragment : fragments) {
                if (fragment == mCurrentFragment || fragment == mLeftMenuFragment) continue;
                fm.beginTransaction().hide(fragment).commit();
            }
        mLeftMenuFragment.setOnMenuItemSelectedListener(new LeftMenuFragment.OnMenuItemSelectedListener() {
            @Override
            public void menuItemSelected(String title) {

                FragmentManager fm = getSupportFragmentManager();
                ContentFragment fragment = (ContentFragment) getSupportFragmentManager().findFragmentByTag(title);
                if (fragment == mCurrentFragment) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    return;
                }

                FragmentTransaction transaction = fm.beginTransaction();
                transaction.hide(mCurrentFragment);

                if (fragment == null) {
                    fragment = ContentFragment.newInstance(title);
                    transaction.add(R.id.id_content_container, fragment, title);
                } else {
                    transaction.show(fragment);
                }
                transaction.commit();

                mCurrentFragment = fragment;
                mTitle = title;
                mtoolBar.setSubtitle(mTitle);
                mDrawerLayout.closeDrawer(Gravity.LEFT);


            }
        });
    }

    private void restoreTitle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mTitle = savedInstanceState.getString(KEY_TITLLE);
        }
        mTitle=getResources().getString(R.string.content_subtitle);
        mtoolBar.setSubtitle(mTitle);
    }

    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerlayout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, mtoolBar, R.string.open, R.string.close);
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

    }

    private void initToolBar() {
        mtoolBar= (Toolbar) findViewById(R.id.toolbar);
        mtoolBar.setTitle(R.string.app_name);
        mtoolBar.setTitleTextAppearance(this,R.style.titleTextStyle);
        mtoolBar.setSubtitleTextAppearance(this,R.style.subtitleTextStyle);
        setSupportActionBar(mtoolBar);
        mtoolBar.setNavigationIcon(R.drawable.ic_navigation);
    }
    public void setmCurrentFragment(String title){

    }


}
