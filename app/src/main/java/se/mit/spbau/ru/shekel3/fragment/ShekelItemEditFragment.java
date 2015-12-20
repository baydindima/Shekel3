package se.mit.spbau.ru.shekel3.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import se.mit.spbau.ru.shekel3.MainActivity;
import se.mit.spbau.ru.shekel3.R;
import se.mit.spbau.ru.shekel3.adapter.CheckBoxListShekelNamedAdapter;
import se.mit.spbau.ru.shekel3.model.ShekelBaseEntity;
import se.mit.spbau.ru.shekel3.model.ShekelEvent;
import se.mit.spbau.ru.shekel3.model.ShekelItem;
import se.mit.spbau.ru.shekel3.model.ShekelReceipt;
import se.mit.spbau.ru.shekel3.model.ShekelUser;
import se.mit.spbau.ru.shekel3.utils.ShekelFormBuilder;
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

    private ShekelReceipt receipt;

    public void setReceipt(ShekelReceipt shekelReceipt) {
        receipt = shekelReceipt;
    }

    private ShekelEvent event;
    public void setEvent(ShekelEvent event) {
        this.event = event;
    }

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
            ShekelFormBuilder.addFormField(form, getActivity(), nameEditText, "", "Name of Item", InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
            ShekelFormBuilder.addFormField(form, getActivity(), costEditText, "", "Cost of Item", InputType.TYPE_CLASS_NUMBER);
        } else {
            ShekelFormBuilder.addFormField(form, getActivity(), nameEditText, item.getName(), "Name of Item", InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
            ShekelFormBuilder.addFormField(form, getActivity(), costEditText, String.valueOf(item.getCost()), "Cost of Item", InputType.TYPE_CLASS_NUMBER);
        }

        List<ShekelBaseEntity> userList = new ArrayList<ShekelBaseEntity>(users.values());
        Set<Integer> selectedId = new HashSet<>();
        if (!isNew) {
            for (ShekelUser shekelUser : item.getConsumers()) {
                selectedId.add(shekelUser.getId());
            }
        }
        consumersList = new ListView(getActivity());
        ShekelFormBuilder.addCheckBoxListView(form, getContext(), consumersList, userList, selectedId);

        ShekelFormBuilder.addButton(form, getActivity(), R.string.SaveButtonText, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();

            }
        });

//        ShekelFormBuilder.addButton(form, getActivity(), R.string.CancelButtonText, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cancelChanges();
//                mainActivity.showItemList(event, receipt);
//            }
//        });
    }

    private void saveChanges() {
        String name = nameEditText.getText().toString();
        Integer cost = Integer.valueOf(costEditText.getText().toString());
        Set<Integer> selected = ((CheckBoxListShekelNamedAdapter) consumersList.getAdapter()).getSelected();

        if (name.isEmpty()) {
            showErrorDialog("Name is empty");
            return;
        }

        if (cost < 0) {
            showErrorDialog("Cost must be > 0");
            return;
        }

        if (selected.size() == 0) {
            showErrorDialog("No consumer selected!");
            return;
        }

        item.setName(name);
        item.setCost(cost);
        item.setConsumers(new ArrayList<ShekelUser>());
        for (Integer userId : selected) {
            item.getConsumers().add(users.get(userId));
        }
        String url;
        if (isNew) {
            url = ShekelNetwork.getInstance(getContext()).getUrlForAddItem(event, receipt, item);
        } else {
            url = ShekelNetwork.getInstance(getContext()).getUrlForUpdateItem(event, receipt, item);
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, // no parameters post
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mainActivity.showItemList(event, receipt);
                        Log.d("Item Edit Fragment", "onResponse() called with: " + "response = [" + response + "]");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mainActivity.showItemList(event, receipt);
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
}
