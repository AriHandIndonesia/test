package com.hand.demo.infra.repository.impl;

import com.hand.demo.api.dto.InvoiceApplyHeaderDTO;
import com.hand.demo.domain.entity.InvoiceApplyLine;
import com.hand.demo.infra.constant.Constants;
import com.hand.demo.infra.mapper.InvoiceApplyLineMapper;
import org.apache.commons.collections.CollectionUtils;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.cache.ProcessCacheValue;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.domain.repository.InvoiceApplyHeaderRepository;
import com.hand.demo.infra.mapper.InvoiceApplyHeaderMapper;

import javax.annotation.Resource;
import java.util.ArrayList;
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
//        invoiceApplyHeader.setDelFlag(invoiceApplyHeader.getDelFlag() == null ? 0 : invoiceApplyHeader.getDelFlag()); //not neeeded
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
}

