package com.agamy.android.memoplaces.route;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;

import com.agamy.android.memoplaces.ui.activity.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * @author Priyanka
 */

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {
    private Activity mContext;
    public GetNearbyPlacesData(Activity context) {
        this.mContext = context;
    }

    private String googlePlacesData;
    private GoogleMap mMap;
    String url;
    Utils utils = new Utils();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        utils.showProgressDialog(mContext);

    }

    @Override
    protected String doInBackground(Object... objects){
        mMap = (GoogleMap)objects[0];
        url = (String)objects[1];

        DownloadURL downloadURL = new DownloadURL();
        try {
            googlePlacesData = downloadURL.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s){

        List<HashMap<String, String>> nearbyPlaceList;
        DataParser parser = new DataParser();
        if(!s.isEmpty()) {
            nearbyPlaceList = parser.parse(s);
            Log.d("nearbyplacesdata", "called parse method");
            showNearbyPlaces(nearbyPlaceList);
        }

        new CountDownTimer(1000, 8000) {
            @Override
            public void onTick(long l) {

            }
            @Override
            public void onFinish() {
                utils.hideProgressDialog();
            }
        }.start();

        super.onPostExecute(s);
    }

    private void showNearbyPlaces(List<HashMap<String, String>> nearbyPlaceList)
    {
        for(int i = 0; i < nearbyPlaceList.size(); i++)
        {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlaceList.get(i);

            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            double lat = Double.parseDouble( googlePlace.get("lat"));
            double lng = Double.parseDouble( googlePlace.get("lng"));

            LatLng latLng = new LatLng( lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : "+ vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }
    }
}
