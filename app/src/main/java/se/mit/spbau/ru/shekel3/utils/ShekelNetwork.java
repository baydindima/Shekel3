package se.mit.spbau.ru.shekel3.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import se.mit.spbau.ru.shekel3.model.ShekelEvent;
import se.mit.spbau.ru.shekel3.model.ShekelItem;
import se.mit.spbau.ru.shekel3.model.ShekelReceipt;
import se.mit.spbau.ru.shekel3.model.ShekelUser;

public class ShekelNetwork {
    private static final String IP = "46.101.144.60";
    private static final String SERVER_ADDRESS = "http://" + IP + "/";

    private static ShekelNetwork ourInstance;
    private static Context context;
    private static ShekelAccountManager shekelAccountManager = ShekelAccountManager.getInstance();
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

    public String getEventUserSpentUrl(Integer eventId) {
        return SERVER_ADDRESS + String.format("%d/spent?", eventId) + shekelAccountManager.getAuthParam();
    }

    public String getEventUserConsumedUrl(Integer eventId) {
        return SERVER_ADDRESS + String.format("%d/consumed?", eventId) + shekelAccountManager.getAuthParam();
    }

    public String getEventDebtsUrl(Integer eventId) {
        return SERVER_ADDRESS + String.format("%d/debts?", eventId) + shekelAccountManager.getAuthParam();
    }

    public String getAllEventsUrl() {
        return SERVER_ADDRESS + "events?" + shekelAccountManager.getAuthParam();
    }

    public String getEventInfo(ShekelEvent event) {
        return SERVER_ADDRESS + event.getId() + "?" + shekelAccountManager.getAuthParam();
    }

    public String getUrlForDeleteEvent(ShekelEvent event) {
        return SERVER_ADDRESS + String.format("%d/delete?",
                event.getId()) + shekelAccountManager.getAuthParam();
    }

    @SuppressLint("SimpleDateFormat")
    public String getUrlForUpdateEvent(ShekelEvent event) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("name", event.getName());
        params.put("date", new SimpleDateFormat(ShekelFormBuilder.DATE_FORMAT).format(event.getDate()));
        params.put("members_ids", getUrlParamValueForUserList(event.getUsers()));
        params.put("id", String.valueOf(event.getId()));
        return SERVER_ADDRESS + String.format("%d/edit%s&",
                event.getId(),
                getUrlParams(params)) + shekelAccountManager.getAuthParam();
    }

    @SuppressLint("SimpleDateFormat")
    public String getUrlForAddEvent(ShekelEvent event) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("name", event.getName());
        params.put("date", new SimpleDateFormat(ShekelFormBuilder.DATE_FORMAT).format(event.getDate()));
        params.put("members_ids", getUrlParamValueForUserList(event.getUsers()));
        return SERVER_ADDRESS + String.format("events/add%s&",
                getUrlParams(params)) + shekelAccountManager.getAuthParam();
    }

    public String getLogInUrl(String login, String pass) {
        return SERVER_ADDRESS +  String.format("login?username=%s&password=%s", login, pass);
    }

    public String getAllItemsUrl(ShekelEvent event, ShekelReceipt receipt) {
        return SERVER_ADDRESS + String.format("%d/%d/items?",
                event.getId(),
                receipt.getId()) + shekelAccountManager.getAuthParam();
    }

    public String getItemUrl(ShekelEvent event, ShekelReceipt receipt, ShekelItem item) {
        return SERVER_ADDRESS + String.format("%d/%d/%d?",
                event.getId(),
                receipt.getId(),
                item.getId()) + shekelAccountManager.getAuthParam();
    }

    public String getAllReceiptsUrl(ShekelEvent event) {
        return SERVER_ADDRESS + String.format("%d/receipts?",
                event.getId()) + shekelAccountManager.getAuthParam();
    }

    public String getReceiptUrl(ShekelEvent event, ShekelReceipt receipt) {
        return SERVER_ADDRESS + String.format("%d/%d?",
                event.getId(),
                receipt.getId()) + shekelAccountManager.getAuthParam();
    }

    public String getUrlForUpdateItem(ShekelEvent event, ShekelReceipt receipt, ShekelItem item) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("name", item.getName());
        params.put("cost", String.valueOf(item.getCost()));
        params.put("consumer_ids", getUrlParamValueForUserList(item.getConsumers()));
        return String.format(SERVER_ADDRESS + "%d/%d/%d/edit%s&",
                event.getId(),
                receipt.getId(),
                item.getId(),
                getUrlParams(params)) + shekelAccountManager.getAuthParam();
    }

    public String getUrlForUpdateReceipt(ShekelEvent event, ShekelReceipt receipt) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("name", receipt.getName());
        return String.format(SERVER_ADDRESS + "%d/%d/rename%s&",
                event.getId(),
                receipt.getId(),
                getUrlParams(params)) + shekelAccountManager.getAuthParam();
    }

    public String getUrlForAddReceipt(ShekelEvent event, ShekelReceipt receipt) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("name", receipt.getName());
        params.put("owner", String.valueOf(receipt.getOwner()));
        return String.format(SERVER_ADDRESS + "%d/receipts/add%s&",
                event.getId(),
                getUrlParams(params)) + shekelAccountManager.getAuthParam();
    }

    public String getUrlForAddItem(ShekelEvent event, ShekelReceipt receipt, ShekelItem item) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("name", item.getName());
        params.put("cost", String.valueOf(item.getCost()));
        params.put("consumer_ids", getUrlParamValueForUserList(item.getConsumers()));
        return String.format(SERVER_ADDRESS + "%d/%d/add%s&",
                event.getId(),
                receipt.getId(),
                getUrlParams(params)) + shekelAccountManager.getAuthParam();
    }

    public String getAllUsersUrl() {
        return SERVER_ADDRESS + "users?" + shekelAccountManager.getAuthParam();
    }

    public String getUrlForDeleteItem(ShekelEvent event, ShekelReceipt receipt, ShekelItem item) {
        return String.format(SERVER_ADDRESS + "%d/%d/%d/delete?",
                event.getId(),
                receipt.getId(),
                item.getId()) + shekelAccountManager.getAuthParam();
    }

    public String getUrlForDeleteReceipt(ShekelEvent event, ShekelReceipt receipt) {
        return String.format(SERVER_ADDRESS + "%d/%d/delete?",
                event.getId(),
                receipt.getId()) + shekelAccountManager.getAuthParam();
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
