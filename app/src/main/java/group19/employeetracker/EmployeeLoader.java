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
 * Created by kylemart on 11/28/17.
 */

public class EmployeeLoader extends AsyncTaskLoader<List<EmployeeListItem>> {

    private static final String LOG_TAG = GroupLoader.class.getName();

    public EmployeeLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<EmployeeListItem> loadInBackground() {
        String auth = PrefUtil.getAuth(getContext());

        JSONObject body = new JSONObject();

        try {
            JSONObject user = BackendServiceUtil.get("user/me", auth);
            JSONArray groups = user.getJSONObject("result").getJSONArray("groups");
            JSONArray groupIds = new JSONArray();
            for (int i = 0; i < groups.length(); ++i) {
                JSONObject group = groups.getJSONObject(i);
                int id = group.getInt("id");
                groupIds.put(id);
            }
            body.put("ids", groupIds);
        } catch (JSONException e) {

        }

        JSONObject employees = BackendServiceUtil.post("group/many", body, auth);

        return extractEmployeesFromResponse(employees);
    }

    private static List<EmployeeListItem> extractEmployeesFromResponse(JSONObject response) {
        List<EmployeeListItem> employeeListItems = new ArrayList<>();

        try {
            JSONArray employeeArray = response.getJSONArray("result");

            for (int i = 0; i < employeeArray.length(); ++i) {
                JSONObject employee = employeeArray.getJSONObject(i);
                String firstName = employee.getString("first_name");
                String lastName = employee.getString("last_name");
                employeeListItems.add(new EmployeeListItem(firstName, lastName));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing groups from result", e);
        }

        return employeeListItems;
    }
}
