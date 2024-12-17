package com.hand.demo.app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.demo.api.dto.ExcelReportReqDTO;
import com.hand.demo.api.dto.InvoiceApplyHeaderDTO;
import com.hand.demo.api.dto.InvoiceApplyLineDTO;
import com.hand.demo.app.service.InvoiceApplyLineService;
import com.hand.demo.domain.entity.InvoiceApplyLine;
import com.hand.demo.domain.repository.InvoiceApplyLineRepository;
import com.hand.demo.infra.constant.Constants;
import com.hand.demo.infra.mapper.InvoiceApplyLineMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.util.StringUtil;
import org.hzero.boot.apaas.common.userinfo.domain.UserVO;
import org.hzero.boot.apaas.common.userinfo.infra.feign.IamRemoteService;
import org.hzero.boot.platform.code.builder.CodeRuleBuilder;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.domian.Condition;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.hand.demo.app.service.InvoiceApplyHeaderService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.domain.repository.InvoiceApplyHeaderRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * (InvoiceApplyHeader)应用服务
 *
 * @author Zamzam
 * @since 2024-12-03 11:01:36
 */
@Service
public class InvoiceApplyHeaderServiceImpl implements InvoiceApplyHeaderService {
    @Autowired
    private InvoiceApplyHeaderRepository invoiceApplyHeaderRepository;

    @Autowired
    private LovAdapter lovAdapter;

    @Autowired
    private CodeRuleBuilder codeRuleBuilder;

    @Autowired
    private InvoiceApplyLineRepository invoiceApplyLineRepository;

    @Autowired
    private InvoiceApplyLineService invoiceApplyLineService;

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private InvoiceApplyLineMapper invoiceApplyLineMapper;

