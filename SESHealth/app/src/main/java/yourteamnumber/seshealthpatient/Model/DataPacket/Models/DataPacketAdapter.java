package yourteamnumber.seshealthpatient.Model.DataPacket.Models;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import yourteamnumber.seshealthpatient.R;

public class DataPacketAdapter extends RecyclerView.Adapter<DataPacketAdapter.ViewHolder> {

    private Context mContext;
    private List<DataPacket> mDataPacketList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView id, description, date;

        public ViewHolder(View view) {
            super(view);
            id = (TextView) view.findViewById(R.id.data_packet_id);
            description = (TextView) view.findViewById(R.id.data_packet_description);
            date = (TextView) view.findViewById(R.id.data_packet_date);
        }
    }


    public DataPacketAdapter(Context context, List<DataPacket> dataPacketList) {
        this.mContext = context;
        this.mDataPacketList = dataPacketList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.data_packet_list_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DataPacket dataPacket = mDataPacketList.get(position);
        holder.id.setText(dataPacket.getDataPackedId().toString());
    }

    @Override
    public int getItemCount() {
        return mDataPacketList.size();
    }
}
