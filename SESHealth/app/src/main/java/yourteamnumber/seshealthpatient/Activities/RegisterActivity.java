package yourteamnumber.seshealthpatient.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import yourteamnumber.seshealthpatient.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText register_usernameET;   //Initailization of Variables required
    private EditText register_passwordET;
    private EditText  register_passwordET2;
    private Spinner spinner;

    private Button register_btn;

    private TextView textView4;
    private TextView textView5;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        register_usernameET = (EditText) findViewById(R.id.register_usernameET);
        register_passwordET = (EditText) findViewById(R.id.register_passwordET);
        register_passwordET2 = (EditText) findViewById(R.id.register_passwordET2);
        spinner = (Spinner) findViewById(R.id.spinner);

        register_btn = (Button) findViewById(R.id.register_btn);

        textView4 = (TextView) findViewById(R.id.textView4);
        textView5 = (TextView) findViewById(R.id.textView5);

        firebaseAuth = FirebaseAuth.getInstance();

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.usertype_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        TextView registered_textview = findViewById(R.id.textView5);
        registered_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent k = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(k);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick(R.id.register_btn)
    public void registerUser() {
        String email = register_usernameET.getText().toString().trim();
        String password = register_passwordET.getText().toString().trim();
        String confirmPassword = register_passwordET2.getText().toString().trim();

        boolean passwordConfirmation = false;


        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "The username and password cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }
        final Intent intent = new Intent(this, LoginActivity.class);
        if(!password.equals(confirmPassword)) {
            Toast.makeText(RegisterActivity.this,"Passwords don't match" , Toast.LENGTH_SHORT).show();

        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Map hashMap = new HashMap();
                        String userId = firebaseAuth.getUid();
                        DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child("user_id");

                        //hashMap.put("UserType", );
                        //currentUser.setValue(hashMap);


                        Toast.makeText(RegisterActivity.this, "Registration Sucessful!", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @OnClick(R.id.textView4)
    public void switchToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}










