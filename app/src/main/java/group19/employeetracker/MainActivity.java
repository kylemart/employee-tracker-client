package group19.employeetracker;

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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        debugInit();
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
    }
}
