package com.hand.demo.app.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.demo.api.dto.InvoiceApplyHeaderDTO;
import com.hand.demo.app.service.InvoiceInfoQueueService;
import com.hand.demo.domain.entity.InvoiceInfoQueue;
import com.hand.demo.domain.repository.InvoiceInfoQueueRepository;
import com.hand.demo.infra.constant.Constants;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.entity.Message;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.redis.RedisQueueHelper;
import org.hzero.core.redis.handler.IBatchQueueHandler;
import org.hzero.core.redis.handler.QueueHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

@QueueHandler(Constants.REDIS_QUEUE)
public class MessageListener implements IBatchQueueHandler {

    @Autowired
    MessageClient messageClient;

    @Autowired
    InvoiceInfoQueueService invoiceInfoQueueService;

    @Autowired
    InvoiceInfoQueueRepository invoiceInfoQueueRepository;

    @Override
    public void process(List<String> messages) {
        //base configuration
        Map<String, String> param = new HashMap<>();
        List<String> invoiceMessageMap = new ArrayList<>();
        Receiver receiver = new Receiver();
        Long organizationId = 0L;

        for (String message : messages) {
            //convert message to object
            ObjectMapper objectMapper = new ObjectMapper();
            if (!message.isEmpty()) {
                try {
                    //convert json to map
                    Map<String,String> invoiceMessage = objectMapper.readValue(message, Map.class);
                    //convert map userDetail to CustomUserDetails
                    CustomUserDetails customUserDetails = objectMapper.readValue(invoiceMessage.get("userDetail"), CustomUserDetails.class);
                    //set CustomUserDetails
                    DetailsHelper.setCustomUserDetails(customUserDetails);
                    //convert and set employeeNumber from Map
                    String employeeNumber = objectMapper.readValue(invoiceMessage.get("employeeId"), String.class);
                    //convert and set InvoiceApplyHeaderDTO from Map
                    InvoiceApplyHeaderDTO invoiceApplyHeader = objectMapper.readValue(invoiceMessage.get("invoice"), InvoiceApplyHeaderDTO.class);

                    invoiceMessageMap.add(invoiceApplyHeader.getApplyHeaderNumber());

                    organizationId = DetailsHelper.getUserDetails().getOrganizationId();

                    //add message to db
                    InvoiceInfoQueue invoiceInfoQueue = new InvoiceInfoQueue();
                    invoiceInfoQueue.setContent(message);
                    invoiceInfoQueue.setTenantId(invoiceApplyHeader.getTenantId());
                    invoiceInfoQueue.setEmployeeId(employeeNumber);

                    invoiceInfoQueueRepository.insertSelective(invoiceInfoQueue);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        String invoiceMessage = invoiceMessageMap.stream()
                .collect(Collectors.joining(","));
        param.put("invoice", invoiceMessage);
        param.put("sender_email", Constants.EMAIL_SENDER);
        //send message
        receiver.setUserId(DetailsHelper.getUserDetails().getUserId());
        receiver.setTargetUserTenantId(organizationId);
        messageClient.sendWebMessage(
                DetailsHelper.getUserDetails().getOrganizationId(),
                Constants.TEMPLATE_CODE_MESSAGE,
                DetailsHelper.getUserDetails().getLanguage(),
                Collections.singletonList(receiver),
                param
        );
    }
}
