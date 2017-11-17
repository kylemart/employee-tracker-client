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
    private TextView firstNameView, lastNameView, employeeIDView, privacyView, activeView;
    private ImageView profilePicView;
    private Button profilePicButton;

    boolean connectionSuccessful;
    private String firstName, lastName, email;
    //private int employeeID;
    private boolean publicEmployee;
    private boolean active;
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

        employee = getIntent().getParcelableExtra("employee");

        initData();

        initUI();
    }

    /**
     * Sets the data for the employee if one exists.
     * @author John Sermarini
     */
    private void initData()
    {
        if(employee == null)
        {
            firstName = "FirstName";
            lastName = "LastName";
            //employeeID = -1;
            publicEmployee = true;
            email = "Email";
            active = false;
            profilePic = null;
        }
        else
        {
            firstName = employee.firstName;
            lastName = employee.lastName;
            //employeeID
            publicEmployee = employee.getVisibility();
            //email
            active = employee.active;
            profilePic = employee.pic;
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
        //employeeIDView = (TextView) findViewById(R.id.employeeIDView);
        privacyView = (TextView) findViewById(R.id.privacyView);
        activeView = (TextView) findViewById(R.id.activeView);
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
        //employeeIDView.setText(Integer.toString(employeeID));
        if(publicEmployee)
        {
            privacyView.setText("Public Account");
        }
        else
        {
            privacyView.setText("Private Account");
        }
        if(active)
        {
            activeView.setText("Active");
        }
        else
        {
            activeView.setText("Inactive");
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

        setEmployeePic(profilePic);
    }

    /**
     * Updates the employees picture server side.
     * @param newPic the picture to send back
     */
    private void setEmployeePic(Bitmap newPic)
    {
        employee.pic = newPic;

        // TODO: update employee on database
    }
}
