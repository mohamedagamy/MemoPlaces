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
import android.support.v7.widget.Toolbar;
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case android.R.id.home:
                finish();
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
