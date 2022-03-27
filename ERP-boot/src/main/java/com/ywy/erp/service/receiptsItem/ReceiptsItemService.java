package com.ywy.erp.service.receiptsItem;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ywy.erp.constants.BusinessConstants;
import com.ywy.erp.constants.ExceptionConstants;

import com.ywy.erp.vo.ReceiptsItemStockWarningCount;
import com.ywy.erp.vo.ReceiptsItemVo4Stock;
import com.ywy.erp.vo.ReceiptsItemVoBatchNumberList;
import com.ywy.erp.entities.*;
import com.ywy.erp.exception.BusinessRunTimeException;
import com.ywy.erp.exception.JshException;
import com.ywy.erp.mappers.*;
import com.ywy.erp.service.materialExtend.MaterialExtendService;
import com.ywy.erp.service.log.LogService;
import com.ywy.erp.service.material.MaterialService;
import com.ywy.erp.service.serialNumber.SerialNumberService;
import com.ywy.erp.service.systemConfig.SystemConfigService;
import com.ywy.erp.service.user.UserService;
import com.ywy.erp.utils.QueryUtils;
import com.ywy.erp.utils.StringUtil;
import com.ywy.erp.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

@Service
public class ReceiptsItemService {
    private Logger logger = LoggerFactory.getLogger(ReceiptsItemService.class);

    private final static String TYPE = "入库";
    private final static String SUM_TYPE = "number";
    private final static String IN = "in";
    private final static String OUT = "out";

    @Resource
    private ReceiptsItemMapper receiptsItemMapper;
    @Resource
    private ReceiptsItemMapperEx receiptsItemMapperEx;
    @Resource
    private MaterialService materialService;
    @Resource
    private MaterialExtendService materialExtendService;
    @Resource
    SerialNumberMapperEx serialNumberMapperEx;
    @Resource
    private ReceiptsMapper receiptsMapper;
    @Resource
    SerialNumberService serialNumberService;
    @Resource
    private UserService userService;
    @Resource
    private SystemConfigService systemConfigService;
    @Resource
    private MaterialCurrentStockMapper materialCurrentStockMapper;
    @Resource
    private LogService logService;

