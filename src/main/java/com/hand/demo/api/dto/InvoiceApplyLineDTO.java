package com.hand.demo.api.dto;

import com.hand.demo.domain.entity.InvoiceApplyLine;
import lombok.Getter;
import lombok.Setter;
import org.hzero.export.annotation.ExcelSheet;

@ExcelSheet(en = "Invoice Line")
@Getter
@Setter
public class InvoiceApplyLineDTO extends InvoiceApplyLine {
}
