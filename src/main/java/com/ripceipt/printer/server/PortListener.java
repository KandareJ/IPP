package com.ripceipt.printer.server;

import com.ripceipt.printer.Data.RequestResponse;
import com.ripceipt.printer.Exceptions.IPPParseException;
import com.ripceipt.printer.ipp.*;
import com.ripceipt.printer.operations.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class PortListener implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        ValidateJob validateJob = new ValidateJob();
        PrintJob printJob = new PrintJob();
        GetPrinterAttributes getPrinterAttributes = new GetPrinterAttributes();
        GetJobs getJobs = new GetJobs();
        GetJobAttributes getJobAttributes = new GetJobAttributes();
        CancelJob cancelJob = new CancelJob();

        try {
            InputStream reqBody = exchange.getRequestBody();
            Headers reqHead = exchange.getRequestHeaders();
            String host = reqHead.get("Host").get(0).substring(0, reqHead.get("Host").get(0).indexOf(':'));
            Printer p = Printer.getPrinter();
            p.setHost(host);

            if (exchange.getRequestMethod().toLowerCase().equals("post") && reqHead.containsKey("Content-type") && reqHead.get("Content-type").get(0).equals("application/ipp")) {
                IPPDecoder decoder = new IPPDecoder(reqBody);
                IPPEncoder encoder = new IPPEncoder();

                RequestResponse decoded = decoder.decode();
                System.out.println(decoded.requestToString());

                switch(decoded.getOperationID()) {
                    case 0x02://print-job
                        sendGoodResponse(exchange, encoder.encode(printJob.execute(decoded)));
                        break;
                    case 0x04://validate-job
                        sendGoodResponse(exchange, encoder.encode(validateJob.execute(decoded)));
                        break;
                    case 0x08: //cancel-job
                        sendGoodResponse(exchange, encoder.encode(cancelJob.execute(decoded)));
                        break;
                    case 0x09://get-job-attributes
                        sendGoodResponse(exchange, encoder.encode(getJobAttributes.execute(decoded)));
                        break;
                    case 0x0a: // get-jobs
                        sendGoodResponse(exchange, encoder.encode(getJobs.execute(decoded)));
                        break;
                    case 0x0b: // get-printer-attributes
                        sendGoodResponse(exchange, encoder.encode(getPrinterAttributes.execute(decoded)));
                        break;

                }
            }

        }
        catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
            e.printStackTrace();
        }
        catch (IPPParseException e) {
            System.out.println("IPP Parse Error: " + e.getMessage());
        }
    }

    private void sendGoodResponse(HttpExchange exchange, List<Byte> body) throws IOException {
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        exchange.getResponseHeaders().set("Content-type", "application/ipp");
        OutputStream os = exchange.getResponseBody();

        for(int i = 0; i < body.size(); i++) {
            os.write(body.get(i));
        }

        os.close();
    }

}