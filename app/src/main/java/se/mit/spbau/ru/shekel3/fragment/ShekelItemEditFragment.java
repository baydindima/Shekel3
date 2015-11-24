package se.mit.spbau.ru.shekel3.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import se.mit.spbau.ru.shekel3.MainActivity;

public class ShekelItemEditFragment extends Fragment {
    private LinearLayout form;
    private static int sId;
    private int id() { return sId++;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ScrollView view = new ScrollView(getActivity());
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));

        form = new LinearLayout(getActivity());
        form.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        form.setOrientation(LinearLayout.VERTICAL);

        view.addView(form);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buildForm();
            }
        });
        return view;
    }

    private void buildForm() {
        addFormField("Name of Item", InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        addFormField("Cost of Item", InputType.TYPE_CLASS_NUMBER);

        Button buttonSave = new Button(getActivity());
        buttonSave.setLayoutParams(getDefaultParams(false));
        buttonSave.setText("Save");
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
                ((MainActivity) getActivity()).showList();
            }
        });
        form.addView(buttonSave);

        Button buttonCancel = new Button(getActivity());
        buttonCancel.setLayoutParams(getDefaultParams(false));
        buttonCancel.setText("Cancel");
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelChanges();
                ((MainActivity) getActivity()).showList();
            }
        });
        form.addView(buttonCancel);
    }

    private void saveChanges() {
        //// TODO: 11/14/2015 something
    }

    private void cancelChanges() {
        //// TODO: 11/14/2015 something
    }

    private void addFormField(String label, int type) {
        TextView tvLabel = new TextView(getActivity());
        tvLabel.setLayoutParams(getDefaultParams(true));
        form.addView(tvLabel);
        tvLabel.setText(label);

        EditText editView = new EditText(getActivity());
        editView.setLayoutParams(getDefaultParams(false));
        // setting an unique id is important in order to save the state
        // (content) of this view across screen configuration changes
        editView.setId(id());
        editView.setInputType(type);
        form.addView(editView);
    }

    private LinearLayout.LayoutParams getDefaultParams(boolean isLabel) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        if (isLabel) {
            params.bottomMargin = 5;
            params.topMargin = 10;
        }
        return params;
    }
}
