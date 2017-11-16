package group19.employeetracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DatabaseIOTest extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_io);
    }

    protected void onResume() {
        super.onResume();

        DatabaseIO db = new DatabaseIO(getApplicationContext());

        db.getEmployees();
    }
}
