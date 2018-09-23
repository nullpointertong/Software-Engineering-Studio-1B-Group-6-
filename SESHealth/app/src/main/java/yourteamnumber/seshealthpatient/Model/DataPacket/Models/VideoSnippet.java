package yourteamnumber.seshealthpatient.Model.DataPacket.Models;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class VideoSnippet implements Serializable{

    private int numVideoSnippets;
    private ArrayList<String> videoSnippetsPath = new ArrayList<>();
    private ArrayList<String> videoSnippetsNames = new ArrayList<>();

    public VideoSnippet(){this.numVideoSnippets = 1;}

    public int getNumVideoSnippets() {
        return numVideoSnippets;
    }

    public void setNumVideoSnippets(int numVideoSnippets) {
        this.numVideoSnippets = numVideoSnippets;
    }

    public ArrayList<String> getVideoSnippetsPath() {
        return videoSnippetsPath;
    }

    public void setVideoSnippetsPath(ArrayList<String> videoSnippetsPath) {
        this.videoSnippetsPath = videoSnippetsPath;
    }

    public ArrayList<String> getVideoSnippetsNames() {
        return videoSnippetsNames;
    }

    public void setVideoSnippetsNames(ArrayList<String> videoSnippetsNames) {
        this.videoSnippetsNames = videoSnippetsNames;
    }

    public void addVideoSnippets(File videoSnippet)
    {
        this.videoSnippetsPath.add(videoSnippet.getAbsolutePath());
        this.videoSnippetsNames.add(videoSnippet.getName());
        numVideoSnippets++;
    }


    @Override
    public String toString()
    {
        return "N/A";
    }
}
