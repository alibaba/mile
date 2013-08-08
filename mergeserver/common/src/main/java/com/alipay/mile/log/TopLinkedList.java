package com.alipay.mile.log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//import org.apache.log4j.Logger;

public class TopLinkedList {

    //    private static final int  DEFAULT_SIZE = 10;
    private int                    size;
    private LinkedList<TopSqlData> dataList = new LinkedList<TopSqlData>(); ;
    private Lock                   lock     = new ReentrantLock();

    public TopLinkedList(int size) {
        this.size = size;
    }

    public void put(TopSqlData data) {
        lock.lock();
        try {
            boolean isadd = false;
            int i = dataList.size() - 1;
            while (i >= 0) {
                if (dataList.get(i).getExcTime() >= data.getExcTime()) {
                    dataList.add(i + 1, data);
                    isadd = true;
                    break;
                }
                i--;
            }
            if (!isadd && i < 0) {
                dataList.add(0, data);
            }
            if (dataList.size() > size) {
                dataList.removeLast();
            }
        } finally {
            lock.unlock();
        }
    }

    public List<TopSqlData> get() {
        lock.lock();
        List<TopSqlData> ntsds = new ArrayList<TopSqlData>();
        try {
            for (TopSqlData tsd : dataList) {
                ntsds.add(tsd);
            }
            dataList.clear();
        } finally {
            lock.unlock();
        }
        return ntsds;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public LinkedList<TopSqlData> getDataList() {
        return dataList;
    }

    public void setDataList(LinkedList<TopSqlData> dataList) {
        this.dataList = dataList;
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }

}
