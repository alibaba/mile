/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server.query.special;

import java.io.DataInput;
import java.io.IOException;

import org.apache.log4j.Logger;



/**
 * 
 * @author bin.lb
 * 
 */
public class SubSelectFactory {

    private static final Logger LOGGER = Logger.getLogger(SubSelectFactory.class.getName());

    public static SubSelect createFromType(byte type) {
        switch (type) {
            case SubSelect.SUB_SELECT_TYPE_RAW_VALUE:
                return null;
            case SubSelect.SUB_SELECT_TYPE_COUNT_FUNC:
                // TODO
                return null;
            case SubSelect.SUB_SELECT_TYPE_MAX_FUNC:
                return new MaxFuncSelect();
            case SubSelect.SUB_SELECT_TYPE_MIN_FUNC:
                // TODO return new MinFuncSelect();
                return null;
            case SubSelect.SUB_SELECT_TYPE_SUM_FUNC:
                // TODO return new SumFuncSelect();
                return null;
            default:
                LOGGER.error("create SubSelect from type failed, type: " + type);
                return null;
        }
    }

    public static SubSelect createFromRequestStream(DataInput is) throws IOException {
        byte type = is.readByte();
        SubSelect subSelect = createFromType(type);
        subSelect.readRequestFromStream(is);
        return subSelect;
    }
}
