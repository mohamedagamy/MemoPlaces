package com.agamy.android.memoplaces;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.agamy.android.memoplaces.database.PlacesContract;
import com.agamy.android.memoplaces.fragment.HospitalFragment;
import com.agamy.android.memoplaces.fragment.RestaurantFragment;
import com.agamy.android.memoplaces.fragment.SchoolFragment;
import com.agamy.android.memoplaces.model.PlaceModel;

import java.util.ArrayList;

import com.agamy.android.memoplaces.database.PlacesContract.PlacesEntry;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<PlaceModel> locationArrayList;
    ArrayAdapter arrayAdapter;
    View navHeaderView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    static int navItemIndex = 0;
    public static String CURRENT_TAG = "";
    public static final String HOSPITAL_TAG = "hospital";
    public static final String SCHOOL_TAG = "school";
    public static final String RESTAURANT_TAG = "restaurant";



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
                        Toast.makeText(MainActivity.this, "Hospitals", Toast.LENGTH_SHORT).show();
                        navItemIndex = 0;
                        CURRENT_TAG = HOSPITAL_TAG;
                        intent = new Intent(MainActivity.this , MapsActivity.class);
                        intent.putExtra("CURRENT_TAG",HOSPITAL_TAG);
                        startActivity(intent);
                        break;
                    case  R.id.nav_restaurant:
                        Toast.makeText(MainActivity.this, "Restaurants", Toast.LENGTH_SHORT).show();
                        navItemIndex = 1;
                        CURRENT_TAG = RESTAURANT_TAG;

                        intent = new Intent(MainActivity.this , MapsActivity.class);
                        intent.putExtra("CURRENT_TAG",RESTAURANT_TAG);
                        startActivity(intent);
                        break;
                    case R.id.nav_school:
                        Toast.makeText(MainActivity.this, "Schools", Toast.LENGTH_SHORT).show();
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
        listView = findViewById(R.id.myList);
        locationArrayList = new ArrayList<>();
        locationArrayList.add(new PlaceModel("My Favourite Places"));
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, locationArrayList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("locInfo", locationArrayList.get(i));
                intent.putExtra("listItemClickFlag","true");
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

    private void displayDatabaseData() {
        try {
            Cursor cursor = getContentResolver().query(PlacesEntry.CONTENT_URI, null, null, null, null);

            while (cursor != null && !cursor.isAfterLast()) {
                double lat = cursor.getDouble(PlacesEntry.COLUMN_PLACE_LATITUDE_INDEX);
                double lng = cursor.getDouble(PlacesEntry.COLUMN_PLACE_LONGITUDE_INDEX);
                String country = cursor.getString(PlacesEntry.COLUMN_PLACE_COUNTRY_INDEX);
                String address = cursor.getString(PlacesEntry.COLUMN_PLACE_ADDRESS_INDEX);
                PlaceModel placeModel = new PlaceModel(lat, lng, address, country);
                locationArrayList.add(placeModel);
                cursor.moveToNext();
            }

            arrayAdapter.notifyDataSetChanged();
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
                Toast.makeText(this, "Delete All Places", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.list_all:
                Toast.makeText(this, "List All Places", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this , MapsActivity.class);
                intent.putExtra("listAllAction","true");
                intent.putExtra("listItemClickFlag","false");
                startActivity(intent);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }



/*    private Fragment getMyFragment()
    {
        switch (navItemIndex)
        {
            case 0:
                return new HospitalFragment();
            case 1:
                return new RestaurantFragment();
            case 2:
                return new SchoolFragment();

                default:
                    return new HospitalFragment();
        }



    }*/

/*    private void loadMyFragement()
    {
        Fragment fragment = getMyFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment , CURRENT_TAG);
        fragmentTransaction.commit();

    }*/
}
