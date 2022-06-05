package com.final_project.plantidentifier.NetworkUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MultipartBody;

public class CameraConnection implements ClientConnection{

    private final Uri mImgUri;
    private boolean success;
    private boolean failed;
    private boolean failed_backup;
    private boolean finished;
    private JSONObject res;
    private volatile boolean innerFinish;
    private boolean curRes;
    Context mContext;

    private static final String TAG = "PhotoConnectionDebug";

    public CameraConnection(Uri ImgUri, Context context){
        res = null;
        mImgUri = ImgUri;
        mContext = context;
        success = false;
        finished = false;
        failed = false;
        innerFinish = false;
        curRes = false;
        failed_backup = false;
        Log.d(TAG,"start test");
        failed = testConnection(context, ipOnly);
        Log.d(TAG,"finish test");
        if(failed){
            Log.d(TAG,"start test b");
            failed_backup = testConnection(context, ipOnlyBackup);
            Log.d(TAG,"finish test b");
            if(failed_backup) {
                finished = true;
            }
        }
    }

    /**
     * checks if wifi and data is off
     * @param context
     * @return false if off, else true
     */
    private boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * tests if connection to server is possible
     * @param context
     * @return TRUE if connection CANNOT BE MADE, else false
     */
    private boolean testConnection(Context context, String ipOnly){
        innerFinish = false;
        if(!isOnline(context)){
            return true;
        }
//        try {
            new Thread( new Runnable() {
                @Override
                public void run() {
                    SocketAddress sockaddr = new InetSocketAddress(ipOnly, port);
                    // Create an unbound socket
                    Socket sock = new Socket();

                    // This method will block no more than timeoutMs.
                    // If the timeout occurs, SocketTimeoutException is thrown.

                    int timeoutMs = 2000;   // 2 seconds
                    try {
                        sock.connect(sockaddr, timeoutMs);
                        sock.close();
                        curRes = false;
                        innerFinish = true;
                    } catch (IOException e) {
                        curRes = true;
                        innerFinish = true;

                    }
                }
            }).start();

            while (!innerFinish) {
            }
//            innerFinish = false;
            return curRes;
//        } catch(Exception e) {
//            // Handle exception
//            Log.d(TAG, "!!!!!!!!!!" + e.toString());
//            return false;

    }

    public JSONObject getRes(){
        return res;
    }

    public boolean getSuccess() {
        return success && !failed;
    }

    public boolean isFinished(){
        return finished;
    }

    /**
     * connects to the server, and sets up the request
     */
    @Override
    public void connect(){
        String postUrl = ipString + "recognize/";
        if (failed){
            if(failed_backup) {
                return; // no need as it wont work anyway!
            }
            postUrl = ipStringBackup + "recognize/";
        }



        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            // Read BitMap by file path.
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), mImgUri);

//            Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath, options);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!found picture from uri!!!!!!!!!!!!!!!!!");
        }catch(Exception e){
            //todo: handle failure
            Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!problem with picture!!!!!!!!!!!!!!!!!!!");
            return;
        }
        byte[] byteArray = stream.toByteArray();

        multipartBodyBuilder.addFormDataPart("image", "Android_Flask_" + ".jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray));
        RequestBody postBodyImage = multipartBodyBuilder.build();
        postRequest(postUrl, postBodyImage);

    }

    /**
     * sends the picture to the server
     * @param postUrl server url
     * @param postBody picture
     */
    void postRequest(String postUrl, RequestBody postBody) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .addHeader("Connection", "close")
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "on failure ");
                call.cancel();
                finished = true;
            }

            @Override
            public void onResponse(Call call, final Response response) {
                try {
                    success = true;
                    res = new JSONObject(Objects.requireNonNull(response.body()).string());
                    finished = true;
                    Log.d(TAG, "on response success ");
                } catch (IOException | JSONException e) {
                    Log.d(TAG, "on response failed " + e.toString());
                    success = false;
                    finished = true;
                }
            }
        });
    }
}
