package app.woxapp.ui.box;

import java.util.List;

import app.woxapp.models.Address;
import app.woxapp.models.GeocodeResponse;
import io.reactivex.disposables.Disposable;

/**
 * Created by Lobster on 14.08.17.
 */

public interface IRouteBoxPresenter {

    interface Actions {

        Disposable loadSuggestions(String query);

        void saveAddresses(List<Address> addresses);

        void getRouteById(String routeId);

        void stop();

    }

    interface View {

        void showSuggestions(GeocodeResponse response);

        void updateRouteBox(List<Address> addresses);

        void showToast(String msg);

    }

}
