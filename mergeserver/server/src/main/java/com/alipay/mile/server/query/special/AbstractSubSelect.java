/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server.query.special;




/**
 * @author bin.lb
 */
public abstract class AbstractSubSelect implements SubSelect {
	protected byte type;

	public AbstractSubSelect(byte type) {
		this.type = type;
	}

	@Override
	public byte getType() {
		return type;
	}

	@Override
	public void setType(byte type) {
		this.type = type;
	}

}
