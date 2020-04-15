package com.ripceipt.printer.ipp;

import com.ripceipt.printer.Data.Attribute;
import com.ripceipt.printer.Exceptions.IPPParseException;

import java.nio.ByteBuffer;
import java.util.*;

public class Printer {
    private static Printer instance;

    private Map<String, Attribute> attrs1_1;
    private Map<String, Attribute> attrs2_0;
    private String host;
    private boolean printerAcceptingJobs;
    private int printerState;
    private String printerStateReasons;
    private long uptime;
    private int queuedJobCount;

    public static Printer getPrinter() {
        if (instance == null) {
            instance = new Printer();
        }
        return instance;
    }


    public Printer() {
        try {
            initAttributes11();
            initAttributes20();
            initVariableAttributes();
        }
        catch (IPPParseException e) {
            e.printStackTrace();
            System.out.println("Be a better programmer bro");
        }
    }

    private String getHost() {
        if(host != null) {
            return host;
        }
        else {
            return "localhost";
        }
    }

    public void setHost(String host) {
        try {
            this.host = host;
            initAttributes11();
        }
        catch (IPPParseException e) {
            e.printStackTrace();
            System.out.println("Be a better programmer bro");
        }
    }

    private void initVariableAttributes() throws IPPParseException {
        uptime = new Date().getTime();
        printerState = 3;
        printerAcceptingJobs = true;
        printerStateReasons = "none";
        queuedJobCount = 0;
    }

    public int integerUpTime() {
        return (int) (new Date().getTime() - uptime);
    }

    private Attribute getUptime() throws IPPParseException {
        byte[] integer = ByteBuffer.allocate(4).putInt((int) (new Date().getTime() - uptime)).array();
        return new Attribute("printer-up-time".getBytes(), IPPConstants.INTEGER, integer);
    }

    private Attribute getPrinterIsAcceptingJobs() throws IPPParseException {
        if(printerAcceptingJobs) return new Attribute("printer-is-accepting-jobs".getBytes(), IPPConstants.BOOLEAN, IPPConstants.TRUE);
        else return new Attribute("printer-is-accepting-jobs".getBytes(), IPPConstants.BOOLEAN, IPPConstants.FALSE);
    }

    private Attribute getPrinterState() throws IPPParseException {
        //3-idle 4-processing 5-stopped
        byte[] integer = ByteBuffer.allocate(4).putInt(printerState).array();
        return new Attribute("printer-state".getBytes(), (byte) 0x23, integer);
    }

    private Attribute getPrinterStateReasons() throws IPPParseException {
        //other,none,media-needed,media-jam,moving-to-paused,paused,shutdown,connecting-to-device,timed-out,stopping
        //stopped-partly,toner-low,toner-empty,spool-area-full,cover-open,interlock-open,door-open,input-tray-missing,media-low,media-empty
        //output-tray-missing,output-area-almost-full,output-area-full,marker-supply-low,marker-supply-empty,marker-waste-almost-full,marker-waste-full
        //fuser-over-temp,fuser-under-temp,opc-near-eol,opc-life-over,developer-low,developer-empty,interpreter-resource-unavailable
        return new Attribute("printer-state-reasons".getBytes(), (byte) 0x44, printerStateReasons.getBytes());
    }

    private Attribute getQueuedJobCount() throws IPPParseException {
        byte[] integer = ByteBuffer.allocate(4).putInt(queuedJobCount).array();
        return new Attribute("queued-job-count".getBytes(), (byte) 0x21, integer);
    }

    public Map<String, Attribute> get1_1Attributes() throws IPPParseException {
        attrs1_1.put("printer-up-time", getUptime());
        attrs1_1.put("printer-is-accepting-jobs", getPrinterIsAcceptingJobs());
        attrs1_1.put("printer-state", getPrinterState());
        attrs1_1.put("printer-state-reasons", getPrinterStateReasons());
        attrs1_1.put("queued-job-count", getQueuedJobCount());

        return attrs1_1;
    }

    public Map<String, Attribute> get2_0Attributes() throws IPPParseException {
        Map<String, Attribute> toRet = new HashMap<>();
        
        attrs1_1.put("printer-up-time", getUptime());
        attrs1_1.put("printer-is-accepting-jobs", getPrinterIsAcceptingJobs());
        attrs1_1.put("printer-state", getPrinterState());
        attrs1_1.put("printer-state-reasons", getPrinterStateReasons());
        attrs1_1.put("queued-job-count", getQueuedJobCount());
        
        toRet.putAll(attrs1_1);
        toRet.putAll(attrs2_0);

        return toRet;
    }

