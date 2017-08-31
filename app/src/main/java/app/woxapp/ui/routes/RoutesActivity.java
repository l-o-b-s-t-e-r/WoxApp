package app.woxapp.ui.routes;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import app.woxapp.R;
import app.woxapp.databinding.ActivityRoutesBinding;
import app.woxapp.models.RouteRealm;
import app.woxapp.ui.map.MapActivity;
import io.realm.Realm;

import static app.woxapp.ui.box.RouteBoxFragment.ROUTE;

public class RoutesActivity extends AppCompatActivity implements RoutesAdapter.OnItemClickListener {

    private ActivityRoutesBinding mBinding;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_routes);

        realm = Realm.getDefaultInstance();

        RoutesAdapter adapter = new RoutesAdapter(realm.where(RouteRealm.class).findAll(), this);
        mBinding.meetings.setAdapter(adapter);
        mBinding.meetings.setLayoutManager(new LinearLayoutManager(RoutesActivity.this, LinearLayoutManager.VERTICAL, false));

        mBinding.emptyList.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(String routeId) {
        setResult(MapActivity.RESULT_OK, new Intent().putExtra(ROUTE, routeId));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
