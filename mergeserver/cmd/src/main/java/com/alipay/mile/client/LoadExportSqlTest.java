/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */

package com.alipay.mile.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.alipay.mile.Config;
import com.alipay.mile.message.TypeCode;
import com.alipay.mile.util.ByteConveror;

/**
 *
 * @author bin.lb
 *
 */
public class LoadExportSqlTest {
	private static final Logger LOGGER = Logger.getLogger(LoadExportSqlTest.class
			.getName());

	public static       int    defaultWorkingThread = 8;
	public static final String THREADS = "threads"; // working thread number
	public static final String INTERVAL = "interval"; // report interval
	public static final String TABLENAME = "table"; // table name
	public static final String CONFIGFILE = "config-file"; // config name
	public static final String LOG4J = "log4j"; // log4j name
	public static       String table;
	public static final String HELP = "help"; // report interval


	public class SqlCmd {
		public String sql;
		public String file;
		public long line;
		public Object [] params;
		public String text;
	}

	public class SqlProvider {
		private List<String> files;
		private String currentFile;
		private BufferedReader bufferedReader;
		private long fileLine = 0;
		private Iterator<String> fileIter;
		private String linetmp =  "";

		public SqlProvider(List<String> files) throws FileNotFoundException {
			if (files.isEmpty()) {
				LOGGER.error("no file to load");
				System.exit(1);
			}
			this.files = files;
			fileIter = this.files.iterator();
			nextFile();
		}

		private void nextFile() throws FileNotFoundException {
			bufferedReader = null;
			fileLine = 0;
			if(fileIter.hasNext()){
		        currentFile = fileIter.next();
	            fileLine = 0;
                FileReader fr = new FileReader(currentFile);
                this.bufferedReader = new BufferedReader(fr);
		    }
		}

		/**
		 *
		 * @param cmd
		 *            if get the end or error occurred cmd.sql set to null.
		 */
        synchronized public void next(SqlCmd cmd) {
            while (bufferedReader != null) {
                try {
                    String readline = null ;
                    String rowline ;
                    int rowIndex=linetmp.indexOf("#row#");
                    if(rowIndex>0){
                        rowline = linetmp.substring(0, rowIndex);
                        linetmp = linetmp.substring(rowIndex+5);
                        fileLine++;
                        cmd.file = currentFile;
                        cmd.line = fileLine;
                        cmd.text = rowline;
                        break;
                    }else{
                        int endindex =0;
                        char[] cbuf = new char [20000];
                        endindex = bufferedReader.read(cbuf);
                        readline = new String(cbuf);
                        if (endindex == -1) {
                          linetmp+="#row#";
                          nextFile();
                        }else{
                            linetmp += readline;
                        }
                    }
                } catch (IOException e) {
                    LOGGER.error(currentFile + " ", e);
                    break;
                }

            }
        }
	}

	public class Counter {
		private Map<Integer, Long> map = new HashMap<Integer, Long>();
		private long startTime = System.currentTimeMillis();
		private long failedCount = 0;

		synchronized public void count(int nodeId) {
			Long count = map.get(nodeId);
			if (null == count) {
				map.put(nodeId, (long) 1);
			} else {
				map.put(nodeId, count + 1);
			}
		}

		synchronized public void countFailed() {
			failedCount++;
		}

		synchronized Map<Integer, Long> get() {
			Map<Integer, Long> map = new HashMap<Integer, Long>();
			for (int i : this.map.keySet()) {
				map.put(i, this.map.get(i).longValue());
			}
			return map;
		}

		public long getStartTime() {
			return startTime;
		}

		public long getFailedCount() {
			return failedCount;
		}
	}

	public class WorkingThread extends Thread {
		private ApplationClientImpl client;
		private SqlProvider sqlProvider;
		private Counter counter;

		public WorkingThread(ApplationClientImpl client,SqlProvider sqlProvider, Counter counter) {
			this.client = client;
			this.sqlProvider = sqlProvider;
			this.counter = counter;
		}

		@Override
		public void run() {
			SqlCmd cmd;
			do {
			    cmd = new SqlCmd();
				sqlProvider.next(cmd);
				if (cmd.text == null) {
					break;
				}
				try {
				    String [] tmp = cmd.text.split("#mileparams#");
                    cmd.sql = tmp[0];
                    cmd.params = strs2objs(tmp[1].split("#milecols#"));
	                cmd.sql.trim();
	                if (cmd.sql.isEmpty() || cmd.sql.startsWith("#")) {
	                    continue;
	                }
					long docId = client.preInsert(cmd.sql, cmd.params, 3000).getDocId();
					counter.count((int) (docId >> 48));
				} catch (Exception e) {
					LOGGER.error("execute sql failed, file [" + cmd.file
							+ "], line [" + cmd.line + "], SQL [" + cmd.sql
							+ "]"+ "], Params [" +cmd.text, e);
					counter.countFailed();
				}
			} while (cmd.text != null);
		}
		private Object[] strs2objs(String[] strParams){
	        Object[] params = new Object[strParams.length];
	        for(int i=0;i<strParams.length;i++){
	            String strs[] = StringUtils.split(strParams[i], ':');
	            byte columnType = TypeCode.getTCByName(strs[0]);
	            params[i]= ByteConveror.preString2value(columnType, strs[1]);
	        }
	        return params;
	    }
	}


