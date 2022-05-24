package com.example.plantidentifier.NetworkUtils;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public interface ClientConnection {

//    String ipOnly = "plant.uaenorth.cloudapp.azure.com";//webapp plantidentifiere.azurewebsites.net/ //"192.168.1.115"; //"77.125.87.216"
    String ipOnly = "192.168.1.115";
    String ipString = "http://" + ipOnly + ":5000/";// "http://192.168.1.61:5000/"; //"http://77.125.87.0:5000/";//"147.161.13.194";
//192.168.1.113
    //http://10.0.2.2:5000/

    int port = 5000;

    public void connect();
}
