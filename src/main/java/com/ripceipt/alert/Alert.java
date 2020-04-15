package com.ripceipt.alert;

import java.net.HttpURLConnection;
import java.net.URL;

public class Alert {
    public void alertPico() {
        try {
            URL url = new URL("http://");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}


/*
 String endpoint = "http://" + host + ":" + port + "/user/login";

        Gson g = new Gson();

        try {
            URL url = new URL(endpoint);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", "");
            connection.setRequestMethod("POST");
            writeString(g.toJson(req), connection.getOutputStream());
            connection.connect();

            return g.fromJson(readString(connection.getInputStream()), LoginResult.class);

        }
        catch (Exception e) {
            return null;
        }
 */