package app.woxapp.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Lobster on 16.08.17.
 */

public class RouteRealm extends RealmObject {

    @PrimaryKey
    private String id;

    private String title;

    private RealmList<AddressRealm> addresses;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RealmList<AddressRealm> getAddresses() {
        return addresses;
    }

    public void setAddresses(RealmList<AddressRealm> addresses) {
        this.addresses = addresses;
    }
}
