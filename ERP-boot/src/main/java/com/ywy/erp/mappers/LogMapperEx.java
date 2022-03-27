package com.ywy.erp.mappers;

import com.ywy.erp.vo.LogVo4List;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LogMapperEx {

    List<LogVo4List> selectByConditionLog(
            @Param("operation") String operation,
            @Param("userInfo") String userInfo,
            @Param("clientIp") String clientIp,
            @Param("status") Integer status,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("content") String content,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows);

    Long countsByLog(
            @Param("operation") String operation,
            @Param("userInfo") String userInfo,
            @Param("clientIp") String clientIp,
            @Param("status") Integer status,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("content") String content);

    Long getCountByIpAndDate(
            @Param("moduleName") String moduleName,
            @Param("clientIp") String clientIp,
            @Param("createTime") String createTime);
}