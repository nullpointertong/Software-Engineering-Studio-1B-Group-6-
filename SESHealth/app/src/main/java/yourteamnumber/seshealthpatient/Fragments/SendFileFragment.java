package yourteamnumber.seshealthpatient.Fragments;



import android.accounts.AccountManager;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;


import yourteamnumber.seshealthpatient.R;





/**
 * A simple {@link Fragment} subclass.
 */
public class SendFileFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "GoogleDrive API" ;
    private final int RESOLVE_CONNECTION_REQUEST_CODE = 1000;
    private final int OPEN_FILE_REQUEST_CODE = 1001;
    TextView tvData;

    private Context mContext;

    private ListView mListView;


    //reference to the google play services client
    protected GoogleApiClient mGoogleApiClient;

    public SendFileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_send_file, container, false);

        tvData = (TextView) v.findViewById(R.id.tvMsg);
        mContext = getActivity();

        v.findViewById(R.id.google_driveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();
            }
        });

        mListView = (ListView) v.findViewById(R.id.listView1);

        AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView parent, View v, int position, long id)
            {

            }
        };

        mListView.setOnItemClickListener(mMessageClickedHandler);

        final Button button2 = (Button) v.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

            }
        });


        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        return v;
    }

    protected void getRootFolderInfo() {
        // TODO: get the root folder info and display its ID
        DriveFolder rootFolder = Drive.DriveApi.getRootFolder(mGoogleApiClient);
        DriveId rootID = rootFolder.getDriveId();

        tvData.setText("Root Folder: " + rootID.toString());
    }
    //a
    // Give the user a standard file-picker UI that lets them select a file
    // for your app to operate on. Note that the OpenFileActivityBuilder
    // shows all files, not just the ones that your app has created.
    protected void chooseFile() {
        IntentSender openFileIS;

        // TODO: invoke the file chooser and display the file info
        openFileIS = new OpenFileActivityBuilder()
                .setActivityTitle("Select A File")
                .setMimeType(new String[] {"application/pdf"})
                .build(mGoogleApiClient);
        // This code will open the file picker Activity, and the result will
        // be passed to the onActivityResult function.
       try {
            startIntentSenderForResult(openFileIS, OPEN_FILE_REQUEST_CODE, new Intent(), 0,0,0,null);
        }
        catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Problem starting the OpenFileActivityBuilder");
        }
    }

    // Once the user has chosen a file, this function will display information
    // about the file in the TextView
    protected void displayChosenFileData(Intent fileData) {
        // the chosen file is stored as an Extra piece of data in the Intent
        DriveId chosenFileID = fileData
                .getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
        // Given a Drive ID, we can convert it to a file reference
        DriveFile theFile = chosenFileID.asDriveFile();
        // Once  have the file reference,  can get the file's metadata
        // TODO: display the file metadata
        theFile.getMetadata(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveResource.MetadataResult>() {
                    @Override
                    public void onResult(@NonNull DriveResource.MetadataResult metadataResult) {
                        Metadata theData = metadataResult.getMetadata();
                        String str = theData.getTitle() + "\n" + theData.getMimeType() + "\n" + theData.getFileSize() + "bytes\n";
                        tvData.setText(str);
                    }
                });
    }

    private final ResultCallback<DriveApi.DriveContentsResult> mNewContentCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(@NonNull final DriveApi.DriveContentsResult result) {
                    new Thread() {
                        @Override
                        public void run() {
                            // Retrieve the output stream from the DriveContents
                            OutputStream outStrm = result.getDriveContents().getOutputStream();
                            OutputStreamWriter outStrmWrt = new OutputStreamWriter(outStrm);

                            try {
                                outStrmWrt.write("This is some file content!");
                                outStrmWrt.close();
                            }
                            catch (IOException e) {
                                Log.e(TAG, "Error writing to file " + e);
                            }

                            // TODO: Create a MetadataChangesetBuilder to change the MIME type

                            IntentSender intentSender;
                            // TODO: Create the IntentSender to fire off the CreateFileActivity


//                            try {
//                                startIntentSenderForResult(
//                                        intentSender, CREATE_FILE_REQUEST_CODE, null, 0, 0, 0);
//                            }
//                            catch (IntentSender.SendIntentException e) {
//                                Log.w(TAG, "Unable to send intent", e);
//                            }
                        }
                    }.start();
                }
            };

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG,"onStart: Connceting to Google Play Services");

        //connect to play services
        GoogleApiAvailability gAPI = GoogleApiAvailability.getInstance();
        int resultCode = gAPI.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            gAPI.getErrorDialog(getActivity(), resultCode, 1).show();
        }
        else {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            Log.i(TAG, "onStop: Disconnecting from Google Play Services");
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected: Play services onConnected called");
        getRootFolderInfo();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspened: Connection was suspended, cause code is " + i);
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(getActivity(), RESOLVE_CONNECTION_REQUEST_CODE);
            }
            catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        }
        else {
            GoogleApiAvailability gAPI = GoogleApiAvailability.getInstance();
            gAPI.getErrorDialog(getActivity(), connectionResult.getErrorCode(), 0).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == getActivity().RESULT_OK) {
                    mGoogleApiClient.connect();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
