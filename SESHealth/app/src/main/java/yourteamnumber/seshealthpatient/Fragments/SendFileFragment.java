package yourteamnumber.seshealthpatient.Fragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import yourteamnumber.seshealthpatient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendFileFragment extends Fragment {

    private Button mUploadTextBtn;
    private EditText mRevelantText;
    private TextView mInPutTv;
    //receive current input characters
    private CharSequence temp;
    private int maxNum = 200;
    private int nowNum;
    private Button mselectFileBtn;
    private Button muplaodFileBtn;
    private TextView mFileNameTv;
    ProgressDialog progressDialog;
    //local storage
    Uri fileUri;
    //upload files
    FirebaseStorage storage;
    //store files
    FirebaseDatabase database;


    public SendFileFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_send_file, container, false);
       //connect the visual component to my java code
        mFileNameTv = (TextView) v.findViewById(R.id.fileNameTv);
        mInPutTv = (TextView) v.findViewById(R.id.input_tv);

        mselectFileBtn = (Button) v.findViewById(R.id.selectBtn);
        mselectFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    selectFiles();
                }
                else {
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                }
            }
        });

        muplaodFileBtn = (Button) v.findViewById(R.id.uploadBtn);
        muplaodFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileUri != null) {
                    uploadFile(fileUri);
                }
                else {
                    Toast.makeText(getActivity(),
                            "You should select a file",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mUploadTextBtn = (Button) v.findViewById(R.id.upload_btn);
        mUploadTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mRevelantText = (EditText) v.findViewById(R.id.relevant_text);
        mRevelantText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //receive current input characters
                temp = s;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //current characters
                nowNum = editable.length();
                mInPutTv.setText("Already "+nowNum+"/200 words!");
                int selectionStart = mRevelantText.getSelectionStart();
                int selectionEnd = mRevelantText.getSelectionEnd();
                if (temp.length() > maxNum) {
                    //get character string
                    editable.delete(selectionStart - 1, selectionEnd);
                    mRevelantText.setText(editable.toString());
                    int selection = editable.length();
                    //let selection go to the end
                    mRevelantText.setSelection(selection);
                    Toast.makeText(getActivity(),
                            R.string.maxwords_toast,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    private void uploadFile(Uri fileUri) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading file...");
        progressDialog.setProgress(0);
        progressDialog.show();
        final String fileName = System.currentTimeMillis()+"";
        storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        storageReference.child("Uploads").child(fileName).putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //return the upload file url
                String url = taskSnapshot.getDownloadUrl().toString();
                // store the url in realtime database
                database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference();//return path root
                reference.child(fileName).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(),
                                    "Upload successfully",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getActivity(),
                                    "Upload successfully",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),
                        "Upload unsuccessfully",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                //track the progress of  = upload
                int currentProgress =(int) (100 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectFiles();
        }
        else {
            Toast.makeText(getActivity(),
                    "Please provide permission",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void selectFiles() {
        // jump to file manager by using intent
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,86);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if user choose a  file
        if (requestCode == 86 && resultCode == getActivity().RESULT_OK && data != null) {
            fileUri = data.getData();//get uri
            mFileNameTv.setText(data.getData().getLastPathSegment() + " is selected");
        }
        else {
            Toast.makeText(getActivity(),
                    "Please select a file",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
