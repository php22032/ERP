package com.ywy.erp.service.user;

import com.ywy.erp.service.ResourceInfo;

import java.lang.annotation.*;

/**
 * @author  
 */
@ResourceInfo(value = "user")
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserResource {
}
