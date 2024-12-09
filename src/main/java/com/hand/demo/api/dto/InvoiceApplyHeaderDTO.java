package com.hand.demo.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.infra.constant.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.common.HZeroCacheKey;
import org.hzero.core.cache.CacheValue;
import org.hzero.core.cache.Cacheable;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@ExcelSheet(en = "Invoice Header")
@Getter
@Setter
public class InvoiceApplyHeaderDTO extends InvoiceApplyHeader{
    @ExcelColumn(en = "applyStatusMeaning")
    private String applyStatusMeaning;
    @ExcelColumn(en = "invoiceColorMeaning")
    private String invoiceColorMeaning;
    @ExcelColumn(en = "invoiceTypeMeaning")
    private String invoiceTypeMeaning;
    private List<Long> applyLineIdList;

}
