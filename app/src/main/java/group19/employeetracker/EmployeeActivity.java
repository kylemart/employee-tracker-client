package group19.employeetracker;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
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

public class EmployeeActivity extends NavActivity implements LoaderManager.LoaderCallbacks<List<EmployeeListItem>> {
    boolean refreshVisibility = true;

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

    private List<EmployeeListItem> employeeListItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_employees, contentFrameLayout);

        getEmployees();
    }

    private void getEmployees() {
        ListView employeeListView = (ListView) findViewById(R.id.employees_list);

        emptyStateTextView = (TextView) findViewById(R.id.employees_empty_view);
        employeeListView.setEmptyView(emptyStateTextView);

        adapter = new EmployeeAdapter(this, new ArrayList<>());
        employeeListView.setAdapter(adapter);

        employeeListView.setOnItemClickListener((adapterView, view, position, l) -> {
            EmployeeListItem clickedEmployee = adapter.getItem(position);

            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("group", false);
            intent.putExtra("id", clickedEmployee.getId());
            startActivity(intent);
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
            this.employeeListItems = employeeListItems;
        }
    }

    @Override
    public void onLoaderReset(Loader<List<EmployeeListItem>> loader) {
        adapter.clear();
    }

    public ArrayList<EmployeeListItem> search(String query, List<EmployeeListItem> employeeListItems) {
        HashSet<EmployeeListItem> filtered = new HashSet<>();

        if(query.isEmpty() || employeeListItems == null)
            return new ArrayList<>(filtered);

        employeeListItems.forEach(employee -> {
            if(employee.getFullName().toLowerCase().startsWith(query.toLowerCase())
                    || employee.getLastName().toLowerCase().startsWith(query.toLowerCase())) {
                employee.highlight(true);
                filtered.add(employee);
            }
        });

        ArrayList<EmployeeListItem> filteredList = new ArrayList<>(filtered);

        employeeListItems.forEach(employee -> {
            if(!filtered.contains(employee)) {
                employee.highlight(false);
                filteredList.add(employee);
            }
        });

        return filteredList;
    }

    private void searchFor(String str) {
        if(str.isEmpty()) {
            employeeListItems.forEach(employee -> employee.highlight(false));

            adapter.clear();
            adapter.addAll(employeeListItems);
        } else {
            adapter.clear();
            adapter.addAll(search(str, employeeListItems));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_nav, menu);

        MenuItem search = menu.findItem(R.id.action_search);
        search.setVisible(true);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setMaxWidth(4000);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFor(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchFor(newText);
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(search, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                refreshVisibility = !refreshVisibility;
                invalidateOptionsMenu();
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
        });

        menu.findItem(R.id.action_refresh).setVisible(refreshVisibility);

        if(!refreshVisibility)
            search.expandActionView();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_refresh) {
            adapter.clear();
            getEmployees();
        } else if(id == R.id.action_search) {
            refreshVisibility = !refreshVisibility;
            invalidateOptionsMenu();
        }

        return true;
    }
}
