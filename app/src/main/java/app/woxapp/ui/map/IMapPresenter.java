package app.woxapp.ui.map;

import java.util.List;

import app.woxapp.models.Location;
import app.woxapp.models.MarkersWrapper;
import app.woxapp.models.RouteResponse;

/**
 * Created by Lobster on 15.08.17.
 */

public interface IMapPresenter {

    interface Actions {

        void loadRoute(MarkersWrapper markersWrapper);

        void loadRouteDuration(MarkersWrapper markersWrapper);

        void restartCarAnimation(RouteResponse route, double latitude, double longitude);

        String transformToWayPoints(List<Location> locations, int beginIndex, int endIndex);

        void stop();

    }

    interface View {

        void showRoute(RouteResponse route);

        void showCarAnimation(long duration);

        void restartCarAnimation(Integer index);

    }

}
