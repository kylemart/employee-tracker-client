package group19.employeetracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class Camera extends AppCompatActivity
{
    private int employeeID;
    private boolean pictureTaken;

    private ImageView pictureView;
    private Button takePictureButton;
    private Button toMapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        employeeID = 123456789;
        pictureTaken = false;

        pictureView = (ImageView) findViewById(R.id.pictureView);

        takePictureButton = (Button) findViewById(R.id.toCameraAction);
        takePictureButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, 0);
            }
        });

        toMapButton = (Button) findViewById(R.id.toMap);
        toMapButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                takePicture();
            }
        });
    }

    /**
     * Takes the user's picture.
     * @author John Sermarini
     */
    private void takePicture()
    {
        if(pictureTaken == true)
        {
            sendPictureToDatabase();

            Intent toOptions = new Intent(Camera.this, MapsActivity.class);
            startActivity(toOptions);
        }
        else
        {
            sendFailed();
        }
    }

    /**
     * Sends the picture taken to the database.
     * @author John Sermarini
     */
    private void sendPictureToDatabase()
    {
        pictureTaken = false;

        pictureView.setImageResource(android.R.color.transparent);

        // TODO: Send picture to database
    }

    /**
     * Alerts the user that they cannot continue until a picture is taken and sent to the database.
     * @author John Sermarini
     */
    private void sendFailed()
    {
        new Toast(getApplicationContext()).makeText(getApplicationContext(), "To continue, you must take a picture to send.", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");

        if(bitmap != null)
        {
            pictureView.setImageBitmap(bitmap);
        }

        pictureTaken = true;
    }
}
