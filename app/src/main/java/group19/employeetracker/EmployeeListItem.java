package group19.employeetracker;

/**
 * Created by kylemart on 11/28/17.
 */

public class EmployeeListItem {

    private String firstName;

    private String lastName;

    private boolean highlighted;

    public EmployeeListItem(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        highlighted = false;
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

    public void highlight(boolean val) {
        highlighted = val;
    }

    public boolean isHighlighted() {
        return highlighted;
    }
}
