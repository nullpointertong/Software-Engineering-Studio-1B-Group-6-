package yourteamnumber.seshealthpatient.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.location.LocationListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yourteamnumber.seshealthpatient.Model.DataPacket.Models.MedicalFacility;
import yourteamnumber.seshealthpatient.R;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {

    MapView mMapView;
    private GoogleMap mMap;
    private LatLng mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean mLocationPermissionGranted;
    private static final int DEFAULT_ZOOM = 14;
    private final LatLng mDefaultLocation = new LatLng(-33.8840504, 151.1992254);


    private LocationManager mLocationManager;
    private final String TAG = "map_fragment";
    private StringBuilder sbPlaceQuery;
    private JSONObject jsonPlaceList;
    private int recommendSpinnerPosition = 0;

    private String userUid = "";
    private boolean isDoctor;
    private String userName;
    List<String> patientIDs = new ArrayList<String>();
    Map<String, String> myPatients = new HashMap<>();
    List<MedicalFacility> recommendedPlaces = new ArrayList<>();

//    private final LocationListener mLocationListener = new LocationListener() {
//        @Override
//        public void onLocationChanged(final Location location) {
//            mLastKnownLocation = location;
////            Log.d(TAG, "My location: "+ mLastKnownLocation.toString());
//            if (mMap.isMyLocationEnabled() && mLocationPermissionGranted) {
//                createPlaceQuery();
//
//            }
//        }
//
//        @Override
//        public void onStatusChanged(String s, int i, Bundle bundle) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String s) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String s) {
//
//        }
//    };

    public MapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Set up location client to get current location of the user **/
        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        /** Check if current user is a doctor or a patient **/
        userUid = FirebaseAuth.getInstance().getUid();
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference currentUser = dataRef.child("Users").child("user_id").child(userUid);
        currentUser.addValueEventListener(
            new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    isDoctor = (dataSnapshot.child("UserType").getValue().toString().equals("Doctor"));
                    userName = (dataSnapshot.child("First Name").getValue().toString()) + " " + (dataSnapshot.child("Last Name").getValue().toString());
                    /** Get patients **/
                    if (isDoctor) {
                        for (DataSnapshot patientSnap : dataSnapshot.child("MyPatients").getChildren()){
                            patientIDs.add(patientSnap.getValue().toString());
                        }
                        for (String patientID : patientIDs) {
                            dataRef.child("Users").child("user_id").child(patientID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    String firstName = "";
                                    String lastName = "";
                                    String gender = "";
                                    String medicalCondition = "";
                                    double height = 0;
                                    double weight = 0;

                                    for (DataSnapshot info : snapshot.getChildren()) {
                                        if (info.getKey().toString().equals("First Name")) {
                                            firstName = info.getValue().toString();
                                        }
                                        if (info.getKey().toString().equals("Last Name")) {
                                            lastName = info.getValue().toString();
                                        }
                                        myPatients.put(patientID, lastName + "," + firstName);
                                    }

                                    if (myPatients.size() > 0) {
                                        /** Set spinner adapter to match patient dictionary **/
                                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                                                getActivity(), android.R.layout.simple_spinner_item, new ArrayList<String>(myPatients.values())
                                        );
                                        spinnerAdapter.setDropDownViewResource(android.R.layout
                                                .simple_spinner_dropdown_item);

                                        Spinner spinner = ((Spinner) getActivity().findViewById(R.id.recommendee));
                                        spinner.setAdapter(spinnerAdapter);
                                        spinnerAdapter.notifyDataSetChanged();
                                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                recommendSpinnerPosition = position;
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError firebaseError) {
                                }
                            });
                        }
                    }
                    /** if patient, get recommended locations **/
                    else {
                        dataRef.child("Recommendations").child(userUid).addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    recommendedPlaces.clear();
                                    for (DataSnapshot recFacSnap : dataSnapshot.getChildren())
                                    {
                                        /** Retrieve recommended location info from firebase **/
                                        String facName = recFacSnap.getKey();
                                        String facAddress = recFacSnap.child("Address").getValue().toString();
                                        double facLat = Double.parseDouble(recFacSnap.child("Location").child("latitude").getValue().toString());
                                        double facLng = Double.parseDouble(recFacSnap.child("Location").child("longitude").getValue().toString());
                                        String doctorName = recFacSnap.child("DoctorName").getValue().toString();
                                        String facRating = recFacSnap.child("Rating").getValue().toString();
                                        String facTypes = recFacSnap.child("Types").getValue().toString();
                                        // Create special marker
                                        mMap.addMarker(new MarkerOptions().position(new LatLng(facLat, facLng))
                                                .title(facAddress).snippet(facName + ";" + facRating + ";" + facTypes)
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                                        // Add to list of recommended places
                                        recommendedPlaces.add(new MedicalFacility(facName, facAddress, facLat, facLng, doctorName));
                                        Button recommendedPlaceTv = new Button(getActivity());
                                        recommendedPlaceTv.setClickable(true);
                                        recommendedPlaceTv.setPadding(0, 10,0,10);
                                        recommendedPlaceTv.setText(Html.fromHtml("<b>" + facName + "</b>" + "<br>" + facAddress + "<br>Recommended by: " + doctorName));
                                        recommendedPlaceTv.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (mMap == null) return;
                                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(facLat, facLng), DEFAULT_ZOOM + 2));
                                                getActivity().findViewById(R.id.recommended_scrollview).setVisibility(View.GONE);
                                            }
                                        });
                                        ((LinearLayout)(getActivity().findViewById(R.id.recommended_list))).addView(recommendedPlaceTv);
                                        recommendedPlaceTv.setPadding(0,5,0,5);

                                    }
                                    if (recommendedPlaces.size() > 0) {
                                        getActivity().findViewById(R.id.patient_recommended_panel).setVisibility(View.VISIBLE);
                                        getActivity().findViewById(R.id.patient_recommended_panel).setOnClickListener(
                                                new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        getActivity().findViewById(R.id.recommend_panel).setVisibility(View.GONE);
                                                        ScrollView scrollView = getActivity().findViewById(R.id.recommended_scrollview);
                                                        if (scrollView.getVisibility() == View.GONE)
                                                            scrollView.setVisibility(View.VISIBLE);
                                                        else
                                                            scrollView.setVisibility(View.GONE);
                                                    }
                                                }
                                        );

                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            }

                        );
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


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
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        // Close the recommended view
                        getActivity().findViewById(R.id.recommended_scrollview).setVisibility(View.GONE);
                        /** Information about the facility is retrieved from the marker snippets
                         *   and copied into the pop up panel
                         **/
                        String address = marker.getTitle();
                        String[] info = marker.getSnippet().split(";");
                        getActivity().findViewById(R.id.recommend_panel).setVisibility(View.VISIBLE);
                        getActivity().findViewById(R.id.doctor_recommend).setVisibility(isDoctor?View.VISIBLE:View.GONE);
                        ((TextView)getActivity().findViewById(R.id.facility_info_name)).setText(info[0]);
                        ((TextView)getActivity().findViewById(R.id.facility_info_address)).setText(address);
                        ((TextView)getActivity().findViewById(R.id.facility_info_rating)).setText("Rating: " + info[1]);
                        try {
                            ((TextView) getActivity().findViewById(R.id.facility_info_type)).setText("Tags: " + info[2] + "nearby");
                        } catch (Exception e) {
                            ((TextView) getActivity().findViewById(R.id.facility_info_type)).setText("");
                        }

                        /** On click, the recommend button will send the currently selected location info
                         *   to the patient selected by the spinner through a dedicated firebase reference.
                         */
                        getActivity().findViewById(R.id.recommend_button).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /**
                                 * Set Recommendations on the database under [ID of patient] > [Name of facility]
                                 * Patient ID is retrieved from a dictionary corresponding to the Spinner position.
                                 **/
                                DatabaseReference recommendedFacilityRef = dataRef.child("Recommendations").child(patientIDs.get(recommendSpinnerPosition)).child(info[0]);

                                /** The rest of the facility info **/
                                recommendedFacilityRef.child("Location").setValue(marker.getPosition());
                                recommendedFacilityRef.child("Address").setValue(address);
                                recommendedFacilityRef.child("Rating").setValue(info[1]);
                                recommendedFacilityRef.child("Types").setValue(info[2]);

                                /** Recommending doctor **/
                                recommendedFacilityRef.child("DoctorID").setValue(userUid);
                                recommendedFacilityRef.child("DoctorName").setValue(userName);

                                Toast.makeText(getContext(), "Successfully shared location with patient.", Toast.LENGTH_LONG);
                            }
                        });
                        return true;
                    }
                });

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        getActivity().findViewById(R.id.recommend_panel).setVisibility(View.GONE);
                        getActivity().findViewById(R.id.recommended_scrollview).setVisibility(View.GONE);
                    }
                });
            }
        });
        return rootView;
    }

    void createPlaceQuery() {
        /** Create
         * Create query to get medical facilities through google's Nearby Search function
         */
        sbPlaceQuery = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");

        sbPlaceQuery.append("location=" + mLastKnownLocation.latitude + "," + mLastKnownLocation.longitude);
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
//                try {
//                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 1000, mLocationListener);
//                } catch (Exception e) {
//                    Log.d(TAG, e.toString());
//                }

                if ( ContextCompat.checkSelfPermission( getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {

                    mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override @NonNull
                        public void onSuccess(android.location.Location location) {
                            LatLng currentLoc = mDefaultLocation;
                            if (location != null)
                                currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
                            else
                                Toast.makeText(getActivity(), "Location could not be retrieved.", Toast.LENGTH_SHORT);
                            mLastKnownLocation = currentLoc;
                            createPlaceQuery();
                            runPlacesRequestTask();
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
                    boolean recommended = false;
                    for (MedicalFacility fac: recommendedPlaces)
                    {
                        if (latitude == fac.getLatitude() && longitude == fac.getLongitude())
                        {
                            recommended = true;
                            break;
                        }
                    }
                    if (recommended)
                        continue;
                    String name = place.getString("name");
                    String address = place.getString("vicinity");
                    int rating = place.getInt("rating");
                    JSONArray types = place.getJSONArray("types");
                    int j;
                    String facilityTypes = "";
                    for (j = 0; j < types.length(); j++)
                    {
                        // Facility types e.g. dentist, health, physiotherapy
                        String type = types.get(j).toString();
                        if (!type.equals("establishment") && !type.equals("point_of_interest"))
                            facilityTypes += type + ", ";
                    }
                    mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                            .title(address).snippet(name + ";" + rating + ";" + facilityTypes));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

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

    /**
     * Below are methods and classes for retrieving nearby medical facilities from Google.
     * Uses HTTP to get results from a Google Places request.
     */
    public void runPlacesRequestTask() {
        new HTTPRequestAsyncTask(getActivity(), mMap, sbPlaceQuery.toString()).execute("");
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
                Log.d(TAG, "Getting nearby medical facilities...");
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
                Log.d(TAG, data);
                br.close();
                return true;

            } catch (Exception e) {
                Log.e(TAG, String.format("Exception: %s", e.toString()));
                return false;
            }
        }

        protected void onPostExecute(Boolean result) {
            try {

                StringReader is = new StringReader(data);

                reader = new BufferedReader(is);

                StringBuffer buffer = new StringBuffer();
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                }
                jsonPlaceList = new JSONObject(buffer.toString());
                updateMap();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}