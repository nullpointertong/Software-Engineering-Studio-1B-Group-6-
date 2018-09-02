package yourteamnumber.seshealthpatient.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import yourteamnumber.seshealthpatient.Model.DataPacket.Models.DataPacket;
import yourteamnumber.seshealthpatient.R;

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
        Log.d("DAta Packet: ", dataPacket.toString());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_view_data_packet, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textView = getActivity().findViewById(R.id.data_packet_id_tv);
        textView.setText(dataPacket.getDataPackedId());

    }

}