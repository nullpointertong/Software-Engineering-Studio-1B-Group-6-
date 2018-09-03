package yourteamnumber.seshealthpatient.Model.DataPacket.Models;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import yourteamnumber.seshealthpatient.R;

public class DataPacketAdapter extends RecyclerView.Adapter<DataPacketAdapter.ViewHolder> {

    private Context mContext;
    private List<DataPacket> mDataPacketList;
    CustomItemClickListener listener;


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView number, description, id;

        public ViewHolder(View view) {
            super(view);
            number = (TextView) view.findViewById(R.id.data_packet_num);
            description = (TextView) view.findViewById(R.id.data_packet_description);
            id = (TextView) view.findViewById(R.id.data_packet_id);
        }

    }


    public DataPacketAdapter(Context context, List<DataPacket> dataPacketList, CustomItemClickListener listener) {
        this.mContext = context;
        this.mDataPacketList = dataPacketList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_packet_list_row, parent, false);
        final ViewHolder mViewHolder = new ViewHolder(mView);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, mViewHolder.getPosition());
            }
        });
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DataPacket dataPacket = mDataPacketList.get(position);

        holder.number.setText(String.format(holder.number.getText().toString(), position+1));
        holder.id.setText(String.format(holder.id.getText().toString(), dataPacket.getDataPackedId().toString()));

        if (dataPacket.getTextData() == null) {
            holder.description.setText("");
        } else {
            String temp = dataPacket.getTextData().getData();
            if (temp.length() < 20) {
                holder.description.setText(dataPacket.getTextData().getData());
            } else {
                holder.description.setText(dataPacket.getTextData().getData().substring(0, 18) + "...");
            }
        }

    }

    @Override
    public int getItemCount() {
        return mDataPacketList.size();
    }
}
