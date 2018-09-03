package yourteamnumber.seshealthpatient.Model.DataPacket.Models;

import java.io.Serializable;

public class HeartRate implements Serializable{

    private int heartRate;

    public HeartRate()
    {
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    @Override
    public String toString() {
        return (Integer.toString(heartRate));
    }
}
