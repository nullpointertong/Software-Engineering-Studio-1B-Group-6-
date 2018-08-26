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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import yourteamnumber.seshealthpatient.R;

import static android.app.Activity.RESULT_OK;
import static com.google.android.gms.internal.zzahn.runOnUiThread;



/**
 * A simple {@link Fragment} subclass.
 */
public class SendFileFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "GoogleDrive API" ;
    private final int RESOLVE_CONNECTION_REQUEST_CODE = 1000;
    private final int OPEN_FILE_REQUEST_CODE = 1001;
    private final int CREATE_FILE_REQUEST_CODE = 1002;
    private final int DELETE_FILE_REQUEST_CODE = 1003;
    TextView tvData;
    static final int 				REQUEST_ACCOUNT_PICKER = 1;
    static final int 				REQUEST_AUTHORIZATION = 2;
    static final int 				RESULT_STORE_FILE = 4;
    private static Uri mFileUri;
    private static Drive 			mService;
    private GoogleAccountCredential mCredential;
    private Context mContext;
    private List<File> mResultList;
    private ListView mListView;
    private String[] 				mFileArray;
    private String 					mDLVal;
    private ArrayAdapter mAdapter;

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

        v.findViewById(R.id.google_driveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();
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

    // Give the user a standard file-picker UI that lets them select a file
    // for your app to operate on. Note that the OpenFileActivityBuilder
    // shows all files, not just the ones that your app has created.
    protected void chooseFile() {
        IntentSender openFileIS;

        // TODO: invoke the file chooser and display the file info
        openFileIS = new OpenFileActivityBuilder()
                .setActivityTitle("Select A File")
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
    // about the file in the TextView in the Activity
    protected void displayChosenFileData(Intent fileData) {
        // the chosen file is stored as an Extra piece of data in the Intent
        DriveId chosenFileID = fileData
                .getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
        // Given a Drive ID, we can convert it to a file reference
        DriveFile theFile = chosenFileID.asDriveFile();

        // Once we have the file reference, we can get the file's metadata
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

    // Create a file using the CreateFileActivityBuilder - instantiate
    // new DriveContents and then invoke the callback to set the
    // initial file content and use the Activity to choose a location
    protected void createFileUsingActivity() {
        // Step 1: Create a new DriveContents object and set its callback
        // TODO: Create the new content
    }

    private final ResultCallback<DriveApi.DriveContentsResult> mNewContentCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(@NonNull final DriveApi.DriveContentsResult result) {
                    // Step 2: Using the newly created content, set the initial
                    // file metadata and properties and then invoke the
                    // CreateFileWithActivityBuilder

                    // Performing intensive work, such as writing data to a file, should
                    // always happen off of the main UI thread so that your app stays responsive.
                    // This is a very simple example so it just creates a new Thread,
                    // but you can use other methods like an AsyncTask or IntentService for this.
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
            // This code is passed when the user has resolved whatever connection
            // problem there was with the Google Play Services library
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == getActivity().RESULT_OK) {
                    mGoogleApiClient.connect();
                }
                break;


            // This code is passed when the user has selected a filename and
            // folder to create new content in.



            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
