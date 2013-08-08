/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server.query.special;



public class SubSelectDescFactory {

    public static SubSelectDesc createFromSubSelect(SubSelect subSelect) {
        SubSelectDesc desc = null;
        if (subSelect instanceof MaxFuncSelect) {
            desc = new MaxFuncSelectDesc();
            desc.fromSubSelect(subSelect);
        }
        //		else if (false) { // TODO : 需要后期开发
        //
        //		}
        return desc;
    }
}