    @Autowired
    private IamRemoteService iamRemoteService;
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public Page<InvoiceApplyHeaderDTO> selectList(PageRequest pageRequest, InvoiceApplyHeaderDTO invoiceApplyHeader) {
        ResponseEntity<String> stringResponseEntity = iamRemoteService.selectSelf();
//        ObjectMapper objectMapper = new ObjectMapper();
        try {
            UserVO userVO = objectMapper.readValue(stringResponseEntity.getBody(), UserVO.class);
            if (userVO.getTenantAdminFlag() == null){
                invoiceApplyHeader.setTenantAdminFlag(false);
            }else {
                invoiceApplyHeader.setTenantAdminFlag(true);
            }
//            if (userVO.getTenantAdminFlag() == null) {
//
//                //not admin
//                if (userVO.getId() == null) {
//                    return PageHelper.doPageAndSort(pageRequest, () -> new ArrayList<>());
//                } else {
//                    invoiceApplyHeader.setCreatedBy(userVO.getId());
//                    return PageHelper.doPageAndSort(pageRequest, () -> invoiceApplyHeaderRepository.selectList(invoiceApplyHeader));
//                }
//            } else {
//                //admin
//                return PageHelper.doPageAndSort(pageRequest, () -> invoiceApplyHeaderRepository.selectList(invoiceApplyHeader));
//            }
            return PageHelper.doPageAndSort(pageRequest, () -> invoiceApplyHeaderRepository.selectList(invoiceApplyHeader));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        return PageHelper.doPageAndSort(pageRequest, () -> invoiceApplyHeaderRepository.selectList(invoiceApplyHeader));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void saveData(Long organizationId, List<InvoiceApplyHeaderDTO> invoiceApplyHeaders) {
        //V 1.1 [S]
        validate(invoiceApplyHeaders);
        generateHeaderNumber(invoiceApplyHeaders);
        calculate(invoiceApplyHeaders);
        redisUpdate(invoiceApplyHeaders);
        //V 1.1 [E]
        List<InvoiceApplyHeader> insertList = invoiceApplyHeaders.stream().filter(line -> line.getApplyHeaderId() == null).collect(Collectors.toList());
        List<InvoiceApplyHeader> updateList = invoiceApplyHeaders.stream().filter(line -> line.getApplyHeaderId() != null).collect(Collectors.toList());
        invoiceApplyHeaderRepository.batchInsertSelective(insertList);
        invoiceApplyHeaderRepository.batchUpdateByPrimaryKeySelective(updateList);
        //do insert line
//        insertLine(invoiceApplyHeaders);
        self().insertLine(invoiceApplyHeaders);
//        int i = 1 / 0;
    }

    @Override
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public List<InvoiceApplyHeaderDTO> exportData(InvoiceApplyHeaderDTO invoiceApplyHeaderDTO) {
        List<InvoiceApplyHeaderDTO> invoiceApplyHeaderDTOList = invoiceApplyHeaderRepository.selectList(invoiceApplyHeaderDTO);
        List<Long> invoiceApplyHeaderIdList = new ArrayList<>();
        // Retrieve the necessary maps
        Map<String, String> statusMap = getLovMap(Constants.LOV_CODE_STATUS, BaseConstants.DEFAULT_TENANT_ID);
        Map<String, String> invoiceColorMap = getLovMap(Constants.LOV_CODE_INVOICE_COLOR, BaseConstants.DEFAULT_TENANT_ID);
        Map<String, String> invoiceTypeMap = getLovMap(Constants.LOV_CODE_INVOICE_TYPE, BaseConstants.DEFAULT_TENANT_ID);
        invoiceApplyHeaderDTOList.forEach(invoiceHeaderId -> {
            // Add ApplyHeaderId to the list
            invoiceApplyHeaderIdList.add(invoiceHeaderId.getApplyHeaderId());

            // Retrieve meaning values based on the current invoiceApplyHeader's properties
            invoiceHeaderId.setApplyStatusMeaning(statusMap.get(invoiceHeaderId.getApplyStatus()));
            invoiceHeaderId.setInvoiceColorMeaning(invoiceColorMap.get(invoiceHeaderId.getInvoiceColor()));
            invoiceHeaderId.setInvoiceTypeMeaning(invoiceTypeMap.get(invoiceHeaderId.getInvoiceType()));
        });
        Map<Long, List<InvoiceApplyLineDTO>> lineMap = invoiceApplyLineRepository.selectListDto(new InvoiceApplyLine().setApplyHeaderIdList(invoiceApplyHeaderIdList))
                .stream()
                .collect(Collectors.groupingBy(InvoiceApplyLineDTO::getApplyHeaderId));

        invoiceApplyHeaderDTOList.forEach(applyHeader -> applyHeader.setInvoiceApplyLineList(lineMap.get(applyHeader.getApplyHeaderId())));
        return invoiceApplyHeaderDTOList;
    }

    @Override
    public InvoiceApplyHeaderDTO selectDetail(Long applyHeaderId) {
        //check redis
        InvoiceApplyHeaderDTO invoiceApplyHeader = new InvoiceApplyHeaderDTO();
//        ObjectMapper objectMapper = new ObjectMapper();
        String redisData = redisHelper.strGet(Constants.REDIS_INVOICE_HEADER_PREFIX + applyHeaderId);
        if (StringUtil.isEmpty(redisData)) {
            invoiceApplyHeader = invoiceApplyHeaderRepository.selectByPrimary(applyHeaderId);
            invoiceApplyHeader.setRealName(DetailsHelper.getUserDetails().getRealName());
            //select list of line
            InvoiceApplyLine invoiceApplyLine = new InvoiceApplyLine().setApplyHeaderId(invoiceApplyHeader.getApplyHeaderId());
            invoiceApplyHeader.setInvoiceApplyLineList(invoiceApplyLineMapper.selectList(invoiceApplyLine));
            //add to redis
            try {
                String invoiceString = objectMapper.writeValueAsString(invoiceApplyHeader);
                redisHelper.strSet(Constants.REDIS_INVOICE_HEADER_PREFIX + applyHeaderId, invoiceString, 300, TimeUnit.SECONDS);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                invoiceApplyHeader = objectMapper.readValue(redisData, InvoiceApplyHeaderDTO.class);
                invoiceApplyHeader.setRealName(DetailsHelper.getUserDetails().getRealName());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return invoiceApplyHeader;
    }

    @Override
    public void remove(Long organizationId, List<InvoiceApplyHeaderDTO> invoiceApplyHeaderDTO) {
        List<InvoiceApplyHeader> invoiceApplyHeaderList = new ArrayList<>();
        BeanUtils.copyProperties(invoiceApplyHeaderDTO, invoiceApplyHeaderList);
        invoiceApplyHeaderList.forEach(item -> {
            item.setTenantId(organizationId);
            item.setDelFlag(1);
        });
        redisUpdate(invoiceApplyHeaderDTO);
        invoiceApplyHeaderRepository.batchUpdateOptional(new ArrayList<>(invoiceApplyHeaderDTO), InvoiceApplyHeaderDTO.FIELD_DEL_FLAG);
        invoiceApplyHeaderRepository.batchUpdateByPrimaryKeySelective(new ArrayList<>(invoiceApplyHeaderDTO));
    }


    //validation
    private List<InvoiceApplyHeaderDTO> validate(List<InvoiceApplyHeaderDTO> invoiceApplyHeaderList) {
        // Retrieve the necessary maps
        Map<String, String> statusMap = getLovMap(Constants.LOV_CODE_STATUS, BaseConstants.DEFAULT_TENANT_ID);
        Map<String, String> invoiceColorMap = getLovMap(Constants.LOV_CODE_INVOICE_COLOR, BaseConstants.DEFAULT_TENANT_ID);
        Map<String, String> invoiceTypeMap = getLovMap(Constants.LOV_CODE_INVOICE_TYPE, BaseConstants.DEFAULT_TENANT_ID);
        // Iterate over the invoiceApplyHeaderList
        for (InvoiceApplyHeaderDTO invoiceApplyHeader1 : invoiceApplyHeaderList) {
            // Retrieve meaning values based on the current invoiceApplyHeader's properties
            String statusMeaning = statusMap.get(invoiceApplyHeader1.getApplyStatus());
            String invoiceColorMeaning = invoiceColorMap.get(invoiceApplyHeader1.getInvoiceColor());
            String invoiceTypeMeaning = invoiceTypeMap.get(invoiceApplyHeader1.getInvoiceType());

            // Check and throw exceptions if needed
            throwIfNull(statusMeaning, Constants.LOV_CODE_STATUS, Constants.ERROR_CODE_STATUS, invoiceApplyHeader1.getTenantId(), statusMap);
            throwIfNull(invoiceColorMeaning, Constants.LOV_CODE_INVOICE_COLOR, Constants.ERROR_CODE_INVOICE_COLOR, invoiceApplyHeader1.getTenantId(), invoiceColorMap);
            throwIfNull(invoiceTypeMeaning, Constants.LOV_CODE_INVOICE_TYPE, Constants.ERROR_CODE_INVOICE_TYPE, invoiceApplyHeader1.getTenantId(), invoiceTypeMap);
        }

        return invoiceApplyHeaderList;
    }

    //generate header number
    private List<InvoiceApplyHeaderDTO> generateHeaderNumber(List<InvoiceApplyHeaderDTO> invoiceApplyHeaderList) {
        Map<String, String> variableMap = new HashMap<>();
        for (InvoiceApplyHeader invoiceApplyHeader1 : invoiceApplyHeaderList) {
            if (invoiceApplyHeader1.getApplyHeaderId() == null) {
                invoiceApplyHeader1.setApplyHeaderNumber(codeRuleBuilder.generateCode(Constants.RULE_CODE_HEADER_NUMBER, variableMap));
            }
        }
        return invoiceApplyHeaderList;
    }

    //calculate
    private List<InvoiceApplyHeaderDTO> calculate(List<InvoiceApplyHeaderDTO> invoiceApplyHeaderList) {
        for (InvoiceApplyHeaderDTO invoiceApplyHeader1 : invoiceApplyHeaderList) {
            //old [S]
            BigDecimal total = BigDecimal.ZERO;
            BigDecimal exclude = BigDecimal.ZERO;
            BigDecimal tax_amount = BigDecimal.ZERO;
            if (invoiceApplyHeader1.getApplyHeaderId() != null) {
                if (invoiceApplyHeader1.getInvoiceApplyLineList().isEmpty()) {
                    InvoiceApplyLineDTO invoiceApplyLine = new InvoiceApplyLineDTO();
                    invoiceApplyLine.setApplyHeaderId(invoiceApplyHeader1.getApplyHeaderId());
                    List<InvoiceApplyLineDTO> invoiceApplyLines = invoiceApplyLineRepository.selectList(invoiceApplyLine);
                    for (InvoiceApplyLine invoiceApplyLines1 : invoiceApplyLines) {
                        BigDecimal totalAmount = invoiceApplyLines1.getQuantity().multiply(invoiceApplyLines1.getUnitPrice());
                        total = total.add(totalAmount);
                        tax_amount = tax_amount.add(totalAmount.multiply(invoiceApplyLines1.getTaxRate()));
                        exclude = exclude.add(totalAmount.subtract(tax_amount));
                    }
                } else {
                    for (InvoiceApplyLine invoiceApplyLines1 : invoiceApplyHeader1.getInvoiceApplyLineList()) {
                        BigDecimal totalAmount = invoiceApplyLines1.getQuantity().multiply(invoiceApplyLines1.getUnitPrice());
                        total = total.add(totalAmount);
                        tax_amount = tax_amount.add(totalAmount.multiply(invoiceApplyLines1.getTaxRate()));
                        exclude = exclude.add(totalAmount.subtract(tax_amount));
                    }
                }
            }
            invoiceApplyHeader1.setTotalAmount(total);
            invoiceApplyHeader1.setTaxAmount(tax_amount);
            invoiceApplyHeader1.setExcludeTaxAmount(exclude);
            //old [E]
            //new
//            invoiceApplyHeader1.setTotalAmount(BigDecimal.ZERO);
//            invoiceApplyHeader1.setTaxAmount(BigDecimal.ZERO);
//            invoiceApplyHeader1.setExcludeTaxAmount(BigDecimal.ZERO);

        }

        return invoiceApplyHeaderList;
    }

    @Override
//    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void insertLine(List<InvoiceApplyHeaderDTO> invoiceApplyHeaders) {
        for (InvoiceApplyHeaderDTO invoiceApplyHeaderDTO : invoiceApplyHeaders) {
            if (invoiceApplyHeaderDTO.getInvoiceApplyLineList() != null) {
                //set line header id
                for (InvoiceApplyLine invoiceApplyLine : invoiceApplyHeaderDTO.getInvoiceApplyLineList()) {
                    if (invoiceApplyHeaderDTO.getApplyHeaderId() != null) {
                        invoiceApplyLine.setApplyHeaderId(invoiceApplyHeaderDTO.getApplyHeaderId());
                    }
                }
                invoiceApplyLineService.saveData(invoiceApplyHeaderDTO.getInvoiceApplyLineList());
            }
        }
    }

    //helper
    Map<String, String> getLovMap(String lovCode, Long organizationId) {
        //cek redis
        String meaningRedis = redisHelper.strGet(Constants.REDIS_LOV_CODE_PREFIX + lovCode);
//        ObjectMapper objectMapper = new ObjectMapper();
        if (StringUtil.isEmpty(meaningRedis)) {
            //add to redis
            try {
                Map<String, String> meaningMap = lovAdapter.queryLovValue(lovCode, organizationId).stream()
                        .collect(Collectors.toMap(LovValueDTO::getValue, LovValueDTO::getMeaning));
                String meaningStr = objectMapper.writeValueAsString(meaningMap);
                redisHelper.strSet(Constants.REDIS_LOV_CODE_PREFIX + lovCode, meaningStr, 300, TimeUnit.SECONDS);
                return meaningMap;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                return objectMapper.readValue(meaningRedis, Map.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    void throwIfNull(String meaning, String lovCode, String errorCode, Long organizationId, Map<String, String> lovMap) {
        if (meaning == null) {
//            String value = lovMap.
            List<LovValueDTO> value = lovAdapter.queryLovValue(lovCode, organizationId);
            String val = value.stream()
                    .map(lovValueDTO -> lovValueDTO.getValue() + "-" + lovValueDTO.getMeaning())
                    .collect(Collectors.joining(","));
            throw new CommonException(errorCode, val);
        }
    }

    @Override
    public void redisUpdate(List<InvoiceApplyHeaderDTO> invoiceApplyHeaders) {
        for (InvoiceApplyHeader invoiceApplyHeader : invoiceApplyHeaders) {
            if (invoiceApplyHeader.getApplyHeaderId() != null) {
                redisHelper.delKey(Constants.REDIS_INVOICE_HEADER_PREFIX + invoiceApplyHeader.getApplyHeaderId());
                //reset redis value instead of delete
//                try {
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    redisHelper.strSet(Constants.REDIS_INVOICE_HEADER_PREFIX + invoiceApplyHeader.getApplyHeaderId(), objectMapper.writeValueAsString(invoiceApplyHeader), 300, TimeUnit.SECONDS);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
            }
        }
    }

    @Override
    public ExcelReportReqDTO selectExcelReport(Long organizationId, ExcelReportReqDTO excelReportReqDTO) {
        //select header
        List<InvoiceApplyHeaderDTO> invoiceApplyHeaderDTOList = invoiceApplyHeaderRepository.selectForReport(organizationId, excelReportReqDTO);
        //make header id into string ids
        Set<String> headerIds = invoiceApplyHeaderDTOList.stream().map(header -> header.getApplyHeaderId().toString())
                .collect(Collectors.toSet());
        //select line
        InvoiceApplyLine invoiceApplyLine = new InvoiceApplyLine();

        Condition condition = new Condition(InvoiceApplyLine.class);
        condition.createCriteria().andIn(InvoiceApplyLine.FIELD_APPLY_HEADER_ID, headerIds);
        List<InvoiceApplyLine> invoiceApplyLineList = invoiceApplyLineRepository.selectByCondition(condition);
        //set line to header
/*        for (InvoiceApplyHeaderDTO headerDTO : invoiceApplyHeaderDTOList) {
//            List<InvoiceApplyLineDTO> invoiceApplyLineDTOList = invoiceApplyLineList.stream()
//                    .filter(lineDTO -> headerDTO.getApplyHeaderId().equals(lineDTO.getApplyHeaderId()))
//                    .map(lineDTO -> {
//
//                        InvoiceApplyLineDTO invoiceApplyLineDTO = new InvoiceApplyLineDTO();
//                        BeanUtils.copyProperties(lineDTO, invoiceApplyLineDTO);
//                        return invoiceApplyLineDTO;
//                    })
//                    .collect(Collectors.toList());
//            headerDTO.setInvoiceApplyLineList(invoiceApplyLineDTOList);
//        }*/
        for (InvoiceApplyHeaderDTO headerDTO : invoiceApplyHeaderDTOList) {
            String invoiceNamesString = invoiceApplyLineList.stream()
                    .filter(line -> headerDTO.getApplyHeaderId().equals(line.getApplyHeaderId()))
                    .map(line -> line.getInvoiceName())
                    .collect(Collectors.joining(","));

            headerDTO.setLineInvoiceNames(invoiceNamesString);
        }

        //set tenant name
        try {
            UserVO userVO = objectMapper.readValue(iamRemoteService.selectSelf().getBody(), UserVO.class);
            excelReportReqDTO.setTenantName(userVO.getTenantName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        excelReportReqDTO.setInvoiceApplyHeaderDTOList(invoiceApplyHeaderDTOList);

        return excelReportReqDTO;
    }


}

