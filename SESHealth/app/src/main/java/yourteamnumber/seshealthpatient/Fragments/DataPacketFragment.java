package yourteamnumber.seshealthpatient.Fragments;


import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.common.io.ByteStreams;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unstoppable.submitbuttonview.SubmitButton;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import yourteamnumber.seshealthpatient.Model.DataPacket.CustomComponents.TextInputComponent;
import yourteamnumber.seshealthpatient.Model.DataPacket.Models.DataPacket;
import yourteamnumber.seshealthpatient.Model.DataPacket.Models.Location;
import yourteamnumber.seshealthpatient.Model.DataPacket.Models.SupplementaryFiles;
import yourteamnumber.seshealthpatient.Model.DataPacket.Models.VideoSnippet;
import yourteamnumber.seshealthpatient.Model.User;
import yourteamnumber.seshealthpatient.R;

import static android.app.Activity.RESULT_OK;

public class DataPacketFragment extends Fragment {


    private static final int DEFAULT_ZOOM = 14;
    private final int VIDEO_REQUEST_CODE = 1001;
    private FusedLocationProviderClient mFusedLocationClient;


    private DataPacket dataPacket;
    private LatLng currentLocation;
    private boolean locationAdded;

    private TextInputComponent textInputComponent;
    private Button addLocationButton;
    private Button addFilesButton;
    private TextView heartRateText;
    private ListView suppFiles;
    private ListView suppVideos;
    private Button heartRateButton;
    private Button recordVideoButton;
    private SubmitButton sendButton;
    private MaterialSpinner selectDoctorsSpinner;
    private Context context;
    private Map<String, String> doctors = new HashMap<>();

    private MapView map;
    private GoogleMap mMap;

    ArrayAdapter<String> suppFilesAdapter;
    ArrayAdapter<String> videoAdapter;
    ArrayList<String> addedFilesList = new ArrayList<String>();
    ArrayList<String> addedVideosList = new ArrayList<String>();
    ArrayList<String> addedFilesFullPathList = new ArrayList<>();

    private static final String TAG = "DataPacketFragment" ;
    protected static final int REQUEST_CODE_SIGN_IN = 0;
    protected static final int REQUEST_CODE_OPEN_ITEM = 1;

    private TaskCompletionSource<DriveId> mOpenItemTaskSource;

    private DriveClient mDriveClient;

    private DriveResourceClient mDriveResourceClient;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mFirebaseDatabase;
    private final LatLng mDefaultLocation = new LatLng(-33.8840504, 151.1992254);

