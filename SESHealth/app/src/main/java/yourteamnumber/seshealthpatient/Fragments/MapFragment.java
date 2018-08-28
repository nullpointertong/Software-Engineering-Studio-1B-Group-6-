package yourteamnumber.seshealthpatient.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import yourteamnumber.seshealthpatient.Activities.MainActivity;
import yourteamnumber.seshealthpatient.R;
import yourteamnumber.seshealthpatient.Model.DataPacket.Models.MedicalFacility;



/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {

    MapView mMapView;
    private GoogleMap mMap;
    private Location mLastKnownLocation;
    private boolean mLocationPermissionGranted;
    private static final int DEFAULT_ZOOM = 14;
    private final LatLng mDefaultLocation = new LatLng(-33.8840504, 151.1992254);

    private static FirebaseDatabase database;
    private DatabaseReference facilitiesRef;
    private List<MedicalFacility> mFacilities = new ArrayList<MedicalFacility>();
    public MapFragment() {
        database = FirebaseDatabase.getInstance();
        facilitiesRef = database.getReference().child("MedicalFacilities");
        /**
         * Write

        List<String> physicians = new ArrayList<String>();
        physicians.add("Shitty McShitfaced");
        MedicalFacility facility = new MedicalFacility(
                "King Henry VIII",
                "25 Suck It Street",
                physicians,
                new LatLng(-33.963721, 151.224782));
        MedicalFacility facility1 = new MedicalFacility(
                "UTS HEalth Unit",
                "15 Broadway, Ultimo 2007 NSW",
                physicians,
                new LatLng(-33.92324, 151.224243));
        MedicalFacility facility2 = new MedicalFacility(
                "Bing Bong",
                "420 Blaze It Street",
                physicians,
                new LatLng(-33.923321, 151.2232111));
        facilitiesRef.child("0").setValue(facility);
        facilitiesRef.child("1").setValue(facility1);
        facilitiesRef.child("2").setValue(facility2);

         *   Read
         */
        facilitiesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot facilityData : dataSnapshot.getChildren())
                {
                    MedicalFacility facility = facilityData.getValue(MedicalFacility.class);
                    mFacilities.add(facility);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Read from Firebase failed: " + databaseError.getCode() + " "+ databaseError.getMessage());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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

                markFacilities();
            }
        });

        return rootView;
    }

    void markFacilities()
    {
        for (MedicalFacility facility : mFacilities) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(facility.getLatitude(), facility.getLongitude()))
                    .title(facility.getName())
                    .snippet("Address: " + facility.getAddress() + " | Physicians: " + facility.getPhysicians()));
        }
    }

    void initiateDeviceLocation()
    {
        try {
            if (mLocationPermissionGranted) {
                // For showing a move to my location button
                mMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng currentLoc = mDefaultLocation;//new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
                mMap.addMarker(new MarkerOptions().position(currentLoc).title("Current location").snippet("You are here"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLoc).zoom(DEFAULT_ZOOM).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }



    void requestLocationPermission()
    {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "Location permission is required to find nearby facilities!", Toast.LENGTH_LONG);
            mLocationPermissionGranted = false;

        } else {
            mLocationPermissionGranted = true;
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
}
