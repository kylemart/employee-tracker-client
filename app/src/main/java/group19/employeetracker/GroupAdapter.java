package group19.employeetracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by kylemart on 11/27/17.
 */

public class GroupAdapter extends ArrayAdapter<GroupListItem> {

    public GroupAdapter(Context context, List<GroupListItem> groupListItems) {
        super(context, 0, groupListItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_groups, parent, false);
        }

        GroupListItem currentGroup = getItem(position);

        TextView groupName = (TextView) listItemView.findViewById(R.id.group_name);
        groupName.setText(currentGroup.getName());

        TextView groupSize = (TextView) listItemView.findViewById(R.id.group_size);
        String groupSizeText = currentGroup.getSize() + " members";
        groupSize.setText(groupSizeText);

        return listItemView;
    }
}
