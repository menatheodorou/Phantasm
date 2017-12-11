package com.phantasm.phantasm.main;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kevinfinn on 5/13/15.
 */
public abstract class BasePagerAdapter extends PagerAdapter {
    public Context context = null;

    public BasePagerAdapter(Context context) {
        super();
        this.context = context;
    }

    protected Context getContext() {
        return context;
    }

    @Override
    public abstract Object instantiateItem(final ViewGroup container, final int position);

    @Override
    public abstract int getCount();

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public abstract Object getItemAtPosition(int position);


}
