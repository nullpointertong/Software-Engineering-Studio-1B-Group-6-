package yourteamnumber.seshealthpatient.Fragments;

import android.app.FragmentTransaction;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.res.Configuration;

import android.hardware.Camera.PreviewCallback;
import android.widget.Toast;

import yourteamnumber.seshealthpatient.Model.DataPacket.Models.DataPacket;
import yourteamnumber.seshealthpatient.Model.DataPacket.Models.HeartRate;
import yourteamnumber.seshealthpatient.R;
import yourteamnumber.seshealthpatient.Tools.ProgressBarAnimation;

/**
 * A simple {@link Fragment} subclass.
 */
public class HeartRateFragment extends Fragment {

    // private static final String TAG = "HeartRateMonitor";
    private static final AtomicBoolean processing = new AtomicBoolean(false);
    private static SurfaceView preview = null;
    private static SurfaceHolder previewHolder = null;
    private static Camera camera = null;
    // private static View image = null;
    private static TextView heartRateText;
    private static TextView heartRateTipText;
    private static LinearLayout layout;
    private static Context context;
    private static Activity activity;
    private static DataPacket datapacket;
    private static ProgressBar progressBar;

    private static int averageIndex = 0;
    private static final int averageArraySize = 4;
    private static final int[] averageArray = new int[averageArraySize];

    public static enum TYPE {
        GREEN, RED
    }

    private static TYPE currentType = TYPE.GREEN;

    private static int beatsIndex = 0;
    private static final int beatsArraySize = 3;
    private static final int[] beatsArray = new int[beatsArraySize];
    private static double beats = 0;
    private static long startTime = 0;
    private static int progress;

    public HeartRateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_heart_rate, container, false);
        // Inflate the layout for this fragment



                /*Message message = Message.obtain();
                message.what = HANDLER_MESSAGE;
                handler.sendMessage(message);*/

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layout = view.findViewById(R.id.heartRateLayout);
        preview = (SurfaceView) view.findViewById(R.id.preview);
        heartRateText = (TextView) view.findViewById(R.id.txtHeartRate);
        heartRateTipText = (TextView) view.findViewById(R.id.txtHeartRateTip);
        progressBar = view.findViewById(R.id.pbrHeartRate);

        datapacket = getArguments() != null ? (DataPacket) getArguments().getSerializable("data_packet") : null;

        context = getContext();
        activity = getActivity();

        progress = 0;

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            previewHolder = preview.getHolder();
            previewHolder.addCallback(surfaceCallback);
            previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA}, 1);//1 can be another integer
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();

        camera = Camera.open();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private static PreviewCallback previewCallback = (data, cam) -> {
        if (data == null)
            throw new NullPointerException();
        Camera.Size size = cam.getParameters().getPreviewSize();
        if (size == null)
            throw new NullPointerException();
        if (!processing.compareAndSet(false, true))
            return;
        int width = size.width;
        int height = size.height;

        int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(),
                height, width);


        // Log.i(TAG, "imgAvg=" + imgAvg);
        if (imgAvg == 0 || imgAvg == 255) {
            processing.set(false);
            return;
        }

        int averageArrayAvg = 0;
        int averageArrayCnt = 0;
        for (int i = 0; i < averageArray.length; i++) {
            if (averageArray[i] > 0) {
                averageArrayAvg += averageArray[i];
                averageArrayCnt++;
            }
        }

        int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt)
                : 0;
        TYPE newType = currentType;
        if (imgAvg < rollingAverage) {
            newType = TYPE.RED;
            if (newType != currentType) {
                beats++;

                // Log.e(TAG, "BEAT!! beats=" + beats);
            }
        } else if (imgAvg > rollingAverage) {
            newType = TYPE.GREEN;
        }

        if (averageIndex == averageArraySize)
            averageIndex = 0;
        averageArray[averageIndex] = imgAvg;
        averageIndex++;

        // Transitioned from one state to another to the same
        if (newType != currentType) {
            currentType = newType;
            // image.postInvalidate();
        }

        long endTime = System.currentTimeMillis();
        double totalTimeInSecs = (endTime - startTime) / 1000d;
        if (totalTimeInSecs >= 2) {
            double bps = (beats / totalTimeInSecs);
            int dpm = (int) (bps * 60d);
            if (dpm < 30 || dpm > 180 || imgAvg < 200) {

                startTime = System.currentTimeMillis();
                heartRateTipText.setVisibility(View.VISIBLE);
                beats = 0;
                processing.set(false);
                return;
            }
            // Log.e(TAG, "totalTimeInSecs=" + totalTimeInSecs + " beats="+
            // beats);
            if (beatsIndex == beatsArraySize)
                beatsIndex = 0;
            beatsArray[beatsIndex] = dpm;
            beatsIndex++;
            int beatsArrayAvg = 0;
            int beatsArrayCnt = 0;
            for (int i = 0; i < beatsArray.length; i++) {
                if (beatsArray[i] > 0) {
                    beatsArrayAvg += beatsArray[i];
                    beatsArrayCnt++;
                }
            }
            int beatsAvg = (beatsArrayAvg / beatsArrayCnt);

            heartRateTipText.setVisibility(View.INVISIBLE);
            String heartRate = String.valueOf(beatsAvg) + " BPM";
            progress += 25;
            heartRateText.setText(heartRate + " - " + progress + "% complete.");
            ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, progress - 25, progress);
            anim.setDuration(1000);
            progressBar.startAnimation(anim);
            progressBar.setProgress(progress);

            if (progress == 125)
            {
                camera.stopPreview();
                camera.release();
                camera = null;
                layout.removeAllViews();
                Toast.makeText(context, "Heart Rate is: " + heartRate, Toast.LENGTH_LONG).show();

                Fragment newFragment = new DataPacketFragment();
                Bundle bundle = new Bundle();
                datapacket.addHeartRate(new HeartRate(beatsAvg));
                bundle.putSerializable("data_packet", datapacket);
                newFragment.setArguments(bundle);
                FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);

                transaction.commit();
            }

            startTime = System.currentTimeMillis();
            beats = 0;
        }
        processing.set(false);
    };

    private static SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {
                // Log.e("PreviewDemo-surfaceCallback","Exception in setPreviewDisplay()",
                // t);
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                // Log.d(TAG, "Using width=" + size.width + " height=" +
                // size.height);
            }
            camera.setParameters(parameters);
            camera.startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // Ignore
        }
    };

    private static Camera.Size getSmallestPreviewSize(int width, int height,
                                                      Camera.Parameters parameters) {
        Camera.Size result = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;
                    if (newArea < resultArea)
                        result = size;
                }
            }
        }
        return result;
    }


}