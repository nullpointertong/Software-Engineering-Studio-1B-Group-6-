package yourteamnumber.seshealthpatient.Activities;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import yourteamnumber.seshealthpatient.R;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button edit_profile= findViewById(R.id.editProfileButton);
        Button create_data_packet = findViewById(R.id.dataPacketButton);
        edit_profile.setOnClickListener(this);
        create_data_packet.setOnClickListener(this);

        setTitle("Main");
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.editProfileButton) {
            Intent intent = new Intent(MainActivity.this, UpdateProfileActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        }
        if (v.getId() == R.id.dataPacketButton) {
            Intent intent = new Intent(MainActivity.this, DataPacketActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }
}
