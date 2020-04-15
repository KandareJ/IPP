package com.ripceipt.printer.operations;

import com.ripceipt.printer.Data.Attribute;
import com.ripceipt.printer.Data.AttributeGroup;
import com.ripceipt.printer.Data.RequestResponse;
import com.ripceipt.printer.Exceptions.IPPParseException;
import com.ripceipt.printer.Exceptions.UnsupportedVersion;
import com.ripceipt.printer.ipp.JobQueue;
import com.ripceipt.printer.ipp.Printer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CancelJob {
    public CancelJob() {

    }

    public RequestResponse execute(RequestResponse request) throws IPPParseException {
        RequestResponse response = new RequestResponse();

        try {

            if (request.getVersion().equals("1.1") || request.getVersion().equals("2.0")) {
                List<AttributeGroup> requestGroups = request.getGroups();
                Printer p = Printer.getPrinter();
                List<String> unsupportedRequested = new ArrayList<>();
                List<Attribute> unsupported = new ArrayList<>();
                List<Attribute> supported = new ArrayList<>();
                Map<String, Attribute> printerAttributes;
                List<String> requestedAttributes = new ArrayList<>();
                short statusCode = 0x0000;

                if(request.getVersion().equals("1.1")) printerAttributes = p.get1_1Attributes();
                else printerAttributes = p.get2_0Attributes();

                for(AttributeGroup ag : requestGroups) {//TODO check that they give us the correct job ID or job url
                    for(Attribute a : ag.getAttributes()) {
                        switch(a.getName()) {
                            case "attributes-natural-language":
                                if(!printerAttributes.get("generated-natural-language-supported").attributeIsSupported(a.getValue().get(0))) {
                                    System.out.println("unsupported language...");//TODO maybe implement the language override...
                                }
                                break;
                            case "attributes-charset":
                                if (!printerAttributes.get("charset-supported").attributeIsSupported(a.getValue().get(0))) {
                                    unsupported.add(a);
                                    statusCode = 0x040D;
                                }
                                break;
                            case "printer-uri":
                                if (!printerAttributes.get("printer-uri-supported").attributeIsSupported(a.getValue().get(0))) {
                                    statusCode = 0x040C;
                                }
                                break;
                            case "requesting-user-name":
                                break;


                        }
                    }
                }


                response.setVersionMajor(request.getVersionMajor());
                response.setVersionMinor(request.getVersionMinor());
                response.setRequestID(request.getRequestID());
                response.addGroup(new AttributeGroup((byte)0x01, p.getOperationAttributes()));


                if(unsupported.size() > 0)  {
                    response.addGroup(new AttributeGroup((byte)0x05, unsupported));
                    response.setOperationID(statusCode);
                }
                else {
                    response.setOperationID((short)0x00);
                }

                JobQueue.getJobQueue().cancelJob();

            }
            else throw new UnsupportedVersion("Version " + request.getVersion() + " is not supported");


            System.out.println(response.responseToString());
        }
        catch (UnsupportedVersion e) {
            response.setRequestID(request.getRequestID());
            response.setOperationID((short) 0x0503);
            response.setVersionMajor(request.getVersionMajor());
            response.setVersionMinor(request.getVersionMinor());
            System.out.println(e.getMessage());
        }

        return response;
    }
}
