package app.woxapp.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Lobster on 14.08.17.
 */

public class Address {

    @SerializedName("formatted_address")
    private String address;

    private Geometry geometry;

    public Address() {

    }

    public Address(AddressRealm a) {
        address = a.getAddress();
        geometry = new Geometry(a.getLatitude(), a.getLongitude());
    }

    public Address(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public LatLng getLatLng() {
        return geometry != null ? geometry.location.getLatLng() : null;
    }

    public Location getLocation() {
        return geometry.location;
    }

    @Override
    public String toString() {
        return address;
    }
}
