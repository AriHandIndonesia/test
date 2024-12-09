package com.hand.demo.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import com.hand.demo.domain.entity.InvoiceInfoQueue;

import java.util.List;

/**
 * Redis Message Queue Table(InvoiceInfoQueue)应用服务
 *
 * @author Zamzam
 * @since 2024-12-05 09:24:29
 */
public interface InvoiceInfoQueueMapper extends BaseMapper<InvoiceInfoQueue> {
    /**
     * 基础查询
     *
     * @param invoiceInfoQueue 查询条件
     * @return 返回值
     */
    List<InvoiceInfoQueue> selectList(InvoiceInfoQueue invoiceInfoQueue);
}

