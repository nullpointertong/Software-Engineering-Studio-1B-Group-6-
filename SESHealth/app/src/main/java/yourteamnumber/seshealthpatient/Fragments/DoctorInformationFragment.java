package yourteamnumber.seshealthpatient.Fragments;

import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
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

public class DoctorInformationFragment extends Fragment {
    private TextView doctor_firstName;   //Possible Bug in code as this should be edit text may have been caused by sharing the same name of var
    private TextView doctor_lastName;
    private TextView doctor_occupation;  //Initailization of Variables required
    private TextView doctor_specialty;
    private TextView doctor_hospital;
    private TextView doctor_department;   //Initailization of Variables required

    private FirebaseAuth firebaseAuth;

    public DoctorInformationFragment() {
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

        return inflater.inflate(R.layout.fragment_doctor_information, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();

        doctor_firstName = getActivity().findViewById(R.id.doctor_firstName);
        doctor_lastName = getActivity().findViewById(R.id.doctor_lastName);
        doctor_occupation = getActivity().findViewById(R.id.doctor_occupation);
        doctor_specialty = getActivity().findViewById(R.id.doctor_specialty);
        doctor_hospital = getActivity().findViewById(R.id.doctor_hospital);
        doctor_department = getActivity().findViewById(R.id.doctor_department);

        String userId = firebaseAuth.getUid();
        DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child("user_id").child(userId);
        doctor_firstName.setText(" ");
        doctor_lastName.setText(" ");
        doctor_occupation.setText(" ");
        doctor_specialty.setText(" ");
        doctor_hospital.setText(" ");
        doctor_department.setText(" ");
        currentUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                doctor_firstName.setText(dataSnapshot.child("First Name").getValue().toString());
                doctor_lastName.setText(dataSnapshot.child("Last Name").getValue().toString());
                doctor_occupation.setText(dataSnapshot.child("Occupation").getValue().toString());
                doctor_specialty.setText(dataSnapshot.child("Specialty").getValue().toString());
                doctor_hospital.setText(dataSnapshot.child("Hospital").getValue().toString());
                doctor_department.setText(dataSnapshot.child("Department").getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "ERROR!", Toast.LENGTH_SHORT).show();
            }
        });

        getActivity().findViewById(R.id.edit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToEdit();
            }
        });
    }

    @OnClick(R.id.edit_button)
    public void switchToEdit() {
        Fragment newFragment = new UpdateDoctorInformationFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
