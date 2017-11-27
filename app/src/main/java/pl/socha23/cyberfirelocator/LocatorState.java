package pl.socha23.cyberfirelocator;

import java.util.List;

public class LocatorState {

    private double latitude;
    private double longitude;
    private String id;
    private String name;
    private String type;
    private List<NearbyDevice> nearbyDevices;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<NearbyDevice> getNearbyDevices() {
        return nearbyDevices;
    }

    public void setNearbyDevices(List<NearbyDevice> nearbyDevices) {
        this.nearbyDevices = nearbyDevices;
    }
}
