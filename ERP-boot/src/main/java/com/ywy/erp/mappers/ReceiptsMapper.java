package com.ywy.erp.mappers;

import com.ywy.erp.entities.Receipts;
import com.ywy.erp.entities.ReceiptsExample;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface ReceiptsMapper {
    long countByExample(ReceiptsExample example);

    int deleteByExample(ReceiptsExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Receipts record);

    int insertSelective(Receipts record);

    List<Receipts> selectByExample(ReceiptsExample example);

   Receipts selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record")Receipts record, @Param("example")ReceiptsExample example);

    int updateByExample(@Param("record")Receipts record, @Param("example")ReceiptsExample example);

    int updateByPrimaryKeySelective(Receipts record);

    int updateByPrimaryKey(Receipts record);
}