package group19.employeetracker;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kylemart on 11/27/17.
 */

public class GroupLoader extends AsyncTaskLoader<List<GroupListItem>> {

    private static final String LOG_TAG = GroupLoader.class.getName();

    public GroupLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<GroupListItem> loadInBackground() {
        String auth = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0aW1lIjoxNTExODQ3MDIwLCJpZCI6MX0.z8Eqyvgtqg6OKTVp_bPr4_If4xJvClKXja1EG89fE9g"; // PrefUtil.getAuth(getContext());
        JSONObject response = BackendServiceUtil.get("user/me", auth);
        return extractGroupsFromResponse(response);
    }

    private static List<GroupListItem> extractGroupsFromResponse(JSONObject response) {
        List<GroupListItem> groupListItems = new ArrayList<>();

        try {
            JSONArray groupArray = response
                    .getJSONObject("result")
                    .getJSONArray("groups");

            for (int i = 0; i < groupArray.length(); ++i) {
                JSONObject group = groupArray.getJSONObject(i);
                String id = group.getString("id");
                String name = group.getString("name");
                String size = group.getString("size");

                groupListItems.add(new GroupListItem(id, name, size));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing groups from result", e);
        }

        return groupListItems;
    }
}
