package group19.employeetracker;

import com.google.android.gms.maps.model.LatLng;
import org.junit.Test;
import static org.junit.Assert.*;

public class HideEmployee {
    @Test
    public void hide() throws Exception {
        User user = new User(true, "User", "Name", "user@domain.com");

        Employee employee = new Employee("Fred Flintstone", "Fred@email.com", "Alpha", new LatLng(28.604273, -81.200187), null);

        user.hideEmployee(employee);

        assertEquals(false, employee.getVisibility());
    }
}
