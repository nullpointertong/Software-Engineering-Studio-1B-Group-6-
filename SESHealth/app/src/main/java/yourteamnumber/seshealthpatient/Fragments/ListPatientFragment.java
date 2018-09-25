package yourteamnumber.seshealthpatient.Fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import yourteamnumber.seshealthpatient.Model.DataPacket.Models.CustomItemClickListener;
import yourteamnumber.seshealthpatient.Model.DataPacket.Models.Patient;
import yourteamnumber.seshealthpatient.Model.DataPacket.Models.PatientAdapter;
import yourteamnumber.seshealthpatient.R;


public class ListPatientFragment extends Fragment {
    public ArrayList<Patient> mPatientList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private PatientAdapter mAdapter;
    public ListPatientFragment() {
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
        return inflater.inflate(R.layout.fragment_list_patient, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.patient_recycler_view);

        mAdapter = new PatientAdapter(this.getContext(), mPatientList, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d("List Patient Button", "clicked position:" + position);
                Fragment listDataPacketFragment = new ListDataPacketFragment();
                Bundle bundle = new Bundle();
                bundle.putString("PatientID", mPatientList.get(position).getPatientId());
                listDataPacketFragment.setArguments(bundle);
                getActivity().getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, listDataPacketFragment)
                        .addToBackStack(null)
                        .commit();

            }});
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        getAllPatientData();

    }
    private void getAllPatientData() {
        mPatientList.clear();
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userUid = currentFirebaseUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        ref.child("Users").child("user_id").child("C55Zfl4kK7YG5epI1I1ub04rdGR2").child("MyPatients").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot patientIDSnapshot : dataSnapshot.getChildren())
                {
                    ArrayList<Patient> patientList = new ArrayList<Patient> ();
                    String patientID = patientIDSnapshot.getValue(String.class);

                    getPatientInfo(patientID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPatientInfo(String patientID)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.child("Users").child("user_id").child(patientID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String firstName = "";
                String lastName = "";
                String gender = "";
                String medicalCondition = "";
                double height = 0;
                double weight = 0;

                for (DataSnapshot info : snapshot.getChildren())
                {
                    if (info.getKey().toString().equals("First Name")) { firstName = info.getValue().toString(); }
                    if (info.getKey().toString().equals("Last Name")) { lastName = info.getValue().toString(); }
                    if (info.getKey().toString().equals("Gender")) { gender = info.getValue().toString(); }
                    if (info.getKey().toString().equals("Medical Condition")) { medicalCondition = info.getValue().toString(); }
                    if (info.getKey().toString().equals("Height")) { height = Double.parseDouble(info.getValue().toString()); }
                    if (info.getKey().toString().equals("Weight")) { weight = Double.parseDouble(info.getValue().toString()); }
                }
                mPatientList.add(new Patient(snapshot.getKey().toString(), firstName, lastName, medicalCondition, gender, height, weight));
                mAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
    }
}
