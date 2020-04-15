package com.ripceipt.printer.ipp;

import com.ripceipt.printer.Data.RequestResponse;
import com.ripceipt.printer.Exceptions.IPPParseException;

import java.util.ArrayList;
import java.util.List;

public class IPPEncoder {

    List<Byte> data;
    RequestResponse reqresp;

    public IPPEncoder() {
        data = new ArrayList<>();
    }

    public List<Byte> encode(RequestResponse r) throws IPPParseException {
        reqresp = r;

        //encode the version numbers
        data.add(reqresp.getVersionMajor());
        data.add(reqresp.getVersionMinor());

        //encodes the operation id/status code
        data.add((byte) ((reqresp.getOperationID() >> 8) & 0xFF));
        data.add((byte) (reqresp.getOperationID() & 0xFF));

        //encodes the request ID
        data.add((byte) ((reqresp.getRequestID() >> 24) & 0xFF));
        data.add((byte) ((reqresp.getRequestID() >> 16) & 0xFF));
        data.add((byte) ((reqresp.getRequestID() >> 8) & 0xFF));
        data.add((byte) (reqresp.getRequestID() & 0xFF));

        //encodes attribute group
        data.addAll(reqresp.attributeGroupBytes());


        data.add((byte) 0x03);


        return data;
    }
}
