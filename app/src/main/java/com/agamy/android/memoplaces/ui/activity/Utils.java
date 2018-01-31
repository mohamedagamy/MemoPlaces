package com.agamy.android.memoplaces.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.agamy.android.memoplaces.R;
import com.kaopiz.kprogresshud.KProgressHUD;

/**
 * Created by agamy on 1/11/2018.
 */

public class Utils {
    private KProgressHUD progressHUD;

    static boolean isWifiEnabled(Context context) {
        WifiManager mng = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean isEnabled = (mng != null && mng.isWifiEnabled());
        return isEnabled;
    }

    public void showProgressDialog(Activity activity) {
        if (progressHUD == null) {
            progressHUD = KProgressHUD.create(activity)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();
        }

    }

    public void hideProgressDialog() {
        if (progressHUD != null && progressHUD.isShowing())
            progressHUD.dismiss();

    }

    public static void onActivityRecreate(Activity activity) {

        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        activity.finish();

        Intent intent = new Intent(activity , activity.getClass());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(activity,
                    android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
            activity.startActivity(intent, bundle);
        } else {
            activity.startActivity(intent);
        }
    }

    /**
     * Set the theme of the activity, according to the configuration.
     *
     * @param activity
     */
    public static void onActivityChangeTheme(Activity activity) {

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String myTheme = mSharedPreferences.getString(activity.getString(R.string.prefs_theme_key), "");

        switch (myTheme) {
            case "AppTheme":
                activity.setTheme(R.style.AppTheme);
                break;

            case "AppThemeOrange":
                activity.setTheme(R.style.AppThemeOrange);
                break;
            case "AppThemeRed":
                activity.setTheme(R.style.AppThemeRed);
                break;
            case "AppThemeGreen":
                activity.setTheme(R.style.AppThemeGreen);
                break;
            default:
                activity.setTheme(R.style.AppTheme);

        }

    }

}
