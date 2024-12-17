package com.hand.demo.app.service;

import com.hand.demo.api.dto.ExcelReportReqDTO;
import com.hand.demo.api.dto.InvoiceApplyHeaderDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.core.base.AopProxy;

import java.util.List;

/**
 * (InvoiceApplyHeader)应用服务
 *
 * @author Zamzam
 * @since 2024-12-03 11:01:36
 */
public interface InvoiceApplyHeaderService extends AopProxy<InvoiceApplyHeaderService>{

    /**
     * 查询数据
     *
     * @param pageRequest         分页参数
     * @param invoiceApplyHeaders 查询条件
     * @return 返回值
     */
    Page<InvoiceApplyHeaderDTO> selectList(PageRequest pageRequest, InvoiceApplyHeaderDTO invoiceApplyHeaders);

    /**
     * 保存数据
     *
     * @param organizationId
     * @param invoiceApplyHeaders 数据
     */
    void saveData(Long organizationId, List<InvoiceApplyHeaderDTO> invoiceApplyHeaders);

    List<InvoiceApplyHeaderDTO> exportData(InvoiceApplyHeaderDTO invoiceApplyHeaderDTO);

    InvoiceApplyHeaderDTO selectDetail(Long applyHeaderId);

    void remove(Long organizationId, List<InvoiceApplyHeaderDTO> invoiceApplyHeaderDTO);
    void redisUpdate(List<InvoiceApplyHeaderDTO> invoiceApplyHeaders);

    ExcelReportReqDTO selectExcelReport(Long organizationId, ExcelReportReqDTO excelReportReqDTO);
    void insertLine(List<InvoiceApplyHeaderDTO> invoiceApplyHeaders);

}

