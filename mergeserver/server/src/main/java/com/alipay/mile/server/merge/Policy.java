/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server.merge;

/**
 * 代理DocServer接口
 * @author huabing.du
 * @version $Id: Policy.java, v 0.1 2011-5-11 下午09:39:41 huabing.du Exp $
 */
public interface Policy {

    /**
     * 检查docserver 健康状态
     */
    void checkDocServerHealth();
}