    public List<Attribute> getOperationAttributes() throws IPPParseException {
        List<Attribute> toRet = new ArrayList<>();

        toRet.add(new Attribute("attributes-charset".getBytes(), IPPConstants.CHARSET, "utf-8".getBytes()));
        toRet.add(new Attribute("attributes-natural-language".getBytes(), IPPConstants.NATURAL_LANGUAGE, "en-us".getBytes()));
        return toRet;
    }

    private void initAttributes11() throws IPPParseException {
        Attribute ippVersionsSupported = new Attribute("ipp-versions-supported".getBytes(), IPPConstants.KEYWORD, "1.1".getBytes());
        ippVersionsSupported.addValue("2.0".getBytes(), IPPConstants.KEYWORD);
        Attribute operationsSupported = new Attribute("operations-supported".getBytes(), IPPConstants.ENUM, new byte[] {0x00, 0x00, 0x00, 0x02});
        operationsSupported.addValue(new byte[] {0x00, 0x00, 0x00, 0x04}, IPPConstants.ENUM);
        operationsSupported.addValue(new byte[] {0x00, 0x00, 0x00, 0x08}, IPPConstants.ENUM);
        operationsSupported.addValue(new byte[] {0x00, 0x00, 0x00, 0x09}, IPPConstants.ENUM);
        operationsSupported.addValue(new byte[] {0x00, 0x00, 0x00, 0x0a}, IPPConstants.ENUM);
        operationsSupported.addValue(new byte[] {0x00, 0x00, 0x00, 0x0b}, IPPConstants.ENUM);
        Attribute generatedNaturalLanguageSupported = new Attribute("generated-natural-language-supported".getBytes(), IPPConstants.NATURAL_LANGUAGE, "en".getBytes());
        generatedNaturalLanguageSupported.addValue("en-us".getBytes(), IPPConstants.NATURAL_LANGUAGE);



        Attribute printerURISupported = new Attribute("printer-uri-supported".getBytes(), IPPConstants.URI, ("ipp://" + getHost() + "/").getBytes());
        printerURISupported.addValue(("ipp://" + getHost() + ":8383/").getBytes(), IPPConstants.URI);

        attrs1_1 = new HashMap<>();
        attrs1_1.put("color-supported", new Attribute("color-supported".getBytes(), IPPConstants.BOOLEAN, IPPConstants.FALSE ));
        attrs1_1.put("charset-configured", new Attribute("charset-configured".getBytes(), IPPConstants.CHARSET, "utf-8".getBytes()));
        attrs1_1.put("charset-supported", new Attribute("charset-supported".getBytes(), IPPConstants.CHARSET, "utf-8".getBytes()));
        attrs1_1.put("compression-supported", new Attribute("compression-supported".getBytes(), IPPConstants.KEYWORD, "none".getBytes()));
        attrs1_1.put("document-format-default", new Attribute("document-format-default".getBytes(), IPPConstants.MIME_MEDIA_TYPE, "application/pdf".getBytes()));
        attrs1_1.put("document-format-supported", new Attribute("document-format-supported".getBytes(), IPPConstants.MIME_MEDIA_TYPE, "application/pdf".getBytes()));
        attrs1_1.put("generated-natural-language-supported", generatedNaturalLanguageSupported);
        attrs1_1.put("ipp-versions-supported", ippVersionsSupported);
        attrs1_1.put("natural-language-configured", new Attribute("natural-language-configured".getBytes(), IPPConstants.NATURAL_LANGUAGE, "en-us".getBytes()));
        attrs1_1.put("operations-supported", operationsSupported);
        attrs1_1.put("pdl-override-supported", new Attribute("pdl-override-supported".getBytes(), IPPConstants.KEYWORD, "not-attempted".getBytes()));
        attrs1_1.put("printer-name", new Attribute("printer-name".getBytes(), IPPConstants.NAME_WITHOUT_LANGUAGE, "ripceipt".getBytes()));
        attrs1_1.put("printer-uri-supported", printerURISupported);
        attrs1_1.put("uri-authentication-supported", new Attribute("uri-authentication-supported".getBytes(), IPPConstants.KEYWORD, "none".getBytes()));
        attrs1_1.put("uri-security-supported", new Attribute("uri-security-supported".getBytes(), IPPConstants.KEYWORD, "none".getBytes()));

    }


