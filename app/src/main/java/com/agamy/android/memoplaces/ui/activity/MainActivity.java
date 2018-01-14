package com.agamy.android.memoplaces.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.Toast;


import com.agamy.android.memoplaces.R;
import com.agamy.android.memoplaces.model.PlaceModel;

import java.util.ArrayList;
import java.util.List;

import com.agamy.android.memoplaces.database.PlacesContract.PlacesEntry;
import com.agamy.android.memoplaces.app.MyApp;
import com.agamy.android.memoplaces.app.MyConnectivityReceiver;
import com.agamy.android.memoplaces.services.MyJobSevice;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.JobTrigger;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.kaopiz.kprogresshud.KProgressHUD;

import adapter.CustomAdapter;

public class MainActivity extends AppCompatActivity implements CustomAdapter.OnItemClickListenerInterface, MyConnectivityReceiver.OnNetworkConnectionChange {

    RecyclerView recyclerView;
    List<PlaceModel> locationList;
    CustomAdapter arrayAdapter;
    View navHeaderView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    FloatingActionButton fab;
    static int navItemIndex = 0;
    public static String CURRENT_TAG = "";
    public static final String HOSPITAL_TAG = "hospital";
    public static final String SCHOOL_TAG = "school";
    public static final String RESTAURANT_TAG = "restaurant";
    KProgressHUD progressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        setUpNavigationDrawer();
    }

    private void setUpNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                final Intent intent;
                switch (item.getItemId())
                {
                    case R.id.nav_hospital:
                        Toast.makeText(MainActivity.this, getString(R.string.hospitals), Toast.LENGTH_SHORT).show();
                        navItemIndex = 0;
                        CURRENT_TAG = HOSPITAL_TAG;
                        intent = new Intent(MainActivity.this , MapsActivity.class);
                        intent.putExtra("CURRENT_TAG",HOSPITAL_TAG);
                        startActivity(intent);
                        break;
                    case  R.id.nav_restaurant:
                        Toast.makeText(MainActivity.this, getString(R.string.restaurants), Toast.LENGTH_SHORT).show();
                        navItemIndex = 1;
                        CURRENT_TAG = RESTAURANT_TAG;

                        intent = new Intent(MainActivity.this , MapsActivity.class);
                        intent.putExtra("CURRENT_TAG",RESTAURANT_TAG);
                        startActivity(intent);
                        break;
                    case R.id.nav_school:
                        Toast.makeText(MainActivity.this, getString(R.string.schools), Toast.LENGTH_SHORT).show();
                        navItemIndex = 2;
                        CURRENT_TAG = SCHOOL_TAG;
                        intent = new Intent(MainActivity.this , MapsActivity.class);
                        intent.putExtra("CURRENT_TAG",SCHOOL_TAG);
                        startActivity(intent);


                        break;
                }

                drawerLayout.closeDrawers();


                return false;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


    }

    private void initComponents() {
        recyclerView = findViewById(R.id.myList);
        locationList = new ArrayList<>();
        arrayAdapter = new CustomAdapter(this);
        arrayAdapter.setMyPlaceModels(locationList);
        recyclerView.setAdapter(arrayAdapter);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0 ,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Toast.makeText(MainActivity.this, "Item at "+viewHolder.getAdapterPosition()+" Deleted", Toast.LENGTH_SHORT).show();
                int id = (int) viewHolder.itemView.getTag();

                String whereClause = PlacesEntry._ID+" = ? ";
                String[] whereArgs = new String[]{String.valueOf(id)};
                Uri uri = PlacesEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
                int numOfDeleted = getContentResolver().delete(uri,whereClause,whereArgs);
                if(numOfDeleted > 0) {
                    displayDatabaseData();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this , MapsActivity.class);
                intent.putExtra("fabClicked","true");
                startActivity(intent);
            }
        });

    }

    public void openMap(View view) {
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApp.getInstance().setConnectionListener(this);
    }

    private void displayDatabaseData() {
        try {
            Cursor cursor = getContentResolver().query(PlacesEntry.CONTENT_URI, null, null, null, null);
            locationList.clear();
            while (cursor != null && !cursor.isAfterLast()) {
                Log.e("cursor_index",""+cursor.getInt(0));
                int id = cursor.getInt(PlacesEntry.COLUMN_PLACE_ID_INDEX);
                double lat = cursor.getDouble(PlacesEntry.COLUMN_PLACE_LATITUDE_INDEX);
                double lng = cursor.getDouble(PlacesEntry.COLUMN_PLACE_LONGITUDE_INDEX);
                String country = cursor.getString(PlacesEntry.COLUMN_PLACE_COUNTRY_INDEX);
                String address = cursor.getString(PlacesEntry.COLUMN_PLACE_ADDRESS_INDEX);
                PlaceModel placeModel = new PlaceModel(id,lat, lng, address, country);
                locationList.add(placeModel);
                cursor.moveToNext();
            }
            Log.e("cursor_index","=====================================");
            arrayAdapter.setMyPlaceModels(locationList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.delete_all:
                Toast.makeText(this, getString(R.string.delete_all_places), Toast.LENGTH_SHORT).show();
                getContentResolver().delete(PlacesEntry.CONTENT_URI,null,null);
                return true;
            case R.id.list_all:
                Toast.makeText(this, getString(R.string.list_all_places), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this , MapsActivity.class);
                intent.putExtra("listAllAction","true");
                intent.putExtra("listItemClickFlag","false");
                startActivity(intent);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int pos) {
        Toast.makeText(this, "item :"+pos+" clicked", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        intent.putExtra("locInfo", locationList.get(pos));
        intent.putExtra("listItemClickFlag","true");
        startActivity(intent);
    }


    @Override
    public void onNetworkChange(boolean isConnected) {
            showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String msg = isConnected ? getString(R.string.connected) : getString(R.string.disconnected);
        int color = isConnected ? Color.BLUE  : Color.RED;
        final Snackbar snackbar = Snackbar.make(fab , msg,Snackbar.LENGTH_LONG);
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
    }
}
