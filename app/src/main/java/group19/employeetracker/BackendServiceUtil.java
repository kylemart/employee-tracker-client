package group19.employeetracker;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class BackendServiceUtil {

    /** Tag used for identifying log messages from object of this class. */
    private static final String LOG_TAG = BackendServiceUtil.class.getSimpleName();

    /** Base URL of the backend service. */
    private static final String BASE_URL = "http://192.168.43.165";

    /** HTTP client used for issuing requests to the backend service. */
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();

    /** MediaType used for creating JSON request bodies. */
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private BackendServiceUtil() {
    }

    /**
     * Gets data from the backend service at the specified route.
     *
     * @param route the backend service route to target
     * @return the response from the backend service
     * @throws IOException if there was a problem extracting
     */
    public static String get(String route) throws IOException {
        Request request = new Request.Builder()
                .url(createUrl(route))
                .get()
                .build();

        Response response = executeRequest(request);

        return response.body().string();
    }

    /**
     * Posts a JSON body to the backend service at the specified route.
     *
     * @param route the backend service route to target
     * @param json the JSON to post to the backend service
     * @return the response from the backend service
     * @throws IOException if there was a problem handling the request
     */
    public static String post(String route, JSONObject json) throws IOException {
        RequestBody requestBody = RequestBody.create(JSON, json.toString());

        Request request = new Request.Builder()
                .url(createUrl(route))
                .post(requestBody)
                .build();

        Response response = null;

        try {
            response = executeRequest(request);
        } catch (IOException e) {
            Log.e(LOG_TAG,"Problem performing POST request", e);
        }

        return response.body().string();
    }

    /**
     * Executes an HTTP request.
     *
     * @param request the request to execute
     * @return the response
     */
    private static Response executeRequest(Request request) throws IOException {
        try {
            return HTTP_CLIENT.newCall(request).execute();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem executing request", e);
            return null;
        }
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
