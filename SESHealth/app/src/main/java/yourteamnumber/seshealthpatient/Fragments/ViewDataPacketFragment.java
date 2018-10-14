package yourteamnumber.seshealthpatient.Fragments;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.w3c.dom.Text;

import java.net.Authenticator;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import yourteamnumber.seshealthpatient.Model.DataPacket.Models.DataPacket;
import yourteamnumber.seshealthpatient.Model.DataPacket.Models.Location;
import yourteamnumber.seshealthpatient.R;

/**
* This fragment display the data packet in easy graphic user interface.
* The data packet information includes text input, files, heart rate, video, and location.
*/
public class ViewDataPacketFragment extends Fragment {
    private DataPacket dataPacket;

    private Spinner mTypeSp;
    private EditText mFeedbackTxt;
    private List<String> Typelist;
    private String item;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth currentUser;
    private Button mSendBtn;
    private TextView mPatientIdTV;
    private String dataPacketID;
    private DatabaseReference swithchLayout;
    private TextView mPasseedPatientIDTV;
    private TextView mViewDataPacketTV;
    private List<String> DoctorList;
    private Spinner mDoctorsSp;
    private TextView mFeedbackFromDoctors;
    private DatabaseReference allDoctors;
    private String doctorName;
    private String receivedFeedback;
    private DatabaseReference mDatabaseReference1;
    private DatabaseReference mDatabaseReference2;
    private String UserType;
    private MapView map;
    private GoogleMap mMap;
    private String patientName = null;
    private String patientID = null;
    private StorageReference storageRef;
    private VideoView VideoPlay;
    private TextView UrlHint;
    public ViewDataPacketFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve data packet from previous fragment, i.e. ListDataPacketFragment
        if (getArguments() != null) {
            if (getArguments().getSerializable("data_packet") != null) {
                dataPacket = (DataPacket) getArguments().getSerializable("data_packet");
            } else {
                Toast.makeText(this.getContext(), "Error: Data packet read error", Toast.LENGTH_SHORT);
                this.getFragmentManager().popBackStackImmediate();
            }

            patientName = getArguments().getString("patient_name");
            patientID = getArguments().getString("patient_id");
        }

