package group19.employeetracker;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by kylemart on 11/28/17.
 */

public class EmployeeAdapter extends ArrayAdapter<EmployeeListItem> {

    Context ctx;

    public EmployeeAdapter(Context context, List<EmployeeListItem> employeeListItems) {
        super(context, 0, employeeListItems);

        ctx = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_employees, parent, false);
        }

        EmployeeListItem currentEmployee = getItem(position);

        if(currentEmployee.isHighlighted())
            listItemView.setBackgroundColor(Color.parseColor("#8585D0"));
        else
            listItemView.setBackgroundColor(Color.parseColor("#FFFFFF"));

        TextView groupName = (TextView) listItemView.findViewById(R.id.employee_name);
        groupName.setText(currentEmployee.getFullName());

        return listItemView;
    }
}
