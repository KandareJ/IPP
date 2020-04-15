package com.ripceipt.Processor.Uploader;

import java.io.*;
import java.util.Base64;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;

public class Uploader {
    public String upload(byte[] toSend) {
        String endpoint = "https://s5pdvll4hf.execute-api.us-west-2.amazonaws.com/RipceiptDev/upload";
        UploaderRequest req = new UploaderRequest();
        req.toUpload = "data:image/png;base64," + Base64.getEncoder().encodeToString(toSend);


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

            return g.fromJson(readString(connection.getInputStream()), UploadResponse.class).url;
        }
        catch (Exception e) {
            return null;
        }
    }

    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }

    private String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

}
