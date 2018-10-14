package yourteamnumber.seshealthpatient.Fragments;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import butterknife.OnClick;
import yourteamnumber.seshealthpatient.R;

public class UpdateDoctorInformationFragment extends Fragment {
    //Create Global Variables with the same name as XML Attributes
    private TextView doctor_firstName_et;   //Possible Bug in code as this should be edit text may have been caused by sharing the same name of var
    private TextView doctor_lastName_et;
    private TextView doctor_occupation_et;
    private TextView doctor_specialty_et;
    private TextView doctor_hospital_et;  //Initailization of Variables required
    private TextView doctor_department_et;


    private FirebaseAuth firebaseAuth;


    public UpdateDoctorInformationFragment() {
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
        return inflater.inflate(R.layout.fragment_update_doctor_information, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //Bind Text variables to XML
        doctor_firstName_et = getActivity().findViewById(R.id.doctor_firstName_et);
        doctor_lastName_et = getActivity().findViewById(R.id.doctor_lastName_et);
        doctor_occupation_et = getActivity().findViewById(R.id.doctor_occupation_et);
        doctor_specialty_et = getActivity().findViewById(R.id.doctor_specialty_et);
        doctor_hospital_et = getActivity().findViewById(R.id.doctor_hospital_et);
        doctor_department_et = getActivity().findViewById(R.id.doctor_department_et);
        firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getUid();
        DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child("user_id").child(userId);
        //Set Edit Text to Blank(to prevent nullpointer)
        doctor_firstName_et.setText(" ");
        doctor_lastName_et.setText(" ");
        doctor_occupation_et.setText(" ");
        doctor_specialty_et.setText(" ");
        doctor_hospital_et.setText(" ");
        doctor_department_et.setText(" ");
        currentUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Override and Set Edittext field to branch in Firebase Tree
                doctor_firstName_et.setText(dataSnapshot.child("First Name").getValue().toString());
                doctor_lastName_et.setText(dataSnapshot.child("Last Name").getValue().toString());
                doctor_occupation_et.setText(dataSnapshot.child("Occupation").getValue().toString());
                doctor_specialty_et.setText(dataSnapshot.child("Specialty").getValue().toString());
                doctor_hospital_et.setText(dataSnapshot.child("Hospital").getValue().toString());
                doctor_department_et.setText(dataSnapshot.child("Department").getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //If the operation is cancelled create an Error Message
                Toast.makeText(getContext(), "ERROR!", Toast.LENGTH_SHORT).show();
            }
        });

        getActivity().findViewById(R.id.doctor_updateButton).setOnClickListener(new View.OnClickListener() {
            //Switch to Update Patient Information Fragment to allow Information to update
            @Override
            public void onClick(View v) {
                switchToView();
            }
        });
    }

    @OnClick(R.id.doctor_updateButton)
    public void switchToView() {
        Map hashMap = new HashMap();
        String userId = firebaseAuth.getUid();
        DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child("user_id").child(userId);

        String firstName = doctor_firstName_et.getText().toString();
        String lastName = doctor_lastName_et.getText().toString();
        String occupation =  doctor_occupation_et.getText().toString();
        String specialty = doctor_specialty_et.getText().toString();
        String hospital = doctor_hospital_et.getText().toString();
        String department = doctor_department_et.getText().toString();

        hashMap.put("First Name", firstName); //Type specifier maybe required here
        hashMap.put("Last Name", lastName);
        hashMap.put("Occupation", occupation);
        hashMap.put("Specialty", specialty);
        hashMap.put("Hospital", hospital);
        hashMap.put("Department", department);

        currentUser.updateChildren(hashMap);
        //Put contents of Edittext into Hashmap and update the Firebase Tree using the hashmap

        Fragment fragment = new DoctorInformationFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        //Return to Information Fragment
    }
}
