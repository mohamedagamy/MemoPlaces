package com.agamy.android.memoplaces;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.agamy.android.memoplaces.listener.MapListener;
import com.agamy.android.memoplaces.model.PlaceModel;
import com.agamy.android.memoplaces.route.DirectionsJSONParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.agamy.android.memoplaces.route.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.agamy.android.memoplaces.database.PlacesContract.PlacesEntry;
public class MapsActivity extends FragmentActivity implements Observer, OnMapReadyCallback, GoogleMap.OnMapLongClickListener, LocationListener,MapListener {

    private static final int REQUEST_LOCATION_CODE = 150;
    private Observable mDirectionJsonParserObservable;
    public static GoogleMap mapView;
    LocationManager locationManager;
    Location mLocation;
    PlaceModel mPlaceModel;
    Marker myPlaceMarker , selectedPlaceMarker;
    String[] providers = new String[]{LocationManager.GPS_PROVIDER , LocationManager.NETWORK_PROVIDER};
    private static final int LOCATION_INTERVAL = 6000;
    private static final float LOCATION_DISTANCE = 0.0f;
    Handler handler;
    Runnable runnable;
    //private GoogleApiClient client;
    double latitude , longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initComponents();

    }

    private void initComponents() {
        mDirectionJsonParserObservable = DirectionsJSONParser.getInstance();
        mDirectionJsonParserObservable.addObserver(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //provider = locationManager.getBestProvider(new Criteria(), false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();

        locationManager.requestLocationUpdates(providers[1], LOCATION_INTERVAL, LOCATION_DISTANCE, MapsActivity.this);

        handler = new Handler();
        runnable =new Runnable() {
            @Override
            public void run() {
                //locationManager.requestLocationUpdates(providers[0] ,LOCATION_INTERVAL , LOCATION_DISTANCE,MapsActivity.this);
                String listItemClickFlag=getIntent().getStringExtra("listItemClickFlag");
                if (listItemClickFlag != null && listItemClickFlag.equals("true")) {
                    locationManager.requestLocationUpdates(providers[1], LOCATION_INTERVAL, LOCATION_DISTANCE, MapsActivity.this);
                    handler.postDelayed(this, 5000);
                }
            }
        };

        handler.post(runnable);

    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
        if(handler != null && runnable != null)
            handler.removeCallbacks(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDirectionJsonParserObservable.deleteObserver(this);
        locationManager.removeUpdates(this);

        if(handler != null && runnable != null)
            handler.removeCallbacks(runnable);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapView = googleMap;
        mapView.setOnMapLongClickListener(this);
        mapView.setMyLocationEnabled(true);

        Toast.makeText(this, "Map Ready", Toast.LENGTH_SHORT).show();
        this.onMapReadyNow();

        boolean success = false;
        try {
            success = mapView.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.mapstyle));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

        if (!success)
            Log.e("", "Style parsing failed.");
    }

    private void showNearByPlaces()
    {

        String drawerData = getIntent().getStringExtra("CURRENT_TAG");
        if(drawerData != null) {

            Object dataTransfer[] = new Object[2];
            GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
            switch (drawerData) {
                case MainActivity.HOSPITAL_TAG:
                    mapView.clear();
                    String hospital = "hospital";
                    String url = getUrl(latitude, longitude, hospital);
                    dataTransfer[0] = mapView;
                    dataTransfer[1] = url;

                    getNearbyPlacesData.execute(dataTransfer);
                    Toast.makeText(MapsActivity.this, "Showing Nearby Hospitals", Toast.LENGTH_SHORT).show();
                    break;

                case MainActivity.RESTAURANT_TAG:

                    mapView.clear();
                    String resturant = "restaurant";
                    url = getUrl(latitude, longitude, resturant);
                    dataTransfer[0] = mapView;
                    dataTransfer[1] = url;

                    getNearbyPlacesData.execute(dataTransfer);
                    Toast.makeText(MapsActivity.this, "Showing Nearby Restaurants", Toast.LENGTH_SHORT).show();

                    break;
                case MainActivity.SCHOOL_TAG:
                    mapView.clear();
                    String school = "school";
                    url = getUrl(latitude, longitude, school);
                    dataTransfer[0] = mapView;
                    dataTransfer[1] = url;

                    getNearbyPlacesData.execute(dataTransfer);
                    Toast.makeText(MapsActivity.this, "Showing Nearby Schools", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }

    private void drawAllItemsMarkers() {
        Intent intent = getIntent();
        String listAction = intent.getStringExtra("listAllAction");
        if (listAction != null && listAction.equals("true")) {
            Toast.makeText(this, "All Markers has been drawn", Toast.LENGTH_SHORT).show();
            displayDatabaseData();

        }
    }

    private void drawSelectedItemMarker() {
        Intent intent = getIntent();
        mPlaceModel = intent.getParcelableExtra("locInfo");
        if (mPlaceModel != null) {
            if(mPlaceModel.getLatitude() != 0.0) {
                drawOneMarkerInMap(mPlaceModel.getLatitude(), mPlaceModel.getLongitude(), false);
                zoomToSelectedAndMyMarkers();
            }else {
                zoomAndDrawMyLocation();
            }
        }
    }

    private void zoomAndDrawMyLocation() {
        drawMyLocationMarker();
        mapView.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude() , mLocation.getLongitude()),16.0f));

    }

    private void drawOneMarkerInMap(double lat, double lng , boolean isCurrentLocation) {

        String address = new String("default");
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addressList = geocoder.getFromLocation(lat, lng, 1);
            if (addressList != null && addressList.size() > 0) {
                address = addressList.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        LatLng latLngCountry = new LatLng(lat, lng);
        if(isCurrentLocation)
            myPlaceMarker =  mapView.addMarker(new MarkerOptions().position(latLngCountry).title(address));
        else
            selectedPlaceMarker = mapView.addMarker(new MarkerOptions().position(latLngCountry).title(address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

    }

    private void drawRoute(LatLng srcLocation , LatLng dstLocation)
    {
        try {
            String url = DirectionsJSONParser.getDirectionsUrl(srcLocation , dstLocation);
            if (url != null)
                new DirectionsJSONParser.DownloadTask().execute(url);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        Toast.makeText(this, "Map Long Clicked", Toast.LENGTH_SHORT).show();
        String address = "Marker Icon";
        String country = "";
        if (latLng != null) {
            double lat = latLng.latitude;
            double lng = latLng.longitude;
            // Add a marker in Sydney and move the camera
            Geocoder geocoder = new Geocoder(this);
            try {
                List<Address> addressList = geocoder.getFromLocation(lat, lng, 1);
                if (addressList != null && addressList.size() > 0) {
                    address = addressList.get(0).getAddressLine(0);
                    country = addressList.get(0).getCountryName();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            LatLng latLngCountry = new LatLng(lat, lng);
            mapView.addMarker(new MarkerOptions().position(latLngCountry).title(address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
            mapView.moveCamera(CameraUpdateFactory.newLatLng(latLngCountry));
            mapView.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 10));

            PlaceModel placeModel = new PlaceModel(lat, lng, address, country);
            insertNewPlace(placeModel);
        }

    }

    private void insertNewPlace(PlaceModel placeModel) {
        ContentValues values = new ContentValues();
        values.put(PlacesEntry.COLUMN_PLACE_LATITUDE, placeModel.getLatitude());
        values.put(PlacesEntry.COLUMN_PLACE_LONGITUDE, placeModel.getLongitude());
        values.put(PlacesEntry.COLUMN_PLACE_ADDRESS, placeModel.getAddress());
        values.put(PlacesEntry.COLUMN_PLACE_COUNTRY, placeModel.getCountry());
        getContentResolver().insert(PlacesEntry.CONTENT_URI, values);
    }

    @Override
    public void onLocationChanged(Location userLocation) {

        latitude = userLocation.getLatitude();
        longitude = userLocation.getLongitude();

        saveMyLocationToSharedPrefs(mLocation);
        boolean isNewLocation = userLocation.getLatitude() != getMyLocationFromShard().get(0) &&
                userLocation.getLongitude() != getMyLocationFromShard().get(1);
        if(isNewLocation)
        {
            showNearByPlaces();
            mLocation = userLocation;
            LatLng srcLocation = null;
            LatLng dstLocation = null;
            if(mPlaceModel != null)
             dstLocation = new LatLng(mPlaceModel.getLatitude(), mPlaceModel.getLongitude());
            if(mLocation != null)
                 srcLocation = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            //Here Route Track call is made
            if(DirectionsJSONParser.ParserTask.getPolylineOptions() == null && srcLocation != null && dstLocation != null)
              drawRoute(srcLocation, dstLocation);
            Log.e("Current Location", mLocation.getLatitude() + "" + mLocation.getLongitude());
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void displayDatabaseData() {

        ArrayList<MarkerOptions> markerOptionsArrayList = new ArrayList<>();
        mapView.clear();
        drawMyLocationMarker();
        try {
            Cursor cursor = getContentResolver().query(PlacesEntry.CONTENT_URI, null, null, null, null);
            while (cursor != null && !cursor.isAfterLast()) {
                double lat = cursor.getDouble(PlacesEntry.COLUMN_PLACE_LATITUDE_INDEX);
                double lng = cursor.getDouble(PlacesEntry.COLUMN_PLACE_LONGITUDE_INDEX);
                String country = cursor.getString(PlacesEntry.COLUMN_PLACE_COUNTRY_INDEX);
                String address = cursor.getString(PlacesEntry.COLUMN_PLACE_ADDRESS_INDEX);
                PlaceModel placeModel = new PlaceModel(lat, lng, address, country);
                MarkerOptions markerOptions = new MarkerOptions().position(placeModel.getLatLng()).title(address)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                mapView.addMarker(markerOptions);
                markerOptionsArrayList.add(markerOptions);
                cursor.moveToNext();
            }

            zoomToAllMarkersInMap(markerOptionsArrayList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    private void drawMyLocationMarker()
    {
        mLocation = locationManager.getLastKnownLocation(providers[1]);
        if (mLocation != null) {
            drawOneMarkerInMap(mLocation.getLatitude(), mLocation.getLongitude(),true);
        }
    }

    private void zoomToAllMarkersInMap(ArrayList<MarkerOptions> markerOptions)
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (MarkerOptions marker : markerOptions) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.15); // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,width , height, padding);
        mapView.animateCamera(cu);
    }


    private void zoomToSelectedAndMyMarkers()
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if(selectedPlaceMarker != null)
            builder.include(selectedPlaceMarker.getPosition());

        if(myPlaceMarker != null)
            builder.include(myPlaceMarker.getPosition());

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.15); // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,width,height,padding);
        mapView.animateCamera(cu);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReadyNow() {
        //Here All Markers updates according to Intent Extras
       //Current Location Marker
        mapView.clear();



        drawMyLocationMarker();

        //if one item selected from list
        drawSelectedItemMarker();

        //If List All in Menu is Choosen
        drawAllItemsMarkers();

    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof DirectionsJSONParser) {
            mapView.clear();
            mapView.addPolyline(DirectionsJSONParser.ParserTask.getPolylineOptions());
            drawMyLocationMarker();
            drawSelectedItemMarker();
        }

    }

    public void saveMyLocationToSharedPrefs(Location mLocation)
    {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("lat" , String.valueOf(mLocation.getLongitude()));
        editor.putString("lng" , String.valueOf(mLocation.getLongitude()));
        editor.apply();
    }

    public ArrayList<Double> getMyLocationFromShard()
    {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String lat = sharedPref.getString("lat", "");
        String lng = sharedPref.getString("lng", "");
        ArrayList<Double> latLng = new ArrayList<>();
        if(!lat.isEmpty() && !lng.isEmpty()) {
            latLng.add(Double.parseDouble(lat));
            latLng.add(Double.parseDouble(lng));
        }
        return latLng;

    }


    private String getUrl(double latitude , double longitude , String nearbyPlace)
    {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+10000);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyCzBDc73H9bFY84hSTJFXzrpwFJ6SKiOjM");

        Log.d("MapsActivity", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;

        }
        else
            return true;
    }


}