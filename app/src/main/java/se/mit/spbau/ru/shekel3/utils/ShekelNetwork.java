package se.mit.spbau.ru.shekel3.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by John on 11/13/2015.
 */
public class ShekelNetwork  {
    private static final String IP = "46.101.144.60";
    private static final String SERVER_ADDRESS = "http://" + IP + ":8000/";
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

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }
}
