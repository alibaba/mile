/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.communication;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import com.alipay.mile.message.Message;

/**
 * @author jin.qian
 * @version $Id: SendFuture.java,v 0.1 2011-4-6 下午05:27:38 jin.qian Exp $
 * Future 实现类
 */
public class SendFuture implements Future<Message> {

    private static final Logger       LOGGER = Logger.getLogger(SendFuture.class.getName());

    private Channel                   channel;
    private int                       timeOut;
    private Message                   result;
    private long                      sendTime;
    private long                      writeTime;
    private long                      resultTime;
    private int                       messageId;

    private boolean                   done   = false;
    //    private static final Throwable    CANCELLED = new Throwable();
    //    private Throwable                 cause;

    private MileClientChannelPipeline mileChannelPipeline;

    public SendFuture(int messageId, Channel channel, int timeOut) {
        this.messageId = messageId;
        this.channel = channel;
        this.timeOut = timeOut;
        sendTime = System.currentTimeMillis();
        mileChannelPipeline = (MileClientChannelPipeline) channel.getPipeline();
    }

    /**
     * @param mayInterruptIfRunning
     * @return
     * @see java.util.concurrent.Future#cancel(boolean)
     * 取消Future 请求
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        //        if (!mayInterruptIfRunning) {
        //            return false;
        //        }
        //
        //        synchronized (this) {
        //            if (done) {
        //                notifyAll();
        //                return false;
        //            }
        //            //            cause = CANCELLED;
        //            done = true;
        //            notifyAll();
        //        }
        //        if (done) {
        //            mileChannelPipeline.getSendDataHandles().remove(messageId);
        //        }
        return false;
    }

    /**
     * @return
     * @see java.util.concurrent.Future#isCancelled()
     *
     */
    //    @Override
    //    public synchronized boolean isCancelled() {
    //        return cause == CANCELLED;
    //    }

    /**
     * @return
     * @see java.util.concurrent.Future#isDone()
     */
    @Override
    public boolean isDone() {
        //        synchronized (this) {
        return done;
        //        }
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    /**
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @see java.util.concurrent.Future#get()
     * 阻塞式取得数据
     */
    @Override
    public Message get() throws InterruptedException, ExecutionException {
        if (result == null) {
            long time = (timeOut + sendTime) - System.currentTimeMillis();
            synchronized (this) {
                if (time > 0 && result == null) {
                    wait(time);
                }
            }
        }
        return result;
    }

    /**
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
     * 自定义超时时间
     */
    @Override
    public Message get(long timeout, TimeUnit unit) throws InterruptedException,
                                                   ExecutionException, TimeoutException {
        if (result == null) {
            long time = (unit.toMillis(timeout) + sendTime) - System.currentTimeMillis();
            synchronized (this) {
                if (time > 0 && result == null) {
                    wait(time);
                }
            }
        }
        resultTime = System.currentTimeMillis();
        return result;
    }

    /**
     * @return
     */
    //    public synchronized Throwable getCause() {
    //        if (cause != CANCELLED) {
    //            return cause;
    //        } else {
    //            return null;
    //        }
    //    }

    /**
     *
     * @see java.lang.Runnable#run()
     * 非等待发送数据
     */
    //	@Override
    public boolean send(byte[] data) {
        mileChannelPipeline.getSendDataHandles().put(messageId, this);
        boolean sendok = false;
        sendok = channel.write(data).awaitUninterruptibly().isSuccess();
        writeTime = System.currentTimeMillis();
        if (sendok) {
            done = false;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("put MessageId: " + messageId);
            }
        } else {
            mileChannelPipeline.getSendDataHandles().remove(messageId);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("remove MessageId: " + messageId);
            }
        }
        return sendok;
        //        channel.write(data);
        //        return false;
    }

    /**
     * @param result
     */
    public void setResult(Message result) {
        this.result = result;
    }

    /**
     * @return
     */
    public Message getResult() {
        return result;
    }

    /**
     * @param sendTime
     */
    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    /**
     * @return
     */
    public long getSendTime() {
        return sendTime;
    }

    /**
     * @param timeOut
     */
    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    /**
     * @return
     */
    public int getTimeOut() {
        return timeOut;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public MileClientChannelPipeline getMileChannelPipeline() {
        return mileChannelPipeline;
    }

    public void setMileChannelPipeline(MileClientChannelPipeline mileChannelPipeline) {
        this.mileChannelPipeline = mileChannelPipeline;
    }

    public long getResultTime() {
        return resultTime;
    }

    public void setResultTime(long resultTime) {
        this.resultTime = resultTime;
    }

    public long getWriteTime() {
        return writeTime;
    }

    public void setWriteTime(long writeTime) {
        this.writeTime = writeTime;
    }

}
