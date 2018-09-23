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
        doctor_firstName_et = getActivity().findViewById(R.id.doctor_firstName_et);
        doctor_lastName_et = getActivity().findViewById(R.id.doctor_lastName_et);
        doctor_occupation_et = getActivity().findViewById(R.id.doctor_occupation_et);
        doctor_specialty_et = getActivity().findViewById(R.id.doctor_specialty_et);
        doctor_hospital_et = getActivity().findViewById(R.id.doctor_hospital_et);
        doctor_department_et = getActivity().findViewById(R.id.doctor_department_et);
        firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getUid();
        DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child("user_id").child(userId);
        doctor_firstName_et.setText(" ");
        doctor_lastName_et.setText(" ");
        doctor_occupation_et.setText(" ");
        doctor_specialty_et.setText(" ");
        doctor_hospital_et.setText(" ");
        doctor_department_et.setText(" ");
        currentUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                doctor_firstName_et.setText(dataSnapshot.child("First Name").getValue().toString());
                doctor_lastName_et.setText(dataSnapshot.child("Last Name").getValue().toString());
                doctor_occupation_et.setText(dataSnapshot.child("Occupation").getValue().toString());
                doctor_specialty_et.setText(dataSnapshot.child("Specialty").getValue().toString());
                doctor_hospital_et.setText(dataSnapshot.child("Hospital").getValue().toString());
                doctor_department_et.setText(dataSnapshot.child("Department").getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "ERROR!", Toast.LENGTH_SHORT).show();
            }
        });

        getActivity().findViewById(R.id.doctor_updateButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToEdit();
            }
        });
    }

    @OnClick(R.id.doctor_updateButton)
    public void switchToEdit() {
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

        Fragment fragment = new DoctorInformationFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
