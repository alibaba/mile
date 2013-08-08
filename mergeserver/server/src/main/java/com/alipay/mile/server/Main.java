/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server;

import java.io.File;
import java.net.InetAddress;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.alipay.mile.Config;
import com.alipay.mile.mileexception.ArgumentFormantException;

/**
 * parse config file new domain object from config
 * start communication layer
 * @author huabing.du
 * @version $Id: Main.java, v 0.1 2011-5-10 下午01:24:57 huabing.du Exp $
 */
public class Main {
    /** 日志 */
    private static final Logger            LOGGER    = Logger.getLogger(Main.class.getName());

    /** Proxy server */
    private ProxyServer                    proxy;

    public static void main(String[] args) {
        Main ma = new Main();
        ma.init(args);
        ma.start();
    }

    /**
     * 分析输入参数加载配置文件
     * @param args
     * @return
     * @throws ArgumentFormantException
     */
    private static void parseArguments(String[] args) throws ArgumentFormantException {
        for (String arg : args) {
            if (arg.startsWith("--")) {
                arg = arg.substring(2);
            } else if (arg.startsWith("-")) {
                arg = arg.substring(1);
            }

            String[] keyValue = arg.split("=");
            if (keyValue == null || keyValue.length != 2) {
                throw new ArgumentFormantException("Argument[" + arg + "] Format Error.");
            }

            Config.setConfigValue(keyValue[0], keyValue[1]);
        }
        Config.resolveArgment();
    }

    /**
     *  初始化log4j.
     */
    private static void initLog4j() {
        try {
            if (null == System.getProperty("LOG_PATH")) {
                System.setProperty("LOG_PATH", Config.getLog4jPathString());
            }
            if (null == System.getProperty("HOST_NAME")) {
                System.setProperty("HOST_NAME", InetAddress.getLocalHost().getHostName());
            }
            File log4jFile = Config.getLog4jConfig();
            //动态变更log 级别
            DOMConfigurator.configureAndWatch(log4jFile.getPath(), 1000);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("log4j初始化成功.");
            }
        } catch (Exception e) {
            LOGGER.error("Error to initializ log4j", e);
        }
    }

    /**
     * jsvc interface implement.
     * @param arguments
     */
    public void init(String[] args) {
        try {
            //加载配置文件
            parseArguments(args);
        } catch (ArgumentFormantException afe) {
            LOGGER.error("Error to load config file", afe);
            System.exit(-1);
        }

        //初始化log4j
        initLog4j();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Start Server: " + System.currentTimeMillis());
        }
        proxy = new ProxyServer();
    }

    /**
     * jsvc interface implement.
     */
    public void start() {
        try {
            proxy.init();
            proxy.start();
        } catch (Exception e) {
            LOGGER.error("Start failed: ", e);
        }
    }

    /**
     * jsvc interface implement.
     */
    public void stop() {
        // TODO
        // proxy.stop();
    }

    /**
     * jsvc interface implement.
     */
    public void destroy() {
        // TODO
        // proxy.destroy();
    }
}
