package com.ripceipt.printer.ipp;

import com.ripceipt.printer.Data.Attribute;
import com.ripceipt.printer.Data.AttributeGroup;
import com.ripceipt.printer.Data.RequestResponse;
import com.ripceipt.printer.Exceptions.IPPParseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IPPDecoder {
    private byte[] body;
    private int index;
    private RequestResponse request;
    private List<Attribute> tempAttrList;

    public IPPDecoder(InputStream bodyRequest) throws IOException {
        this.body = toByteArray(bodyRequest);
        request = new RequestResponse();
        index = 0;

    }

    private byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;

        while ((length = in.read(buffer)) != -1) os.write(buffer, 0, length);

        return os.toByteArray();
    }

    public RequestResponse decode() throws IPPParseException {
        versionNumber();
        return request;

    }

    private void versionNumber() throws IPPParseException {
        if(index + 2 >= body.length) throw new IPPParseException("parse exception in version number");
        request.setVersionMajor(body[index]);
        index++;
        request.setVersionMinor(body[index]);
        index++;
        operationID();
    }

    private void operationID() throws IPPParseException {
        if(index + 2 >= body.length) throw new IPPParseException("parse exception in operation id");
        request.setOperationID((short) ((body[index] & 0xff) << 8 | (body[index+1] & 0xff)));
        index+=2;
        requestID();
    }

    private void requestID() throws IPPParseException {
        if(index + 4 >= body.length) throw new IPPParseException("parse exception in request id");
        request.setRequestID(body[index] << 24 | (body[index+1] & 0xff) << 16 | (body[index+2] & 0xff) << 8 | (body[index+3] & 0xff));
        index+=4;
        beginAttributeGroupTag();
    }

    private void beginAttributeGroupTag() throws IPPParseException {
        if(index + 2 >= body.length) throw new IPPParseException("parse exception in begin attribute group tag");
        if(body[index] == (byte)0x03) {
            endOfAttributesTag();
            return;
        }

        byte attributeGroupType = body[index];
        index++;

        if(isGroupTag(body[index])) beginAttributeGroupTag();
        else if(body[index] == (byte)0x03) endOfAttributesTag();
        else {
            tempAttrList = new ArrayList<>();
            valueTag();
            request.addGroup(new AttributeGroup(attributeGroupType, tempAttrList));
            tempAttrList = new ArrayList<>();
        }
    }

    private void valueTag() throws IPPParseException {
        if(index + 1 >= body.length) throw new IPPParseException("parse exception in attribute value tag");
        byte valueTag = body[index];
        index++;
        nameLength(valueTag);
    }

    private void nameLength(byte valueTag) throws IPPParseException {
        if(index + 2 >= body.length) throw new IPPParseException("parse exception in attribute name length");
        int nameLength = (body[index] & 0xff) << 8 | (body[index + 1] & 0xff);
        index+=2;
        name(valueTag, nameLength);
    }

    private void name(byte valueTag, int nameLength) throws IPPParseException {
        if(index + nameLength >= body.length) {
            System.out.println("name length: " + nameLength);
            System.out.println("value tag: " + valueTag);
            throw new IPPParseException("parse exception in attribute name");
        }
        byte[] nameData = Arrays.copyOfRange(body, index, index + nameLength);
        index += nameLength;
        valueLength(valueTag, nameData);
    }

    private void valueLength(byte valueTag, byte[] nameData) throws IPPParseException {
        if(index + 2 >= body.length) throw new IPPParseException("parse exception in attribute value length");
        int valueLength = (body[index] & 0xff) << 8 | (body[index + 1] & 0xff);
        index+=2;
        value(valueTag, nameData, valueLength);
    }

    private void value(byte valueTag, byte[] nameData, int valueLength) throws IPPParseException {
        if(index + valueLength >= body.length) throw new IPPParseException("parse exception in attribute value");
        byte[] valueData = Arrays.copyOfRange(body, index, index + valueLength);
        index += valueLength;

        if(nameData.length > 0) tempAttrList.add(new Attribute(nameData, valueTag, valueData));
        else tempAttrList.get(tempAttrList.size() - 1).addValue(valueData, valueTag);

        if(body[index] == 0x03) endOfAttributesTag();
        else if(isGroupTag(body[index])) beginAttributeGroupTag();
        else valueTag();
    }

    private void endOfAttributesTag() {
        index++;
        if(index < body.length) request.setBody(Arrays.copyOfRange(body, index, body.length-1));
        else request.setBody(new byte[] {0});
        return;
    }

    private boolean isGroupTag(byte tag) {
        switch (tag) {
            case 0x01:
                return true;
            case 0x02:
                return true;
            case 0x04:
                return true;
            case 0x05:
                return true;
            default:
                return false;
        }
    }
}
