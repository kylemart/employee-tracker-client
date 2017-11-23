package group19.employeetracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class SignUp extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final EditText etname = (EditText)findViewById(R.id.etfirstName);
        final EditText etemail = (EditText)findViewById(R.id.etlastName);
        final EditText etusername = (EditText)findViewById(R.id.etEmail);
        final EditText etpass1 = (EditText)findViewById(R.id.etpass1);
        final EditText etpass2 = (EditText)findViewById(R.id.etpass2);
        final Button bcreateaccount = (Button) findViewById(R.id.bcreateaccount);

    }
}