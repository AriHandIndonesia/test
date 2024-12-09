package com.hand.demo.app.service.impl;

import com.hand.demo.api.dto.InvoiceApplyHeaderDTO;
import com.hand.demo.api.dto.InvoiceApplyLineDTO;
import com.hand.demo.domain.entity.InvoiceApplyLine;
import com.hand.demo.domain.repository.InvoiceApplyLineRepository;
import com.hand.demo.infra.constant.Constants;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.boot.platform.code.builder.CodeRuleBuilder;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.springframework.beans.factory.annotation.Autowired;
import com.hand.demo.app.service.InvoiceApplyHeaderService;
import org.springframework.stereotype.Service;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.domain.repository.InvoiceApplyHeaderRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * (InvoiceApplyHeader)应用服务
 *
 * @author Zamzam
 * @since 2024-12-03 11:01:36
 */
@Service
public class InvoiceApplyHeaderServiceImpl implements InvoiceApplyHeaderService {
    @Autowired
    private InvoiceApplyHeaderRepository invoiceApplyHeaderRepository;

    @Autowired
    private LovAdapter lovAdapter;

    @Autowired
    private CodeRuleBuilder codeRuleBuilder;

    @Autowired
    private InvoiceApplyLineRepository invoiceApplyLineRepository;

    @Override
    public Page<InvoiceApplyHeaderDTO> selectList(PageRequest pageRequest, InvoiceApplyHeader invoiceApplyHeader) {
        return PageHelper.doPageAndSort(pageRequest, () -> invoiceApplyHeaderRepository.selectList(invoiceApplyHeader));
    }

    @Override
    public void saveData(Long organizationId, List<InvoiceApplyHeader> invoiceApplyHeaders) {
        //V 1.1 [S]
        validate(invoiceApplyHeaders);
        generateHeaderNumber(invoiceApplyHeaders);
        calculate(invoiceApplyHeaders);
        //V 1.1 [E]
        List<InvoiceApplyHeader> insertList = invoiceApplyHeaders.stream().filter(line -> line.getApplyHeaderId() == null).collect(Collectors.toList());
        List<InvoiceApplyHeader> updateList = invoiceApplyHeaders.stream().filter(line -> line.getApplyHeaderId() != null).collect(Collectors.toList());
        invoiceApplyHeaderRepository.batchInsertSelective(insertList);
        invoiceApplyHeaderRepository.batchUpdateByPrimaryKeySelective(updateList);
    }

    @Override
    public List<InvoiceApplyHeaderDTO> exportData(InvoiceApplyHeaderDTO invoiceApplyHeaderDTO) {
        List<InvoiceApplyHeaderDTO> invoiceApplyHeaderDTOList = invoiceApplyHeaderRepository.selectList(invoiceApplyHeaderDTO);
        List<Long> invoiceApplyHeaderIdList = new ArrayList<>();
        invoiceApplyHeaderDTOList.forEach(invoiceHeaderId -> {
            // Add ApplyHeaderId to the list
            invoiceApplyHeaderIdList.add(invoiceHeaderId.getApplyHeaderId());

            // Set ApplyStatusMeaning using lovAdapter
            invoiceHeaderId.setApplyStatusMeaning(lovAdapter.queryLovMeaning(Constants.LOV_CODE_STATUS, invoiceHeaderId.getTenantId(), invoiceHeaderId.getApplyStatus()));

            // Set InvoiceColor using lovAdapter
            invoiceHeaderId.setInvoiceColorMeaning(lovAdapter.queryLovMeaning(Constants.LOV_CODE_INVOICE_COLOR, invoiceHeaderId.getTenantId(), invoiceHeaderId.getInvoiceColor()));

            // Set InvoiceType using lovAdapter
            invoiceHeaderId.setInvoiceTypeMeaning(lovAdapter.queryLovMeaning(Constants.LOV_CODE_INVOICE_TYPE, invoiceHeaderId.getTenantId(), invoiceHeaderId.getInvoiceType()));
        });
        Map<Long, List<InvoiceApplyLineDTO>> lineMap = invoiceApplyLineRepository.selectListDto(new InvoiceApplyLine().setApplyHeaderIdList(invoiceApplyHeaderIdList))
                .stream()
                .collect(Collectors.groupingBy(InvoiceApplyLineDTO::getApplyHeaderId));

        invoiceApplyHeaderDTOList.forEach(applyHeader -> applyHeader.setInvoiceApplyLineListDto(lineMap.get(applyHeader.getApplyHeaderId())));
        return invoiceApplyHeaderDTOList;
    }


