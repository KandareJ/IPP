package com.ripceipt.printer.Data;

import com.ripceipt.printer.Data.Attribute;
import com.ripceipt.printer.Exceptions.IPPParseException;

import java.util.ArrayList;
import java.util.List;

public class AttributeGroup {

    private byte attributesType;
    private List<Attribute> attributes;

    public AttributeGroup(byte attributesType, List<Attribute> attributes) {
        this.attributesType = attributesType;
        this.attributes = attributes;
    }

    public String getAttributesTypeString() {
        String toRet = new String();

        switch(attributesType) {
            case 0x00:
                toRet = "reserved";
                break;
            case 0x01:
                toRet = "operation-attributes-tag";
                break;
            case 0x02:
                toRet = "job-attributes-tag";
                break;
            case 0x03:
                toRet = "Well someone f-ed up their decoder. This value should not be 3";
                break;
            case 0x04:
                toRet = "printer-attributes-tag";
                break;
            case 0x05:
                toRet = "unsupported-attributes-tag";
                break;
            default:
                toRet = "man you f-ed up. IDK what you did wrong to get that as your attribute type";
        }

        return toRet;
    }

    public short getAttributesType() {
        return attributesType;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }


    public void setAttributesType(byte attributesType) {
        this.attributesType = attributesType;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public List<Byte> toBytes() throws IPPParseException {
        List<Byte> toRet = new ArrayList<>();
        toRet.add(attributesType);
        for (Attribute a : attributes) {
            toRet.addAll(a.toBytes());
        }
        return toRet;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if(attributes == null) return "error building attr group string";

        sb.append("\t" + getAttributesTypeString() + ":\n");
        for(int i = 0; i < attributes.size(); i++) {
            sb.append("\t\t" + attributes.get(i).attrString() + "\n");
        }

        return sb.toString();
    }
}
