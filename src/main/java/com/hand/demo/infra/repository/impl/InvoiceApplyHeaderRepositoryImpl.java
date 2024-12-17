package com.hand.demo.infra.repository.impl;

import com.hand.demo.api.dto.ExcelReportReqDTO;
import com.hand.demo.api.dto.InvoiceApplyHeaderDTO;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.core.cache.ProcessCacheValue;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;

import org.hzero.mybatis.domian.Condition;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.domain.repository.InvoiceApplyHeaderRepository;
import com.hand.demo.infra.mapper.InvoiceApplyHeaderMapper;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * (InvoiceApplyHeader)资源库
 *
 * @author Zamzam
 * @since 2024-12-03 11:01:36
 */
@Component
public class InvoiceApplyHeaderRepositoryImpl extends BaseRepositoryImpl<InvoiceApplyHeader> implements InvoiceApplyHeaderRepository {
    @Resource
    private InvoiceApplyHeaderMapper invoiceApplyHeaderMapper;

    @Autowired
    private LovAdapter lovAdapter;


    @Override
    public List<InvoiceApplyHeaderDTO> selectList(InvoiceApplyHeaderDTO invoiceApplyHeader) {
        //V 1.1 [S]
//        return invoiceApplyHeaderMapper.selectList(invoiceApplyHeader);
        invoiceApplyHeader.setDelFlag(invoiceApplyHeader.getDelFlag() == null ? 0 : invoiceApplyHeader.getDelFlag());
        List<InvoiceApplyHeaderDTO> invoiceApplyHeaderList = invoiceApplyHeaderMapper.selectList(invoiceApplyHeader);
        return invoiceApplyHeaderList;
        //V 1.1 [E]
    }

    @Override
    @ProcessCacheValue
    public InvoiceApplyHeaderDTO selectByPrimary(Long applyHeaderId) {
        InvoiceApplyHeaderDTO invoiceApplyHeader = new InvoiceApplyHeaderDTO();
        invoiceApplyHeader.setApplyHeaderId(applyHeaderId);
        List<InvoiceApplyHeaderDTO> invoiceApplyHeaders = invoiceApplyHeaderMapper.selectList(invoiceApplyHeader);
        if (invoiceApplyHeaders.size() == 0) {
            return null;
        }
        invoiceApplyHeader = invoiceApplyHeaders.get(0);
        return invoiceApplyHeader;
    }

    @Override
    public List<InvoiceApplyHeaderDTO> selectForReport(Long organizationId, ExcelReportReqDTO excelReportReqDTO) {
        Condition condition = new Condition(InvoiceApplyHeader.class);

        //tenant id condition
        condition.createCriteria().andEqualTo(InvoiceApplyHeader.FIELD_TENANT_ID, organizationId != null ? organizationId : 0L);

        //apply header id condition
        if (excelReportReqDTO.getApplyHeaderNumberStart() != null && excelReportReqDTO.getApplyHeaderNumberEnd() != null) {
            condition.and().andBetween(InvoiceApplyHeader.FIELD_APPLY_HEADER_NUMBER,
                    excelReportReqDTO.getApplyHeaderNumberStart(),
                    excelReportReqDTO.getApplyHeaderNumberEnd());
        }

        //created date condition
        if (excelReportReqDTO.getDateStartFrom() != null && excelReportReqDTO.getDateEndTo() != null) {
            condition.and().andBetween(InvoiceApplyHeader.FIELD_CREATION_DATE,
                    excelReportReqDTO.getDateStartFrom(),
                    excelReportReqDTO.getDateEndTo());
        }

        //submit time condition
        if (excelReportReqDTO.getSubmitStartFrom() != null && excelReportReqDTO.getSubmitEndTo() != null) {
            condition.and().andBetween(InvoiceApplyHeader.FIELD_SUBMIT_TIME,
                    excelReportReqDTO.getSubmitStartFrom(),
                    excelReportReqDTO.getSubmitEndTo());
        }

        //invoice type condition
        if (excelReportReqDTO.getInvoiceType() != null) {
            condition.and().andEqualTo(InvoiceApplyHeader.FIELD_INVOICE_TYPE,
                    excelReportReqDTO.getInvoiceType());
        }

        //apply status condition
        if (excelReportReqDTO.getApplyStatusList() != null && !excelReportReqDTO.getApplyStatusList().isEmpty()) {
            condition.and().andIn(InvoiceApplyHeader.FIELD_APPLY_STATUS,
                    excelReportReqDTO.getApplyStatusList());
        }

        //execute select
        List<InvoiceApplyHeader> invoiceApplyHeaderList = selectByCondition(condition);
        //convert to DTO
        List<InvoiceApplyHeaderDTO> invoiceApplyHeaderDTOList = invoiceApplyHeaderList.stream()
                .map(header -> {
                    InvoiceApplyHeaderDTO dto = new InvoiceApplyHeaderDTO();
                    BeanUtils.copyProperties(header, dto);
                    return dto;
                })
                .collect(Collectors.toList());
//        List<InvoiceApplyHeaderDTO> invoiceApplyHeaderDTOList = new ArrayList<>();
//        for (int i = 0; i < invoiceApplyHeaderList.size(); i++) {
//            InvoiceApplyHeaderDTO invoiceApplyHeaderDTO = new InvoiceApplyHeaderDTO();
//            BeanUtils.copyProperties(invoiceApplyHeaderList.get(i), invoiceApplyHeaderDTO);
//            invoiceApplyHeaderDTOList.add(invoiceApplyHeaderDTO);
//        }

        return invoiceApplyHeaderDTOList;
    }
}

