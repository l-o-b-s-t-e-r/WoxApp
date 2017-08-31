package app.woxapp.ui.box;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import app.woxapp.api.GoogleMapsApi;
import app.woxapp.models.Address;
import app.woxapp.models.AddressRealm;
import app.woxapp.models.GeocodeResponse;
import app.woxapp.models.RouteRealm;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmChangeListener;

/**
 * Created by Lobster on 14.08.17.
 */

public class RouteBoxPresenter implements IRouteBoxPresenter.Actions {

    private IRouteBoxPresenter.View view;
    private GoogleMapsApi api;
    private Realm realm;
    private RealmAsyncTask transaction;

    @Inject
    public RouteBoxPresenter(IRouteBoxPresenter.View view, GoogleMapsApi api, Realm realm) {
        this.view = view;
        this.api = api;
        this.realm = realm;
    }

    @Override
    public Disposable loadSuggestions(String query) {
        return api.getAddresses(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        geocodeResponse -> view.showSuggestions(geocodeResponse),
                        throwable -> view.showToast("Can't load addresses")
                );
    }

    @Override
    public void saveAddresses(final List<Address> addresses) {
        if (addresses.size() > 1) {
            transaction = realm.executeTransactionAsync(realm1 -> {
                        Address address;
                        AddressRealm addressRealm;
                        RouteRealm route = realm1.createObject(RouteRealm.class, UUID.randomUUID().toString());
                        for (int i = 0; i < addresses.size(); i++) {
                            address = addresses.get(i);

                            addressRealm = realm1.createObject(AddressRealm.class);
                            addressRealm.setAddress(address.getAddress());
                            addressRealm.setLatitude(address.getLocation().getLatitude());
                            addressRealm.setLongitude(address.getLocation().getLongitude());

                            route.setTitle(i == 0 ? address.getAddress() : route.getTitle() + " - " + address.getAddress());
                            route.getAddresses().add(addressRealm);
                        }
                    },
                    () -> view.showToast("Route saved"),
                    error -> view.showToast("Route not saved"));
        }
    }

    @Override
    public void getRouteById(String routeId) {
        realm.where(RouteRealm.class)
                .equalTo("id", routeId)
                .findFirstAsync()
                .addChangeListener(new RealmChangeListener<RouteRealm>() {
                    @Override
                    public void onChange(RouteRealm route) {
                        view.updateRouteBox(new GeocodeResponse(route).addresses);
                    }
                });
    }

    @Override
    public void stop() {
        realm.close();
        if (transaction != null && !transaction.isCancelled()) {
            transaction.cancel();
        }
    }
}
