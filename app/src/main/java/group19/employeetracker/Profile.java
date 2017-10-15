package group19.employeetracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class Profile extends AppCompatActivity
{
    private TextView firstNameView, lastNameView, employeeIDView, privacyView;
    private Button nameButton;

    boolean connectionSuccessful;
    private String firstName, lastName;
    private int employeeID;
    private boolean publicEmployee;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
    }
}
