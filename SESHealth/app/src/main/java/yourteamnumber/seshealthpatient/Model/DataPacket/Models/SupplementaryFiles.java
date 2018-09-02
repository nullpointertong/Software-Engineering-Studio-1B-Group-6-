package yourteamnumber.seshealthpatient.Model.DataPacket.Models;

import java.util.ArrayList;

public class SupplementaryFiles {

    ArrayList<String> filePaths;
    ArrayList<String> fileNames;

    public SupplementaryFiles()
    {
    }

    public void setFilePaths(ArrayList<String> filePaths) {
        this.filePaths = filePaths;
    }

    public void setFileNames(ArrayList<String> fileNames) {
        this.fileNames = fileNames;
    }

    public ArrayList<String> getFilePaths()
    {
        return filePaths;
    }

    public ArrayList<String> getFileNames()
    {
        return fileNames;
    }

    @Override
    public String toString() {
        StringBuilder filePathsString = new StringBuilder();

        for (String filePath : filePaths)
        {
            filePathsString.append(filePath);
        }

        return filePathsString.toString();
    }
}
