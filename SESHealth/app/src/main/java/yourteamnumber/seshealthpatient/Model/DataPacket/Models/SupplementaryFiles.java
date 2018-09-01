package yourteamnumber.seshealthpatient.Model.DataPacket.Models;

import java.util.ArrayList;

public class SupplementaryFiles {

    ArrayList<String> filePaths;

    public SupplementaryFiles(ArrayList<String> filePaths)
    {
        this.filePaths = filePaths;
    }

    public ArrayList<String> getFilePaths()
    {
        return filePaths;
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
