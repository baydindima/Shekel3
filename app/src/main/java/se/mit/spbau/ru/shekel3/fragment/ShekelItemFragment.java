package se.mit.spbau.ru.shekel3.fragment;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;


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
import se.mit.spbau.ru.shekel3.adapter.ListShekelNamedAdapter;
import se.mit.spbau.ru.shekel3.model.ShekelItem;
import se.mit.spbau.ru.shekel3.model.ShekelBaseEntity;
import se.mit.spbau.ru.shekel3.model.ShekelUser;
import se.mit.spbau.ru.shekel3.utils.ShekelNetwork;


public class ShekelItemFragment extends ListFragment {
    private MainActivity mainActivity;
    private List<ShekelBaseEntity> itemList = new ArrayList<>();
    private Map<Integer, ShekelUser> users;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ShekelNetwork.getInstance(getContext()).getAllItemsUrl(1),
                null, // no parameters post
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        users = mainActivity.getUsers(); //todo maybe error (race)
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
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mainActivity.changeItem((ShekelItem) l.getAdapter().getItem(position));
    }
}
