package pl.socha23.cyberfirelocator;

import java.util.List;

public class NearbyDevicesFoundEvent {

    private List<NearbyDevice> devices;

    public NearbyDevicesFoundEvent(List<NearbyDevice> devices) {
        this.devices = devices;
    }

    public List<NearbyDevice> getDevices() {
        return devices;
    }
}
