package group19.employeetracker;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity
{

    private Button debugCameraButton;
    private Button debugProfileButton;
    private Button debugMapButton;
    private Button debugDButton;
    private Button debugLoginButton;

    Intent serviceIntent;
    BackgroundGPS mService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        debugInit();

        serviceIntent = new Intent(this, BackgroundGPS.class);
        startService(serviceIntent);
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    protected void onDestroy() {
        super.onDestroy();

        if(!mService.isRunning())
            stopService(serviceIntent);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            BackgroundGPS.LocalBinder binder = (BackgroundGPS.LocalBinder) service;
            mService = binder.getService();

            if(mService.isRunning()) {
                Intent toMap = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(toMap);
            }

            unbindService(mConnection);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    /**
     * Initializes the buttons and intents necessary for testing and debugging.
     * @author John Sermarini
     */
    void debugInit()
    {
        debugCameraButton = (Button) findViewById(R.id.debugCameraButton);
        debugCameraButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent toCamera = new Intent(MainActivity.this, Camera.class);
                startActivity(toCamera);
            }
        });

        debugProfileButton = (Button) findViewById(R.id.debugProfileButton);
        debugProfileButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                User user = new User(false, "John Smith", "JohnSmith@average.com");
                Intent toProfile = new Intent(MainActivity.this, Profile.class);
                toProfile.putExtra("user", user);
                startActivity(toProfile);
            }
        });

        debugMapButton = (Button) findViewById(R.id.debugMapButton);
        debugMapButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                User user = new User(false, "John Smith", "JohnSmith@average.com");
                Intent toMap = new Intent(MainActivity.this, MapsActivity.class);
                toMap.putExtra("user", user);
                startActivity(toMap);
            }
        });

        debugDButton = (Button) findViewById(R.id.debugDButton);
        debugDButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent toD = new Intent(MainActivity.this, DatabaseIOTest.class);
                startActivity(toD);
            }
        });

        debugLoginButton = (Button) findViewById(R.id.debugLogInButton);
        debugLoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent toLogIn = new Intent(MainActivity.this, LogIn.class);
                startActivity(toLogIn);
            }
        });
    }
}