	public class StatusThread extends Thread {
		private Counter counter;
		private int interval;
		boolean running = true;

		public StatusThread(Counter counter, int interval) {
			this.counter = counter;
			this.interval = interval;
		}

		public void setStop() {
			running = false;
		}

		private void printStatus() {
			Map<Integer, Long> map = counter.get();
			long sum = 0;
			String msg = "";
			for(int nodeId : map.keySet()) {
				if(!msg.isEmpty()) {
					msg += ", ";
				}
				msg += "NodeId: " + nodeId + " " + map.get(nodeId);
				sum += map.get(nodeId);
			}
			msg += ", Success: " + sum;
			msg += ", Throughpu(ops/sec): " + (float)sum * 1000 / (System.currentTimeMillis() - counter.getStartTime());
			if(counter.getFailedCount() > 0) {
				msg += ", Failed: " + counter.getFailedCount();
			}
			if(LOGGER.isInfoEnabled()) {
				LOGGER.info(msg);
			}
		}

		@Override
		public void run() {
			while(running) {
				try {
					sleep((long)interval * 1000);
				} catch (InterruptedException e) {
				}

				printStatus();
			}
		}
	}
	/**
     * 分析输入参数加载配置文件
     * @param args
     * @return
     * @throws ArgumentFormantException
     */
	public static void main(String[] args) throws IOException, InterruptedException {
		// configure

		OptionParser parser = new OptionParser();
		parser.accepts(THREADS,"max number concurrent worker threads  Default = "+ defaultWorkingThread).withRequiredArg().ofType(Integer.class);
		parser.accepts(INTERVAL, "print requests on this interval  Default = 1").withRequiredArg().ofType(Integer.class);
		parser.accepts(TABLENAME, "Insert into table name ").withRequiredArg().ofType(String.class);
		parser.accepts(LOG4J, "log4j into mod ").withRequiredArg().ofType(String.class);
		parser.accepts(CONFIGFILE, "ConfigFile into mod ").withRequiredArg().ofType(String.class);
		parser.accepts(HELP);

		OptionSet options = parser.parse(args);
		if (options.has(HELP)) {
			if(LOGGER.isInfoEnabled()) {
				LOGGER.info("Usage, args: [options] sql_file [sql_file ...]");
			}
			parser.printHelpOn(System.out);
			System.exit(0);
		}
		int threadNum = defaultWorkingThread;
		if (options.has(THREADS)) {
			threadNum = (Integer) options.valueOf(THREADS);
		}
		int interval = 1;
		if (options.has(INTERVAL)) {
			interval = (Integer) options.valueOf(INTERVAL);
		}

		if (options.has(TABLENAME)) {
		    table = (String) options.valueOf(TABLENAME);
        }
		BasicConfigurator.configure();
        if(options.has(LOG4J)){
            PropertyConfigurator.configure((String) options.valueOf(LOG4J));
        }else{
            PropertyConfigurator.configure("etc"
                    + File.separator + Config.CONFIG_DEFAULT_MILE_LOG_PROPERTIES);
        }
		LoadExportSqlTest loadSqlTest = new LoadExportSqlTest();

		SqlProvider sqlProvider = loadSqlTest.new SqlProvider(options.nonOptionArguments());
		Counter counter = loadSqlTest.new Counter();

		ApplationClientImpl client = new ApplationClientImpl();
		if(options.has(CONFIGFILE)){
		    client.readProperties((String) options.valueOf(CONFIGFILE));
        }else{
            String filePath = System.getProperty("user.dir") + File.separator + "etc"
            + File.separator + "mileCliClent.properties.prod";
            client.readProperties(filePath);
        }
		// create ApplationClientImpl
		client.init();

		// create working thread
		Vector<Thread> threads = new Vector<Thread>(threadNum);
		for( int i = 0; i < threadNum; i++) {
			threads.add(loadSqlTest.new WorkingThread(client, sqlProvider, counter));
			threads.get(i).start();
		}

		StatusThread statusThread = loadSqlTest.new StatusThread(counter, interval);
		statusThread.start();

		for(Thread t : threads) {
			t.join();
		}

		statusThread.setStop();
		statusThread.interrupt();
		statusThread.join();
		client.close();

		System.exit(0);
	}
}
