package app.woxapp.ui.box;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import app.woxapp.R;
import app.woxapp.databinding.StopFieldBinding;
import io.reactivex.disposables.Disposable;

/**
 * Created by Lobster on 23.08.17.
 */

public class RouteBoxAdapter<T> {

    public interface OnTextChangedListener {
        Disposable loadSuggestions(String query);
    }

    private final int MAX_VIEWS = 5;

    private ViewGroup mParent;

    private ViewHolder mLastVH;
    private ViewHolder mFocusedVH;
    private Set<ViewHolder> mViews = new LinkedHashSet<>();
    private String mHints[];

    private SuggestionsAdapter<T> mSuggestionsAdapter;
    private OnTextChangedListener mTextChangedListener;
    private RouteBoxFragment.OnUpdateMap<T> mMapUpdateListener;
    private Disposable mDisposable;

    public RouteBoxAdapter(ViewGroup parent, OnTextChangedListener textChangedListener, RouteBoxFragment.OnUpdateMap<T> updateMapListener) {
        mParent = parent;
        mTextChangedListener = textChangedListener;
        mMapUpdateListener = updateMapListener;
        mHints = parent.getContext().getResources().getStringArray(R.array.stop_filed_hints);
        mSuggestionsAdapter = new SuggestionsAdapter<>(parent.getContext(), android.R.layout.simple_dropdown_item_1line);
    }

    public void updateViewHolders(List<T> newValues) {
        int i = 0;
        if (newValues.size() < mViews.size()) {
            Iterator<ViewHolder> iterator = mViews.iterator();
            while (iterator.hasNext()) {
                ViewHolder vh = iterator.next();
                if (i < newValues.size()) {
                    vh.setSelectedSuggestion(newValues.get(i++));
                } else {
                    iterator.remove();
                    mParent.removeView(vh.binding.frame);
                }
            }
        } else {
            Iterator<T> iterator = newValues.iterator();
            Iterator<ViewHolder> viewIterator = mViews.iterator();
            while (iterator.hasNext()) {
                T t = iterator.next();
                ViewHolder vh = viewIterator.hasNext() ? viewIterator.next() : null;

                if (vh != null) {
                    vh.setSelectedSuggestion(newValues.get(i++));
                } else {
                    addView();
                    mLastVH.setSelectedSuggestion(newValues.get(i++));
                }
            }
        }

        if (mViews.size() < MAX_VIEWS)
            addView();

        updateHints();

        mMapUpdateListener.updateMap(getSelectedSuggestions());
    }

    public void addView() {
        if (mViews.size() < MAX_VIEWS) {
            View view = LayoutInflater.from(mParent.getContext())
                    .inflate(R.layout.stop_field, mParent, false);

            mParent.addView(view);

            mViews.add(mLastVH = new ViewHolder(view, mViews.size()));
        }
    }

    public void showSuggestions(List<T> suggestions) {
        if (mFocusedVH != null)
            mFocusedVH.setSuggestions(suggestions);
    }

    public List<T> getSelectedSuggestions() {
        List<T> suggestions = new ArrayList<>();
        for (ViewHolder vh : mViews) {
            if (vh.getSelectedSuggestion() != null) {
                suggestions.add(vh.getSelectedSuggestion());
            }
        }

        return suggestions;
    }

    public void enableViews(boolean enabled) {
        for (ViewHolder vh : mViews) {
            vh.binding.stop.setEnabled(enabled);
        }
    }

    public void removeView(ViewHolder vh) {
        mViews.remove(vh);
        mParent.removeView(vh.binding.frame);
    }

    public void updateHints() {
        int i = 0;
        for (ViewHolder vh : mViews) {
            vh.binding.stop.setHint(mHints[i++]);
        }
    }

    public void setUpdateMapListener(RouteBoxFragment.OnUpdateMap<T> mapUpdateListener) {
        mMapUpdateListener = mapUpdateListener;
    }

    public void dispose() {
        if (mDisposable != null && !mDisposable.isDisposed())
            mDisposable.dispose();
    }

    public void clearSuggestions() {
        mSuggestionsAdapter.clear();
    }

    private class ViewHolder {

        private StopFieldBinding binding;
        private T selectedSuggestion;

        public ViewHolder(View view, final int pos) {
            binding = DataBindingUtil.bind(view);

            binding.stop.setId(UUID.randomUUID().hashCode());
            binding.stop.setHint(mHints[pos]);
            binding.stop.setAdapter(mSuggestionsAdapter);

            binding.stop.setOnFocusChangeListener((view1, focused) -> {
                if (focused) {
                    mFocusedVH = ViewHolder.this;
                } else {
                    mFocusedVH = null;
                    dispose();
                }

                if (focused && ViewHolder.this == mLastVH && mViews.size() < MAX_VIEWS) {
                    addView();
                }
            });

            binding.stop.setOnItemClickListener((adapterView, view12, position, id) -> {
                selectedSuggestion = (T) adapterView.getItemAtPosition(position);
                mMapUpdateListener.updateMap(getSelectedSuggestions());
            });

            binding.remove.setOnClickListener(view13 -> {
                if (mViews.size() > 2) {
                    if (mLastVH == ViewHolder.this) {
                        ViewHolder.this.selectedSuggestion = null;
                        ViewHolder.this.binding.stop.setText("");
                    } else {
                        removeView(ViewHolder.this);
                        updateHints();
                    }

                    mMapUpdateListener.updateMap(getSelectedSuggestions());
                }
            });

            RxTextView.textChangeEvents(binding.stop)
                    .skipInitialValue()
                    .debounce(300, TimeUnit.MILLISECONDS)
                    .filter(event -> event.text().length() >= 3)
                    .subscribe(event -> {
                        if (selectedSuggestion != null && !selectedSuggestion.toString().equals(event.text().toString())) {
                            selectedSuggestion = null;
                        }

                        dispose();
                        mDisposable = mTextChangedListener.loadSuggestions(event.text().toString());
                    });
        }

        public void setSuggestions(List<T> suggestions) {
            mSuggestionsAdapter.updateSuggestions(suggestions);
        }

        public T getSelectedSuggestion() {
            return selectedSuggestion;
        }

        public void setSelectedSuggestion(T suggestion) {
            selectedSuggestion = suggestion;
            binding.stop.setText(suggestion.toString());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ViewHolder that = (ViewHolder) o;

            return binding.stop.getId() == that.binding.stop.getId();

        }

        @Override
        public int hashCode() {
            return binding.stop.getId();
        }
    }
}
