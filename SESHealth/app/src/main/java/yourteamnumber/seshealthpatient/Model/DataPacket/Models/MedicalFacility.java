package yourteamnumber.seshealthpatient.Model.DataPacket.Models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MedicalFacility extends Location {

    private List<String> physicians;

    public MedicalFacility(String name, String address, List<String> physicians, LatLng location) {
        super(name, address, location);
        this.physicians = physicians;
    }

    public MedicalFacility() {
        super("Default name", "Default address", new LatLng(0,0));
        this.physicians = new ArrayList<String>();
    }

    public MedicalFacility(String name, String address, List<String> physicians, double latitude, double longitude)
    {
        super(name, address, latitude, longitude);
        this.physicians = physicians;
    }

    public List<String> getPhysicians() {
        return physicians;
    }

}
