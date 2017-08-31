package app.woxapp.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Lobster on 14.08.17.
 */

public class Location {

    @SerializedName("lat")
    private double latitude;

    @SerializedName("lng")
    private double longitude;

    private LatLng latLng;

    public Location() {

    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(LatLng latLng) {
        latitude = latLng.latitude;
        longitude = latLng.longitude;
    }

    public LatLng getLatLng() {
        if (latLng == null) {
            latLng = new LatLng(latitude, longitude);
        }

        return latLng;
    }

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

    @Override
    public String toString() {
        return latitude + "," + longitude;
    }
}
