package com.ywy.erp.service.platformConfig;

import com.ywy.erp.service.ResourceInfo;

import java.lang.annotation.*;

/**
 * @author  qq752718920  2020-10-16 22:26:27
 */
@ResourceInfo(value = "platformConfig")
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PlatformConfigResource {
}
