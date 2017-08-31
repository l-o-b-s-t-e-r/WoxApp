package app.woxapp.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lobster on 15.08.17.
 */

public class MarkersWrapper {

    private List<Location> locations = new ArrayList<>();

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public String getStart() {
        return locations.get(0).toString();
    }

    public String getFinish() {
        return locations.get(locations.size() - 1).toString();
    }

    public void updateLocations(List<Address> addresses) {
        locations.clear();
        for (Address a : addresses) {
            locations.add(a.getLocation());
        }
    }

    public int size() {
        return locations.size();
    }
}
