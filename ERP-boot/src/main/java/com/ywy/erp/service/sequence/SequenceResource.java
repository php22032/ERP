package com.ywy.erp.service.sequence;

import com.ywy.erp.service.ResourceInfo;

import java.lang.annotation.*;

/**
 * Description
 *
 * @Author:
 * @Date: 2021/3/16 16:33
 */
@ResourceInfo(value = "sequence")
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SequenceResource {
}
