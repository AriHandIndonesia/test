package com.hand.demo.app.service.impl;

import com.hand.demo.api.dto.InvoiceApplyHeaderDTO;
import com.hand.demo.api.dto.InvoiceApplyLineDTO;
import com.hand.demo.domain.repository.InvoiceApplyHeaderRepository;
import com.hand.demo.infra.constant.Constants;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import com.hand.demo.app.service.InvoiceApplyLineService;
import org.springframework.stereotype.Service;
import com.hand.demo.domain.entity.InvoiceApplyLine;
import com.hand.demo.domain.repository.InvoiceApplyLineRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * (InvoiceApplyLine)应用服务
 *
 * @author Zamzam
 * @since 2024-12-03 11:01:37
 */
@Service
public class InvoiceApplyLineServiceImpl implements InvoiceApplyLineService {
    @Autowired
    private InvoiceApplyLineRepository invoiceApplyLineRepository;
    @Autowired
    private InvoiceApplyHeaderRepository invoiceApplyHeaderRepository;

    @Override
    public Page<InvoiceApplyLine> selectList(PageRequest pageRequest, InvoiceApplyLine invoiceApplyLine) {
        return PageHelper.doPageAndSort(pageRequest, () -> invoiceApplyLineRepository.selectList(invoiceApplyLine));
    }

    @Override
    public void saveData(List<InvoiceApplyLine> invoiceApplyLines) {
        //V 1.1 [S]
        validate(invoiceApplyLines);
        calculate(invoiceApplyLines, false);
        //V 1.1 [E]
        List<InvoiceApplyLine> insertList = invoiceApplyLines.stream().filter(line -> line.getApplyLineId() == null).collect(Collectors.toList());
        List<InvoiceApplyLine> updateList = invoiceApplyLines.stream().filter(line -> line.getApplyLineId() != null).collect(Collectors.toList());
        invoiceApplyLineRepository.batchInsertSelective(insertList);
        invoiceApplyLineRepository.batchUpdateByPrimaryKeySelective(updateList);
        calculate(invoiceApplyLines, true); //V 1.1 [S.E]
    }

    //calculate
    @Override
    public void calculate(List<InvoiceApplyLine> invoiceApplyLines, Boolean doUpdateHeader) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal exclude = BigDecimal.ZERO;
        BigDecimal tax_amount = BigDecimal.ZERO;
        Long headerId = invoiceApplyLines.get(0).getApplyHeaderId();
        if (doUpdateHeader) {
            InvoiceApplyLine invoiceApplyLine = new InvoiceApplyLine();
            invoiceApplyLine.setApplyHeaderId(headerId);
            invoiceApplyLines = invoiceApplyLineRepository.selectList(invoiceApplyLine);
        }
        //get header total
        for (InvoiceApplyLine invoiceApplyLines1 : invoiceApplyLines) {
            BigDecimal totalAmount = invoiceApplyLines1.getQuantity().multiply(invoiceApplyLines1.getUnitPrice());
            BigDecimal taxAmount = totalAmount.multiply(invoiceApplyLines1.getTaxRate());
            BigDecimal excludeAmount = totalAmount.subtract(taxAmount);

            total = total.add(totalAmount);
            tax_amount = tax_amount.add(taxAmount);
            exclude = exclude.add(excludeAmount);

            invoiceApplyLines1.setTotalAmount(totalAmount);
            invoiceApplyLines1.setTaxAmount(taxAmount);
            invoiceApplyLines1.setExcludeTaxAmount(excludeAmount);
        }
        if (doUpdateHeader){
            InvoiceApplyHeaderDTO invoiceApplyHeaderDTO =  invoiceApplyHeaderRepository.selectByPrimary(headerId);
            invoiceApplyHeaderDTO.setTotalAmount(total);
            invoiceApplyHeaderDTO.setExcludeTaxAmount(exclude);
            invoiceApplyHeaderDTO.setTaxAmount(tax_amount);
            invoiceApplyHeaderRepository.updateByPrimaryKeySelective(invoiceApplyHeaderDTO);
        }

        //do update head
    }

    @Override
    public List<InvoiceApplyLineDTO> exportData(InvoiceApplyLineDTO invoiceApplyLineDTO) {
        return invoiceApplyLineRepository.selectListDto(invoiceApplyLineDTO);
    }

    //validate
    private void validate(List<InvoiceApplyLine> invoiceApplyLines) {
        for (InvoiceApplyLine invoiceApplyLines1 : invoiceApplyLines) {
            InvoiceApplyHeaderDTO invoiceApplyHeaderDTO =  invoiceApplyHeaderRepository.selectByPrimary(invoiceApplyLines1.getApplyHeaderId());
            if (invoiceApplyHeaderDTO == null || invoiceApplyHeaderDTO.getDelFlag().equals(1)) {
                throw new CommonException(Constants.ERROR_CODE_HEADER_INVALID);
            }
        }
    }
}

