package se.mit.spbau.ru.shekel3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import se.mit.spbau.ru.shekel3.R;
import se.mit.spbau.ru.shekel3.model.ShekelItem;

/**
 * Created by John on 11/13/2015.
 */
public class ListShekelItemAdapter extends ArrayAdapter<ShekelItem> {

    private static class ViewHolder {
        TextView name;
    }

    public ListShekelItemAdapter(Context context, List<ShekelItem> items1) {
        super(context, android.R.layout.simple_list_item_1, items1);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ShekelItem item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.name.setText(item.getName());
        // Return the completed view to render on screen
        return convertView;
    }
}
