package com.ripceipt.printer.ipp;

import com.ripceipt.printer.Data.AttributeGroup;
import com.ripceipt.printer.Data.Job;
import com.ripceipt.printer.Exceptions.IPPParseException;

import java.util.List;

public class JobQueue {

    private static JobQueue instance;

    public static JobQueue getJobQueue() {
        if (instance == null) {
            instance = new JobQueue();
        }
        return instance;
    }

    private Job printQueue;
    private int id;

    public JobQueue() {
        id = 0;
        printQueue = null;
    }

    public void cancelJob() {
        printQueue.setState(7);
    }

    public void finishJob() {
        printQueue.setState(9);
    }

    public int getNextID() {
        return id++;
    }

    public void addJob(Job toPrint) {
        printQueue = toPrint;
    }

    public AttributeGroup getJobAttributes(List<String> requested) throws IPPParseException {
        if(printQueue == null) return null;
        else return printQueue.toAttributeGroup(requested);
    }


}
