package app.woxapp.ui.box;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.List;

/**
 * Created by Lobster on 23.08.17.
 */

public class SuggestionsAdapter<T> extends ArrayAdapter<T> implements Filterable {

    private List<T> mSuggestions;

    public SuggestionsAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public int getCount() {
        return mSuggestions == null ? 0 : mSuggestions.size();
    }

    @Override
    public T getItem(int position) {
        return mSuggestions == null ? null : mSuggestions.get(position);
    }

    @Override
    public void clear() {
        super.clear();
        if (mSuggestions != null) {
            mSuggestions.clear();
        }
    }

    public void updateSuggestions(List<T> suggestions) {
        mSuggestions = suggestions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                return null;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            }
        };
    }

}
