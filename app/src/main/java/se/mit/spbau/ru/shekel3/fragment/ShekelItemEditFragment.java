package se.mit.spbau.ru.shekel3.fragment;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import se.mit.spbau.ru.shekel3.MainActivity;
import se.mit.spbau.ru.shekel3.R;
import se.mit.spbau.ru.shekel3.adapter.CheckBoxListShekelNamedAdapter;
import se.mit.spbau.ru.shekel3.adapter.ListShekelNamedAdapter;
import se.mit.spbau.ru.shekel3.model.ShekelItem;
import se.mit.spbau.ru.shekel3.model.ShekelBaseEntity;
import se.mit.spbau.ru.shekel3.model.ShekelUser;
import se.mit.spbau.ru.shekel3.utils.ShekelNetwork;

public class ShekelItemEditFragment extends Fragment {
    private ShekelItem item;
    private boolean isNew;
    private LinearLayout form;
    private MainActivity mainActivity;
    private Map<Integer, ShekelUser> users;

    private EditText nameEditText;
    private EditText costEditText;
    private ListView consumersList;

    public void setShekelItem(ShekelItem shekelItem) {
        item = shekelItem;
    }

    public void setIsNew(Boolean value) {
        isNew = value;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ScrollView view = new ScrollView(getActivity());
        mainActivity = (MainActivity) getActivity();
        users = mainActivity.getUsers(); //todo maybe error (race)

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
        nameEditText = new EditText(getActivity());
        costEditText = new EditText(getActivity());
        if (isNew) {
            addFormField(nameEditText, "", "Name of Item", InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
            addFormField(costEditText, "", "Cost of Item", InputType.TYPE_CLASS_NUMBER);
        } else {
            addFormField(nameEditText, item.getName(), "Name of Item", InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
            addFormField(costEditText, String.valueOf(item.getCost()), "Cost of Item", InputType.TYPE_CLASS_NUMBER);
        }

        List<ShekelBaseEntity> userList = new ArrayList<ShekelBaseEntity>(users.values());
        Set<Integer> selectedId = new HashSet<>();
        if (!isNew) {
            for (ShekelUser shekelUser : item.getConsumers()) {
                selectedId.add(shekelUser.getId());
            }
        }
        consumersList = new ListView(getActivity());
        addCheckBoxListView(consumersList, userList, selectedId);

        addButton(R.string.SaveButtonText, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
                mainActivity.showList();
            }
        });

        addButton(R.string.DeleteButtonText, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem();
                mainActivity.showList();
            }
        });

        addButton(R.string.CancelButtonText, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelChanges();
                mainActivity.showList();
            }
        });
    }

    private void saveChanges() {
        String name = nameEditText.getText().toString();
        Integer cost = Integer.valueOf(costEditText.getText().toString());
        Set<Integer> selected = ((CheckBoxListShekelNamedAdapter) consumersList.getAdapter()).getSelected();

        if (name.isEmpty()) {
            showErrorDialog("Name is empty");
        }

        if (cost < 0) {
            showErrorDialog("Cost must be > 0");
        }

        if (selected.size() == 0) {
            showErrorDialog("No consumer selected!");
        }

        item.setName(name);
        item.setCost(cost);
        item.setConsumers(new ArrayList<ShekelUser>());
        for (Integer userId : selected) {
            item.getConsumers().add(users.get(userId));
        }
        String url = "";
        if (isNew) {
            url = ShekelNetwork.getInstance(getContext()).getUrlForAddItem(item);
        } else {
            url = ShekelNetwork.getInstance(getContext()).getUrlForUpdateItem(item);
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, // no parameters post
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Item Edit Fragment", "onResponse() called with: " + "response = [" + response + "]");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Item Edit Fragment", "onErrorResponse() called with: " + "error = [" + error + "]");
                    }
                }
        );
        ShekelNetwork.getInstance(getContext()).addToRequestQueue(request);
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setTitle("Error!").show();
    }

    private void cancelChanges() {
        // nothing to do here
    }

    private void deleteItem() {
        //// TODO: 11/25/2015 something
    }

    private void addCheckBoxListView(ListView listView, List<ShekelBaseEntity> elements, Set<Integer> selectedId) {
        CheckBoxListShekelNamedAdapter adapter = new CheckBoxListShekelNamedAdapter(getContext(), elements, selectedId);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("ShekelItemEditFragment", "Position " + position);
            }
        });
        form.addView(listView);
    }

    private void addFormField(EditText editText, String text, String label, int type) {
        TextView tvLabel = new TextView(getActivity());
        tvLabel.setLayoutParams(getDefaultParams(true));
        form.addView(tvLabel);
        tvLabel.setText(label);

        editText.setLayoutParams(getDefaultParams(false));
        editText.setInputType(type);
        editText.setText(text);
        form.addView(editText);
    }

    private void addButton(int textId, View.OnClickListener onClickListener) {
        Button button = new Button(getActivity());
        button.setLayoutParams(getDefaultParams(false));
        button.setText(textId);
        button.setOnClickListener(onClickListener);
        form.addView(button);
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
