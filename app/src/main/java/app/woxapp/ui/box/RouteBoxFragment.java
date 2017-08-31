package app.woxapp.ui.box;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import app.woxapp.App;
import app.woxapp.R;
import app.woxapp.databinding.FragmentRouteBoxBinding;
import app.woxapp.di.modules.RouteBoxModule;
import app.woxapp.models.Address;
import app.woxapp.models.GeocodeResponse;
import app.woxapp.ui.routes.RoutesActivity;
import io.reactivex.disposables.Disposable;

import static app.woxapp.ui.map.MapActivity.REQUEST_ROUTE;
import static app.woxapp.ui.map.MapActivity.RESULT_OK;


public class RouteBoxFragment extends Fragment implements IRouteBoxPresenter.View, RouteBoxAdapter.OnTextChangedListener {

    public static final String ROUTE = "route";

    public static RouteBoxFragment newInstance() {
        RouteBoxFragment fragment = new RouteBoxFragment();
        fragment.setRetainInstance(true);

        return fragment;
    }

    public interface OnUpdateMap<T> {
        void updateMap(List<T> addresses);
    }

    @Inject
    RouteBoxPresenter presenter;

    private FragmentRouteBoxBinding mBinding;
    private RouteBoxAdapter<Address> adapter;
    private OnUpdateMap<Address> mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUpdateMap) {
            mListener = (OnUpdateMap) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnUpdateMap");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mBinding == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_route_box, container, false);

            App.getComponent()
                    .plus(new RouteBoxModule(this))
                    .inject(this);

            mBinding.fabSave.setOnClickListener(view -> presenter.saveAddresses(adapter.getSelectedSuggestions()));

            mBinding.fabLoad.setOnClickListener(view -> startActivityForResult(new Intent(getActivity(), RoutesActivity.class), REQUEST_ROUTE));
        }

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (adapter == null) {
            adapter = new RouteBoxAdapter<>(mBinding.routeBox, this, mListener);
            adapter.addView();
            adapter.addView();
        } else {
            adapter.setUpdateMapListener(mListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.clearSuggestions();
        adapter.dispose();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_ROUTE) {
            presenter.getRouteById(data.getExtras().getString(ROUTE));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.stop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setBoxEnabled(boolean enabled) {
        adapter.enableViews(enabled);
        mBinding.fabLoad.setEnabled(enabled);
        mBinding.fabSave.setEnabled(enabled);
    }

    @Override
    public void showSuggestions(GeocodeResponse response) {
        adapter.showSuggestions(response.addresses);
    }

    @Override
    public void updateRouteBox(List<Address> addresses) {
        adapter.updateViewHolders(addresses);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Disposable loadSuggestions(String query) {
        return presenter.loadSuggestions(query);
    }
}
