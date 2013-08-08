/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.communication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.alipay.mile.message.AbstractMessage;
import com.alipay.mile.message.Message;
import com.alipay.mile.message.MessageFactory;
import com.alipay.mile.util.SimpleThreadFactory;

/**
 * @author jin.qian, yuzhong.zhao
 * @version $Id: MileClient.java,v 0.1 2011-4-6 上午11:10:32 jin.qian Exp $
 */
public class MileClient {

    private static final Logger          LOGGER              = Logger.getLogger(MileClient.class
                                                                 .getName());
    /**通讯连接启动器  */
    private ClientBootstrap              bootstrap;

    /**侦听连接线程池  */
    private Executor                     bossExecutor;
    /**通讯工作线程池*/
    private Executor                     workerExecutor;
    /**过滤器链  */
    private MessageClientPipelineFactory messageClientPipelineFactory;
    /**侦听连接线程数  */
    private int                          bossExecutorCount   = 0;
    /**通讯工作线数  */
    private int                          workerExecutorCount = 0;
    /** 节点号，和Node的map映射*/
    private ConcurrentMap<Integer, Node> nodes               = new ConcurrentHashMap<Integer, Node>();
    /**可用服务器列表  */
    private List<ServerRef>              serverRefOk         = new CopyOnWriteArrayList<ServerRef>();
    /**非可用服务器列表  */
    private List<ServerRef>              serverRefFail       = new CopyOnWriteArrayList<ServerRef>();

    /**SendFuture集合*/
    private Map<Integer, SendFuture>     sendDataHandles     = new ConcurrentHashMap<Integer, SendFuture>();
    /**消息id计数器  */
    private AtomicInteger                sendDataHandleID    = new AtomicInteger();
    /**清除sendeFuture定时调度器  */
    public ScheduledExecutorService      timer               = Executors
                                                                 .newScheduledThreadPool(
                                                                     1,
                                                                     new SimpleThreadFactory(
                                                                         "MileClient_SendFutureCleanThreadGroup",
                                                                         false));

    /**
     * 启动Clent 并配置默认参数
     */
    public void customBootstrap() {
        //bossExecutor 线程 默认为 cpu*2
        if (bossExecutorCount == 0) {
            bossExecutorCount = Runtime.getRuntime().availableProcessors() * 2;
        }
        //workerExecutor 线程 默认为 cpu*2
        if (workerExecutorCount == 0) {
            workerExecutorCount = Runtime.getRuntime().availableProcessors() * 2;
        }
        //侦听连入线程池
        if (bossExecutor == null) {
            //            bossExecutor = Executors.newFixedThreadPool(bossExecutorCount);
            bossExecutor = Executors.newCachedThreadPool(new SimpleThreadFactory(
                "MileClient-bossExecutor-core", false));
        }
        //处理消息线程池
        if (workerExecutor == null) {
            //            workerExecutor = Executors.newFixedThreadPool(workerExecutorCount);
            workerExecutor = Executors.newFixedThreadPool(workerExecutorCount,
                new SimpleThreadFactory("MileClient-workerExecutor-core", false));
        }
        //过滤器链 初始化
        if (getMessageClientPipelineFactory() == null) {
            messageClientPipelineFactory = new MessageClientPipelineFactory(serverRefOk,
                serverRefFail, sendDataHandles);
        }
        ChannelFactory channelFactory = new NioClientSocketChannelFactory(bossExecutor,
            workerExecutor, workerExecutorCount);
        //初始化 bootstrap
        bootstrap = new ClientBootstrap(channelFactory);
        bootstrap.setPipelineFactory(getMessageClientPipelineFactory());
        bootstrap.setOption("child.tcpNoDelay", true);
        //设置tpcip长连接 心跳功能开启
        bootstrap.setOption("child.keepAlive", true);
        //启动SendFuture清理器
        timer.scheduleAtFixedRate(new SendFutureCleanTimerTask(sendDataHandles), 1, 60,
            TimeUnit.SECONDS);
    }


