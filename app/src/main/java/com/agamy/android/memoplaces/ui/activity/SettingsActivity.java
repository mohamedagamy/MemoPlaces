package com.agamy.android.memoplaces.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceFragmentCompat.OnPreferenceStartScreenCallback;
import android.support.v7.preference.PreferenceScreen;
import android.view.MenuItem;

import com.agamy.android.memoplaces.R;
import com.agamy.android.memoplaces.ui.fragment.SettingsFragment;

/**
 * Created by agamy on 1/16/2018.
 */

public class SettingsActivity extends AppCompatActivity{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Utils.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }


}
