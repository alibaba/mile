/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server.sharding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 规则数据封装类
 * @author jin.qian
 * @version $Id: TableRule.java, v 0.1 2011-5-10 下午03:22:36 jin.qian Exp $
 */
public class TableRule {
    //各列的sharding规则
    private Map<String, List<ShardingRule>> columnRules = new HashMap<String, List<ShardingRule>>();

    public Map<String, List<ShardingRule>> getColumnRules() {
        return columnRules;
    }

    public void setColumnRules(Map<String, List<ShardingRule>> columnRules) {
        this.columnRules = columnRules;
    }
}
