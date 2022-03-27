package com.ywy.erp.mappers;

import com.ywy.erp.entities.Msg;
import com.ywy.erp.entities.MsgExample;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface MsgMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table msg
     *
     * @mbggenerated
     */
    int countByExample(MsgExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table msg
     *
     * @mbggenerated
     */
    int deleteByExample(MsgExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table msg
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table msg
     *
     * @mbggenerated
     */
    int insert(Msg record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table msg
     *
     * @mbggenerated
     */
    int insertSelective(Msg record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table msg
     *
     * @mbggenerated
     */
    List<Msg> selectByExample(MsgExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table msg
     *
     * @mbggenerated
     */
    Msg selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table msg
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") Msg record, @Param("example") MsgExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table msg
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") Msg record, @Param("example") MsgExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table msg
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(Msg record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table msg
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(Msg record);
}