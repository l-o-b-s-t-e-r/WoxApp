package app.woxapp.di.modules;

import app.woxapp.ui.map.IMapPresenter;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Lobster on 15.08.17.
 */

@Module
public class MapModule {

    private IMapPresenter.View mView;

    public MapModule(IMapPresenter.View view) {
        mView = view;
    }

    @Provides
    public IMapPresenter.View provideMapView() {
        return mView;
    }

}
