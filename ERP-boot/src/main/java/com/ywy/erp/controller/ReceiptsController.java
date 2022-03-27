package com.ywy.erp.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ywy.erp.constants.BusinessConstants;
import com.ywy.erp.constants.ExceptionConstants;
import com.ywy.erp.entities.ReceiptsVo4Body;
import com.ywy.erp.entities.Supplier;
import com.ywy.erp.vo.ReceiptsVo4InDetail;
import com.ywy.erp.vo.ReceiptsVo4InOutMCount;
import com.ywy.erp.vo.ReceiptsVo4List;
import com.ywy.erp.vo.ReceiptsVo4StatementAccount;
import com.ywy.erp.service.accountHead.AccountHeadService;
import com.ywy.erp.service.depot.DepotService;
import com.ywy.erp.service.receipts.ReceiptsService;
import com.ywy.erp.service.redis.RedisService;
import com.ywy.erp.service.supplier.SupplierService;
import com.ywy.erp.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ywy.erp.utils.ResponseJsonUtil.returnJson;
import static com.ywy.erp.utils.Tools.getNow3;

/**
 * @author ji-sheng-hua 752*718*920
 */
@RestController
@RequestMapping(value = "/receipts")
@Api(tags = {"单据管理"})
public class ReceiptsController {
    private Logger logger = LoggerFactory.getLogger(ReceiptsController.class);

    @Resource
    private ReceiptsService receiptsService;

    @Resource
    private AccountHeadService accountHeadService;

    @Resource
    private SupplierService supplierService;

    @Resource
    private DepotService depotService;

    @Resource
    private RedisService redisService;

