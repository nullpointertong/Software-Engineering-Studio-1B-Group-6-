package yourteamnumber.seshealthpatient.Fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import yourteamnumber.seshealthpatient.R;

public class ViewMyDoctorsFragment extends Fragment {

    private Button mAddDoctor;
    private Button mSearchDoctor;
    private TextView mResult;
    private EditText mDoctorID;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mFirebaseDatabase;
    private ListView mDoctorList;
    private ArrayList<String> Doctor = new ArrayList<>();

    public ViewMyDoctorsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mydoctors, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDoctorList = getActivity().findViewById(R.id.doctor_list);
        mAddDoctor = getActivity().findViewById(R.id.addDoctor_btn);
        mDoctorID = getActivity().findViewById(R.id.doctorID_txt);
        mSearchDoctor =getActivity().findViewById(R.id.searchDoctor_btn);
        mResult = getActivity().findViewById(R.id.result_txt);
        mDoctorList = getActivity().findViewById(R.id.doctor_list);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, Doctor);
        mDoctorList.setAdapter(arrayAdapter);

        mFirebaseAuth = FirebaseAuth.getInstance();

        String patientId = mFirebaseAuth.getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("user_id");

        mFirebaseDatabase.child(patientId).child("MyDoctors").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String value = "name: " + dataSnapshot.getKey() + "\nid: " + dataSnapshot.getValue(String.class);
                Doctor.add(value);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSearchDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String doctorId = mDoctorID.getText().toString().trim();
                mFirebaseDatabase.child(doctorId).child("First Name").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String doctorName = dataSnapshot.getValue().toString();
                        mResult.setText(doctorName);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        mAddDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String doctorId = mDoctorID.getText().toString().trim();
                if(patientId != null && doctorId != null) {
                    mFirebaseDatabase.child(patientId).child("MyDoctors").child(mResult.getText().toString()).setValue(doctorId);
                    mFirebaseDatabase.child(doctorId).child("MyPatients").child(patientId).setValue(patientId);
                }

            }
        });
    }
}