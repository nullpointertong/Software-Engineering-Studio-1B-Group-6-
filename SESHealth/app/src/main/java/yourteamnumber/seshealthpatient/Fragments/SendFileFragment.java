package yourteamnumber.seshealthpatient.Fragments;



import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.app.Fragment;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.GenericUrl;
import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.OnClick;
import yourteamnumber.seshealthpatient.Activities.MainActivity;
import yourteamnumber.seshealthpatient.R;

import static android.app.Activity.RESULT_OK;
import static com.google.android.gms.internal.zzahn.runOnUiThread;



/**
 * A simple {@link Fragment} subclass.
 */
public class SendFileFragment extends Fragment {

    private Button btnSignIn;
    private ListView lvAddedFiles;
    private int dataPacketIdent = 1;

    ArrayAdapter<String> adapter;
    ArrayList<String> addedFilesList = new ArrayList<String>();

    private static final String TAG = "SendFileFragment" ;
    protected static final int REQUEST_CODE_SIGN_IN = 0;
    protected static final int REQUEST_CODE_OPEN_ITEM = 1;

    /**
     * Tracks completion of the drive picker
     */
    private TaskCompletionSource<DriveId> mOpenItemTaskSource;


    /**
     * Handles high-level drive functions like sync
     */
    private DriveClient mDriveClient;

    /**
     * Handle access to Drive resources/files.
     */
    private DriveResourceClient mDriveResourceClient;


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
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnSignIn = getActivity().findViewById(R.id.google_driveBtn);
        lvAddedFiles = getActivity().findViewById(R.id.lvAddedFiles);

        adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1,
                addedFilesList);

        lvAddedFiles.setAdapter(adapter);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testBoi(v);
            }
        });
    }

    /**
     * Starts the sign-in process and initializes the Drive client.
     */
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

    /**
     * Prompts the user to select a text file using OpenFileActivity.
     *
     * @return Task that resolves with the selected item's ID.
     */
    private Task<DriveId> pickTextFile() {
        Log.d(TAG, "Filter applied");

        OpenFileActivityOptions openOptions =
                new OpenFileActivityOptions.Builder()
                        .setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "image/jpeg"))
                        .setActivityTitle(getString(R.string.select_file_title))
                        .build();
        return pickItem(openOptions);
    }

    /**
     * Prompts the user to select a folder using OpenFileActivity.
     *
     * @param openOptions Filter that should be applied to the selection
     * @return Task that resolves with the selected item's ID.
     */
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

    protected DriveClient getDriveClient() {
        return mDriveClient;
    }
    protected DriveResourceClient getDriveResourceClient() {
        return mDriveResourceClient;
    }

    /**
     * Continues the sign-in process, initializing the Drive clients with the current
     * user's account.
     */
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

                                            File dir = new File (sdCard.getAbsolutePath() + "/SESHealthPation/DataPackets/DataPacket" + dataPacketIdent++);
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
                                                adapter.add(newFile.getName());
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

    protected void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

}
