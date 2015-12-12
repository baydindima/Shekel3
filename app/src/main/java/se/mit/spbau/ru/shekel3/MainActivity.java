package se.mit.spbau.ru.shekel3;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import se.mit.spbau.ru.shekel3.fragment.ShekelAccountFragment;
import se.mit.spbau.ru.shekel3.fragment.ShekelEventEditFragment;
import se.mit.spbau.ru.shekel3.fragment.ShekelEventFragment;
import se.mit.spbau.ru.shekel3.fragment.ShekelItemEditFragment;
import se.mit.spbau.ru.shekel3.fragment.ShekelItemFragment;
import se.mit.spbau.ru.shekel3.fragment.ShekelReceiptEditFragment;
import se.mit.spbau.ru.shekel3.fragment.ShekelReceiptFragment;
import se.mit.spbau.ru.shekel3.fragment.ShekelStatisticFragment;
import se.mit.spbau.ru.shekel3.model.ShekelEvent;
import se.mit.spbau.ru.shekel3.model.ShekelItem;
import se.mit.spbau.ru.shekel3.model.ShekelReceipt;
import se.mit.spbau.ru.shekel3.model.ShekelUser;
import se.mit.spbau.ru.shekel3.utils.ShekelAccountManager;
import se.mit.spbau.ru.shekel3.utils.ShekelNetwork;

public class MainActivity extends AppCompatActivity {

    private Map<Integer, ShekelUser> users = new HashMap<>();

    public Map<Integer, ShekelUser> getUsers() {
        return users;
    }

    private void logIn() {
        ShekelAccountFragment accountFragment = new ShekelAccountFragment();

        accountFragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, accountFragment).commit();
    }

    public void initUserList() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ShekelNetwork.getInstance(getBaseContext()).getAllUsersUrl(),
                null, // no parameters post
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        ShekelUser.UserContainer container = gson.fromJson(response.toString(), ShekelUser.UserContainer.class);
                        for (ShekelUser shekelUser : container.getData()) {
                            users.put(shekelUser.getId(), shekelUser);
                        }
                        showEventList();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MainActivity", "onErrorResponse() called with: " + "error = [" + error + "]");
                    }
                }
        );
        ShekelNetwork.getInstance(getBaseContext()).addToRequestQueue(request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logIn();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            FragmentManager.BackStackEntry backStackEntry = getSupportFragmentManager().getBackStackEntryAt(count - 1);
            getSupportFragmentManager().popBackStack();
            switch (backStackEntry.getName()){
                case "ItemList" :
                    getSupportFragmentManager().popBackStack();
                case "ReceiptList" :
                case "Statistics" :
                    showEventList();
                    break;
                case "EventList" :
                default:
                    super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showStatistics();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showStatistics() {
        ShekelStatisticFragment statisticFragment = new ShekelStatisticFragment();
        statisticFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, statisticFragment).addToBackStack("Statistics").commit();
    }

    public void changeItem(ShekelEvent event, ShekelReceipt receipt, ShekelItem item) {
        ShekelItemEditFragment itemFragment = new ShekelItemEditFragment();
        itemFragment.setShekelItem(item);
        itemFragment.setEvent(event);
        itemFragment.setArguments(getIntent().getExtras());
        itemFragment.setReceipt(receipt);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, itemFragment).commit();
    }

    public void addNewItem(ShekelEvent event, ShekelReceipt receipt) {
        ShekelItemEditFragment itemFragment = new ShekelItemEditFragment();
        itemFragment.setShekelItem(new ShekelItem());
        itemFragment.setIsNew(true);
        itemFragment.setEvent(event);
        itemFragment.setArguments(getIntent().getExtras());
        itemFragment.setReceipt(receipt);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, itemFragment).commit();
    }

    public void showItemList(ShekelEvent event, ShekelReceipt receipt) {
        ShekelItemFragment itemFragment = new ShekelItemFragment();
        itemFragment.setEvent(event);
        itemFragment.setArguments(getIntent().getExtras());
        itemFragment.setReceipt(receipt);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, itemFragment).addToBackStack("ItemList").commit();
    }

    public void showReceiptList(ShekelEvent event) {
        ShekelReceiptFragment receiptFragment = new ShekelReceiptFragment();
        receiptFragment.setArguments(getIntent().getExtras());
        receiptFragment.setEvent(event);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, receiptFragment).addToBackStack("ReceiptList").commit();
    }

    public void changeReceipt(ShekelEvent event, ShekelReceipt receipt) {
        ShekelReceiptEditFragment receiptEditFragment = new ShekelReceiptEditFragment();
        receiptEditFragment.setReceipt(receipt);
        receiptEditFragment.setEvent(event);
        receiptEditFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, receiptEditFragment).commit();
    }

    public void addNewReceipt(ShekelEvent event) {
        ShekelReceiptEditFragment receiptEditFragment = new ShekelReceiptEditFragment();
        receiptEditFragment.setIsNew(true);
        receiptEditFragment.setEvent(event);
        receiptEditFragment.setReceipt(new ShekelReceipt());
        receiptEditFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, receiptEditFragment).commit();
    }

    public void addNewEvent() {
        ShekelEventEditFragment eventEditFragment = new ShekelEventEditFragment();
        eventEditFragment.setIsNew(true);
        eventEditFragment.setEvent(new ShekelEvent());
        eventEditFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, eventEditFragment).commit();
    }

    public void changeEvent(ShekelEvent event) {
        ShekelEventEditFragment eventEditFragment = new ShekelEventEditFragment();
        eventEditFragment.setEvent(event);
        eventEditFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, eventEditFragment).commit();
    }

    public void showEventList() {
        ShekelEventFragment eventFragment = new ShekelEventFragment();
        eventFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, eventFragment).commit();
    }
}
