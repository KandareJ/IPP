package com.ripceipt.printer.Data;

import com.ripceipt.printer.Data.AttributeGroup;
import com.ripceipt.printer.Exceptions.IPPParseException;

import java.util.ArrayList;
import java.util.List;

public class RequestResponse {
    private short operationID;
    private byte versionMajor;
    private byte versionMinor;
    private int requestID;
    private List<AttributeGroup> groups;
    private byte[] body;

    public RequestResponse() {
        body = new byte[] {};
        groups = new ArrayList<>();
    }

    public void addGroup(AttributeGroup toAdd) {
        this.groups.add(toAdd);
    }

    public byte getVersionMajor() {
        return versionMajor;
    }

    public byte getVersionMinor() {
        return versionMinor;
    }

    public byte[] getBody() {
        return body;
    }

    public List<Byte> attributeGroupBytes() throws IPPParseException {
        List<Byte> toRet = new ArrayList<>();
        for (AttributeGroup ag : groups) {
            toRet.addAll(ag.toBytes());
        }
        return toRet;
    }

    public String getOperation () {
        String toRet = new String();

        switch(operationID) {
            case 2:
                toRet = "print-job";
                break;
            case 4:
                toRet = "validate-job";
                break;
            case 8:
                toRet = "cancel-job";
                break;
            case 9:
                toRet = "get-job-attributes";
                break;
            case 10:
                toRet = "get-jobs";
                break;
            case 11:
                toRet = "get-printer-attributes";
                break;
            default:
                toRet = "" + operationID;
        }

        return toRet;
    }

    private short getStatusCode() {
        return operationID;
    }

    public short getOperationID() {
        return operationID;
    }

    public String getVersion() {
        return (short)versionMajor + "." + (short)versionMinor;
    }

    public int getRequestID() {
        return requestID;
    }

    public String requestToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Request received:\n");
        sb.append("\tid: " + getRequestID());
        sb.append("\n\tversion: " + getVersion());
        sb.append("\n\toperation: " + getOperation() + "\n");

        for (AttributeGroup g : groups) {
            sb.append(g.toString());
        }

        return sb.toString();
    }

    public String responseToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Proposed response:\n");
        sb.append("\tid: " + getRequestID());
        sb.append("\n\tversion: " + getVersion());
        sb.append("\n\tstatus code: " + getStatusCode() + "\n");

        for (AttributeGroup g : groups) {
            sb.append(g.toString());
        }


        return sb.toString();
    }

    public void setOperationID(short operationID) {
        this.operationID = operationID;
    }

    public void setVersionMajor(byte versionMajor) {
        this.versionMajor = versionMajor;
    }

    public void setVersionMinor(byte versionMinor) {
        this.versionMinor = versionMinor;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public List<AttributeGroup> getGroups() {
        return groups;
    }
}
