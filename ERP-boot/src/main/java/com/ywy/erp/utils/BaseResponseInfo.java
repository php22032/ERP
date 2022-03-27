package com.ywy.erp.utils;

/**
 *  @description 响应信息
 */
public class BaseResponseInfo {
	public int code;
	public Object data;
	
	public BaseResponseInfo() {
		code = 400;
		data = null;
	}
}
