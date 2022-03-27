package com.ywy.erp.service.receipts;

import com.alibaba.fastjson.JSONObject;
import com.ywy.erp.constants.BusinessConstants;
import com.ywy.erp.constants.ExceptionConstants;
import com.ywy.erp.mappers.ReceiptsMapper;
import com.ywy.erp.mappers.ReceiptsMapperEx;
import com.ywy.erp.mappers.ReceiptsItemMapperEx;
import com.ywy.erp.vo.ReceiptsVo4InDetail;
import com.ywy.erp.vo.ReceiptsVo4InOutMCount;
import com.ywy.erp.vo.ReceiptsVo4StatementAccount;
import com.ywy.erp.vo.ReceiptsVo4List;
import com.ywy.erp.exception.BusinessRunTimeException;
import com.ywy.erp.exception.JshException;
import com.ywy.erp.service.account.AccountService;
import com.ywy.erp.service.accountItem.AccountItemService;
import com.ywy.erp.service.depot.DepotService;
import com.ywy.erp.service.receiptsItem.ReceiptsItemService;
import com.ywy.erp.service.log.LogService;
import com.ywy.erp.service.orgaUserRel.OrgaUserRelService;
import com.ywy.erp.service.person.PersonService;
import com.ywy.erp.service.redis.RedisService;
import com.ywy.erp.service.serialNumber.SerialNumberService;
import com.ywy.erp.service.supplier.SupplierService;
import com.ywy.erp.service.user.UserService;
import com.ywy.erp.utils.StringUtil;
import com.ywy.erp.utils.Tools;
import com.ywy.erp.entities.Receipts;
import com.ywy.erp.entities.ReceiptsExample;
import com.ywy.erp.entities.ReceiptsItem;
import com.ywy.erp.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.ywy.erp.utils.Tools.getCenternTime;

@Service
public class ReceiptsService {
    private Logger logger = LoggerFactory.getLogger(ReceiptsService.class);

    @Resource
    private ReceiptsMapper receiptsMapper;
    @Resource
    private ReceiptsMapperEx receiptsMapperEx;
    @Resource
    private UserService userService;
    @Resource
    private DepotService depotService;
    @Resource
    ReceiptsItemService receiptsItemService;
    @Resource
    private SupplierService supplierService;
    @Resource
    private SerialNumberService serialNumberService;
    @Resource
    private OrgaUserRelService orgaUserRelService;
    @Resource
    private PersonService personService;
    @Resource
    private AccountService accountService;
    @Resource
    private AccountItemService accountItemService;
    @Resource
    ReceiptsItemMapperEx receiptsItemMapperEx;

    @Resource
    private LogService logService;
    @Resource
    private RedisService redisService;

