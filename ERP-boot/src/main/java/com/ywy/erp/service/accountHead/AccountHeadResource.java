package com.ywy.erp.service.accountHead;

import com.ywy.erp.service.ResourceInfo;

import java.lang.annotation.*;

/**
 * @author  
 */
@ResourceInfo(value = "accountHead")
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccountHeadResource {
}
