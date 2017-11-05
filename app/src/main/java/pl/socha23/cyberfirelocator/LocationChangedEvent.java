package pl.socha23.cyberfirelocator;

import android.location.Location;

public class LocationChangedEvent {

    private Location location;

    public LocationChangedEvent(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
