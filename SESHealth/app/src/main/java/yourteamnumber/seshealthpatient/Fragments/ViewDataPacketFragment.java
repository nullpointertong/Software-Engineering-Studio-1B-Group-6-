package yourteamnumber.seshealthpatient.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import yourteamnumber.seshealthpatient.Model.DataPacket.Models.DataPacket;
import yourteamnumber.seshealthpatient.Model.DataPacket.Models.Location;
import yourteamnumber.seshealthpatient.R;


public class ViewDataPacketFragment extends Fragment {
    private DataPacket dataPacket;
    private MapView map;
    private GoogleMap mMap;

    public ViewDataPacketFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().getSerializable("data_packet") != null)
        {
            dataPacket = (DataPacket) getArguments().getSerializable("data_packet");
        }
        else
        {
            Toast.makeText(this.getContext(), "Error: Data packet cannot be read", Toast.LENGTH_SHORT);
            this.getFragmentManager().popBackStackImmediate();
        }
        //Log.d("Data Packet: ", dataPacket.toString());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_view_data_packet, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
          Display the information from the patient's data packet
          **/
        ((TextView)getActivity().findViewById(R.id.data_packet_id_tv))
                .setText(dataPacket.getDataPackedId());
        ((TextView)getActivity().findViewById(R.id.data_packet_desc_tv))
                .setText(dataPacket.getTextData() != null ? dataPacket.getTextData().getData() : "N.A.");
        ((TextView)getActivity().findViewById(R.id.data_packet_location_tv))
                .setText(dataPacket.getLocation() != null ? dataPacket.getLocation().toString() : "N.A.");
        if (dataPacket.getLocation() != null)
            /** Show the patient location in a map container **/
            showLocation(view, savedInstanceState, dataPacket.getLocation());
        else
            /** If location not available, Hide the map container **/
            getActivity().findViewById(R.id.mapContainer).setVisibility(View.GONE);
        ((TextView)getActivity().findViewById(R.id.data_packet_heartrate_tv))
                .setText(dataPacket.getHeartRate() != null ? dataPacket.getHeartRate().toString() : "N.A.");
        ((TextView)getActivity().findViewById(R.id.data_packet_video_bool_tv))
                .setText(dataPacket.getVideoSnippet() != null ? "Yes" : "No");
        ((TextView)getActivity().findViewById(R.id.data_packet_file_bool_tv))
                .setText(dataPacket.getSupplementaryFiles() != null ? "Yes" : "No");

    }

    private void showLocation(View view, Bundle savedInstanceState, Location location)
    {
        map = view.findViewById(R.id.mapViewPatient);
        map.onCreate(savedInstanceState);

        map.onResume(); // needed to get the map to display immediately

        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                // Move camera to Sydney
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                mMap.addMarker(new MarkerOptions().position(latLng));
            }
        });
    }
}