        storageRef = FirebaseStorage.getInstance().getReference().child(patientID).child(dataPacket.getDataPackedId());

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
        ((TextView)getActivity().findViewById(R.id.date_sent_tv))
                .setText(dataPacket.getDateSent().toString());
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
                    storageRef = FirebaseStorage.getInstance().getReference();
                    storageRef.child(patientID).child(dataPacketID).child("videos").child("Video - 1.mp4").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            UrlHint.setText(uri.toString());
                            VideoPlay.setVideoURI(uri);
                            VideoPlay.start();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception  e) {
                            Toast.makeText(getContext(), "Can not play this Video", Toast.LENGTH_LONG).show();
                        }
                    });

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

        // Data packet UI elements declaration.
        dataPacketID = dataPacket.getDataPackedId();
        mTypeSp = (Spinner) getActivity().findViewById(R.id.dataType_sp);
        mPatientIdTV = (TextView) getActivity().findViewById(R.id.patientID_Txt);
        mPatientIdTV.setText(patientID);
        mFeedbackTxt = (EditText) getActivity().findViewById(R.id.feedback_Txt);
        mSendBtn = (Button) getActivity().findViewById(R.id.sendFeedback_btn);
        mPasseedPatientIDTV = (TextView) getActivity().findViewById(R.id.passed_patient_id_Tv);
        mViewDataPacketTV = (TextView) getActivity().findViewById(R.id.view_data_packet_Txt1);
        VideoPlay = (VideoView) getActivity().findViewById(R.id.video_container);
        UrlHint = (TextView) getActivity().findViewById(R.id.Url_hint);


        mFeedbackFromDoctors = (TextView) getActivity().findViewById(R.id.feedbackFromDoctors_Tv);
        mDoctorsSp = (Spinner) getActivity().findViewById(R.id.patient_choose_doctor_sp);

        currentUser = FirebaseAuth.getInstance();
        String currentID = currentUser.getUid();

        swithchLayout = FirebaseDatabase.getInstance().getReference().child("Users").child("user_id").child(currentID).child("UserType");
        swithchLayout.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { ;
              // If the user is doctor, show UIs in order to send feedback.
                if (dataSnapshot.getValue().toString().equals("Doctor")) {
                    UserType = "Doctor";
                    mTypeSp.setVisibility(View.VISIBLE);
                    mPatientIdTV.setVisibility(View.VISIBLE);
                    mFeedbackTxt.setVisibility(View.VISIBLE);
                    mSendBtn.setVisibility(View.VISIBLE);
                    mPasseedPatientIDTV.setVisibility(View.VISIBLE);
                }
              // If the user is patient, show UIs in order to receive feedback.
                else {
                    UserType = "Patient";
                    mTypeSp.setVisibility(View.VISIBLE);
                    mFeedbackFromDoctors.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("DataPackets").child(patientID).child(dataPacketID);

        // Get all doctors that has been paired with the patient and add to the spinner.
        allDoctors = FirebaseDatabase.getInstance().getReference().child("Users").child("user_id").child(patientID).child("MyDoctors");
        allDoctors.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DoctorList = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    DoctorList.add(String.valueOf(dataSnapshot1.getKey()));
                    int a = DoctorList.size();
                    final ArrayAdapter DoctorAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, DoctorList);
                    DoctorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mDoctorsSp.setAdapter(DoctorAdapter);
                    mDoctorsSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            doctorName = DoctorAdapter.getItem(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Retrieve supplimentary files attached to the data packet from the database.
        Typelist = new ArrayList<>();
        Typelist.add("Choose File Type");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    String spinnerItem = itemSnapshot.getKey();
                    if ((spinnerItem.equals("textData") || spinnerItem.equals("videoSnippet") || spinnerItem.equals("location") ||
                            spinnerItem.equals("heartRate") || spinnerItem.equals("supplementaryFiles") && (!Typelist.contains(spinnerItem)))) {
                        Typelist.add(spinnerItem);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // Retrieve feedback attached to the data packet from the doctor.
        ArrayAdapter<String> TypeAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, Typelist);
        TypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeSp.setAdapter(TypeAdapter);
        mTypeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFeedbackFromDoctors.setText("Nothing received.");
                item = TypeAdapter.getItem(position);
                if (!item.equals("Choose File Type")) {
                    mDatabaseReference2 = FirebaseDatabase.getInstance().getReference().child("DataPackets").child(patientID).child(dataPacketID).child(item);
                    mDatabaseReference2.child("Feedback").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (UserType.equals("Patient")) {
                                if (dataSnapshot.getValue() != null) {
                                    receivedFeedback = dataSnapshot.getValue().toString();
                                    mFeedbackFromDoctors.setText(receivedFeedback);
                                    mFeedbackFromDoctors.setText(receivedFeedback);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    mFeedbackFromDoctors.setText("Please choose a file type.");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Send the feedback to the patient.
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseReference1 = FirebaseDatabase.getInstance().getReference().child("Users").child("user_id").child(currentID);
                mDatabaseReference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (item.equals("Choose File Type")) {
                            Toast.makeText(getContext(), "Please choose a file type", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String feedback = mFeedbackTxt.getText().toString().trim();
                            if (feedback.isEmpty()) {
                                Toast.makeText(getContext(), "Feedback can not be empty", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                mDatabaseReference.child(item).child("Feedback").setValue(feedback);
                                Toast.makeText(getContext(), "Send successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
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

    // this method allow user to download files from the database.
    private void downloadFile(StorageReference storageRef, String filename) {

        File rootPath = new File(Environment.getExternalStorageDirectory(), filename);
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath, filename);

        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebase ",";local temp file created" +localFile.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ",";local temp file not created" +exception.toString());
            }
        });
    }
}
