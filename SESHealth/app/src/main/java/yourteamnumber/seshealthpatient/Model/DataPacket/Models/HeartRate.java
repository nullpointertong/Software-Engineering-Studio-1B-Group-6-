package yourteamnumber.seshealthpatient.Model.DataPacket.Models;

public class HeartRate {

    private int heartRate;

    public HeartRate(int heartRate)
    {
        this.heartRate = heartRate;
    }

    @Override
    public String toString() {
        return (Integer.toString(heartRate));
    }
}
