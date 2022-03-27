package com.ywy.erp.service.materialAttribute;

import com.ywy.erp.service.ResourceInfo;

import java.lang.annotation.*;

/**
 * @author  qq752718920  2021-07-21 22:26:27
 */
@ResourceInfo(value = "materialAttribute")
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MaterialAttributeResource {
}
