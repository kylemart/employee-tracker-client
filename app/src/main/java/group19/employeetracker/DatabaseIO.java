package group19.employeetracker;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;

import java.util.HashSet;

public class DatabaseIO extends AppCompatActivity {
    String url = "https://tracker.osyr.is";
    RequestQueue requestQueue;

    public DatabaseIO(Context mContext) {
        Cache cache = new DiskBasedCache(mContext.getCacheDir(), 512 * 512);
        Network network = new BasicNetwork(new HurlStack());

        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
    }

    public HashSet<Employee> getEmployees() {
        HashSet<Employee> employees = new HashSet<>();

        JsonObjectRequest jr = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                Log.d("DATABASEIO", response.getString("message"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // TODO: handle error
        });

        requestQueue.add(jr);

        return employees;
    }
}
