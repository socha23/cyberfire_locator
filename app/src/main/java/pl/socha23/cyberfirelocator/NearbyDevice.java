package pl.socha23.cyberfirelocator;

public class NearbyDevice {

    private String id;
    private int rssi;

    public NearbyDevice(String id, int rssi) {
        this.id = id;
        this.rssi = rssi;
    }

    public String getId() {
        return id;
    }

    public int getRssi() {
        return rssi;
    }
}
