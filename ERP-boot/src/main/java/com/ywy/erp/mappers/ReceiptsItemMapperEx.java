package com.ywy.erp.mappers;

import com.ywy.erp.vo.ReceiptsItemStockWarningCount;
import com.ywy.erp.vo.ReceiptsItemVo4Stock;
import com.ywy.erp.vo.ReceiptsItemVoBatchNumberList;
import com.ywy.erp.entities.ReceiptsItem;
import com.ywy.erp.entities.ReceiptsItemVo4DetailByTypeAndMId;
import com.ywy.erp.entities.ReceiptsItemVo4WithInfoEx;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Description
 *
 * @Author: cjl
 * @Date: 2019/1/24 16:59
 */
public interface ReceiptsItemMapperEx {
    List<ReceiptsItem> selectByConditionReceiptsItem(
            @Param("name") String name,
            @Param("type") Integer type,
            @Param("remark") String remark,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows);

    Long countsByReceiptsItem(
            @Param("name") String name,
            @Param("type") Integer type,
            @Param("remark") String remark);

    List<ReceiptsItemVo4DetailByTypeAndMId> findDetailByTypeAndMaterialIdList(
            @Param("mId") Long mId,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows);

    Long findDetailByTypeAndMaterialIdCounts(
            @Param("mId") Long mId);

    List<ReceiptsItemVo4WithInfoEx> getDetailList(
            @Param("headerId") Long headerId);

    List<ReceiptsItemVo4WithInfoEx> findByAll(
            @Param("materialParam") String materialParam,
            @Param("endTime") String endTime,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows);

    int findByAllCount(
            @Param("materialParam") String materialParam,
            @Param("endTime") String endTime);

    BigDecimal buyOrSaleNumber(
            @Param("type") String type,
            @Param("subType") String subType,
            @Param("MId") Long MId,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("sumType") String sumType);

    BigDecimal buyOrSalePrice(
            @Param("type") String type,
            @Param("subType") String subType,
            @Param("MId") Long MId,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("sumType") String sumType);

    BigDecimal inOrOutPrice(
            @Param("type") String type,
            @Param("subType") String subType,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime);

    BigDecimal inOrOutRetailPrice(
            @Param("type") String type,
            @Param("subType") String subType,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime);

    BigDecimal getStockCheckSumByDepotList(
            @Param("depotList") List<Long> depotList,
            @Param("mId") Long mId,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime);

    ReceiptsItemVo4Stock getSkuStockByParam(
            @Param("depotId") Long depotId,
            @Param("meId") Long meId,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime);

    ReceiptsItemVo4Stock getStockByParamWithDepotList(
            @Param("depotList") List<Long> depotList,
            @Param("mId") Long mId,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime);

    /**
     * 通过单据主表id查询所有单据子表数据
     * @param depotheadId
     * @param enableSerialNumber
     * @return
     */
     List<ReceiptsItem> findReceiptsItemListBydepotheadId(@Param("depotheadId")Long depotheadId,
                                                       @Param("enableSerialNumber")String enableSerialNumber);
     /**
      * 根据单据主表id删除单据子表数据
      * */
     int batchDeleteReceiptsItemByReceiptsIds(@Param("depotheadIds")Long []receiptsIds);

    int batchDeleteReceiptsItemByIds(@Param("updateTime") Date updateTime, @Param("updater") Long updater, @Param("ids") String ids[]);

    List<ReceiptsItem> getReceiptsItemListListByDepotIds(@Param("depotIds") String[] depotIds);

    List<ReceiptsItem> getReceiptsItemListListByMaterialIds(@Param("materialIds") String[] materialIds);

    List<ReceiptsItemStockWarningCount> findStockWarningCount(
            @Param("offset") Integer offset,
            @Param("rows") Integer rows,
            @Param("materialParam") String materialParam,
            @Param("depotList") List<Long> depotList);

    int findStockWarningCountTotal(
            @Param("materialParam") String materialParam,
            @Param("depotList") List<Long> depotList);

    BigDecimal getFinishNumber(
            @Param("mId") Long mId,
            @Param("linkNumber") String linkNumber,
            @Param("goToType") String goToType);

    List<ReceiptsItemVoBatchNumberList> getBatchNumberList(
            @Param("name") String name,
            @Param("depotId") Long depotId,
            @Param("barCode") String barCode,
            @Param("batchNumber") String batchNumber
            );
}
