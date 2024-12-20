package com.hand.demo.domain.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hand.demo.api.dto.InvoiceApplyLineDTO;
import com.hand.demo.infra.constant.Constants;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.common.HZeroCacheKey;
import org.hzero.core.cache.CacheValue;
import org.hzero.core.cache.Cacheable;
import org.hzero.export.annotation.ExcelColumn;

/**
 * (InvoiceApplyHeader)实体类
 *
 * @author Zamzam
 * @since 2024-12-03 11:01:36
 */

@Getter
@Setter
@ApiModel("")
@VersionAudit
@ModifyAudit
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "todo_invoice_apply_header")
public class InvoiceApplyHeader extends AuditDomain{
    private static final long serialVersionUID = -14098534283457100L;

    public static final String FIELD_APPLY_HEADER_ID = "applyHeaderId";
    public static final String FIELD_APPLY_HEADER_NUMBER = "applyHeaderNumber";
    public static final String FIELD_APPLY_STATUS = "applyStatus";
    public static final String FIELD_ATTRIBUTE1 = "attribute1";
    public static final String FIELD_ATTRIBUTE10 = "attribute10";
    public static final String FIELD_ATTRIBUTE11 = "attribute11";
    public static final String FIELD_ATTRIBUTE12 = "attribute12";
    public static final String FIELD_ATTRIBUTE13 = "attribute13";
    public static final String FIELD_ATTRIBUTE14 = "attribute14";
    public static final String FIELD_ATTRIBUTE15 = "attribute15";
    public static final String FIELD_ATTRIBUTE2 = "attribute2";
    public static final String FIELD_ATTRIBUTE3 = "attribute3";
    public static final String FIELD_ATTRIBUTE4 = "attribute4";
    public static final String FIELD_ATTRIBUTE5 = "attribute5";
    public static final String FIELD_ATTRIBUTE6 = "attribute6";
    public static final String FIELD_ATTRIBUTE7 = "attribute7";
    public static final String FIELD_ATTRIBUTE8 = "attribute8";
    public static final String FIELD_ATTRIBUTE9 = "attribute9";
    public static final String FIELD_BILL_TO_ADDRESS = "billToAddress";
    public static final String FIELD_BILL_TO_EMAIL = "billToEmail";
    public static final String FIELD_BILL_TO_PERSON = "billToPerson";
    public static final String FIELD_BILL_TO_PHONE = "billToPhone";
    public static final String FIELD_DEL_FLAG = "delFlag";
    public static final String FIELD_EXCLUDE_TAX_AMOUNT = "excludeTaxAmount";
    public static final String FIELD_INVOICE_COLOR = "invoiceColor";
    public static final String FIELD_INVOICE_TYPE = "invoiceType";
    public static final String FIELD_REMARK = "remark";
    public static final String FIELD_SUBMIT_TIME = "submitTime";
    public static final String FIELD_TAX_AMOUNT = "taxAmount";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_TOTAL_AMOUNT = "totalAmount";

    @ApiModelProperty("PK")
    @Id
    @GeneratedValue
    private Long applyHeaderId;

    @JsonProperty("applyHeaderNumber")
    @ExcelColumn(en = "applyHeaderNumber")
    private String applyHeaderNumber;

    @JsonProperty("applyStatus")
    @ApiModelProperty(value = "（need Value Set） D : Draft S : Success F : Fail C : Canceled")
    @LovValue(lovCode = Constants.LOV_CODE_STATUS)
    @ExcelColumn(en = "applyStatus")
    private String applyStatus;

    @JsonProperty("attribute1")
    private String attribute1;

    @JsonProperty("attribute10")
    private String attribute10;

    @JsonProperty("attribute11")
    private String attribute11;

    @JsonProperty("attribute12")
    private String attribute12;

    @JsonProperty("attribute13")
    private String attribute13;

    @JsonProperty("attribute14")
    private String attribute14;

    @JsonProperty("attribute15")
    private String attribute15;

    @JsonProperty("attribute2")
    private String attribute2;

    @JsonProperty("attribute3")
    private String attribute3;

    @JsonProperty("attribute4")
    private String attribute4;

    @JsonProperty("attribute5")
    private String attribute5;

    @JsonProperty("attribute6")
    private String attribute6;

    @JsonProperty("attribute7")
    private String attribute7;

    @JsonProperty("attribute8")
    private String attribute8;

    @JsonProperty("attribute9")
    private String attribute9;

    @JsonProperty("billToAddress")
    @ExcelColumn(en = "billToAddress")
    private String billToAddress;

    @JsonProperty("billToEmail")
    @ExcelColumn(en = "billToEmail")
    private String billToEmail;

    @JsonProperty("billToPerson")
    @ExcelColumn(en = "billToPerson")
    private String billToPerson;

    @JsonProperty("billToPhone")
    @ExcelColumn(en = "billToPhone")
    private String billToPhone;

    @JsonProperty("delFlag")
    @ApiModelProperty(value = "1 : deleted 0 : normal")
    @ExcelColumn(en = "delFlag")
    private Integer delFlag;

    @JsonProperty("excludeTaxAmount")
    @ApiModelProperty(value = "sum(line exclude_tax_amount)")
    @ExcelColumn(en = "excludeTaxAmount")
    private BigDecimal excludeTaxAmount;

    @JsonProperty("invoiceColor")
    @ApiModelProperty(value = "(need Value Set) R : Red invoice B : Blue invoice")
    @LovValue(lovCode = Constants.LOV_CODE_INVOICE_COLOR)
    @ExcelColumn(en = "invoiceColor")
    private String invoiceColor;

    @JsonProperty("invoiceType")
    @ApiModelProperty(value = "(need Value Set) P : Paper invoice E : E-invoice")
    @LovValue(lovCode = Constants.LOV_CODE_INVOICE_TYPE)
    @ExcelColumn(en = "invoiceType")
    private String invoiceType;

    @JsonProperty("remark")
    @ExcelColumn(en = "remark")
    private String remark;

    @JsonProperty("submitTime")
    @ExcelColumn(en = "submitTime")
    private Date submitTime;

    @JsonProperty("taxAmount")
    @ApiModelProperty(value = "sum(line tax_amount)")
    @ExcelColumn(en = "taxAmount")
    private BigDecimal taxAmount;

    @JsonProperty("tenantId")
    @ExcelColumn(en = "tenantId")
    private Long tenantId;

    @JsonProperty("totalAmount")
    @ApiModelProperty(value = "sum(line total_amount)")
    @ExcelColumn(en = "totalAmount")
    private BigDecimal totalAmount;

    @JsonProperty("invoiceApplyLineList")
    @Transient
    @ExcelColumn(promptCode = "child", promptKey = "child", child = true)
    private List<InvoiceApplyLineDTO> invoiceApplyLineList;

//    @Transient
//    @ExcelColumn(promptCode = "line", promptKey = "line", child = true)
//    private List<InvoiceApplyLineDTO> invoiceApplyLineListDto;

}

