package com.ywy.erp.service.materialExtend;

import com.ywy.erp.service.ResourceInfo;

import java.lang.annotation.*;

/**
 * @author  
 */
@ResourceInfo(value = "materialExtend")
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MaterialExtendResource {
}
