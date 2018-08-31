package yourteamnumber.seshealthpatient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import yourteamnumber.seshealthpatient.Model.DataPacket.Models.DataPacket;
import yourteamnumber.seshealthpatient.Model.DataPacket.Models.DataPacketAdapter;
import yourteamnumber.seshealthpatient.R;

public class DataPacketActivity extends AppCompatActivity {

    private DataPacket dataPacket;

    private List<DataPacket> mDataPacketList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DataPacketAdapter mAdapter;
    public static final int REQUEST_CODE = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_packet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Passing data using Intent and start AddTrainActivity
                Intent intent = new Intent(DataPacketActivity.this, CreateDataPacketActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.data_packet_recycler_view);
        mAdapter = new DataPacketAdapter(this, mDataPacketList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        prepareDatapackets();
    }

    private void prepareDatapackets() {
        dataPacket = new DataPacket();
        mDataPacketList.add(dataPacket);

        dataPacket = new DataPacket();
        mDataPacketList.add(dataPacket);

        dataPacket = new DataPacket();
        mDataPacketList.add(dataPacket);

        mAdapter.notifyDataSetChanged();

    }

}
