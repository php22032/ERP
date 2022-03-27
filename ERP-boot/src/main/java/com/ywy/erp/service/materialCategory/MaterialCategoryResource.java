package com.ywy.erp.service.materialCategory;

import com.ywy.erp.service.ResourceInfo;

import java.lang.annotation.*;

/**
 * @author jishenghua 
 */
@ResourceInfo(value = "materialCategory")
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MaterialCategoryResource {
}
