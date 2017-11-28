package group19.employeetracker;

import android.content.Context;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class LogIn extends AppCompatActivity
{
    private static final String LOG_TAG = LogIn.class.getSimpleName();

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

        // DEBUG
        etusername.setText("test@example.com");
        etpassword.setText("hunter2");
        // DEBUG

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
                JSONObject payload = new JSONObject();
                try {
                    payload
                        .put("email", etusername.getText().toString())
                        .put("password", etpassword.getText().toString());
                } catch (JSONException e) {
                    Log.d(LOG_TAG, "JSONException", e);
                }

                new AsyncTask<JSONObject, Void, JSONObject>() {
                    protected JSONObject doInBackground(JSONObject[] params) {
                        return BackendServiceUtil.post("route", params[0], PrefUtil.getAuth(ctx));
                    }
                    protected void onPostExecute(JSONObject response) {
                        if (response.optBoolean("success")) {
                            String token = response.optString("token");
                            if (token != null && token.length() > 0) {
                                getApplicationContext()
                                    .getSharedPreferences("User", MODE_PRIVATE)
                                    .edit()
                                    .putString("token", token)
                                    .apply();

                                user = new User(
                                    response.optBoolean("isAdmin"),
                                    response.optString("first_name"),
                                    response.optString("last_name"),
                                    response.optString("email")
                                );

                                Log.d("DEBUG", "firstName: " + user.firstName);
                                Log.d("DEBUG", "lastName: " + user.lastName);
                                Log.d("DEBUG", "email: " + user.email);
                                Log.d("DEBUG", "isAdmin: " + String.valueOf(user.isAdmin));

                                if (user.isAdmin) {
                                    LogIn.this.startActivity(
                                        new Intent(LogIn.this, NavActivity.class)
                                            .putExtra("user", user)
                                    );
                                } else {
                                    LogIn.this.startActivity(
                                        new Intent(LogIn.this, Camera.class)
                                            .putExtra("user", user)
                                    );
                                }
                            }
                        } else {
                            new Toast(getApplicationContext())
                                .makeText(
                                    getApplicationContext(),
                                    response.optString("message", "Invalid login"),
                                    Toast.LENGTH_LONG
                                )
                                .show();
                        }
                    }
                }.execute(payload);
            }
        });
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
