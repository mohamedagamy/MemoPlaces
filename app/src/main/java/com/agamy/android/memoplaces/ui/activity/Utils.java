package com.agamy.android.memoplaces.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;

import com.kaopiz.kprogresshud.KProgressHUD;

/**
 * Created by agamy on 1/11/2018.
 */

public class Utils {
    public KProgressHUD progressHUD;
    public static boolean isWifiEnabled(Context context)
    {
        WifiManager mng = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean isEnabled = (mng!= null && mng.isWifiEnabled());
        return isEnabled;
    }

    public void showProgressDialog(Activity activity)
    {
        if(progressHUD == null) {
            progressHUD = KProgressHUD.create(activity)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();
        }

    }

    public void hideProgressDialog()
    {
        if(progressHUD != null && progressHUD.isShowing())
            progressHUD.dismiss();

    }

}
