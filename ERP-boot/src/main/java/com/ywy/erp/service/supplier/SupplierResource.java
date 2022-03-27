package com.ywy.erp.service.supplier;

import com.ywy.erp.service.ResourceInfo;

import java.lang.annotation.*;

/**
 * @author  
 */
@ResourceInfo(value = "supplier")
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SupplierResource {
}
