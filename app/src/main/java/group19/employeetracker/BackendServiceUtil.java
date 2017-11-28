package group19.employeetracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class BackendServiceUtil {

    /** Tag used for identifying log messages from object of this class. */
    private static final String LOG_TAG = BackendServiceUtil.class.getSimpleName();

    /** Base URL of the backend service. */
    private static final String BASE_URL = "https://tracker.osyr.is";

    /** HTTP client used for issuing requests to the backend service. */
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();

    /** MediaType used for creating JSON request bodies. */
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private BackendServiceUtil() {
    }

    /**
     * Gets a JSON response object from the backend service with an authorization token.
     * @param route the route to get data from
     * @param auth the authorization token
     * @return a JSON response object
     */
    public static JSONObject get(String route, String auth) {
        Request request = new Request.Builder()
                .url(createUrl(route))
                .get()
                .header("Authorization", auth)
                .build();

        return executeRequest(request);
    }

    public static JSONObject get(String route) {
        return get(route, "");
    }

    public static JSONObject post(String route, JSONObject payload, String auth) {
        RequestBody requestBody = RequestBody.create(JSON, payload.toString());

        Request request = new Request.Builder()
                .url(createUrl(route))
                .header("Authorization", auth)
                .post(requestBody)
                .build();

        return executeRequest(request);
    }

    public static JSONObject post(String route, JSONObject payload) {
        return post(route, payload, "");
    }

    private static JSONObject executeRequest(Request request) {
        try {
            Response response = HTTP_CLIENT.newCall(request).execute();
            String json = response.body().string();
            return new JSONObject(json);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem performing " + request.method() + " request", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing request", e);
        }
        return new JSONObject();
    }

    /**
     * Creates a url to be used for building a request.
     *
     * @param route the route being hit
     * @return the absolute url of the route
     */
    private static URL createUrl(String route) {
        URL url = null;
        try {
            url = new URL(BASE_URL + "/" + route);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL", e);
        }
        return url;
    }
}
