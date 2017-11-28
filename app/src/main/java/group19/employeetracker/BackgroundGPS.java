package group19.employeetracker;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class BackgroundGPS extends Service {
    private static final String LOG_TAG = BackgroundGPS.class.getSimpleName();

    Context ctx;

    private boolean running = false;

    private final IBinder mBinder = new LocalBinder();

    private final long ONE_SECOND = 1000;
    private final long TEN_SECONDS = ONE_SECOND*10;


    FusedLocationProviderClient mFusedLocationClient;
    LocationCallback mLocationCallback;

    LatLng currentLoc =  new LatLng(0, 0);

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        return START_STICKY;
    }

    // TODO: When DatabaseIO is made, use the method that uploads the location
    // This way the background service doesn't have to worry about who is logged in

    private void sendLocation(double lat, double lng) {
        currentLoc = new LatLng(lat, lng);

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

    public LatLng getLocation() {
        return currentLoc;
    }

    @Override
    public void onDestroy() {
        if(isRunning())
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public BackgroundGPS getService() {
            return BackgroundGPS.this;
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void start() {
        if(!running) {
            running = true;

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

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(TEN_SECONDS);
        mLocationRequest.setMaxWaitTime(TEN_SECONDS);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }
}
