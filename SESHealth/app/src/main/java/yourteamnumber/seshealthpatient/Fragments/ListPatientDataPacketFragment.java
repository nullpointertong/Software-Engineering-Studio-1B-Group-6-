package yourteamnumber.seshealthpatient.Fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

public class ListPatientDataPacketFragment extends Fragment {
    private List<DataPacket> mDataPacketList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DataPacketAdapter mAdapter;
    private DataPacket dataPacket;
    private String patientID;
    private final static String TAG = "Faild";

    public ListPatientDataPacketFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().getString("patient") != null)
        {
            patientID = getArguments().getString("patient");
        }
        else
        {
            Toast.makeText(this.getContext(), "Error: Data packet cannot be read", Toast.LENGTH_SHORT);
            this.getFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_patient_data_packet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        mAdapter = new DataPacketAdapter(this.getContext(), mDataPacketList, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d(TAG, "clicked position:" + position);
                Fragment viewDataPacketFragment = new ViewDataPacketFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("data_packet", mDataPacketList.get(position));
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
