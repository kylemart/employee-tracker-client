package group19.employeetracker;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUp extends AppCompatActivity
{
    private static final String LOG_TAG = LogIn.class.getSimpleName();

    User user;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_sign_up);

        final EditText etfirstName = (EditText)findViewById(R.id.etfirstName);
        final EditText etlastName = (EditText)findViewById(R.id.etlastName);
        final EditText etemail = (EditText)findViewById(R.id.etEmail);
        final EditText etpass1 = (EditText)findViewById(R.id.etpass1);
        final EditText etpass2 = (EditText)findViewById(R.id.etpass2);
        final Button bcreateaccount = (Button) findViewById(R.id.bcreateaccount);

        bcreateaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass1 = etpass1.getText().toString();
                String pass2 = etpass2.getText().toString();
                if (pass1.equals(pass2)) {
                    JSONObject payload = new JSONObject();
                    try {
                        payload
                            .put("email", etemail.getText().toString())
                            .put("password", pass1)
                            .put("first_name", etfirstName.getText().toString())
                            .put("last_name", etlastName.getText().toString());
                    } catch (JSONException e) {
                        Log.d(LOG_TAG, "JSONException", e);
                    }

                    new AsyncTask<JSONObject, Void, JSONObject>() {
                        protected JSONObject doInBackground(JSONObject[] params) {
                            return BackendServiceUtil.post("signup", params[0]);
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

                                    SignUp.this.startActivity(
                                            new Intent(SignUp.this, LogIn.class));
                                }
                            } else {
                                Toast.makeText(
                                    getApplicationContext(),
                                    response.optString("message", "Invalid signup :("),
                                    Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                    }.execute(payload);
                }
            }
        });
    }
}