    /**
     * 关闭client并断开连接
     */
    public void close() {
        timer.shutdown();
        for (ServerRef sr : serverRefOk) {
            sr.getChannel().disconnect().awaitUninterruptibly(5, TimeUnit.SECONDS);
        }
        for (ServerRef sr : serverRefFail) {
            sr.getChannel().disconnect().awaitUninterruptibly(5, TimeUnit.SECONDS);
        }
        
        bootstrap.getFactory().releaseExternalResources();
    }

    
    
    
    /**
     * Future 模式 发送数据 发送请求时不阻塞线程，应用线程get时阻塞应用线程。
     * @param channel 通行channle
     * @param message 待发送的消息
     * @param TimeOut 发送超时
     * @param autoMessageId 自动分配消息Id 这个消息id是查找返回时的调用对象
     * @return SendFuture 对象
     * @throws IOException
     */
    public SendFuture futureSendData(Channel channel, Message message, int TimeOut,
                                     boolean autoMessageId) throws IOException {
        if (autoMessageId && message instanceof AbstractMessage) {
            AbstractMessage tempMag = (AbstractMessage) message;
            tempMag.setId(sendDataHandleID.incrementAndGet());
        }
        SendFuture task = new SendFuture(message.getId(), channel, TimeOut);
        task.send(MessageFactory.toSendMessage(message));
        return task;
    }

    /**
     * @param channel
     * @param message
     * @param autoMessageId
     * @return
     * @throws IOException
     * 非阻塞发送 消息，无返回值
     */
    public boolean sendData(Channel channel, Message message, boolean autoMessageId)
                                                                                    throws IOException {
        //发送状态标志
        boolean flag = false;
        if (autoMessageId && message instanceof AbstractMessage) {
            AbstractMessage tempMag = (AbstractMessage) message;
            //消息id赋值
            tempMag.setId(sendDataHandleID.incrementAndGet());
        }
        if (channel.isConnected()) {
            //将消息转换成byte[]发送、返回发送状态
            flag = channel.write(MessageFactory.toSendMessage(message)).awaitUninterruptibly()
                .isSuccess();
        }
        return flag;
    }

    /**
     * @param host 主机地址
     * @param port  端口号
     * @return 和主机建立的通讯channle
     * @throws Throwable
     * 得到一个通信channel
     */
    public Channel getConnectedChannel(String host, int port) {
        if (bootstrap == null) {
            customBootstrap();
        }
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
        Channel channel = future.awaitUninterruptibly().getChannel();
        if (future.getCause() != null) {
            LOGGER.warn("连接失败->" + host + ":" + port);
            //            throw future.getCause();
        }
        return channel;
    }

    /**
     * @return
     */
    public ClientBootstrap getBootstrap() {
        return bootstrap;
    }

    /**
     * @param bootstrap
     */
    public void setBootstrap(ClientBootstrap bootstrap) {
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
     * @param messageClientPipelineFactory
     */
    public void setMessageClientPipelineFactory(
                                                MessageClientPipelineFactory messageClientPipelineFactory) {
        this.messageClientPipelineFactory = messageClientPipelineFactory;
    }

    /**
     * @return
     */
    public MessageClientPipelineFactory getMessageClientPipelineFactory() {
        return messageClientPipelineFactory;
    }

    /**
     * @return
     */
    public List<ServerRef> getServerRefOk() {
        return serverRefOk;
    }

    /**
     * @param mergeServersOk
     */
    public void setServerRefOk(List<ServerRef> mergeServersOk) {
        this.serverRefOk = mergeServersOk;
    }

    /**
     * @return
     */
    public List<ServerRef> getServerRefFail() {
        return serverRefFail;
    }

    /**
     * @param mergeServersFail
     */
    public void setServerRefFail(List<ServerRef> mergeServersFail) {
        this.serverRefFail = mergeServersFail;
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

    public Map<Integer, SendFuture> getSendDataHandles() {
        return sendDataHandles;
    }

    public void setSendDataHandles(Map<Integer, SendFuture> sendDataHandles) {
        this.sendDataHandles = sendDataHandles;
    }

    public AtomicInteger getSendDataHandleID() {
        return sendDataHandleID;
    }

    public void setSendDataHandleID(AtomicInteger sendDataHandleID) {
        this.sendDataHandleID = sendDataHandleID;
    }

    public ConcurrentMap<Integer, Node> getNodes() {
        return nodes;
    }

    public void setNodes(ConcurrentMap<Integer, Node> nodes) {
        this.nodes = nodes;
    }
}
