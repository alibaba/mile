/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.alipay.mile.mileexception.ArgumentFormantException;

/**
 *
 * @author huabing.du
 * @version $Id: Config.java, v 0.1 2011-5-10 下午01:17:52 huabing.du Exp $
 */
public class Config {

    private static final Logger     LOGGER                                        = Logger
                                                                                      .getLogger(Config.class
                                                                                          .getName());

    public static final String      CONFIG_KEY_MILE_HOME                          = "mile.home";
    public static final String      CONFIG_DEFAULT_MILE_HOME                      = "/home/admin/mile/etc";

    public static final String      CONFIG_KEY_CONFIG_FILE                        = "mile.config.file";
    public static final String      CONFIG_DEFAULT_CONFIG_FILE                    = "proxy.cfg";

    public static final String      CONFIG_KEY_MILE_SERVER_PORT                   = "mile.server.port";
    public static final String      CONFIG_DEFAULT_MILE_SERVER_PORT               = "8964";

    public static final String      CONFIG_KEY_MILE_DOCUMENT_SERVERS              = "mile.document.servers";
    public static final String      CONFIG_DEFAULT_MILE_DOCUMENT_SERVERS          = "servers.xml";

    public static final String      CONFIG_KEY_MILE_SCHEMA                        = "mile.schema";
    public static final String      CONFIG_DEFAULT_MILE_SCHEMA                    = "schema.xml";

    //Digest调度时间
    public static final String      CONFIG_KEY_MILE_MERGESERVER_QUERY_DIGEST      = "mile.mergerserver.query.digest";
    public static final String      CONFIG_DEFAULT_MILE_MERGESERVER_QUERY_DIGEST  = "false";
    public static final String      CONFIG_KEY_MILE_MERGESERVER_INSERT_DIGEST     = "mile.mergerserver.insert.digest";
    public static final String      CONFIG_DEFAULT_MILE_MERGESERVER_INSERT_DIGEST = "false";
    public static final String      CONFIG_KEY_MILE_DOCSERVER_QUERY_DIGEST        = "mile.docserver.query.digest";
    public static final String      CONFIG_DEFAULT_MILE_DOCSERVER_QUERY_DIGEST    = "false";
    public static final String      CONFIG_KEY_MILE_DOCSERVER_INSERT_DIGEST       = "mile.docserver.insert.digest";
    public static final String      CONFIG_DEFAULT_MILE_DOCSERVER_INSERT_DIGEST   = "false";
    public static final String      CONFIG_KEY_MILE_TOPTEN_QUERY_DIGEST           = "mile.topten.query.digest";
    public static final String      CONFIG_DEFAULT_MILE_TOPTEN_QUERY_DIGEST       = "false";
    public static final String      CONFIG_KEY_MILE_TOPTEN_INSERT_DIGEST          = "mile.topten.insert.digest";
    public static final String      CONFIG_DEFAULT_MILE_TOPTEN_INSERT_DIGEST      = "false";

    public static final String      CONFIG_KEY_MILE_DIGEST_TIME                   = "mile.digest.time";
    public static final String      CONFIG_KEY_MILE_DEFAULT_DIGEST_TIME           = "60";

    public static final String      CONFIG_KEY_MILE_CHECK_DOCSERVER               = "mile.check.docserver";
    public static final String      CONFIG_DEFAULT_MILE_CHECK_DOCSERVER           = "false";

    //log4j 文件配置
    public static final String      CONFIG_KEY_MILE_LOG_FILE                      = "mile.log4j";
    public static final String      CONFIG_DEFAULT_MILE_LOG_PROPERTIES            = "log4j.properties";
    public static final String      CONFIG_DEFAULT_MILE_LOG_FILE                  = "log4j.xml";
    public static final String      CONFIG_KEY_MILE_LOG_PATH                      = "mile.log4j.path";
    public static final String      CONFIG_DEFAULT_MILE_LOG_PATH                  = "/home/admin/logs/";

    public static final String      CONFIG_KEY_MILE_SHARDING                      = "mile.sharding";
    public static final String      CONFIG_DEFAULT_MILE_SHARDING_PROPERTIES       = "sharding.cfg";

    public static final String      CONFIG_KEY_QUERY_LIMIT                        = "mile.query.limit";
    public static final String      CONFIG_DEFAULT_QUERY_LIMIT                    = "50";

    public static final String      CONFIG_KEY_MILE_INSERT_QUEUE_THRESHOLD        = "mile.server.insert.queue.threshold";
    public static final String      CONFIG_DEFAULT_MILE_INSERT_QUEUE_THRESHOLD    = "100";

    public static final String      CONFIG_KEY_MILE_QUERY_QUEUE_THRESHOLD         = "mile.server.query.queue.threshold";
    public static final String      CONFIG_DEFAULT_MILE_QUERY_QUEUE_THRESHOLD     = "32";

