package se.mit.spbau.ru.shekel3.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Map;

import se.mit.spbau.ru.shekel3.MainActivity;
import se.mit.spbau.ru.shekel3.model.ShekelUser;
import se.mit.spbau.ru.shekel3.model.statistics.ShekelDebtsStatistics;
import se.mit.spbau.ru.shekel3.utils.ShekelNetwork;

/**
 * Created by John on 12/18/2015.
 */
public class ShekelDebtsStatisticsFragment extends Fragment {
    private LinearLayout form;
    private MainActivity mainActivity;
    private Map<Integer, ShekelUser> users;

    private TableLayout tableLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ScrollView view = new ScrollView(getActivity());
        mainActivity = (MainActivity) getActivity();
        users = mainActivity.getUsers();

        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));


        form = new LinearLayout(getActivity());
        form.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        form.setOrientation(LinearLayout.VERTICAL);

        view.addView(form);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buildForm();
            }
        });
        return view;
    }

    private void buildForm() {
        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        final TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);

        tableLayout = new TableLayout(getActivity());
        tableLayout.setLayoutParams(tableParams);

        tableLayout.setGravity(Gravity.CENTER);

        TableRow tableRow = new TableRow(getActivity());
        tableRow.setLayoutParams(rowParams);

        TextView textView1 = new TextView(getActivity());
        textView1.setTextSize(45);
        textView1.setPadding(10, 10, 10, 10);
        TextView textView2 = new TextView(getActivity());
        textView2.setTextSize(45);
        textView2.setPadding(10, 10, 10, 10);
        TextView textView3 = new TextView(getActivity());
        textView3.setTextSize(45);
        textView3.setPadding(10, 10, 10, 10);

        textView1.setText("From  ");

        tableRow.addView(textView1);
        textView2.setText("  To  ");
        tableRow.addView(textView2);
        textView3.setText("  Value  ");
        tableRow.addView(textView3);

        tableLayout.addView(tableRow);
        form.addView(tableLayout);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ShekelNetwork.getInstance(getContext()).getEventDebtsUrl(1),
                null, // no parameters post
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        ShekelDebtsStatistics.Container container = gson.fromJson(response.toString(), ShekelDebtsStatistics.Container.class);
                        for (ShekelDebtsStatistics.Model model : container.getData().getDebts()) {
                            TableRow tableRow = new TableRow(getActivity());
                            tableRow.setLayoutParams(rowParams);

                            TextView textView1 = new TextView(getActivity());
                            textView1.setTextSize(45);
                            textView1.setPadding(10, 10, 10, 10);
                            TextView textView2 = new TextView(getActivity());
                            textView2.setTextSize(45);
                            textView2.setPadding(10, 10, 10, 10);
                            TextView textView3 = new TextView(getActivity());
                            textView3.setTextSize(45);
                            textView3.setPadding(10, 10, 10, 10);

                            textView1.setText(users.get(model.getFrom()).getName());
                            tableRow.addView(textView1);
                            textView2.setText(users.get(model.getTo()).getName());
                            tableRow.addView(textView2);
                            textView3.setText(String.valueOf(model.getDebt()));
                            tableRow.addView(textView3);

                            tableLayout.addView(tableRow);
                        }
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

}
