package group19.employeetracker;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A service that runs in the background to get and upload the user's location to the server
 * @author ryantgraves
 * */
public class BackgroundGPS extends Service {
    private static final String LOG_TAG = BackgroundGPS.class.getSimpleName();

   private final IBinder mBinder = null;

    private final long ONE_SECOND = 1000;
    private final long TEN_SECONDS = ONE_SECOND*10;

    FusedLocationProviderClient mFusedLocationClient;
    LocationCallback mLocationCallback;

    Context ctx;

    @Override
    public void onCreate() {
        ctx = this;

        // This creates a service that stays alive even after the app is closed

        Intent notificationIntent = new Intent(this, GroupActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new Notification.Builder(this)
                .setContentTitle("Tracking Location")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();

                sendLocation(location.getLatitude(), location.getLongitude());
            }
        };
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(TEN_SECONDS);
        mLocationRequest.setMaxWaitTime(TEN_SECONDS);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);

        return START_STICKY;
    }

    /**
     * Sends the user's location to the server
     * @author talbotwhite
     * */
    private void sendLocation(double lat, double lng) {
        Log.d("BACKGROUNDGPS", lat + ", " + lng);

        JSONObject payload = new JSONObject();
        try {
            payload
                .put("lat", lat)
                .put("lng", lng);
        } catch (JSONException e) {
            Log.d(LOG_TAG, "JSONException", e);
        }

        Context ctx = this;

        new AsyncTask<JSONObject, Void, JSONObject>() {
            protected JSONObject doInBackground(JSONObject[] params) {
                return BackendServiceUtil.post("report", params[0], PrefUtil.getAuth(ctx));
            }
            protected void onPostExecute(JSONObject response) {
                if (response.optBoolean("success")) {
                } else {
                    Toast.makeText(
                        getApplicationContext(),
                        response.optString("message", "Could not send location! :("),
                        Toast.LENGTH_LONG
                    ).show();
                }
            }
        }.execute(payload);
    }

    @Override
    public void onDestroy() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        PrefUtil.deleteUser(ctx);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
