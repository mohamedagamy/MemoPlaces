package com.agamy.android.memoplaces.route;

/**
 * Created by agamy on 12/24/2017.
 */

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;


public class DirectionsJSONParser extends Observable {
    //public static PolylineOptions polylineOptions;
    //public static GoogleMap googleMap;
    private static DirectionsJSONParser INSTANCE = null;

    public DirectionsJSONParser() {

    }

    public static DirectionsJSONParser getInstance() {
        if(INSTANCE == null){
            INSTANCE = new DirectionsJSONParser();
        }
        return INSTANCE;
    }

    void notifyMyObservers()
    {
        setChanged();
        notifyObservers();
    }
    /**
     * Receives a JSONObject and returns a list of lists containing latitude and longitude
     */
    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List list = decodePoly(polyline);

                        /** Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                            hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }

        return routes;
    }

    private List decodePoly(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


    public static String getDirectionsUrl(LatLng origin, LatLng dest) {

        String url = null;
        try {
            // Origin of route
            String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

            // Destination of route
            String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

            // Sensor enabled
            String sensor = "sensor=false";
            String mode = "mode=driving";
            // Building the parameters to the web service
            String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

            // Output format
            String output = "json";

            // Building the url to the web service
            url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        } catch (Exception e) {

        }


        return url;
    }

    /**
     * A method to download json data from url
     */
    public static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            if (iStream != null && urlConnection != null) {
                iStream.close();
                urlConnection.disconnect();
            }

        }
        return data;
    }

    public static String data = "";

    public static class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            try {
                /**
                 * A method to download json data from url
                 */
                data = downloadUrl(url[0]);

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (!result.equalsIgnoreCase("")) {
                ParserTask parserTask = new ParserTask();
                parserTask.execute(result);
            }
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    public static class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        //String distance = null, Time = null;
        static PolylineOptions polylineOptions;
        private PolylineOptions myPolyLineOptions;

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                JSONArray obj = jObject.getJSONArray("routes");
                if (obj.length() > 0) {
                    //distance = String.valueOf(obj.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").get("text"));
                    //Time = String.valueOf(obj.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").get("text"));

                    DirectionsJSONParser parser = new DirectionsJSONParser();
                    routes = parser.parse(jObject);
                }
            } catch (Exception e) {
                return null;
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            MarkerOptions markerOptions = new MarkerOptions();

            try {
                if (result != null) {
                    for (int i = 0; i < result.size(); i++) {
                        points = new ArrayList();
                        polylineOptions = new PolylineOptions();

                        List<HashMap<String, String>> path = result.get(i);

                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);

                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);

                            points.add(position);
                        }

                        polylineOptions.addAll(points);
                        //polylineOptions.width(50.0f);
                        polylineOptions.color(Color.DKGRAY);
                        polylineOptions.geodesic(true);
                        polylineOptions.startCap(new RoundCap());
                        polylineOptions.endCap(new RoundCap());

                        /**
                         * 7b3358
                         * 6a1b9a
                         * 4a148c
                         * 4caf50
                         * 2E8B57
                         */

                    }
                    //TODO here we add PolyLines to Our Map
                    DirectionsJSONParser.getInstance().notifyMyObservers();
                    setPolylineOptions(polylineOptions);

                }
            } catch (Exception ex) {

            }
        }

        public static void setPolylineOptions(PolylineOptions polylineOptions) {
            ParserTask.polylineOptions = polylineOptions;
        }

        public static PolylineOptions getPolylineOptions() {
            return polylineOptions;
        }
    }

}