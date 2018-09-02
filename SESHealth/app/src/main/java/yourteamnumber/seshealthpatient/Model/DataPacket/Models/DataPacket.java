package yourteamnumber.seshealthpatient.Model.DataPacket.Models;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.UUID;

public class DataPacket implements Serializable{

    private UUID dataPackedId;
    private TextData textData;
    private LatLng location;
    private VideoSnippet videoSnippet;
    private SupplementaryFiles supplementaryFiles;
    private HeartRate heartRate;

    public DataPacket()
    {
        this.dataPackedId = UUID.randomUUID();
    }

    @Override
    public String toString() {
        return "DataPacket{" +
                "\ndataPackedId=" + dataPackedId +
                ", \ntextData=" + textData +
                ", \nlocation=" + location +
                ", \nvideoSnippet=" + videoSnippet +
                ", \nsupplementaryFiles=" + supplementaryFiles +
                ", \nheartRate=" + heartRate +
                "\n}";
    }

    public void Send(Context context)
    {
        ShowDataPacketDialog(this.toString(), context);
    }

    private void ShowDataPacketDialog(String message, Context context)
    {
        AlertDialog.Builder alertDialogBuilder =
                new AlertDialog.Builder(context)
                        .setTitle("Data Packet: ")
                        .setMessage(message)
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

        alertDialogBuilder.show();
    }

    //region Getters and Setters

    public UUID getDataPackedId() {
        return dataPackedId;
    }

    public void addDataPackedId(UUID dataPackedId) {
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
