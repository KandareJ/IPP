package com.ripceipt.printer.operations;

import com.ripceipt.Processor.Converter;
import com.ripceipt.Processor.Uploader.Uploader;
import com.ripceipt.nfc.PythonProcess;
import com.ripceipt.printer.Data.Attribute;
import com.ripceipt.printer.Data.AttributeGroup;
import com.ripceipt.printer.Data.Job;
import com.ripceipt.printer.Exceptions.CompressionNotSupportedException;
import com.ripceipt.printer.Exceptions.IPPParseException;
import com.ripceipt.printer.Exceptions.UnsupportedVersion;
import com.ripceipt.printer.ipp.JobQueue;
import com.ripceipt.printer.ipp.Printer;
import com.ripceipt.printer.Data.RequestResponse;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrintJob {
    public PrintJob() {

    }

    public RequestResponse execute (RequestResponse request) throws IPPParseException {
        RequestResponse response = new RequestResponse();
        try {
            if (request.getVersion().equals("1.1") || request.getVersion().equals("2.0")) {
                Printer p = Printer.getPrinter();
                JobQueue jobs = JobQueue.getJobQueue();
                Map<String, Attribute> printerAttributes;
                if(request.getVersion().equals("1.1")) printerAttributes = p.get1_1Attributes();
                else printerAttributes = p.get2_0Attributes();
                List<Attribute> unsupported = new ArrayList<>();
                List<Attribute> unsupportedTemplate = new ArrayList<>();
                List<AttributeGroup> requestGroups = request.getGroups();
                short statusCode = 0x0000;
                boolean ippFidelity = false;

                int jobID = jobs.getNextID();
                byte[] jobIDBytes = ByteBuffer.allocate(4).putInt(jobID).array();
                String jobURI = "localhost:8383/" + jobID;
                List<Attribute> jobDescriptionAttrs = new ArrayList<>();
                jobDescriptionAttrs.add(new Attribute("job-id".getBytes(), (byte) 0x21, jobIDBytes));
                jobDescriptionAttrs.add(new Attribute("job-uri".getBytes(), (byte) 0x45, jobURI.getBytes()));

                List<Attribute> jobTemplateAttrs = new ArrayList<>();

                for(AttributeGroup ag : requestGroups) {
                    switch (ag.getAttributesType()) {
                        case 0x01:
                            for (Attribute a : ag.getAttributes()) {
                                switch (a.getName()) {
                                    case "attributes-natural-language":
                                        if (!printerAttributes.get("generated-natural-language-supported").attributeIsSupported(a.getValue().get(0))) {
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
                                        jobDescriptionAttrs.add(new Attribute("job-originating-user-name".getBytes(), (byte) 0x42, a.getValue().get(0).getBytes()));
                                        break;
                                    case "job-name":
                                        jobDescriptionAttrs.add(a);
                                        break;
                                    case "ipp-attribute-fidelity":
                                        if(a.getValue().get(0).toLowerCase().equals("true")) ippFidelity = true;
                                        break;
                                    case "document-name":
                                        jobDescriptionAttrs.add(a);
                                        break;
                                    case "compression":
                                        if (!printerAttributes.get("compression-supported").attributeIsSupported(a.getValue().get(0))) {
                                            throw new CompressionNotSupportedException(a.getValue().get(0) + " compression is not supported");
                                        } else jobDescriptionAttrs.add(a);
                                        break;
                                    case "document-format":
                                        if (!printerAttributes.get("document-format-supported").attributeIsSupported(a.getValue().get(0))) {
                                            statusCode = 0x040A;
                                        } else jobDescriptionAttrs.add(a);
                                        break;

                                }
                            }
                            break;
                        case 0x02:
                            for (Attribute a  : ag.getAttributes()) {
                                if(printerAttributes.containsKey(a.getName()+"-supported") && printerAttributes.get(a.getName()+"-supported").attributeIsSupported(a.getValue().get(0))) {
                                    jobTemplateAttrs.add(a);
                                }
                                else {
                                    unsupportedTemplate.add(a);
                                }
                            }
                    }
                }

                if(unsupportedTemplate.size() > 0 && ippFidelity && statusCode == 0x0000) {
                    statusCode = 0x040B;
                    unsupported.addAll(unsupportedTemplate);

                }
                else if (unsupportedTemplate.size() > 0 && statusCode == 0x0000) {
                    statusCode = 0x0001;
                }

                response.setVersionMajor(request.getVersionMajor());
                response.setVersionMinor(request.getVersionMinor());
                response.setOperationID(statusCode);
                response.setRequestID(request.getRequestID());
                response.addGroup(new AttributeGroup((byte)0x01, p.getOperationAttributes()));

                if(unsupported.size() > 0) response.addGroup(new AttributeGroup((byte) 0x05, unsupported));

                response.addGroup(new AttributeGroup((byte) 0x02, jobDescriptionAttrs));

                jobs.addJob(new Job(jobURI, jobID, request.getBody(), jobTemplateAttrs, jobDescriptionAttrs));

                System.out.println("Data received");
                Converter c = new Converter();
                System.out.println("Data Converted");
                //Uploader u = new Uploader();
                //String url = "string url";//u.upload(c.convert(request.getBody()));
                PythonProcess pp = new PythonProcess();
                //pp.sendURL(url);
                pp.send(c.convert(request.getBody()));
                System.out.println("Data sent");

                jobs.finishJob();

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
        catch (CompressionNotSupportedException e) {
            response.setRequestID(request.getRequestID());
            response.setOperationID((short) 0x040F);
            response.setVersionMajor(request.getVersionMajor());
            response.setVersionMinor(request.getVersionMinor());
            System.out.println(e.getMessage());
        }

        return response;
    }



}
