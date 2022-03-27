package com.ywy.erp.mappers;

import com.ywy.erp.entities.PlatformConfig;
import com.ywy.erp.entities.PlatformConfigExample;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface PlatformConfigMapper {
    long countByExample(PlatformConfigExample example);

    int deleteByExample(PlatformConfigExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PlatformConfig record);

    int insertSelective(PlatformConfig record);

    List<PlatformConfig> selectByExample(PlatformConfigExample example);

    PlatformConfig selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PlatformConfig record, @Param("example") PlatformConfigExample example);

    int updateByExample(@Param("record") PlatformConfig record, @Param("example") PlatformConfigExample example);

    int updateByPrimaryKeySelective(PlatformConfig record);

    int updateByPrimaryKey(PlatformConfig record);
}