    public static final String      CONFIG_KEY_MILE_SQL_CACHE_COUNT               = "mile.sql.cache.count";
    public static final String      CONFIG_DEFAULT_KEY_MILE_SQL_CACHE_COUNT       = "100";

    public static final String      CONFIG_LOG_TIME_THRESHOLD                     = "mile.log.time.threshold";
    public static final String      CONFIG_DEFAULT_LOG_TIME_THRESHOLD             = "100";
    public static final String      CONFIG_LOG_TIME_LEVEL                         = "mile.log.time.level";
    public static final String      CONFIG_DEFAULT_LOG_TIME_LEVEL                 = "warn";
    private static final Properties ENV                                           = new Properties(
                                                                                      System
                                                                                          .getProperties());

    /**
     *
     * @param key
     * @return
     */
    public static String getConfigString(final String key) {
        return ENV.getProperty(key);
    }

    /**
     * 取得Home路径
     * @return
     * @throws ArgumentFormantException
     */
    public static File getConfigHomeDir() throws ArgumentFormantException {
        String home = ENV.getProperty(CONFIG_KEY_MILE_HOME, CONFIG_DEFAULT_MILE_HOME);
        File dir = new File(home);
        if (dir.isDirectory() && dir.canRead()) {
            try {
                return dir.getCanonicalFile();
            } catch (IOException ioe) {
                throw new ArgumentFormantException("MILE_HOME[" + home + "] file system error.",
                    ioe);
            }
        }
        throw new ArgumentFormantException("MILE_HOME[" + home
                                           + "] is NOT a directory or can NOT read.");
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getConfigString(final String key, final String defaultValue) {
        return ENV.getProperty(key, defaultValue);
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getLog4jPathString() {
        return getConfigString(CONFIG_KEY_MILE_LOG_PATH, CONFIG_DEFAULT_MILE_LOG_PATH);
    }

    /**
     *
     * @param key
     * @param value
     */
    public static void setConfigValue(final String key, final String value) {
        ENV.setProperty(key, value);
    }

    /**
     * 读取配置文件
     * @throws ArgumentFormantException
     */
    public static void resolveArgment() throws ArgumentFormantException {
        // resolve CONFIG_KEY_CONFIG_FILE
        String config = getConfigString(CONFIG_KEY_CONFIG_FILE, CONFIG_DEFAULT_CONFIG_FILE);
        if (config != null) {
            File home = getConfigHomeDir();
            File configFile = new File(home, config);
            if (configFile.exists()) {
                //加载配置项
                Properties newEnv = new Properties();
                try {
                    // append config file to config item
                    newEnv.load(new BufferedInputStream(new FileInputStream(configFile)));
                } catch (IOException e) {
                    LOGGER.error("加载配置文件proxy", e);
                }
                // argument override config file
                ENV.putAll(newEnv);
            }
        }
    }

    /**
     * String to Integer
     * @param value
     * @return
     * @throws ArgumentFormantException
     */
    private static int parseInt(final String value) {
        return Integer.parseInt(value);
    }

    /**
     * mergerServer 端口号
     * @return
     * @throws ArgumentFormantException
     */
    public static int getServerIOPort() throws ArgumentFormantException {
        return parseInt(getConfigString(CONFIG_KEY_MILE_SERVER_PORT,
            CONFIG_DEFAULT_MILE_SERVER_PORT));
    }

    /**
     * 是否允许检测DOCServer状态
     * @return
     * @throws ArgumentFormantException
     */
    public static boolean getAllowMergerServerCheckDocServer() {
        return Boolean.parseBoolean(getConfigString(CONFIG_KEY_MILE_CHECK_DOCSERVER,
            CONFIG_DEFAULT_MILE_CHECK_DOCSERVER));
    }

    /**
     * 是否允许打印Digest日志
     * @return
     * @throws ArgumentFormantException
     */
    public static boolean getAllowMergerServerQueryDigest() {
        return Boolean.parseBoolean(getConfigString(CONFIG_KEY_MILE_MERGESERVER_QUERY_DIGEST,
            CONFIG_DEFAULT_MILE_MERGESERVER_QUERY_DIGEST));
    }

    /**
     * 是否允许打印Digest日志
     * @return
     * @throws ArgumentFormantException
     */
    public static boolean getAllowMergerServerInsertDigest() {
        return Boolean.parseBoolean(getConfigString(CONFIG_KEY_MILE_MERGESERVER_INSERT_DIGEST,
            CONFIG_DEFAULT_MILE_MERGESERVER_INSERT_DIGEST));
    }

    /**
     * 是否允许打印Digest日志
     * @return
     * @throws ArgumentFormantException
     */
    public static boolean getAllowDocServerQueryDigest() {
        return Boolean.parseBoolean(getConfigString(CONFIG_KEY_MILE_DOCSERVER_QUERY_DIGEST,
            CONFIG_DEFAULT_MILE_DOCSERVER_QUERY_DIGEST));
    }

    /**
     * 是否允许打印Digest日志
     * @return
     * @throws ArgumentFormantException
     */
    public static boolean getAllowDocServerInsertDigest() {
        return Boolean.parseBoolean(getConfigString(CONFIG_KEY_MILE_DOCSERVER_INSERT_DIGEST,
            CONFIG_DEFAULT_MILE_DOCSERVER_INSERT_DIGEST));
    }

    /**
     * 是否允许打印Digest日志
     * @return
     * @throws ArgumentFormantException
     */
    public static boolean getAllowTopTenQueryDigest() {
        return Boolean.parseBoolean(getConfigString(CONFIG_KEY_MILE_TOPTEN_QUERY_DIGEST,
            CONFIG_DEFAULT_MILE_TOPTEN_QUERY_DIGEST));
    }

    /**
     * 是否允许打印Digest日志
     * @return
     * @throws ArgumentFormantException
     */
    public static boolean getAllowTopTenInsertDigest() {
        return Boolean.parseBoolean(getConfigString(CONFIG_KEY_MILE_TOPTEN_INSERT_DIGEST,
            CONFIG_DEFAULT_MILE_TOPTEN_INSERT_DIGEST));
    }

    /**
     * 是否允许打印Digest日志
     * @return
     * @throws ArgumentFormantException
     */
    public static int getDigestTime() {
        return Integer.parseInt(getConfigString(CONFIG_KEY_MILE_DIGEST_TIME,
            CONFIG_KEY_MILE_DEFAULT_DIGEST_TIME));
    }

    /**
     * 取得docserver配置文件
     * @return
     * @throws ArgumentFormantException
     */
    public static File getDocumentServerConfig() throws ArgumentFormantException {
        String conf = getConfigString(CONFIG_KEY_MILE_DOCUMENT_SERVERS,
            CONFIG_DEFAULT_MILE_DOCUMENT_SERVERS);
        return new File(getConfigHomeDir(), conf);
    }

    /**
     * 取得段配置文件
     * @return
     * @throws ArgumentFormantException
     */
    public static File getSchemaConfig() throws ArgumentFormantException {
        String conf = getConfigString(CONFIG_KEY_MILE_SCHEMA, CONFIG_DEFAULT_MILE_SCHEMA);
        return new File(getConfigHomeDir(), conf);
    }

    /**
     * 取得MergrServer配置文件
     * @return
     * @throws ArgumentFormantException
     */
    public static File getProxyConfig() throws ArgumentFormantException {
        String conf = getConfigString(Config.CONFIG_KEY_CONFIG_FILE,
            Config.CONFIG_DEFAULT_CONFIG_FILE);
        return new File(getConfigHomeDir(), conf);
    }

    /**
     * 取得sharding 配置文件
     * @return
     * @throws ArgumentFormantException
     */
    public static File getShardingConfig() throws ArgumentFormantException {
        String conf = getConfigString(CONFIG_KEY_MILE_SHARDING,
            CONFIG_DEFAULT_MILE_SHARDING_PROPERTIES);
        return new File(getConfigHomeDir(), conf);
    }

    /**
     * 取到log4j的配置文件名
     *
     * @return 文件路径
     * @throws ArgumentFormantException
     */
    public static File getLog4jConfig() throws ArgumentFormantException {
        String conf = getConfigString(CONFIG_KEY_MILE_LOG_FILE, Config.CONFIG_DEFAULT_MILE_LOG_FILE);
        return new File(getConfigHomeDir(), conf);
    }

    public static int getQueryLimit() throws ArgumentFormantException {
        String conf = getConfigString(CONFIG_KEY_QUERY_LIMIT, Config.CONFIG_DEFAULT_QUERY_LIMIT);
        return Integer.valueOf(conf);
    }

    public static int getQueryQueueThreshold() {
        String conf = getConfigString(CONFIG_KEY_MILE_QUERY_QUEUE_THRESHOLD,
            Config.CONFIG_DEFAULT_MILE_QUERY_QUEUE_THRESHOLD);
        return Integer.valueOf(conf);
    }

    public static int getInsertQueueThreshold() {
        String conf = getConfigString(CONFIG_KEY_MILE_INSERT_QUEUE_THRESHOLD,
            Config.CONFIG_DEFAULT_MILE_INSERT_QUEUE_THRESHOLD);
        return Integer.valueOf(conf);
    }

    public static int getSqlCacheCount() {
        String count = getConfigString(CONFIG_KEY_MILE_SQL_CACHE_COUNT,
            Config.CONFIG_DEFAULT_KEY_MILE_SQL_CACHE_COUNT);
        return Integer.valueOf(count);
    }

    public static int getLogTimeThreshold() {
        String conf = getConfigString(CONFIG_LOG_TIME_THRESHOLD, CONFIG_DEFAULT_LOG_TIME_THRESHOLD);
        return Integer.valueOf(conf);
    }

    public static String getLogTimeLevel() {
        String conf = getConfigString(CONFIG_LOG_TIME_LEVEL, CONFIG_DEFAULT_LOG_TIME_LEVEL);
        return conf;
    }
}
