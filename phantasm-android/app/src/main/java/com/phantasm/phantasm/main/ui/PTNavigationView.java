package com.phantasm.phantasm.main.ui;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MenuItem;

import com.phantasm.phantasm.main.PTMainActivity;

/**
 * Created by Joseph Luns on 2016/2/11.
 */
public class PTNavigationView extends NavigationView {
    private PTMainActivity mActivity;
    private DrawerLayout mDrawerLayout;

    public PTNavigationView(Context context) {
        super(context);
    }

    public PTNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PTNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        initUI();
    }

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;
    }

    private void initUI() {
        if (!(getContext() instanceof PTMainActivity)) return;

        mActivity = (PTMainActivity) getContext();
        setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (mDrawerLayout != null) {
                    mDrawerLayout.closeDrawers();
                }

                return mActivity.onNavigationItemSelected(menuItem);
            }
        });
    }
}
