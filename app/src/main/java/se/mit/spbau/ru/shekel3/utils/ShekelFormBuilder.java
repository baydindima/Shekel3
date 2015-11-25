package se.mit.spbau.ru.shekel3.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.Set;

import se.mit.spbau.ru.shekel3.adapter.CheckBoxListShekelNamedAdapter;
import se.mit.spbau.ru.shekel3.model.ShekelBaseEntity;

/**
 * Created by John on 11/25/2015.
 */
public class ShekelFormBuilder {

    public static LinearLayout.LayoutParams getDefaultParams(boolean isLabel) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        if (isLabel) {
            params.bottomMargin = 5;
            params.topMargin = 10;
        }
        return params;
    }

    public static void addCheckBoxListView(LinearLayout form, Context context, ListView listView, List<ShekelBaseEntity> elements, Set<Integer> selectedId) {
        CheckBoxListShekelNamedAdapter adapter = new CheckBoxListShekelNamedAdapter(context, elements, selectedId);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("ShekelItemEditFragment", "Position " + position);
            }
        });
        form.addView(listView);
    }

    public static void addFormField(LinearLayout form, Activity activity, EditText editText, String text, String label, int type) {
        TextView tvLabel = new TextView(activity);
        tvLabel.setLayoutParams(getDefaultParams(true));
        form.addView(tvLabel);
        tvLabel.setText(label);

        editText.setLayoutParams(getDefaultParams(false));
        editText.setInputType(type);
        editText.setText(text);
        form.addView(editText);
    }

    public static void addButton(LinearLayout form, Activity activity, int textId, View.OnClickListener onClickListener) {
        Button button = new Button(activity);
        button.setLayoutParams(getDefaultParams(false));
        button.setText(textId);
        button.setOnClickListener(onClickListener);
        form.addView(button);
    }

}
