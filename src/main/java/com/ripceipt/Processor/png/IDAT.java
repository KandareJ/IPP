package com.ripceipt.Processor.png;

public class IDAT {
    public IDAT() {
    }

    private byte cm;
    private byte f;
    private byte fcheck;
    private byte fdict;
    private byte flevel;

    public void setCmf(byte cmf) {
        f = (byte) ((cmf >> 4) & 0x0f);
        cm = (byte) (cmf & 0x0f);
    }

    public void setFlags(byte flags) {
        flevel = (byte) ((flags >> 6) & 0x03);
        fdict = (byte) ((flags >> 5) & 0x01);
        fcheck = (byte) (flags & 0x0f);

    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\tIDAT:");
        sb.append("\n\t\tzlib Compression method: ");
        sb.append(cm);
        sb.append("\n\t\tCompression flag: ");
        sb.append(f);
        sb.append("\n\t\tfcheck: ");
        sb.append(fcheck);
        sb.append("\n\t\tfdict: ");
        sb.append(fdict);
        sb.append("\n\t\tflevel: ");
        sb.append(flevel);
        sb.append("\n");

        return sb.toString();
    }
}
