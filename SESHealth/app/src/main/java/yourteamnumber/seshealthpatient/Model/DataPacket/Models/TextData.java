package yourteamnumber.seshealthpatient.Model.DataPacket.Models;

import java.util.Arrays;

public class TextData {
    private String data;

    public TextData(String data)
    {
        this.data = data;
    }

    public String getData()
    {
        return data;
    }

    @Override
    public String toString() {
        return data;
    }
}
