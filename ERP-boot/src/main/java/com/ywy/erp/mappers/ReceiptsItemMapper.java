package com.ywy.erp.mappers;

import com.ywy.erp.entities.ReceiptsItem;
import com.ywy.erp.entities.ReceiptsItemExample;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface ReceiptsItemMapper {
    long countByExample(ReceiptsItemExample example);

    int deleteByExample(ReceiptsItemExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ReceiptsItem record);

    int insertSelective(ReceiptsItem record);

    List<ReceiptsItem> selectByExample(ReceiptsItemExample example);

    ReceiptsItem selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ReceiptsItem record, @Param("example") ReceiptsItemExample example);

    int updateByExample(@Param("record") ReceiptsItem record, @Param("example") ReceiptsItemExample example);

    int updateByPrimaryKeySelective(ReceiptsItem record);

    int updateByPrimaryKey(ReceiptsItem record);
}