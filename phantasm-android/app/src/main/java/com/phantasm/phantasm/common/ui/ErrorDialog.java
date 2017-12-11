package com.phantasm.phantasm.common.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

/**
 * Created by kevinfinn on 6/12/15.
 */
public class ErrorDialog {
    static AlertDialog dialog;
    FragmentActivity activity;

    public AlertDialog createVolleyError(@NonNull final FragmentActivity activity, final VolleyError error, @Nullable final DialogInterface.OnDismissListener onDismissListener)
    {
        String title = "Error", msg = error.getMessage();

        this.activity = activity;

        try {
            VolleyLog.d(error.getMessage(), activity.getClass().getSimpleName());
        } catch(NullPointerException e){
            //Do nothing
        }

        if(error instanceof NoConnectionError){
            return createNetworkConnectionErrorDialog(activity, onDismissListener );

        } else {
            if (error instanceof TimeoutError) {
                title = "Timeout Error";
                msg = "Server did not respond in time.";
            } else if (error instanceof ParseError) {
                title = "Parse Error";
            } else if (error instanceof NetworkError) {
                title = "Network Error";
            } else if (error instanceof AuthFailureError) {
                title = "Authentication Failure Error";
            } else if (error instanceof ServerError) {
                title = "Server Error";
            }
            dialog =  new AlertDialog.Builder(activity).setTitle(title).setMessage(msg).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    onDismissListener.onDismiss(dialogInterface);
                }
            }).setPositiveButton("Try Again", null).create();
        }
        if(onDismissListener!=null)
            dialog.setOnDismissListener(onDismissListener);

        return dialog;
    }

    public static AlertDialog createWarningDialog(Context context, String msg){
        return new AlertDialog.Builder(context).setMessage(msg).setTitle("Warning").create();
    }
    public static AlertDialog createDialog(Context context, String title, String msg){
        return new AlertDialog.Builder(context).setMessage(msg).setTitle(title).create();
    }

    static AlertDialog networkConnectionErrorDialog;
    public static AlertDialog createNetworkConnectionErrorDialog(final Context context, @NonNull final DialogInterface.OnDismissListener dismissListener){

            networkConnectionErrorDialog = new AlertDialog.Builder(context).setTitle("No network connection").setMessage("Network connection is required to run Phantasm").setCancelable(false).create();
                networkConnectionErrorDialog.setButton(Dialog.BUTTON_NEGATIVE, "Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismissListener.onDismiss(dialogInterface);
                    }
                });

             networkConnectionErrorDialog.show();

        return networkConnectionErrorDialog;
    }

    public static void hideNetworkConnectionErrorDialog(){
        if(networkConnectionErrorDialog!=null && networkConnectionErrorDialog.isShowing()){
            try {
                networkConnectionErrorDialog.dismiss();
            } catch(IllegalArgumentException e){
                e.printStackTrace();
            }
        }
    }

}
