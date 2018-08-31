package yourteamnumber.seshealthpatient.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import yourteamnumber.seshealthpatient.R;

public class CreateDataPacketActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_data_packet);

        EditText heartrate_et= findViewById(R.id.heartrate_et);
        EditText location_et = findViewById(R.id.location_et);
        Button attach_file_btn = findViewById(R.id.attach_file_button);
        Button record_video_btn = findViewById(R.id.record_button);
        Button add_btn = findViewById(R.id.create_data_packet_add_button);
        Button cancel_btn = findViewById(R.id.create_data_packet_cancel_button);

        heartrate_et.setOnClickListener(this);
        location_et.setOnClickListener(this);
        attach_file_btn.setOnClickListener(this);
        record_video_btn.setOnClickListener(this);
        add_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.heartrate_et:
                Intent intent = new Intent(CreateDataPacketActivity.this, HeartRateActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.location_et:
                intent = new Intent(CreateDataPacketActivity.this, MapActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.attach_file_button:
                intent = new Intent(CreateDataPacketActivity.this, SendFileActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.record_button:
                intent = new Intent(CreateDataPacketActivity.this, RecordVideoActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.create_data_packet_add_button:
                intent = new Intent(CreateDataPacketActivity.this, DataPacketActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.create_data_packet_cancel_button:
                intent = new Intent(CreateDataPacketActivity.this, DataPacketActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
        }

    }
}
