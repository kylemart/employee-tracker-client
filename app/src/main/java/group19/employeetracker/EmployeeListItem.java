package group19.employeetracker;

/**
 * Created by kylemart on 11/28/17.
 */

public class EmployeeListItem {

    private String firstName;

    private String lastName;

    public EmployeeListItem(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