    public Receipts getReceipts(long id)throws Exception {
        Receipts result=null;
        try{
            result=receiptsMapper.selectByPrimaryKey(id);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<Receipts> getReceipts()throws Exception {
        ReceiptsExample example = new ReceiptsExample();
        example.createCriteria().andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<Receipts> list=null;
        try{
            list=receiptsMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<ReceiptsVo4List> select(String type, String subType, String roleType, String status, String number, String linkNumber,
                                         String beginTime, String endTime, String materialParam, Long organId, Long creator, Long depotId, int offset, int rows) throws Exception {
        List<ReceiptsVo4List> resList = new ArrayList<>();
        List<ReceiptsVo4List> list=new ArrayList<>();
        try{
            String [] depotArray = getDepotArray(subType);
            String [] creatorArray = getCreatorArray(roleType);
            String [] statusArray = StringUtil.isNotEmpty(status) ? status.split(",") : null;
            Map<Long,String> personMap = personService.getPersonMap();
            Map<Long,String> accountMap = accountService.getAccountMap();
            beginTime = Tools.parseDayToTime(beginTime,BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime,BusinessConstants.DAY_LAST_TIME);
            list=receiptsMapperEx.selectByConditionReceipts(type, subType, creatorArray, statusArray, number, linkNumber, beginTime, endTime,
                 materialParam, organId, creator, depotId, depotArray, offset, rows);
            if (null != list) {
                for (ReceiptsVo4List dh : list) {
                    if(accountMap!=null && StringUtil.isNotEmpty(dh.getAccountIdList()) && StringUtil.isNotEmpty(dh.getAccountMoneyList())) {
                        String accountStr = accountService.getAccountStrByIdAndMoney(accountMap, dh.getAccountIdList(), dh.getAccountMoneyList());
                        dh.setAccountName(accountStr);
                    }
                    if(dh.getAccountIdList() != null) {
                        String accountidlistStr = dh.getAccountIdList().replace("[", "").replace("]", "").replaceAll("\"", "");
                        dh.setAccountIdList(accountidlistStr);
                    }
                    if(dh.getAccountMoneyList() != null) {
                        String accountmoneylistStr = dh.getAccountMoneyList().replace("[", "").replace("]", "").replaceAll("\"", "");
                        dh.setAccountMoneyList(accountmoneylistStr);
                    }
                    if(dh.getChangeAmount() != null) {
                        dh.setChangeAmount(dh.getChangeAmount().abs());
                    }
                    if(dh.getTotalPrice() != null) {
                        dh.setTotalPrice(dh.getTotalPrice().abs());
                    }
                    if(StringUtil.isNotEmpty(dh.getSalesMan())) {
                        dh.setSalesManStr(personService.getPersonByMapAndIds(personMap,dh.getSalesMan()));
                    }
                    if(dh.getOperTime() != null) {
                        dh.setOperTimeStr(getCenternTime(dh.getOperTime()));
                    }
                    dh.setMaterialsList(findMaterialsListByHeaderId(dh.getId()));
                    resList.add(dh);
                }
            }
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return resList;
    }

    public Long countReceipts(String type, String subType, String roleType, String status, String number, String linkNumber,
           String beginTime, String endTime, String materialParam, Long organId, Long creator, Long depotId) throws Exception{
        Long result=null;
        try{
            String [] depotArray = getDepotArray(subType);
            String [] creatorArray = getCreatorArray(roleType);
            String [] statusArray = StringUtil.isNotEmpty(status) ? status.split(",") : null;
            beginTime = Tools.parseDayToTime(beginTime,BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime,BusinessConstants.DAY_LAST_TIME);
            result=receiptsMapperEx.countsByReceipts(type, subType, creatorArray, statusArray, number, linkNumber, beginTime, endTime,
                   materialParam, organId, creator, depotId, depotArray);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 根据单据类型获取仓库数组
     * @param subType
     * @return
     * @throws Exception
     */
    public String[] getDepotArray(String subType) throws Exception {
        String [] depotArray = null;
        if(!BusinessConstants.SUB_TYPE_PURCHASE_ORDER.equals(subType) && !BusinessConstants.SUB_TYPE_SALES_ORDER.equals(subType)) {
            String depotIds = depotService.findDepotStrByCurrentUser();
            depotArray = StringUtil.isNotEmpty(depotIds) ? depotIds.split(",") : null;
        }
        return depotArray;
    }

    /**
     * 根据角色类型获取操作员数组
     * @param roleType
     * @return
     * @throws Exception
     */
    public String[] getCreatorArray(String roleType) throws Exception {
        String creator = getCreatorByRoleType(roleType);
        String [] creatorArray=null;
        if(StringUtil.isNotEmpty(creator)){
            creatorArray = creator.split(",");
        }
        return creatorArray;
    }

    /**
     * 根据角色类型获取操作员
     * @param roleType
     * @return
     * @throws Exception
     */
    public String getCreatorByRoleType(String roleType) throws Exception {
        String creator = "";
        User user = userService.getCurrentUser();
        if(BusinessConstants.ROLE_TYPE_PRIVATE.equals(roleType)) {
            creator = user.getId().toString();
        } else if(BusinessConstants.ROLE_TYPE_THIS_ORG.equals(roleType)) {
            creator = orgaUserRelService.getUserIdListByUserId(user.getId());
        }
        return creator;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertReceipts(JSONObject obj, HttpServletRequest request)throws Exception {
        Receipts receipts = JSONObject.parseObject(obj.toJSONString(), Receipts.class);
        receipts.setCreateTime(new Timestamp(System.currentTimeMillis()));
        receipts.setStatus(BusinessConstants.BILLS_STATUS_UN_AUDIT);
        int result=0;
        try{
            result=receiptsMapper.insert(receipts);
            logService.insertLog("单据", BusinessConstants.LOG_OPERATION_TYPE_ADD, request);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateReceipts(JSONObject obj, HttpServletRequest request) throws Exception{
        Receipts receipts = JSONObject.parseObject(obj.toJSONString(), Receipts.class);
        Receipts dh=null;
        try{
            dh = receiptsMapper.selectByPrimaryKey(receipts.getId());
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        receipts.setStatus(dh.getStatus());
        receipts.setCreateTime(dh.getCreateTime());
        int result=0;
        try{
            result = receiptsMapper.updateByPrimaryKey(receipts);
            logService.insertLog("单据",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(receipts.getId()).toString(), request);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteReceipts(Long id, HttpServletRequest request)throws Exception {
        return batchDeleteBillByIds(id.toString());
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteReceipts(String ids, HttpServletRequest request)throws Exception {
        return batchDeleteBillByIds(ids);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteBillByIds(String ids)throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append(BusinessConstants.LOG_OPERATION_TYPE_DELETE);
        List<Receipts> dhList = getReceiptsListByIds(ids);
        for(Receipts receipts: dhList){
            sb.append("[").append(receipts.getNumber()).append("]");
            //只有未审核的单据才能被删除
            if("0".equals(receipts.getStatus())) {
                User userInfo = userService.getCurrentUser();
                //删除出库数据回收序列号
                if (BusinessConstants.DEPOTHEAD_TYPE_OUT.equals(receipts.getType())
                        && !BusinessConstants.SUB_TYPE_TRANSFER.equals(receipts.getSubType())) {
                    //查询单据子表列表
                    List<ReceiptsItem> receiptsItemList = null;
                    try {
                        receiptsItemList = receiptsItemMapperEx.findReceiptsItemListBydepotheadId(receipts.getId(), BusinessConstants.ENABLE_SERIAL_NUMBER_ENABLED);
                    } catch (Exception e) {
                        JshException.readFail(logger, e);
                    }

                    /**回收序列号*/
                    if (receiptsItemList != null && receiptsItemList.size() > 0) {
                        for (ReceiptsItem receiptsItem : receiptsItemList) {
                            //BasicNumber=OperNumber*ratio
                            serialNumberService.cancelSerialNumber(receiptsItem.getMaterialId(), receipts.getNumber(), (receiptsItem.getBasicNumber() == null ? 0 : receiptsItem.getBasicNumber()).intValue(), userInfo);
                        }
                    }
                }
                //对于零售出库单据，更新会员的预收款信息
                if (BusinessConstants.DEPOTHEAD_TYPE_OUT.equals(receipts.getType())
                        && BusinessConstants.SUB_TYPE_RETAIL.equals(receipts.getSubType())){
                    if(BusinessConstants.PAY_TYPE_PREPAID.equals(receipts.getPayType())) {
                        if (receipts.getOrganId() != null) {
                            supplierService.updateAdvanceIn(receipts.getOrganId(), receipts.getTotalPrice().abs());
                        }
                    }
                }
                //删除单据子表数据
                receiptsItemMapperEx.batchDeleteReceiptsItemByReceiptsIds(new Long[]{receipts.getId()});
                //删除单据主表信息
                batchDeleteReceiptsByIds(receipts.getId().toString());
                //将关联的单据置为审核状态-针对采购入库、销售出库和盘点复盘
                if(StringUtil.isNotEmpty(receipts.getLinkNumber())){
                    if((BusinessConstants.DEPOTHEAD_TYPE_IN.equals(receipts.getType()) &&
                        BusinessConstants.SUB_TYPE_PURCHASE.equals(receipts.getSubType()))
                    || (BusinessConstants.DEPOTHEAD_TYPE_OUT.equals(receipts.getType()) &&
                        BusinessConstants.SUB_TYPE_SALES.equals(receipts.getSubType()))
                    || (BusinessConstants.DEPOTHEAD_TYPE_OTHER.equals(receipts.getType()) &&
                        BusinessConstants.SUB_TYPE_REPLAY.equals(receipts.getSubType()))) {
                        Receipts dh = new Receipts();
                        dh.setStatus(BusinessConstants.BILLS_STATUS_AUDIT);
                        ReceiptsExample example = new ReceiptsExample();
                        example.createCriteria().andNumberEqualTo(receipts.getLinkNumber());
                        receiptsMapper.updateByExampleSelective(dh, example);
                    }
                }
                //更新当前库存
                List<ReceiptsItem> list = receiptsItemService.getListByHeaderId(receipts.getId());
                for (ReceiptsItem receiptsItem : list) {
                    receiptsItemService.updateCurrentStock(receiptsItem);
                }
            } else {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_UN_AUDIT_DELETE_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_UN_AUDIT_DELETE_FAILED_MSG));
            }
        }
        logService.insertLog("单据", sb.toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
        return 1;
    }

    /**
     * 删除单据主表信息
     * @param ids
     * @return
     * @throws Exception
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteReceiptsByIds(String ids)throws Exception {
        User userInfo=userService.getCurrentUser();
        String [] idArray=ids.split(",");
        int result=0;
        try{
            result = receiptsMapperEx.batchDeleteReceiptsByIds(new Date(),userInfo==null?null:userInfo.getId(),idArray);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    public List<Receipts> getReceiptsListByIds(String ids)throws Exception {
        List<Long> idList = StringUtil.strToLongList(ids);
        List<Receipts> list = new ArrayList<>();
        try{
            ReceiptsExample example = new ReceiptsExample();
            example.createCriteria().andIdIn(idList);
            list = receiptsMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public int checkIsNameExist(Long id, String name)throws Exception {
        ReceiptsExample example = new ReceiptsExample();
        example.createCriteria().andIdNotEqualTo(id).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<Receipts> list = null;
        try{
            list = receiptsMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list==null?0:list.size();
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchSetStatus(String status, String receiptsIDs)throws Exception {
        int result = 0;
        List<Long> dhIds = new ArrayList<>();
        List<Long> ids = StringUtil.strToLongList(receiptsIDs);
        for(Long id: ids) {
            Receipts receipts = getReceipts(id);
            if("0".equals(status)){
                if("1".equals(receipts.getStatus())) {
                    dhIds.add(id);
                } else {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_AUDIT_TO_UN_AUDIT_FAILED_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_AUDIT_TO_UN_AUDIT_FAILED_MSG));
                }
            } else if("1".equals(status)){
                if("0".equals(receipts.getStatus())) {
                    dhIds.add(id);
                } else {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_UN_AUDIT_TO_AUDIT_FAILED_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_UN_AUDIT_TO_AUDIT_FAILED_MSG));
                }
            }
        }
        if(dhIds.size()>0) {
            Receipts receipts = new Receipts();
            receipts.setStatus(status);
            ReceiptsExample example = new ReceiptsExample();
            example.createCriteria().andIdIn(dhIds);
            result = receiptsMapper.updateByExampleSelective(receipts, example);
        }
        return result;
    }

    public String findMaterialsListByHeaderId(Long id)throws Exception {
        String result = null;
        try{
            result = receiptsMapperEx.findMaterialsListByHeaderId(id);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<ReceiptsVo4InDetail> findByAll(String beginTime, String endTime, String type, String materialParam,
                                                List<Long> depotList, Integer oId, String number, Integer offset, Integer rows) throws Exception{
        List<ReceiptsVo4InDetail> list = null;
        try{
            list =receiptsMapperEx.findByAll(beginTime, endTime, type, materialParam, depotList, oId, number, offset, rows);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public int findByAllCount(String beginTime, String endTime, String type, String materialParam, List<Long> depotList, Integer oId, String number) throws Exception{
        int result = 0;
        try{
            result =receiptsMapperEx.findByAllCount(beginTime, endTime, type, materialParam, depotList, oId, number);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<ReceiptsVo4InOutMCount> findInOutMaterialCount(String beginTime, String endTime, String type, String materialParam, List<Long> depotList, Integer oId, Integer offset, Integer rows)throws Exception {
        List<ReceiptsVo4InOutMCount> list = null;
        try{
            list =receiptsMapperEx.findInOutMaterialCount(beginTime, endTime, type, materialParam, depotList, oId, offset, rows);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public int findInOutMaterialCountTotal(String beginTime, String endTime, String type, String materialParam, List<Long> depotList, Integer oId)throws Exception {
        int result = 0;
        try{
            result =receiptsMapperEx.findInOutMaterialCountTotal(beginTime, endTime, type, materialParam, depotList, oId);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<ReceiptsVo4InDetail> findAllocationDetail(String beginTime, String endTime, String subType, String number,
           String materialParam, List<Long> depotList, List<Long> depotFList, Integer offset, Integer rows) throws Exception{
        List<ReceiptsVo4InDetail> list = null;
        try{
            list =receiptsMapperEx.findAllocationDetail(beginTime, endTime, subType, number, materialParam, depotList, depotFList, offset, rows);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public int findAllocationDetailCount(String beginTime, String endTime, String subType, String number,
               String materialParam, List<Long> depotList,  List<Long> depotFList) throws Exception{
        int result = 0;
        try{
            result =receiptsMapperEx.findAllocationDetailCount(beginTime, endTime, subType, number, materialParam, depotList, depotFList);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<ReceiptsVo4StatementAccount> findStatementAccount(String beginTime, String endTime, Integer organId, String supType, Integer offset, Integer rows)throws Exception {
        List<ReceiptsVo4StatementAccount> list = null;
        try{
            int j = 1;
            if (supType.equals("客户")) { //客户
                j = 1;
            } else if (supType.equals("供应商")) { //供应商
                j = -1;
            }
            list =receiptsMapperEx.findStatementAccount(beginTime, endTime, organId, supType, offset, rows);
            if (null != list) {
                for (ReceiptsVo4StatementAccount dha : list) {
                    dha.setNumber(dha.getNumber()); //单据编号
                    dha.setType(dha.getType()); //类型
                    String type = dha.getType();
                    BigDecimal p1 = BigDecimal.ZERO ;
                    BigDecimal p2 = BigDecimal.ZERO;
                    if (dha.getDiscountLastMoney() != null) {
                        p1 = dha.getDiscountLastMoney();
                    }
                    if (dha.getChangeAmount() != null) {
                        p2 = dha.getChangeAmount();
                    }
                    BigDecimal allPrice = BigDecimal.ZERO;
                    if ((p1.compareTo(BigDecimal.ZERO))==-1) {
                        p1 = p1.abs();
                    }
                    if(dha.getOtherMoney()!=null) {
                        p1 = p1.add(dha.getOtherMoney()); //与其它费用相加
                    }
                    if ((p2 .compareTo(BigDecimal.ZERO))==-1) {
                        p2 = p2.abs();
                    }
                    if (type.equals("采购入库")) {
                        allPrice = p2.subtract(p1);
                    } else if (type.equals("销售退货入库")) {
                        allPrice = p2.subtract(p1);
                    } else if (type.equals("销售出库")) {
                        allPrice = p1.subtract(p2);
                    } else if (type.equals("采购退货出库")) {
                        allPrice = p1.subtract(p2);
                    } else if (type.equals("收款")) {
                        allPrice = BigDecimal.ZERO.subtract(p1);
                    } else if (type.equals("付款")) {
                        allPrice = p1;
                    } else if (type.equals("收入")) {
                        allPrice =  p1.subtract(p2);
                    } else if (type.equals("支出")) {
                        allPrice = p2.subtract(p1);
                    }
                    dha.setBillMoney(p1); //单据金额
                    dha.setChangeAmount(p2); //实际支付
                    DecimalFormat df = new DecimalFormat(".##");
                    dha.setAllPrice(new BigDecimal(df.format(allPrice.multiply(new BigDecimal(j))))); //本期变化
                    dha.setSupplierName(dha.getSupplierName()); //单位名称
                    dha.setoTime(dha.getoTime()); //单据日期
                }
            }
        } catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public int findStatementAccountCount(String beginTime, String endTime, Integer organId, String supType) throws Exception{
        int result = 0;
        try{
            result =receiptsMapperEx.findStatementAccountCount(beginTime, endTime, organId, supType);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public BigDecimal findAllMoney(Integer supplierId, String type, String subType, String mode, String endTime)throws Exception {
        String modeName = "";
        BigDecimal allOtherMoney = BigDecimal.ZERO;
        if (mode.equals("实际")) {
            modeName = "change_amount";
        } else if (mode.equals("合计")) {
            modeName = "discount_last_money";
            allOtherMoney = receiptsMapperEx.findAllOtherMoney(supplierId, type, subType, endTime);
        }
        BigDecimal result = BigDecimal.ZERO;
        try{
            result =receiptsMapperEx.findAllMoney(supplierId, type, subType, modeName, endTime);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        if(allOtherMoney!=null) {
            result = result.add(allOtherMoney);
        }
        return result;
    }

    /**
     * 统计总金额
     * @param getS
     * @param type
     * @param subType
     * @param mode 合计或者金额
     * @return
     */
    public BigDecimal allMoney(String getS, String type, String subType, String mode, String endTime) {
        BigDecimal allMoney = BigDecimal.ZERO;
        try {
            Integer supplierId = Integer.valueOf(getS);
            BigDecimal sum = findAllMoney(supplierId, type, subType, mode, endTime);
            if(sum != null) {
                allMoney = sum;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回正数，如果负数也转为正数
        if ((allMoney.compareTo(BigDecimal.ZERO))==-1) {
            allMoney = allMoney.abs();
        }
        return allMoney;
    }

    /**
     * 查询单位的累计应收和累计应付，零售不能计入
     * @param supplierId
     * @param endTime
     * @param supType
     * @return
     */
    public BigDecimal findTotalPay(Integer supplierId, String endTime, String supType) {
        BigDecimal sum = BigDecimal.ZERO;
        String getS = supplierId.toString();
        if (("客户").equals(supType)) { //客户
            sum = allMoney(getS, "出库", "销售", "合计",endTime).subtract(allMoney(getS, "出库", "销售", "实际",endTime));
        } else if (("供应商").equals(supType)) { //供应商
            sum = allMoney(getS, "入库", "采购", "合计",endTime).subtract(allMoney(getS, "入库", "采购", "实际",endTime));
        }
        return sum;
    }

    public List<ReceiptsVo4List> getDetailByNumber(String number)throws Exception {
        List<ReceiptsVo4List> resList = new ArrayList<ReceiptsVo4List>();
        List<ReceiptsVo4List> list = null;
        try{
            Map<Long,String> personMap = personService.getPersonMap();
            Map<Long,String> accountMap = accountService.getAccountMap();
            list = receiptsMapperEx.getDetailByNumber(number);
            if (null != list) {
                for (ReceiptsVo4List dh : list) {
                    if(accountMap!=null && StringUtil.isNotEmpty(dh.getAccountIdList()) && StringUtil.isNotEmpty(dh.getAccountMoneyList())) {
                        String accountStr = accountService.getAccountStrByIdAndMoney(accountMap, dh.getAccountIdList(), dh.getAccountMoneyList());
                        dh.setAccountName(accountStr);
                    }
                    if(dh.getAccountIdList() != null) {
                        String accountidlistStr = dh.getAccountIdList().replace("[", "").replace("]", "").replaceAll("\"", "");
                        dh.setAccountIdList(accountidlistStr);
                    }
                    if(dh.getAccountMoneyList() != null) {
                        String accountmoneylistStr = dh.getAccountMoneyList().replace("[", "").replace("]", "").replaceAll("\"", "");
                        dh.setAccountMoneyList(accountmoneylistStr);
                    }
                    if(dh.getChangeAmount() != null) {
                        dh.setChangeAmount(dh.getChangeAmount().abs());
                    }
                    if(dh.getTotalPrice() != null) {
                        dh.setTotalPrice(dh.getTotalPrice().abs());
                    }
                    if(StringUtil.isNotEmpty(dh.getSalesMan())) {
                        dh.setSalesManStr(personService.getPersonByMapAndIds(personMap,dh.getSalesMan()));
                    }
                    dh.setOperTimeStr(getCenternTime(dh.getOperTime()));
                    dh.setMaterialsList(findMaterialsListByHeaderId(dh.getId()));
                    dh.setCreatorName(userService.getUser(dh.getCreator()).getUsername());
                    resList.add(dh);
                }
            }
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return resList;
    }

    /**
     * 新增单据主表及单据子表信息
     * @param beanJson
     * @param rows
     * @param request
     * @throws Exception
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void addReceiptsAndDetail(String beanJson, String rows,
                                      HttpServletRequest request) throws Exception {
        /**处理单据主表数据*/
        Receipts receipts = JSONObject.parseObject(beanJson, Receipts.class);
        String subType = receipts.getSubType();
        //结算账户校验
        if("采购".equals(subType) || "采购退货".equals(subType) || "销售".equals(subType) || "销售退货".equals(subType)) {
            if (StringUtil.isEmpty(receipts.getAccountIdList()) && receipts.getAccountId() == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_ACCOUNT_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_ACCOUNT_FAILED_MSG));
            }
        }
        //欠款校验
        if("采购退货".equals(subType) || "销售退货".equals(subType)) {
            if(receipts.getChangeAmount().abs().compareTo(receipts.getDiscountLastMoney().add(receipts.getOtherMoney()))!=0) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_BACK_BILL_DEBT_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_BACK_BILL_DEBT_FAILED_MSG));
            }
        }
        //判断用户是否已经登录过，登录过不再处理
        User userInfo=userService.getCurrentUser();
        receipts.setCreator(userInfo==null?null:userInfo.getId());
        receipts.setCreateTime(new Timestamp(System.currentTimeMillis()));
        receipts.setStatus(BusinessConstants.BILLS_STATUS_UN_AUDIT);
        receipts.setPayType(receipts.getPayType()==null?"现付":receipts.getPayType());
        if(StringUtil.isNotEmpty(receipts.getAccountIdList())){
            receipts.setAccountIdList(receipts.getAccountIdList().replace("[", "").replace("]", "").replaceAll("\"", ""));
        }
        if(StringUtil.isNotEmpty(receipts.getAccountMoneyList())) {
            //校验多账户的结算金额
            String accountMoneyList = receipts.getAccountMoneyList().replace("[", "").replace("]", "").replaceAll("\"", "");
            int sum = StringUtil.getArrSum(accountMoneyList.split(","));
            BigDecimal manyAccountSum = BigDecimal.valueOf(sum).abs();
            if(manyAccountSum.compareTo(receipts.getChangeAmount().abs())!=0) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_MANY_ACCOUNT_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_MANY_ACCOUNT_FAILED_MSG));
            }
            receipts.setAccountMoneyList(accountMoneyList);
        }
        try{
            receiptsMapper.insertSelective(receipts);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        /**入库和出库处理预付款信息*/
        if(BusinessConstants.PAY_TYPE_PREPAID.equals(receipts.getPayType())){
            if(receipts.getOrganId()!=null) {
                supplierService.updateAdvanceIn(receipts.getOrganId(), BigDecimal.ZERO.subtract(receipts.getTotalPrice()));
            }
        }
        //根据单据编号查询单据id
        ReceiptsExample dhExample = new ReceiptsExample();
        dhExample.createCriteria().andNumberEqualTo(receipts.getNumber()).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<Receipts> list = receiptsMapper.selectByExample(dhExample);
        if(list!=null) {
            Long headId = list.get(0).getId();
            /**入库和出库处理单据子表信息*/
            receiptsItemService.saveDetials(rows,headId, request);
        }
        logService.insertLog("单据",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD).append(receipts.getNumber()).toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }

    /**
     * 更新单据主表及单据子表信息
     * @param beanJson
     * @param rows
     * @param request
     * @throws Exception
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void updateReceiptsAndDetail(String beanJson, String rows,HttpServletRequest request)throws Exception {
        /**更新单据主表信息*/
        Receipts receipts = JSONObject.parseObject(beanJson, Receipts.class);
        //获取之前的金额数据
        BigDecimal preTotalPrice = getReceipts(receipts.getId()).getTotalPrice().abs();
        String subType = receipts.getSubType();
        //结算账户校验
        if("采购".equals(subType) || "采购退货".equals(subType) || "销售".equals(subType) || "销售退货".equals(subType)) {
            if (StringUtil.isEmpty(receipts.getAccountIdList()) && receipts.getAccountId() == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_ACCOUNT_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_ACCOUNT_FAILED_MSG));
            }
        }
        //欠款校验
        if("采购退货".equals(subType) || "销售退货".equals(subType)) {
            if(receipts.getChangeAmount().abs().compareTo(receipts.getDiscountLastMoney().add(receipts.getOtherMoney()))!=0) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_BACK_BILL_DEBT_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_BACK_BILL_DEBT_FAILED_MSG));
            }
        }
        if(StringUtil.isNotEmpty(receipts.getAccountIdList())){
            receipts.setAccountIdList(receipts.getAccountIdList().replace("[", "").replace("]", "").replaceAll("\"", ""));
        }
        if(StringUtil.isNotEmpty(receipts.getAccountMoneyList())) {
            //校验多账户的结算金额
            String accountMoneyList = receipts.getAccountMoneyList().replace("[", "").replace("]", "").replaceAll("\"", "");
            int sum = StringUtil.getArrSum(accountMoneyList.split(","));
            BigDecimal manyAccountSum = BigDecimal.valueOf(sum).abs();
            if(manyAccountSum.compareTo(receipts.getChangeAmount().abs())!=0) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_MANY_ACCOUNT_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_MANY_ACCOUNT_FAILED_MSG));
            }
            receipts.setAccountMoneyList(accountMoneyList);
        }
        try{
            receiptsMapper.updateByPrimaryKeySelective(receipts);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        /**入库和出库处理预付款信息*/
        if(BusinessConstants.PAY_TYPE_PREPAID.equals(receipts.getPayType())){
            if(receipts.getOrganId()!=null){
                supplierService.updateAdvanceIn(receipts.getOrganId(), BigDecimal.ZERO.subtract(receipts.getTotalPrice().subtract(preTotalPrice)));
            }
        }
        /**入库和出库处理单据子表信息*/
        receiptsItemService.saveDetials(rows,receipts.getId(),request);
        logService.insertLog("单据",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(receipts.getNumber()).toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }

    public BigDecimal getBuyAndSaleStatistics(String type, String subType, Integer hasSupplier, String beginTime, String endTime) {
        return receiptsMapperEx.getBuyAndSaleStatistics(type, subType, hasSupplier, beginTime, endTime);
    }

    public BigDecimal getBuyAndSaleRetailStatistics(String type, String subType, String beginTime, String endTime) {
        return receiptsMapperEx.getBuyAndSaleRetailStatistics(type, subType, beginTime, endTime).abs();
    }

    public Receipts getReceipts(String number)throws Exception {
        Receipts receipts = new Receipts();
        try{
            ReceiptsExample example = new ReceiptsExample();
            example.createCriteria().andNumberEqualTo(number).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
            List<Receipts> list = receiptsMapper.selectByExample(example);
            if(null!=list && list.size()>0) {
                receipts = list.get(0);
            }
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return receipts;
    }

    public List<ReceiptsVo4List> debtList(Long organId, String materialParam, String number, String beginTime, String endTime,
                                              String type, String subType, String roleType, String status) {
        List<ReceiptsVo4List> resList = new ArrayList<>();
        try{
            String depotIds = depotService.findDepotStrByCurrentUser();
            String [] depotArray=depotIds.split(",");
            String [] creatorArray = getCreatorArray(roleType);
            beginTime = Tools.parseDayToTime(beginTime,BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime,BusinessConstants.DAY_LAST_TIME);
            List<ReceiptsVo4List> list=receiptsMapperEx.debtList(organId, type, subType, creatorArray, status, number, beginTime, endTime, materialParam, depotArray);
            if (null != list) {
                for (ReceiptsVo4List dh : list) {
                    if(dh.getChangeAmount() != null) {
                        dh.setChangeAmount(dh.getChangeAmount().abs());
                    }
                    if(dh.getTotalPrice() != null) {
                        dh.setTotalPrice(dh.getTotalPrice().abs());
                    }
                    if(dh.getOperTime() != null) {
                        dh.setOperTimeStr(getCenternTime(dh.getOperTime()));
                    }
                    dh.setFinishDebt(accountItemService.getEachAmountByBillId(dh.getId()));
                    dh.setMaterialsList(findMaterialsListByHeaderId(dh.getId()));
                    resList.add(dh);
                }
            }
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return resList;
    }
}
