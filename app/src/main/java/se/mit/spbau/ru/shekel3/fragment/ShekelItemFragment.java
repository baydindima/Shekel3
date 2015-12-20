package se.mit.spbau.ru.shekel3.fragment;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
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
import se.mit.spbau.ru.shekel3.model.ShekelItem;
import se.mit.spbau.ru.shekel3.model.ShekelReceipt;
import se.mit.spbau.ru.shekel3.model.ShekelUser;
import se.mit.spbau.ru.shekel3.utils.ShekelFormBuilder;
import se.mit.spbau.ru.shekel3.utils.ShekelNetwork;


public class ShekelItemFragment extends ListFragment {
    private MainActivity mainActivity;
    private List<ShekelBaseEntity> itemList = new ArrayList<>();
    private Map<Integer, ShekelUser> users;
    private ShekelReceipt receipt;

    public void setReceipt(ShekelReceipt shekelReceipt) {
        receipt = shekelReceipt;
    }

    private ShekelEvent event;
    public void setEvent(ShekelEvent event) {
        this.event = event;
    }

    private void addHeader() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView header = new TextView(getActivity());
                header.setText(R.string.ItemListName);
                header.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Item Fragment", "OnClickListener() called with: ");
                    }
                });

                Button button = new Button(getActivity());
                button.setText(R.string.AddName);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mainActivity.addNewItem(event, receipt);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ShekelNetwork.getInstance(getContext()).getAllItemsUrl(event, receipt),
                null, // no parameters post
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        users = mainActivity.getUsers();
//                        addHeader();
                        ShekelItem.ItemModelContainer container = gson.fromJson(response.toString(), ShekelItem.ItemModelContainer.class);
                        List<ShekelItem.ItemModel> data = container.getData();
                        for (ShekelItem.ItemModel itemModel : data) {
                            ShekelItem shekelItem = new ShekelItem();
                            shekelItem.setCost(itemModel.getCost());
                            shekelItem.setId(itemModel.getId());
                            shekelItem.setName(itemModel.getName());
                            for (Integer id : itemModel.getConsumer_ids()) {
                                shekelItem.getConsumers().add(users.get(id));
                            }
                            shekelItem.setCustomer(users.get(itemModel.getCustomer()));
                            itemList.add(shekelItem);
                        }
                        setListAdapter(new ListShekelNamedAdapter(getActivity(), itemList));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Item Fragment", "onErrorResponse() called with: " + "error = [" + error + "]");
                    }
                }
        );

        ShekelNetwork.getInstance(getContext()).addToRequestQueue(request);
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
            ShekelItem shekelItem = (ShekelItem) getListAdapter().getItem((int) info.id);
            switch (item.getItemId()) {
                case R.id.action_add:
                    mainActivity.addNewItem(event, receipt);
                    return true;
                case R.id.action_delete:
                    JsonObjectRequest request = new JsonObjectRequest(
                            Request.Method.GET,
                            ShekelNetwork.getInstance(getContext()).getUrlForDeleteItem(event, receipt, shekelItem),
                            null, // no parameters post
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    mainActivity.showItemList(event, receipt);
                                    Log.d("Item Fragment", "onErrorResponse() called with: " + "error = [" + response + "]");
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    mainActivity.showItemList(event, receipt);
                                    Log.d("Item Fragment", "onErrorResponse() called with: " + "error = [" + error + "]");
                                }
                            }
                    );
                    ShekelNetwork.getInstance(getContext()).addToRequestQueue(request);
                    return true;
                case R.id.action_edit:
                    mainActivity.changeItem(event, receipt, shekelItem);
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }
        return super.onContextItemSelected(item);
    }

//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
//        mainActivity.changeItem(event, receipt, (ShekelItem) l.getAdapter().getItem(position));
//    }

}
