package com.agamy.android.memoplaces.ui.activity;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.agamy.android.memoplaces.R;
import com.agamy.android.memoplaces.model.PlaceModel;

import java.util.ArrayList;
import java.util.List;

import com.agamy.android.memoplaces.database.PlacesContract.PlacesEntry;
import com.agamy.android.memoplaces.app.MyApp;
import com.agamy.android.memoplaces.app.MyConnectivityReceiver;
import com.agamy.android.memoplaces.services.MyJobSevice;
import com.agamy.android.memoplaces.ui.fragment.EmptyViewFragment;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.JobTrigger;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.kaopiz.kprogresshud.KProgressHUD;


import adapter.CustomAdapter;

import static com.agamy.android.memoplaces.ui.activity.Constants.*;

public class MainActivity extends AppCompatActivity implements CustomAdapter.OnItemClickListenerInterface, MyConnectivityReceiver.OnNetworkConnectionChange,SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int PLACE_PICKER_REQUEST = 25;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 26;
    RecyclerView recyclerView;
    List<PlaceModel> locationList;
    CustomAdapter arrayAdapter;
    View navHeaderView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    FloatingActionButton fab;
    //static int navItemIndex = 0;
    //public static String CURRENT_TAG = "";
    //KProgressHUD progressHUD;
    SharedPreferences mSharedPreferences;
    ImageView profileBackgroundImage;
    ImageView profilePicture;
    TextView profileName , profileJobTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        setUpNavigationDrawer();
        setUpSharedPreferences();
    }

    private void setUpSharedPreferences() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);

        String myTheme = mSharedPreferences.getString(getString(R.string.prefs_theme_key), "");
        String myName = mSharedPreferences.getString(getString(R.string.prefs_name_key), "");
        String myJobTitle = mSharedPreferences.getString(getString(R.string.prefs_job_key), "");

        profileName.setText(myName);
        profileJobTitle.setText(myJobTitle);
        switch (myTheme) {
            case "AppTheme":
                profileBackgroundImage.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                break;
            case "AppThemeOrange":
                profileBackgroundImage.setBackgroundColor(getResources().getColor(R.color.colorPrimaryOrange));
                break;
            case "AppThemeRed":
                profileBackgroundImage.setBackgroundColor(getResources().getColor(R.color.colorPrimaryRed));
                break;
            case "AppThemeGreen":
                profileBackgroundImage.setBackgroundColor(getResources().getColor(R.color.colorPrimaryGreen));
                break;
        }

    }

    private void showDemoPopupDialog() {

        final iOSDialog iOSDialog = new iOSDialog(MainActivity.this);
        iOSDialog.setTitle("Tips to use this app");
        iOSDialog.setSubtitle("1-Click (bottom right) button \n 2-Choose Place you want \n 3-Swipe/Drag Items Left/Right to delete saved places ");
        iOSDialog.setPositiveLabel("Ok");
        iOSDialog.setBoldPositiveLabel(true);
        iOSDialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iOSDialog.dismiss();
            }
        });
        iOSDialog.show();

    }

    private void setUpNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navHeaderView = navigationView.getHeaderView(0);
        profileBackgroundImage = navHeaderView.findViewById(R.id.profile_background_image);
        profilePicture = navHeaderView.findViewById(R.id.profile_picture);
        profileName = navHeaderView.findViewById(R.id.profile_name);
        profileJobTitle = navHeaderView.findViewById(R.id.profile_job_title);

        navHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
            }
        });
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                final Intent intent;
                switch (item.getItemId()) {
                    case R.id.nav_hospital:
                        Toast.makeText(MainActivity.this, getString(R.string.hospitals), Toast.LENGTH_SHORT).show();
                        //navItemIndex = 0;
                        //CURRENT_TAG = HOSPITAL_TAG;
                        intent = new Intent(MainActivity.this, MapsActivity.class);
                        intent.putExtra(FRAGMENT_CURRENT_TAG, HOSPITAL_TAG);
                        startActivity(intent);
                        break;
                    case R.id.nav_restaurant:
                        Toast.makeText(MainActivity.this, getString(R.string.restaurants), Toast.LENGTH_SHORT).show();
                        //navItemIndex = 1;
                        //CURRENT_TAG = RESTAURANT_TAG;

                        intent = new Intent(MainActivity.this, MapsActivity.class);
                        intent.putExtra(FRAGMENT_CURRENT_TAG, RESTAURANT_TAG);
                        startActivity(intent);
                        break;
                    case R.id.nav_school:
                        Toast.makeText(MainActivity.this, getString(R.string.schools), Toast.LENGTH_SHORT).show();
                        //navItemIndex = 2;
                        //CURRENT_TAG = SCHOOL_TAG;
                        intent = new Intent(MainActivity.this, MapsActivity.class);
                        intent.putExtra(FRAGMENT_CURRENT_TAG, SCHOOL_TAG);
                        startActivity(intent);
                        break;

                    case R.id.nav_bus_station:
                        Toast.makeText(MainActivity.this, getString(R.string.bus_station), Toast.LENGTH_SHORT).show();
                        //navItemIndex = 2;
                        //CURRENT_TAG = SCHOOL_TAG;
                        intent = new Intent(MainActivity.this, MapsActivity.class);
                        intent.putExtra(FRAGMENT_CURRENT_TAG, BUS_STATION_TAG);
                        startActivity(intent);
                        break;

                    case R.id.nav_train_station:
                        Toast.makeText(MainActivity.this, getString(R.string.train_station), Toast.LENGTH_SHORT).show();
                        //navItemIndex = 2;
                        //CURRENT_TAG = SCHOOL_TAG;
                        intent = new Intent(MainActivity.this, MapsActivity.class);
                        intent.putExtra(FRAGMENT_CURRENT_TAG, TRAIN_STATION_TAG);
                        startActivity(intent);
                        break;

                    case R.id.nav_gas_station:
                        Toast.makeText(MainActivity.this, getString(R.string.gas_station), Toast.LENGTH_SHORT).show();
                        //navItemIndex = 2;
                        //CURRENT_TAG = SCHOOL_TAG;
                        intent = new Intent(MainActivity.this, MapsActivity.class);
                        intent.putExtra(FRAGMENT_CURRENT_TAG, GAS_STATION_TAG);
                        startActivity(intent);
                        break;

                }
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                drawerLayout.closeDrawers();


                return false;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void initComponents() {
        recyclerView = findViewById(R.id.myList);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
        locationList = new ArrayList<>();
        arrayAdapter = new CustomAdapter(this);
        arrayAdapter.setMyPlaceModels(locationList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Toast.makeText(MainActivity.this, "Saved Place Deleted", Toast.LENGTH_SHORT).show();
                int id = (int) viewHolder.itemView.getTag();

                String whereClause = PlacesEntry._ID + " = ? ";
                String[] whereArgs = new String[]{String.valueOf(id)};
                Uri uri = PlacesEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
                int numOfDeleted = getContentResolver().delete(uri, whereClause, whereArgs);
                if (numOfDeleted > 0) {
                    displayDatabaseData();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        //Now Set Adapter
        recyclerView.setAdapter(arrayAdapter);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra(FLOATING_ACTION_CLICK, "true");
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.e("act", "OnStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("act", "onPause");
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("act", "OnResume");
        MyApp.getInstance().setConnectionListener(this);
        displayDatabaseData();

    }
    private void displayDatabaseData() {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(PlacesEntry.CONTENT_URI, null, null, null, null);
            locationList.clear();
            while (cursor != null && !cursor.isAfterLast()) {
                //Log.e("cursor_index",""+cursor.getInt(0));
                int id = cursor.getInt(PlacesEntry.COLUMN_PLACE_ID_INDEX);
                double lat = cursor.getDouble(PlacesEntry.COLUMN_PLACE_LATITUDE_INDEX);
                double lng = cursor.getDouble(PlacesEntry.COLUMN_PLACE_LONGITUDE_INDEX);
                String country = cursor.getString(PlacesEntry.COLUMN_PLACE_COUNTRY_INDEX);
                String address = cursor.getString(PlacesEntry.COLUMN_PLACE_ADDRESS_INDEX);
                PlaceModel placeModel = new PlaceModel(id, lat, lng, address, country);
                locationList.add(placeModel);
                cursor.moveToNext();
            }
            arrayAdapter.setMyPlaceModels(locationList);
            loadEmptyFragmentView(arrayAdapter.getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public int getRowsCount()
    {
        Cursor cursor = getContentResolver().query(PlacesEntry.CONTENT_URI, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    void loadEmptyFragmentView(int count) {

        if (count == 0) {
            Fragment fragment = new EmptyViewFragment();
            //Note We replace , commit
            if (!fragment.isAdded()) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment, "empty");
                fragmentTransaction.commit();
            }
        } else {
            Fragment emptyFragment = getSupportFragmentManager().findFragmentByTag("empty");
            if (emptyFragment != null) {
                //Note We remove , commit
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.remove(emptyFragment);
                fragmentTransaction.commit();
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
/*            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;*/
            case R.id.delete_all:
                if(getRowsCount() > 0) {
                    Toast.makeText(this, getString(R.string.delete_all_places), Toast.LENGTH_SHORT).show();
                    showDeleteAllPopupDialog();
                }else {
                    Toast.makeText(this, "All places Already Deleted!", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.list_all:
                Toast.makeText(this, getString(R.string.list_all_places), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra(LIST_ALL_CLICK, "true");
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.settings:
                Toast.makeText(this, getString(R.string.settings), Toast.LENGTH_SHORT).show();
                Intent prefsIntent = new Intent(this , SettingsActivity.class);
                startActivity(prefsIntent);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void showDeleteAllPopupDialog() {

        final iOSDialog iOSDialog = new iOSDialog(MainActivity.this);
        iOSDialog.setTitle(getString(R.string.delete_all));
        iOSDialog.setSubtitle("Are you sure to delete all places?");
        iOSDialog.setPositiveLabel("Ok");
        iOSDialog.setNegativeLabel("Cancel");
        iOSDialog.setBoldPositiveLabel(true);
        iOSDialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iOSDialog.dismiss();
                getContentResolver().delete(PlacesEntry.CONTENT_URI, null, null);
                displayDatabaseData();
            }
        });
        iOSDialog.setNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iOSDialog.dismiss();
            }
        });
        iOSDialog.show();
    }

    @Override
    public void onItemClick(int pos) {
        Toast.makeText(this, locationList.get(pos).getAddress(), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        intent.putExtra(PLACE_MODEL_OBJECT, locationList.get(pos));
        intent.putExtra(RECYCLER_ITEM_CLICK, "true");
        startActivity(intent);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    @Override
    public void onNetworkChange(boolean isConnected) {
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String msg = isConnected ? getString(R.string.connected) : getString(R.string.disconnected);
        int color = isConnected ? Color.BLUE : Color.RED;
        final Snackbar snackbar = Snackbar.make(fab, msg, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.close, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        View snakView = snackbar.getView();
        snakView.setBackgroundColor(color);
        snackbar.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSharedPreferences != null)
            mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        String changedPrefs = sharedPreferences.getString(key, "");
        if (key.equals(getString(R.string.prefs_theme_key))) {
            Utils.changeToTheme(MainActivity.this, changedPrefs);
        }else if(key.equals(getString(R.string.prefs_job_key))){
            profileJobTitle.setText(changedPrefs);
        }else if(key.equals(getString(R.string.prefs_name_key))){
            profileName.setText(changedPrefs);
        }
    }



}
