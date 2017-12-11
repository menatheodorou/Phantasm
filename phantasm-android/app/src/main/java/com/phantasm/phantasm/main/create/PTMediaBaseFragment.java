package com.phantasm.phantasm.main.create;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.ui.PTBaseFragment;

import java.util.Locale;

public abstract class PTMediaBaseFragment extends PTBaseFragment {
    protected ProgressDialog progressDialog;

    protected ProgressDialog getProgressDialog(@NonNull String title ){
        if(progressDialog == null)
            progressDialog = new ProgressDialog(getActivity());

        progressDialog.setTitle(title);
        progressDialog.setIndeterminate(true);
        return progressDialog;
    }

    protected void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    final String TAG = this.getClass().getSimpleName();

    /**
     * Method to make json array request where response starts with [
     */
    public abstract void makeJsonRequest(@Nullable String searchquery);
    public abstract void refreshViewPager();
    public abstract PlaceholderFragment getNewPlaceholderFragment(int position);

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class
            // below).
            Fragment fragment;
            fragment = getNewPlaceholderFragment(position + 1);
            return fragment;

        }

        @Override
        public int getCount() {
            // 4 fragments is needed for loop
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private int fragmentlayout = 0;

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_SECTION_LAYOUT = "fragment_layout";

        /**
         * Returns a new INSTANCE of this fragment for the given section number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            if (savedInstanceState != null) {
                // Restore last state for checked position.
                fragmentlayout = savedInstanceState.getInt(ARG_SECTION_LAYOUT, fragmentlayout);
            }
        }

        @Override
        public void onStart() {
            super.onStart();
        }
    }

    @Override
    public void onStop(){
        super.onStop();

        if(progressDialog!=null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }

    }
}
