package app.woxapp.di.modules;

import app.woxapp.ui.box.IRouteBoxPresenter;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Lobster on 14.08.17.
 */

@Module
public class RouteBoxModule {

    private IRouteBoxPresenter.View mView;

    public RouteBoxModule(IRouteBoxPresenter.View view) {
        mView = view;
    }

    @Provides
    public IRouteBoxPresenter.View provideRouteBoxView() {
        return mView;
    }

}
