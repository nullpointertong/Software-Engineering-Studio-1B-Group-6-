package yourteamnumber.seshealthpatient.Fragments;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import yourteamnumber.seshealthpatient.Activities.RegisterActivity;
import yourteamnumber.seshealthpatient.R;

/**
 * Class: PatientInformationFragment
 * Extends: {@link Fragment}
 * Author: Carlos Tirado < Carlos.TiradoCorts@uts.edu.au> and YOU!
 * Description:
 * <p>
 * This fragment's job will be that to display patients information, and be able to edit that
 * information (either edit it in this fragment or a new fragment, up to you!)
 * <p>

 */
public class PatientInformationFragment extends Fragment {


    // Note how Butter Knife also works on Fragments, but here it is a little different
    private TextView patient_firstName;   //Initailization of Variables required
    private TextView patient_lastName;
    private TextView patient_gender;
    private TextView patient_height;  //Initailization of Variables required
    private TextView patient_weight;
    private TextView patient_medicalCondition;

    private FirebaseAuth firebaseAuth;

    public PatientInformationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(getActivity());
        //TODO: Instead of hardcoding the title perhaps take the user name from somewhere?
        // Note the use of getActivity() to reference the Activity holding this fragment

        getActivity().setTitle("Username Information");



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_patient_information, container, false);

        // Note how we are telling butter knife to bind during the on create view method
        ButterKnife.bind(this, v);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Now that the view has been created, we can use butter knife functionality
        patient_firstName = getActivity().findViewById(R.id.patient_firstName);
        patient_lastName = getActivity().findViewById(R.id.patient_lastName);
        patient_gender = getActivity().findViewById(R.id.patient_gender);
        patient_height = getActivity().findViewById(R.id.patient_height);
        patient_weight = getActivity().findViewById(R.id.patient_weight);
        patient_medicalCondition = getActivity().findViewById(R.id.patient_medicalCondition);

        firebaseAuth = FirebaseAuth.getInstance();

        String userId = firebaseAuth.getUid();
        DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child("user_id").child(userId);
        patient_firstName.setText(" ");
        patient_lastName.setText(" ");
        patient_gender.setText(" ");
        patient_height.setText(" ");
        patient_weight.setText(" ");
        patient_medicalCondition.setText(" ");
        currentUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                patient_firstName.setText(dataSnapshot.child("First Name").getValue().toString());
                patient_lastName.setText(dataSnapshot.child("Last Name").getValue().toString());
                patient_gender.setText(dataSnapshot.child("Gender").getValue().toString());
                patient_height.setText(dataSnapshot.child("Height").getValue().toString());
                patient_weight.setText(dataSnapshot.child("Weight").getValue().toString());
                patient_medicalCondition.setText(dataSnapshot.child("Medical Condition").getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "ERROR!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.swapButton)
    public void switchToEdit() {
        Fragment newFragment = new UpdatePatientInformationFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
