package yourteamnumber.seshealthpatient.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import yourteamnumber.seshealthpatient.Model.DataPacket.Models.DataPacket;
import yourteamnumber.seshealthpatient.Model.DataPacket.Models.Location;
import yourteamnumber.seshealthpatient.R;


public class ViewDataPacketFragment extends Fragment {
    private DataPacket dataPacket;
    private MapView map;
    private GoogleMap mMap;
    private String patientName = null;
    private StorageReference storageRef;

    public ViewDataPacketFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().getSerializable("data_packet") != null) {
                dataPacket = (DataPacket) getArguments().getSerializable("data_packet");
            } else {
                Toast.makeText(this.getContext(), "Error: Data packet read error", Toast.LENGTH_SHORT);
                this.getFragmentManager().popBackStackImmediate();
            }

            patientName = getArguments().getString("patient_name");
        }

        storageRef = FirebaseStorage.getInstance().getReference();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_view_data_packet, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /** Display patient name on top of the packet (if doctor side) **/
        if (patientName != null)
            ((TextView)getActivity().findViewById(R.id.patient_name)).setText(patientName);
        else
            getActivity().findViewById(R.id.patient_name).setVisibility(View.GONE);
        /**
          Display the information from the patient's data packet
          **/
        ((TextView)getActivity().findViewById(R.id.data_packet_id_tv))
                .setText(dataPacket.getDataPackedId());
        ((TextView)getActivity().findViewById(R.id.data_packet_desc_tv))
                .setText(dataPacket.getTextData() != null ? dataPacket.getTextData().getData() : "N.A.");
        ((TextView)getActivity().findViewById(R.id.data_packet_location_tv))
                .setText(dataPacket.getLocation() != null ? "" : "N.A.");

        if (dataPacket.getLocation() != null)
            /** Show the patient location in a map container **/
            showLocation(view, savedInstanceState, dataPacket.getLocation());
        else
            /** If location not available, Hide the map container **/
            getActivity().findViewById(R.id.mapContainer).setVisibility(View.GONE);

        ((TextView)getActivity().findViewById(R.id.data_packet_heartrate_tv))
                .setText(dataPacket.getHeartRate() != null ? dataPacket.getHeartRate().toString() : "N.A.");
        Button videoSnippetBtn = ((Button)getActivity().findViewById(R.id.data_packet_video_button));
        if (dataPacket.getVideoSnippet() != null) {
            videoSnippetBtn.setText("DOWNLOAD");
            videoSnippetBtn.setClickable(true);
            videoSnippetBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StorageReference videoRef = storageRef.child(dataPacket.getDataPackedId().toString()).child("videos");
                    //downloadFile(videoRef);

                }
            });
        }
        Button suppFilesBtn = ((Button)getActivity().findViewById(R.id.data_packet_file_button));
        if (dataPacket.getSupplementaryFiles() != null) {
            suppFilesBtn.setText("DOWNLOAD");
            suppFilesBtn.setClickable(true);
            suppFilesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

    }

    private void showLocation(View view, Bundle savedInstanceState, Location location)
    {
        /** Show the location of the patient shared in the data packet in a map container **/
        map = view.findViewById(R.id.mapViewPatient);
        map.onCreate(savedInstanceState);

        map.onResume(); /** needed to get the map to display immediately **/

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

    private void downloadFile(StorageReference storageRef) {

        File rootPath = new File(Environment.getExternalStorageDirectory(), "imageName.txt");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath,"imageName.txt");

        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebase ",";local temp file created" +localFile.toString());
                //  updateDb(timestamp,localFile.toString(),position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ",";local temp file not created" +exception.toString());
            }
        });
    }
}