package com.ripceipt.printer.Data;

import com.ripceipt.printer.Exceptions.IPPParseException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Attribute {

    private String name;
    private List<String> value;
    private List<Byte> valueTypes;

    public List<String> getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public Attribute(byte[] nameData, byte type, byte[] valueData) throws IPPParseException {
        valueTypes = new ArrayList<>();
        value = new ArrayList<>();
        convertName(nameData);
        storeType(type);
        storeValue(valueData);
    }

    public void addValue(byte[] valueData, byte type) throws IPPParseException {
        if(valueData.length < 1) throw new IPPParseException("Attempted to add attribute value with no data");
        else {
            storeType(type);
            storeValue(valueData);
        }
    }

    private void convertName(byte[] nameData) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < nameData.length; i++) {
            sb.append((char)nameData[i]);
        }
        name = sb.toString();
    }

    private void storeType(byte type) {
        valueTypes.add(type);
    }

    private void storeValue(byte[] valueData) throws IPPParseException {
        byte lastType = valueTypes.get(valueTypes.size() - 1);
        StringBuilder sb = new StringBuilder();

        if(lastType >= 0x30 && lastType <= 0x5f) {
            for(int i = 0; i < valueData.length; i++) {
                sb.append((char)valueData[i]);
            }
            value.add(sb.toString());
        }
        else if (lastType == 0x10) {
            value.add("unsupported type");
        }
        else if (lastType == 0x12) {
            value.add("unknown type");
        }
        else if (lastType == 0x13) {
            value.add("no-value");
        }
        else if (lastType == 0x21 || lastType == 0x23 || (lastType >= 0x24 && lastType <= 0x2f)) {
            int val = 0;
            if (valueData.length != 4) throw new IPPParseException("data type integer not 4 bytes");
            val = valueData[0] << 24 | (valueData[1] & 0xff) << 16 | (valueData[2] & 0xff) << 8 | (valueData[3] & 0xff);
            value.add(Integer.toString(val));
        }
        else if (lastType == 0x22) {
            int val = 0;
            if(valueData[0] == (byte)0x01) value.add("true");
            else value.add("false");
        }
        else {
            value.add("Unrecognized type: " + lastType);
        }
    }

    public String attrString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(" : [ ");

        for (int i = 0; i < value.size(); i++) {
            sb.append(value.get(i));
            if(i != value.size() - 1) sb.append(", ");
        }

        sb.append(" ]");

        return sb.toString();
    }

    public List<Byte> toBytes () throws IPPParseException {
        List<Byte> attrData = new ArrayList<>();
        int nameLength = name.length();
        int valueLength = value.get(0).length();

        attrData.add(valueTypes.get(0));
        attrData.add((byte) ((nameLength >> 8) & 0xFF));
        attrData.add((byte) (nameLength & 0xFF));

        for(int i = 0; i < nameLength; i++) {
            attrData.add((byte)name.charAt(i));
        }

        if (valueTypes.get(0) >= (byte) 0x30 && valueTypes.get(0) <= (byte) 0x5f) {
            attrData.add((byte) ((valueLength >> 8) & 0xFF));
            attrData.add((byte) (valueLength & 0xFF));

            for(int i = 0; i < valueLength; i++) {
                attrData.add((byte)value.get(0).charAt(i));
            }
        }
        else if (valueTypes.get(0) == (byte)0x22) {
            attrData.add((byte) 0x00);
            attrData.add((byte) 0x01);

            if(value.get(0).equals("true")) attrData.add((byte) 0x01);
            else attrData.add((byte) 0x00);
        }
        else if (valueTypes.get(0) >= (byte) 0x20 && valueTypes.get(0) <= (byte) 0x2f) {
            attrData.add((byte) 0x00);
            attrData.add((byte) 0x04);

            byte[] integer = ByteBuffer.allocate(4).putInt(Integer.parseInt(value.get(0))).array();

            for(int j = 0; j < integer.length; j++) {
                attrData.add(integer[j]);
            }
        }


        for(int numAttrs = 1; numAttrs < value.size(); numAttrs++) {
            byte type = valueTypes.get(numAttrs);
            valueLength = value.get(numAttrs).length();
            attrData.add(type);
            //no name
            attrData.add((byte)0);
            attrData.add((byte)0);


            if (valueTypes.get(numAttrs) >= (byte) 0x30 && valueTypes.get(numAttrs) <= (byte) 0x5f) {
                attrData.add((byte) ((valueLength >> 8) & 0xFF));
                attrData.add((byte) (valueLength & 0xFF));

                for (int i = 0; i < valueLength; i++) {
                    attrData.add((byte) value.get(numAttrs).charAt(i));
                }
            }
            else if (valueTypes.get(numAttrs) == (byte)0x22) {
                attrData.add((byte) 0x00);
                attrData.add((byte) 0x01);

                if(value.get(numAttrs).equals("true")) attrData.add((byte) 0x01);
                else attrData.add((byte) 0x00);
            }
            else if (valueTypes.get(numAttrs) >= (byte) 0x20 && valueTypes.get(numAttrs) <= (byte) 0x2f) {
                attrData.add((byte) 0x00);
                attrData.add((byte) 0x04);

                byte[] integer = ByteBuffer.allocate(4).putInt(Integer.parseInt(value.get(numAttrs))).array();

                for(int j = 0; j < integer.length; j++) {
                    attrData.add(integer[j]);
                }
            }


        }

        return attrData;
    }

    public boolean attributeIsSupported(String attribute) {
        for (String s : value) {
            if (s.equals(attribute)) return true;
        }
        System.out.println("ATTR NOT SUPPORTED: " + attribute);

        return false;
    }

    /*
    //encode value (int)
            if(type == 0x21 || type == 0x23 || type == 0x24 || type == 0x2f) {
                int intToParse = Integer.parseInt(value.get(numAttrs));
                if(valueLength != 4) throw new IPPParseException("Encoding error: integer is not 4 bytes");
                attrData.add((byte) ((intToParse >> 24) & 0xFF));
                attrData.add((byte) ((intToParse >> 16) & 0xFF));
                attrData.add((byte) ((intToParse >> 8) & 0xFF));
                attrData.add((byte) (intToParse & 0xFF));
            }
            // (string)
            else {
     */


}
