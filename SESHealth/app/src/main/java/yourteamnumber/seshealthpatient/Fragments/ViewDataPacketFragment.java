package yourteamnumber.seshealthpatient.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.widget.Toast;

import yourteamnumber.seshealthpatient.Model.DataPacket.Models.DataPacket;

public class ViewDataPacketFragment extends Fragment {
    private DataPacket dataPacket;

    public ViewDataPacketFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().getSerializable("data_packet") != null)
        {
            dataPacket = (DataPacket) getArguments().getSerializable("data_packet");
        }
        else
        {
            Toast.makeText(this.getContext(), "Error: Data packet cannot be read", Toast.LENGTH_SHORT);
            this.getFragmentManager().popBackStackImmediate();
        }

    }



}