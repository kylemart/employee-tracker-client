package group19.employeetracker;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import static java.lang.Thread.sleep;

public class BackgroundGPS extends Service {
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
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        // So this service runs in parallel to the app

        Thread t = new Thread("BackgroundGPS(" + startId + ")") {
            @Override
            public void run() {
                actuallyStart(intent, flags, startId);
            }
        };
        t.start();

        return START_STICKY;
    }

    public void actuallyStart(Intent intent, int flags, int startId) {
        // Guide on services: developer.android.com/guide/components/services.html

        // TODO: Write a getLocation() method
        // It should probably be dissimilar from MapsActivity.getUserCoords()
        // because that relies on someone else getting the location. On a 10 minute
        // update perhaps no one else gets the location?
        // TODO: Add some sort of timer or something that calls getLocation()
        // I don't think sleep() like below is good for this. Need something better
        // TODO: Maybe use this service to keep the user logged in to the app even after they close it
        // Only when a user logs out should this task die
        // This part should be discussed on merge of code
        // This is not required, and probably hard to implement
        // TODO: When DatabaseIO is made, use the method that uploads the location
        // This way the background service doesn't have to worry about who is logged in

        try {
            sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
