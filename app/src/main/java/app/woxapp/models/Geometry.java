package app.woxapp.models;

/**
 * Created by Lobster on 14.08.17.
 */

public class Geometry {

    public Location location;

    public Geometry() {

    }

    public Geometry(double latitude, double longitude) {
        location = new Location(latitude, longitude);
    }
}
