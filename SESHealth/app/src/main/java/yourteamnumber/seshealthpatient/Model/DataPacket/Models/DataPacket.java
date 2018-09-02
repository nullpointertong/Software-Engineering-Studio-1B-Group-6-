package yourteamnumber.seshealthpatient.Model.DataPacket.Models;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.Serializable;
import java.util.UUID;

public class DataPacket implements Serializable{

    private String dataPackedId;
    private String userId;
    private String userEmail;
    private TextData textData;
    private LatLng location;
    private VideoSnippet videoSnippet;
    private SupplementaryFiles supplementaryFiles;
    private HeartRate heartRate;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private static FirebaseDatabase database;
    private DatabaseReference dataPacketStorageRef;
    private DatabaseReference dataPacketRef;

    private boolean dataPacketSuccess = true;
    private View view;

    public DataPacket(View view)
    {
        this.dataPackedId = UUID.randomUUID().toString();
        this.mAuth = FirebaseAuth.getInstance();
        this.mUser = mAuth.getCurrentUser();
        this.userId = mUser.getUid();
        this.userEmail = mUser.getEmail();
        this.view = view;
    }

    @Override
    public String toString() {
        return "DataPacket{" +
                "\ndataPackedId=" + dataPackedId +
                ", \nUserId=" + userId +
                ", \nUserEmail=" + userEmail +
                ", \ntextData=" + textData +
                ", \nlocation=" + location +
                ", \nvideoSnippet=" + videoSnippet +
                ", \nsupplementaryFiles=" + supplementaryFiles +
                ", \nheartRate=" + heartRate +
                "\n}";
    }

    public boolean send(Context context)
    {
        Toast.makeText(context, "Sending...", Toast.LENGTH_SHORT).show();

        try {
            database = FirebaseDatabase.getInstance();
            dataPacketStorageRef = database.getReference().child("DataPackets");
            dataPacketStorageRef.child(userId).child(dataPackedId).setValue(this);
        }
        catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }

        return uploadSupplementaryFiles();
    }

    private boolean uploadSupplementaryFiles()
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        for (int i = 0; i < getSupplementaryFiles().getFilePaths().size(); i++) {
            String fileName = getSupplementaryFiles().getFileNames().get(i);
            String filePath = getSupplementaryFiles().getFilePaths().get(i);

            StorageReference supplementaryImageRef = storageRef.child(userId + "/" + dataPackedId + "/images/" + fileName);

            UploadTask uploadTask;
            Uri file = Uri.fromFile(new File(filePath));
            uploadTask = supplementaryImageRef.putFile(file);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    dataPacketSuccess = false;
                    Log.d("DataPacket", exception.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    dataPacketSuccess = true;
                    Log.d("DataPacket", "Data Packet sent");
                }
            });
        }

        return dataPacketSuccess;
    }

    public void showDataPacketDialog(Context context)
    {
        String dataPacketSummary = "Description: " + getTextData() +
                                   "\nLocation: " + getLocation() +
                                   "\nFiles: " + getSupplementaryFiles().getFileNames() +
                                   "\nHeart Rate: " + getHeartRate();

        AlertDialog.Builder alertDialogBuilder =
                new AlertDialog.Builder(context)
                        .setTitle("Data Packet Summary: ")
                        .setMessage(dataPacketSummary)
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

        alertDialogBuilder.show();
    }

    //region Getters and Setters

    public String getDataPackedId() {
        return dataPackedId;
    }

    public void addDataPackedId(String dataPackedId) {
        this.dataPackedId = dataPackedId;
    }

    public TextData getTextData() {
        return textData;
    }

    public void addTextData(TextData textData) {
        this.textData = textData;
    }

    public LatLng getLocation() {
        return location;
    }

    public void addLocation(LatLng location) {
        this.location = location;
    }

    public VideoSnippet getVideoSnippet() {
        return videoSnippet;
    }

    public void addVideoSnippet(VideoSnippet videoSnippet) {
        this.videoSnippet = videoSnippet;
    }

    public SupplementaryFiles getSupplementaryFiles() {
        return supplementaryFiles;
    }

    public void addSupplementaryFiles(SupplementaryFiles supplementaryFiles) {
        this.supplementaryFiles = supplementaryFiles;
    }

    public HeartRate getHeartRate() {
        return heartRate;
    }

    public void addHeartRate(HeartRate heartRate) {
        this.heartRate = heartRate;
    }

    //endregion
}
