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

import com.google.android.gms.maps.model.LatLng;

import java.util.HashSet;

public class Profile extends AppCompatActivity
{
    private TextView fullNameView, privacyView;
    private ImageView profilePicView;
    private Button profileButton;

    private String firstName, lastName;
    private boolean publicEmployee;
    private Bitmap profilePic;

    User user;
    Employee employee;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initData();

        initUI();
    }

    /**
     * Sets the data for the employee if one exists.
     * @author John Sermarini
     */
    private void initData()
    {
        user = getIntent().getParcelableExtra("user");
        employee = getIntent().getParcelableExtra("employee");

        if(employee == null)
        {
            firstName = "First";
            lastName = "Last";
            publicEmployee = true;
            profilePic = null;
        }
        else
        {
            firstName = employee.firstName;
            lastName = employee.lastName;
            publicEmployee = employee.getVisibility();
            profilePic = employee.pic;
        }

    }

    /**
     * Initializes the UI and sets the text to the data retrieved from the database.
     * @author John Sermarini
     */
    private void initUI()
    {
        fullNameView = (TextView) findViewById(R.id.fullNameView);
        privacyView = (TextView) findViewById(R.id.privacyView);
        profilePicView = (ImageView) findViewById(R.id.profilePicView);
        initProfileButton();

        fullNameView.setText(firstName + " " + lastName);
        if(publicEmployee)
        {
            privacyView.setText("Public");
        }
        else
        {
            privacyView.setText("Private");
        }
        if(employee == null)
        {
            profilePicView.setImageResource(android.R.color.transparent);
        }
        else
        {
            profilePicView.setImageBitmap(profilePic);
        }
    }

    /**
     * If employee, sets the profile button to change pic, if not sets button to change employee privacy setting.
     * @author John Sermarini
     */
    private void initProfileButton()
    {
        profileButton = (Button) findViewById(R.id.profileButton);
        final boolean isBoss = user.type;

        if(!isBoss)
        {
            profileButton.setText("Change Profile Picture");
        }
        else
        {
            profileButton.setText("Change Privacy Setting");
        }

        profileButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!isBoss)
                {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePictureIntent, 0);
                }
                else
                {
                    if(!employee.getVisibility())
                    {
                        privacyView.setText("Public");
                        employee.setVisibility(true);
                    }
                    else
                    {
                        privacyView.setText("Private");
                        employee.setVisibility(false);
                    }

                    //TODO: Send active status to database
                }
            }
        });
    }

    /**
     * Updates the employees picture server side.
     * @param newPic the picture to send back
     */
    private void setEmployeePic(Bitmap newPic)
    {
        employee.pic = newPic;

        // TODO: Update employee picture on database
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
}
