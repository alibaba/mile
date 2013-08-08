/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server.merge;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.alipay.mile.Constants;
import com.alipay.mile.communication.MileClient;
import com.alipay.mile.communication.Node;
import com.alipay.mile.communication.ServerRef;
import com.alipay.mile.server.CheckDocServersTimerTask;
import com.alipay.mile.util.SimpleThreadFactory;

/**
 * DocServer 连接代理
 * @author jin.qian
 * @version $Id: DocumentServerPolicy.java, v 0.1 2011-4-21 上午10:46:19 jin.qian Exp $
 */
public class DocumentServerPolicy implements Policy {

    private static final Logger            LOGGER = Logger.getLogger(DocumentServerPolicy.class
                                                      .getName());

    /** 检查连接有效性的调度器 */
    private final ScheduledExecutorService timer  = Executors.newScheduledThreadPool(1,
                                                      new SimpleThreadFactory(
                                                          "CheckDocServerThreadGroup", true));

    /** 通信client */
    private final MileClient               engineConnector;

    /** 配置文件 */
    private final File                     confFile;

    /**
     * @param confFile
     * @param engineConnector
     */
    public DocumentServerPolicy(File confFile, MileClient engineConnector) {
        super();
        this.confFile = confFile;
        this.engineConnector = engineConnector;
        initConnetDocServers(getDocumentServers());
    }

    @Override
    public void checkDocServerHealth() {
        timer.scheduleAtFixedRate(new CheckDocServersTimerTask(engineConnector), 10, 10,
            TimeUnit.SECONDS);
    }

    private ServerRef getServerRef(Element nodeElement, Integer serverId)
                                                                         throws DataConversionException {
        ServerRef serverRef = new ServerRef();
        serverRef.setServerIp(nodeElement.getChildTextTrim("Ip"));
        serverRef.setPort(Integer.parseInt(nodeElement.getChildTextTrim("Port")));
        serverRef.setServerId(serverId);
        return serverRef;
    }

    /**
     * 解析所有docserver的配置文件，格式如下
     * <Docservers>
     * <Node>
     *  <ID></ID>
     *  <Master>
     *      <Ip></Ip>
     *      <Port></Port>
     *  </Master>
     *  <Slave>
     *      <Ip></Ip>
     *      <Port></Port>
     *  </Slave>
     * </Node>
     * </Docservers>
     *
     * @return 所有docserver的信息
     */
    @SuppressWarnings("unchecked")
    private List<Node> getDocumentServers() {
        List<Node> servers = new ArrayList<Node>();
        SAXBuilder sb = new SAXBuilder();
        try {

            Document domDoc = sb.build(confFile);
            Element root = domDoc.getRootElement();

            //解析根节点
            if (!StringUtils.equals(root.getName(), "Docservers")) {
                LOGGER.error("配置文件解析错误");
                return servers;
            }

            //解析Node
            Iterator<Element> nodeIter = (Iterator<Element>) root.getChildren("Node").iterator();

            while (nodeIter.hasNext()) {
                Element nodeElement = nodeIter.next();

                //获取serverID
                Integer serverId = Integer.parseInt(nodeElement.getChildTextTrim("ID"));

                //获取master结点
                Element masteElement = nodeElement.getChild("Master");

                //初始化一个serverRef，并且将它添加到servers列表里
                ServerRef serverRef = getServerRef(masteElement, serverId);

                //将serverRef的身份设置为master状态
                serverRef.setIdentity(Constants.MASTER);

                //初始化一个node
                Node node = new Node(serverId, serverRef);

                //存入到MAP中
                engineConnector.getNodes().putIfAbsent(serverId, node);

                //初始化slave
                Iterator<Element> slaveIter = (Iterator<Element>) nodeElement.getChildren("Slave")
                    .iterator();
                while (slaveIter.hasNext()) {
                    Element slaveElement = slaveIter.next();

                    //初始化一个serverRef，并且将它添加到servers列表里
                    ServerRef slaveRef = getServerRef(slaveElement, serverId);

                    //对node设置slave
                    node.addSlaveDoc(slaveRef);
                }
                servers.add(node);

            }
        } catch (Exception e) {
            LOGGER.error("解析配置文件出错");
        }

        return servers;
    }

    /**
     * 按照Node层级，初始化服务器
     * @param servers
     */
    private void initConnetDocServers(List<Node> servers) {
        for (Node node : servers) {
            connectDocServer(node.getMasterDoc());
            for (ServerRef sRef : node.getSlaveDocs()) {
                connectDocServer(sRef);
            }
        }
    }

    /**
     * 连接DocServer
     * @param sr
     */
    private ServerRef connectDocServer(ServerRef sr) {
        Channel channel = engineConnector.getConnectedChannel(sr.getServerIp(), sr.getPort());
        if (channel != null && channel.isConnected()) {
            sr.setChannel(channel);
            if (CheckDocServersTimerTask.getDocServerState(channel, engineConnector)) {
                sr.setAvailable(true);
                engineConnector.getServerRefOk().add(sr);
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Connect DocServer success...->" + channel.getLocalAddress() + "->"
                                + channel.getRemoteAddress());
                }
            } else {
                sr.setAvailable(false);
                engineConnector.getServerRefFail().add(sr);
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Connect DocServer success but no available->"
                                + channel.getLocalAddress() + "->" + channel.getRemoteAddress());
                }
            }

        } else {
            sr.setAvailable(false);
            engineConnector.getServerRefFail().add(sr);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Connect DocServer failed...->" + "->" + sr.getServerIp() + ":"
                            + sr.getPort());
            }
        }
        return sr;
    }
}
