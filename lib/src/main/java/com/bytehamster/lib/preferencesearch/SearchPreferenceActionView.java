package com.bytehamster.lib.preferencesearch;

import android.app.FragmentManager;
import android.content.Context;
import android.preference.PreferenceActivity;
import android.util.AttributeSet;

import androidx.appcompat.widget.SearchView;

public class SearchPreferenceActionView extends SearchView {
    private SearchPreferenceFragment searchFragment;
    private SearchConfiguration searchConfiguration = new SearchConfiguration();
    private PreferenceActivity activity;

    public SearchPreferenceActionView(Context context) {
        super(context);
        initView();
    }

    public SearchPreferenceActionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SearchPreferenceActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        searchConfiguration.setSearchBarEnabled(false);
        setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (searchFragment != null) {
                    searchFragment.setSearchTerm(newText);
                }
                return true;
            }
        });
        setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && (searchFragment == null || !searchFragment.isVisible())) {
                searchFragment = searchConfiguration.showSearchFragment();
                searchFragment.setHistoryClickListener(entry -> setQuery(entry, false));
            }
        });
    }

    public SearchConfiguration getSearchConfiguration() {
        return searchConfiguration;
    }

    /**
     * Hides the search fragment
     * @return true if it was hidden, so the calling activity should not go back itself.
     */
    public boolean cancelSearch() {
        setQuery("", false);

        boolean didSomething = false;
        if (!isIconified()) {
            setIconified(true);
            didSomething = true;
        }
        if (searchFragment != null && searchFragment.isVisible()) {
            removeFragment();

            /*
            AnimationUtils.startCircularExitAnimation(getContext(), searchFragment.getView(),
                    getSearchConfiguration().getRevealAnimationSetting(),
                    new AnimationUtils.OnDismissedListener() {
                @Override
                public void onDismissed() {
                    removeFragment();
                }
            });
            */

            didSomething = true;
        }
        return didSomething;
    }

    private void removeFragment() {
        if (searchFragment.isVisible()) {
            FragmentManager fm = activity.getFragmentManager();
            fm.beginTransaction().remove(searchFragment).commit();
            fm.popBackStack(SearchPreferenceFragment.NAME, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void setActivity(PreferenceActivity activity) {
        searchConfiguration.setActivity(activity);
        this.activity = activity;
    }
}
