package com.hand.demo.domain.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hzero.export.annotation.ExcelColumn;

/**
 * (InvoiceApplyLine)实体类
 *
 * @author Zamzam
 * @since 2024-12-03 11:01:36
 */

@Getter
@Setter
@ApiModel("")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "todo_invoice_apply_line")
@Accessors(chain = true)
public class InvoiceApplyLine extends AuditDomain {
    private static final long serialVersionUID = -34514684423664147L;

    public static final String FIELD_APPLY_LINE_ID = "applyLineId";
    public static final String FIELD_APPLY_HEADER_ID = "applyHeaderId";
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
    public static final String FIELD_CONTENT_NAME = "contentName";
    public static final String FIELD_EXCLUDE_TAX_AMOUNT = "excludeTaxAmount";
    public static final String FIELD_INVOICE_NAME = "invoiceName";
    public static final String FIELD_QUANTITY = "quantity";
    public static final String FIELD_REMARK = "remark";
    public static final String FIELD_TAX_AMOUNT = "taxAmount";
    public static final String FIELD_TAX_CLASSIFICATION_NUMBER = "taxClassificationNumber";
    public static final String FIELD_TAX_RATE = "taxRate";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_TOTAL_AMOUNT = "totalAmount";
    public static final String FIELD_UNIT_PRICE = "unitPrice";

    @JsonProperty("applyLineId")
    @ApiModelProperty("PK")
    @Id
    @GeneratedValue
    @ExcelColumn(en = "applyLineId")
    private Long applyLineId;

    @JsonProperty("applyHeaderId")
    @ApiModelProperty(value = "header id")
    private Long applyHeaderId;

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

    @JsonProperty("contentName")
    @ExcelColumn(en = "contentName")
    @NotNull(groups = Save.class, message = "multilingual")
    private String contentName;

    @JsonProperty("excludeTaxAmount")
    @ApiModelProperty(value = "total_amount - tax_amount")
    @ExcelColumn(en = "excludeTaxAmount")
    private BigDecimal excludeTaxAmount;

    @JsonProperty("invoiceName")
    @ExcelColumn(en = "invoiceName")
    @NotNull(groups = Save.class, message = "multilingual")
    private String invoiceName;

    @JsonProperty("quantity")
    @ExcelColumn(en = "quantity")
    @NotNull(groups = Save.class, message = "multilingual")
    private BigDecimal quantity;

    @JsonProperty("remark")
    @ExcelColumn(en = "remark")
    @NotNull(groups = Save.class, message = "hexam-47835.null_line_remark")
    private String remark;

    @JsonProperty("taxAmount")
    @ExcelColumn(en = "taxAmount")
    private BigDecimal taxAmount;

    @JsonProperty("taxClassificationNumber")
    @ExcelColumn(en = "taxClassificationNumber")
    @NotNull(groups = Save.class, message = "multilingual")
    private String taxClassificationNumber;

    @JsonProperty("taxRate")
    @ExcelColumn(en = "taxRate")
    @NotNull(groups = Save.class, message = "multilingual")
    private BigDecimal taxRate;

    @JsonProperty("tenantId")
    @ExcelColumn(en = "tenantId")
    @NotNull(groups = Save.class, message = "multilingual")
    private Long tenantId;

    @JsonProperty("totalAmount")
    @ExcelColumn(en = "totalAmount")
    private BigDecimal totalAmount;

    @JsonProperty("unitPrice")
    @ExcelColumn(en = "unitPrice")
    @NotNull(groups = Save.class, message = "multilingual")
    private BigDecimal unitPrice;

    @Transient
    private List<Long> applyHeaderIdList;

    public interface Save { }
}

