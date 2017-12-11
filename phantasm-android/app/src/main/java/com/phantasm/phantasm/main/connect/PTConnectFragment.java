package com.phantasm.phantasm.main.connect;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;

import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.PTPreference;
import com.phantasm.phantasm.main.PTTabBaseFragment;
import com.phantasm.phantasm.main.connect.mychannel.PTMyChannelListView;
import com.phantasm.phantasm.main.connect.mychannel.PTSigninEmailFragment;
import com.phantasm.phantasm.main.connect.mychannel.PTSigninFragment;
import com.phantasm.phantasm.main.connect.mychannel.PTSigninPhoneNumberFragment;
import com.phantasm.phantasm.main.connect.mychannel.PTSignupCodeFragment;
import com.phantasm.phantasm.main.connect.mychannel.PTSignupFragment;
import com.phantasm.phantasm.main.connect.mychannel.PTVerifyCodeFragment;
import com.phantasm.phantasm.main.connect.mychannel.PTVerifyPhoneNumberFragment;
import com.phantasm.phantasm.main.connect.spotlight.PTChannelDescriptionFragment;
import com.phantasm.phantasm.main.connect.spotlight.PTSpotlightView;
import com.phantasm.phantasm.main.model.PTFeaturedVideoObject;
import com.phantasm.phantasm.main.model.PTTabID;

public class PTConnectFragment extends PTTabBaseFragment {

    public final static String FRAGMENT_TAG_CDV = "channel_description_view";
    public final static String FRAGMENT_TAG_SIGNIN = "signin";
    public final static String FRAGMENT_TAG_SIGNIN_PHONE_NUMBER = "signin_phone_number";
    public final static String FRAGMENT_TAG_SIGNIN_EMAIL = "signin_email";
    public final static String FRAGMENT_TAG_SIGNUP = "signup";
    public final static String FRAGMENT_TAG_SIGNUP_CODE = "signup_code";
    public final static String FRAGMENT_TAG_VERIFY_CODE = "verify_code";
    public final static String FRAGMENT_TAG_VERIFY_PHONE_NUMBER = "verify_phone_number";

    private SearchView mSearchView;
    private boolean mSearchClosing = false;

    private PTSpotlightView mSpotlightView;
    private PTChannelDescriptionFragment mCdvFragment;

    private PTMyChannelListView mMyChannelListView;
    private PTSigninFragment mSigninFragment;
    private PTSigninPhoneNumberFragment mSigninPhoneNumberFragment;
    private PTSigninEmailFragment mSigninEmailFragment;
    private PTSignupFragment mSignupFragment;
    private PTSignupCodeFragment mSignupCodeFragment;
    private PTVerifyCodeFragment mVerifyCodeFragment;
    private PTVerifyPhoneNumberFragment mVerifyPhoneNumberFragment;

    private String mCurrentFragmentTag;

    public PTConnectFragment() {
        super(PTTabID.TAB_CONNECT);
    }

    public static PTConnectFragment newInstance() {
        return new PTConnectFragment();
    }

    @Override
    public int getContentLayout() {
        return R.layout.fragment_connect;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mCdvFragment = PTChannelDescriptionFragment.newInstance();
        mCdvFragment.setParentFragment(this);

        mSigninFragment = PTSigninFragment.newInstance();
        mSigninFragment.setParentFragment(this);
        mSigninPhoneNumberFragment = PTSigninPhoneNumberFragment.newInstance();
        mSigninPhoneNumberFragment.setParentFragment(this);
        mSigninEmailFragment = PTSigninEmailFragment.newInstance();
        mSigninEmailFragment.setParentFragment(this);
        mSignupFragment = PTSignupFragment.newInstance();
        mSignupFragment.setParentFragment(this);
        mSignupCodeFragment = PTSignupCodeFragment.newInstance();
        mSignupCodeFragment.setParentFragment(this);
        mVerifyCodeFragment = PTVerifyCodeFragment.newInstance();
        mVerifyCodeFragment.setParentFragment(this);
        mVerifyPhoneNumberFragment = PTVerifyPhoneNumberFragment.newInstance();
        mVerifyPhoneNumberFragment.setParentFragment(this);
    }

    @Override
    public void initUI() {
        initActionBar();

        mSpotlightView = (PTSpotlightView) findViewById(R.id.sv_spotlight);
        mSpotlightView.setParentFragment(this);
        mMyChannelListView = (PTMyChannelListView) findViewById(R.id.lv_mychannel);
        mMyChannelListView.setParentFragment(this);

        updateUI();
    }

