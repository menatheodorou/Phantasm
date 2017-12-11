package com.phantasm.phantasm.main;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.PTSettings;
import com.phantasm.phantasm.common.ui.PTBaseActivity;
import com.phantasm.phantasm.common.ui.PTBaseFragment;
import com.phantasm.phantasm.main.connect.PTConnectFragment;
import com.phantasm.phantasm.main.create.PTCreateFragment;
import com.phantasm.phantasm.main.flick.PTFlickFragment;
import com.phantasm.phantasm.main.model.PTTabID;
import com.phantasm.phantasm.main.ui.PTNavigationView;

/**
 * Created by Ratan on 7/27/2015.
 */
public class PTMainFragment extends PTBaseFragment {
    private DrawerLayout mDrawerLayout;
    private PTNavigationView mNavigationView;
    private Toolbar mToolbar;
    public static TabLayout mTabLayout;

    public static ViewPager mViewPager;
    public static int TAB_COUNT = 3;
    private PTTabID mLatestTabId = PTTabID.TAB_CREATE;

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mLatestTabId = PTSettings.getInstance(getActivity()).getLatestTab();
    }

    @Override
    protected void initUI() {
        /**
         *Setup the DrawerLayout and NavigationView
         */
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (PTNavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setDrawerLayout(mDrawerLayout);

        mToolbar = (Toolbar) mContentView.findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        ((PTBaseActivity)getActivity()).setSupportActionBar(mToolbar);

        /**
         * Setup Drawer Toggle of the Toolbar
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout,
                toolbar, R.string.app_name,
                R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mTabLayout = (TabLayout) mContentView.findViewById(R.id.tabs);
        mViewPager = (ViewPager) mContentView.findViewById(R.id.viewpager);

        /**
         * Set an Adapter for the View Pager
         */
        mViewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        mViewPager.setOffscreenPageLimit(TAB_COUNT);
        mViewPager.setCurrentItem(mLatestTabId.getValue());

        /**
         * Now, this is a workaround,
         * The setupWithViewPager doesn't works without the runnable.
         * Maybe a Support Library Bug.
         */

        mTabLayout.post(new Runnable() {
            @Override
            public void run() {
                mTabLayout.setupWithViewPager(mViewPager);
            }
        });
    }

    @Override
    protected void reload(Bundle bundle) {

    }

    class MyAdapter extends FragmentStatePagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position) {
            PTTabID tabID = PTTabID.getValue(position);
            switch (tabID) {
                case TAB_CREATE:
                    return new PTCreateFragment();
                case TAB_CONNECT:
                    return new PTConnectFragment();
                case TAB_FLICK:
                    return new PTFlickFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return TAB_COUNT;

        }

        /**
         * This method returns the title of the tab according to the position.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            PTTabID tabID = PTTabID.getValue(position);
            return tabID.getValue(getActivity());
        }
    }

}
