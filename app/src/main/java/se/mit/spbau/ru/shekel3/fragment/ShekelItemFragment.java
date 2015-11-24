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

import se.mit.spbau.ru.shekel3.MainActivity;
import se.mit.spbau.ru.shekel3.R;
import se.mit.spbau.ru.shekel3.adapter.ListShekelItemAdapter;
import se.mit.spbau.ru.shekel3.model.ShekelItem;
import se.mit.spbau.ru.shekel3.utils.ShekelNetwork;


public class ShekelItemFragment extends ListFragment {
    private List<ShekelItem> itemList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        ShekelItem item1 = new ShekelItem("Item1", 1, 10, null, null);
//        itemList.add(item1);
//        ShekelItem item2 = new ShekelItem("Item2", 2, 10, null, null);
//        itemList.add(item2);
//        ShekelItem item3 = new ShekelItem("Item3", 3, 10, null, null);
//        itemList.add(item3);
//        setListAdapter(new ListShekelItemAdapter(getActivity(), itemList));

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ShekelNetwork.ITEMS_API_ADDRESS,
                null, // no parameters post
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        ShekelItem.ItemModelContainer container = gson.fromJson(response.toString(), ShekelItem.ItemModelContainer.class);
                        List<ShekelItem.ItemModel> data = container.getData();
                        for (ShekelItem.ItemModel itemModel : data) {
                            ShekelItem shekelItem = new ShekelItem();
                            shekelItem.setCost(itemModel.getCost());
                            shekelItem.setId(itemModel.getId());
                            shekelItem.setName(itemModel.getName());
                            itemList.add(shekelItem);
                        }
                        setListAdapter(new ListShekelItemAdapter(getActivity(), itemList));
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
        ((MainActivity) getActivity()).changeItem((ShekelItem) l.getAdapter().getItem(position));
    }
}
