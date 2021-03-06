package se.mit.spbau.ru.shekel3.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import se.mit.spbau.ru.shekel3.MainActivity;
import se.mit.spbau.ru.shekel3.R;
import se.mit.spbau.ru.shekel3.adapter.ListShekelNamedAdapter;
import se.mit.spbau.ru.shekel3.model.ShekelBaseEntity;
import se.mit.spbau.ru.shekel3.model.ShekelEvent;
import se.mit.spbau.ru.shekel3.model.ShekelReceipt;
import se.mit.spbau.ru.shekel3.model.ShekelUser;
import se.mit.spbau.ru.shekel3.utils.ShekelNetwork;

public class ShekelReceiptFragment extends ListFragment {
    private MainActivity mainActivity;
    private List<ShekelBaseEntity> receiptList = new ArrayList<>();
    private Map<Integer, ShekelUser> users;

    private ShekelEvent event;

    public void setEvent(ShekelEvent event) {
        this.event = event;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ShekelNetwork.getInstance(getContext()).getAllReceiptsUrl(event),
                null, // no parameters post
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        addHeader();
                        Gson gson = new Gson();
                        users = mainActivity.getUsers(); //todo maybe error (race)
                        ShekelReceipt.ShekelReceiptModelContainer container = gson.fromJson(response.toString(), ShekelReceipt.ShekelReceiptModelContainer.class);
                        for (ShekelReceipt.ShekelReceiptModel shekelReceiptModel : container.getData()) {
                            ShekelReceipt shekelReceipt = new ShekelReceipt();
                            shekelReceipt.setId(shekelReceiptModel.getId());
                            shekelReceipt.setName(shekelReceiptModel.getName());
                            shekelReceipt.setCost(shekelReceiptModel.getCost());
                            shekelReceipt.setOwner(users.get(shekelReceiptModel.getOwner()));
                            List<ShekelUser> userList = new ArrayList<>();
                            for (Integer userId : shekelReceiptModel.getConsumer_ids()) {
                                userList.add(users.get(userId));
                            }
                            shekelReceipt.setConsumers(userList);
                            receiptList.add(shekelReceipt);
                        }
                        setListAdapter(new ListShekelNamedAdapter(getActivity(), receiptList));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Receipt Fragment", "onErrorResponse() called with: " + "error = [" + error + "]");
                    }
                }
        );

        ShekelNetwork.getInstance(getContext()).addToRequestQueue(request);
    }

    private void addHeader() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView header = new TextView(getActivity());
                header.setText(R.string.ReceiptListName);
                header.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Receipt Fragment", "OnClickListener() called with: ");
                    }
                });

                Button button = new Button(getActivity());
                button.setText(R.string.AddName);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mainActivity.addNewReceipt(event);
                    }
                });

                AbsListView.LayoutParams layoutParams = new ListView.LayoutParams(
                        100, ListView.LayoutParams.WRAP_CONTENT);

                button.setLayoutParams(layoutParams);

                getListView().addHeaderView(header);
                getListView().addHeaderView(button);
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        registerForContextMenu(getListView());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.edit_item_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (getUserVisibleHint()) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            ShekelReceipt receipt = (ShekelReceipt) getListAdapter().getItem((int) info.id);
            switch (item.getItemId()) {
                case R.id.action_add:
                    mainActivity.addNewReceipt(event);
                    return true;
                case R.id.action_delete:
                    JsonObjectRequest request = new JsonObjectRequest(
                            Request.Method.GET,
                            ShekelNetwork.getInstance(getContext()).getUrlForDeleteReceipt(event, receipt),
                            null, // no parameters post
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    mainActivity.showReceiptList(event);
                                    Log.d("Receipt Fragment", "onErrorResponse() called with: " + "error = [" + response + "]");
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    mainActivity.showReceiptList(event);
                                    Log.d("Receipt Fragment", "onErrorResponse() called with: " + "error = [" + error + "]");
                                }
                            }
                    );
                    ShekelNetwork.getInstance(getContext()).addToRequestQueue(request);
                    return true;
                case R.id.action_edit:
                    mainActivity.changeReceipt(event, receipt);
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mainActivity.showItemList(event, (ShekelReceipt) l.getAdapter().getItem(position));
    }

}
