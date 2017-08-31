package app.woxapp.ui.map;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.util.List;

import javax.inject.Inject;

import app.woxapp.App;
import app.woxapp.R;
import app.woxapp.databinding.ActivityMapBinding;
import app.woxapp.di.modules.MapModule;
import app.woxapp.misc.DoubleArrayEvaluator;
import app.woxapp.models.Address;
import app.woxapp.models.Location;
import app.woxapp.models.MarkersWrapper;
import app.woxapp.models.RouteResponse;
import app.woxapp.ui.box.RouteBoxFragment;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapActivity extends AppCompatActivity implements IMapPresenter.View,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        RouteBoxFragment.OnUpdateMap<Address> {

    public static final int REQUEST_ROUTE = 193;
    public static final int RESULT_OK = 194;

    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 145;
    private final String MARKERS = "markers";
    private final String ROUTE = "route";
    private final String ANIMATION_TIME = "animation_time";
    private final String ANIMATED_VALUE = "animated_value";
    private static final int DEFAULT_ZOOM = 12;

    @Inject
    MapPresenter presenter;

    private ActivityMapBinding mBinding;
    private GoogleMap mMap;
    private Marker carMarker;
    private BitmapDescriptor carBitmap;
    private ValueAnimator latLngAnimator;
    private GoogleApiClient mGoogleApiClient;
    private RouteBoxFragment fragment;
    private android.location.Location mLastKnownLocation;
    private boolean mLocationPermissionGranted;

    private MarkersWrapper markersWrapper = new MarkersWrapper();
    private RouteResponse route;
    private long animationTime;
    private double[] animatedValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_map);

        App.getComponent()
                .plus(new MapModule(this))
                .inject(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

        if (savedInstanceState != null) {
            markersWrapper = new Gson().fromJson(savedInstanceState.getString(MARKERS), MarkersWrapper.class);
            route = new Gson().fromJson(savedInstanceState.getString(ROUTE), RouteResponse.class);
            animationTime = savedInstanceState.getLong(ANIMATION_TIME);
            animatedValue = savedInstanceState.getDoubleArray(ANIMATED_VALUE);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment f = fragmentManager.findFragmentById(R.id.route);
        if (f == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.route, fragment = RouteBoxFragment.newInstance())
                    .commit();
        } else {
            if (f instanceof RouteBoxFragment) {
                fragment = (RouteBoxFragment) f;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mBinding.fab.setVisibility(View.VISIBLE);

        if (markersWrapper.getLocations().isEmpty()) {
            moveToDeviceLocation();
        } else {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Location l : markersWrapper.getLocations()) {
                builder.include(
                        mMap.addMarker(new MarkerOptions().position(l.getLatLng()))
                                .getPosition()
                );
            }

            if (route != null) {
                showRoute(route);
            }

            if (latLngAnimator == null && animationTime > 0 && animatedValue != null) {
                fragment.setBoxEnabled(false);
                presenter.restartCarAnimation(route, animatedValue[0], animatedValue[1]);
            }
        }
    }

    public void updateMap(List<Address> addresses) {
        if (addresses != null && !addresses.isEmpty()) {
            route = null;
            markersWrapper.updateLocations(addresses);

            if (addresses.size() > 1) {
                presenter.loadRoute(markersWrapper);
            }

            if (mMap != null) {
                mMap.clear();

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Address a : addresses) {
                    builder.include(
                            mMap.addMarker(new MarkerOptions().position(a.getLatLng()))
                                    .getPosition()
                    );
                }

                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50));
            }
        }
    }

    @Override
    public void showRoute(RouteResponse route) {
        this.route = route;
        if (mMap != null) {
            PolylineOptions line = new PolylineOptions()
                    .width(8f)
                    .color(getResources().getColor(R.color.colorPrimaryDark))
                    .addAll(route.getPositions());

            mMap.addPolyline(line);
        }
    }

    @Override
    public void showCarAnimation(long duration) {
        LatLng location;
        Object positions[] = new Object[route.getPositions().size()];

        for (int i = 0; i < positions.length; i++) {
            location = route.getPositions().get(i);
            positions[i] = new double[]{location.latitude, location.longitude};
        }

        latLngAnimator = createAnimator(duration, positions); //duration * 1000
        latLngAnimator.start();
    }

    @Override
    public void restartCarAnimation(Integer index) {
        LatLng location, startFrom = route.getPositions().get(index);
        Object positions[] = new Object[route.getPositions().size() - index];
        for (int i = 0; i < positions.length; i++) {
            location = route.getPositions().get(index + i);
            positions[i] = new double[]{location.latitude, location.longitude};
        }

        carMarker = mMap.addMarker(getCarMarkerOptions(startFrom));
        latLngAnimator = createAnimator(animationTime, positions);
        latLngAnimator.start();
    }

    private void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    private void moveToDeviceLocation() {
        checkPermission(ACCESS_FINE_LOCATION, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastKnownLocation == null) {
                return;
            }
        } else {
            return;
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
    }

    private ValueAnimator createAnimator(long duration, Object positions[]) {
        ValueAnimator latLngAnimator = ValueAnimator.ofObject(new DoubleArrayEvaluator(), positions);
        latLngAnimator.setDuration(duration);
        latLngAnimator.setInterpolator(new LinearInterpolator());
        latLngAnimator.addUpdateListener(animation -> {
            animationTime = animation.getDuration() - animation.getCurrentPlayTime();
            animatedValue = (double[]) animation.getAnimatedValue();

            carMarker.setPosition(new LatLng(animatedValue[0], animatedValue[1]));
        });

        latLngAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mBinding.fab.setImageResource(R.drawable.ic_stop_black_24dp);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mBinding.fab.setImageResource(R.drawable.ic_directions_car_black_24dp);
                fragment.setBoxEnabled(true);
            }
        });

        return latLngAnimator;
    }

    private MarkerOptions getCarMarkerOptions(LatLng latLng) {
        if (carBitmap == null) {
            carBitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_car);
        }

        return new MarkerOptions()
                .position(latLng)
                .anchor(0.5f, 0.5f)
                .icon(carBitmap);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBinding.fab.setOnClickListener(view -> {
            if (latLngAnimator != null && latLngAnimator.isRunning()) {
                latLngAnimator.cancel();
                carMarker.remove();
            } else if (route != null) {
                presenter.loadRouteDuration(markersWrapper);
                fragment.setBoxEnabled(false);

                if (carMarker != null) {
                    carMarker.remove();
                }
                carMarker = mMap.addMarker(getCarMarkerOptions(route.getPositions().get(0)));
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.stop();
        if (mMap != null) {
            mMap.clear();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (latLngAnimator != null && latLngAnimator.isRunning()) {
            latLngAnimator.cancel();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(MARKERS, new Gson().toJson(markersWrapper));
        outState.putString(ROUTE, new Gson().toJson(route));

        if (latLngAnimator != null && latLngAnimator.isRunning()) {
            outState.putLong(ANIMATION_TIME, latLngAnimator.getDuration() - latLngAnimator.getCurrentPlayTime());
            outState.putDoubleArray(ANIMATED_VALUE, (double[]) latLngAnimator.getAnimatedValue());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    moveToDeviceLocation();
                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
