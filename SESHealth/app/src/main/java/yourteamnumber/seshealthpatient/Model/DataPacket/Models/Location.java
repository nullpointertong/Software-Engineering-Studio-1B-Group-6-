package yourteamnumber.seshealthpatient.Model.DataPacket.Models;

import com.google.android.gms.maps.model.LatLng;

public class Location {

    private double latitude;
    private double longitude;
    private String name;
    private String address;

    public Location(String name, String address, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(String name, String address, LatLng latLng) {
        this.name = name;
        this.address = address;
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }

    public double getLatitude () { return latitude; }
    public void setLatitude (double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude () { return longitude; }
    public void setLongitude (double longitude) {
        this.longitude = longitude;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }


    public String getAddress() { return address; }
    public void setAddress() { this.address = address; }


    @Override
    public String toString() {
        return name + ", " + address + ", " + latitude + ", " + longitude;
    }

}
