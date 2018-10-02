package yourteamnumber.seshealthpatient.Fragments;

import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import yourteamnumber.seshealthpatient.Model.DataPacket.Models.CustomItemClickListener;
import yourteamnumber.seshealthpatient.Model.DataPacket.Models.DataPacket;
import yourteamnumber.seshealthpatient.Model.DataPacket.Models.DataPacketAdapter;
import yourteamnumber.seshealthpatient.R;


public class ListDataPacketFragment extends Fragment {

    private List<DataPacket> mDataPacketList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DataPacketAdapter mAdapter;
    private DataPacket dataPacket;
    private String patientID;
    private String doctorID;
    private String patientName;
    private boolean isDoctor = false;
    private final static String TAG = "Failed";

    public ListDataPacketFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (getArguments() != null) {
            if (getArguments().getString("PatientID") != null) {
                patientID = getArguments().getString("PatientID");
                doctorID = currentFirebaseUser.getUid();
                isDoctor = true;
            }
            else {
                patientID = currentFirebaseUser.getUid();
            }

            patientName = getArguments().getString("patientName");
        }
        else {
            patientID = currentFirebaseUser.getUid();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_data_packet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        mAdapter = new DataPacketAdapter(this.getContext(), mDataPacketList, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d(TAG, "clicked position:" + position);
                Fragment viewDataPacketFragment = new ViewDataPacketFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("data_packet", mDataPacketList.get(position));
                if (patientName != null)
                    bundle.putString("patient_name", patientName);
                bundle.putString("patient_id", patientID);
                viewDataPacketFragment.setArguments(bundle);
                getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, viewDataPacketFragment)
                .addToBackStack(null)
                .commit();

            }});
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        // Get a reference to our posts
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        ref.child("DataPackets").child(patientID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDataPacketList.clear();
                for (DataSnapshot datapacketDataSnapshot : dataSnapshot.getChildren()) {
                    DataPacket dataPacket = datapacketDataSnapshot.getValue(DataPacket.class);
                    if (isDoctor)
                    {
                        if (!datapacketDataSnapshot.child("doctorId").getValue().toString().equals(doctorID))
                            continue;
                    }
                    mDataPacketList.add(dataPacket);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadDatapacket:onCancelled", databaseError.toException());
                Toast.makeText(getActivity(), "Failed to retrieve data packets.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
