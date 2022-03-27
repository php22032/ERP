package com.ywy.erp.service.msg;

import com.ywy.erp.service.ResourceInfo;

import java.lang.annotation.*;

/**
 * @author  qq752718920  2019-9-7 22:52:35
 */
@ResourceInfo(value = "msg")
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MsgResource {
}
