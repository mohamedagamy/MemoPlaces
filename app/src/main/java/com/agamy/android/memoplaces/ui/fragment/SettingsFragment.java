package com.agamy.android.memoplaces.ui.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.agamy.android.memoplaces.R;
import com.agamy.android.memoplaces.ui.activity.MainActivity;
import com.agamy.android.memoplaces.ui.activity.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_layout);
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String job = mSharedPreferences.getString(getString(R.string.prefs_job_key),"");
        String name = mSharedPreferences.getString(getString(R.string.prefs_name_key),"");
        String theme = mSharedPreferences.getString(getString(R.string.prefs_theme_key),"");

        Preference jobPrefs = findPreference(getString(R.string.prefs_job_key));
        Preference namePrefs = findPreference(getString(R.string.prefs_name_key));
        Preference themePrefs = findPreference(getString(R.string.prefs_theme_key));

        jobPrefs.setSummary(job);
        namePrefs.setSummary(name);
        themePrefs.setSummary(theme);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String changedPrefs = sharedPreferences.getString(key, "");
        Preference preference = findPreference(key);
        preference.setSummary(changedPrefs);


    }
}
