package com.ywy.erp.entities;

/**
 * Description
 *
 * @Author: cjl
 * @Date: 2019/1/21 17:32
 */
public class SerialNumberEx extends SerialNumber{
    /**
     * 商品条码
     * */
    private String materialCode;
    /**
     * 商品名称
     * */
    private String materialName;
    /**
     * 创建者名称
     * */
    private String creatorName;
    /**
     * 更新者名称
     * */
    private String updaterName;
    /**单据编号*/
    private String receiptsNumber;
    /**单据类型（出库入库）*/
    private String receiptsType;

    private String depotName;

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getUpdaterName() {
        return updaterName;
    }

    public void setUpdaterName(String updaterName) {
        this.updaterName = updaterName;
    }

    public String getReceiptsNumber() {
        return receiptsNumber;
    }

    public void setReceiptsNumber(String receiptsNumber) {
        this.receiptsNumber = receiptsNumber;
    }

    public String getReceiptsType() {
        return receiptsType;
    }

    public void setReceiptsType(String receiptsType) {
        this.receiptsType = receiptsType;
    }

    public String getDepotName() {
        return depotName;
    }

    public void setDepotName(String depotName) {
        this.depotName = depotName;
    }
}
