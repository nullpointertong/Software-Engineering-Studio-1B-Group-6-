package yourteamnumber.seshealthpatient.Fragments;

import android.app.Fragment;
import android.os.Bundle;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.net.Authenticator;
import java.util.ArrayList;
import java.util.List;

import yourteamnumber.seshealthpatient.Model.DataPacket.Models.DataPacket;
import yourteamnumber.seshealthpatient.R;

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
    private String patientID;
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

    public ViewDataPacketFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().getSerializable("data_packet") != null && getArguments().getString("patientID") != null)
        {
            dataPacket = (DataPacket) getArguments().getSerializable("data_packet");
            patientID = getArguments().getString("patientID");
            //Toast.makeText(getContext(), " " + patientID, Toast.LENGTH_SHORT).show();
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

        ((TextView)getActivity().findViewById(R.id.date_sent_tv))
                .setText(dataPacket.getDateSent().toString());
        ((TextView)getActivity().findViewById(R.id.data_packet_desc_tv))
                .setText(dataPacket.getTextData() != null ? dataPacket.getTextData().getData() : "N.A.");
        ((TextView)getActivity().findViewById(R.id.data_packet_location_tv))
                .setText(dataPacket.getLocation() != null ? dataPacket.getLocation().toString() : "N.A.");
        ((TextView)getActivity().findViewById(R.id.data_packet_heartrate_tv))
                .setText(dataPacket.getHeartRate() != null ? dataPacket.getHeartRate().toString() : "N.A.");
        ((TextView)getActivity().findViewById(R.id.data_packet_video_bool_tv))
                .setText(dataPacket.getVideoSnippet() != null ? "Yes" : "No");
        ((TextView)getActivity().findViewById(R.id.data_packet_file_bool_tv))
                .setText(dataPacket.getSupplementaryFiles() != null ? "Yes" : "No");

        dataPacketID = dataPacket.getDataPackedId();
        mTypeSp = (Spinner) getActivity().findViewById(R.id.dataType_sp);
        mPatientIdTV = (TextView) getActivity().findViewById(R.id.patientID_Txt);
        mPatientIdTV.setText(patientID);
        mFeedbackTxt = (EditText) getActivity().findViewById(R.id.feedback_Txt);
        mSendBtn = (Button) getActivity().findViewById(R.id.sendFeedback_btn);
        mPasseedPatientIDTV = (TextView) getActivity().findViewById(R.id.passed_patient_id_Tv);
        mViewDataPacketTV = (TextView) getActivity().findViewById(R.id.view_data_packet_Txt1);

        mFeedbackFromDoctors = (TextView) getActivity().findViewById(R.id.feedbackFromDoctors_Tv);
        mDoctorsSp = (Spinner) getActivity().findViewById(R.id.patient_choose_doctor_sp);

        currentUser = FirebaseAuth.getInstance();
        String currentID = currentUser.getUid();

        swithchLayout = FirebaseDatabase.getInstance().getReference().child("Users").child("user_id").child(currentID).child("UserType");
        swithchLayout.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { ;
                //Toast.makeText(getContext(), "" + type, Toast.LENGTH_SHORT).show();
                if (dataSnapshot.getValue().toString().equals("Doctor")) {
                    UserType = "Doctor";
                    mTypeSp.setVisibility(View.VISIBLE);
                    mPatientIdTV.setVisibility(View.VISIBLE);
                    mFeedbackTxt.setVisibility(View.VISIBLE);
                    mSendBtn.setVisibility(View.VISIBLE);
                    mPasseedPatientIDTV.setVisibility(View.VISIBLE);
                }
                else {
                    UserType = "Patient";
                    mDoctorsSp.setVisibility(View.VISIBLE);
                    mTypeSp.setVisibility(View.VISIBLE);
                    mFeedbackFromDoctors.setVisibility(View.VISIBLE);
                    mViewDataPacketTV.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("DataPackets").child(patientID).child(dataPacketID);


        allDoctors = FirebaseDatabase.getInstance().getReference().child("Users").child("user_id").child(patientID).child("MyDoctors");
        allDoctors.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DoctorList = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    DoctorList.add(String.valueOf(dataSnapshot1.getKey()));
                    int a = DoctorList.size();
                    //Toast.makeText(getContext(), " " + a, Toast.LENGTH_LONG).show();
                    final ArrayAdapter DoctorAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, DoctorList);
                    DoctorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mDoctorsSp.setAdapter(DoctorAdapter);
                    mDoctorsSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            doctorName = DoctorAdapter.getItem(position).toString();
                            //Toast.makeText(getContext(), "It will show all feedbacks from " + doctorName, Toast.LENGTH_LONG).show();
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

        Typelist = new ArrayList<>();
        Typelist.add("heartRate");
        Typelist.add("location");
        Typelist.add("supplementaryFiles");
        Typelist.add("textData");
        Typelist.add("videoSnippet");

        final ArrayAdapter<String> TypeAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, Typelist);
        TypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeSp.setAdapter(TypeAdapter);
        mTypeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item = TypeAdapter.getItem(position);
                mDatabaseReference2 = FirebaseDatabase.getInstance().getReference().child("DataPackets").child(patientID).child(dataPacketID).child(item);
                mDatabaseReference2.child("Feedback").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (UserType.equals("Patient")) {
                            if (dataSnapshot.child(doctorName).getValue() != null) {
                                receivedFeedback = dataSnapshot.child(doctorName).getValue().toString();
                                mFeedbackFromDoctors.setText(receivedFeedback);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                //Toast.makeText(getContext(), "Feedback for " + item, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseReference1 = FirebaseDatabase.getInstance().getReference().child("Users").child("user_id").child(currentID);
                mDatabaseReference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String currentDoctorName = dataSnapshot.child("First Name").getValue().toString() + " " + dataSnapshot.child("Last Name").getValue().toString();
                        String feedback = mFeedbackTxt.getText().toString().trim();
                        mDatabaseReference.child(item).child("Feedback").child(currentDoctorName).setValue(feedback);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
    }
}