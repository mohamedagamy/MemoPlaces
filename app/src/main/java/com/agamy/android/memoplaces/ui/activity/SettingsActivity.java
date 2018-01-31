package com.agamy.android.memoplaces.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.agamy.android.memoplaces.R;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
     Toolbar toolbar;
     ListView settingsListview;
     SharedPreferences mSharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.onActivityChangeTheme(SettingsActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        toolbar = findViewById(R.id.toolbar);
        settingsListview = findViewById(R.id.settings_list_view);

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        final ArrayList<String> arrayList = new ArrayList<>(
                Arrays.asList(getString(R.string.choose_your_theme) , getString(R.string.enter_your_name) , getString(R.string.enter_your_job_title)));
        settingsListview.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,arrayList));

        settingsListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String seletedItem = arrayList.get(i);
                switch (i)
                {
                    case 0:
                        showCustomThemeDialog();
                        break;
                    case 1:
                        showEditTextInputDialog("Full Name",getString(R.string.enter_your_name) , getString(R.string.prefs_name_key));
                        break;
                    case 2:
                        showEditTextInputDialog("Job Title",getString(R.string.enter_your_job_title),getString(R.string.prefs_job_key));
                        break;
                }
            }
        });
    }

    private void showEditTextInputDialog(String head , String body , final String sharedPrefKey) {

        String currentValue = mSharedPreferences.getString(sharedPrefKey ,"");
        new MaterialDialog.Builder(this)
                .title(head)
                .content(body)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(null, currentValue, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if(!input.toString().isEmpty()) {
                            Toast.makeText(SettingsActivity.this, "" + input, Toast.LENGTH_SHORT).show();
                            mSharedPreferences.edit().putString(sharedPrefKey , input.toString()).apply();
                        }
                    }
                })
                .contentColor(Color.BLACK)
                .show();
    }

    private void showCustomThemeDialog() {
        final String[] itemkeys = getResources().getStringArray(R.array.themes_keys);
        String selectedItem = mSharedPreferences.getString(getString(R.string.prefs_theme_key) , "");
        int selectedIndex = !selectedItem.isEmpty() ? Arrays.asList(itemkeys).indexOf(selectedItem) : -1;

        new MaterialDialog.Builder(this)
                .title(R.string.choose_your_theme)
                .items(R.array.themes_values)
                .itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/
                        mSharedPreferences.edit().putString(getString(R.string.prefs_theme_key) , itemkeys[which]).apply();
                        //Toast.makeText(SettingsActivity.this, ""+which, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                })
                .positiveText("Ok")
                .contentColor(Color.BLACK)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                startMainActivity();
                break;

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startMainActivity();
    }

    void startMainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(this,
                    android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
            startActivity(intent, bundle);
        } else {
           startActivity(intent);
        }
        finish();
    }
    public void chooseThemeColor(View view) {
        Toast.makeText(this, "Color Selected", Toast.LENGTH_SHORT).show();
    }

    public void enterJobTitle(View view) {
        Toast.makeText(this, "Job Title", Toast.LENGTH_SHORT).show();
    }

    public void enterFullName(View view) {
        Toast.makeText(this, "Full Name", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String changedPrefs = sharedPreferences.getString(key, "");
        Log.e("",changedPrefs);
        Utils.onActivityRecreate(SettingsActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mSharedPreferences != null) {
            mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
            mSharedPreferences = null;
        }
    }
}
