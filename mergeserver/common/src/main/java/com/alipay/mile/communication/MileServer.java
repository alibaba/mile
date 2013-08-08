/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.communication;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.alipay.mile.util.SimpleThreadFactory;

/**
 * @author jin.qian
 * @version $Id: MileServer.java,v 0.1 2011-4-6 下午05:01:15 jin.qian Exp $ 通讯服务端
 */

public class MileServer {

	private static final Logger LOGGER = Logger.getLogger(MileServer.class
			.getName());
	/** 通讯连接启动器 */
	private ServerBootstrap bootstrap;
	/** 侦听连接线程池 */
	private Executor bossExecutor;
	/** 通讯工作线程池 */
	private Executor workerExecutor;
	/** 侦听端口号 */
	private int port = 8080;
	/** 侦听连接线程数 */
	private int bossExecutorCount = 0;
	/** 通讯工作线数 */
	private int workerExecutorCount = 0;
	/** 过滤器链 */
	private MessageServerPipelineFactory messageServerPipelineFactory;
	/** 消息管理 */
	private MessageManager messageManager;
	/** 插入线程数 */
	private int execMin = 5;
	private int execMax = 5;
	/** 查询线程数 */
	private int queryExecMin = 5;
	private int queryExecMax = 5;

	// 线程池的保持持续时间
	private int keepAliveTime = 10;
	// 线程池阻塞队列的堆栈大小
	private int blockingQueueCount = 20000;
	// 单CPU执行线程数量
	private int singleCpuThreadCount = 2;

	/** 是否可用 */
	private AtomicBoolean online = new AtomicBoolean(true);

	/**
	 * @param port
	 */
	public MileServer(int port) {
		this.port = port;
	}

