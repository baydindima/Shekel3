package se.mit.spbau.ru.shekel3.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by John on 12/11/2015.
 */
public class ShekelAccountManager {
    private String accessToken;
    private Integer userId;

    private static ShekelAccountManager shekelAccountNetwork;

    private ShekelAccountManager(){
        //todo get pass from disk
    }

    public boolean isLogIn() {
        return true;
    }

    public void saveUserPass(String userName, String pass) {

    }

    public void deleteUserPass() {

    }

    public void setAccessToken(String accessToken, Integer userId, Activity activity) {
        this.accessToken = accessToken;
        this.userId = userId;

        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("ShekelAccessToken", accessToken);
        editor.putInt("ShekelUserId", userId);
        editor.commit();
    }

    public static synchronized ShekelAccountManager getInstance() {
        if (shekelAccountNetwork == null) {
            shekelAccountNetwork = new ShekelAccountManager();
        }
        return shekelAccountNetwork;
    }

    public String getAuthParam() {
        return String.format("access_token=%s&user=%d", accessToken, userId);
    }

}
