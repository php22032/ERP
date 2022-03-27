package com.ywy.erp.service.receiptsItem;

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

@Service(value = "receiptsItem_component")
@ReceiptsItemResource
public class ReceiptsItemComponent implements ICommonQuery {

    @Resource
    private ReceiptsItemService receiptsItemService;

    @Override
    public Object selectOne(Long id) throws Exception {
        return receiptsItemService.getReceiptsItem(id);
    }

    @Override
    public List<?> select(Map<String, String> map)throws Exception {
        return getReceiptsItemList(map);
    }

    private List<?> getReceiptsItemList(Map<String, String> map)throws Exception {
        String search = map.get(Constants.SEARCH);
        String name = StringUtil.getInfo(search, "name");
        Integer type = StringUtil.parseInteger(StringUtil.getInfo(search, "type"));
        String remark = StringUtil.getInfo(search, "remark");
        String order = QueryUtils.order(map);
        return receiptsItemService.select(name, type, remark, QueryUtils.offset(map), QueryUtils.rows(map));
    }

    @Override
    public Long counts(Map<String, String> map)throws Exception {
        String search = map.get(Constants.SEARCH);
        String name = StringUtil.getInfo(search, "name");
        Integer type = StringUtil.parseInteger(StringUtil.getInfo(search, "type"));
        String remark = StringUtil.getInfo(search, "remark");
        return receiptsItemService.countReceiptsItem(name, type, remark);
    }

    @Override
    public int insert(JSONObject obj, HttpServletRequest request)throws Exception {
        return receiptsItemService.insertReceiptsItem(obj, request);
    }

    @Override
    public int update(JSONObject obj, HttpServletRequest request)throws Exception {
        return receiptsItemService.updateReceiptsItem(obj, request);
    }

    @Override
    public int delete(Long id, HttpServletRequest request)throws Exception {
        return receiptsItemService.deleteReceiptsItem(id, request);
    }

    @Override
    public int deleteBatch(String ids, HttpServletRequest request)throws Exception {
        return receiptsItemService.batchDeleteReceiptsItem(ids, request);
    }

    @Override
    public int checkIsNameExist(Long id, String name)throws Exception {
        return receiptsItemService.checkIsNameExist(id, name);
    }

}
