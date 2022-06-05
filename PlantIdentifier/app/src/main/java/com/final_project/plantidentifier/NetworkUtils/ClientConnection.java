package com.final_project.plantidentifier.NetworkUtils;

public interface ClientConnection {

//    String ipOnly = "plant.uaenorth.cloudapp.azure.com";//webapp plantidentifiere.azurewebsites.net/ //"192.168.1.115"; //"77.125.87.216"
    String ipOnly = "77.125.86.145"; // "192.168.1.115";// "77.125.86.145"
    String ipOnlyBackup = "plant.uaenorth.cloudapp.azure.com";
    String ipString = "http://" + ipOnly + ":5000/";// "http://192.168.1.61:5000/"; //"http://77.125.87.0:5000/";//"147.161.13.194";
    String ipStringBackup = "http://" + ipOnlyBackup + ":5000/";
//192.168.1.113
    //http://10.0.2.2:5000/

    int port = 5000;

    public void connect();
}
