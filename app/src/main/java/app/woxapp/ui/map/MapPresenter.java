package app.woxapp.ui.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import app.woxapp.api.GoogleMapsApi;
import app.woxapp.models.DurationElement;
import app.woxapp.models.DurationRow;
import app.woxapp.models.Location;
import app.woxapp.models.MarkersWrapper;
import app.woxapp.models.Route;
import app.woxapp.models.RouteResponse;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Lobster on 15.08.17.
 */

public class MapPresenter implements IMapPresenter.Actions {

    private IMapPresenter.View view;
    private GoogleMapsApi api;

    private Disposable loadRouteDisposable;
    private CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    public MapPresenter(IMapPresenter.View view, GoogleMapsApi api) {
        this.view = view;
        this.api = api;
    }

    @Override
    public void loadRoute(MarkersWrapper route) {
        if (loadRouteDisposable != null && !loadRouteDisposable.isDisposed())
            loadRouteDisposable.dispose();

        loadRouteDisposable =
                api.getRoute(route.getStart(), route.getFinish(), transformToWayPoints(route.getLocations(), 0, route.getLocations().size() - 1))
                        .doOnNext(routeResponse -> {
                            List<LatLng> positions = new ArrayList<>();
                            for (Route r : routeResponse.routes) {
                                positions.addAll(PolyUtil.decode(r.polyline.points));
                            }

                            routeResponse.setPositions(positions);
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                routeResponse -> view.showRoute(routeResponse),
                                Throwable::printStackTrace
                        );

        disposable.add(loadRouteDisposable);
    }

    @Override
    public void loadRouteDuration(MarkersWrapper route) {
        disposable.add(
                api.getRouteDuration(route.getStart(), transformToWayPoints(route.getLocations(), 0, route.getLocations().size()))
                        .map(durationResponse -> {
                            long duration = 0;
                            for (DurationRow dr : durationResponse.rows)
                                for (DurationElement de : dr.elements)
                                    duration += de.duration.getValue();

                            return duration;
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                duration -> view.showCarAnimation(duration),
                                Throwable::printStackTrace
                        )
        );
    }

    @Override
    public void restartCarAnimation(final RouteResponse route, final double latitude, final double longitude) {
        disposable.add(
                Single.create(new SingleOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(SingleEmitter<Integer> e) throws Exception {
                        int index = 0;
                        LatLng last = null;
                        for (LatLng current : route.getPositions()) {
                            if (last != null) {
                                if (latitude < Math.max(last.latitude, current.latitude) && latitude > Math.min(last.latitude, current.latitude) &&
                                        longitude < Math.max(last.longitude, current.longitude) && longitude > Math.min(last.longitude, current.longitude)) {
                                    e.onSuccess(index - 1);
                                }
                            }

                            last = current;
                            index++;
                        }

                        e.onSuccess(0);
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(view::restartCarAnimation)
        );
    }

    @Override
    public String transformToWayPoints(List<Location> locations, int beginIndex, int endIndex) {
        if (beginIndex < 0 || beginIndex >= endIndex || endIndex > locations.size()) {
            throw new IndexOutOfBoundsException();
        }

        String wayPoints = "";
        for (int i = beginIndex; i < endIndex; i++) {
            wayPoints += locations.get(i).toString() + "|";
        }

        return wayPoints.substring(0, wayPoints.length() - 1);
    }

    @Override
    public void stop() {
        disposable.clear();
    }

}
