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
import se.mit.spbau.ru.shekel3.model.ShekelReceipt;
import se.mit.spbau.ru.shekel3.model.ShekelUser;
import se.mit.spbau.ru.shekel3.utils.ShekelFormBuilder;
import se.mit.spbau.ru.shekel3.utils.ShekelNetwork;

/**
 * Created by John on 11/25/2015.
 */
public class ShekelReceiptEditFragment extends Fragment {
    private boolean isNew;
    private LinearLayout form;
    private MainActivity mainActivity;
    private Map<Integer, ShekelUser> users;

    private EditText nameEditText;
    private ListView consumersList;

    private ShekelReceipt receipt;

    public void setReceipt(ShekelReceipt shekelReceipt) {
        receipt = shekelReceipt;
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
        if (isNew) {
            ShekelFormBuilder.addFormField(form, getActivity(), nameEditText, "", "Name of Receipt", InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        } else {
            ShekelFormBuilder.addFormField(form, getActivity(), nameEditText, receipt.getName(), "Name of Receipt", InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        }

        if (isNew) {
            List<ShekelBaseEntity> userList = new ArrayList<ShekelBaseEntity>(users.values());
            Set<Integer> selectedId = new HashSet<>();
            if (!isNew) {
                for (ShekelUser shekelUser : receipt.getConsumers()) {
                    selectedId.add(shekelUser.getId());
                }
            }
            consumersList = new ListView(getActivity());
            ShekelFormBuilder.addCheckBoxListView(form, getContext(), consumersList, userList, selectedId);
        }

        ShekelFormBuilder.addButton(form, getActivity(), R.string.SaveButtonText, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
                mainActivity.showReceiptList();
            }
        });

        ShekelFormBuilder.addButton(form, getActivity(), R.string.CancelButtonText, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelChanges();
                mainActivity.showReceiptList();
            }
        });
    }

    private void saveChanges() {
        String name = nameEditText.getText().toString();
        if (name.isEmpty()) {
            showErrorDialog("Name is empty");
            return;
        }

        if (isNew) {
            Set<Integer> selected = ((CheckBoxListShekelNamedAdapter) consumersList.getAdapter()).getSelected();
            if (selected.size() != 1) {
                showErrorDialog("One consumer must be selected!");
                return;
            }
            for (Integer userId : selected) { // hack in selected must be 1 user
                receipt.setOwner(users.get(userId));
            }
        }

        receipt.setName(name);

        String url;
        if (isNew) {
            url = ShekelNetwork.getInstance(getContext()).getUrlForAddReceipt(receipt);
        } else {
            url = ShekelNetwork.getInstance(getContext()).getUrlForUpdateReceipt(receipt);
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, // no parameters post
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Receipt Edit Fragment", "onResponse() called with: " + "response = [" + response + "]");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Receipt Edit Fragment", "onErrorResponse() called with: " + "error = [" + error + "]");
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
