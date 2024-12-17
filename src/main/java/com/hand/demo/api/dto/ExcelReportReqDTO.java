package com.hand.demo.api.dto;

import com.hand.demo.infra.constant.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hzero.boot.platform.lov.annotation.LovValue;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ExcelReportReqDTO{
    //parameter
    String applyHeaderNumberStart;
    String applyHeaderNumberEnd;
    LocalDate dateStartFrom;
    LocalDate dateEndTo;
    LocalDate submitStartFrom;
    LocalDate submitEndTo;
    @LovValue(lovCode = Constants.LOV_CODE_INVOICE_TYPE)
    String invoiceType;
    String tenantName;
    List<String> applyStatusList;

    //Data
    List<InvoiceApplyHeaderDTO> invoiceApplyHeaderDTOList;
}