    @Override
    public boolean onBackPressed() {
        int nCnt = mFragmentManager.getBackStackEntryCount();
        if(nCnt > 0) {
            mFragmentManager.popBackStack();
            if(nCnt == 1) {
                ScrollView svContent = (ScrollView) findViewById(R.id.sv_content);
                svContent.setVisibility(View.VISIBLE);
                if(PTPreference.getInstance(getContext()).getIsSignin()) {
                    mMyChannelListView.initMyChannelListView();
                }
            }
            return true;
        } else {
            return super.onBackPressed();
        }
    }

    @Override
    public void reload(Bundle bundle) {

    }

    private void initActionBar() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_search, menu);

        final MenuItem menuSearch = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) menuSearch.getActionView();
        mSearchView.setInputType(mSearchView.getInputType() | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                mSearchView.clearFocus();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!mSearchClosing && !mSearchView.isIconified() && TextUtils.isEmpty(s)) {
                    search("");
                }

                return false;
            }
        });

        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {
                if (!queryTextFocused) {
                    mSearchClosing = true;
                    mSearchView.setQuery("", false);
                    mSearchView.setIconified(true);
                    menuSearch.collapseActionView();
                    mSearchClosing = false;
                }
            }
        });

        updateUI();

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateUI() {
        if (mSearchView == null) return;

//        updateLayout(false);
    }

    private void search(final String keyword) {
//        Assert.assertTrue(mCurrentChooseContentFragment != null);
//
//        mCurrentChooseContentFragment.search(keyword);
    }

    public void showChannelDescriptionFragment(PTFeaturedVideoObject obj) {
        ScrollView svContent = (ScrollView) findViewById(R.id.sv_content);
        svContent.setVisibility(View.GONE);
        mCdvFragment.setChannelVideoObject(obj);
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.left_in, R.anim.left_out, R.anim.right_in, R.anim.right_out);
        if (mFragmentManager.findFragmentById(R.id.fragment_subview) == null) {
            ft.replace(R.id.fragment_subview, mCdvFragment, FRAGMENT_TAG_CDV);
            ft.addToBackStack(FRAGMENT_TAG_CDV);
        }
        mCurrentFragmentTag = FRAGMENT_TAG_CDV;
        ft.commit();
        mFragmentManager.executePendingTransactions();
    }

    public void showSignupFragment() {
        ScrollView svContent = (ScrollView) findViewById(R.id.sv_content);
        svContent.setVisibility(View.GONE);
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.left_in, R.anim.left_out, R.anim.right_in, R.anim.right_out);
        if (mFragmentManager.findFragmentById(R.id.fragment_subview) == null) {
            ft.replace(R.id.fragment_subview, mSigninFragment, FRAGMENT_TAG_SIGNIN);
            ft.addToBackStack(FRAGMENT_TAG_SIGNIN);
        }
        mCurrentFragmentTag = FRAGMENT_TAG_SIGNIN;
        ft.commit();
        mFragmentManager.executePendingTransactions();
    }

    public void gotoNextFragment(String tagName) {
        mCurrentFragmentTag = tagName;
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.left_in, R.anim.left_out, R.anim.right_in, R.anim.right_out);
        if(tagName.equals(FRAGMENT_TAG_SIGNIN)) {
            mFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ft.replace(R.id.fragment_subview, mSigninFragment, tagName);
        } else if(tagName.equals(FRAGMENT_TAG_SIGNIN_PHONE_NUMBER)) {
            ft.replace(R.id.fragment_subview, mSigninPhoneNumberFragment, tagName);
        } else if(tagName.equals(FRAGMENT_TAG_SIGNIN_EMAIL)) {
            ft.replace(R.id.fragment_subview, mSigninEmailFragment, tagName);
        } else if(tagName.equals(FRAGMENT_TAG_SIGNUP)) {
            ft.replace(R.id.fragment_subview, mSignupFragment, tagName);
        } else if(tagName.equals(FRAGMENT_TAG_SIGNUP_CODE)) {
            ft.replace(R.id.fragment_subview, mSignupCodeFragment, tagName);
        } else if(tagName.equals(FRAGMENT_TAG_VERIFY_CODE)) {
            ft.replace(R.id.fragment_subview, mVerifyCodeFragment, tagName);
        } else if(tagName.equals(FRAGMENT_TAG_VERIFY_PHONE_NUMBER)) {
            ft.replace(R.id.fragment_subview, mVerifyPhoneNumberFragment, tagName);
        }
        ft.addToBackStack(tagName);
        ft.commit();
        mFragmentManager.executePendingTransactions();
    }

    public void closeAllSubFragments() {
        mFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        ScrollView svContent = (ScrollView) findViewById(R.id.sv_content);
        svContent.setVisibility(View.VISIBLE);
        if(PTPreference.getInstance(getContext()).getIsSignin()) {
            mMyChannelListView.initMyChannelListView();
        }
    }
}
