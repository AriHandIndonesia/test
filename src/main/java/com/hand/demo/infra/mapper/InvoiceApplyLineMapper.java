package com.hand.demo.infra.mapper;

import com.hand.demo.api.dto.InvoiceApplyLineDTO;
import io.choerodon.mybatis.common.BaseMapper;
import com.hand.demo.domain.entity.InvoiceApplyLine;

import java.util.List;

/**
 * (InvoiceApplyLine)应用服务
 *
 * @author Zamzam
 * @since 2024-12-03 11:01:36
 */
public interface InvoiceApplyLineMapper extends BaseMapper<InvoiceApplyLine> {
    /**
     * 基础查询
     *
     * @param invoiceApplyLine 查询条件
     * @return 返回值
     */
    List<InvoiceApplyLine> selectList(InvoiceApplyLine invoiceApplyLine);
    List<InvoiceApplyLineDTO> selectListDto(InvoiceApplyLine invoiceApplyLine);
}

