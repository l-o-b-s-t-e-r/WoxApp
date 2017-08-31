package app.woxapp.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Lobster on 15.08.17.
 */

public class RouteResponse {

    public List<Route> routes;

    private List<LatLng> positions;

    public List<LatLng> getPositions() {
        return positions;
    }

    public void setPositions(List<LatLng> positions) {
        this.positions = positions;
    }

}