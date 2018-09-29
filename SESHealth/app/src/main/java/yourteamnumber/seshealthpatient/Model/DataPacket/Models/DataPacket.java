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
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class DataPacket implements Serializable{

    private String dataPackedId;
    private String userId;
    private String doctorId;
    private String doctorName;
    private String userEmail;
    private TextData textData;
    private Location location;
    private VideoSnippet videoSnippet;
    private Date dateSent;
    private SupplementaryFiles supplementaryFiles;
    private HeartRate heartRate;

    private transient FirebaseAuth mAuth;
    private transient FirebaseUser mUser;
    private transient static FirebaseDatabase database;
    private transient DatabaseReference dataPacketStorageRef;

    private boolean dataPacketSuccess = true;

    public DataPacket()
    {
        this.dataPackedId = UUID.randomUUID().toString();
        this.mAuth = FirebaseAuth.getInstance();
        this.mUser = mAuth.getCurrentUser();
        this.userId = mUser.getUid();
        this.userEmail = mUser.getEmail();
    }

    @Override
    public String toString() {
        String locationString;
        if (location != null)
            locationString = location.toString();
        else
            locationString = "null";
        return "DataPacket{" +
                "\ndataPackedId=" + dataPackedId +
                ", \nUserId=" + userId +
                ", \nDoctorId=" + doctorId +
                ", \nDoctorName=" + doctorName +
                ", \nUserEmail=" + userEmail +
                ", \ntextData=" + textData +
                ", \nlocation=" + locationString +
                ", \nvideoSnippet=" + videoSnippet +
                ", \nsupplementaryFiles=" + supplementaryFiles +
                ", \nheartRate=" + heartRate +
                "\n}";
    }

    public boolean send(Context context)
    {
        Toast.makeText(context, "Sending...", Toast.LENGTH_SHORT).show();
        this.setDateSent(new Date());

        try {
            database = FirebaseDatabase.getInstance();
            dataPacketStorageRef = database.getReference().child("DataPackets");
            dataPacketStorageRef.child(userId).child(dataPackedId).setValue(this);
        }
        catch (Exception e) {
            Log.d("DataPacket", e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }

        return uploadFiles();
    }

    private boolean uploadFiles()
    {
        boolean returnValue;

        if (getSupplementaryFiles() != null && getVideoSnippet() != null)
        {
            return uploadSupplementaryFiles() && uploadVideoSnippets();
        }
        else if (getVideoSnippet() != null && getSupplementaryFiles() == null)
        {
            return uploadVideoSnippets();
        }
        else if (getVideoSnippet() == null && getSupplementaryFiles() != null)
        {
            return uploadSupplementaryFiles();
        }
        else
        {
            return true;
        }
    }

    private boolean uploadVideoSnippets()
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        for (int i = 0; i < getVideoSnippet().getVideoSnippetsNames().size(); i++)
        {
            String fileName = getVideoSnippet().getVideoSnippetsNames().get(i);
            String filePath = getVideoSnippet().getVideoSnippetsPath().get(i);

            StorageReference supplementaryImageRef = storageRef.child(userId + "/" + dataPackedId + "/videos/" + fileName);

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
                    Log.d("DataPacket", "Videos Uploaded");
                }
            });
        }

        return dataPacketSuccess;
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
                    Log.d("DataPacket", "Supp Files uploaded");
                }
            });
        }

        return dataPacketSuccess;
    }

    public void showDataPacketDialog(Context context)
    {
        String dataPacketSummary = "Description: " + getTextData() +
                                   "\nDate Sent: " + getDateSent() +
                                   "\nDoctor: " + getDoctorName() +
                                   "\nLocation: " + getLocation() +
                                   "\nFiles: " + (getSupplementaryFiles() == null ? "null" : getSupplementaryFiles().getFileNames()) +
                                   "\nHeart Rate: " + getHeartRate() +
                                   "\nVideo Snippets " + (getVideoSnippet() == null ? "null" : getVideoSnippet().getVideoSnippetsNames());

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


    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

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

    public Location getLocation() {
        return location;
    }

    public void addLocation(Location location) {
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
