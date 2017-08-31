package app.woxapp.ui.routes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.woxapp.R;
import app.woxapp.models.RouteRealm;

/**
 * Created by Lobster on 16.08.17.
 */

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onClick(String routeId);
    }

    private List<RouteRealm> routes = new ArrayList<>();
    private OnItemClickListener listener;

    public RoutesAdapter(List<RouteRealm> routes, OnItemClickListener listener) {
        this.routes = routes;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int layoutId) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setContent(routes.get(position));
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RouteRealm route;

        private TextView title;

        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.address);
            view.setOnClickListener(v -> listener.onClick(route.getId()));
        }

        void setContent(RouteRealm route) {
            this.route = route;
            title.setText(route.getTitle());
        }
    }
}
