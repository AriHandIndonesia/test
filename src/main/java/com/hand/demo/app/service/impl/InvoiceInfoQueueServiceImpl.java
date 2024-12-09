package com.hand.demo.app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.demo.api.dto.InvoiceApplyHeaderDTO;
import com.hand.demo.app.listener.MessageListener;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.domain.repository.InvoiceApplyHeaderRepository;
import com.hand.demo.infra.constant.Constants;
import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.redis.RedisQueueHelper;
import org.springframework.beans.factory.annotation.Autowired;
import com.hand.demo.app.service.InvoiceInfoQueueService;
import org.springframework.stereotype.Service;
import com.hand.demo.domain.entity.InvoiceInfoQueue;
import com.hand.demo.domain.repository.InvoiceInfoQueueRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis Message Queue Table(InvoiceInfoQueue)应用服务
 *
 * @author Zamzam
 * @since 2024-12-05 09:24:29
 */
@Service
public class InvoiceInfoQueueServiceImpl implements InvoiceInfoQueueService {
    @Autowired
    private InvoiceInfoQueueRepository invoiceInfoQueueRepository;

    @Autowired
    private InvoiceApplyHeaderRepository invoiceApplyHeaderRepository;

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private RedisQueueHelper redisQueueHelper;

    @Autowired
    private MessageListener messageListener;

    @Override
    public Page<InvoiceInfoQueue> selectList(PageRequest pageRequest, InvoiceInfoQueue invoiceInfoQueue) {
        return PageHelper.doPageAndSort(pageRequest, () -> invoiceInfoQueueRepository.selectList(invoiceInfoQueue));
    }

    @Override
    public void saveData(List<InvoiceInfoQueue> invoiceInfoQueues) {
        List<InvoiceInfoQueue> insertList = invoiceInfoQueues.stream().filter(line -> line.getId() == null).collect(Collectors.toList());
        List<InvoiceInfoQueue> updateList = invoiceInfoQueues.stream().filter(line -> line.getId() != null).collect(Collectors.toList());
        invoiceInfoQueueRepository.batchInsertSelective(insertList);
        invoiceInfoQueueRepository.batchUpdateByPrimaryKeySelective(updateList);
    }

    @Override
    public void scheduleListener(Map<String, String> map) {
        //prepare data
        Long organizationId = DetailsHelper.getUserDetails().getOrganizationId();
        String employeeNumber = "47835";
        InvoiceApplyHeaderDTO invoiceApplyHeader = new InvoiceApplyHeaderDTO();

        invoiceApplyHeader.setTenantId(organizationId);
        invoiceApplyHeader.setApplyStatus(map.get("applyStatus"));
        invoiceApplyHeader.setInvoiceType(map.get("invoiceType"));
        invoiceApplyHeader.setInvoiceColor(map.get("invoiceColor"));
        invoiceApplyHeader.setDelFlag(Integer.parseInt(map.get("delFlag")));

        //find data
        List<InvoiceApplyHeaderDTO> invoiceApplyHeaderList = invoiceApplyHeaderRepository.selectList(invoiceApplyHeader);

        if (invoiceApplyHeaderList != null && !invoiceApplyHeaderList.isEmpty()){
            //change to string
            //do send message queue
            ObjectMapper objectMapper = new ObjectMapper();
            for (int i = 0; i < invoiceApplyHeaderList.size(); i++) {
                try {
                    Map<String,String> invoiceMessage = new HashMap<>();
                    String invoiceHeaderString = objectMapper.writeValueAsString(invoiceApplyHeaderList.get(i));
                    String userDetail = objectMapper.writeValueAsString(DetailsHelper.getUserDetails());
                    invoiceMessage.put("invoice", invoiceHeaderString);
                    invoiceMessage.put("userDetail", userDetail);
                    invoiceMessage.put("employeeId", "47835");
//                    redisQueueHelper.push(Constants.REDIS_QUEUE,invoiceHeaderString);
                    redisQueueHelper.push(Constants.REDIS_QUEUE,objectMapper.writeValueAsString(invoiceMessage));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }


    }
}

