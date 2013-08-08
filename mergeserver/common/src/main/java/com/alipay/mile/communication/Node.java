/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile.communication;

import java.util.ArrayList;
import java.util.List;


/**
 * 一个结点的定义，包含了一个身份为master的docserver，和若干个slave
 * @author yunliang.shi
 * @version $Id: Node.java, v 0.1 2011-7-6 下午03:13:01 yunliang.shi Exp $
 */
public class Node {

    /**
     * serverId
     */
    private int             serverId;

    /**
     * master身份的docserver
     */
    private ServerRef       masterDoc;

    /**slave身份的docserver*/
    private List<ServerRef> slaveDocs;

    /**
     * 一个Node必须有master
     */
    public Node(int serverId, ServerRef masterDoc) {
        this.slaveDocs = new ArrayList<ServerRef>();
        this.masterDoc = masterDoc;
        this.serverId = serverId;
        masterDoc.setServerId(serverId);
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    /**
     * 向slave添加一个docserver
     * 
     * @param slave
     */
    public void addSlaveDoc(ServerRef slave) {
        slaveDocs.add(slave);
        slave.setServerId(serverId);
    }

    /**
     * 判断结点是否为可插入有效，结点的有效性取决于master的有效性
     * 
     * @return
     */
    public boolean isInsertAvailable() {
        if (masterDoc == null) {
            return false;
        } else {
            return masterDoc.isAvailable() && masterDoc.isOnline();
        }
    }

    /**
     * 判断结点是否为查询有效
     * 
     * @return
     */
    public boolean isSelectAvailable() {
        boolean flag = false;
        if (masterDoc != null) {
            flag = flag || (masterDoc.isAvailable() && masterDoc.isOnline());
        }
        if (!slaveDocs.isEmpty()) {
            for (ServerRef srf : slaveDocs) {
                flag = flag || (srf.isAvailable() && srf.isOnline());
            }
        }
        return flag;
    }

    public ServerRef getMasterDoc() {
        return masterDoc;
    }

    public void setMasterDoc(ServerRef masterDoc) {
        this.masterDoc = masterDoc;
    }

    public List<ServerRef> getSlaveDocs() {
        return slaveDocs;
    }

    public void setSlaveDocs(List<ServerRef> slaveDocs) {
        this.slaveDocs = slaveDocs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("m:").append(this.masterDoc.getServerIp()).append(" ").append(
            this.masterDoc.getPort());
        if (this.getSlaveDocs().size() > 0) {
            sb.append(",s:");
            List<ServerRef> slaveDocs = this.getSlaveDocs();
            for (int i = 0; i < slaveDocs.size(); i++) {
                ServerRef ssRef = slaveDocs.get(i);
                if (i == slaveDocs.size() - 1) {
                    sb.append(ssRef.getServerIp()).append(" ").append(ssRef.getPort());
                } else {
                    sb.append(ssRef.getServerIp()).append(" ").append(ssRef.getPort()).append(",");
                }
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
