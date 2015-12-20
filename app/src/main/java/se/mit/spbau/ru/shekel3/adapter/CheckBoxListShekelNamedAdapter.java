package se.mit.spbau.ru.shekel3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import se.mit.spbau.ru.shekel3.R;
import se.mit.spbau.ru.shekel3.model.ShekelBaseEntity;

/**
 * Created by John on 11/24/2015.
 */
public class CheckBoxListShekelNamedAdapter extends ArrayAdapter<ShekelBaseEntity> {

    private Set<Integer> selected = new HashSet<>();
    private List<ShekelBaseEntity> items2;


    public CheckBoxListShekelNamedAdapter(Context context, List<ShekelBaseEntity> items1, Set<Integer> selectedItems) {
        super(context, R.layout.check_list, items1);
        items2 = new ArrayList<>(items1);
        selected = selectedItems;
    }

    public Set<Integer> getSelected() {
        return selected;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ShekelBaseEntity item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.check_list, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);

            if (selected.contains(item.getId())) {
                viewHolder.name.setChecked(true);
            }

            convertView.setTag(viewHolder);

            viewHolder.name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox) v;
                    ShekelBaseEntity item = (ShekelBaseEntity) v.getTag();
                    if (checkBox.isChecked()) {
                        selected.add(item.getId());
                    } else {
                        selected.remove(item.getId());
                    }
                }
            });
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.name.setText(item.getName());
        viewHolder.name.setTag(item);
        // Return the completed view to render on screen
        return convertView;
    }

    private static class ViewHolder {
        CheckBox name;
    }
}


