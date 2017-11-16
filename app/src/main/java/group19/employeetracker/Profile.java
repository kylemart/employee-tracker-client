package group19.employeetracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Profile extends AppCompatActivity
{
    private TextView firstNameView, lastNameView, employeeIDView, privacyView;
    private ImageView profilePicView;
    private Button profilePicButton;

    boolean connectionSuccessful;
    private String firstName, lastName, email;
    private int employeeID;
    private boolean publicEmployee;
    private Bitmap profilePic;

    User user;
    Employee employee;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // TODO: Use this to find out if the user is admin or employee and add special admin buttons
        user = getIntent().getParcelableExtra("user");

        // TODO: Don't need to get stuff from server, here is the employee
        employee = getIntent().getParcelableExtra("employee");

        connectionSuccessful = retrieveData();

        initData(connectionSuccessful);

        initUI();
    }

    /**
     * Gathers the data from the database.
     * @return true if data was retrieved from the server, otherwise false
     * @author John Sermarini
     */
    private boolean retrieveData()
    {
        // Get the necessary data for the employee from the database

        return false;
    }

    /**
     * Initializes the data using the data from the database. If connection fails, use default data.
     * @param connectionSuccessful the server connection was successful
     * @author John Sermarini
     */
    private void initData(boolean connectionSuccessful)
    {
        if(!connectionSuccessful)
        {
            firstName = "FirstName";
            lastName = "LastName";
            employeeID = -1;
            publicEmployee = true;
            email = "Email";
            profilePicView = null;
        }
        else
        {
            // Uses the retrieved data
        }

    }

    /**
     * Initializes the UI and sets the text to the data retrieved from the database.
     * @author John Sermarini
     */
    private void initUI()
    {
        firstNameView = (TextView) findViewById(R.id.firstNameView);
        lastNameView = (TextView) findViewById(R.id.lastNameView);
        employeeIDView = (TextView) findViewById(R.id.employeeIDView);
        privacyView = (TextView) findViewById(R.id.privacyView);
        profilePicView = (ImageView) findViewById(R.id.profilePicView);
        profilePicButton = (Button) findViewById(R.id.profilePicButton);
        profilePicButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, 0);
            }
        });

        firstNameView.setText(firstName);
        lastNameView.setText(lastName);
        employeeIDView.setText(Integer.toString(employeeID));
        if(publicEmployee)
        {
            privacyView.setText("Public Account");
        }
        else
        {
            privacyView.setText("Private Account");
        }
        if(!connectionSuccessful)
        {
            profilePicView.setImageResource(android.R.color.transparent);
        }
        else
        {
            profilePicView.setImageBitmap(profilePic);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        profilePic = (Bitmap) data.getExtras().get("data");

        if(profilePic != null)
        {
            profilePicView.setImageBitmap(profilePic);
        }

        sendPicToBack(profilePic);
    }

    /**
     * Updates the employees picture server side.
     * @param newPic the picture to send back
     */
    private void sendPicToBack(Bitmap newPic)
    {
        // TO DO
    }
}
