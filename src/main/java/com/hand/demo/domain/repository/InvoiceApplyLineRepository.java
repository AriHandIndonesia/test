package com.hand.demo.domain.repository;

import com.hand.demo.api.dto.InvoiceApplyLineDTO;
import org.hzero.mybatis.base.BaseRepository;
import com.hand.demo.domain.entity.InvoiceApplyLine;

import java.util.List;

/**
 * (InvoiceApplyLine)资源库
 *
 * @author Zamzam
 * @since 2024-12-03 11:01:37
 */
public interface InvoiceApplyLineRepository extends BaseRepository<InvoiceApplyLine> {
    /**
     * 查询
     *
     * @param invoiceApplyLine 查询条件
     * @return 返回值
     */
    List<InvoiceApplyLine> selectList(InvoiceApplyLine invoiceApplyLine);

    /**
     * 根据主键查询（可关联表）
     *
     * @param applyLineId 主键
     * @return 返回值
     */
    InvoiceApplyLine selectByPrimary(Long applyLineId);

    List<InvoiceApplyLineDTO> selectListDto(InvoiceApplyLine invoiceApplyLine);
}
