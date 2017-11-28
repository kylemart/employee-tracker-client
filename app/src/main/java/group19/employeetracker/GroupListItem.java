package group19.employeetracker;

import java.util.List;

/**
 * Created by kylemart on 11/27/17.
 */
public class GroupListItem {

    private int id;

    private String name;

    private int size;

    public GroupListItem(int id, String name, int size) {
        this.id = id;
        this.name = name;
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }
}
