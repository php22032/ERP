package com.ywy.erp.service.tenant;

import com.ywy.erp.service.ResourceInfo;

import java.lang.annotation.*;

/**
 * @author  qq752718920  2019-6-27 22:56:56
 */
@ResourceInfo(value = "tenant")
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TenantResource {
}
