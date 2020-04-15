package com.ripceipt.nfc;

import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.nio.ByteBuffer;

public class PythonProcess {
    public boolean send(byte[] toSend) {
        /*for(int i = 0; i < 5; i++) {
            if (attemptSend(toSend)) return true;
        }
        return false;*/
        attemptSend(toSend);
        return true;
    }


    private boolean attemptSend(byte[] toSend) {
        try {
            System.out.println("In python process");
            int i;
            Process pythonProc = Runtime.getRuntime().exec("python P2p.py");
            StringBuilder errorString = new StringBuilder();
            StringBuilder fromPythonString = new StringBuilder();
            //Process pythonProc = new ProcessBuilder("python P2p.py").start();

            OutputStream toPython = pythonProc.getOutputStream();
            InputStream errors = pythonProc.getErrorStream();
            InputStream fromPython = pythonProc.getInputStream();

            System.out.println("toSend's size: " + toSend.length);
            toPython.write(formatNDEF(toSend));
            toPython.close();

            while ((i = fromPython.read()) != -1) {
                fromPythonString.append((char) i);
                System.out.print((char) i);
            }

            while ((i = errors.read()) != -1) {
                errorString.append((char) i);
                System.out.print((char) i);
            }

            if (fromPythonString.toString().equals("true\n")) return true;
            else return false;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private byte[] formatNDEF(byte[] toFormat) {
        //final int LENGTH = 6;
        byte[] beginning = new byte[]{(byte) 0xc2, 0x18};
        byte[] length = ByteBuffer.allocate(4).putInt(toFormat.length).array();
        byte[] type = "application/octet-stream".getBytes();

        byte[] header1 = ArrayUtils.addAll(beginning, length);
        byte[] header2 = ArrayUtils.addAll(header1, type);

        /*byte[] data = new byte[LENGTH];

        for(int i = 0; i < LENGTH; i++) {
            data[i] = toFormat[i];
            System.out.print(data[i] + " ");
        }*/

        return ArrayUtils.addAll(header2, toFormat);
    }

    public void sendURL(String url) {
        try {
            System.out.println("In python process");
            int i;
            Process pythonProc = Runtime.getRuntime().exec("python P2p.py");
            StringBuilder errorString = new StringBuilder();
            StringBuilder fromPythonString = new StringBuilder();

            OutputStream toPython = pythonProc.getOutputStream();
            InputStream errors = pythonProc.getErrorStream();
            InputStream fromPython = pythonProc.getInputStream();

            toPython.write(formatNDEF(url.getBytes()));

            while ((i = fromPython.read()) != -1) {
                fromPythonString.append((char) i);
                System.out.print((char) i);
            }
            System.out.println(fromPythonString.toString());

            while ((i = errors.read()) != -1) {
                errorString.append((char) i);
                System.out.print((char) i);
            }
            System.out.println(errorString.toString());

            toPython.close();

            System.out.println("Python Connection Closed");

        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        PythonProcess pp = new PythonProcess();
        pp.send(new byte[]{(byte) 0x48, 0x69});
    }

}
