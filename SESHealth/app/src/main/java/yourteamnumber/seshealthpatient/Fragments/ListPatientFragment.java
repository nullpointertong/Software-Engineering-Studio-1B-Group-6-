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

import java.util.ArrayList;

import yourteamnumber.seshealthpatient.Model.DataPacket.Models.CustomItemClickListener;
import yourteamnumber.seshealthpatient.Model.DataPacket.Models.Patient;
import yourteamnumber.seshealthpatient.Model.DataPacket.Models.PatientAdapter;
import yourteamnumber.seshealthpatient.R;


public class ListPatientFragment extends Fragment {
    private ArrayList<Patient> mPatientList = new ArrayList<>();
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
        /*recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
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
        */
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
        if (mPatientList.size() == 0) preparePatientData();
    }
    private void preparePatientData() {
        Patient patient = new Patient("No3rPDJhZWWqx9l9fuOBCASUW5K2", "Jimmy", "Rhee", "medical condition", "male", 123, 70);
        mPatientList.add(patient);
        Patient patient1 = new Patient("prUzSlAKEUgCvlsY9mViuR8np3m1", "Jimmy", "Rhee", "medical condition", "male", 123, 70);
        mPatientList.add(patient1);
    }

}
