package se.mit.spbau.ru.shekel3.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.mit.spbau.ru.shekel3.model.ShekelItem;
import se.mit.spbau.ru.shekel3.model.ShekelReceipt;
import se.mit.spbau.ru.shekel3.model.ShekelUser;

public class ShekelNetwork {
    private static final String IP = "46.101.144.60";
    private static final String SERVER_ADDRESS = "http://" + IP + "/";

    private static ShekelNetwork ourInstance;
    private static Context context;

    private RequestQueue requestQueue;

    private ShekelNetwork(Context context) {
        ShekelNetwork.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized ShekelNetwork getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new ShekelNetwork(context);
        }
        return ourInstance;
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public String getAllItemsUrl(Integer receiptId) {
        return SERVER_ADDRESS + "event_id/" + receiptId + "/items";
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

    public String getUrlForUpdateItem(Integer receiptId, ShekelItem item) {
        Map<String, String> params = new HashMap<>();
        params.put("name", item.getName());
        params.put("cost", String.valueOf(item.getCost()));
        params.put("consumer_ids", getUrlParamValueForUserList(item.getConsumers()));
        return String.format(SERVER_ADDRESS + "event_id/%d/%d/edit%s", receiptId, item.getId(), getUrlParams(params));
    }

    public String getUrlForUpdateReceipt(ShekelReceipt receipt) {
        Map<String, String> params = new HashMap<>();
        params.put("name", receipt.getName());
        return String.format(SERVER_ADDRESS + "event_id/%d/rename%s", receipt.getId(), getUrlParams(params));
    }

    public String getUrlForAddReceipt(ShekelReceipt receipt) {
        Map<String, String> params = new HashMap<>();
        params.put("name", receipt.getName());
        params.put("owner", String.valueOf(receipt.getOwner()));
        return String.format(SERVER_ADDRESS + "event_id/receipts/add%s", getUrlParams(params));
    }

    public String getUrlForAddItem(Integer receiptId, ShekelItem item) {
        Map<String, String> params = new HashMap<>();
        params.put("name", item.getName());
        params.put("cost", String.valueOf(item.getCost()));
        params.put("consumer_ids", getUrlParamValueForUserList(item.getConsumers()));
        return String.format(SERVER_ADDRESS + "event_id/%d/add%s", receiptId, getUrlParams(params));
    }

    public String getAllUsersUrl() {
        return SERVER_ADDRESS + "users";
    }

    public String getUrlForDeleteItem(Integer receiptId, ShekelItem item) {
        return String.format(SERVER_ADDRESS + "event_id/%d/%d/delete", receiptId, item.getId());
    }

    public String getUrlForDeleteReceipt(ShekelReceipt receipt) {
        return String.format(SERVER_ADDRESS + "event_id/%d/delete", receipt.getId());
    }

    public String getUserUrl(Integer userId) {
        return SERVER_ADDRESS + "users/" + userId;
    }

    private String getUrlParams(Map<String, String> param) {
        StringBuilder builder = new StringBuilder("?");
        for (Map.Entry<String, String> paramEntry : param.entrySet()) {
            builder.append(paramEntry.getKey()).append("=").append(paramEntry.getValue()).append("&");
        }
        return builder.deleteCharAt(builder.length() - 1).toString();
    }

    private String getUrlParamValueForUserList(List<ShekelUser> userList) {
        StringBuilder builder = new StringBuilder();
        for (ShekelUser shekelUser : userList) {
            builder.append(shekelUser.getId()).append(",");
        }
        return builder.deleteCharAt(builder.length() - 1).toString();
    }


    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }
}
