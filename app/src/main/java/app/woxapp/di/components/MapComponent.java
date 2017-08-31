package app.woxapp.di.components;

import app.woxapp.di.modules.MapModule;
import app.woxapp.ui.map.MapActivity;
import dagger.Subcomponent;

/**
 * Created by Lobster on 15.08.17.
 */

@Subcomponent(modules = {MapModule.class})
public interface MapComponent {

    void inject(MapActivity activity);

}

