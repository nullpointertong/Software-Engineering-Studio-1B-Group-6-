package yourteamnumber.seshealthpatient.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.location.LocationListener;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.lang.StringBuilder;

import yourteamnumber.seshealthpatient.Model.DataPacket.Models.MedicalFacility;
import yourteamnumber.seshealthpatient.R;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {

    MapView mMapView;
    private GoogleMap mMap;
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean mLocationPermissionGranted;
    private static final int DEFAULT_ZOOM = 14;
    private final LatLng mDefaultLocation = new LatLng(-33.8840504, 151.1992254);

    private LocationManager mLocationManager;
    private final String TAG = "map_fragment";
    private StringBuilder sbPlaceQuery;
    private JSONObject jsonPlaceList;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            mLastKnownLocation = location;
            Log.d(TAG, "My location: "+ mLastKnownLocation.toString());
            if (mMap.isMyLocationEnabled() && mLocationPermissionGranted) {
                createPlaceQuery();
                runPlacesRequestTask();
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
    };

    public MapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());


        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                // Move camera to Sydney
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));

                requestLocationPermission();

                initiateDeviceLocation();


            }
        });
        return rootView;
    }

    void createPlaceQuery() {
        /** Create
         * Create query to get medical facilities through google's Nearby Search function
         */
        sbPlaceQuery = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");

        sbPlaceQuery.append("location=" + mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude());
        sbPlaceQuery.append("&radius=5000");
        sbPlaceQuery.append("&keyword=hospital+clinic+doctor+health");
        sbPlaceQuery.append("&sensor=true");
        sbPlaceQuery.append("&key=" + getResources().getString(R.string.google_server_key));


    }

    void initiateDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {

                // For showing a move to my location button
                mMap.setMyLocationEnabled(true);
                try {
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 1000, mLocationListener);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }

                if ( ContextCompat.checkSelfPermission( getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {

                    mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override @NonNull
                        public void onSuccess(android.location.Location location) {
                            LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());

                            // For zooming automatically to the location of the marker
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLoc).zoom(DEFAULT_ZOOM).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    });
                }

            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        mLocationPermissionGranted = true;

    }

    void updateMap()
    {
        try {
            if (jsonPlaceList == null) {
                Log.e(TAG, "Failed to get list of places");
                return;
            } else {
                JSONArray placeList = (JSONArray)jsonPlaceList.get("results");
                for (int i = 0; i < placeList.length(); i++) {
                    JSONObject place = (JSONObject)placeList.get(i);
                    double latitude = place.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    double longitude = place.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                    String name = place.getString("name");
                    String address = place.getString("vicinity");
                    int rating = place.getInt("rating");
                    mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                            .title(name)
                            .snippet(address));

                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

    }



    public void runPlacesRequestTask() {
        new HTTPRequestAsyncTask(getActivity(), mMap, sbPlaceQuery.toString()).execute("");
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    class HTTPRequestAsyncTask extends AsyncTask<String, Void, Boolean> {

        private GoogleMap mMap;
        private Activity contex;
        private String placeQuery;
        private String data;
        BufferedReader reader;

        HTTPRequestAsyncTask(Activity activity, GoogleMap googleMap, String placeQuery)
        {
            this.mMap=googleMap;
            this.contex=activity;
            this.placeQuery=placeQuery;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... urls) {
            return getNearbyPlaces();
        }

        Boolean getNearbyPlaces() {
            try {
                URL url = new URL(placeQuery);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();
                //Log.d(TAG, data);
                br.close();
                return true;

            } catch (Exception e) {
                Log.e("Exception: %s", e.toString());
                return false;
            }
        }

        protected void onPostExecute(Boolean result) {
            try {

                StringReader is = new StringReader(data);
//                InputStream is = getActivity().getResources().openRawResource(R.raw.place_search_response); // For testing without internet
                reader = new BufferedReader(is);

                StringBuffer buffer = new StringBuffer();
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
//                    Log.d("Response: ", "> " + line);
                }
                jsonPlaceList = new JSONObject(buffer.toString());
                //Log.d("JSON Object >", jsonPlaceList.toString());
                updateMap();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}