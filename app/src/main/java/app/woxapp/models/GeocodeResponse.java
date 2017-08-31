package app.woxapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lobster on 14.08.17.
 */

public class GeocodeResponse {

    @SerializedName("results")
    public List<Address> addresses;

    private List<String> shortAddresses;

    public GeocodeResponse() {

    }

    public GeocodeResponse(List<Address> addresses) {
        this.addresses = addresses;
    }

    public GeocodeResponse(RouteRealm route) {
        addresses = new ArrayList<>();
        for (AddressRealm a : route.getAddresses()) {
            addresses.add(new Address(a));
        }
    }

}
