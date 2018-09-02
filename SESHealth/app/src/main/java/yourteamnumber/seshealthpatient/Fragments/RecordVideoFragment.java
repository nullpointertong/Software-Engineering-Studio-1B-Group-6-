package yourteamnumber.seshealthpatient.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import yourteamnumber.seshealthpatient.BuildConfig;
import yourteamnumber.seshealthpatient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordVideoFragment extends Fragment {
    private final int VIDEO_REQUEST_CODE = 1001;
    private Button mRecordBtn;
    private static final String TAG = "RecordVideoFragment" ;


    //test test test test

    public RecordVideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_record_video, container, false);
        mRecordBtn = (Button) v.findViewById(R.id.recordButton);
        mRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordVideo(v);
            }
        });
        return v;
    }

    public void recordVideo(View view) {
        Intent camera_intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        File video_file = getFilepath();
        //Uri video_uri = Uri.fromFile(video_file);
        Uri video_uri = FileProvider.getUriForFile(getActivity(), "yourteamnumber.seshealthpatient.provider", video_file);
        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, video_uri);
        camera_intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(camera_intent, VIDEO_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIDEO_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                Toast.makeText(getActivity(),
                        "Video Successfully Recorded",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(),
                        "Video recorded failed",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public File getFilepath() {
        /**
        //create a folder to store the video
        File sdCard = Environment.getExternalStorageDirectory();
        File folder = new File(sdCard.getAbsolutePath() + "/SESHealthPatient/DataPackets/DataPacket");
        Log.d(TAG, "creat file successfully");
        //check fold if exists
        if (!folder.exists()) {
            boolean success = folder.mkdir();
            Log.d(TAG, "no exist file");
            if (success) {
                Log.d(TAG, "success == true");
            }
        }
        File video_file = new File(folder, "sample_video.mp4");
        Log.d(TAG, "creat video successfully");
        return video_file;
         **/
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();
        Toast.makeText(getActivity(),"State is " + state, Toast.LENGTH_LONG).show();
        if (Environment.MEDIA_MOUNTED.equals(state)){
            //We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
            Toast.makeText(getActivity(), "We Can Read And Write ", Toast.LENGTH_LONG).show();
            File file = new File(Environment.getExternalStorageDirectory()
                    +File.separator
                    +"studentrecords"); //folder name
            file.mkdir();
            File video_file = new File(file, "video1.mp4");
            return video_file;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
            Toast.makeText(getActivity(), "We Can Read but Not Write ", Toast.LENGTH_LONG).show();
            return null;
        }else{
            //something else is wrong
            mExternalStorageAvailable = mExternalStorageWriteable = false;
            Toast.makeText(getActivity(), "We Can't Read OR Write ", Toast.LENGTH_LONG).show();
            return null;
        }
    }
}
