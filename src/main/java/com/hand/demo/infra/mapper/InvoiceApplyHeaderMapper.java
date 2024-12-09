package com.hand.demo.infra.mapper;

import com.hand.demo.api.dto.InvoiceApplyHeaderDTO;
import io.choerodon.mybatis.common.BaseMapper;
import com.hand.demo.domain.entity.InvoiceApplyHeader;

import java.util.List;

/**
 * (InvoiceApplyHeader)应用服务
 *
 * @author Zamzam
 * @since 2024-12-03 11:01:36
 */
public interface InvoiceApplyHeaderMapper extends BaseMapper<InvoiceApplyHeader> {
    /**
     * 基础查询
     *
     * @param invoiceApplyHeader 查询条件
     * @return 返回值
     */
    List<InvoiceApplyHeaderDTO> selectList(InvoiceApplyHeader invoiceApplyHeader);
}

