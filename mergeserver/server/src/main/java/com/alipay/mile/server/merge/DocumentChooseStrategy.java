/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile.server.merge;

import java.util.Collection;
import java.util.List;

import com.alipay.mile.communication.ServerRef;

/**
 * 用于选择合适的docserver，上层获取到需要发送到的docserver列表，不需要关心下层使用的什么规则
 * 有两种规则，一种是读写权重，一种是Roundbin模式，如果有sharding的话，两种规则都需要先做sharding
 * @author yunliang.shi
 * @version $Id: DocumentChooseStrategy.java, v 0.1 2011-7-6 下午01:56:39 yunliang.shi Exp $
 */
public interface DocumentChooseStrategy {

    /**
     * 根据serverId选择可读的docserver
     * @param serverId
     * @return
     */
    ServerRef chooseReadDocumentServerById(int serverId);

    /**
     * 根据serverId选择可写的docserver
     * @param serverId
     * @return
     */
    ServerRef chooseWriteDocumentServerById(int serverId);

    /**
     * 根据插入选择合适的docserver
     * 
     * @param nodeIds
     * @return
     */
    ServerRef chooseInsertDocumentServer(Collection<Integer> nodeIds);

    /**
     * 根据查询的参数选择合适的docserver
     * 
     * @param nodeIds
     * @return
     */
    List<ServerRef> chooseQueryDocumentServer(Collection<Integer> nodeIds);

    /**
     * 在delete和update的时候选择合适的docserver
     * 
     * @param nodeIds
     * @return
     */
    List<ServerRef> chooseChangeDocumentServer(Collection<Integer> nodeIds);

}
