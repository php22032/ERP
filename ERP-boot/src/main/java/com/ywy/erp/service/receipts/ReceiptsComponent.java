package com.ywy.erp.service.receipts;

import com.alibaba.fastjson.JSONObject;
import com.ywy.erp.service.ICommonQuery;
import com.ywy.erp.utils.Constants;
import com.ywy.erp.utils.QueryUtils;
import com.ywy.erp.utils.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service(value = "receipts_component")
@ReceiptsResource
public class ReceiptsComponent implements ICommonQuery {

    @Resource
    private ReceiptsService receiptsService;

    @Override
    public Object selectOne(Long id) throws Exception {
        return receiptsService.getReceipts(id);
    }

    @Override
    public List<?> select(Map<String, String> map)throws Exception {
        return getReceiptsList(map);
    }

    private List<?> getReceiptsList(Map<String, String> map)throws Exception {
        String search = map.get(Constants.SEARCH);
        String type = StringUtil.getInfo(search, "type");
        String subType = StringUtil.getInfo(search, "subType");
        String roleType = StringUtil.getInfo(search, "roleType");
        String status = StringUtil.getInfo(search, "status");
        String number = StringUtil.getInfo(search, "number");
        String linkNumber = StringUtil.getInfo(search, "linkNumber");
        String beginTime = StringUtil.getInfo(search, "beginTime");
        String endTime = StringUtil.getInfo(search, "endTime");
        String materialParam = StringUtil.getInfo(search, "materialParam");
        Long organId = StringUtil.parseStrLong(StringUtil.getInfo(search, "organId"));
        Long creator = StringUtil.parseStrLong(StringUtil.getInfo(search, "creator"));
        Long depotId = StringUtil.parseStrLong(StringUtil.getInfo(search, "depotId"));
        return receiptsService.select(type, subType, roleType, status, number, linkNumber, beginTime, endTime, materialParam,
                organId, creator, depotId, QueryUtils.offset(map), QueryUtils.rows(map));
    }

    @Override
    public Long counts(Map<String, String> map)throws Exception {
        String search = map.get(Constants.SEARCH);
        String type = StringUtil.getInfo(search, "type");
        String subType = StringUtil.getInfo(search, "subType");
        String roleType = StringUtil.getInfo(search, "roleType");
        String status = StringUtil.getInfo(search, "status");
        String number = StringUtil.getInfo(search, "number");
        String linkNumber = StringUtil.getInfo(search, "linkNumber");
        String beginTime = StringUtil.getInfo(search, "beginTime");
        String endTime = StringUtil.getInfo(search, "endTime");
        String materialParam = StringUtil.getInfo(search, "materialParam");
        Long organId = StringUtil.parseStrLong(StringUtil.getInfo(search, "organId"));
        Long creator = StringUtil.parseStrLong(StringUtil.getInfo(search, "creator"));
        Long depotId = StringUtil.parseStrLong(StringUtil.getInfo(search, "depotId"));
        return receiptsService.countReceipts(type, subType, roleType, status, number, linkNumber, beginTime, endTime, materialParam,
                organId, creator, depotId);
    }

    @Override
    public int insert(JSONObject obj, HttpServletRequest request) throws Exception{
        return receiptsService.insertReceipts(obj, request);
    }

    @Override
    public int update(JSONObject obj, HttpServletRequest request)throws Exception {
        return receiptsService.updateReceipts(obj, request);
    }

    @Override
    public int delete(Long id, HttpServletRequest request)throws Exception {
        return receiptsService.deleteReceipts(id, request);
    }

    @Override
    public int deleteBatch(String ids, HttpServletRequest request)throws Exception {
        return receiptsService.batchDeleteReceipts(ids, request);
    }

    @Override
    public int checkIsNameExist(Long id, String name)throws Exception {
        return receiptsService.checkIsNameExist(id, name);
    }

}