    /**
     * 批量设置状态-审核或者反审核
     * @param jsonObject
     * @param request
     * @return
     */
    @PostMapping(value = "/batchSetStatus")
    @ApiOperation(value = "批量设置状态-审核或者反审核")
    public String batchSetStatus(@RequestBody JSONObject jsonObject,
                                 HttpServletRequest request) throws Exception{
        Map<String, Object> objectMap = new HashMap<>();
        String status = jsonObject.getString("status");
        String ids = jsonObject.getString("ids");
        int res = receiptsService.batchSetStatus(status, ids);
        if(res > 0) {
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    /**
     * 入库出库明细接口
     * @param currentPage
     * @param pageSize
     * @param oId
     * @param number
     * @param materialParam
     * @param depotId
     * @param beginTime
     * @param endTime
     * @param type
     * @param request
     * @return
     */
    @GetMapping(value = "/findInDetail")
    @ApiOperation(value = "入库出库明细接口")
    public BaseResponseInfo findInDetail(@RequestParam("currentPage") Integer currentPage,
                                         @RequestParam("pageSize") Integer pageSize,
                                         @RequestParam(value = "organId", required = false) Integer oId,
                                         @RequestParam("number") String number,
                                         @RequestParam("materialParam") String materialParam,
                                         @RequestParam(value = "depotId", required = false) Long depotId,
                                         @RequestParam("beginTime") String beginTime,
                                         @RequestParam("endTime") String endTime,
                                         @RequestParam("type") String type,
                                         HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            List<Long> depotList = new ArrayList<>();
            if(depotId != null) {
                depotList.add(depotId);
            } else {
                //未选择仓库时默认为当前用户有权限的仓库
                JSONArray depotArr = depotService.findDepotByCurrentUser();
                for(Object obj: depotArr) {
                    JSONObject object = JSONObject.parseObject(obj.toString());
                    depotList.add(object.getLong("id"));
                }
            }
            List<ReceiptsVo4InDetail> resList = new ArrayList<ReceiptsVo4InDetail>();
            beginTime = Tools.parseDayToTime(beginTime, BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime,BusinessConstants.DAY_LAST_TIME);
            List<ReceiptsVo4InDetail> list = receiptsService.findByAll(beginTime, endTime, type, materialParam, depotList, oId, number, (currentPage-1)*pageSize, pageSize);
            int total = receiptsService.findByAllCount(beginTime, endTime, type, materialParam, depotList, oId, number);
            map.put("total", total);
            //存放数据json数组
            if (null != list) {
                for (ReceiptsVo4InDetail dhd : list) {
                    resList.add(dhd);
                }
            }
            map.put("rows", resList);
            res.code = 200;
            res.data = map;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 入库出库统计接口
     * @param currentPage
     * @param pageSize
     * @param oId
     * @param materialParam
     * @param depotId
     * @param beginTime
     * @param endTime
     * @param type
     * @param request
     * @return
     */
    @GetMapping(value = "/findInOutMaterialCount")
    @ApiOperation(value = "入库出库统计接口")
    public BaseResponseInfo findInOutMaterialCount(@RequestParam("currentPage") Integer currentPage,
                                         @RequestParam("pageSize") Integer pageSize,
                                         @RequestParam(value = "organId", required = false) Integer oId,
                                         @RequestParam("materialParam") String materialParam,
                                         @RequestParam(value = "depotId", required = false) Long depotId,
                                         @RequestParam("beginTime") String beginTime,
                                         @RequestParam("endTime") String endTime,
                                         @RequestParam("type") String type,
                                         HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            List<Long> depotList = new ArrayList<>();
            if(depotId != null) {
                depotList.add(depotId);
            } else {
                //未选择仓库时默认为当前用户有权限的仓库
                JSONArray depotArr = depotService.findDepotByCurrentUser();
                for(Object obj: depotArr) {
                    JSONObject object = JSONObject.parseObject(obj.toString());
                    depotList.add(object.getLong("id"));
                }
            }
            List<ReceiptsVo4InOutMCount> resList = new ArrayList<>();
            beginTime = Tools.parseDayToTime(beginTime,BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime,BusinessConstants.DAY_LAST_TIME);
            List<ReceiptsVo4InOutMCount> list = receiptsService.findInOutMaterialCount(beginTime, endTime, type, materialParam, depotList, oId, (currentPage-1)*pageSize, pageSize);
            int total = receiptsService.findInOutMaterialCountTotal(beginTime, endTime, type, materialParam, depotList, oId);
            map.put("total", total);
            //存放数据json数组
            if (null != list) {
                for (ReceiptsVo4InOutMCount dhc : list) {
                    resList.add(dhc);
                }
            }
            map.put("rows", resList);
            res.code = 200;
            res.data = map;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 调拨明细统计
     * @param currentPage
     * @param pageSize
     * @param number
     * @param materialParam
     * @param depotIdF  调出仓库
     * @param depotId  调入仓库
     * @param beginTime
     * @param endTime
     * @param subType
     * @param request
     * @return
     */
    @GetMapping(value = "/findAllocationDetail")
    @ApiOperation(value = "调拨明细统计")
    public BaseResponseInfo findallocationDetail(@RequestParam("currentPage") Integer currentPage,
                                                 @RequestParam("pageSize") Integer pageSize,
                                                 @RequestParam("number") String number,
                                                 @RequestParam("materialParam") String materialParam,
                                                 @RequestParam(value = "depotId", required = false) Long depotId,
                                                 @RequestParam(value = "depotIdF", required = false) Long depotIdF,
                                                 @RequestParam("beginTime") String beginTime,
                                                 @RequestParam("endTime") String endTime,
                                                 @RequestParam("subType") String subType,
                                                 HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            List<Long> depotList = new ArrayList<>();
            List<Long> depotFList = new ArrayList<>();
            if(depotId != null) {
                depotList.add(depotId);
            } else {
                //未选择仓库时默认为当前用户有权限的仓库
                JSONArray depotArr = depotService.findDepotByCurrentUser();
                for(Object obj: depotArr) {
                    JSONObject object = JSONObject.parseObject(obj.toString());
                    depotList.add(object.getLong("id"));
                }
            }
            if(depotIdF != null) {
                depotFList.add(depotIdF);
            } else {
                //未选择仓库时默认为当前用户有权限的仓库
                JSONArray depotArr = depotService.findDepotByCurrentUser();
                for(Object obj: depotArr) {
                    JSONObject object = JSONObject.parseObject(obj.toString());
                    depotFList.add(object.getLong("id"));
                }
            }
            beginTime = Tools.parseDayToTime(beginTime, BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime,BusinessConstants.DAY_LAST_TIME);
            List<ReceiptsVo4InDetail> list = receiptsService.findAllocationDetail(beginTime, endTime, subType, number, materialParam, depotList, depotFList, (currentPage-1)*pageSize, pageSize);
            int total = receiptsService.findAllocationDetailCount(beginTime, endTime, subType, number, materialParam, depotList, depotFList);
            map.put("rows", list);
            map.put("total", total);
            res.code = 200;
            res.data = map;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 对账单接口
     * @param currentPage
     * @param pageSize
     * @param beginTime
     * @param endTime
     * @param organId
     * @param supType
     * @param request
     * @return
     */
    @GetMapping(value = "/findStatementAccount")
    @ApiOperation(value = "对账单接口")
    public BaseResponseInfo findStatementAccount(@RequestParam("currentPage") Integer currentPage,
                                                   @RequestParam("pageSize") Integer pageSize,
                                                   @RequestParam("beginTime") String beginTime,
                                                   @RequestParam("endTime") String endTime,
                                                   @RequestParam(value = "organId", required = false) Integer organId,
                                                   @RequestParam("supType") String supType,
                                                   HttpServletRequest request) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            beginTime = Tools.parseDayToTime(beginTime,BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime,BusinessConstants.DAY_LAST_TIME);
            List<ReceiptsVo4StatementAccount> list = receiptsService.findStatementAccount(beginTime, endTime, organId, supType, (currentPage-1)*pageSize, pageSize);
            int total = receiptsService.findStatementAccountCount(beginTime, endTime, organId, supType);
            map.put("rows", list);
            map.put("total", total);
            if(null!=organId) {
                Supplier supplier = supplierService.getSupplier(organId);
                BigDecimal beginNeed = BigDecimal.ZERO;
                if (("客户").equals(supType)) {
                    if(supplier.getBeginNeedGet()!=null) {
                        beginNeed = supplier.getBeginNeedGet();
                    }
                } else if (("供应商").equals(supType)) {
                    if(supplier.getBeginNeedPay()!=null) {
                        beginNeed = supplier.getBeginNeedPay();
                    }
                }
                BigDecimal firstMoney = receiptsService.findTotalPay(organId, beginTime, supType)
                        .subtract(accountHeadService.findTotalPay(organId, beginTime, supType)).add(beginNeed);
                BigDecimal lastMoney = receiptsService.findTotalPay(organId, endTime, supType)
                        .subtract(accountHeadService.findTotalPay(organId, endTime, supType)).add(beginNeed);
                map.put("firstMoney", firstMoney); //期初
                map.put("lastMoney", lastMoney);  //期末
            }
            res.code = 200;
            res.data = map;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 根据编号查询单据信息
     * @param number
     * @param request
     * @return
     */
    @GetMapping(value = "/getDetailByNumber")
    @ApiOperation(value = "根据编号查询单据信息")
    public BaseResponseInfo getDetailByNumber(@RequestParam("number") String number,
                                         HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
       ReceiptsVo4List dhl = new ReceiptsVo4List();
        try {
            List<ReceiptsVo4List> list = receiptsService.getDetailByNumber(number);
            if(list.size() == 1) {
                dhl = list.get(0);
            }
            res.code = 200;
            res.data = dhl;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 新增单据主表及单据子表信息
     * @param body
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/addReceiptsAndDetail")
    @ApiOperation(value = "新增单据主表及单据子表信息")
    public Object addReceiptsAndDetail(@RequestBody ReceiptsVo4Body body, HttpServletRequest request) throws  Exception{
        JSONObject result = ExceptionConstants.standardSuccess();
        String beanJson = body.getInfo();
        String rows = body.getRows();
        receiptsService.addReceiptsAndDetail(beanJson, rows, request);
        return result;
    }

    /**
     * 更新单据主表及单据子表信息
     * @param body
     * @param request
     * @return
     * @throws Exception
     */
    @PutMapping(value = "/updateReceiptsAndDetail")
    @ApiOperation(value = "更新单据主表及单据子表信息")
    public Object updateReceiptsAndDetail(@RequestBody ReceiptsVo4Body body, HttpServletRequest request) throws Exception{
        JSONObject result = ExceptionConstants.standardSuccess();
        String beanJson = body.getInfo();
        String rows = body.getRows();
        receiptsService.updateReceiptsAndDetail(beanJson,rows,request);
        return result;
    }

    /**
     * 统计今日采购额、本月采购额、今日销售额、本月销售额、今日零售额、本月零售额
     * @param request
     * @return
     */
    @GetMapping(value = "/getBuyAndSaleStatistics")
    @ApiOperation(value = "统计今日采购额、本月采购额、今日销售额、本月销售额、今日零售额、本月零售额")
    public BaseResponseInfo getBuyAndSaleStatistics(HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            String today = Tools.getNow() + BusinessConstants.DAY_FIRST_TIME;
            String firstDay = Tools.firstDayOfMonth(Tools.getCurrentMonth()) + BusinessConstants.DAY_FIRST_TIME;
            BigDecimal todayBuy = receiptsService.getBuyAndSaleStatistics("入库", "采购",
                    1, today, getNow3()); //今日采购入库
            BigDecimal todayBuyBack = receiptsService.getBuyAndSaleStatistics("出库", "采购退货",
                    1, today, getNow3()); //今日采购退货
            BigDecimal todaySale = receiptsService.getBuyAndSaleStatistics("出库", "销售",
                    1, today, getNow3()); //今日销售出库
            BigDecimal todaySaleBack = receiptsService.getBuyAndSaleStatistics("入库", "销售退货",
                    1, today, getNow3()); //今日销售退货
            BigDecimal todayRetailSale = receiptsService.getBuyAndSaleRetailStatistics("出库", "零售",
                    today, getNow3()); //今日零售出库
            BigDecimal todayRetailSaleBack = receiptsService.getBuyAndSaleRetailStatistics("入库", "零售退货",
                    today, getNow3()); //今日零售退货
            BigDecimal monthBuy = receiptsService.getBuyAndSaleStatistics("入库", "采购",
                    1, firstDay, getNow3()); //本月采购入库
            BigDecimal monthBuyBack = receiptsService.getBuyAndSaleStatistics("出库", "采购退货",
                    1, firstDay, getNow3()); //本月采购退货
            BigDecimal monthSale = receiptsService.getBuyAndSaleStatistics("出库", "销售",
                    1,firstDay, getNow3()); //本月销售出库
            BigDecimal monthSaleBack = receiptsService.getBuyAndSaleStatistics("入库", "销售退货",
                    1,firstDay, getNow3()); //本月销售退货
            BigDecimal monthRetailSale = receiptsService.getBuyAndSaleRetailStatistics("出库", "零售",
                    firstDay, getNow3()); //本月零售出库
            BigDecimal monthRetailSaleBack = receiptsService.getBuyAndSaleRetailStatistics("入库", "零售退货",
                    firstDay, getNow3()); //本月零售退货
            map.put("todayBuy", todayBuy.subtract(todayBuyBack));
            map.put("todaySale", todaySale.subtract(todaySaleBack));
            map.put("todayRetailSale", todayRetailSale.subtract(todayRetailSaleBack));
            map.put("monthBuy", monthBuy.subtract(monthBuyBack));
            map.put("monthSale", monthSale.subtract(monthSaleBack));
            map.put("monthRetailSale", monthRetailSale.subtract(monthRetailSaleBack));
            res.code = 200;
            res.data = map;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 根据当前用户获取操作员数组，用于控制当前用户的数据权限，限制可以看到的单据范围
     * 注意：该接口提供给部分插件使用，勿删
     * @param request
     * @return
     */
    @GetMapping(value = "/getCreatorByCurrentUser")
    @ApiOperation(value = "根据当前用户获取操作员数组")
    public BaseResponseInfo getCreatorByRoleType(HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            String creator = "";
            String roleType = redisService.getObjectFromSessionByKey(request,"roleType").toString();
            if(StringUtil.isNotEmpty(roleType)) {
                creator = receiptsService.getCreatorByRoleType(roleType);
            }
            res.code = 200;
            res.data = creator;
        } catch (Exception e) {
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 查询存在欠款的单据
     * @param search
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/debtList")
    @ApiOperation(value = "查询存在欠款的单据")
    public String debtList(@RequestParam(value = Constants.SEARCH, required = false) String search,
                           HttpServletRequest request)throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        String organIdStr = StringUtil.getInfo(search, "organId");
        Long organId = Long.parseLong(organIdStr);
        String materialParam = StringUtil.getInfo(search, "materialParam");
        String number = StringUtil.getInfo(search, "number");
        String beginTime = StringUtil.getInfo(search, "beginTime");
        String endTime = StringUtil.getInfo(search, "endTime");
        String type = StringUtil.getInfo(search, "type");
        String subType = StringUtil.getInfo(search, "subType");
        String roleType = StringUtil.getInfo(search, "roleType");
        String status = StringUtil.getInfo(search, "status");
        List<ReceiptsVo4List> list = receiptsService.debtList(organId, materialParam, number, beginTime, endTime, type, subType, roleType, status);
        if (list != null) {
            objectMap.put("rows", list);
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            objectMap.put("rows", new ArrayList<>());
            return returnJson(objectMap, "查找不到数据", ErpInfo.OK.code);
        }
    }
}