	/**
	 * @param port
	 *            绑定监听端口启动服务端
	 */
	private void startNewBootstrap(int port) {
		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				bossExecutor, workerExecutor));
		bootstrap.setPipelineFactory(getMessageServerPipelineFactory());
		// 设置tpcip长连接 心跳功能开启
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);
		bootstrap.bind(new InetSocketAddress(port));
	}

	/**
	 * 使用默认配置参数
	 */
	public void startCustomBootstrap() {
		// bossExecutor 线程 默认为 cpu*2
		if (bossExecutorCount == 0) {
			bossExecutorCount = Runtime.getRuntime().availableProcessors()
					* singleCpuThreadCount;
		}
		// workerExecutor 线程 默认为 cpu*2
		if (workerExecutorCount == 0) {
			workerExecutorCount = Runtime.getRuntime().availableProcessors()
					* singleCpuThreadCount;
		}
		// 侦听连入线程池
		if (bossExecutor == null) {
			bossExecutor = Executors
					.newCachedThreadPool(new SimpleThreadFactory(
							"MileServer-bossExecutor-core", false));
		}
		// 处理消息线程池
		if (workerExecutor == null) {
			workerExecutor = Executors.newFixedThreadPool(workerExecutorCount);
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("workerExecutorCount:" + workerExecutorCount);
			}
		}
		// 消息侦听器管理器
		if (messageManager == null) {
			messageManager = new MessageManager(execMin, execMax, queryExecMin,
					queryExecMax, keepAliveTime, blockingQueueCount);
		}
		// 初始化连接器
		if (getMessageServerPipelineFactory() == null) {
			setMessageServerPipelineFactory(new MessageServerPipelineFactory(
					messageManager));
		}
		// 启动服务器
		startNewBootstrap(port);
	}

	
	
	/**
	 * 读取配置文件初始化参数
	 * 
	 * @param filePath
	 *            配置文件路径
	 */
	public void readProperties(String filePath) {
		Properties props = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					filePath));
			props.load(in);
			bossExecutorCount = Integer.parseInt(props.getProperty(
					"mile.server.boss.executor.thread.count", "0"));
			workerExecutorCount = Integer.parseInt(props.getProperty(
					"mile.server.worker.executor.thread.count", "0"));
			this.execMin = Integer.parseInt(props.getProperty(
					"mile.server.execMin.executor.thread.count", "5"));
			this.execMax = Integer.parseInt(props.getProperty(
					"mile.server.execMax.executor.thread.count", "5"));
			this.queryExecMin = Integer.parseInt(props.getProperty(
					"mile.server.queryExecMin.executor.thread.count", "5"));
			this.queryExecMax = Integer.parseInt(props.getProperty(
					"mile.server.queryExecMax.executor.thread.count", "5"));
			this.blockingQueueCount = Integer.parseInt(props.getProperty(
					"mile.server.executor.blockQueue.size", "20000"));
			this.keepAliveTime = Integer.parseInt(props.getProperty(
					"mile.server.executor.keepAliveTime.count", "10"));
			this.singleCpuThreadCount = Integer.parseInt(props.getProperty(
					"mile.server.cpu.thread.count", "2"));
		} catch (Exception e) {
			LOGGER.error("读取配置文件初始化参数", e);
		}
	}

	/**
	 * @return
	 */
	public ServerBootstrap getBootstrap() {
		return bootstrap;
	}

	/**
	 * @param bootstrap
	 */
	public void setBootstrap(ServerBootstrap bootstrap) {
		this.bootstrap = bootstrap;
	}

	/**
	 * @return
	 */
	public Executor getBossExecutor() {
		return bossExecutor;
	}

	/**
	 * @param bossExecutor
	 */
	public void setBossExecutor(Executor bossExecutor) {
		this.bossExecutor = bossExecutor;
	}

	/**
	 * @return
	 */
	public Executor getWorkerExecutor() {
		return workerExecutor;
	}

	/**
	 * @param workerExecutor
	 */
	public void setWorkerExecutor(Executor workerExecutor) {
		this.workerExecutor = workerExecutor;
	}

	/**
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return
	 */
	public MessageManager getMessageManager() {
		return messageManager;
	}

	/**
	 * @param messageManager
	 */
	public void setMessageManager(MessageManager messageManager) {
		this.messageManager = messageManager;
	}

	/**
	 * @param messageServerPipelineFactory
	 */
	public void setMessageServerPipelineFactory(
			MessageServerPipelineFactory messageServerPipelineFactory) {
		this.messageServerPipelineFactory = messageServerPipelineFactory;
	}

	/**
	 * @return
	 */
	public MessageServerPipelineFactory getMessageServerPipelineFactory() {
		return messageServerPipelineFactory;
	}

	/**
	 * @return
	 */
	public int getBossExecutorCount() {
		return bossExecutorCount;
	}

	/**
	 * @param bossExecutorCount
	 */
	public void setBossExecutorCount(int bossExecutorCount) {
		this.bossExecutorCount = bossExecutorCount;
	}

	/**
	 * @return
	 */
	public int getWorkerExecutorCount() {
		return workerExecutorCount;
	}

	/**
	 * @param workerExecutorCount
	 */
	public void setWorkerExecutorCount(int workerExecutorCount) {
		this.workerExecutorCount = workerExecutorCount;
	}

	public boolean isOnline() {
		return online.get();
	}

	public void setOnline(boolean online) {
		this.online.set(online);
	}

	public AtomicBoolean getOnline() {
		return online;
	}

	public void setOnline(AtomicBoolean online) {
		this.online = online;
	}

	public int getExecMin() {
		return execMin;
	}

	public void setExecMin(int execMin) {
		this.execMin = execMin;
	}

	public int getExecMax() {
		return execMax;
	}

	public void setExecMax(int execMax) {
		this.execMax = execMax;
	}

	public int getQueryExecMin() {
		return queryExecMin;
	}

	public void setQueryExecMin(int queryExecMin) {
		this.queryExecMin = queryExecMin;
	}

	public int getQueryExecMax() {
		return queryExecMax;
	}

	public void setQueryExecMax(int queryExecMax) {
		this.queryExecMax = queryExecMax;
	}

	public int getKeepAliveTime() {
		return keepAliveTime;
	}

	public void setKeepAliveTime(int keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}

	public int getBlockingQueueCount() {
		return blockingQueueCount;
	}

	public void setBlockingQueueCount(int blockingQueueCount) {
		this.blockingQueueCount = blockingQueueCount;
	}

}
