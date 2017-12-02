package group19.employeetracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class Camera extends AppCompatActivity
{
    private static final String LOG_TAG = Camera.class.getSimpleName();

    User user;
    Context ctx;

    private boolean pictureTaken;

    private ImageView pictureView;
    private Button takePictureButton;
    private Button toMapButton;
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_camera);

        user = User.getUser(getApplicationContext());

        pictureTaken = false;

        pictureView = (ImageView) findViewById(R.id.pictureView);
        takePictureButton = (Button) findViewById(R.id.toCameraAction);
        takePictureButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                takePicture();
            }
        });

        toMapButton = (Button) findViewById(R.id.toMap);
        toMapButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sendPicture();
            }
        });

        welcomeText = (TextView) findViewById(R.id.welcomeText);
        welcomeText.setText("Welcome " + user.firstName + " " + user.lastName);
    }

    /**
     * Takes the user's picture.
     * @author John Sermarini
     */
    public void takePicture()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, 0);
    }

    /**
     * Sends the user's picture and completes the activity.
     * @author John Sermarini
     */
    private void sendPicture()
    {
        if(pictureTaken == true)
        {
            sendSuccessful();
        }
        else
        {
            sendFailed();
        }
    }

    /**
     * The picture has been saved successfully.
     * @suthor John Sermarini
     */
    private void sendSuccessful()
    {
        pictureTaken = false;
        pictureView.setImageResource(android.R.color.transparent);
        toMap();
    }

    /**
     * Alerts the user that they cannot continue until a picture is taken and sent to the database.
     * @author John Sermarini
     */
    private void sendFailed()
    {
        new Toast(getApplicationContext()).makeText(getApplicationContext(), "Must take picture", Toast.LENGTH_LONG).show();
    }

    /**
     * Starts Map activity
     * @author John Sermarini
     */
    private void toMap()
    {
        Intent toOptions = new Intent(Camera.this, GroupActivity.class);
        startActivity(toOptions);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(data.getExtras().get("data") == null)
        {
            return;
        }
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");

        if(bitmap != null)
        {
            bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);

            JSONObject payload = new JSONObject();
            try {
                payload.put("data", Employee.encodePic(bitmap));
            } catch (JSONException e) {
                Log.d(LOG_TAG, "JSONException", e);
            }

            new AsyncTask<JSONObject, Void, JSONObject>() {
                protected JSONObject doInBackground(JSONObject[] params) {
                    return BackendServiceUtil.post("user/update/verify", params[0], PrefUtil.getAuth(ctx));
                }
                protected void onPostExecute(JSONObject response) {
                    if (response.optBoolean("success")) {

                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                response.optString("message", "Could not connect :("),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
            }.execute(payload);

            pictureView.setImageBitmap(bitmap);

            pictureTaken = true;
        }
    }
}
