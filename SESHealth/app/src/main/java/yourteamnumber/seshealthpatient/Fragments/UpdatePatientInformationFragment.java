package yourteamnumber.seshealthpatient.Fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import yourteamnumber.seshealthpatient.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdatePatientInformationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdatePatientInformationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;

    private String mParam2;

    private TextView patient_firstName;   //Possible Bug in code as this should be edit text may have been caused by sharing the same name of var
    private TextView patient_lastName;
    private Spinner spinner;
    private TextView patient_height;  //Initailization of Variables required
    private TextView patient_weight;
    private TextView patient_medicalCondition;

    private FirebaseAuth firebaseAuth;


    public UpdatePatientInformationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
     * @return A new instance of fragment UpdatePatientInformationFragment.
     */
    // TODO: Rename and change types and number of parameters
//    public static UpdatePatientInformationFragment newInstance(String param1, String param2) {
//        UpdatePatientInformationFragment fragment = new UpdatePatientInformationFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(getActivity());
        firebaseAuth = FirebaseAuth.getInstance();

        patient_firstName = getActivity().findViewById(R.id.patient_firstName);
        patient_lastName = getActivity().findViewById(R.id.patient_lastName);
        spinner = getActivity().findViewById(R.id.spinner);
        patient_height = getActivity().findViewById(R.id.patient_height);
        patient_weight = getActivity().findViewById(R.id.patient_weight);
        patient_medicalCondition = getActivity().findViewById(R.id.patient_medicalCondition);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_patient_information, container, false);
    }

    @OnClick(R.id.updateButton)
    public void switchToEdit() {
        Map hashMap = new HashMap();
        String userId = firebaseAuth.getUid();
        DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child("user_id").child(userId);

        String firstName = patient_firstName.getText().toString();
        String lastName = patient_lastName.getText().toString();
        String gender = spinner.getSelectedItem().toString();
        String height = patient_height.getText().toString();
        String weight = patient_weight.getText().toString();
        String medicalCondition = patient_medicalCondition.getText().toString();

        hashMap.put("First Name:", firstName); //Type specifier maybe required here
        hashMap.put("Last Name:", lastName);
        hashMap.put("Gender:", gender);
        hashMap.put("Height", height);
        hashMap.put("Weight", weight);
        hashMap.put("Medical Condition", medicalCondition);
        currentUser.setValue(hashMap);


        Fragment fragment = new PatientInformationFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
