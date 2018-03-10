package com.agamy.android.memoplaces.route;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.agamy.android.memoplaces.R;
import com.agamy.android.memoplaces.ui.activity.*;
import com.agamy.android.memoplaces.ui.activity.Utils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Priyanka
 */

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {
    private Activity mContext;
    private String mFragmentTag;
    List<MarkerOptions> markerOptionsList=new ArrayList<>();

    public GetNearbyPlacesData(Activity context, String fragmentTag) {
        this.mContext = context;
        this.mFragmentTag = fragmentTag;
    }

    private String googlePlacesData;
    private GoogleMap mMap;
    private String url;
    private Utils utils = new Utils();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        utils.showProgressDialog(mContext);

    }

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadURL downloadURL = new DownloadURL();
        try {
            googlePlacesData = downloadURL.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {

        List<HashMap<String, String>> nearbyPlaceList;
        DataParser parser = new DataParser();

        if (!s.isEmpty()) {
            nearbyPlaceList = parser.parse(s);
            Log.d("nearbyplacesdata", "called parse method");

            if (nearbyPlaceList.size() != 0) {
                String toastNearBy = getStringResNearByPlace();
                Toast.makeText(mContext, toastNearBy, Toast.LENGTH_LONG).show();
                showNearbyPlaces(nearbyPlaceList);
            } else {
                String toastFarFrom = getStringResFarFromPlace();
                Toast.makeText(mContext, toastFarFrom, Toast.LENGTH_LONG).show();
            }

        }
            utils.hideProgressDialog();
            super.onPostExecute(s);
    }

    private void showNearbyPlaces(List<HashMap<String, String>> nearbyPlaceList) {
        for (int i = 0; i < nearbyPlaceList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlaceList.get(i);

            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));

            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : " + vicinity);
            int resourecIcon = getResourceIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromResource(resourecIcon));

            mMap.addMarker(markerOptions);
            //markerOptionsList.add(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }



        //zoomToAllMarkersInMap(markerOptionsList);
    }

    private void zoomToAllMarkersInMap(List<MarkerOptions> markerOptions)
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (MarkerOptions marker : markerOptions) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int height = mContext.getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.15); // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,width , height, padding);
        mMap.animateCamera(cu);
    }

    private String getStringResNearByPlace()
    {
        switch (mFragmentTag)
        {
            case Constants.HOSPITAL_TAG:
                return mContext.getString(R.string.near_by_hospitals);
            case Constants.RESTAURANT_TAG:
                return mContext.getString(R.string.near_by_restaurant);
            case Constants.SCHOOL_TAG:
                return mContext.getString(R.string.near_by_schools);
            case Constants.BUS_STATION_TAG:
                return mContext.getString(R.string.near_by_bus_station);
            case Constants.TRAIN_STATION_TAG:
                return mContext.getString(R.string.near_by_train_station);
            case Constants.GAS_STATION_TAG:
                return mContext.getString(R.string.near_by_gas_station);

            case Constants.BANK_TAG:
                return mContext.getString(R.string.near_by_bank);
            case Constants.AIRPORT_TAG:
                return mContext.getString(R.string.near_by_airport);
            case Constants.CAFE_TAG:
                return mContext.getString(R.string.near_by_cafe);
            case Constants.CINEMA_TAG:
                return mContext.getString(R.string.near_by_cinema);
            case Constants.HOTEL_TAG:
                return mContext.getString(R.string.near_by_hotel);
            case Constants.ATM_TAG:
                return mContext.getString(R.string.near_by_atm);

            default:
                return "";
        }

    }


    private String getStringResFarFromPlace()
    {
        switch (mFragmentTag)
        {
            case Constants.HOSPITAL_TAG:
                return mContext.getString(R.string.far_from_hospitals);
            case Constants.RESTAURANT_TAG:
                return mContext.getString(R.string.far_from_restaurant);
            case Constants.SCHOOL_TAG:
                return mContext.getString(R.string.far_from_schools);
            case Constants.BUS_STATION_TAG:
                return mContext.getString(R.string.far_from_bus_station);
            case Constants.TRAIN_STATION_TAG:
                return mContext.getString(R.string.far_from_train_station);

            case Constants.BANK_TAG:
                return mContext.getString(R.string.far_from_bank);
            case Constants.AIRPORT_TAG:
                return mContext.getString(R.string.far_from_airport);
            case Constants.CAFE_TAG:
                return mContext.getString(R.string.far_from_cafe);
            case Constants.CINEMA_TAG:
                return mContext.getString(R.string.far_from_cinema);
            case Constants.HOTEL_TAG:
                return mContext.getString(R.string.far_from_hotel);
            case Constants.ATM_TAG:
                return mContext.getString(R.string.far_from_atm);


            default:
                return "";
        }

    }

    public int getResourceIcon() {

        switch (mFragmentTag)
        {
            case Constants.HOSPITAL_TAG:
                return R.drawable.ic_local_hospital_black_24dp;
            case Constants.RESTAURANT_TAG:
                return R.drawable.ic_restaurant_black_24dp;
            case Constants.SCHOOL_TAG:
                return R.drawable.ic_school_black_24dp;
            case Constants.BUS_STATION_TAG:
                return R.drawable.ic_directions_bus_black_24dp;
            case Constants.TRAIN_STATION_TAG:
                return R.drawable.ic_train_black_24dp;

            case Constants.BANK_TAG:
                return R.drawable.ic_account_balance_black_24dp;
            case Constants.AIRPORT_TAG:
                return R.drawable.ic_local_airport_black_24dp;
            case Constants.CAFE_TAG:
                return R.drawable.ic_local_cafe_black_24dp;
            case Constants.CINEMA_TAG:
                return R.drawable.ic_local_movies_black_24dp;
            case Constants.HOTEL_TAG:
                return R.drawable.ic_local_hotel_black_24dp;
            case Constants.ATM_TAG:
                return R.drawable.ic_local_atm_black_24dp;
        }
        return 0;
    }
}
