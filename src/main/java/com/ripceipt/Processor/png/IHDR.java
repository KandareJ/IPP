package com.ripceipt.Processor.png;

public class IHDR {

    public IHDR() {
    }

    private int width;
    private int height;
    private byte bitDepth;
    private byte colorType;
    private byte compressionMethod;
    private byte filterMethod;
    private byte interlaceMethod;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public byte getBitDepth() {
        return bitDepth;
    }

    public void setBitDepth(byte bitDepth) {
        this.bitDepth = bitDepth;
    }

    public byte getColorType() {
        return colorType;
    }

    public void setColorType(byte colorType) {
        this.colorType = colorType;
    }

    public byte getCompressionMethod() {
        return compressionMethod;
    }

    public void setCompressionMethod(byte compressionMethod) {
        this.compressionMethod = compressionMethod;
    }

    public byte getFilterMethod() {
        return filterMethod;
    }

    public void setFilterMethod(byte filterMethod) {
        this.filterMethod = filterMethod;
    }

    public byte getInterlaceMethod() {
        return interlaceMethod;
    }

    public void setInterlaceMethod(byte interlaceMethod) {
        this.interlaceMethod = interlaceMethod;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\tIHDR:\n");
        sb.append("\t\tSize: ");
        sb.append(width + "x" + height);
        sb.append("\n\t\tBit depth: " + bitDepth);
        sb.append("\n\t\tColor Type: " + colorType);
        sb.append("\n\t\tCompression Method: " + compressionMethod);
        sb.append("\n\t\tInterlace Method: " + interlaceMethod + "\n");


        return sb.toString();
    }
}
