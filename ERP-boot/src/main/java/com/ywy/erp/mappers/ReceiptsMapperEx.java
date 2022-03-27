package com.ywy.erp.mappers;

import com.ywy.erp.entities.Receipts;
import com.ywy.erp.vo.ReceiptsVo4InDetail;
import com.ywy.erp.vo.ReceiptsVo4InOutMCount;
import com.ywy.erp.vo.ReceiptsVo4List;
import com.ywy.erp.vo.ReceiptsVo4StatementAccount;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Description
 *
 * @Author: cjl
 * @Date: 2019/1/25 14:50
 */
public interface ReceiptsMapperEx {
    List<ReceiptsVo4List> selectByConditionReceipts(
            @Param("type") String type,
            @Param("subType") String subType,
            @Param("creatorArray") String[] creatorArray,
            @Param("statusArray") String[] statusArray,
            @Param("number") String number,
            @Param("linkNumber") String linkNumber,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("materialParam") String materialParam,
            @Param("organId") Long organId,
            @Param("creator") Long creator,
            @Param("depotId") Long depotId,
            @Param("depotArray") String[] depotArray,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows);

    Long countsByReceipts(
            @Param("type") String type,
            @Param("subType") String subType,
            @Param("creatorArray") String[] creatorArray,
            @Param("statusArray") String[] statusArray,
            @Param("number") String number,
            @Param("linkNumber") String linkNumber,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("materialParam") String materialParam,
            @Param("organId") Long organId,
            @Param("creator") Long creator,
            @Param("depotId") Long depotId,
            @Param("depotArray") String[] depotArray);

    String findMaterialsListByHeaderId(
            @Param("id") Long id);

    List<ReceiptsVo4InDetail> findByAll(
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("type") String type,
            @Param("materialParam") String materialParam,
            @Param("depotList") List<Long> depotList,
            @Param("oId") Integer oId,
            @Param("number") String number,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows);

    int findByAllCount(
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("type") String type,
            @Param("materialParam") String materialParam,
            @Param("depotList") List<Long> depotList,
            @Param("oId") Integer oId,
            @Param("number") String number);

    List<ReceiptsVo4InOutMCount> findInOutMaterialCount(
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("type") String type,
            @Param("materialParam") String materialParam,
            @Param("depotList") List<Long> depotList,
            @Param("oId") Integer oId,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows);

    int findInOutMaterialCountTotal(
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("type") String type,
            @Param("materialParam") String materialParam,
            @Param("depotList") List<Long> depotList,
            @Param("oId") Integer oId);

    List<ReceiptsVo4InDetail> findAllocationDetail(
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("subType") String subType,
            @Param("number") String number,
            @Param("materialParam") String materialParam,
            @Param("depotList") List<Long> depotList,
            @Param("depotFList") List<Long> depotFList,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows);

    int findAllocationDetailCount(
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("subType") String subType,
            @Param("number") String number,
            @Param("materialParam") String materialParam,
            @Param("depotList") List<Long> depotList,
            @Param("depotFList") List<Long> depotFList);

    List<ReceiptsVo4StatementAccount> findStatementAccount(
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("organId") Integer organId,
            @Param("supType") String supType,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows);

    int findStatementAccountCount(
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("organId") Integer organId,
            @Param("supType") String supType);

    BigDecimal findAllMoney(
            @Param("supplierId") Integer supplierId,
            @Param("type") String type,
            @Param("subType") String subType,
            @Param("modeName") String modeName,
            @Param("endTime") String endTime);

    BigDecimal findAllOtherMoney(
            @Param("supplierId") Integer supplierId,
            @Param("type") String type,
            @Param("subType") String subType,
            @Param("endTime") String endTime);

    List<ReceiptsVo4List> getDetailByNumber(
            @Param("number") String number);

    int batchDeleteReceiptsByIds(@Param("updateTime") Date updateTime, @Param("updater") Long updater, @Param("ids") String ids[]);

    List<Receipts> getReceiptsListByAccountIds(@Param("accountIds") String[] accountIds);

    List<Receipts> getReceiptsListByOrganIds(@Param("organIds") String[] organIds);

    List<Receipts> getReceiptsListByCreator(@Param("creatorArray") String[] creatorArray);

    BigDecimal getBuyAndSaleStatistics(
            @Param("type") String type,
            @Param("subType") String subType,
            @Param("hasSupplier") Integer hasSupplier,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime);

    BigDecimal getBuyAndSaleRetailStatistics(
            @Param("type") String type,
            @Param("subType") String subType,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime);

    List<ReceiptsVo4List> debtList(
            @Param("organId") Long organId,
            @Param("type") String type,
            @Param("subType") String subType,
            @Param("creatorArray") String[] creatorArray,
            @Param("status") String status,
            @Param("number") String number,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("materialParam") String materialParam,
            @Param("depotArray") String[] depotArray);
}
