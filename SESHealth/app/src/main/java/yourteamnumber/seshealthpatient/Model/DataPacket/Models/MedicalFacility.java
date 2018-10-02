package yourteamnumber.seshealthpatient.Model.DataPacket.Models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MedicalFacility extends Location {

    private String doctorName;

    public MedicalFacility()
    {

    }

    public MedicalFacility(String name, String address, double latitude, double longitude, String doctorName) {
        super(name, address, latitude, longitude);
        this.doctorName = doctorName;
    }

}
