package group19.employeetracker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        debugInit();

        if(isLoggedIn()) {
            Intent toMap = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(toMap);
        }
    }

    // TODO: Use this in final login page to skip logging in if already logged in
    private boolean isLoggedIn() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
            if (BackgroundGPS.class.getName().equals(service.service.getClassName()))
                return true;

        return false;
    }

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
                Intent toProfile = new Intent(MainActivity.this, Profile.class);
                startActivity(toProfile);
            }
        });

        debugMapButton = (Button) findViewById(R.id.debugMapButton);
        debugMapButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent toMap = new Intent(MainActivity.this, MapsActivity.class);
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
    }
}
