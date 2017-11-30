package group19.employeetracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GroupActivity extends NavActivity
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

    private List<GroupListItem> groupListItems;

    private HashSet<Integer> groups = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_groups, contentFrameLayout);

        getGroups();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> sendToMap());
    }

    private void getGroups() {
        ListView groupListView = (ListView) findViewById(R.id.groups_list);

        emptyStateTextView = (TextView) findViewById(R.id.empty_view);
        groupListView.setEmptyView(emptyStateTextView);

        adapter = new GroupAdapter(this, new ArrayList<>());
        groupListView.setAdapter(adapter);

        groupListView.setOnItemClickListener((adapterView, view, position, l) -> {
            GroupListItem clickedGroupListItem = adapter.getItem(position);

            int groupId = clickedGroupListItem.getId();

            String strColor = String.format("#%06X", 0xFFFFFF & ((ColorDrawable)view.getBackground()).getColor());

            if(strColor.equals("#8585D0")) {
                view.setBackgroundColor(Color.parseColor("#FFFFFF"));

                groups.remove(groupId);

            } else {
                view.setBackgroundColor(Color.parseColor("#8585D0"));

                groups.add(groupId);
            }
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

    private void sendToMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("group", true);
        intent.putExtra("groups", groups);

        startActivity(intent);

        groups.clear();
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

            this.groupListItems = groupListItems;
        }
    }

    @Override
    public void onLoaderReset(Loader<List<GroupListItem>> loader) {
        adapter.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_refresh) {
            groups.clear();
            adapter.clear();
            getGroups();
        }

        return true;
    }
}
