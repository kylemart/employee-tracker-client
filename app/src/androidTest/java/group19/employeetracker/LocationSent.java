package group19.employeetracker;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeoutException;

import static android.os.SystemClock.sleep;
import static org.junit.Assert.*;

public class LocationSent {
    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();

    BackgroundGPS mService;

    @Test
    public void location() throws TimeoutException {
        Intent serviceIntent = new Intent(InstrumentationRegistry.getTargetContext(), BackgroundGPS.class);
        mServiceRule.startService(serviceIntent);
        IBinder binder = mServiceRule.bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);

        // wait until a little after ten minutes
        sleep(600500);

        assertNotEquals(mService.getLocation(), new LatLng(0,0));
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            BackgroundGPS.LocalBinder binder = (BackgroundGPS.LocalBinder) service;
            mService = binder.getService();
            mService.start();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };
}
