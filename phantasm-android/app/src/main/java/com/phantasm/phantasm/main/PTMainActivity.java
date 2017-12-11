package com.phantasm.phantasm.main;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.ui.PTBaseActivity;

import net.ralphpina.permissionsmanager.PermissionsManager;

public class PTMainActivity extends PTBaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragment as the first Fragment
         */

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new PTMainFragment()).commit();

        if (!PermissionsManager.get().isStorageGranted()) {
            PermissionsManager.get().requestStoragePermission(this);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_item_home) {
            mFragmentTransaction.replace(R.id.containerView, new PTMainFragment()).commit();
        } else if (menuItem.getItemId() == R.id.nav_item_help) {
            mFragmentTransaction.replace(R.id.containerView, new PTHelpFragment()).commit();
        } else if (menuItem.getItemId() == R.id.nav_item_about) {
            mFragmentTransaction.replace(R.id.containerView, new PTAboutFragment()).commit();
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (!PermissionsManager.get().isStorageGranted()) {
            finish();
        }
    }
}