    public ReceiptsItem getReceiptsItem(long id)throws Exception {
        ReceiptsItem result=null;
        try{
            result=receiptsItemMapper.selectByPrimaryKey(id);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<ReceiptsItem> getReceiptsItem()throws Exception {
        ReceiptsItemExample example = new ReceiptsItemExample();
        example.createCriteria().andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<ReceiptsItem> list=null;
        try{
            list=receiptsItemMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<ReceiptsItem> select(String name, Integer type, String remark, int offset, int rows)throws Exception {
        List<ReceiptsItem> list=null;
        try{
            list=receiptsItemMapperEx.selectByConditionReceiptsItem(name, type, remark, offset, rows);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public Long countReceiptsItem(String name, Integer type, String remark) throws Exception{
        Long result =null;
        try{
            result=receiptsItemMapperEx.countsByReceiptsItem(name, type, remark);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertReceiptsItem(JSONObject obj, HttpServletRequest request)throws Exception {
        ReceiptsItem receiptsItem = JSONObject.parseObject(obj.toJSONString(), ReceiptsItem.class);
        int result =0;
        try{
            result=receiptsItemMapper.insertSelective(receiptsItem);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateReceiptsItem(JSONObject obj, HttpServletRequest request)throws Exception {
        ReceiptsItem receiptsItem = JSONObject.parseObject(obj.toJSONString(), ReceiptsItem.class);
        int result =0;
        try{
            result=receiptsItemMapper.updateByPrimaryKeySelective(receiptsItem);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteReceiptsItem(Long id, HttpServletRequest request)throws Exception {
        int result =0;
        try{
            result=receiptsItemMapper.deleteByPrimaryKey(id);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteReceiptsItem(String ids, HttpServletRequest request)throws Exception {
        List<Long> idList = StringUtil.strToLongList(ids);
        ReceiptsItemExample example = new ReceiptsItemExample();
        example.createCriteria().andIdIn(idList);
        int result =0;
        try{
            result=receiptsItemMapper.deleteByExample(example);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    public int checkIsNameExist(Long id, String name)throws Exception {
        ReceiptsItemExample example = new ReceiptsItemExample();
        example.createCriteria().andIdNotEqualTo(id).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<ReceiptsItem> list =null;
        try{
            list=receiptsItemMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list==null?0:list.size();
    }

    public List<ReceiptsItemVo4DetailByTypeAndMId> findDetailByTypeAndMaterialIdList(Map<String, String> map)throws Exception {
        String mIdStr = map.get("mId");
        Long mId = null;
        if(!StringUtil.isEmpty(mIdStr)) {
            mId = Long.parseLong(mIdStr);
        }
        List<ReceiptsItemVo4DetailByTypeAndMId> list =null;
        try{
            list = receiptsItemMapperEx.findDetailByTypeAndMaterialIdList(mId, QueryUtils.offset(map), QueryUtils.rows(map));
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public Long findDetailByTypeAndMaterialIdCounts(Map<String, String> map)throws Exception {
        String mIdStr = map.get("mId");
        Long mId = null;
        if(!StringUtil.isEmpty(mIdStr)) {
            mId = Long.parseLong(mIdStr);
        }
        Long result =null;
        try{
            result = receiptsItemMapperEx.findDetailByTypeAndMaterialIdCounts(mId);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertReceiptsItemWithObj(ReceiptsItem receiptsItem)throws Exception {
        int result =0;
        try{
            result = receiptsItemMapper.insertSelective(receiptsItem);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateReceiptsItemWithObj(ReceiptsItem receiptsItem)throws Exception {
        int result =0;
        try{
            result = receiptsItemMapper.updateByPrimaryKeySelective(receiptsItem);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    public List<ReceiptsItem> getListByHeaderId(Long headerId)throws Exception {
        List<ReceiptsItem> list =null;
        try{
            ReceiptsItemExample example = new ReceiptsItemExample();
            example.createCriteria().andHeaderIdEqualTo(headerId);
            list = receiptsItemMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<ReceiptsItemVo4WithInfoEx> getDetailList(Long headerId)throws Exception {
        List<ReceiptsItemVo4WithInfoEx> list =null;
        try{
            list = receiptsItemMapperEx.getDetailList(headerId);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<ReceiptsItemVo4WithInfoEx> findByAll(String materialParam, String endTime, Integer offset, Integer rows)throws Exception {
        List<ReceiptsItemVo4WithInfoEx> list =null;
        try{
            list = receiptsItemMapperEx.findByAll(materialParam, endTime, offset, rows);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public int findByAllCount(String materialParam, String endTime)throws Exception {
        int result=0;
        try{
            result = receiptsItemMapperEx.findByAllCount(materialParam, endTime);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public BigDecimal buyOrSale(String type, String subType, Long MId, String monthTime, String sumType) throws Exception{
        BigDecimal result= BigDecimal.ZERO;
        try{
            String beginTime = Tools.firstDayOfMonth(monthTime) + BusinessConstants.DAY_FIRST_TIME;
            String endTime = Tools.lastDayOfMonth(monthTime) + BusinessConstants.DAY_LAST_TIME;
            if (SUM_TYPE.equals(sumType)) {
                result= receiptsItemMapperEx.buyOrSaleNumber(type, subType, MId, beginTime, endTime, sumType);
            } else {
                result= receiptsItemMapperEx.buyOrSalePrice(type, subType, MId, beginTime, endTime, sumType);
            }
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;

    }

    /**
     * 统计采购或销售的总金额
     * @param type
     * @param subType
     * @param month
     * @return
     * @throws Exception
     */
    public BigDecimal inOrOutPrice(String type, String subType, String month) throws Exception{
        BigDecimal result= BigDecimal.ZERO;
        try{
            String beginTime = Tools.firstDayOfMonth(month) + BusinessConstants.DAY_FIRST_TIME;
            String endTime = Tools.lastDayOfMonth(month) + BusinessConstants.DAY_LAST_TIME;
            result = receiptsItemMapperEx.inOrOutPrice(type, subType, beginTime, endTime);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 统计零售的总金额
     * @param type
     * @param subType
     * @param month
     * @return
     * @throws Exception
     */
    public BigDecimal inOrOutRetailPrice(String type, String subType, String month) throws Exception{
        BigDecimal result= BigDecimal.ZERO;
        try{
            String beginTime = Tools.firstDayOfMonth(month) + BusinessConstants.DAY_FIRST_TIME;
            String endTime = Tools.lastDayOfMonth(month) + BusinessConstants.DAY_LAST_TIME;
            result = receiptsItemMapperEx.inOrOutRetailPrice(type, subType, beginTime, endTime);
            result = result.abs();
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void saveDetials(String rows, Long headerId, HttpServletRequest request) throws Exception{
        //查询单据主表信息
        Receipts receipts =receiptsMapper.selectByPrimaryKey(headerId);
        //获得当前操作人
        User userInfo=userService.getCurrentUser();
        //首先回收序列号，如果是调拨，不用处理序列号
        if(BusinessConstants.DEPOTHEAD_TYPE_OUT.equals(receipts.getType())
            &&!BusinessConstants.SUB_TYPE_TRANSFER.equals(receipts.getSubType())){
            List<ReceiptsItem> receiptsItemList = getListByHeaderId(headerId);
            for(ReceiptsItem receiptsItem : receiptsItemList){
                Material material= materialService.getMaterial(receiptsItem.getMaterialId());
                if(material==null){
                    continue;
                }
                if(BusinessConstants.ENABLE_SERIAL_NUMBER_ENABLED.equals(material.getEnableSerialNumber())){
                    serialNumberService.cancelSerialNumber(receiptsItem.getMaterialId(),receipts.getNumber(),
                            (receiptsItem.getBasicNumber()==null?0:receiptsItem.getBasicNumber()).intValue(), userInfo);
                }
            }
        }
        //删除单据的明细
        deleteReceiptsItemHeadId(headerId);
        //单据状态:是否全部完成 2-全部完成 3-部分完成（针对订单的分批出入库）
        String billStatus = BusinessConstants.BILLS_STATUS_SKIPED;
        JSONArray rowArr = JSONArray.parseArray(rows);
        if (null != rowArr && rowArr.size()>0) {
            for (int i = 0; i < rowArr.size(); i++) {
                ReceiptsItem receiptsItem = new ReceiptsItem();
                JSONObject rowObj = JSONObject.parseObject(rowArr.getString(i));
                receiptsItem.setHeaderId(headerId);
                String barCode = rowObj.getString("barCode");
                MaterialExtend materialExtend = materialExtendService.getInfoByBarCode(barCode);
                receiptsItem.setMaterialId(materialExtend.getMaterialId());
                receiptsItem.setMaterialExtendId(materialExtend.getId());
                receiptsItem.setMaterialUnit(rowObj.getString("unit"));
                if (StringUtil.isExist(rowObj.get("snList"))) {
                    receiptsItem.setSnList(rowObj.getString("snList"));
                    if(StringUtil.isExist(rowObj.get("depotId"))) {
                        Long depotId = rowObj.getLong("depotId");
                        if(BusinessConstants.SUB_TYPE_PURCHASE.equals(receipts.getSubType())||
                                BusinessConstants.SUB_TYPE_SALES_RETURN.equals(receipts.getSubType())) {
                            serialNumberService.addSerialNumberByBill(receipts.getNumber(), materialExtend.getMaterialId(), depotId, receiptsItem.getSnList());
                        }
                    }
                }
                if (StringUtil.isExist(rowObj.get("batchNumber"))) {
                    receiptsItem.setBatchNumber(rowObj.getString("batchNumber"));
                }
                if (StringUtil.isExist(rowObj.get("expirationDate"))) {
                    receiptsItem.setExpirationDate(rowObj.getDate("expirationDate"));
                }
                if (StringUtil.isExist(rowObj.get("sku"))) {
                    receiptsItem.setSku(rowObj.getString("sku"));
                }
                if (StringUtil.isExist(rowObj.get("operNumber"))) {
                    receiptsItem.setOperNumber(rowObj.getBigDecimal("operNumber"));
                    String unit = rowObj.get("unit").toString();
                    BigDecimal oNumber = rowObj.getBigDecimal("operNumber");
                    //以下进行单位换算
                    Unit unitInfo = materialService.findUnit(materialExtend.getMaterialId()); //查询计量单位信息
                    if (StringUtil.isNotEmpty(unitInfo.getName())) {
                        String basicUnit = unitInfo.getBasicUnit(); //基本单位
                        if (unit.equals(basicUnit)) { //如果等于基本单位
                            receiptsItem.setBasicNumber(oNumber); //数量一致
                        } else if (unit.equals(unitInfo.getOtherUnit())) { //如果等于副单位
                            receiptsItem.setBasicNumber(oNumber.multiply(new BigDecimal(unitInfo.getRatio())) ); //数量乘以比例
                        } else if (unit.equals(unitInfo.getOtherUnitTwo())) { //如果等于副单位2
                            receiptsItem.setBasicNumber(oNumber.multiply(new BigDecimal(unitInfo.getRatioTwo())) ); //数量乘以比例
                        } else if (unit.equals(unitInfo.getOtherUnitThree())) { //如果等于副单位3
                            receiptsItem.setBasicNumber(oNumber.multiply(new BigDecimal(unitInfo.getRatioThree())) ); //数量乘以比例
                        }
                    } else {
                        receiptsItem.setBasicNumber(oNumber); //其他情况
                    }
                }
                //如果数量+已完成数量<原订单数量，代表该单据状态为未全部完成出入库(判断前提是存在关联订单)
                if (StringUtil.isNotEmpty(receipts.getLinkNumber())
                        && StringUtil.isExist(rowObj.get("preNumber")) && StringUtil.isExist(rowObj.get("finishNumber"))) {
                    BigDecimal preNumber = rowObj.getBigDecimal("preNumber");
                    BigDecimal finishNumber = rowObj.getBigDecimal("finishNumber");
                    if(receiptsItem.getOperNumber().add(finishNumber).compareTo(preNumber)<0) {
                        billStatus = BusinessConstants.BILLS_STATUS_SKIPING;
                    } else if(receiptsItem.getOperNumber().add(finishNumber).compareTo(preNumber)>0) {
                        throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_NUMBER_NEED_EDIT_FAILED_CODE,
                                String.format(ExceptionConstants.DEPOT_HEAD_NUMBER_NEED_EDIT_FAILED_MSG, barCode));
                    }
                }
                if (StringUtil.isExist(rowObj.get("unitPrice"))) {
                    receiptsItem.setUnitPrice(rowObj.getBigDecimal("unitPrice"));
                }
                if (StringUtil.isExist(rowObj.get("taxUnitPrice"))) {
                    receiptsItem.setTaxUnitPrice(rowObj.getBigDecimal("taxUnitPrice"));
                }
                if (StringUtil.isExist(rowObj.get("allPrice"))) {
                    receiptsItem.setAllPrice(rowObj.getBigDecimal("allPrice"));
                }
                if (StringUtil.isExist(rowObj.get("depotId"))) {
                    receiptsItem.setDepotId(rowObj.getLong("depotId"));
                } else {
                    if(!BusinessConstants.SUB_TYPE_PURCHASE_ORDER.equals(receipts.getSubType())
                            && !BusinessConstants.SUB_TYPE_SALES_ORDER.equals(receipts.getSubType())) {
                        throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_DEPOT_FAILED_CODE,
                                String.format(ExceptionConstants.DEPOT_HEAD_DEPOT_FAILED_MSG));
                    }
                }
                if(BusinessConstants.SUB_TYPE_TRANSFER.equals(receipts.getSubType())) {
                    if (StringUtil.isExist(rowObj.get("anotherDepotId"))) {
                        if(rowObj.getLong("anotherDepotId").equals(rowObj.getLong("depotId"))) {
                            throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_ANOTHER_DEPOT_EQUAL_FAILED_CODE,
                                    String.format(ExceptionConstants.DEPOT_HEAD_ANOTHER_DEPOT_EQUAL_FAILED_MSG));
                        } else {
                            receiptsItem.setAnotherDepotId(rowObj.getLong("anotherDepotId"));
                        }
                    } else {
                        throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_ANOTHER_DEPOT_FAILED_CODE,
                                String.format(ExceptionConstants.DEPOT_HEAD_ANOTHER_DEPOT_FAILED_MSG));
                    }
                }
                if (StringUtil.isExist(rowObj.get("taxRate"))) {
                    receiptsItem.setTaxRate(rowObj.getBigDecimal("taxRate"));
                }
                if (StringUtil.isExist(rowObj.get("taxMoney"))) {
                    receiptsItem.setTaxMoney(rowObj.getBigDecimal("taxMoney"));
                }
                if (StringUtil.isExist(rowObj.get("taxLastMoney"))) {
                    receiptsItem.setTaxLastMoney(rowObj.getBigDecimal("taxLastMoney"));
                }
                if (StringUtil.isExist(rowObj.get("mType"))) {
                    receiptsItem.setMaterialType(rowObj.getString("mType"));
                }
                if (StringUtil.isExist(rowObj.get("remark"))) {
                    receiptsItem.setRemark(rowObj.getString("remark"));
                }
                //出库时判断库存是否充足
                if(BusinessConstants.DEPOTHEAD_TYPE_OUT.equals(receipts.getType())){
                    if(receiptsItem==null){
                        continue;
                    }
                    Material material= materialService.getMaterial(receiptsItem.getMaterialId());
                    if(material==null){
                        continue;
                    }
                    BigDecimal stock = getStockByParam(receiptsItem.getDepotId(),receiptsItem.getMaterialId(),null,null);
                    BigDecimal thisBasicNumber = receiptsItem.getBasicNumber()==null?BigDecimal.ZERO:receiptsItem.getBasicNumber();
                    if(systemConfigService.getMinusStockFlag() == false && stock.compareTo(thisBasicNumber)<0){
                        throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_STOCK_NOT_ENOUGH_CODE,
                                String.format(ExceptionConstants.MATERIAL_STOCK_NOT_ENOUGH_MSG,material==null?"":material.getName()));
                    }
                    //出库时处理序列号
                    if(!BusinessConstants.SUB_TYPE_TRANSFER.equals(receipts.getSubType())) {
                        //判断商品是否开启序列号，开启的收回序列号，未开启的跳过
                        if(BusinessConstants.ENABLE_SERIAL_NUMBER_ENABLED.equals(material.getEnableSerialNumber())) {
                            //查询单据子表中开启序列号的数据列表
                            serialNumberService.checkAndUpdateSerialNumber(receiptsItem, receipts.getNumber(), userInfo, StringUtil.toNull(receiptsItem.getSnList()));
                        }
                    }
                }
                this.insertReceiptsItemWithObj(receiptsItem);
                //更新当前库存
                updateCurrentStock(receiptsItem);
            }
            //如果关联单据号非空则更新订单的状态,单据类型：采购入库单或销售出库单
            if(BusinessConstants.SUB_TYPE_PURCHASE.equals(receipts.getSubType())
                    || BusinessConstants.SUB_TYPE_SALES.equals(receipts.getSubType())) {
                if(StringUtil.isNotEmpty(receipts.getLinkNumber())) {
                    changeBillStatus(receipts, billStatus);
                }
            }
        } else {
            throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_ROW_FAILED_CODE,
                    String.format(ExceptionConstants.DEPOT_HEAD_ROW_FAILED_MSG));
        }
    }

    /**
     * 更新单据状态
     * @param receipts
     * @param billStatus
     */
    public void changeBillStatus(Receipts receipts, String billStatus) {
        Receipts receiptsOrders = new Receipts();
        receiptsOrders.setStatus(billStatus);
        ReceiptsExample example = new ReceiptsExample();
        List<String> linkNumberList = StringUtil.strToStringList(receipts.getLinkNumber());
        example.createCriteria().andNumberIn(linkNumberList);
        try{
            receiptsMapper.updateByExampleSelective(receiptsOrders, example);
        }catch(Exception e){
            logger.error("异常码[{}],异常提示[{}],异常[{}]",
                    ExceptionConstants.DATA_WRITE_FAIL_CODE,ExceptionConstants.DATA_WRITE_FAIL_MSG,e);
            throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE,
                    ExceptionConstants.DATA_WRITE_FAIL_MSG);
        }
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void deleteReceiptsItemHeadId(Long headerId)throws Exception {
        try{
            //1、查询删除前的单据明细
            List<ReceiptsItem> receiptsItemList = getListByHeaderId(headerId);
            //2、删除单据明细
            ReceiptsItemExample example = new ReceiptsItemExample();
            example.createCriteria().andHeaderIdEqualTo(headerId);
            receiptsItemMapper.deleteByExample(example);
            //3、计算删除之后单据明细中商品的库存
            for(ReceiptsItem receiptsItem : receiptsItemList){
                updateCurrentStock(receiptsItem);
            }
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public List<ReceiptsItemStockWarningCount> findStockWarningCount(Integer offset, Integer rows, String materialParam, List<Long> depotList) {
        List<ReceiptsItemStockWarningCount> list = null;
        try{
            list =receiptsItemMapperEx.findStockWarningCount(offset, rows, materialParam, depotList);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int findStockWarningCountTotal(String materialParam, List<Long> depotList) {
        int result = 0;
        try{
            result =receiptsItemMapperEx.findStockWarningCountTotal(materialParam, depotList);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 库存统计-sku
     * @param depotId
     * @param meId
     * @param beginTime
     * @param endTime
     * @return
     */
    public BigDecimal getSkuStockByParam(Long depotId, Long meId, String beginTime, String endTime){
        ReceiptsItemVo4Stock stockObj = receiptsItemMapperEx.getSkuStockByParam(depotId, meId, beginTime, endTime);
        BigDecimal stockSum = BigDecimal.ZERO;
        if(stockObj!=null) {
            BigDecimal inTotal = stockObj.getInTotal();
            BigDecimal transfInTotal = stockObj.getTransfInTotal();
            BigDecimal assemInTotal = stockObj.getAssemInTotal();
            BigDecimal disAssemInTotal = stockObj.getDisAssemInTotal();
            BigDecimal outTotal = stockObj.getOutTotal();
            BigDecimal transfOutTotal = stockObj.getTransfOutTotal();
            BigDecimal assemOutTotal = stockObj.getAssemOutTotal();
            BigDecimal disAssemOutTotal = stockObj.getDisAssemOutTotal();
            stockSum = inTotal.add(transfInTotal).add(assemInTotal).add(disAssemInTotal)
                    .subtract(outTotal).subtract(transfOutTotal).subtract(assemOutTotal).subtract(disAssemOutTotal);
        }
        return stockSum;
    }

    /**
     * 库存统计-单仓库
     * @param depotId
     * @param mId
     * @param beginTime
     * @param endTime
     * @return
     */
    public BigDecimal getStockByParam(Long depotId, Long mId, String beginTime, String endTime){
        List<Long> depotList = new ArrayList<>();
        if(depotId != null) {
            depotList.add(depotId);
        }
        return getStockByParamWithDepotList(depotList, mId, beginTime, endTime);
    }

    /**
     * 库存统计-多仓库
     * @param depotList
     * @param mId
     * @param beginTime
     * @param endTime
     * @return
     */
    public BigDecimal getStockByParamWithDepotList(List<Long> depotList, Long mId, String beginTime, String endTime){
        //初始库存
        BigDecimal initStock = materialService.getInitStockByMidAndDepotList(depotList, mId);
        //盘点复盘后数量的变动
        BigDecimal stockCheckSum = receiptsItemMapperEx.getStockCheckSumByDepotList(depotList, mId, beginTime, endTime);
        ReceiptsItemVo4Stock stockObj = receiptsItemMapperEx.getStockByParamWithDepotList(depotList, mId, beginTime, endTime);
        BigDecimal stockSum = BigDecimal.ZERO;
        if(stockObj!=null) {
            BigDecimal inTotal = stockObj.getInTotal();
            BigDecimal transfInTotal = stockObj.getTransfInTotal();
            BigDecimal assemInTotal = stockObj.getAssemInTotal();
            BigDecimal disAssemInTotal = stockObj.getDisAssemInTotal();
            BigDecimal outTotal = stockObj.getOutTotal();
            BigDecimal transfOutTotal = stockObj.getTransfOutTotal();
            BigDecimal assemOutTotal = stockObj.getAssemOutTotal();
            BigDecimal disAssemOutTotal = stockObj.getDisAssemOutTotal();
            stockSum = inTotal.add(transfInTotal).add(assemInTotal).add(disAssemInTotal)
                    .subtract(outTotal).subtract(transfOutTotal).subtract(assemOutTotal).subtract(disAssemOutTotal);
        }
        return initStock.add(stockCheckSum).add(stockSum);
    }

    /**
     * 统计时间段内的入库和出库数量-多仓库
     * @param depotList
     * @param mId
     * @param beginTime
     * @param endTime
     * @return
     */
    public Map<String, BigDecimal> getIntervalMapByParamWithDepotList(List<Long> depotList, Long mId, String beginTime, String endTime){
        Map<String,BigDecimal> intervalMap = new HashMap<>();
        BigDecimal inSum = BigDecimal.ZERO;
        BigDecimal outSum = BigDecimal.ZERO;
        //盘点复盘后数量的变动
        BigDecimal stockCheckSum = receiptsItemMapperEx.getStockCheckSumByDepotList(depotList, mId, beginTime, endTime);
        ReceiptsItemVo4Stock stockObj = receiptsItemMapperEx.getStockByParamWithDepotList(depotList, mId, beginTime, endTime);
        if(stockObj!=null) {
            BigDecimal inTotal = stockObj.getInTotal();
            BigDecimal transfInTotal = stockObj.getTransfInTotal();
            BigDecimal assemInTotal = stockObj.getAssemInTotal();
            BigDecimal disAssemInTotal = stockObj.getDisAssemInTotal();
            inSum = inTotal.add(transfInTotal).add(assemInTotal).add(disAssemInTotal);
            BigDecimal outTotal = stockObj.getOutTotal();
            BigDecimal transfOutTotal = stockObj.getTransfOutTotal();
            BigDecimal assemOutTotal = stockObj.getAssemOutTotal();
            BigDecimal disAssemOutTotal = stockObj.getDisAssemOutTotal();
            outSum = outTotal.add(transfOutTotal).add(assemOutTotal).add(disAssemOutTotal);
        }
        if(stockCheckSum.compareTo(BigDecimal.ZERO)>0) {
            inSum = inSum.add(stockCheckSum);
        } else {
            //盘点复盘数量为负数代表出库
            outSum = outSum.subtract(stockCheckSum);
        }
        intervalMap.put("inSum", inSum);
        intervalMap.put("outSum", outSum);
        return intervalMap;
    }

    /**
     * 根据单据明细来批量更新当前库存
     * @param receiptsItem
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void updateCurrentStock(ReceiptsItem receiptsItem){
        updateCurrentStockFun(receiptsItem.getMaterialId(), receiptsItem.getDepotId());
        if(receiptsItem.getAnotherDepotId()!=null){
            updateCurrentStockFun(receiptsItem.getMaterialId(), receiptsItem.getAnotherDepotId());
        }
    }

    /**
     * 根据商品和仓库来更新当前库存
     * @param mId
     * @param dId
     */
    public void updateCurrentStockFun(Long mId, Long dId) {
        if(mId!=null && dId!=null) {
            MaterialCurrentStockExample example = new MaterialCurrentStockExample();
            example.createCriteria().andMaterialIdEqualTo(mId).andDepotIdEqualTo(dId)
                    .andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
            List<MaterialCurrentStock> list = materialCurrentStockMapper.selectByExample(example);
            MaterialCurrentStock materialCurrentStock = new MaterialCurrentStock();
            materialCurrentStock.setMaterialId(mId);
            materialCurrentStock.setDepotId(dId);
            materialCurrentStock.setCurrentNumber(getStockByParam(dId,mId,null,null));
            if(list!=null && list.size()>0) {
                Long mcsId = list.get(0).getId();
                materialCurrentStock.setId(mcsId);
                materialCurrentStockMapper.updateByPrimaryKeySelective(materialCurrentStock);
            } else {
                materialCurrentStockMapper.insertSelective(materialCurrentStock);
            }
        }
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public BigDecimal getFinishNumber(Long mId, Long headerId) {
        String goToType = "";
        Receipts receipts =receiptsMapper.selectByPrimaryKey(headerId);
        String linkNumber = receipts.getNumber(); //订单号
        if(BusinessConstants.SUB_TYPE_PURCHASE_ORDER.equals(receipts.getSubType())) {
            goToType = BusinessConstants.SUB_TYPE_PURCHASE;
        }
        if(BusinessConstants.SUB_TYPE_SALES_ORDER.equals(receipts.getSubType())) {
            goToType = BusinessConstants.SUB_TYPE_SALES;
        }
        BigDecimal count = receiptsItemMapperEx.getFinishNumber(mId, linkNumber, goToType);
        return count;
    }

    public List<ReceiptsItemVoBatchNumberList> getBatchNumberList(String name, Long depotId, String barCode, String batchNumber){
        return receiptsItemMapperEx.getBatchNumberList(name, depotId, barCode, batchNumber);
    }
}
