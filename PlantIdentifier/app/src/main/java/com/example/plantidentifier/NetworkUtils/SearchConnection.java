package com.example.plantidentifier.NetworkUtils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchConnection implements ClientConnection{

    private final String searchWord;
    private JSONObject res;
    private boolean success = false;
    private boolean finished = false;

    private static final String TAG = "SearchConnectionDebug";

    public boolean getSuccess(){
        return success;
    }

    public boolean isFinished() {
        return finished;
    }

    public SearchConnection(String search){
        searchWord = search;
        res = null;
    }

    public JSONObject getRes(){
        return res;
    }

    @Override
    public void connect(){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request
                .Builder()
                .get()
                .url(ipString + "search/" + searchWord)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                call.cancel();
                finished = true;
            }

            @Override
            public void onResponse(Call call, final Response response){
                try {
                    success = true;
                    res = new JSONObject(Objects.requireNonNull(response.body()).string());
                    finished = true;
                } catch (IOException | JSONException e) {
                    success = false;
                    finished = true;
                }
            }
        });
    }
}
