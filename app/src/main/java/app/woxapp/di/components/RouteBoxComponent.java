package app.woxapp.di.components;

import app.woxapp.di.modules.RouteBoxModule;
import app.woxapp.ui.box.RouteBoxFragment;
import dagger.Subcomponent;

/**
 * Created by Lobster on 15.08.17.
 */

@Subcomponent(modules = {RouteBoxModule.class})
public interface RouteBoxComponent {

    void inject(RouteBoxFragment fragment);

}
