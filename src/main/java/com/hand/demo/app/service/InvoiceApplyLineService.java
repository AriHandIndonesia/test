package com.hand.demo.app.service;

import com.hand.demo.api.dto.InvoiceApplyHeaderDTO;
import com.hand.demo.api.dto.InvoiceApplyLineDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import com.hand.demo.domain.entity.InvoiceApplyLine;
import org.hzero.core.base.AopProxy;


import java.util.List;

/**
 * (InvoiceApplyLine)应用服务
 *
 * @author Zamzam
 * @since 2024-12-03 11:01:37
 */
public interface InvoiceApplyLineService extends AopProxy<InvoiceApplyLineService> {

    /**
     * 查询数据
     *
     * @param pageRequest       分页参数
     * @param invoiceApplyLines 查询条件
     * @return 返回值
     */
    Page<InvoiceApplyLine> selectList(PageRequest pageRequest, InvoiceApplyLineDTO invoiceApplyLines);

    /**
     * 保存数据
     *
     * @param invoiceApplyLines 数据
     */
    void saveData(List<InvoiceApplyLineDTO> invoiceApplyLines);
    void calculate(List<InvoiceApplyLineDTO> invoiceApplyLines, Boolean doUpdateHeader);
    List<InvoiceApplyLineDTO> exportData(InvoiceApplyLineDTO invoiceApplyHeaderDTO);
    InvoiceApplyLineDTO selectDetail(Long applyLineId);
    void redisUpdate(List<InvoiceApplyLineDTO> invoiceApplyLines);
}

