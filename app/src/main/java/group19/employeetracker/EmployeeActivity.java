package group19.employeetracker;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by kylemart on 11/28/17.
 */

public class EmployeeActivity extends NavActivity
        implements LoaderManager.LoaderCallbacks<List<EmployeeListItem>> {

    private static final String LOG_TAG = GroupActivity.class.getName();

    /**
     * Constant value for the group item loader ID. We can choose any integer.
     * This really only comes into play if multiple loaders are used.
     */
    private static final int GROUP_LOADER_ID = 1;

    /** Adapter for the list of groups. */
    private EmployeeAdapter adapter;

    /** TextView that is displayed when the list is empty. */
    private TextView emptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_employees, contentFrameLayout);

        ListView employeeListView = (ListView) findViewById(R.id.employees_list);

        emptyStateTextView = (TextView) findViewById(R.id.employees_empty_view);
        employeeListView.setEmptyView(emptyStateTextView);

        adapter = new EmployeeAdapter(this, new ArrayList<>());
        employeeListView.setAdapter(adapter);

        employeeListView.setOnItemClickListener((adapterView, view, position, l) -> {
            EmployeeListItem clickedEmployee = adapter.getItem(position);

            // Create intent and switch to map activity
        });

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(GROUP_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.employees_loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            emptyStateTextView.setText("No internet connection.");
        }
    }

    @Override
    public Loader<List<EmployeeListItem>> onCreateLoader(int id, Bundle args) {
        return new EmployeeLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<EmployeeListItem>> loader, List<EmployeeListItem> employeeListItems) {
        View loadingIndicator = findViewById(R.id.employees_loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        emptyStateTextView.setText("You don't know anyone.");

        adapter.clear();

        if (employeeListItems != null && !employeeListItems.isEmpty()) {
            adapter.addAll(employeeListItems);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<EmployeeListItem>> loader) {
        adapter.clear();
    }
}
