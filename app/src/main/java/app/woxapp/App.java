package app.woxapp;

import android.app.Application;

import app.woxapp.di.components.AppComponent;
import app.woxapp.di.components.DaggerAppComponent;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Lobster on 15.08.17.
 */

public class App extends Application {

    private static AppComponent component;

    public static AppComponent getComponent() {
        return component;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        Realm.setDefaultConfiguration(
                new RealmConfiguration.Builder()
                        .deleteRealmIfMigrationNeeded()
                        .build());

        component = DaggerAppComponent.builder()
                .build();
    }
}
