package app.woxapp.api;

import app.woxapp.models.DurationResponse;
import app.woxapp.models.GeocodeResponse;
import app.woxapp.models.RouteResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Lobster on 14.08.17.
 */

public interface GoogleMapsApi {

    @GET("geocode/json")
    Observable<GeocodeResponse> getAddresses(@Query("address") String query);

    @GET("directions/json")
    Observable<RouteResponse> getRoute(@Query("origin") String start, @Query("destination") String finish, @Query("waypoints") String waypoints);

    @GET("distancematrix/json")
    Observable<DurationResponse> getRouteDuration(@Query("origins") String start, @Query("destinations") String finish);

}
