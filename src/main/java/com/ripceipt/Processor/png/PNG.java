package com.ripceipt.Processor.png;

import java.util.ArrayList;
import java.util.List;

public class PNG {
    public PNG() {
        data = new ArrayList<>();
        endReached = false;
    }

    public void setHeader(IHDR header) {
        this.header = header;
    }

    public void setEndReached(boolean endReached) {
        this.endReached = endReached;
    }

    public void addIDAT(IDAT idat) {
        data.add(idat);
    }

    private IHDR header;
    private List<IDAT> data;
    private boolean endReached;

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("PNG file:\n");
        sb.append(header.toString());
        for (IDAT i : data) {
            sb.append(i.toString());
        }
        if(endReached) sb.append("\tIEND Reached\n");

        return sb.toString();
    }


}