    private void initAttributes20() throws IPPParseException {
        Attribute jobCreationAttributesSupported = new Attribute("job-creation-attributes-supported".getBytes(), IPPConstants.KEYWORD, "media".getBytes());
        jobCreationAttributesSupported.addValue("media-col".getBytes(), IPPConstants.KEYWORD);
        jobCreationAttributesSupported.addValue("printer-quality".getBytes(), IPPConstants.KEYWORD);
        Attribute printQualityDefault = new Attribute("print-quality-default".getBytes(), IPPConstants.ENUM, IPPConstants.intToBytes(3));
        printQualityDefault.addValue(IPPConstants.intToBytes(4), IPPConstants.ENUM);
        printQualityDefault.addValue(IPPConstants.intToBytes(5), IPPConstants.ENUM);
        Attribute printQualitySupported = new Attribute("print-quality-supported".getBytes(), IPPConstants.ENUM, IPPConstants.intToBytes(3));
        printQualitySupported.addValue(IPPConstants.intToBytes(4), IPPConstants.ENUM);
        printQualitySupported.addValue(IPPConstants.intToBytes(5), IPPConstants.ENUM);

        attrs2_0 = new HashMap<>();
        attrs2_0.put("copies-default", new Attribute("copies-default".getBytes(), IPPConstants.INTEGER, IPPConstants.intToBytes(1) ));
        attrs2_0.put("copies-supported", new Attribute("copies-supported".getBytes(), IPPConstants.INTEGER, IPPConstants.intToBytes(5) ));
        attrs2_0.put("finishings-default", new Attribute("finishings-default".getBytes(), IPPConstants.ENUM, IPPConstants.intToBytes(3) ));
        attrs2_0.put("finishings-supported", new Attribute("finishings-supported".getBytes(), IPPConstants.ENUM, IPPConstants.intToBytes(3) ));
        attrs2_0.put("job-creation-attributes-supported", jobCreationAttributesSupported);
        attrs2_0.put("media-default", new Attribute("media-default".getBytes(), IPPConstants.KEYWORD, "paper".getBytes() ));
        attrs2_0.put("media-supported", new Attribute("media-supported".getBytes(), IPPConstants.KEYWORD, "paper".getBytes() ));
        attrs2_0.put("media-ready", new Attribute("media-ready".getBytes(), IPPConstants.KEYWORD, "paper".getBytes() ));
        attrs2_0.put("orientation-requested-default", new Attribute("orientation-requested-default".getBytes(), IPPConstants.ENUM, IPPConstants.intToBytes(3) ));
        attrs2_0.put("orientation-requested-supported", new Attribute("orientation-requested-supported".getBytes(), IPPConstants.ENUM, IPPConstants.intToBytes(3) ));
        attrs2_0.put("output-bin-default", new Attribute("output-bin-default".getBytes(), IPPConstants.KEYWORD, "tray-1".getBytes() ));
        attrs2_0.put("output-bin-supported", new Attribute("output-bin-supported".getBytes(), IPPConstants.KEYWORD, "tray-1".getBytes() ));
        attrs2_0.put("pages-per-minute", new Attribute("pages-per-minute".getBytes(), IPPConstants.KEYWORD, IPPConstants.intToBytes(60) ));
        attrs2_0.put("printer-quality-default", printQualityDefault);
        attrs2_0.put("printer-quality-supported", printQualitySupported);
        attrs2_0.put("printer-info", new Attribute("printer-info".getBytes(), IPPConstants.TEXT_WITHOUT_LANGUAGE, "This printer was made by Jace".getBytes()));
        attrs2_0.put("printer-location", new Attribute("printer-location".getBytes(), IPPConstants.TEXT_WITHOUT_LANGUAGE, "The desk".getBytes()));
        attrs2_0.put("printer-make-and-model", new Attribute("printer-make-and-model".getBytes(), IPPConstants.TEXT_WITHOUT_LANGUAGE, "ripceipt 0.1".getBytes()));
        attrs2_0.put("printer-more-info", new Attribute("printer-more-info".getBytes(), IPPConstants.URI, "https://www.ripceipt.com/".getBytes()));
        attrs2_0.put("printer-resolution-default", new Attribute("printer-resolution-default".getBytes(), IPPConstants.RESOLUTION, new byte[] {0x00, 0x00, 0x01, 0x2C, 0x00, 0x00, 0x02, 0x58, 0x03}));
        attrs2_0.put("printer-resolution-supported", new Attribute("printer-resolution-supported".getBytes(), IPPConstants.RESOLUTION, new byte[] {0x00, 0x00, 0x01, 0x2C, 0x00, 0x00, 0x02, 0x58, 0x03}));
        attrs2_0.put("sides-default", new Attribute("sides-default".getBytes(), IPPConstants.KEYWORD, "one-sided".getBytes()));
        attrs2_0.put("sides-supported", new Attribute("sides-supported".getBytes(), IPPConstants.KEYWORD, "one-sided".getBytes()));
    }



}
