package group19.employeetracker;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Objects;

import static org.junit.Assert.*;

public class SearchTest {
    @Test
    public void search() throws Exception {
        MapsActivity map = new MapsActivity();

        ArrayList<Employee> employees = createEmployees();
        ArrayList<Employee> filtered;

        // Test nothing
        filtered = map.searchList("", employees);
        assertEquals(new ArrayList<Employee>(), filtered);

        // Test query not in list
        filtered = map.searchList("a", employees);
        assertEquals(new ArrayList<Employee>(), filtered);

        // Test query not in list
        filtered = map.searchList("-", employees);
        assertEquals(new ArrayList<Employee>(), filtered);

        // Test first name
        filtered = map.searchList("Fred", employees);
        assertEquals("Fred Flintstone", filtered.get(0).fullName);

        // Test last name
        filtered = map.searchList("Flintstone", employees);

        int count = 0;
        for(Employee employee : filtered)
            if(Objects.equals("Fred Flintstone", employee.fullName) || (Objects.equals("Wilma Flintstone", employee.fullName)) || (Objects.equals("Pebbles Flintstone", employee.fullName)))
                count++;

        assertEquals(3, count);

        // Test one letter, four hits, three objects
        filtered = map.searchList("f", employees);

        count = 0;
        for(Employee employee : filtered)
            if(Objects.equals("Fred Flintstone", employee.fullName) || (Objects.equals("Wilma Flintstone", employee.fullName)) || (Objects.equals("Pebbles Flintstone", employee.fullName)))
                count++;

        assertEquals(3, count);
    }

    private ArrayList<Employee> createEmployees() {
        ArrayList<Employee> employees = new ArrayList<>(6);

        String[] names = {"Fred Flintstone", "Wilma Flintstone", "Pebbles Flintstone",
                "Barney Rubble", "Betty Rubble", "Bamm-Bamm Rubble"};

        String[] emails = {"Fred@email.com", "Wilma@email.com", "Pebbles@email.com",
                "Barney@email.com", "Betty@email.com", "Bamm-Bamm@email.com"};

        String[] groups = {"Alpha,Delta,Sigma","Alpha","Sigma","Alpha,Theta","Delta","Alpha,Delta,Theta"};

        LatLng[] coords = {new LatLng(28.604273, -81.200187), new LatLng(28.603718, -81.200488),
                new LatLng(28.599837, -81.198138), new LatLng(28.601940, -81.200552),
                new LatLng(28.601947, -81.198342), new LatLng(28.601447, -81.198438)
        };

        for(int i = 0; i < names.length; i++) {
            employees.add(new Employee(names[i], emails[i], groups[i], coords[i], null));
        }

        return employees;
    }
}
