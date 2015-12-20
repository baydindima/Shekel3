package se.mit.spbau.ru.shekel3.fragment;

import android.annotation.SuppressLint;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import se.mit.spbau.ru.shekel3.MainActivity;
import se.mit.spbau.ru.shekel3.R;
import se.mit.spbau.ru.shekel3.adapter.ListShekelNamedAdapter;
import se.mit.spbau.ru.shekel3.model.ShekelBaseEntity;
import se.mit.spbau.ru.shekel3.model.ShekelEvent;
import se.mit.spbau.ru.shekel3.model.ShekelUser;
import se.mit.spbau.ru.shekel3.utils.ShekelFormBuilder;
import se.mit.spbau.ru.shekel3.utils.ShekelNetwork;


public class ShekelEventFragment extends ListFragment {
    private MainActivity mainActivity;
    private List<ShekelBaseEntity> eventList = new ArrayList<>();
    private Map<Integer, ShekelUser> users;

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat dateFormat = new SimpleDateFormat(ShekelFormBuilder.DATE_FORMAT);

    private void addHeader() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView header = new TextView(getActivity());
                header.setText(R.string.EventListName);
                header.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Event Fragment", "OnClickListener() called with: ");
                    }
                });

                Button button = new Button(getActivity());
                button.setText(R.string.AddName);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mainActivity.addNewEvent();
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
    public void onViewCreated(View view1, Bundle savedInstanceState) {
        super.onViewCreated(view1, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ShekelNetwork.getInstance(getContext()).getAllEventsUrl(),
                null, // no parameters post
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        addHeader();
                        Gson gson = new Gson();
                        users = mainActivity.getUsers();
                        ShekelEvent.ShekelEventModelContainer container = gson.fromJson(response.toString(), ShekelEvent.ShekelEventModelContainer.class);
                        for (ShekelEvent.ShekelEventModel model : container.getData()) {
                            ShekelEvent shekelEvent = new ShekelEvent();
                            shekelEvent.setName(model.getName());
                            shekelEvent.setId(model.getId());
                            for (Integer userId : model.getMember_ids()) {
                                shekelEvent.getUsers().add(users.get(userId));
                            }
                            try {
                                shekelEvent.setDate(dateFormat.parse(model.getDate()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            eventList.add(shekelEvent);
                        }
                        setListAdapter(new ListShekelNamedAdapter(getActivity(), eventList));

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Event Fragment", "onErrorResponse() called with: " + "error = [" + error + "]");
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
            ShekelEvent event = (ShekelEvent) getListAdapter().getItem((int) info.id);
            switch (item.getItemId()) {
                case R.id.action_add:
                    mainActivity.addNewEvent();
                    return true;
                case R.id.action_delete:
                    JsonObjectRequest request = new JsonObjectRequest(
                            Request.Method.GET,
                            ShekelNetwork.getInstance(getContext()).getUrlForDeleteEvent(event),
                            null, // no parameters post
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    mainActivity.showEventList();
                                    Log.d("Event Fragment", "onResponse() called with: " + "response = [" + response + "]");
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    mainActivity.showEventList();
                                    Log.d("Event Fragment", "onErrorResponse() called with: " + "error = [" + error + "]");
                                }
                            }
                    );
                    ShekelNetwork.getInstance(getContext()).addToRequestQueue(request);
                    return true;
                case R.id.action_edit:
                    mainActivity.changeEvent(event);
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
        mainActivity.showReceiptList((ShekelEvent) l.getAdapter().getItem(position));
    }

}
