/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server.query.special;


/**
 *
 * @author bin.lb
 *
 */
public class SubSelectResultFactory {
    public static SubSelectResult createFromDesc(SubSelectDesc desc) {
        if (desc instanceof MaxFuncSelectDesc) {
            return new MaxFuncResult();
        }
        //		else if (false) { // TODO
        //			return null;
        //		}
        //		else { // TODO
        //		    return null;
        //		}
        return null;
    }
}
