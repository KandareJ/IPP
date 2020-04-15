package com.ripceipt.printer.ipp;

import java.nio.ByteBuffer;

public class IPPConstants {
    //delimiter tag values
    public static byte RESERVED = 0X00;
    public static byte OPERATION_ATTRIBUTES_TAG = 0X01;
    public static byte JOB_ATTRIBUTES_TAG = 0X02;
    public static byte END_OF_ATTRIBUTES_TAG = 0X03;
    public static byte PRINTER_ATTRIBUTES_TAG = 0X04;
    public static byte UNSUPPORTED_ATTRIBUTES_TAG = 0X05;


    //out-of-band values
    public static byte UNSUPPORTED = 0x10;
    public static byte UNKNOWN = 0x12;
    public static byte NO_VALUE = 0x13;

    //integer tags
    public static byte UNASSIGNE_INTEGER_TYPE = 0x20;
    public static byte INTEGER = 0x21;
    public static byte BOOLEAN = 0x22;
    public static byte ENUM = 0x23;
    public static byte MAX_INTEGER_TAG = 0x2f;

    //octetString tags
    public static byte OCTET_UNSPECIFIED_FORMAT = 0x30;
    public static byte DATE_TIME = 0x31;
    public static byte RESOLUTION = 0x32;
    public static byte RANGE_OF_INTEGER = 0x33;
    public static byte BEG_COLLECTION = 0x34;
    public static byte TEXT_WITH_LANGUAGE = 0x35;
    public static byte NAME_WITH_LANGUAGE = 0x36;
    public static byte END_COLLECTION = 0x37;
    public static byte MAX_OCTET_TAG = 0x3f;

    //string tags
    public static byte UNASSIGNED_CHARACTER_STRING = 0x40;
    public static byte TEXT_WITHOUT_LANGUAGE = 0x41;
    public static byte NAME_WITHOUT_LANGUAGE = 0x42;
    public static byte UNASSIGNED_CHARACTER_STRING_2 = 0x43;
    public static byte KEYWORD = 0x44;
    public static byte URI = 0x45;
    public static byte URI_SCHEME = 0x46;
    public static byte CHARSET = 0x47;
    public static byte NATURAL_LANGUAGE = 0x48;
    public static byte MIME_MEDIA_TYPE = 0x49;
    public static byte MEMBER_ATTR_NAME = 0x4a;
    public static byte MAX_STRING_TAG = 0x5f;

    //helpful values
    public static byte[] FALSE = {0x00};
    public static byte[] TRUE = {0x01};

    //helpful functions
    public static byte[] intToBytes(int toConvert) {
        return ByteBuffer.allocate(4).putInt(toConvert).array();
    }

}
