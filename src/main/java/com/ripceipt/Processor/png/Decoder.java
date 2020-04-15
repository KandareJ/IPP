package com.ripceipt.Processor.png;

import com.ripceipt.Processor.Exceptions.PNGParseException;

public class Decoder {
    public Decoder(byte[] data) {
        image = new PNG();
        this.data = data;
        index = 0;
        System.out.println("Number of bytes: " + data.length);
    }

    private PNG image;
    private byte[] data;
    private int index;

    public PNG decode() throws PNGParseException {
        signature();
        return image;
    }

    private void signature() throws PNGParseException {
        if(index + 8 >= data.length) throw new PNGParseException("File does not have full signature");

        byte[] sig = { (byte)137, (byte)80, (byte)78, (byte)71, (byte)13, (byte)10, (byte)26, (byte)10};
        StringBuilder errorMessage = new StringBuilder();

        for(; index < 8; index++) {
            if(data[index] != sig[index]) {
                errorMessage.append("Parse error in Signature; byte ");
                errorMessage.append(index);
                errorMessage.append(" is ");
                errorMessage.append((int)data[index]);
                errorMessage.append(" should be ");
                errorMessage.append((int)sig[index]);
                throw new PNGParseException(errorMessage.toString());
            }
        }
        chunkLength();
    }

    private void chunkLength() throws PNGParseException {
        if(index + 4 >= data.length) throw new PNGParseException("File does not have full chunk length");
        int length = data[index] << 24 | (data[index+1] & 0xff) << 16 | (data[index+2] & 0xff) << 8 | (data[index+3] & 0xff);
        index += 4;
        chunkType(length);
    }

    private void chunkType(int length) throws PNGParseException {
        if(index + 4 >= data.length) throw new PNGParseException("Parse error in chunk type");
        StringBuilder chunkString = new StringBuilder();
        int end = index + 4;
        for(; index < end; index++) {
            chunkString.append((char)data[index]);
        }

        switch(chunkString.toString()) {
            case "IHDR":
                ihdr(length);
                break;
            case "IDAT":
                idat(length);
                break;
            case "IEND":
                iend(length);
                break;
            default:
                unimportantChunk(length, chunkString.toString());
        }
    }

    private void ihdr(int length) throws PNGParseException {
        if(index + length >= data.length) throw new PNGParseException("IHDR is missing data");
        if(length != 13) throw new PNGParseException("IHDR data length is " + length);
        IHDR header = new IHDR();

        header.setWidth(data[index] << 24 | (data[index+1] & 0xff) << 16 | (data[index+2] & 0xff) << 8 | (data[index+3] & 0xff));
        index += 4;
        header.setHeight(data[index] << 24 | (data[index+1] & 0xff) << 16 | (data[index+2] & 0xff) << 8 | (data[index+3] & 0xff));
        index += 4;
        header.setBitDepth(data[index]);
        index++;
        header.setColorType(data[index]);
        index++;
        header.setCompressionMethod(data[index]);
        index++;
        header.setFilterMethod(data[index]);
        index++;
        header.setInterlaceMethod(data[index]);
        index++;

        image.setHeader(header);
        //System.out.println(header.toString());

        crc();
    }

    private void idat(int length) throws PNGParseException {
        if(index + length >= data.length) throw new PNGParseException("IDAT is missing data");

        IDAT toAdd = new IDAT();
        toAdd.setCmf(data[index]);
        toAdd.setFlags(data[index+1]);
        image.addIDAT(toAdd);

        //System.out.println("IDAT: " + length);

        index += length;
        crc();
    }

    private void unimportantChunk(int length, String type) throws PNGParseException {
        if(index + length >= data.length) throw new PNGParseException(type + " is missing data");
        System.out.println(type + ": ignored");
        index += length;
        crc();
    }

    private void iend(int length) throws PNGParseException {
        if (length != 0) throw new PNGParseException("IEND length is " + length + ", not 0");
        image.setEndReached(true);
        System.out.println(image.toString());
    }

    private void crc() throws PNGParseException {
        if(index + 4 >= data.length) throw new PNGParseException("Chunk is missing CRC");
        index += 4;
        chunkLength();
    }
}
