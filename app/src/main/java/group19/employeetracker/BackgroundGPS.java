package group19.employeetracker;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class BackgroundGPS extends Service {
    private final long TEN_MINUTES = 600000;

    FusedLocationProviderClient mFusedLocationClient;
    LocationCallback mLocationCallback;

    @Override
    public void onCreate() {
        // This creates a service that stays alive even after the app is closed
        Intent notificationIntent = new Intent(this, BackgroundGPS.class);
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
        mLocationRequest.setInterval(TEN_MINUTES);
        mLocationRequest.setMaxWaitTime(TEN_MINUTES);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);

        return START_STICKY;
    }

    // TODO: Maybe use this service to keep the user logged in to the app even after they close it
    // Only when a user logs out should this task die
    // This part should be discussed on merge of code
    // Can be implemented with this: https://stackoverflow.com/questions/600207/how-to-check-if-a-service-is-running-on-android
    // TODO: When DatabaseIO is made, use the method that uploads the location
    // This way the background service doesn't have to worry about who is logged in

    private void sendLocation(double lat, double lng) {
        System.out.println(lat + ", " + lng);
    }

    @Override
    public void onDestroy() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
