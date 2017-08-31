package app.woxapp.di.components;

import javax.inject.Singleton;

import app.woxapp.di.modules.AppModule;
import app.woxapp.di.modules.MapModule;
import app.woxapp.di.modules.RouteBoxModule;
import dagger.Component;

/**
 * Created by Lobster on 14.08.17.
 */

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    MapComponent plus(MapModule module);

    RouteBoxComponent plus(RouteBoxModule module);

}