    public DataPacketFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data_packet, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        init(view, savedInstanceState);
    }

    private void init(View view, Bundle savedInstanceState)
    {
        textInputComponent = view.findViewById(R.id.textInputComponent);
        addLocationButton = view.findViewById(R.id.addLocationButton);
        sendButton = view.findViewById(R.id.submitbutton);
        heartRateButton = view.findViewById(R.id.btnHeartRate);
        addFilesButton = view.findViewById(R.id.btnAddFiles);
        recordVideoButton = view.findViewById(R.id.btnRecordVideo);
        suppFiles = view.findViewById(R.id.suppList);
        suppVideos = view.findViewById(R.id.suppVideos);
        heartRateText = view.findViewById(R.id.txtHeartRate);
        selectDoctorsSpinner = view.findViewById(R.id.spnSelectDoctor);

        dataPacket = new DataPacket();
        context = getContext();

        sendButton.setProgress(0);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());


        suppFilesAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1,
                addedFilesList);

        videoAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1,
                addedVideosList);

        suppFiles.setAdapter(suppFilesAdapter);
        suppVideos.setAdapter(videoAdapter);
        heartRateText.setVisibility(View.INVISIBLE);

        if (getArguments() != null && getArguments().getSerializable("data_packet") != null)
        {
            dataPacket = (DataPacket) getArguments().getSerializable("data_packet");

            setInputsForDataPacket(savedInstanceState);
        }

        addFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testBoi(v);
            }
        });

        heartRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new HeartRateFragment();
                Bundle dataPacketBundle = new Bundle();
                dataPacketBundle.putSerializable("data_packet", getDataPacket());
                newFragment.setArguments(dataPacketBundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);

                transaction.commit();
            }
        });

        recordVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordVideo();
            }
        });

        if ( ContextCompat.checkSelfPermission( getActivity(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( getActivity(), new String[] {  Manifest.permission.ACCESS_FINE_LOCATION},
                    113 );
        }


        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( ContextCompat.checkSelfPermission( getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {

                    mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                        @Override @NonNull
                        public void onSuccess(android.location.Location location) {
                            LatLng latLng;
                            try {
                                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            }
                            catch (Exception e)
                            {
                                latLng = mDefaultLocation;
                            }

                            showLocation(view, savedInstanceState, latLng);
                            currentLocation = latLng;
                            locationAdded = true;
                            addLocationButton.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });

       sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Send(v);
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();

        String patientId = mFirebaseAuth.getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("user_id");

        DatabaseReference doctorsRef = mFirebaseDatabase.child(patientId).child("MyDoctors");

        doctorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> user = (Map<String, Object>) dataSnapshot.getValue();


                for  (String key : user.keySet())
                {
                    doctors.put("Dr. " + key,(String) user.get(key));
                }

                selectDoctorsSpinner.setItems(doctors.keySet().toArray());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // region Drive API

    public void testBoi(View view) {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "No permissions, requesting...");
            showMessage("Please grant permission to store files locally.");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    112);
        }
        else
        {
            Log.d(TAG, "Signing in");
            Set<Scope> requiredScopes = new HashSet<>(2);
            requiredScopes.add(Drive.SCOPE_FILE);
            requiredScopes.add(Drive.SCOPE_APPFOLDER);
            GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getContext());
            if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
                Log.d(TAG, "Init - Account");
                initializeDriveClient(signInAccount);
            } else {
                GoogleSignInOptions signInOptions =
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestScopes(Drive.SCOPE_FILE)
                                .requestScopes(Drive.SCOPE_APPFOLDER)
                                .build();
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getContext(), signInOptions);
                startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
            }
        }
    }

    private void initializeDriveClient(GoogleSignInAccount signInAccount) {
        mDriveClient = Drive.getDriveClient(getContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(getContext(), signInAccount);
        onDriveClientReady();
    }

    protected void onDriveClientReady() {
        Log.d(TAG, "Success: Picking File");
        pickTextFile()
                .addOnSuccessListener(driveId -> {
                    Log.d(TAG, "Success: File Picked");
                    retrieveContents(driveId.asDriveFile());
                })
                .addOnFailureListener(getActivity(), e -> {
                    Log.d(TAG, "No file selected", e);
                    showMessage(getString(R.string.file_not_selected));

                });
    }

    private Task<DriveId> pickTextFile() {
        Log.d(TAG, "Filter applied");

        OpenFileActivityOptions openOptions =
                new OpenFileActivityOptions.Builder()
                        .setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "image/jpeg"))
                        .setActivityTitle(getString(R.string.select_file_title))
                        .build();
        return pickItem(openOptions);
    }

    private Task<DriveId> pickItem(OpenFileActivityOptions openOptions) {
        Log.d(TAG, "Picking item...");
        mOpenItemTaskSource = new TaskCompletionSource<>();
        getDriveClient()
                .newOpenFileActivityIntentSender(openOptions)
                .continueWith((Continuation<IntentSender, Void>) task -> {
                    startIntentSenderForResult(
                            task.getResult(), REQUEST_CODE_OPEN_ITEM, null, 0, 0, 0, null);
                    Log.d(TAG, "Process: Picking File");
                    return null;
                });
        return mOpenItemTaskSource.getTask();
    }

    private void retrieveContents(DriveFile file) {
        Log.d(TAG, "Success: retrieving contents");
        Task<DriveContents> openFileTask =
                getDriveResourceClient().openFile(file, DriveFile.MODE_READ_ONLY);

        openFileTask
                .continueWithTask(task -> {
                    Log.d(TAG, "Success: get result");
                    DriveContents contents = task.getResult();
                    InputStream contentStream = contents.getInputStream();
                    byte[] contentStreamAsByteArray = ByteStreams.toByteArray(contentStream);

                    if (task.isSuccessful())
                    {
                        Log.d(TAG, "Success: Retrieving metadata");
                        Task<Metadata> getMetadataTask = getDriveResourceClient().getMetadata(file);
                        getMetadataTask
                                .addOnSuccessListener(getActivity(),
                                        metadata -> {
                                            Log.d(TAG, "Successful");
                                            Log.d(TAG, "Creating file");
                                            File sdCard = Environment.getExternalStorageDirectory();

                                            String dataPackerIdentifier = dataPacket.getDataPackedId().toString();
                                            File dir = new File (sdCard.getAbsolutePath() + "/SESHealthPatient/DataPackets/" + dataPackerIdentifier + "/images");
                                            if (!dir.exists())
                                            {
                                                dir.mkdirs();
                                            }

                                            File newFile = new File(dir, metadata.getTitle());
                                            try {
                                                newFile.createNewFile();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            Log.d(TAG, "Writing to output");
                                            FileOutputStream fileOutputStream;
                                            try
                                            {
                                                fileOutputStream = new FileOutputStream(newFile);
                                                fileOutputStream.write(contentStreamAsByteArray);
                                                fileOutputStream.close();
                                                Log.d(TAG, newFile.getPath());
                                                addedFilesFullPathList.add(newFile.getAbsolutePath());
                                                suppFilesAdapter.add(newFile.getName());
                                            }
                                            catch (Exception e)
                                            {
                                                Log.d(TAG, e.getMessage());
                                            }

                                            try {
                                                contentStream.close();
                                            } catch (IOException e) {
                                                Log.d(TAG, e.getMessage());
                                            }
                                        })
                                .addOnFailureListener(getActivity(), e -> {
                                    Log.d(TAG, "Unable to retrieve metadata", e);
                                    showMessage(getString(R.string.read_failed));
                                });

                    }

                    Task<Void> discardTask = getDriveResourceClient().discardContents(contents);
                    return discardTask;
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Unable to read contents", e);
                    showMessage(getString(R.string.read_failed));
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode != RESULT_OK) {
                    // Sign-in may fail or be cancelled by the user. For this sample, sign-in is
                    // required and is fatal. For apps where sign-in is optional, handle
                    // appropriately
                    Log.d(TAG, "Sign-in failed.");
                    return;
                }

                Task<GoogleSignInAccount> getAccountTask =
                        GoogleSignIn.getSignedInAccountFromIntent(data);
                if (getAccountTask.isSuccessful()) {
                    initializeDriveClient(getAccountTask.getResult());
                } else {
                    Log.d(TAG, "Sign-in failed.");
                }
                break;
            case REQUEST_CODE_OPEN_ITEM:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
                    mOpenItemTaskSource.setResult(driveId);
                } else {
                    mOpenItemTaskSource.setException(new RuntimeException("Unable to open file"));
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected DriveClient getDriveClient() {
        return mDriveClient;
    }
    protected DriveResourceClient getDriveResourceClient() {
        return mDriveResourceClient;
    }

    // endregion

    private void showLocation(View view, Bundle savedInstanceState, LatLng location)
    {
        map = view.findViewById(R.id.mapView2);
        map.onCreate(savedInstanceState);

        map.onResume(); // needed to get the map to display immediately

        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                // Move camera to Sydney
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM));

                mMap.addMarker(new MarkerOptions().position(location).title("Current Location"));
            }
        });
    }

    // region Record Video


    public void recordVideo() {
        Intent camera_intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        Uri video_uri = FileProvider.getUriForFile(getActivity(), "yourteamnumber.seshealthpatient.provider", filePathForVideo());
        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, video_uri);
        camera_intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(camera_intent, VIDEO_REQUEST_CODE);
    }

    public File filePathForVideo() {
        File sdCard = Environment.getExternalStorageDirectory();

        String dataPackerIdentifier = dataPacket.getDataPackedId().toString();
        File dir = new File (sdCard.getAbsolutePath() + "/SESHealthPatient/DataPackets/" + dataPackerIdentifier + "/videos");
        if (!dir.exists())
        {
            dir.mkdirs();
        }

        VideoSnippet videoSnippet = dataPacket.getVideoSnippet();
        if (videoSnippet == null)
        {
            videoSnippet = new VideoSnippet();
            dataPacket.addVideoSnippet(videoSnippet);
        }

        String fileTitle = "Video - " + videoSnippet.getNumVideoSnippets() + ".mp4";
        File newFile = new File(dir, fileTitle);
        videoSnippet.addVideoSnippets(newFile);

        return newFile;
    }

    // endregion

    private void setInputsForDataPacket(Bundle savedInstanceState)
    {
        if (dataPacket.getTextData() != null && !dataPacket.getTextData().toString().isEmpty())
        {
            textInputComponent.setText(dataPacket.getTextData().toString());
        }
        if (dataPacket.getLocation() != null)
        {
            currentLocation = new LatLng(dataPacket.getLocation().getLatitude(), dataPacket.getLocation().getLongitude());
            addLocationButton.setVisibility(View.INVISIBLE);
            showLocation(getView(), savedInstanceState, currentLocation);
        }

        if (dataPacket.getSupplementaryFiles() != null)
        {
            for (String fileName : dataPacket.getSupplementaryFiles().getFileNames())
            {
                suppFilesAdapter.add(fileName);
            }
        }

        if (dataPacket.getVideoSnippet() != null)
        {
            for (String videoSnippet : dataPacket.getVideoSnippet().getVideoSnippetsNames())
            {
                videoAdapter.add(videoSnippet);
            }
        }

        heartRateText.setVisibility(View.VISIBLE);
        heartRateText.setText(dataPacket.getHeartRate().toString() + " BPM");
    }

    private DataPacket getDataPacket()
    {
        if (textInputComponent.isNotEmpty())
        {
            dataPacket.addTextData(textInputComponent.getTextData());
        }
        if (locationAdded)
        {
            dataPacket.addLocation(new Location("", "", currentLocation.latitude, currentLocation.longitude));
        }

        if (!addedFilesList.isEmpty())
        {
            SupplementaryFiles suppFiles = new SupplementaryFiles();
            suppFiles.setFileNames(addedFilesList);
            suppFiles.setFilePaths(addedFilesFullPathList);
            dataPacket.addSupplementaryFiles(suppFiles);
        }

        return dataPacket;
    }

    private void Send(View view)
    {
        if (textInputComponent.isNotEmpty())
        {
            dataPacket.addTextData(textInputComponent.getTextData());
        }
        if (locationAdded)
        {
            dataPacket.addLocation(new Location("", "", currentLocation.latitude, currentLocation.longitude));
        }

        String selectedDoctor = (String) selectDoctorsSpinner.getItems().get(selectDoctorsSpinner.getSelectedIndex());
        dataPacket.setDoctorId(doctors.get(selectedDoctor));
        dataPacket.setDoctorName(selectedDoctor);

        if (!addedFilesFullPathList.isEmpty())
        {
            SupplementaryFiles supplementaryFiles = new SupplementaryFiles();
            supplementaryFiles.setFilePaths(addedFilesFullPathList);
            supplementaryFiles.setFileNames(addedFilesList);
            dataPacket.addSupplementaryFiles(supplementaryFiles);
        }

        if (dataPacket.send(getContext()))
        {
            Snackbar successSnackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Successfully sent.", Snackbar.LENGTH_LONG);
            successSnackbar.setAction("Review", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataPacket.showDataPacketDialog(getContext());
                }
            });

            successSnackbar.show();

            textInputComponent.disable();
            heartRateButton.setEnabled(false);
            addFilesButton.setEnabled(false);
            recordVideoButton.setEnabled(false);
            sendButton.setEnabled(false);
            suppFiles.setEnabled(false);
        }
    }


    private void ShowAlertDialog(String message)
    {
        AlertDialog.Builder alertDialogBuilder =
                new AlertDialog.Builder(getContext())
                        .setTitle("Error: ")
                        .setMessage(message)
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

        alertDialogBuilder.show();
    }

    protected void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}
