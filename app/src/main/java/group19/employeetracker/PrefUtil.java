package group19.employeetracker;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class PrefUtil {
    
    public static String getAuth(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences("User", MODE_PRIVATE)
                .getString("token", "");
    }

    public static void deleteUser(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences("User", MODE_PRIVATE);

        pref.edit().clear().commit();
    }
}
