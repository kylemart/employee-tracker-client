package group19.employeetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LogIn extends AppCompatActivity {

    @Override
    //XML is loaded into a View resource
    //Loading the layout resource
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //Created instanceo of view object and captured in layout
        final EditText etusername = (EditText)findViewById(R.id.etEmail);
        final EditText etpassword = (EditText)findViewById(R.id.etpassword);

        final Button blogin = (Button) findViewById(R.id.blogin);
        final TextView signup = (TextView) findViewById(R.id.signup);

        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(LogIn.this, SignUp.class);
                LogIn.this.startActivity(i);
            }
        });

        blogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                logIn();
            }
        });
    }

    /**
     *
     * @author John Sermarini
     */
    private void logIn()
    {
        //TODO: Retrieve user information and attach to intent.

        Intent i = new Intent(LogIn.this, Camera.class);
        LogIn.this.startActivity(i);
    }
}
