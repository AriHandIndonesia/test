package com.hand.demo.app.service;

import com.hand.demo.api.dto.InvoiceApplyHeaderDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import com.hand.demo.domain.entity.InvoiceApplyHeader;

import java.util.List;

/**
 * (InvoiceApplyHeader)应用服务
 *
 * @author Zamzam
 * @since 2024-12-03 11:01:36
 */
public interface InvoiceApplyHeaderService {

    /**
     * 查询数据
     *
     * @param pageRequest         分页参数
     * @param invoiceApplyHeaders 查询条件
     * @return 返回值
     */
    Page<InvoiceApplyHeaderDTO> selectList(PageRequest pageRequest, InvoiceApplyHeader invoiceApplyHeaders);

    /**
     * 保存数据
     *
     * @param organizationId
     * @param invoiceApplyHeaders 数据
     */
    void saveData(Long organizationId, List<InvoiceApplyHeader> invoiceApplyHeaders);

    List<InvoiceApplyHeaderDTO> exportData(InvoiceApplyHeaderDTO invoiceApplyHeaderDTO);

}
