package com.hutong.supersdk.mysql;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 
 *<br><b>类描述:</b>
 *<pre>所示PO的父类</pre>
 *@see
 *@since
 */
public abstract class ABaseDomain implements Serializable
{
    /**
	 * 
	 */
	protected static final long serialVersionUID = 1L;

	public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
