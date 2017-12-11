package com.phantasm.phantasm.common.inapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.phantasm.phantasm.common.inapp.util.IabHelper;
import com.phantasm.phantasm.common.inapp.util.IabResult;
import com.phantasm.phantasm.common.inapp.util.Inventory;
import com.phantasm.phantasm.common.inapp.util.Purchase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by ABC on 9/14/2015.
 */
public class BillingService {
    private final static String TAG = BillingService.class.getSimpleName();
    private final static String INAPP_PAYLOAD = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgYV7a6AFja9zszrNWAMDgQ5eq8OOdM3Oh5ABaDVafmrJnafITiULZ913ZSbG4RGwpkhbhAiCTvcW6E5ovX3qJsGrXtkpSfNGARE05Y006tVo1rXyBbJkGBnrwX9M6sk0BT1NMg9ud/fkJji+Dpra37quj+P5A+8PmLStCLVKTQrAGN0U0Ra/uLq2vOO7FbzqYv8/7qSMH+QK6clLUJa6an10CDRVuM3rLpUwE0iergn1h8ZEOQnnm6Na8YFSJUF5XHekhz2U2IbyfEor9oYqihZ16UVD96N/uw64NtDA2xgcKpPdnMcfsHppya/MAdGQBeidKcd2rktd4KaWyXxJ0QIDAQAB";
    // public final static String PURCHASE_NAME = "android.test.purchased";
    public final static String PURCHASE_NAME = "full_version";
    // public final static String PURCHASE_NAME = "test_managed";

    private static BillingService instance;

    public static BillingService getInstance(Context context) {
        return getInstance(context, null);
    }

    public static BillingService getInstance(Context context, IabHelper.QueryInventoryFinishedListener listener) {
        if (instance == null) {
            instance = new BillingService(context, listener);
        }
        instance.mContext = context;
        instance.mActivity = (Activity)context;

        return instance;
    }

    private Context mContext;
    private Activity mActivity;

    private IInAppBillingService mInAppService;
    private IabHelper mInAppHelper;

    private IabHelper.QueryInventoryFinishedListener mInventoryListener;
//    private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;

    private BillingService(Context context, final IabHelper.QueryInventoryFinishedListener inventoryListener) {
        mContext = context;
        mActivity = (Activity) mContext;
        mInventoryListener = inventoryListener;

        mInAppHelper = new IabHelper(mContext, INAPP_PAYLOAD);
        mInAppHelper.enableDebugLogging(true);
        mInAppHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                try {
                    Log.d(TAG, "Setup finished.");

                    if (!result.isSuccess()) {
                        // Oh noes, there was a problem.
                        complain("Problem setting up in-app billing: " + result);
                        return;
                    }

                    // Hooray, IAB is fully set up. Now, let's get an inventory of stuff we own.
                    Log.d(TAG, "Setup successful. Querying inventory.");
                    mInAppHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Listener that's called when we finish querying the items and
    // subscriptions we own
    private IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            // Do we have the premium upgrade?
            Purchase monthlyPurchase = inventory.getPurchase(PURCHASE_NAME);
            if (monthlyPurchase != null
                    && verifyDeveloperPayload(monthlyPurchase)) {
                Log.d(TAG, "User has monthly subscribe");
                // MVAppSetting.getInstance(mContext).setMonthlyPurchased(true);
            } else {
                // MVAppSetting.getInstance(mContext).setMonthlyPurchased(false);
            }

            mInventoryListener.onQueryInventoryFinished(result, inventory);
        }
    };

    public void buy(final String purchaseName, final int requestCode, IabHelper.OnIabPurchaseFinishedListener listener) {
//        mPurchaseFinishedListener = listener;
        // Show expire message
        if (!isServicesConnected()) {
            new AlertDialog.Builder(mContext)
                    .setMessage(
                            "Service Disconnected. Please login to the google service.")
                    .setPositiveButton("Subscribe",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.dismiss();
                                }
                            }).show();
            return;
        }

        try {
            mInAppHelper.launchPurchaseFlow(mActivity, purchaseName,
                    IabHelper.ITEM_TYPE_SUBS, requestCode, listener, "");
        } catch (Exception e) {
            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /*
    // Callback for when a purchase is finished
    private IabHelper.OnIabPurchaseFinishedListener mMonthlyPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                MVAppSetting.getInstance(mContext).setMonthlyPurchased(false);

                return;
            }

            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                MVAppSetting.getInstance(mContext).setMonthlyPurchased(false);

                return;
            }

            // bought 1/4 tank of gas. So consume it.
            Log.d(TAG, "Purchased monthly subscription");
            MVAppSetting.getInstance(mContext).setMonthlyPurchased(true);
        }
    };*/


    public IabHelper getInAppHelper() {
        return mInAppHelper;
    }

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mInAppService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mInAppService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    // Check whether google play services are available on the device
    private boolean isServicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {

            // In debug mode, ITZCommonLog the status
            Log.d("Location Updates", "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            Log.d(TAG, "HomeScreen::servicesConnected google play services are not installed");

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                return false;
            } else {
                Toast.makeText(mContext, "This device is not supported.", Toast.LENGTH_LONG).show();
            }
            return false;
        }
    }

    boolean verifyDeveloperPayload(Purchase p) {
        return true;
    }

    void complain(String message) {
        Log.e(TAG, message);
    }
}
