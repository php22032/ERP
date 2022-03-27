package com.ywy.erp.service.role;

import com.ywy.erp.service.ResourceInfo;

import java.lang.annotation.*;

/**
 * @author  
 */
@ResourceInfo(value = "role")
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleResource {
}
