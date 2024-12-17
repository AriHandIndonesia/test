package com.hand.demo.domain.repository;

import com.hand.demo.api.dto.ExcelReportReqDTO;
import com.hand.demo.api.dto.InvoiceApplyHeaderDTO;
import org.hzero.mybatis.base.BaseRepository;
import com.hand.demo.domain.entity.InvoiceApplyHeader;

import java.util.List;

/**
 * (InvoiceApplyHeader)资源库
 *
 * @author Zamzam
 * @since 2024-12-03 11:01:36
 */
public interface InvoiceApplyHeaderRepository extends BaseRepository<InvoiceApplyHeader> {
    /**
     * 查询
     *
     * @param invoiceApplyHeader 查询条件
     * @return 返回值
     */
    List<InvoiceApplyHeaderDTO> selectList(InvoiceApplyHeaderDTO invoiceApplyHeader);

    /**
     * 根据主键查询（可关联表）
     *
     * @param applyHeaderId 主键
     * @return 返回值
     */
    InvoiceApplyHeaderDTO selectByPrimary(Long applyHeaderId);

    List<InvoiceApplyHeaderDTO> selectForReport(Long organizationId, ExcelReportReqDTO excelReportReqDTO);
}
