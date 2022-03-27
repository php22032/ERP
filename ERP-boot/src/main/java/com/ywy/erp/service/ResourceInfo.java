package com.ywy.erp.service;

import java.lang.annotation.*;

/**
 * @author  2022-2-7 15:25:39
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ResourceInfo {
    String value();
}