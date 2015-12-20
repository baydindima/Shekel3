package se.mit.spbau.ru.shekel3.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import se.mit.spbau.ru.shekel3.MainActivity;
import se.mit.spbau.ru.shekel3.model.ShekelEvent;
import se.mit.spbau.ru.shekel3.model.ShekelUser;
import se.mit.spbau.ru.shekel3.model.statistics.ShekelConsumedStatistic;
import se.mit.spbau.ru.shekel3.model.statistics.ShekelSpentStatistic;
import se.mit.spbau.ru.shekel3.utils.ShekelNetwork;

public class ShekelStatisticFragment extends Fragment {
    private LinearLayout form;
    private MainActivity mainActivity;
    private Map<Integer, ShekelUser> users;

    private List<Integer> colors;


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
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ShekelNetwork.getInstance(getContext()).getAllEventsUrl(),
                null, // no parameters post
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        ShekelEvent.ShekelEventModelContainer container = gson.fromJson(response.toString(), ShekelEvent.ShekelEventModelContainer.class);
                        for (ShekelEvent.ShekelEventModel model : container.getData()) {
                                addConsunedPieChart(model.getId(), model.getName());
                                addSpentPieChart(model.getId(), model.getName());
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

    private void addSpentPieChart(Integer eventId, final String eventName) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ShekelNetwork.getInstance(getContext()).getEventUserSpentUrl(eventId),
                null, // no parameters post
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        users = mainActivity.getUsers();
                        ShekelSpentStatistic.Container container = gson.fromJson(response.toString(), ShekelSpentStatistic.Container.class);
                        List<Entry> data1 =  new ArrayList<>();
                        List<Entry> data2 =  new ArrayList<>();
                        List<String> names = new ArrayList<>();
                        for (int i = 0; i < container.getData().size(); i++) {
                            ShekelSpentStatistic.Model model = container.getData().get(i);
                            data1.add(new Entry(model.getSpent(), i));
                            data2.add(new Entry(model.getItems_bought(), i));
                            names.add(users.get(model.getUser()).getName());
                        }

                        addPieChart(data1, names, eventName + " coins spent");
                        addPieChart(data2, names, eventName + " items spent");
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

    private void addConsunedPieChart(Integer eventId, final String eventName) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ShekelNetwork.getInstance(getContext()).getEventUserConsumedUrl(eventId),
                null, // no parameters post
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        users = mainActivity.getUsers();
                        ShekelConsumedStatistic.Container container = gson.fromJson(response.toString(), ShekelConsumedStatistic.Container.class);
                        List<Entry> data1 =  new ArrayList<>();
                        List<Entry> data2 =  new ArrayList<>();
                        List<String> names = new ArrayList<>();
                        for (int i = 0; i < container.getData().size(); i++) {
                            ShekelConsumedStatistic.Model model = container.getData().get(i);
                            data1.add(new Entry(model.getConsumed(), i));
                            data2.add(new Entry(model.getItems_consumed(), i));
                            names.add(users.get(model.getUser()).getName());
                        }

                        addPieChart(data1, names, eventName + " coins consumed");
                        addPieChart(data2, names, eventName + " items consumed");
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

    private void addPieChart(List<Entry> data, List<String> names, String chartName) {
        PieChart pieChart = new PieChart(getActivity());
        pieChart.setDescription(chartName);
        setDefaultPieParams(pieChart);

        PieDataSet dataSet = new PieDataSet(data, "LabelShekel");
        setDefaultColors(dataSet);

        PieData pieData = new PieData(names, dataSet);
        pieChart.setData(pieData);
        form.addView(pieChart);
    }

    private void setDefaultPieParams(PieChart pieChart) {
        pieChart.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));


        pieChart.setCenterTextSize(11f);
        pieChart.setMinimumHeight(500);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColorTransparent(true);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setDrawCenterText(true);

        pieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setDescriptionTextSize(33f);
    }

    private void setDefaultColors(DataSet set) {
        if (colors == null) {
            colors = new ArrayList<>();

            for (int c : ColorTemplate.VORDIPLOM_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.JOYFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.COLORFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.LIBERTY_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.PASTEL_COLORS)
                colors.add(c);

            colors.add(ColorTemplate.getHoloBlue());
        }

        set.setColors(colors);
        set.setValueTextSize(22f);
    }
}
