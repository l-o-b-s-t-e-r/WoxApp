package app.woxapp.di.modules;

import javax.inject.Singleton;

import app.woxapp.api.GoogleMapsApi;
import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Lobster on 15.08.17.
 */

@Module
public class AppModule {

    private static final String ENDPOINT = "https://maps.googleapis.com/maps/api/";

    @Provides
    public Realm provideRealm() {
        return Realm.getDefaultInstance();
    }

    @Provides
    @Singleton
    public GoogleMapsApi provideAuthRetrofit() {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(ENDPOINT)
                .build()
                .create(GoogleMapsApi.class);
    }

}
