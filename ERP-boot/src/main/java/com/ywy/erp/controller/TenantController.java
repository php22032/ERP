package com.ywy.erp.controller;

import com.alibaba.fastjson.JSONObject;
import com.ywy.erp.service.tenant.TenantService;
import com.ywy.erp.utils.*;
import com.ywy.erp.utils.ErpInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.ywy.erp.utils.ResponseJsonUtil.returnJson;

/**
 * @author ji_sheng_hua 百货商场管理系统
 */
@RestController
@RequestMapping(value = "/tenant")
@Api(tags = {"租户管理"})
public class TenantController {
    private Logger logger = LoggerFactory.getLogger(TenantController.class);

    @Resource
    private TenantService tenantService;

    /**
     * 批量设置状态-启用或者禁用
     * @param jsonObject
     * @param request
     * @return
     */
    @PostMapping(value = "/batchSetStatus")
    @ApiOperation(value = "批量设置状态")
    public String batchSetStatus(@RequestBody JSONObject jsonObject,
                                 HttpServletRequest request)throws Exception {
        Boolean status = jsonObject.getBoolean("status");
        String ids = jsonObject.getString("ids");
        Map<String, Object> objectMap = new HashMap<>();
        int res = tenantService.batchSetStatus(status, ids);
        if(res > 0) {
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }
}
