package group19.employeetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity
{

    private Button debugCameraButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        debugInit();
    }

    /**
     * Initializes the buttons and intents necessary for testing and debugging.
     *
     */
    void debugInit()
    {
        debugCameraButton = (Button) findViewById(R.id.debugCameraButton);
        debugCameraButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent toOptions = new Intent(MainActivity.this, Camera.class);
                startActivity(toOptions);
            }
        });
    }
}
