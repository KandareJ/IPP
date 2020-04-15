package com.ripceipt.printer.Data;

import com.ripceipt.printer.Exceptions.IPPParseException;
import com.ripceipt.printer.ipp.Printer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Job {
    
    public Job(String uri, int id, byte[] document, List<Attribute> template, List<Attribute> description) {
        this.uri = uri;
        this.id = id;
        state = 3;
        stateReasons = "job-queued";
        stateMessage = "We got this #Ripceipt2019";
        this.document = document;
        this.template = template;
        this.description = new HashMap<>();

        for(Attribute a : description) {
            this.description.put(a.getName(), a);
        }

        timeCreated = Printer.getPrinter().integerUpTime();
        timeFinished = -1;
    }

    private String uri;
    private int id;
    private int state;
    private String stateReasons;
    private String stateMessage;
    private byte[] document;
    private int timeCreated;
    private int timeFinished;
    List<Attribute> template;
    Map<String, Attribute> description;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        //3-pending 4-pending-held 5-processing 6-processing-stopped 7-cancelled 8-aborted 9-completed
        this.state = state;
    }

    public String getStateReasons() {
        return stateReasons;
    }

    public void setStateReasons(String stateReasons) {
        this.stateReasons = stateReasons;
    }

    public String getStateMessage() {
        return stateMessage;
    }

    public void setStateMessage(String stateMessage) {
        this.stateMessage = stateMessage;
    }

    public byte[] getDocument() {
        return document;
    }

    private Attribute getTimeCreated() throws IPPParseException {
        return new Attribute("time-at-creation".getBytes(), (byte) 0x21, ByteBuffer.allocate(4).putInt(timeCreated).array());
    }

    private Attribute getTimeProcessing() throws IPPParseException {
        return new Attribute("time-at-processing".getBytes(), (byte) 0x21, ByteBuffer.allocate(4).putInt(timeCreated + 1).array());
    }

    private Attribute getTimeFinished() throws IPPParseException {
        if(timeFinished != -1) return new Attribute("time-at-completed".getBytes(), (byte) 0x21, ByteBuffer.allocate(4).putInt(timeCreated + 1).array());
        else return new Attribute("time-at-completed".getBytes(), (byte) 0x13, new byte[]{});
    }

    private List<Attribute> getDescription() throws IPPParseException {
        description.put("job-uri", new Attribute("job-uri".getBytes(), (byte) 0x45, uri.getBytes()));
        description.put("job-id", new Attribute("job-id".getBytes(), (byte) 0x21, ByteBuffer.allocate(4).putInt(id).array()));
        description.put("job-printer-uri", new Attribute("job-printer-uri".getBytes(), (byte) 0x45, "ipp://localhost:8383/".getBytes()));
        if (!description.containsKey("job-name")) description.put("job-name", new Attribute("job-name".getBytes(), (byte) 0x42, ("job " + id).getBytes()));
        description.put("job-state", new Attribute("job-state".getBytes(), (byte) 0x23, ByteBuffer.allocate(4).putInt(state).array()));
        description.put("job-state-reasons", new Attribute("job-state-reasons".getBytes(), (byte) 0x44, stateReasons.getBytes()));
        description.put("time-at-creation", getTimeCreated());
        description.put("time-at-processing", getTimeProcessing());
        description.put("time-at-completed", getTimeFinished());


        return new ArrayList<>(description.values());
    }

    private List<Attribute> getTemplate() throws IPPParseException {
        return template;
    }

    public AttributeGroup toAttributeGroup(List<String> requested) throws IPPParseException {
        List<Attribute> allAttrs = getDescription();
        allAttrs.addAll(getTemplate());

        if (requested.size() == 0) return new AttributeGroup((byte)0x02, allAttrs);
        else {
            List<Attribute> requestedAttrs = new ArrayList<>();
            for (String s : requested) {
                for (Attribute a : allAttrs) {
                    if (s.equals(a.getName())) requestedAttrs.add(a);
                    else if (s.equals("job-description")) requestedAttrs.addAll(getDescription());
                    else if (s.equals("job-template")) requestedAttrs.addAll(getTemplate());
                    else if (s.equals("all")) {
                        return new AttributeGroup((byte)0x02, allAttrs);
                    }
                }
            }
            return new AttributeGroup((byte)0x02, requestedAttrs);
        }
    }
}

