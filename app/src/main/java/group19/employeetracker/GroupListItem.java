package group19.employeetracker;

import java.util.List;

/**
 * Created by kylemart on 11/27/17.
 */
public class GroupListItem {

    private String id;

    private String name;

    private String size;

    public GroupListItem(String id, String name, String size) {
        this.id = id;
        this.name = name;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }
}
