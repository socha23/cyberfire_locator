package pl.socha23.cyberfirelocator;

import android.location.Location;

public class LocationFormatter {
    public static String format(Location location) {
        if (location == null) {
            return "unknown location";
        } else {
            return formatLat(location.getLatitude()) + "\n" + formatLng(location.getLongitude());
        }

    }

    public static String formatLat(double lat) {
        return format(lat, "N", "S");
    }

    public static String formatLng(double lng) {
        return format(lng, "E", "W");
    }

    public static String format(double x, String positive, String negative) {
        String prefix = x >= 0 ? positive : negative;
        x = Math.abs(x);

        StringBuilder sb = new StringBuilder(prefix).append(" ");
        String latitudeDegrees = Location.convert(x, Location.FORMAT_SECONDS);
        String[] latitudeSplit = latitudeDegrees.split(":");
        sb.append(latitudeSplit[0]);
        sb.append("°");
        sb.append(latitudeSplit[1]);
        sb.append("'");
        sb.append(latitudeSplit[2]);
        sb.append("\"");

        return sb.toString();
    }

}
