package group19.employeetracker;

import android.content.Context;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LogIn extends AppCompatActivity
{
    User user;
    Context ctx;

    Intent serviceIntent;
    BackgroundGPS mService;

    @Override
    //XML is loaded into a View resource
    //Loading the layout resource
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ctx = this;
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
                String email = etusername.getText().toString();
                String password = etpassword.getText().toString();

                JSONObject jsonObj = new JSONObject();
                try {
                    jsonObj.put("email", email);
                    jsonObj.put("password", password);
                } catch (JSONException e) {}

                JSONObject response = BackendServiceUtil.post(ctx, "login", false, jsonObj);

                if (response.optBoolean("success")) {
                    String token = response.optString("token");
                    if (token != null && token.length() > 0) {
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("token", token);
                    }
                } else {
                    String errorMsg = response.optString("message");
                    if (errorMsg.length() == 0) {
                        errorMsg = "Invalid login";
                    }
                    new Toast(getApplicationContext()).makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     *
     * @author John Sermarini
     */
    private void logIn()
    {
        if(!checkData())
        {

            return;
        }

        // User is employee
        if(!user.type)
        {
            Intent i = new Intent(LogIn.this, Camera.class);
            i.putExtra("user", user);

            LogIn.this.startActivity(i);
        }
        // User is boss
        else
        {
            Intent i = new Intent(LogIn.this, NavActivity.class);
            i.putExtra("user", user);

            LogIn.this.startActivity(i);
        }

    }

    /**
     *
     * @return false if employee used invalid login, true if else
     * @author John Sermarini
     */
    private boolean checkData()
    {
        //TODO: Retrieve user information. If invalid return false;

        user = new User(false, "John", "Smith", "JohnSmith@gmail.com");

        return true;
    }

    protected void onDestroy() {
        super.onDestroy();

        /*if(!mService.isRunning())
            stopService(serviceIntent);*/
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            BackgroundGPS.LocalBinder binder = (BackgroundGPS.LocalBinder) service;
            mService = binder.getService();

            if(mService.isRunning()) {
                Intent intent = new Intent(LogIn.this, NavActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }

            unbindService(mConnection);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };
}
