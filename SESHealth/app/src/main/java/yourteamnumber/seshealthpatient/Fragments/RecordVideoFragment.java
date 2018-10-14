package yourteamnumber.seshealthpatient.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import yourteamnumber.seshealthpatient.BuildConfig;
import yourteamnumber.seshealthpatient.Model.DataPacket.Models.DataPacket;
import yourteamnumber.seshealthpatient.Model.DataPacket.Models.VideoSnippet;
import yourteamnumber.seshealthpatient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordVideoFragment extends Fragment {
    private static final String TAG = "RecordVideoFragment" ;

    private final int VIDEO_REQUEST_CODE = 1001;

    private DataPacket currentDataPacker;

    //test test test test

    public RecordVideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_record_video, container, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentDataPacker = getArguments() != null ? (DataPacket) getArguments().getSerializable("data_packet") : null;

        recordVideo();
    }

    public void recordVideo() {
        //call the camera and record a video
        Intent camera_intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        //set the uri of the video 
        Uri video_uri = FileProvider.getUriForFile(getActivity(), "yourteamnumber.seshealthpatient.provider", getFilepath());
        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, video_uri);
        camera_intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(camera_intent, VIDEO_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //check if the video is stored successfully and show a toast to user
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
        //set the path of the video. create a directory and put the video in it.
        File sdCard = Environment.getExternalStorageDirectory();

        String dataPackerIdentifier = currentDataPacker.getDataPackedId().toString();
        File dir = new File (sdCard.getAbsolutePath() + "/SESHealthPatient/DataPackets/" + dataPackerIdentifier + "/videos");
        if (!dir.exists())
        {
            dir.mkdirs();
        }

        VideoSnippet videoSnippet = currentDataPacker.getVideoSnippet();
        if (videoSnippet == null)
        {
            videoSnippet = new VideoSnippet();
        }
        //rename the video. the first one is named as Video-1, the second one is named as video-2.... 
        //Avoid latest video cover previous video.
        String fileTitle = "Video - " + videoSnippet.getNumVideoSnippets() + ".mp4";
        File newFile = new File(dir, fileTitle);
        videoSnippet.addVideoSnippets(newFile);

        return newFile;
    }
}
