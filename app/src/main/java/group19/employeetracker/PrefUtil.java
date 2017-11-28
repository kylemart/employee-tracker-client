package group19.employeetracker;

import android.content.Context;

import static android.content.Context.MODE_PRIVATE;

public class PrefUtil {
    
    public static String getAuth(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences("User", MODE_PRIVATE)
                .getString("token", "");
    }
}
