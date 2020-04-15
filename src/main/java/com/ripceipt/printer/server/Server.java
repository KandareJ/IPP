package com.ripceipt.printer.server;

import java.io.*;
import java.net.*;

import com.sun.net.httpserver.*;

public class Server {

    private static final int MAX_WAITING_CONNECTIONS = 12;
    private HttpServer server;

    private void run(String portNumber) {
        try {
            server = HttpServer.create(
                    new InetSocketAddress(Integer.parseInt(portNumber)),
                    MAX_WAITING_CONNECTIONS);
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }

        server.setExecutor(null);
        server.createContext("/", new PortListener());
        server.start();
        System.out.println("Http server started on " + portNumber);
    }

    public static void main(String[] args) {
        String portNumber = "8383";
        new Server().run(portNumber);
    }
}
