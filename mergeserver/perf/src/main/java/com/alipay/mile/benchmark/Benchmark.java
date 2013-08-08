/*
 * Copyright 2010 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.alipay.mile.benchmark;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.alipay.mile.Config;
import com.alipay.mile.benchmark.util.CmdUtils;
import com.alipay.mile.benchmark.util.Props;
import com.alipay.mile.benchmark.util.Time;
import com.alipay.mile.benchmark.util.Utils;
import com.alipay.mile.client.ApplationClientImpl;
import com.alipay.mile.mileexception.MileException;

public class Benchmark {
    private static final Logger LOGGER                          = Logger.getLogger(Benchmark.class
                                                                    .getName());

    private static final int    MAX_WORKERS                     = 8;

    /**
     * Constants for the benchmark file
     */
    /**线程数 */
    public static final String  THREADS                         = "threads";
    /**运行次数 */
    public static final String  ITERATIONS                      = "iterations";
    /**输出统计状态的间隔 */
    public static final String  INTERVAL                        = "interval";
    /**插入执行比率(百分比)*/
    public static final String  INSERT                          = "i";
    /**查询执行比率(百分比)*/
    public static final String  SELECT                          = "s";
    /**目标吞吐率*/
    public static final String  TARGET_THROUGHPUT               = "target-throughput";
    /**HELP*/
    public static final String  HELP                            = "help";
    /**运行条数 */
    public static final String  OPS_COUNT                       = "ops-count";
    /**基于 histogram/summary 进行统计*/
    public static final String  METRIC_TYPE                     = "metric-type";
    /**  */
    public static final String  HISTOGRAM_METRIC_TYPE           = "histogram";
    /**  */
    public static final String  SUMMARY_METRIC_TYPE             = "summary";
    /**验证SQL执行结果*/
    public static final String  VERIFY                          = "verify";
    /**Mile Client配置文件名(config目录下的配置文件名字*/
    public static final String  CONFIG_FILE                     = "config-file";
    /**插入语句*/
    public static final String  INSERT_CMD                      = "insert-cmd";
    /**查询语句*/
    public static final String  SELECT_CMD                      = "select-cmd";
    /**预编译插入sql 中的参数值 多个值用逗号分隔*/
    public static final String  INSERT_PARAMS                   = "insert-params";
    /**预编译查询sql 中的参数值 多个值用逗号分隔*/
    public static final String  SELECT_PARAMS                   = "select-params";
    public static final String  LOG4J                           = "log4j";                  // log4j name
    /**从文件load SQL语句 */
    public static final String  RECORD_FILE                     = "record-file";
    /** 特殊的命令 */
    public static final String  CMD_PROVIDER                    = "cmd-provider";
    /** 用于控制ctu_minisearch的相似度阈值  */
    public static final String  CTU_SIMILAR_THRESHOLD           = "ctu-similarity";
    /** */
    public static final String  CTU_MINISEARCH_LIMIT            = "ctu-minisearch-limit";
    /** */
    public static final String  CTU_MINISEARCH_CONTAIN_ORIGINAL = "minisearch-original";

    private ApplationClientImpl applicationClient;

    private int                 numThreads;
    private int                 numIterations;
    private int                 targetThroughput;
    /**每个线程平均压力*/
    private double              perThreadThroughputPerMs;
    private int                 opsCount;
    private Workload            workLoad;
    private int                 statusIntervalSec;
    private boolean             storeInitialized                = false;
    private boolean             verifyRead                      = false;

    class StatusThread extends Thread {

        private Vector<Thread> threads;
        private int            intervalSec;
        private long           startTime;

        public StatusThread(Vector<Thread> threads, int intervalSec, long startTime) {
            this.threads = threads;
            this.intervalSec = intervalSec;
            this.startTime = startTime;
        }

        public Vector<Thread> getThreads() {
            return threads;
        }

        public void setThreads(Vector<Thread> threads) {
            this.threads = threads;
        }

        public int getIntervalSec() {
            return intervalSec;
        }

        public void setIntervalSec(int intervalSec) {
            this.intervalSec = intervalSec;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        @Override
        public void run() {
            boolean testComplete = true;
            int totalOps = 0, prevTotalOps = 0;
            do {
                testComplete = true;
                totalOps = 0;
                for (Thread thread : this.threads) {
                    if (thread.getState() != Thread.State.TERMINATED) {
                        testComplete = false;
                    }
                    totalOps += ((ClientThread) thread).getOpsDone();
                }

                if (totalOps != 0 && totalOps != prevTotalOps) {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER
                            .info("[status]\tThroughput(ops/sec): "
                                  + Time.MS_PER_SECOND
                                  * ((double) totalOps / (double) (System.currentTimeMillis() - startTime))
                                  + "\tOperations: " + totalOps);
                    } else {
                        System.currentTimeMillis();
                    }
                }
                prevTotalOps = totalOps;
                try {
                    sleep(intervalSec * Time.MS_PER_SECOND);
                } catch (InterruptedException e) {
                }
            } while (!testComplete);
        }
    }

    class ClientThread extends Thread {

        private ClientWrapper db;
        private Workload      workLoad;
        private int           opsCount;
        private double        targetThroughputPerMs;
        private int           opsDone;
        private boolean       verify;

        public ClientThread(ClientWrapper db, Workload workLoad, int opsCount,
                            double targetThroughputPerMs, boolean verify) {
            this.db = db;
            this.workLoad = workLoad;
            this.opsCount = opsCount;
            this.opsDone = 0;
            this.targetThroughputPerMs = targetThroughputPerMs;
            this.verify = verify;
        }

        public int getOpsDone() {
            return this.opsDone;
        }

        public void run() {
            long startTime = System.currentTimeMillis();
            while (opsDone < this.opsCount) {
                if (!workLoad.doTransaction(this.db)) {
                    if (verify) {
                        break;
                    }
                }

                opsDone++;
                if (targetThroughputPerMs > 0) {
                    while (System.currentTimeMillis() - startTime < ((double) opsDone)
                                                                    / targetThroughputPerMs) {
                        try {
                            sleep(1);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
        }

        public ClientWrapper getDb() {
            return db;
        }

        public void setDb(ClientWrapper db) {
            this.db = db;
        }

        public Workload getWorkLoad() {
            return workLoad;
        }

        public void setWorkLoad(Workload workLoad) {
            this.workLoad = workLoad;
        }

        public int getOpsCount() {
            return opsCount;
        }

        public void setOpsCount(int opsCount) {
            this.opsCount = opsCount;
        }

        public double getTargetThroughputPerMs() {
            return targetThroughputPerMs;
        }

        public void setTargetThroughputPerMs(double targetThroughputPerMs) {
            this.targetThroughputPerMs = targetThroughputPerMs;
        }

        public void setOpsDone(int opsDone) {
            this.opsDone = opsDone;
        }
    }

    public void initializeWorkload(Props workloadProps) throws MileException, IOException {

        if (!this.storeInitialized) {
            throw new MileException("Store not initialized correctly");
        }

        // Calculate perThreadThroughputPerMs = default unlimited (-1)
        this.targetThroughput = workloadProps.getInt(TARGET_THROUGHPUT, -1);
        this.perThreadThroughputPerMs = -1;
        if (targetThroughput > 0) {
            double targetperthread = ((double) targetThroughput) / ((double) numThreads);
            this.perThreadThroughputPerMs = targetperthread / 1000.0;
        }

        // Compulsory parameters
        if (workloadProps.containsKey(OPS_COUNT)) {
            this.opsCount = workloadProps.getInt(OPS_COUNT);
        } else {
            throw new MileException("Missing compulsory parameters - " + OPS_COUNT);
        }

        // Initialize measurement
        Metrics.setProperties(workloadProps);
        Metrics.getInstance().reset();

        // Initialize workload
        this.workLoad = new Workload();
        this.workLoad.init(workloadProps);

    }

    public void initializeStore(Props benchmarkProps) throws UnsupportedEncodingException {

        this.numThreads = benchmarkProps.getInt(THREADS, MAX_WORKERS);
        this.numIterations = benchmarkProps.getInt(ITERATIONS, 1);
        this.statusIntervalSec = benchmarkProps.getInt(INTERVAL, 0);
        this.verifyRead = benchmarkProps.getBoolean(VERIFY, false);

        /**初始化mile client*/
        this.applicationClient = new ApplationClientImpl();
        this.applicationClient.readProperties(benchmarkProps.getString(CONFIG_FILE));
        this.applicationClient.setBossExecutorCount(16);
        this.applicationClient.setWorkerExecutorCount(16);
        this.applicationClient.init();

        this.storeInitialized = true;
    }

    public void initialize(Props benchmarkProps) throws MileException, IOException {
        if (!this.storeInitialized) {
            initializeStore(benchmarkProps);
        }
        initializeWorkload(benchmarkProps);
    }

    public void warmUpAndRun() {
        for (int index = 0; index < this.numIterations; index++) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("======================= iteration = " + index
                            + " ======================================");
            }
            runTests();
            Metrics.getInstance().reset();
        }

    }

    @SuppressWarnings("cast")
    public long runTests() {

        int localOpsCounts = 0;
        String label = null;
        localOpsCounts = this.opsCount;
        label = "benchmark";

        Vector<Thread> threads = new Vector<Thread>();

        for (int index = 0; index < this.numThreads; index++) {
            ClientWrapper db = new ClientWrapper(applicationClient);
            Thread clientThread = new ClientThread(db, this.workLoad, localOpsCounts
                                                                      / this.numThreads,
                this.perThreadThroughputPerMs, this.verifyRead);
            threads.add(clientThread);
        }

        long startRunBenchmark = System.currentTimeMillis();
        for (Thread currentThread : threads) {
            currentThread.start();
        }

        StatusThread statusThread = null;
        if (this.statusIntervalSec > 0) {
            statusThread = new StatusThread(threads, this.statusIntervalSec, startRunBenchmark);
            statusThread.start();
        }

        for (Thread currentThread : threads) {
            try {
                currentThread.join();
            } catch (InterruptedException e) {
                LOGGER.error(e);
            }
        }
        long endRunBenchmark = System.currentTimeMillis();

        if (this.statusIntervalSec > 0) {
            statusThread.interrupt();
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("[" + label + "]\tRunTime(ms): " + (endRunBenchmark - startRunBenchmark));
        }
        double throughput = Time.MS_PER_SECOND * ((double) localOpsCounts)
                            / ((double) (endRunBenchmark - startRunBenchmark));
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("[" + label + "]\tThroughput(ops/sec): " + throughput);
        }

        Metrics.getInstance().printReport(System.out);

        return (endRunBenchmark - startRunBenchmark);
    }

    private static Map<String, String> parseArguments(final String[] args) {
        Map<String, String> config = new HashMap<String, String>();
        for (String arg : args) {
            if (arg.startsWith("--")) {
                arg = arg.substring(2);
            } else if (arg.startsWith("-")) {
                arg = arg.substring(1);
            }
            String[] keyValue = arg.split("=");
            if (keyValue == null || keyValue.length != 2) {
                LOGGER.error("ERROE Argument[" + arg + "] Format Error.");
            } else {
                config.put(keyValue[0], keyValue[1]);
            }
        }
        return config;
    }

    public static void main(String args[]) throws IOException {
        BasicConfigurator.configure();
        Map<String, String> config = parseArguments(args);
        BasicConfigurator.configure();
        if (config.get(LOG4J) != null) {
            PropertyConfigurator.configure(config.get("log4j"));
        } else {
            PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator + "etc"
                                           + File.separator
                                           + Config.CONFIG_DEFAULT_MILE_LOG_PROPERTIES);
        }
        OptionParser parser = new OptionParser();
        parser.accepts(SELECT, "execute select operations").withOptionalArg().ofType(Integer.class);
        parser.accepts(INSERT, "execute insert operations").withOptionalArg().ofType(Integer.class);
        parser.accepts(THREADS, "max number concurrent worker threads  Default = 1")
            .withRequiredArg().ofType(Integer.class);
        parser.accepts(ITERATIONS, "number of times to repeat the test  Default = 1")
            .withRequiredArg().ofType(Integer.class);
        parser.accepts(VERIFY, "verify values read");
        parser.accepts(INTERVAL, "print requests on this interval  Default = 0").withRequiredArg()
            .ofType(Integer.class);
        parser.accepts(LOG4J, "log4j into mod ").withRequiredArg().ofType(String.class);
        parser.accepts(TARGET_THROUGHPUT, "fix the throughput").withRequiredArg().ofType(
            Integer.class);
        parser.accepts(OPS_COUNT, "number of operations to do").withRequiredArg().ofType(
            Integer.class);
        parser.accepts(METRIC_TYPE, "type of metric [histogram | summary]").withRequiredArg();
        parser.accepts(CONFIG_FILE, "the config file path of the mile client").withRequiredArg()
            .ofType(String.class);
        parser.accepts(INSERT_CMD, "the cmd of insert").withRequiredArg().ofType(String.class);
        parser.accepts(SELECT_CMD, "the cmd of select").withRequiredArg().ofType(String.class);
        parser.accepts(INSERT_PARAMS, "the params of insert preSql").withRequiredArg().ofType(
            String.class);
        parser.accepts(SELECT_PARAMS, "the params of select preSql").withRequiredArg().ofType(
            String.class);
        parser.accepts(RECORD_FILE, "the record file path").withOptionalArg().ofType(String.class);
        parser.accepts(CMD_PROVIDER, "command provider").withOptionalArg().ofType(String.class);
        parser.accepts(CTU_SIMILAR_THRESHOLD, "ctu minisearch similarity threshold")
            .withOptionalArg().ofType(Float.class);
        parser.accepts(CTU_MINISEARCH_LIMIT, "ctu minisearch query limit").withOptionalArg()
            .ofType(Integer.class);
        parser.accepts(CTU_MINISEARCH_CONTAIN_ORIGINAL,
            "ctu minisearch contain original text when parse the text");

        parser.accepts(HELP);

        OptionSet options = parser.parse(args);

        if (options.has(HELP)) {
            parser.printHelpOn(System.out);
            System.exit(0);
        }

        Props mainProps = new Props();

        if (!options.has(OPS_COUNT)) {
            printUsage(parser, "Missing " + OPS_COUNT);
        }
        mainProps.put(OPS_COUNT, (Integer) options.valueOf(OPS_COUNT));

        if (options.has(VERIFY)) {
            mainProps.put(VERIFY, "true");
        } else {
            mainProps.put(VERIFY, "false");
        }
        if (options.has(CTU_MINISEARCH_CONTAIN_ORIGINAL)){
            mainProps.put(CTU_MINISEARCH_CONTAIN_ORIGINAL, "true");
        }else{
            mainProps.put(CTU_MINISEARCH_CONTAIN_ORIGINAL, "false");
        }

        if (!options.has(CONFIG_FILE)) {
            printUsage(parser, "Missing " + CONFIG_FILE);
            mainProps.put(CONFIG_FILE, System.getProperty("user.dir") + File.separator + "etc"
                                       + File.separator + "mileCliClent.properties.prod");
        } else {
            mainProps.put(CONFIG_FILE, (String) options.valueOf(CONFIG_FILE));
        }
        if (options.has(RECORD_FILE)) {
            mainProps.put(RECORD_FILE, (String) options.valueOf(RECORD_FILE));
        }
        if (options.has(CMD_PROVIDER)) {
            mainProps.put(CMD_PROVIDER, (String) options.valueOf(CMD_PROVIDER));
        }
        mainProps.put(INSERT, CmdUtils.valueOf(options, INSERT, 0));
        mainProps.put(SELECT, CmdUtils.valueOf(options, SELECT, 0));
        mainProps.put(INSERT_CMD, (String) options.valueOf(INSERT_CMD));
        mainProps.put(SELECT_CMD, (String) options.valueOf(SELECT_CMD));
        mainProps.put(INSERT_PARAMS, (String) options.valueOf(INSERT_PARAMS));
        mainProps.put(SELECT_PARAMS, (String) options.valueOf(SELECT_PARAMS));
        mainProps.put(ITERATIONS, CmdUtils.valueOf(options, ITERATIONS, 1));
        mainProps.put(THREADS, CmdUtils.valueOf(options, THREADS, MAX_WORKERS));
        mainProps.put(INTERVAL, CmdUtils.valueOf(options, INTERVAL, 0));
        mainProps.put(TARGET_THROUGHPUT, CmdUtils.valueOf(options, TARGET_THROUGHPUT, -1));
        mainProps.put(METRIC_TYPE, CmdUtils.valueOf(options, METRIC_TYPE, SUMMARY_METRIC_TYPE));
        mainProps.put(CTU_SIMILAR_THRESHOLD, CmdUtils.valueOf(options, CTU_SIMILAR_THRESHOLD,
            (float) 0.7));
        mainProps.put(CTU_MINISEARCH_LIMIT, CmdUtils.valueOf(options, CTU_MINISEARCH_LIMIT, 100));

        // Start the benchmark
        Benchmark benchmark = null;
        try {
            benchmark = new Benchmark();
            benchmark.initialize(mainProps);
            benchmark.warmUpAndRun();
            benchmark.close();
        } catch (Exception e) {
            LOGGER.error(e);
            parser.printHelpOn(System.err);
            System.exit(-1);
        }
        System.exit(-1);
    }

    public void close() {
        applicationClient.close();
    }

    private static void printUsage(OptionParser parser, String errorCommand) throws IOException {
        parser.printHelpOn(System.err);
        Utils.croak("Usage: $MILE_HOME/bin/run-class.sh " + Benchmark.class.getName()
                    + " [options] config-file ops-count\n " + errorCommand);
    }

    public int getTargetThroughput() {
        return targetThroughput;
    }

    public void setTargetThroughput(int targetThroughput) {
        this.targetThroughput = targetThroughput;
    }
}
