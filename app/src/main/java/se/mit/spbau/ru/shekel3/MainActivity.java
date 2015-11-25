package se.mit.spbau.ru.shekel3;

import android.os.Bundle;
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

import se.mit.spbau.ru.shekel3.fragment.ShekelItemEditFragment;
import se.mit.spbau.ru.shekel3.fragment.ShekelItemFragment;
import se.mit.spbau.ru.shekel3.fragment.ShekelReceiptEditFragment;
import se.mit.spbau.ru.shekel3.fragment.ShekelReceiptFragment;
import se.mit.spbau.ru.shekel3.model.ShekelItem;
import se.mit.spbau.ru.shekel3.model.ShekelReceipt;
import se.mit.spbau.ru.shekel3.model.ShekelUser;
import se.mit.spbau.ru.shekel3.utils.ShekelNetwork;

public class MainActivity extends AppCompatActivity {

    private Map<Integer, ShekelUser> users = new HashMap<>();

    public Map<Integer, ShekelUser> getUsers() {
        return users;
    }

    private void initUserList() {
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

        initUserList();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ShekelReceiptFragment receiptFragment = new ShekelReceiptFragment();

        receiptFragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, receiptFragment).commit();

//        ShekelItemFragment itemFragment = new ShekelItemFragment();
//
//        itemFragment.setArguments(getIntent().getExtras());
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.fragment, itemFragment).commit();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeItem(ShekelReceipt receipt, ShekelItem item) {
        ShekelItemEditFragment itemFragment = new ShekelItemEditFragment();
        itemFragment.setShekelItem(item);

        itemFragment.setArguments(getIntent().getExtras());
        itemFragment.setReceipt(receipt);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, itemFragment).commit();
    }

    public void addNewItem(ShekelReceipt receipt) {
        ShekelItemEditFragment itemFragment = new ShekelItemEditFragment();
        itemFragment.setShekelItem(new ShekelItem());
        itemFragment.setIsNew(true);
        itemFragment.setArguments(getIntent().getExtras());
        itemFragment.setReceipt(receipt);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, itemFragment).commit();
    }

    public void showItemList(ShekelReceipt receipt) {
        ShekelItemFragment itemFragment = new ShekelItemFragment();

        itemFragment.setArguments(getIntent().getExtras());
        itemFragment.setReceipt(receipt);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, itemFragment).commit();
    }

    public void showReceiptList() {
        ShekelReceiptFragment receiptFragment = new ShekelReceiptFragment();
        receiptFragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, receiptFragment).commit();
    }

    public void changeReceipt(ShekelReceipt receipt) {
        ShekelReceiptEditFragment receiptEditFragment = new ShekelReceiptEditFragment();
        receiptEditFragment.setReceipt(receipt);
        receiptEditFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, receiptEditFragment).commit();
    }

    public void addNewReceipt() {
        ShekelReceiptEditFragment receiptEditFragment = new ShekelReceiptEditFragment();
        receiptEditFragment.setIsNew(true);
        receiptEditFragment.setReceipt(new ShekelReceipt());
        receiptEditFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, receiptEditFragment).commit();
    }
}