    //validation
    private List<InvoiceApplyHeader> validate(List<InvoiceApplyHeader> invoiceApplyHeaderList) {
        // Iterate over the invoiceApplyHeaderList
        for (InvoiceApplyHeader invoiceApplyHeader1 : invoiceApplyHeaderList) {
            // Retrieve the necessary maps
            Map<String, String> statusMap = getLovMap(Constants.LOV_CODE_STATUS, invoiceApplyHeader1.getTenantId());
            Map<String, String> invoiceColorMap = getLovMap(Constants.LOV_CODE_INVOICE_COLOR, invoiceApplyHeader1.getTenantId());
            Map<String, String> invoiceTypeMap = getLovMap(Constants.LOV_CODE_INVOICE_TYPE, invoiceApplyHeader1.getTenantId());

            // Retrieve meaning values based on the current invoiceApplyHeader's properties
            String statusMeaning = statusMap.get(invoiceApplyHeader1.getApplyStatus());
            String invoiceColorMeaning = invoiceColorMap.get(invoiceApplyHeader1.getInvoiceColor());
            String invoiceTypeMeaning = invoiceTypeMap.get(invoiceApplyHeader1.getInvoiceType());

            // Check and throw exceptions if needed
            throwIfNull(statusMeaning, Constants.LOV_CODE_STATUS, Constants.ERROR_CODE_STATUS, invoiceApplyHeader1.getTenantId());
            throwIfNull(invoiceColorMeaning, Constants.LOV_CODE_INVOICE_COLOR, Constants.ERROR_CODE_INVOICE_COLOR, invoiceApplyHeader1.getTenantId());
            throwIfNull(invoiceTypeMeaning, Constants.LOV_CODE_INVOICE_TYPE, Constants.ERROR_CODE_INVOICE_TYPE, invoiceApplyHeader1.getTenantId());
        }

        return invoiceApplyHeaderList;
    }

    //generate header number
    private List<InvoiceApplyHeader> generateHeaderNumber(List<InvoiceApplyHeader> invoiceApplyHeaderList) {
        Map<String, String> variableMap = new HashMap<>();
        for (InvoiceApplyHeader invoiceApplyHeader1 : invoiceApplyHeaderList) {
            if (invoiceApplyHeader1.getApplyHeaderId() == null) {
                invoiceApplyHeader1.setApplyHeaderNumber(codeRuleBuilder.generateCode(Constants.RULE_CODE_HEADER_NUMBER, variableMap));
            }
        }
        return invoiceApplyHeaderList;
    }
    //calculate
    private List<InvoiceApplyHeader> calculate(List<InvoiceApplyHeader> invoiceApplyHeaderList) {
        for (InvoiceApplyHeader invoiceApplyHeader1 : invoiceApplyHeaderList) {
            BigDecimal total = BigDecimal.ZERO;
            BigDecimal exclude = BigDecimal.ZERO;
            BigDecimal tax_amount = BigDecimal.ZERO;
            if (invoiceApplyHeader1.getApplyHeaderId() != null) {
                InvoiceApplyLine invoiceApplyLine = new InvoiceApplyLine();
                invoiceApplyLine.setApplyHeaderId(invoiceApplyHeader1.getApplyHeaderId());
                List<InvoiceApplyLine> invoiceApplyLines = invoiceApplyLineRepository.selectList(invoiceApplyLine);
                for (InvoiceApplyLine invoiceApplyLines1 : invoiceApplyLines) {
                    BigDecimal totalAmount = invoiceApplyLines1.getQuantity().multiply(invoiceApplyLines1.getUnitPrice());
                    total = total.add(totalAmount);
                    tax_amount = tax_amount.add(totalAmount.multiply(invoiceApplyLines1.getTaxRate()));
                    exclude = exclude.add(totalAmount.subtract(tax_amount));
                }
            }
            invoiceApplyHeader1.setTotalAmount(total);
            invoiceApplyHeader1.setTaxAmount(tax_amount);
            invoiceApplyHeader1.setExcludeTaxAmount(exclude);

        }

        return invoiceApplyHeaderList;
    }

    //helper
    Map<String, String> getLovMap(String lovCode, Long organizationId) {
        return lovAdapter.queryLovValue(lovCode, organizationId).stream()
                .collect(Collectors.toMap(LovValueDTO::getValue, LovValueDTO::getMeaning));
    }
    void throwIfNull(String meaning, String lovCode, String errorCode, Long organizationId) {
        if (meaning == null) {
            List<LovValueDTO> value = lovAdapter.queryLovValue(lovCode, organizationId);
            String val = value.stream()
                    .map(lovValueDTO -> lovValueDTO.getValue() + "-" + lovValueDTO.getMeaning())
                    .collect(Collectors.joining(","));
            throw new CommonException(errorCode, val);
        }
    }
}

