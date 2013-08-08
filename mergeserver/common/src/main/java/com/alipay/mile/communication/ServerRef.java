/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.channel.Channel;

import com.alipay.mile.Config;
import com.alipay.mile.Constants;
import com.alipay.mile.message.KeyValueData;

/**
 * @author jin.qian
 * @version $Id: MergeServerInfo.java,v 0.1 2011-4-6 下午05:50:09 jin.qian Exp $
 *          服务器状态 BO
 */
public class ServerRef {

    /** docserver的身份，有master和slave之分，默认为SLAVE */
    private int                identity       = Constants.SLAVE;
    /** 服务器id */
    private int                serverId;
    /** 服务器名 */
    private String             serverName;
    /** 服务器IP */
    private String             serverIp;
    /** 服务器端口 */
    private int                port;
    /** 服务器状态 */
    private int                status;                                  // 1、 正常 3、负载 4、不可用 5、停用
    /** 服务器绑定的通讯channel */
    private Channel            channel;
    /**目前通道中的插入消息个数  */
    private AtomicInteger      insertMsgCount = new AtomicInteger(0);
    /**目前通道中的查询消息个数  */
    private AtomicInteger      queryMsgCount  = new AtomicInteger(0);
    /** 用户名 */
    private String             clientUserName;
    /** 用户密码 */
    private byte[]             clientPassWord;
    /** client 参数 */
    private List<KeyValueData> clientProperty;
    /** 服务器通讯sessionid */
    private int                sessionId;
    /** 服务器参数 */
    private List<KeyValueData> serverproperties;
    /** 服务器描述 */
    private String             serverDescription;
    /** 连接错误代码 */
    private short              connErrCode;
    /** 连接错误参数 */
    private List<Object>       errParameter   = new ArrayList<Object>();
    /** 错误描述 */
    private String             errDescription;
    /** 是否可用 */
    private boolean            available;
    /** 是否可用 */
    private boolean            online         = true;
    /** 通信版本号 */
    private short              version;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public List<KeyValueData> getServerproperties() {
        return serverproperties;
    }

    public void setServerproperties(List<KeyValueData> serverproperties) {
        this.serverproperties = serverproperties;
    }

    public String getServerDescription() {
        return serverDescription;
    }

    public void setServerDescription(String serverDescription) {
        this.serverDescription = serverDescription;
    }

    public String getClientUserName() {
        return clientUserName;
    }

    public void setClientUserName(String clientUserName) {
        this.clientUserName = clientUserName;
    }

    public byte[] getClientPassWord() {
        return clientPassWord;
    }

    public void setClientPassWord(byte[] clientPassWord) {
        this.clientPassWord = clientPassWord;
    }

    public List<KeyValueData> getClientProperty() {
        return clientProperty;
    }

    public void setClientProperty(List<KeyValueData> clientProperty) {
        this.clientProperty = clientProperty;
    }

    public short getConnErrCode() {
        return connErrCode;
    }

    public void setConnErrCode(short connErrCode) {
        this.connErrCode = connErrCode;
    }

    public List<Object> getErrParameter() {
        return errParameter;
    }

    public void setErrParameter(List<Object> errParameter) {
        this.errParameter = errParameter;
    }

    public String getErrDescription() {
        return errDescription;
    }

    public void setErrDescription(String errDescription) {
        this.errDescription = errDescription;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public short getVersion() {
        return version;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public int getIdentity() {
        return identity;
    }

    public void setIdentity(int identity) {
        this.identity = identity;
    }

    public void addInsertMsgCount() {
        this.insertMsgCount.getAndIncrement();
    }

    public void subInsertMsgCount() {
        this.insertMsgCount.getAndDecrement();
    }

    public void addQueryMsgCount() {
        this.queryMsgCount.getAndIncrement();
    }

    public void subQueryMsgCount() {
        this.queryMsgCount.getAndDecrement();
    }

    public boolean isInsertBusy() {
        return insertMsgCount.get() >= Config.getInsertQueueThreshold();
    }

    public boolean isQueryBusy() {
        return queryMsgCount.get() >= Config.getQueryQueueThreshold();
    }

    public AtomicInteger getInsertMsgCount() {
        return insertMsgCount;
    }

    public void setInsertMsgCount(AtomicInteger insertMsgCount) {
        this.insertMsgCount = insertMsgCount;
    }

    public AtomicInteger getQueryMsgCount() {
        return queryMsgCount;
    }

    public void setQueryMsgCount(AtomicInteger queryMsgCount) {
        this.queryMsgCount = queryMsgCount;
    }
}
