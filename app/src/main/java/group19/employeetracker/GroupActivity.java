package group19.employeetracker;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<GroupListItem>> {

    private static final String LOG_TAG = GroupActivity.class.getName();

    /**
     * Constant value for the group item loader ID. We can choose any integer.
     * This really only comes into play if multiple loaders are used.
     */
    private static final int GROUP_LOADER_ID = 1;

    /** Adapter for the list of groups. */
    private GroupAdapter adapter;

    /** TextView that is displayed when the list is empty. */
    private TextView emptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        ListView groupListView = (ListView) findViewById(R.id.groups_list);

        emptyStateTextView = (TextView) findViewById(R.id.empty_view);
        groupListView.setEmptyView(emptyStateTextView);

        adapter = new GroupAdapter(this, new ArrayList<>());
        groupListView.setAdapter(adapter);

        groupListView.setOnItemClickListener((adapterView, view, position, l) -> {
            GroupListItem clickedGroupListItem = adapter.getItem(position);

            Log.d(LOG_TAG, "Will eventually launch new activity!");

            int groupId = clickedGroupListItem.getId();

            // Create intent and switch to map activity
        });

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(GROUP_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            emptyStateTextView.setText("No internet connection.");
        }
    }

    @Override
    public Loader<List<GroupListItem>> onCreateLoader(int id, Bundle args) {
        return new GroupLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<GroupListItem>> loader, List<GroupListItem> groupListItems) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        emptyStateTextView.setText("You aren't in any groups.");

        adapter.clear();

        if (groupListItems != null && !groupListItems.isEmpty()) {
            adapter.addAll(groupListItems);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<GroupListItem>> loader) {
        adapter.clear();
    }
}
