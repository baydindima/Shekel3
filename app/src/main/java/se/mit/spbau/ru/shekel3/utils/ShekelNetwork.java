package se.mit.spbau.ru.shekel3.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import se.mit.spbau.ru.shekel3.model.ShekelItem;
import se.mit.spbau.ru.shekel3.model.ShekelUser;

/**
 * Created by John on 11/13/2015.
 */
public class ShekelNetwork  {
    private static final String IP = "46.101.144.60";
    private static final String SERVER_ADDRESS = "http://" + IP + "/";
    public static final String ITEMS_API_ADDRESS = SERVER_ADDRESS + "items/";
    public static final String USERS_API_ADDRESS = SERVER_ADDRESS + "users/";

    private static ShekelNetwork ourInstance;
    private static Context context;

    private RequestQueue requestQueue;

    public static synchronized ShekelNetwork getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new ShekelNetwork(context);
        }
        return ourInstance;
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue== null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    private ShekelNetwork(Context context) {
        ShekelNetwork.context = context;
        requestQueue = getRequestQueue();
    }

    public String getAllItemsUrl(Integer receiptId) {
        return  SERVER_ADDRESS  + "event_id/" + receiptId + "/items";
    }

    public String getItemUrl(Integer receiptId, Integer itemId) {
        return SERVER_ADDRESS + "event_id/" + receiptId + "/" + itemId;
    }

    public String getAllReceiptsUrl() {
        return SERVER_ADDRESS + "event_id/" + "receipts";
    }

    public String getReceiptUrl(Integer receiptId) {
        return SERVER_ADDRESS + "event_id/" + receiptId;
    }

    public String getUrlForUpdateItem(ShekelItem item) {
        StringBuilder builder = new StringBuilder();
        for (ShekelUser shekelUser : item.getConsumers()) {
            builder.append(shekelUser.getId()).append(",");
        }
        String consumersList = builder.deleteCharAt(builder.length() - 1).toString();
        return SERVER_ADDRESS + "event_id/1/" +item.getId() + "/edit?name="+ item.getName()+"&cost="+item.getCost()+"&consumer_ids=" + consumersList;
    }

    public String getUrlForAddItem(ShekelItem item) {
        return "";
    }

    public String getAllUsersUrl() {
        return SERVER_ADDRESS + "users";
    }

    public  String getUserUrl(Integer userId) {
        return SERVER_ADDRESS + "users/" + userId;
    }


    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }
}
