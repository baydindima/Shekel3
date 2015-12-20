package se.mit.spbau.ru.shekel3.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import se.mit.spbau.ru.shekel3.MainActivity;
import se.mit.spbau.ru.shekel3.R;
import se.mit.spbau.ru.shekel3.model.ShekelUser;
import se.mit.spbau.ru.shekel3.utils.ShekelAccountManager;
import se.mit.spbau.ru.shekel3.utils.ShekelFormBuilder;
import se.mit.spbau.ru.shekel3.utils.ShekelNetwork;


public class ShekelAccountFragment extends Fragment {
    private LinearLayout form;
    private MainActivity mainActivity;
    private EditText loginText;
    private EditText passText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ScrollView view = new ScrollView(getActivity());
        String accessToken = getActivity().getPreferences(Context.MODE_PRIVATE).getString("ShekelAccessToken", "");
        Integer userId = getActivity().getPreferences(Context.MODE_PRIVATE).getInt("ShekelUserId", -1);

        if (!accessToken.isEmpty() && userId != -1) {
            ShekelAccountManager.getInstance().setAccessToken(accessToken, userId, getActivity());
            checkConnection();
        }

        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));

        mainActivity = (MainActivity) getActivity();

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
        loginText = new EditText(getActivity());
        ShekelFormBuilder.addFormField(form, getActivity(), loginText, "", "Login", InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        passText = new EditText(getActivity());
        ShekelFormBuilder.addFormField(form, getActivity(), passText, "", "Password", InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passText.setTransformationMethod(new PasswordTransformationMethod());

        ShekelFormBuilder.addButton(form, getActivity(), R.string.SaveButtonText, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });
    }

    private void saveChanges() {
        String login = loginText.getText().toString();
        if (login.isEmpty()) {
            showErrorDialog("Login is empty");
            return;
        }

        String pass = passText.getText().toString();
        if (login.isEmpty()) {
            showErrorDialog("Password is empty");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ShekelNetwork.getInstance(getActivity()).getLogInUrl(login, pass),
                null, // no parameters post
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        ShekelUser.UserAuthResult authResult = gson.fromJson(response.toString(), ShekelUser.UserAuthResult.class);
                        ShekelAccountManager.getInstance().setAccessToken(authResult.getAccess_token(), authResult.getUser_id(), getActivity());
                        checkConnection();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showErrorDialog("Authorization error!");
                        Log.d("Account Fragment", "onErrorResponse() called with: " + "error = [" + error + "]");
                    }
                }
        );
        ShekelNetwork.getInstance(getContext()).addToRequestQueue(request);
    }

    private void checkConnection() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ShekelNetwork.getInstance(getActivity()).getAllUsersUrl(),
                null, // no parameters post
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mainActivity.initUserList();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showErrorDialog("Authorization error!");
                        Log.d("Account Fragment", "onErrorResponse() called with: " + "error = [" + error + "]");
                    }
                }
        );
        ShekelNetwork.getInstance(getContext()).addToRequestQueue(request);
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setTitle("Error!").show();
    }
}
